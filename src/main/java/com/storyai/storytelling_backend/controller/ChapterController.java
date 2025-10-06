package com.storyai.storytelling_backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.storyai.storytelling_backend.DTO.ChapterResponse;
import com.storyai.storytelling_backend.DTO.CreateChapterRequest;
import com.storyai.storytelling_backend.entity.StoryChapter;
import com.storyai.storytelling_backend.service.ChapterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Chapter Management", description = "APIs for managing story chapters")
public class ChapterController {

  private final ChapterService chapterService;

  public ChapterController(ChapterService chapterService) {
    this.chapterService = chapterService;
  }

  @PostMapping("/stories/{storyId}/chapters")
  @Operation(
      summary = "Create a new chapter",
      description = "Creates a new chapter for the specified story")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Chapter created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Story not found")
      })
  public ResponseEntity<ChapterResponse> createChapter(
      @Parameter(description = "ID of the story to add the chapter to") @PathVariable Long storyId,
      @Valid @RequestBody CreateChapterRequest request) {
    StoryChapter chapter = chapterService.createChapter(storyId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ChapterResponse.fromEntity(chapter));
  }

  @GetMapping("/stories/{storyId}/chapters")
  @Operation(
      summary = "List all chapters",
      description = "Retrieves all chapters for a specific story")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved chapters"),
        @ApiResponse(responseCode = "404", description = "Story not found")
      })
  public ResponseEntity<List<ChapterResponse>> listChapters(
      @Parameter(description = "ID of the story to get chapters for") @PathVariable Long storyId) {
    List<StoryChapter> chapters = chapterService.listChapters(storyId);
    return ResponseEntity.ok(chapters.stream().map(ChapterResponse::fromEntity).toList());
  }

  @GetMapping("/chapters/{id}")
  @Operation(summary = "Get chapter by ID", description = "Retrieves a specific chapter by its ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved chapter"),
        @ApiResponse(responseCode = "404", description = "Chapter not found")
      })
  public ResponseEntity<ChapterResponse> getChapter(
      @Parameter(description = "ID of the chapter to retrieve") @PathVariable Long id) {
    return chapterService
        .getChapter(id)
        .map(c -> ResponseEntity.ok(ChapterResponse.fromEntity(c)))
        .orElse(ResponseEntity.notFound().build());
  }
}
