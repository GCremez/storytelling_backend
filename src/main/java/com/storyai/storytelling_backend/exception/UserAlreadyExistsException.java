package com.storyai.storytelling_backend.exception;

public class UserAlreadyExistsException extends AuthException {
  private final String field;
  private final String value;

  public UserAlreadyExistsException(String field, String value) {
    super(field + " already exists: " + value,
      field.equals("username") ? "USERNAME_TAKEN" : "EMAIL_TAKEN");
    this.field = field;
    this.value = value;
  }

  public String getField() {
    return field;
  }

  public String getValue() {
    return value;
  }
}
