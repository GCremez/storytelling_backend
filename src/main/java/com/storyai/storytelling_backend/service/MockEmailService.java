package com.storyai.storytelling_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "email.enabled", havingValue = "false")
public class MockEmailService implements IEmailService {
  private static final Logger logger = LoggerFactory.getLogger(MockEmailService.class);

  @Value("${app.frontend.url:http://localhost:3000}")
  private String frontendUrl;

  /** Send verification code email (mock) */
  public void sendVerificationEmail(String toEmail, String username, String verificationCode) {
    logger.info("MOCK EMAIL - Verification code for {}: {}", toEmail, verificationCode);
    logger.info("MOCK EMAIL - Would send verification email to: {} for user: {}", toEmail, username);
    logger.info("MOCK EMAIL - Use this verification code: {}", verificationCode);
  }

  /** Send welcome email after verification (mock) */
  public void sendWelcomeEmail(String toEmail, String username) {
    logger.info("MOCK EMAIL - Would send welcome email to: {} for user: {}", toEmail, username);
  }

  /** Send password reset email (mock) */
  public void sendPasswordResetEmail(String toEmail, String username, String resetToken) {
    String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
    logger.info("MOCK EMAIL - Would send password reset email to: {} for user: {} with URL: {}", toEmail, username, resetUrl);
  }
}
