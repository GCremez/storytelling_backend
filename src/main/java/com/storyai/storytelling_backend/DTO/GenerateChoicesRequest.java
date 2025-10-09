package com.storyai.storytelling_backend.DTO;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GenerateChoicesRequest {
  @NotNull private Long chapterId;

  @NotNull private Long sessionId;

  @NotBlank private String currentSituation; // Current chapter content

  private Integer numberOfChoices; // Default: 3
  private String difficultyLevel;
  private Map<String, Object> context; // Game state

  public GenerateChoicesRequest() {}

  // Getters and Setters
  public Long getChapterId() {
    return chapterId;
  }

  public void setChapterId(Long chapterId) {
    this.chapterId = chapterId;
  }

  public Long getSessionId() {
    return sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public String getCurrentSituation() {
    return currentSituation;
  }

  public void setCurrentSituation(String currentSituation) {
    this.currentSituation = currentSituation;
  }

  public Integer getNumberOfChoices() {
    return numberOfChoices;
  }

  public void setNumberOfChoices(Integer numberOfChoices) {
    this.numberOfChoices = numberOfChoices;
  }

  public String getDifficultyLevel() {
    return difficultyLevel;
  }

  public void setDifficultyLevel(String difficultyLevel) {
    this.difficultyLevel = difficultyLevel;
  }

  public Map<String, Object> getContext() {
    return context;
  }

  public void setContext(Map<String, Object> context) {
    this.context = context;
  }
}
