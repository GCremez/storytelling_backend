package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.Min;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for updating an existing chapter")
public class UpdateChapterRequest {
  @Schema(
      description = "New chapter number (must be positive)",
      example = "2",
      minimum = "1",
      nullable = true)
  @Min(1)
  private Integer chapterNumber;

  @Schema(
      description = "New title for the chapter",
      example = "The Dark Forest - Revised",
      nullable = true)
  private String title;

  @Schema(
      description = "New content for the chapter in markdown format",
      example =
          "As you step into the forest, you notice the trees are much taller than they appeared...",
      nullable = true)
  private String content;

  // Getters and Setters
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
