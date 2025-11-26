package com.storyai.storytelling_backend.exception;

public class InvalidCredentialsException extends AuthException {
  public InvalidCredentialsException() {
    super("Invalid username or password", "INVALID_CREDENTIALS");
  }
}
