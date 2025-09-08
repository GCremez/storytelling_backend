package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.NotNull;

public class StartSessionRequest {

  @NotNull(message = "Story ID is required") private Long storyId;

  public StartSessionRequest() {}

  public StartSessionRequest(Long storyId) {
    this.storyId = storyId;
  }

  public Long getStoryId() {
    return storyId;
  }

  public void setStoryId(Long storyId) {
    this.storyId = storyId;
  }
}
