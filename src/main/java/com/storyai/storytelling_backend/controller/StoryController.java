package com.storyai.storytelling_backend.controller;


import com.storyai.storytelling_backend.DTO.CreateStoryRequest;
import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1stories")
public class StoryController {
    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping
    public ResponseEntity<List<Story>> getPublicStories(
            @RequestParam(required = false) String genre) {
        List<Story> stories;

        if (genre != null && !genre.isEmpty()) {
            stories = storyService.getStoriesByGenre(genre);
        } else {
            stories = storyService.getPublicStories();
        }

        return ResponseEntity.ok(stories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Story> getStory(@PathVariable Long id) {
        return storyService.getStoryById(id)
                .map(story -> ResponseEntity.ok(story))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Story> createStory(@RequestBody CreateStoryRequest request) {
        //TODO: Get current user from security context
        // For now, we'll need basic auth first
        
        Story story = storyService.createStory(
                request.getTitle(),
                request.getDescription(),
                request.getGenre(),
                null // we'll fix this when we add authentication
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(story);
    }
}
