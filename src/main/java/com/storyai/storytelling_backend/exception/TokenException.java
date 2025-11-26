package com.storyai.storytelling_backend.exception;

public class TokenException extends AuthException {
  public TokenException(String message, String errorCode) {
    super(message, errorCode);
  }

  public static TokenException expired() {
    return new TokenException("Token has expired", "TOKEN_EXPIRED");
  }

  public static TokenException invalid() {
    return new TokenException("Invalid token", "TOKEN_INVALID");
  }

  public static TokenException refreshInvalid() {
    return new TokenException("Invalid refresh token", "REFRESH_TOKEN_INVALID");
  }
}
