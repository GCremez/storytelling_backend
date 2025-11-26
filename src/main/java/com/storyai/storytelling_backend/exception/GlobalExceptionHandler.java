package com.storyai.storytelling_backend.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
      request.getDescription(false).replace("uri=", "")
    );
    errorResponse.setErrorCode("VALIDATION_ERROR");
    errorResponse.setErrors(validationErrors);
    return ResponseEntity.badRequest().body(errorResponse);
  }
}
