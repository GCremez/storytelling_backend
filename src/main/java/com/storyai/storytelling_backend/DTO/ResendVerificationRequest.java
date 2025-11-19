package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.*;

public class ResendVerificationRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  public ResendVerificationRequest() {}

  public ResendVerificationRequest(String email) {
    this.email = email;
  }

  // Getters and Setters
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}
