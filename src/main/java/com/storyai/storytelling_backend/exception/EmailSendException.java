package com.storyai.storytelling_backend.exception;

public class EmailSendException extends AuthException{
  public EmailSendException(String message) {
    super("Failed to send email: " + message, "EMAIL_SEND_FAILED");
  }

  public EmailSendException(String message, Throwable cause) {
    super("Failed to send email: " + message, "EMAIL_SEND_FAILED");
    initCause(cause);
  }
}
