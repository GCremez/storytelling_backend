package com.storyai.storytelling_backend.exception;

public class UserInactiveException extends AuthException {
  public UserInactiveException() {
    super("Your account has been deactivated. Please contact support.", "USER_INACTIVE");
  }
}
