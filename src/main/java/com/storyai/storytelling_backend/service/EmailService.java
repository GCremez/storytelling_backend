package com.storyai.storytelling_backend.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.storyai.storytelling_backend.exception.EmailSendException;
import java.io.IOException;
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

  /** Send verification code email */
  public void sendVerificationEmail(String toEmail, String username, String verificationCode) {
    String subject = "Verify Your Email - StoryTelling App";
    String htmlContent = buildVerificationEmailHtml(username, verificationCode);

    sendEmail(toEmail, subject, htmlContent);
    ;
  }

  /** Send welcome email after verification */
  public void sendWelcomeEmail(String toEmail, String username) {
    String subject = "Welcome to StoryTelling App";
    String htmlContent = buildWelcomeEmailHtml(username);
    sendEmail(toEmail, subject, htmlContent);
  }

  /** Send password reset email */
  public void sendPasswordResetEmail(String toEmail, String username, String resetToken) {
    String subject = "Reset Your Password - Storytelling App";
    String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
    String htmlContent = buildPasswordResetEmailHtml(username, resetUrl);

    sendEmail(toEmail, subject, htmlContent);
  }

  /** Core method to send email via SendGrid */
  private void sendEmail(String toEmail, String subject, String htmlContent) {
    try {
      Email from = new Email(fromEmail, fromName);
      Email to = new Email(toEmail);
      Content content = new Content("text/html", htmlContent);
      Mail mail = new Mail(from, subject, to, content);

      SendGrid sg = new SendGrid(sendGridApiKey);
      Request request = new Request();

      request.setMethod(Method.POST);
      request.setEndpoint("Mail/send");
      request.setBody(mail.build());

      Response response = sg.api(request);

      if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
        logger.info("Email sent successfully to: {}", toEmail);
      } else {
        logger.error(
            "Failed to send email. Status: {}, Body: {}",
            response.getStatusCode(),
            response.getBody());
        throw new EmailSendException("SendGrid returned status: " + response.getStatusCode());
      }

    } catch (IOException e) {
      logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
      throw new EmailSendException("Failed to send email", e);
    }
  }

  /** Build verification email HTML */
  private String buildVerificationEmailHtml(String username, String code) {
    return """
    <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                    <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                        <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 28px;">📖 Storytelling App</h1>
                        </div>
                        <div style="padding: 40px 30px;">
                            <h2 style="color: #333333; margin-top: 0;">Verify Your Email</h2>
                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                Hello <strong>%s</strong>,
                            </p>
                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                                Thank you for registering! Your verification code is:
                            </p>
                            <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 8px; margin: 30px 0;">
                                <div style="font-size: 36px; font-weight: bold; color: #667eea; letter-spacing: 8px; font-family: 'Courier New', monospace;">
                                    %s
                                </div>
                            </div>
                            <p style="color: #999999; font-size: 14px; line-height: 1.6;">
                                This code will expire in <strong>15 minutes</strong>.
                            </p>
                            <p style="color: #999999; font-size: 14px; line-height: 1.6;">
                                If you didn't create this account, please ignore this email.
                            </p>
                        </div>
                        <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
                            <p style="color: #999999; font-size: 12px; margin: 0;">
                                © 2025 Storytelling App. All rights reserved.
                            </p>
                        </div>
                    </div>
                </body>
                </html>

  """
        .formatted(username, code);
  }

  /** Build welcome email HTML */
  private String buildWelcomeEmailHtml(String username) {
    String loginUrl = frontendUrl + "/login";

    return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0;">
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 28px;">🎉 Welcome!</h1>
                    </div>
                    <div style="padding: 40px 30px;">
                        <h2 style="color: #333333; margin-top: 0;">Hello %s!</h2>
                        <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                            Your email has been verified successfully! You're all set to start your storytelling adventure.
                        </p>
                        <div style="margin: 30px 0;">
                            <h3 style="color: #333333; font-size: 18px;">What you can do now:</h3>
                            <ul style="color: #666666; font-size: 16px; line-height: 1.8;">
                                <li>Create and play interactive stories</li>
                                <li>Make choices that shape your adventure</li>
                                <li>Track your progress across multiple stories</li>
                                <li>Experience AI-generated narratives</li>
                            </ul>
                        </div>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="display: inline-block; padding: 15px 40px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #ffffff; text-decoration: none; border-radius: 25px; font-weight: bold; font-size: 16px;">
                                Start Your Journey
                            </a>
                        </div>
                        <p style="color: #999999; font-size: 14px; line-height: 1.6; text-align: center;">
                            Happy storytelling! 📚
                        </p>
                    </div>
                    <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
                        <p style="color: #999999; font-size: 12px; margin: 0;">
                            © 2025 Storytelling App. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """
        .formatted(username, loginUrl);
  }

  /** Build password reset email HTML */
  private String buildPasswordResetEmailHtml(String username, String resetUrl) {
    return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;">
                        <h1 style="color: #ffffff; margin: 0; font-size: 28px;">🔒 Reset Password</h1>
                    </div>
                    <div style="padding: 40px 30px;">
                        <h2 style="color: #333333; margin-top: 0;">Hello %s!</h2>
                        <p style="color: #666666; font-size: 16px; line-height: 1.6;">
                            We received a request to reset your password. Click the button below to create a new password:
                        </p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="display: inline-block; padding: 15px 40px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #ffffff; text-decoration: none; border-radius: 25px; font-weight: bold; font-size: 16px;">
                                Reset Password
                            </a>
                        </div>
                        <p style="color: #999999; font-size: 14px; line-height: 1.6;">
                            This link will expire in <strong>1 hour</strong>.
                        </p>
                        <p style="color: #999999; font-size: 14px; line-height: 1.6;">
                            If you didn't request this, please ignore this email and your password will remain unchanged.
                        </p>
                    </div>
                    <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
                        <p style="color: #999999; font-size: 12px; margin: 0;">
                            © 2025 Storytelling App. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """
        .formatted(username, resetUrl);
  }
}
