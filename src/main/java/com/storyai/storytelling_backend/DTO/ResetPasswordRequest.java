package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.*;


public class ResetPasswordRequest {
  @NotBlank(message = "Token is required")
  private String token;

  @NotBlank(message = "New password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
    message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
  )
  private String newPassword;

  public ResetPasswordRequest() {}

  public ResetPasswordRequest(String token, String newPassword) {
    this.token = token;
    this.newPassword = newPassword;
  }

  // Getters and Setters
  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }

  public String getNewPassword() { return newPassword; }
  public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
