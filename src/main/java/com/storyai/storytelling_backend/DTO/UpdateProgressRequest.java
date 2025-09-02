package com.storyai.storytelling_backend.DTO;

public class UpdateProgressRequest {
    private Integer currentChapter;
    private String sessionData;

    public UpdateProgressRequest() {}

    public UpdateProgressRequest(Integer currentChapter, String sessionData) {
        this.currentChapter = currentChapter;
        this.sessionData = sessionData;
    }

    public Integer getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(Integer currentChapter) {
        this.currentChapter = currentChapter;
    }

    public String getSessionData() {
        return sessionData;
    }

    public void setSessionData(String sessionData) {
        this.sessionData = sessionData;
    }
}
