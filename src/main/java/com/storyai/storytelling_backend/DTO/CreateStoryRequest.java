package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating a new story")
public class CreateStoryRequest {
  
  @Schema(
    description = "Story title",
    example = "The Dragon's Quest",
    required = true,
    minLength = 1,
    maxLength = 200
  )
  @NotBlank 
  @Size(max = 200, message = "Title must not exceed 200 characters")
  private String title;
  
  @Schema(
    description = "Story description",
    example = "An epic fantasy adventure about a young hero's journey to save the kingdom",
    required = true
  )
  @NotBlank 
  private String description;
  
  @Schema(
    description = "Story genre",
    example = "fantasy",
    required = true,
    allowableValues = {"fantasy", "sci-fi", "mystery", "romance", "horror", "adventure"}
  )
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
