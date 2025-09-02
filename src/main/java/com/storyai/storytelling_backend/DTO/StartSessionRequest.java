package com.storyai.storytelling_backend.DTO;

public class StartSessionRequest {
    private Long storyId;

    public StartSessionRequest() {}

    public StartSessionRequest(Long storyId) {
        this.storyId = storyId;
    }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
}
