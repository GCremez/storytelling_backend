package com.storyai.storytelling_backend.DTO;

import java.util.List;

public class GeneratedChoicesResponse {
  private List<GeneratedChoice> choices;
  private String aiProvider;
  private Boolean cached;

  public static class GeneratedChoice {
    private String choiceText;
    private String consequence; // What might happen
    private String emotionalTone;
    private Integer difficulty; // 1-5

    public GeneratedChoice() {}

    public GeneratedChoice(String choiceText, String consequence) {
      this.choiceText = choiceText;
      this.consequence = consequence;
    }

    // Getters and Setters
    public String getChoiceText() {
      return choiceText;
    }

    public void setChoiceText(String choiceText) {
      this.choiceText = choiceText;
    }

    public String getConsequence() {
      return consequence;
    }

    public void setConsequence(String consequence) {
      this.consequence = consequence;
    }

    public String getEmotionalTone() {
      return emotionalTone;
    }

    public void setEmotionalTone(String emotionalTone) {
      this.emotionalTone = emotionalTone;
    }

    public Integer getDifficulty() {
      return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
      this.difficulty = difficulty;
    }
  }

  public GeneratedChoicesResponse() {}

  // Getters and Setters
  public List<GeneratedChoice> getChoices() {
    return choices;
  }

  public void setChoices(List<GeneratedChoice> choices) {
    this.choices = choices;
  }

  public String getAiProvider() {
    return aiProvider;
  }

  public void setAiProvider(String aiProvider) {
    this.aiProvider = aiProvider;
  }

  public Boolean getCached() {
    return cached;
  }

  public void setCached(Boolean cached) {
    this.cached = cached;
  }
}
