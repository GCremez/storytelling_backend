package com.storyai.storytelling_backend.DTO;

public class RegisterResponse {
  private String message;
  private Long userId;
  private String email;
  private Boolean verificationSent;

  public RegisterResponse() {}

  public RegisterResponse(String message, Long userId, String email, Boolean verificationSent) {
    this.message = message;
    this.userId = userId;
    this.email = email;
    this.verificationSent = verificationSent;
  }

  // GETTERS AND SETTERS
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public Boolean getVerificationSent() { return verificationSent; }
  public void setVerificationSent(Boolean verificationSent) { this.verificationSent = verificationSent; }
}
