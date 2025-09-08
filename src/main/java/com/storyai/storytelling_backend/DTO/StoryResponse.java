package com.storyai.storytelling_backend.DTO;


import com.storyai.storytelling_backend.entity.Story;

import java.time.LocalDateTime;

public record StoryResponse(
        long id,
        String title,
        String description,
        String genre,
        boolean isPublic,
        String difficultyLevel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static StoryResponse fromEntity(Story story) {
        return new StoryResponse(
                story.getId(),
                story.getTitle(),
                story.getDescription(),
                story.getGenre(),
                story.getIsPublic(),
                story.getDifficultyLevel().name(),
                story.getCreatedAt(),
                story.getUpdatedAt()
        );
    }
}
