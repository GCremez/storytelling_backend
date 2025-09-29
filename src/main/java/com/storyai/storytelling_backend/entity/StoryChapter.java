package com.storyai.storytelling_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "story_chapters")
public class StoryChapter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "story_id", nullable = false)
  private Story story;

  @Column(name = "chapter_number", nullable = false)
  private Integer chapterNumber;

  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "ai_generated")
  private Boolean aiGenerated = false;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // Constructors
  public StoryChapter() {}

  public StoryChapter(Story story, Integer chapterNumber, String title, String content) {
    this.story = story;
    this.chapterNumber = chapterNumber;
    this.title = title;
    this.content = content;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Story getStory() {
    return story;
  }

  public void setStory(Story story) {
    this.story = story;
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

  public Boolean getAiGenerated() {
    return aiGenerated;
  }

  public void setAiGenerated(Boolean aiGenerated) {
    this.aiGenerated = aiGenerated;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
