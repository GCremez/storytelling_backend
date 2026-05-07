package com.storyai.storytelling_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.storyai.storytelling_backend.DTO.ChapterResponse;
import com.storyai.storytelling_backend.DTO.StartSessionRequest;
import com.storyai.storytelling_backend.DTO.UpdateProgressRequest;
import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.StorySession;
import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.security.CustomUserDetails;
import com.storyai.storytelling_backend.service.StoryService;
import com.storyai.storytelling_backend.service.StorySessionService;

@RestController
@RequestMapping("/api/v1/sessions")
public class StorySessionController {

  private final StorySessionService sessionService;
  private final StoryService storyService;

  public StorySessionController(StorySessionService sessionService, StoryService storyService) {
    this.sessionService = sessionService;
    this.storyService = storyService;
  }

  @PostMapping
  public ResponseEntity<StorySession> startSession(
      @RequestBody StartSessionRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {

    // Get the authenticated user
    User user = userDetails.getUser();

    Story story =
        storyService
            .getStoryById(request.getStoryId())
            .orElseThrow(() -> new RuntimeException("Story not found"));

    // check if user has an active session
    var existingSession = sessionService.getActiveSession(user, story);
    if (existingSession.isPresent()) {
      return ResponseEntity.ok(existingSession.get());
    }

    // Start a new session
    StorySession session = sessionService.startNewSession(user, story);
    return ResponseEntity.status(HttpStatus.CREATED).body(session);
  }

  @GetMapping("/{id}")
  public ResponseEntity<StorySession> getSession(
      @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {

    User currentUser = userDetails.getUser();
    return sessionService
        .getSessionById(id)
        .filter(session -> session.getUser().getId().equals(currentUser.getId()))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<List<StorySession>> getUserSessions(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    User currentUser = userDetails.getUser();
    List<StorySession> sessions = sessionService.getUserSessions(currentUser);
    return ResponseEntity.ok(sessions);
  }

  @PutMapping("/{id}/progress")
  public ResponseEntity<StorySession> updateProgress(
      @PathVariable Long id,
      @RequestBody UpdateProgressRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    User currentUser = userDetails.getUser();
    StorySession session =
        sessionService
            .getSessionById(id)
            .filter(s -> s.getUser().getId().equals(currentUser.getId()))
            .orElseThrow(() -> new RuntimeException("Session not found"));

    StorySession updated =
        sessionService.updateSession(
            session, request.getCurrentChapter(), request.getSessionData());

    return ResponseEntity.ok(updated);
  }

  @PostMapping("/{id}/next")
  public ResponseEntity<?> nextChapter(
      @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {

    User currentUser = userDetails.getUser();
    StorySession session =
        sessionService
            .getSessionById(id)
            .filter(s -> s.getUser().getId().equals(currentUser.getId()))
            .orElseThrow(() -> new RuntimeException("Session not found"));

    if (Boolean.TRUE.equals(session.getIsCompleted())) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    var next = sessionService.advanceToNextChapter(session);
    return next.<ResponseEntity<?>>map(ch -> ResponseEntity.ok(ChapterResponse.fromEntity(ch)))
        .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSession(
      @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {

    User currentUser = userDetails.getUser();
    StorySession session =
        sessionService
            .getSessionById(id)
            .filter(s -> s.getUser().getId().equals(currentUser.getId()))
            .orElseThrow(() -> new RuntimeException("Session not found"));

    sessionService.deleteSession(id);
    return ResponseEntity.noContent().build();
  }
}
