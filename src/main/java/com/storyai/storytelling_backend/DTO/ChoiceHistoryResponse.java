package com.storyai.storytelling_backend.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class ChoiceHistoryResponse {
  private Long sessionId;
  private List<ChoiceRecord> choices;
  private Integer totalChoicesMade;

  public static class ChoiceRecord {
    private Long choiceId;
    private Integer chapterNumber;
    private String chapterTitle;
    private String choiceText;
    private LocalDateTime chosenAt;

    public ChoiceRecord() {}

    // Getters and Setters
    public Long getChoiceId() { return choiceId; }
    public void setChoiceId(Long choiceId) { this.choiceId = choiceId; }

    public Integer getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(Integer chapterNumber) { this.chapterNumber = chapterNumber; }

    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }

    public String getChoiceText() { return choiceText; }
    public void setChoiceText(String choiceText) { this.choiceText = choiceText; }

    public LocalDateTime getChosenAt() { return chosenAt; }
    public void setChosenAt(LocalDateTime chosenAt) { this.chosenAt = chosenAt; }
  }

  public ChoiceHistoryResponse() {}

  // Getters and Setters
  public Long getSessionId() { return sessionId; }
  public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

  public List<ChoiceRecord> getChoices() { return choices; }
  public void setChoices(List<ChoiceRecord> choices) { this.choices = choices; }

  public Integer getTotalChoicesMade() { return totalChoicesMade; }
  public void setTotalChoicesMade(Integer totalChoicesMade) {
    this.totalChoicesMade = totalChoicesMade;
  }
}
