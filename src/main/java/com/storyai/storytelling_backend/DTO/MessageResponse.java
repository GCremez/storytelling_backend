package com.storyai.storytelling_backend.DTO;

public class MessageResponse { private String message;

  public MessageResponse() {}

  public MessageResponse(String message) {
    this.message = message;
  }

  // Getters and Setters
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }}
