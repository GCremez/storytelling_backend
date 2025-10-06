package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating a new chapter")
public class CreateChapterRequest {
  @Schema(description = "Chapter number (must be positive)", example = "1", required = true)
  @NotNull @Min(1)
  private Integer chapterNumber;

  @Schema(description = "Chapter title", example = "The Beginning", required = true)
  @NotBlank
  private String title;

  @Schema(
      description = "Chapter content in markdown format",
      example = "You find yourself in a dark forest...",
      required = true)
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
