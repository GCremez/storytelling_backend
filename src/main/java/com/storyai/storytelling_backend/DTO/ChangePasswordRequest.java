package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.*;

public class ChangePasswordRequest {
  @NotBlank(message = "Current password is required")
  private String currentPassword;

  @NotBlank(message = "New password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
    message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
  )
  private String newPassword;

  public ChangePasswordRequest() {}

  public ChangePasswordRequest(String currentPassword, String newPassword) {
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
  }

  // Getters and Setters
  public String getCurrentPassword() { return currentPassword; }
  public void setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
  }

  public String getNewPassword() { return newPassword; }
  public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
