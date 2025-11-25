package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {
  @NotBlank(message = "Refresh token is required")
  private String refreshToken;

  public RefreshTokenRequest() {}

  public RefreshTokenRequest(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  // GETTERS AND SETTERS
  public String getRefreshToken() { return refreshToken; }
  public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
