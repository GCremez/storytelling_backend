package com.storyai.storytelling_backend.DTO;

import java.util.Map;

public class GeneratedStoryResponse {
  private String content;
  private String title;
  private Integer wordCount;
  private String aiProvider;
  private Boolean cached;
  private Map<String, Object> metadata;

  public GeneratedStoryResponse() {}

  // Getters and Setters
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getWordCount() {
    return wordCount;
  }

  public void setWordCount(Integer wordCount) {
    this.wordCount = wordCount;
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

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }
}
