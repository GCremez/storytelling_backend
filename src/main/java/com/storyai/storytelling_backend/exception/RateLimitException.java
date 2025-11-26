package com.storyai.storytelling_backend.exception;

public class RateLimitException extends AuthException {
  private final int retryAfterSeconds;

  public RateLimitException(String message, int retryAfterSeconds) {
    super(message, "RATE_LIMIT_EXCEEDED");
    this.retryAfterSeconds = retryAfterSeconds;
  }

  public int getRetryAfterSeconds() {
    return retryAfterSeconds;
  }
}
