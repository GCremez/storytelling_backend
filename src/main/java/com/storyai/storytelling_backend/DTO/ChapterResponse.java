package com.storyai.storytelling_backend.DTO;

import com.storyai.storytelling_backend.entity.StoryChapter;

public class ChapterResponse {
  private Long id;
  private Long storyId;
  private Integer chapterNumber;
  private String title;
  private String content;

  public ChapterResponse() {}

  public ChapterResponse(Long id, Long storyId, Integer chapterNumber, String title, String content) {
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
