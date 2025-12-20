package com.storyai.storytelling_backend.service;

import com.storyai.storytelling_backend.DTO.*;
import com.storyai.storytelling_backend.entity.EmailVerification;
import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.exception.*;
import com.storyai.storytelling_backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
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


}
