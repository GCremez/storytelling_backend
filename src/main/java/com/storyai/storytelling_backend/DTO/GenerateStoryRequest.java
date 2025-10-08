package com.storyai.storytelling_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class GenerateStoryRequest {
    @NotNull
    private Long storyId;

    @NotNull
    private Long sessionId;

    @NotBlank
    private String genre;

    private String theme;
    private String tone; // e.g., "dark", "humorous", "serious"
    private Integer targetLength; // word count
    private Map<String, Object> context; // Story state, previous choices, etc.

    public GenerateStoryRequest() {}

    // Getters and Setters
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getTone() { return tone; }
    public void setTone(String tone) { this.tone = tone; }

    public Integer getTargetLength() { return targetLength; }
    public void setTargetLength(Integer targetLength) { this.targetLength = targetLength; }

    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }

  }
