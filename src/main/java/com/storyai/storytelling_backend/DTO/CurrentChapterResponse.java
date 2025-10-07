package com.storyai.storytelling_backend.DTO;


import java.time.LocalDateTime;
import java.util.List;

public class CurrentChapterResponse {
  private Long chapterId;
  private String content;
  private Integer chapterNumber;
  private List<ChoiceOption> availableChoices;
  private LocalDateTime timestamp;
  private boolean isComplete;

  public  static  class  ChoiceOption {
    private Long choiceId;
    private String choiceText;
    private Integer optionNumber;

    public ChoiceOption() {}

    public ChoiceOption(Long choiceId, String choiceText, Integer optionNumber) {
      this.choiceId = choiceId;
      this.choiceText = choiceText;
      this.optionNumber = optionNumber;
    }

    // GETTERS AND SETTERS
    public Long getChoiceId() {return choiceId;}
    public void setChoiceId(Long choiceId) {this.choiceId = choiceId;}

    public String getChoiceText() {return choiceText;}
    public void setChoiceText(String choiceText){this.choiceText = choiceText;}

    public Integer getOptionNumber() {return optionNumber;}
    public void setOptionNumber(Integer optionNumber){this.optionNumber = optionNumber;}
  }

  public CurrentChapterResponse() {}

  // Getters and Setters
  public Long getChapterId() {return chapterId;}
  public void setChapterId(Long chapterId) {this.chapterId = chapterId;}

  public String getContent() {return content;}
  public void setContent(String content) {this.content = content;}

  public Integer getChapterNumber() {return chapterNumber;}
  public void setChapterNumber(Integer chapterNumber) {this.chapterNumber = chapterNumber;}

  public List<ChoiceOption> getAvailableChoices() {return availableChoices;}
  public void setAvailableChoices(List<ChoiceOption> availableChoices) {this.availableChoices = availableChoices;}

  public LocalDateTime getTimestamp() {return timestamp;}
  public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}

  public boolean isComplete() {return isComplete;}
  public void setComplete(boolean complete) {isComplete = complete;}
}
