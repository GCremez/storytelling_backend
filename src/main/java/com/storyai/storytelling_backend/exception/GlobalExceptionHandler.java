package com.storyai.storytelling_backend.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  //ErrorResponse Class
  public static class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode;
    private List<ValidationError> errors;
    private Map<String, Object> additionalInfo;

    public ErrorResponse(){
      this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, String path) {
      this.status = status;
      this.error = error;
      this.message = message;
      this.path = path;
    }

    //GETTER AND SETTERS
    public LocalDateTime getTimestamp() {
      return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
    }
    public int getStatus() {
      return status;
    }
    public void setStatus(int status) {
      this.status = status;
    }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public List<ValidationError> getErrors() { return errors; }
    public void setErrors(List<ValidationError> errors) { this.errors = errors; }

    public Map<String, Object> getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
      this.additionalInfo = additionalInfo;
    }
  }

  public static class ValidationError {
    private String field;
    private String message;
    private String code;

    public ValidationError(String field, String message, String code) {
      this.field = field;
      this.message = message;
      this.code = code;
    }

    //GETTER AND SETTERS
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
  }

  // HELPER
  private String getPath(WebRequest request){
    return request.getDescription(false).replace("uri=", "");
  }

  // Exception Handler

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
    MethodArgumentNotValidException ex, WebRequest request) {
    List<ValidationError> validationErrors = ex.getBindingResult()
      .getAllErrors()
      .stream()
      .map(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      return new ValidationError(fieldName, errorMessage, "VALIDATION_ERROR");
  })
      .collect(Collectors.toList());

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.BAD_REQUEST.value(),
      "BAD_REQUEST",
      "Validation Failed",
      getPath(request)
    );
    errorResponse.setErrorCode("VALIDATION_ERROR");
    errorResponse.setErrors(validationErrors);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
    UserAlreadyExistsException ex, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse (
      HttpStatus.CONFLICT.value(),
      "CONFLICT",
      ex.getMessage(),
      getPath(request)
    );
    errorResponse.setErrorCode(ex.getErrorCode());

    Map<String, Object> additionalInfo = new HashMap<>();
    additionalInfo.put("field", ex.getField());
    additionalInfo.put("value", ex.getValue());
    errorResponse.setAdditionalInfo(additionalInfo);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(EmailNotVerifiedException.class)
  public ResponseEntity<ErrorResponse> handleEmailNotVerified(
    EmailNotVerifiedException ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.FORBIDDEN.value(),
      "FORBIDDEN",
      ex.getMessage(),
      getPath(request)
    );
    errorResponse.setErrorCode(ex.getErrorCode());

    Map<String, Object> additionalInfo = new HashMap<>();
    additionalInfo.put("email", ex.getEmail());
    errorResponse.setAdditionalInfo(additionalInfo);

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> HandleInvalidCredentials(
    InvalidCredentialsException ex, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.UNAUTHORIZED.value(),
      "UNAUTHORIZED",
      ex.getMessage(),
      getPath(request)
    );
    errorResponse.setErrorCode(ex.getErrorCode());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(VerificationCodeException.class)
  public ResponseEntity<ErrorResponse> handleVerificationCode(
    VerificationCodeException ex, WebRequest request) {
    HttpStatus status = ex.getErrorCode().equals("VERIFICCATION_CODE_EXPIRED")
    ? HttpStatus.GONE
    : HttpStatus.BAD_REQUEST;

    ErrorResponse errorResponse = new ErrorResponse(
      status.value(),
      status.getReasonPhrase().toUpperCase().replace(" ", "_"),
      ex.getMessage(),
      getPath(request)
    );
    errorResponse.setErrorCode(ex.getErrorCode());
    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(TokenException.class)
  public ResponseEntity<ErrorResponse> handleTokenException(
    TokenException ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.UNAUTHORIZED.value(),
      "UNAUTHORIZED",
      ex.getMessage(),
      request.getDescription(false).replace("uri=", "")
    );
    errorResponse.setErrorCode(ex.getErrorCode());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(RateLimitException.class)
  public ResponseEntity<ErrorResponse> handleRateLimitException(
    RateLimitException ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.TOO_MANY_REQUESTS.value(),
      "TOO_MANY_REQUESTS",
      ex.getMessage(),
      request.getDescription(false).replace("uri=", "")
    );
    errorResponse.setErrorCode(ex.getErrorCode());

    Map<String, Object> additionalInfo = new HashMap<>();
    additionalInfo.put("retryAfterSeconds", ex.getRetryAfterSeconds());
    errorResponse.setAdditionalInfo(additionalInfo);

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
  }

  @ExceptionHandler(EmailSendException.class)
  public ResponseEntity<ErrorResponse> handleEmailSendException(
    EmailSendException ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      "INTERNAL_SERVER_ERROR",
      ex.getMessage(),
      request.getDescription(false).replace("uri=", "")
    );
    errorResponse.setErrorCode(ex.getErrorCode());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  @ExceptionHandler(UserInactiveException.class)
  public ResponseEntity<ErrorResponse> handleUserInactive(
    UserInactiveException ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.FORBIDDEN.value(),
      "FORBIDDEN",
      ex.getMessage(),
      request.getDescription(false).replace("uri=", "")
    );
    errorResponse.setErrorCode(ex.getErrorCode());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(
    NotFoundException ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.NOT_FOUND.value(),
      "NOT_FOUND",
      ex.getMessage(),
      request.getDescription(false).replace("uri=", "")
    );
    errorResponse.setErrorCode("NOT_FOUND");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
    Exception ex, WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.UNAUTHORIZED.value(),
      "UNAUTHORIZED",
      "Invalid credentials",
      request.getDescription(false).replace("uri=", "")
    );
    errorResponse.setErrorCode("INVALID_CREDENTIALS");

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
    Exception ex, WebRequest request) {

    log.error("Unexpected error occurred at: {}", getPath(request));
    ErrorResponse errorResponse = new ErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      "INTERNAL_SERVER_ERROR",
      "An unexpected error occurred",
      getPath(request)
    );
    errorResponse.setErrorCode("INTERNAL_ERROR");


    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

}
