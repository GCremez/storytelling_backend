package com.storyai.storytelling_backend.DTO;

import com.storyai.storytelling_backend.entity.StoryChapter;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing chapter details")
public class ChapterResponse {
  @Schema(description = "Unique identifier of the chapter", example = "1")
  private Long id;

  @Schema(description = "ID of the story this chapter belongs to", example = "42")
  private Long storyId;

  @Schema(description = "Chapter number in sequence", example = "1")
  private Integer chapterNumber;

  @Schema(description = "Title of the chapter", example = "The Dark Forest")
  private String title;

  @Schema(
      description = "Content of the chapter in markdown format",
      example = "You find yourself standing at the edge of a dark forest...")
  private String content;

  public ChapterResponse() {}

  public ChapterResponse(
      Long id, Long storyId, Integer chapterNumber, String title, String content) {
    this.id = id;
    this.storyId = storyId;
    this.chapterNumber = chapterNumber;
    this.title = title;
    this.content = content;
  }

  public static ChapterResponse fromEntity(StoryChapter chapter) {
    return new ChapterResponse(
        chapter.getId(),
        chapter.getStory() != null ? chapter.getStory().getId() : null,
        chapter.getChapterNumber(),
        chapter.getTitle(),
        chapter.getContent());
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getStoryId() {
    return storyId;
  }

  public void setStoryId(Long storyId) {
    this.storyId = storyId;
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
