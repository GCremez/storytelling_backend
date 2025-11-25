package com.storyai.storytelling_backend.DTO;

import com.storyai.storytelling_backend.entity.User;

public class LoginRespone {
  private String accessToken;
  private String refreshToken;
  private String tokenType = "Bearer";
  private long expiresIn;
  private User user;

  public static class UserInfo {
    private Long id;
    private String username;
    private String email;
    private Boolean isVerified;

    public UserInfo(){}

    public UserInfo(Long id, String username, String email, boolean isVerified) {
      this.id = id;
      this.username = username;
      this.email = email;
      this.isVerified = isVerified;
    }

    // GETTERS AND SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
  }

  public LoginRespone() {}

  public LoginRespone(String accessToken, String refreshToken, long expiresIn, User user) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresIn = expiresIn;
    this.user = user;
  }

  // GETTERS AND SETTERS
  public String getAccessToken() { return accessToken; }
  public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

  public String getRefreshToken() { return refreshToken; }
  public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

  public String getTokenType() { return tokenType; }
  public void setTokenType(String tokenType) { this.tokenType = tokenType; }

  public long getExpiresIn() { return expiresIn; }
  public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
}

