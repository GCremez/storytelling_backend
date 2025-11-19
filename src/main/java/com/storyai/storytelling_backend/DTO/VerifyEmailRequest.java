package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.*;;

public class VerifyEmailRequest {
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Verification code is required")
  @Pattern(regexp = "^[0-9]{6}$", message = "Verification code must be 6 digits")
  private String verificationCode;

  public VerifyEmailRequest() {}

  public VerifyEmailRequest(String email, String verificationCode) {
    this.email = email;
    this.verificationCode = verificationCode;
  }

  // Getters and Setters
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getVerificationCode() { return verificationCode; }
  public void setVerificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
  }
}
