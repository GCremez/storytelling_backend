package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateChapterRequest {
  @NotNull
  @Min(1)
  private Integer chapterNumber;

  @NotBlank
  private String title;

  @NotBlank
  private String content;

  public CreateChapterRequest() {}

  public CreateChapterRequest(Integer chapterNumber, String title, String content) {
    this.chapterNumber = chapterNumber;
    this.title = title;
    this.content = content;
  }

  public Integer getChapterNumber() {
    return chapterNumber;
  }

  public void setChapterNumber(Integer chapterNumber) {
    this.chapterNumber = chapterNumber;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
