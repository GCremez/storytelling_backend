package com.storyai.storytelling_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.storyai.storytelling_backend.DTO.CreateStoryRequest;
import com.storyai.storytelling_backend.DTO.StoryResponse;
import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.service.StoryService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/stories")
public class StoryController {
  private final StoryService storyService;

  public StoryController(StoryService storyService) {
    this.storyService = storyService;
  }

  @GetMapping
  public ResponseEntity<List<StoryResponse>> getPublicStories(
      @RequestParam(required = false) String genre) {
    List<Story> stories =
        (genre != null && !genre.isEmpty())
            ? storyService.getStoriesByGenre(genre)
            : storyService.getPublicStories();

    return ResponseEntity.ok(stories.stream().map(StoryResponse::fromEntity).toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<StoryResponse> getStory(@PathVariable Long id) {
    return storyService
        .getStoryById(id)
        .map(story -> ResponseEntity.ok(StoryResponse.fromEntity(story)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<StoryResponse> createStory(@Valid @RequestBody CreateStoryRequest request) {
    // TODO: Get current user from security context
    // For now, we'll need basic auth first

    Story story =
        storyService.createStory(
            request.getTitle(),
            request.getDescription(),
            request.getGenre(),
            null // we'll fix this when we add authentication
            );

    return ResponseEntity.status(HttpStatus.CREATED).body(StoryResponse.fromEntity(story));
  }
}
