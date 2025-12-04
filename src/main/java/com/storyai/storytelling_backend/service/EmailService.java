package com.storyai.storytelling_backend.service;

import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

  @Value("${sendgrid.apiKey}")
  private String sendGridApiKey;

  @Value("${sendgrid.from.email}")
  private String fromEmail;

  @Value("${sendgrid.from.name::Storytelling App}")
  private String fromName;

  @Value("${app.frontend.url:http://localhost:3000}")
  private String frontendUrl;
}

/**
 * Send verification code email
 */
public void sendVerificationEmail(String toEmail, String username, String verificationCode) {
  String subject = "Verify Your Email - StoryTelling App";
  String htmlContent = buildVerificationEmailHtml(username, verificationCode);

  sendEmail(toEmail, subject, htmlContent);;
}

/**
 * Send welcome email after verification
 */
public void sendWelcomeEmail(String toEmail, String username) {
  String subject = "Welcome to StoryTelling App";
  String htmlContent = buildWelcomeEmailHtml(username);
  sendEmail(toEmail, subject, htmlContent);
}

/**
 * Send password reset email
 */
public void sendPasswordResetEmail(String toEmail, String username, String resetToken) {
  String subject = "Reset Your Password - Storytelling App";
  String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
  String htmlContent = buildPasswordResetEmailHtml(username, resetUrl);

  sendEmail(toEmail, subject, htmlContent);
}

/**
 * Core method to send email via SendGrid
 */
private void sendEmail(String toEmail, String subject, String htmlContent) {
  try {
    Email from = new Email(fromEmail, fromName);
    Email to = new Email(toEmail);

  }
}
