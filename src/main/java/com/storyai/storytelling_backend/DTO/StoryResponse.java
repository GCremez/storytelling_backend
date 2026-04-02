package com.storyai.storytelling_backend.DTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.storyai.storytelling_backend.entity.Story;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing story information")
public record StoryResponse(
    @Schema(
        description = "Unique story identifier",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    long id,
    
    @Schema(
        description = "Story title",
        example = "The Dragon's Quest",
        maxLength = 200
    )
    String title,
    
    @Schema(
        description = "Story description",
        example = "An epic fantasy adventure about a young hero's journey to save the kingdom"
    )
    String description,
    
    @Schema(
        description = "Story genre",
        example = "fantasy",
        allowableValues = {"fantasy", "sci-fi", "mystery", "romance", "horror", "adventure"}
    )
    String genre,
    
    @Schema(
        description = "Whether the story is publicly visible",
        example = "false"
    )
    boolean isPublic,
    
    @Schema(
        description = "Story difficulty level",
        example = "EASY",
        allowableValues = {"EASY", "MEDIUM", "HARD"}
    )
    String difficultyLevel,
    
    @Schema(
        description = "Story creation timestamp",
        example = "2026-04-02T19:20:05.887169",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    String createdAt,
    
    @Schema(
        description = "Story last update timestamp", 
        example = "2026-04-02T19:20:05.887321",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    String updatedAt) {
  
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  
  public static StoryResponse fromEntity(Story story) {
    return new StoryResponse(
        story.getId(),
        story.getTitle(),
        story.getDescription(),
        story.getGenre(),
        story.getIsPublic(),
        story.getDifficultyLevel() != null ? story.getDifficultyLevel().name() : "EASY",
        story.getCreatedAt() != null ? story.getCreatedAt().format(FORMATTER) : null,
        story.getUpdatedAt() != null ? story.getUpdatedAt().format(FORMATTER) : null);
  }
}
