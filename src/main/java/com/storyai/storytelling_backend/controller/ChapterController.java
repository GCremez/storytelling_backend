package com.storyai.storytelling_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.storyai.storytelling_backend.DTO.ChapterResponse;
import com.storyai.storytelling_backend.DTO.CreateChapterRequest;
import com.storyai.storytelling_backend.entity.StoryChapter;
import com.storyai.storytelling_backend.service.ChapterService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ChapterController {

  private final ChapterService chapterService;

  public ChapterController(ChapterService chapterService) {
    this.chapterService = chapterService;
  }

  @PostMapping("/stories/{storyId}/chapters")
  public ResponseEntity<ChapterResponse> createChapter(
      @PathVariable Long storyId, @Valid @RequestBody CreateChapterRequest request) {
    StoryChapter chapter = chapterService.createChapter(storyId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ChapterResponse.fromEntity(chapter));
  }

  @GetMapping("/stories/{storyId}/chapters")
  public ResponseEntity<List<ChapterResponse>> listChapters(@PathVariable Long storyId) {
    List<StoryChapter> chapters = chapterService.listChapters(storyId);
    return ResponseEntity.ok(chapters.stream().map(ChapterResponse::fromEntity).toList());
  }

  @GetMapping("/chapters/{id}")
  public ResponseEntity<ChapterResponse> getChapter(@PathVariable Long id) {
    return chapterService
        .getChapter(id)
        .map(c -> ResponseEntity.ok(ChapterResponse.fromEntity(c)))
        .orElse(ResponseEntity.notFound().build());
  }
}
