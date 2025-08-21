package com.storyai.storytelling_backend.DTO;

public class CreateStoryRequest {
    private String title;
    private String description;
    private String genre;

    //Constructor, getters and setters
    public CreateStoryRequest(String title, String description, String genre) {
        this.title = title;
        this.description = description;
        this.genre = genre;
    }

    //Getters and setters

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
}
