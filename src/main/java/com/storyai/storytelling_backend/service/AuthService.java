package com.storyai.storytelling_backend.service;

import com.storyai.storytelling_backend.DTO.*;
import com.storyai.storytelling_backend.entity.EmailVerification;
import com.storyai.storytelling_backend.entity.PasswordResetToken;
import com.storyai.storytelling_backend.entity.RefreshToken;
import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.exception.*;
import com.storyai.storytelling_backend.repository.*;
import com.storyai.storytelling_backend.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  private static final int VERIFICATION_CODE_LENGTH = 6;
  private static final int MAX_RESEND_PER_HOUR = 3;

  private final UserRepository userRepository;
  private final EmailVerificationRepository emailVerificationRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final EmailService emailService;
  private final CustomUserDetailsService userDetailsService;
  private final AuthenticationManager authenticationManager;

  public AuthService(
    UserRepository userRepository,
    EmailVerificationRepository emailVerificationRepository,
    RefreshTokenRepository refreshTokenRepository,
    PasswordResetTokenRepository passwordResetTokenRepository,
    PasswordEncoder passwordEncoder,
    JwtService jwtService,
    EmailService emailService,
    CustomUserDetailsService userDetailsService,
    AuthenticationManager authenticationManager
  ) {
    this.userRepository = userRepository;
    this.emailVerificationRepository = emailVerificationRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.emailService = emailService;
    this.userDetailsService = userDetailsService;
    this.authenticationManager = authenticationManager;
  }

  /**
   * Register a new user
   * @param request
   * @return
   */
  public RegisterResponse register(RegisterRequest request) {
    logger.info("Registering new user: {}", request.getUsername());

    // Check if username exists
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new UserAlreadyExistsException("username", request.getUsername());
    }

    // Check if email exists
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistsException("email", request.getEmail());
    }

    // Hash Password
    String hashedPassword = passwordEncoder.encode(request.getPassword());

    // Create User
    User user = new User(request.getUsername(), request.getEmail(), hashedPassword);
    user.setIsVerified(false);
    user.setActive(true);
    user.setVerificationSentAt(LocalDateTime.now());

    user = userRepository.save(user);
    logger.info("User created with ID: {}", user.getId());

    // Generate and send verification code
    String verificationCode = generateVerificationCode();
    saveVerificationCode(user, verificationCode);

    try {
      emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), verificationCode);
      logger.info("Verification code sent to: {}", user.getEmail());
    } catch (EmailNotVerifiedException e) {
      logger.error("Failed to send verification code", e);
      // Don't fail registration if email sending fails
    }

    return new RegisterResponse(
        "Registration successful. Please check your email for verification code.",
        user.getId(),
        user.getUsername(),
        true);
  }

  /**
   * Verify email with code
   * @param request
   * @return
   */
  public MessageResponse verifyEmail(VerifyEmailRequest request) {
    logger.info("Verifying email: {}", request.getEmail());

    // Find User
    User user = userRepository.findByEmail(request.getEmail())
      .orElseThrow(() -> new EmailNotVerifiedException("Email not found: " + request.getEmail()));

    // Check if email is already verified
    if (user.getIsVerified()) {
      return new MessageResponse("Email already verified");
    }

    // Find verification code
    EmailVerification verification = emailVerificationRepository
      .findByUserAndVerifiedAtIsNull(user)
      .orElseThrow(VerificationCodeException::invalid);

    // Increments attempts
    verification.incrementAttempts();
    emailVerificationRepository.save(verification);

    // Validate Code
    if (!verification.isValid(request.getVerificationCode())) {
      if (verification.isExpired()) {
        throw VerificationCodeException.expired();
      } else if(verification.getAttempts() >= 5){
        throw  VerificationCodeException.tooManyAttempts();
      } else {
        throw VerificationCodeException.invalid();
      }
    }

    // Mark verification as verified
    verification.setVerifiedAt(LocalDateTime.now());
    emailVerificationRepository.save(verification);

    // Update user
    user.setIsVerified(true);
    userRepository.save(user);

    logger.info("Email verification successfully for user: {}", user.getUsername());

    // Send welcome email
    try {
      emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
    } catch (EmailSendException e) {
      logger.error("Failed to send welcome email", e);
    }

    return new MessageResponse("Email verified successfully");
  }

  /**
   * Resend verification code
   * @param request
   * @return
   */
  public MessageResponse resendVerificationCode(ResendVerificationRequest request){
    logger.info("Resending Verification code to: {}", request.getEmail());

    // Find User
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException("User not found"));

    // Check if already verified
    if (user.getIsVerified()) {
      throw new IllegalStateException("Email is already verified");
    }

    // Check rate limit
    LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
    long recentAttempts  = emailVerificationRepository
      .countByUserAndCreatedAtAfter(user, oneHourAgo);

    if (recentAttempts >= MAX_RESEND_PER_HOUR) {
      int retryAfterMinutes = 3600; // 1 HOUR
      throw new RateLimitException(
        "Too many verification attempts. Please try again later.",
        retryAfterMinutes
      );
    }


    // Generate New Code
    String newVerificationCode  = generateVerificationCode();

    // Delete Old Code
    emailVerificationRepository.findByUserAndVerifiedAtIsNull(user)
      .ifPresent(emailVerificationRepository::delete);

    // Save New Code
    saveVerificationCode(user, newVerificationCode);

    user.setVerificationSentAt(LocalDateTime.now());
    userRepository.save(user);

    try {
      emailService.sendVerificationEmail(
        user.getEmail(),
        user.getUsername(),
        newVerificationCode);

      logger.info("Verification code sent to: {}", user.getEmail());
    } catch (EmailSendException e) {
      logger.error("Failed to send verification code", e);
      // Don't fail registration if email sending fails
    }

    return new MessageResponse("Verification code resent successfully");
  }

  /**
   * Generate random 6-digit verification code
   */

  private String generateVerificationCode() {
    SecureRandom random = new SecureRandom();
    int code = 100000 + random.nextInt(900000);
    return String.valueOf(code);
  }

  /**
   * Save verification code to database
   */

  private void saveVerificationCode(User user, String code) {
    EmailVerification verification = new EmailVerification(user, code);
    emailVerificationRepository.save(verification);
  }

 /**
  * Login user
  */
  public LoginResponse login(LoginRequest request) {
  logger.info("Login attempt for: {}", request.getUsernameOrEmail());

  // Find user
  User user = userRepository.findByUsernameOrEmail(
    request.getUsernameOrEmail(),
    request.getUsernameOrEmail()
  ).orElseThrow(() -> new InvalidCredentialsException());

  // Check if user is active
  if (!user.isActive()) {
    throw new UserInactiveException();
  }

  // Check if email is verified
  if (!user.getIsVerified()) {
    throw new EmailNotVerifiedException(user.getEmail());
  }

  // Authenticate
  try {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        user.getUsername(),
        request.getPassword()
      )
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
  } catch (BadCredentialsException e) {
    logger.warn("Invalid credentials for user: {}", request.getUsernameOrEmail());
    throw new InvalidCredentialsException();
  }

  // Update last login
  user.setLastLogin(LocalDateTime.now());
  userRepository.save(user);

  // Generate tokens
  String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getId());
  RefreshToken refreshToken = createRefreshToken(user);

  logger.info("User logged in successfully: {}", user.getUsername());

  // Build response
  LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
    user.getId(),
    user.getUsername(),
    user.getEmail(),
    user.getIsVerified()
  );

  return new LoginResponse(
    accessToken,
    refreshToken.getToken(),
    jwtService.getExpirationInSeconds(),
    userInfo
  );
}

/**
 * Refresh access token
 */
public LoginResponse refreshToken(RefreshTokenRequest request) {
  logger.info("Refreshing token");

  // Find refresh token
  RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
    .orElseThrow(() -> TokenException.refreshInvalid());

  // Validate token
  if (!refreshToken.isValid()) {
    refreshTokenRepository.delete(refreshToken);
    throw TokenException.refreshInvalid();
  }

  User user = refreshToken.getUser();

  // Check if user is still active
  if (!user.isActive()) {
    throw new UserInactiveException();
  }

  // Generate new access token
  String newAccessToken = jwtService.generateAccessToken(user.getUsername(), user.getId());

  // Optionally rotate refresh token (recommended for security)
  RefreshToken newRefreshToken = createRefreshToken(user);
  refreshTokenRepository.delete(refreshToken); // Delete old refresh token

  logger.info("Token refreshed for user: {}", user.getUsername());

  // Build response
  LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
    user.getId(),
    user.getUsername(),
    user.getEmail(),
    user.getIsVerified()
  );

  return new LoginResponse(
    newAccessToken,
    newRefreshToken.getToken(),
    jwtService.getExpirationInSeconds(),
    userInfo
  );
}

/**
 * Logout user (revoke refresh token)
 */
public MessageResponse logout(String refreshToken) {
  logger.info("Logout request");

  refreshTokenRepository.findByToken(refreshToken)
    .ifPresent(token -> {
      token.setRevoked(true);
      refreshTokenRepository.save(token);
      logger.info("Refresh token revoked for user: {}", token.getUser().getUsername());
    });

  SecurityContextHolder.clearContext();

  return new MessageResponse("Logged out successfully");
}

/**
 * Create refresh token for user
 */
private RefreshToken createRefreshToken(User user) {
  // Delete old refresh tokens for this user
  refreshTokenRepository.findByUserAndRevokedFalse(user)
    .ifPresent(refreshTokenRepository::delete);

  // Create new refresh token
  RefreshToken refreshToken = new RefreshToken(user);
  return refreshTokenRepository.save(refreshToken);
}

/**
 * Forgot password - send reset email
 */
public MessageResponse forgotPassword(ForgotPasswordRequest request) {
  logger.info("Password reset requested for: {}", request.getEmail());

  // Find user
  User user = userRepository.findByEmail(request.getEmail())
    .orElseThrow(() -> new NotFoundException("User not found"));

  // Delete old reset tokens
  passwordResetTokenRepository.deleteByUser(user);

  // Create new reset token
  PasswordResetToken resetToken = new PasswordResetToken(user);
  passwordResetTokenRepository.save(resetToken);

  // Send reset email
  try {
    emailService.sendPasswordResetEmail(
      user.getEmail(),
      user.getUsername(),
      resetToken.getToken()
    );
    logger.info("Password reset email sent to: {}", user.getEmail());
  } catch (EmailSendException e) {
    logger.error("Failed to send password reset email", e);
    throw e;
  }

  return new MessageResponse("Password reset email sent. Please check your inbox.");
}

/**
 * Reset password with token
 */
public MessageResponse resetPassword(ResetPasswordRequest request) {
  logger.info("Resetting password with token");

  // Find reset token
  PasswordResetToken resetToken = passwordResetTokenRepository
    .findByToken(request.getToken())
    .orElseThrow(() -> new NotFoundException("Invalid reset token"));

  // Validate token
  if (!resetToken.isValid()) {
    passwordResetTokenRepository.delete(resetToken);
    throw new IllegalStateException("Reset token is invalid or expired");
  }

  User user = resetToken.getUser();

  // Update password
  String hashedPassword = passwordEncoder.encode(request.getNewPassword());
  user.setPasswordHash(hashedPassword);
  userRepository.save(user);

  // Mark token as used
  resetToken.markAsUsed();
  passwordResetTokenRepository.save(resetToken);

  // Revoke all refresh tokens (force re-login)
  refreshTokenRepository.deleteByUser(user);

  logger.info("Password reset successfully for user: {}", user.getUsername());

  return new MessageResponse("Password reset successfully. Please login with your new password.");
}

/**
 * Change password (authenticated user)
 */
public MessageResponse changePassword(ChangePasswordRequest request, Long userId) {
  logger.info("Changing password for user ID: {}", userId);

  // Find user
  User user = userRepository.findById(userId)
    .orElseThrow(() -> new NotFoundException("User not found"));

  // Verify current password
  if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
    throw new InvalidCredentialsException();
  }

  // Update password
  String hashedPassword = passwordEncoder.encode(request.getNewPassword());
  user.setPasswordHash(hashedPassword);
  userRepository.save(user);

  // Revoke all refresh tokens (force re-login on all devices)
  refreshTokenRepository.deleteByUser(user);

  logger.info("Password changed successfully for user: {}", user.getUsername());

  return new MessageResponse("Password changed successfully. Please login again.");
}

/**
 * Get current authenticated user
 */
public LoginResponse.UserInfo getCurrentUser() {
  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

  if (authentication == null || !authentication.isAuthenticated()) {
    throw new IllegalStateException("No authenticated user found");
  }

  CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
  User user = userDetails.getUser();

  return new LoginResponse.UserInfo(
    user.getId(),
    user.getUsername(),
    user.getEmail(),
    user.getIsVerified()
  );
}

/**
 * Delete user account
 */
public MessageResponse deleteAccount(Long userId) {
  logger.info("Deleting account for user ID: {}", userId);

  User user = userRepository.findById(userId)
    .orElseThrow(() -> new NotFoundException("User not found"));

  // Delete all related data
  emailVerificationRepository.findByUserAndVerifiedAtIsNull(user)
    .ifPresent(emailVerificationRepository::delete);
  refreshTokenRepository.deleteByUser(user);
  passwordResetTokenRepository.deleteByUser(user);

  // Soft delete (deactivate) instead of hard delete
  user.setIsActive(false);
  userRepository.save(user);

  // Or hard delete:
  // userRepository.delete(user);

  logger.info("Account deleted for user: {}", user.getUsername());

  return new MessageResponse("Account deleted successfully");
}
}
