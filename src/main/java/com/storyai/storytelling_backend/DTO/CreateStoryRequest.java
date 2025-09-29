package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.NotBlank;

public class CreateStoryRequest {
  @NotBlank
  private String title;
  @NotBlank
  private String description;
  @NotBlank
  private String genre;

  public CreateStoryRequest() {}

  public CreateStoryRequest(String title, String description, String genre) {
    this.title = title;
    this.description = description;
    this.genre = genre;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }
}
