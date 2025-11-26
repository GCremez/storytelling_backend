package com.storyai.storytelling_backend.exception;

public class VerificationCodeException extends AuthException{
  public VerificationCodeException(String message, String errorCode) {
    super(message, errorCode);
  }

  public static VerificationCodeException invalid() {
    return new VerificationCodeException(
      "Invalid verification code",
      "VERIFICATION_CODE_INVALID"
    );
  }

  public static VerificationCodeException expired() {
    return new VerificationCodeException(
      "Verification code has expired. Please request a new one",
      "VERIFICATION_CODE_EXPIRED"
    );
  }

  public static VerificationCodeException tooManyAttempts() {
    return new VerificationCodeException(
      "Too many verification attempts. Please request a new code.",
      "VERIFICATION_ATTEMPTS_EXCEEDED"
    );
  }
}
