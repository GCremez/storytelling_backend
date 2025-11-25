package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

  @NotBlank(message = "Username or email is required")
  private String usernameOrEmail;

  @NotBlank(message = "Password is required")
  private String Password;

  public LoginRequest() {}

  public LoginRequest(String usernameOrEmail, String password){
    this.usernameOrEmail = usernameOrEmail;
    this.Password = password;
  }

  // GETTERS AND SETTERS
  public String getUsernameOrEmail() { return usernameOrEmail; }
  public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }

  public String getPassword() { return Password; }
  public void setPassword(String password) { this.Password = password; }
}
