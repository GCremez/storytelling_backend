package com.storyai.storytelling_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.storyai.storytelling_backend.DTO.ChapterResponse;
import com.storyai.storytelling_backend.DTO.StartSessionRequest;
import com.storyai.storytelling_backend.DTO.UpdateProgressRequest;
import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.StorySession;
import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.service.StoryService;
import com.storyai.storytelling_backend.service.StorySessionService;
import com.storyai.storytelling_backend.service.UserService;

@RestController
@RequestMapping("/api/v1/sessions")
public class StorySessionController {

  private final StorySessionService sessionService;
  private final StoryService storyService;
  private final UserService userService;

  // constructor
  public StorySessionController(
      StorySessionService sessionService, StoryService storyService, UserService userService) {
    this.sessionService = sessionService;
    this.storyService = storyService;
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<StorySession> startSession(@RequestBody StartSessionRequest request) {
    // TODO: Get current user from security context
    // For now, we are creating a default user

    User user = userService.getOrCreateDefaultUser();

    Story story =
        storyService
            .getStoryById(request.getStoryId())
            .orElseThrow(() -> new RuntimeException("Story not found"));

    // check if user has an active session
    var existingSession = sessionService.getActiveSession(user, story);
    if (existingSession.isPresent()) {
      return ResponseEntity.ok(existingSession.get());
    }

    StorySession session = sessionService.startNewSession(user, story);
    return ResponseEntity.status(HttpStatus.CREATED).body(session);
  }

  @GetMapping("/{id}")
  public ResponseEntity<StorySession> getSession(@PathVariable Long id) {
    // TODO: Add user authorization check
    return sessionService
        .getSessionById(id)
        .map(session -> ResponseEntity.ok(session))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<List<StorySession>> getUserSessions() {
    // TODO: Get current user from security context
    User user = userService.getOrCreateDefaultUser();
    List<StorySession> sessions = sessionService.getUserSessions(user);
    return ResponseEntity.ok(sessions);
  }

  @PutMapping("/{id}/progress")
  public ResponseEntity<StorySession> updateProgress(
      @PathVariable Long id, @RequestBody UpdateProgressRequest request) {

    StorySession session =
        sessionService
            .getSessionById(id)
            .orElseThrow(() -> new RuntimeException("Session not found"));

    StorySession updated =
        sessionService.UpdateSession(
            session, request.getCurrentChapter(), request.getSessionData());

    return ResponseEntity.ok(updated);
  }

  @PostMapping("/{id}/next")
  public ResponseEntity<?> nextChapter(@PathVariable Long id) {
    StorySession session =
        sessionService
            .getSessionById(id)
            .orElseThrow(() -> new RuntimeException("Session not found"));

    if (Boolean.TRUE.equals(session.getIsCompleted())) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    var next = sessionService.advanceToNextChapter(session);
    return next.<ResponseEntity<?>>map(ch -> ResponseEntity.ok(ChapterResponse.fromEntity(ch)))
        .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
  }
}
