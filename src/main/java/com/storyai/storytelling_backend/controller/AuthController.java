package com.storyai.storytelling_backend.controller;

import com.storyai.storytelling_backend.DTO.*;
import com.storyai.storytelling_backend.security.CustomUserDetails;
import com.storyai.storytelling_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Register new user
   */

  @PostMapping("/register")
  @Operation(summary = "Register new user",
    description = "Create a new user account and send verification email")
    @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User registered successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
    RegisterResponse response = authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Resend verification code
   */
  @PostMapping("/verify-email")
  @Operation(summary = "Verify email address",
  description = "Verify user email with 6-digit code sent via email")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Email verified successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid verification code"),
    @ApiResponse(responseCode = "404", description = "User not found"),
    @ApiResponse(responseCode = "410", description = "Verification code expired")
  })
  public ResponseEntity<MessageResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
    MessageResponse response = authService.verifyEmail(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Resend verification code
   */
  @PostMapping("/resend-verification")
  @Operation(summary = "Resend verification code",
  description = "Send a new verification code to user's email")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Verification code resent"),
    @ApiResponse(responseCode = "400", description = "Email already verified"),
    @ApiResponse(responseCode = "404", description = "User not found"),
    @ApiResponse(responseCode = "429", description = "Too many requests")
  })
  public ResponseEntity<MessageResponse> resendVerificationCode(
    @Valid @RequestBody ResendVerificationRequest request) {
    MessageResponse response = authService.resendVerificationCode(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Login User
   */
  @PostMapping("/login")
  @Operation(summary = "User login",
  description = "Authenticate user and return JWT tokens")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Login successfully"),
    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
    @ApiResponse(responseCode = "403", description = "Email not verified or account inactive")
  })
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Request access token
   */
  @PostMapping("/refresh")
  @Operation(summary = "Refresh access token",
  description = "Get new access token using refresh token")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
  })
  public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    LoginResponse response = authService.refreshToken(request);
    return ResponseEntity.ok(response);
  }

  /**
    * Logout user
    */
  @PostMapping("/logout")
  @Operation(summary = "User logout",
    description = "Revoke refresh token and logout user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
  })
  public ResponseEntity<MessageResponse> logout(@Valid @RequestBody RefreshTokenRequest request) {
    MessageResponse response = authService.logout(request.getRefreshToken());
    return ResponseEntity.ok(response);
  }

  /**
   * Forgot password
   */
  @PostMapping("/forgot-password")
  @Operation(summary = "Forgot password",
    description = "Send password reset link to user's email")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Password reset email sent"),
    @ApiResponse(responseCode = "404", description = "User not found")
  })
  public ResponseEntity<MessageResponse> forgotPassword(
    @Valid @RequestBody ForgotPasswordRequest request) {
    MessageResponse response = authService.forgotPassword(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Reset password with token
   */
  @PostMapping("/reset-password")
  @Operation(summary = "Reset password",
    description = "Reset password using token from email")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Password reset successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid or expired token")
  })
  public ResponseEntity<MessageResponse> resetPassword(
    @Valid @RequestBody ResetPasswordRequest request) {
    MessageResponse response = authService.resetPassword(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Change password (authenticated users)
   */
  @PostMapping("/change-password")
  @Operation(summary = "Change password",
    description = "Change password for authenticated user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
    @ApiResponse(responseCode = "401", description = "Invalid current password")
  })
  public ResponseEntity<MessageResponse> changePassword(
    @Valid @RequestBody ChangePasswordRequest request,
    @AuthenticationPrincipal CustomUserDetails userDetails) {
    MessageResponse response = authService.changePassword(request, userDetails.getUserId());
    return ResponseEntity.ok(response);
  }

  /**
   * Get current authenticated user info
   */
  @GetMapping("/me")
  @Operation(summary = "Get current user",
    description = "Get information about currently authenticated user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User info retrieved"),
    @ApiResponse(responseCode = "401", description = "Not authenticated")
  })
  public ResponseEntity<LoginResponse.UserInfo> getCurrentUser() {
    LoginResponse.UserInfo userInfo = authService.getCurrentUser();
    return ResponseEntity.ok(userInfo);
  }

  /**
   * Delete user account
   */
  @DeleteMapping("/account")
  @Operation(summary = "Delete account",
    description = "Delete currently authenticated user's account")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
    @ApiResponse(responseCode = "401", description = "Not authenticated")
  })
  public ResponseEntity<MessageResponse> deleteAccount(
    @AuthenticationPrincipal CustomUserDetails userDetails) {
    MessageResponse response = authService.deleteAccount(userDetails.getUserId());
    return ResponseEntity.ok(response);
  }
}
