package com.storyai.storytelling_backend.service;

public interface IEmailService {
    void sendVerificationEmail(String toEmail, String username, String verificationCode);
    void sendWelcomeEmail(String toEmail, String username);
    void sendPasswordResetEmail(String toEmail, String username, String resetToken);
}
