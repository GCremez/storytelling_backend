package com.storyai.storytelling_backend.exception;

public class EmailNotVerifiedException extends AuthException{
  private final String email;

  public EmailNotVerifiedException(String email) {
    super("Email not verified. Please check your email for verification code");
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
