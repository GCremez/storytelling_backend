package com.storyai.storytelling_backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.storyai.storytelling_backend.DTO.CreateStoryRequest;
import com.storyai.storytelling_backend.DTO.StoryResponse;
import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.security.CustomUserDetails;
import com.storyai.storytelling_backend.service.StoryService;
import com.storyai.storytelling_backend.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/stories")
@Tag(name = "Story Management", description = "APIs for managing interactive stories")
public class StoryController {
  private final StoryService storyService;
  private final UserRepository userRepository;

  public StoryController(StoryService storyService, UserRepository userRepository) {
    this.storyService = storyService;
    this.userRepository = userRepository;
  }

  @GetMapping
  @Operation(
    summary = "Get all public stories",
    description = "Retrieve a list of all published stories. Can be filtered by genre if specified."
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200", 
      description = "Successfully retrieved stories",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = StoryResponse.class)
      )
    )
  })
  public ResponseEntity<List<StoryResponse>> getPublicStories(
      @Parameter(
        description = "Filter stories by genre (optional)",
        example = "fantasy",
        schema = @Schema(allowableValues = {"fantasy", "sci-fi", "mystery", "romance", "horror", "adventure"})
      )
      @RequestParam(required = false) String genre) {
    List<Story> stories =
        (genre != null && !genre.isEmpty())
            ? storyService.getStoriesByGenre(genre)
            : storyService.getPublicStories();

    return ResponseEntity.ok(stories.stream().map(StoryResponse::fromEntity).toList());
  }

  @GetMapping("/{id}")
  @Operation(
    summary = "Get story by ID",
    description = "Retrieve a specific story by its unique identifier."
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200", 
      description = "Successfully retrieved story",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = StoryResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "404", 
      description = "Story not found"
    )
  })
  public ResponseEntity<StoryResponse> getStory(
      @Parameter(
        description = "Story ID",
        example = "1",
        required = true
      )
      @PathVariable Long id) {
    return storyService
        .getStoryById(id)
        .map(story -> ResponseEntity.ok(StoryResponse.fromEntity(story)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create a new story",
    description = "Create a new interactive story with the provided details. The story will be created as private by default."
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "201", 
      description = "Story successfully created",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = StoryResponse.class),
        examples = @ExampleObject(
          value = """
            {
              "id": 1,
              "title": "The Dragon's Quest",
              "description": "An epic fantasy adventure",
              "genre": "fantasy",
              "isPublic": false,
              "difficultyLevel": "EASY",
              "createdAt": "2026-04-02T19:20:05.887169",
              "updatedAt": "2026-04-02T19:20:05.887321"
            }
            """
        )
      )
    ),
    @ApiResponse(
      responseCode = "400", 
      description = "Invalid request data"
    )
  })
  public ResponseEntity<StoryResponse> createStory(
      @Parameter(
        description = "Story creation request",
        required = true
      )
      @Valid @RequestBody CreateStoryRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    
    // Get current user from security context
    User currentUser = userDetails.getUser();
    
    Story story =
        storyService.createStory(
            request.getTitle(),
            request.getDescription(),
            request.getGenre(),
            currentUser // now using actual authenticated user
            );

    return ResponseEntity.status(HttpStatus.CREATED).body(StoryResponse.fromEntity(story));
  }
}
