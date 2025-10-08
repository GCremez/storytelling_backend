package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.NotNull;

public class MakeChoiceRequest {

    @NotNull(message = "Choice ID is required")
    private Long choiceId;

  private String additionalContext; // Optional: user's custom input

    public MakeChoiceRequest() {
    }

    public MakeChoiceRequest(Long choiceId) {

      this.choiceId = choiceId;
    }

    public Long getChoiceId() {
        return choiceId;
    }
    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
    }

  public String getAdditionalContext() { return additionalContext; }
  public void setAdditionalContext(String additionalContext) {
    this.additionalContext = additionalContext;
  }
}
