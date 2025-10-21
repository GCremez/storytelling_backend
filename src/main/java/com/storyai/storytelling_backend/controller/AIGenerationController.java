package com.storyai.storytelling_backend.controller;


import com.storyai.storytelling_backend.DTO.*;
import com.storyai.storytelling_backend.service.AICacheService;
import com.storyai.storytelling_backend.service.AIStoryGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Generation", description = "AI-powered story and choice generation")
public class AIGenerationController {

  private final AIStoryGenerator aiGenerator;
  private final AICacheService cacheService;

  public AIGenerationController(AIStoryGenerator aiGenerator,
                                AICacheService cacheService) {
    this.aiGenerator = aiGenerator;
    this.cacheService = cacheService;
  }

  @PostMapping("/generate-story")
  @Operation(summary = "Generate story content using AI",
             description = "Creates dynamic story content based on genre, theme, and context")
  public ResponseEntity<GeneratedStoryResponse> generateStory(
    @Valid @RequestBody GenerateStoryRequest request) {

    if (!aiGenerator.isAvailable()) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(createUnavailableStoryResponse());
    }

    GeneratedStoryResponse response = aiGenerator.generateStory(request);
    return ResponseEntity.ok(response);
}
  @PostMapping("/generate-choice")
  @Operation(summary = "Generate story choices options using AI",
             description = "Generates dynamic story choices based on genre, theme, and context")
  public ResponseEntity<GeneratedChoicesResponse> generateChoices(
    @Valid @RequestBody GenerateChoicesRequest request) {

    if (!aiGenerator.isAvailable()) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(createUnavailableChoicesReponse());
    }

    GeneratedChoicesResponse response = aiGenerator.generateChoices(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/status")
  @Operation(summary = "Check AI service Availability")
  public ResponseEntity<Map<String, Object>> getAIStatus() {
    Map<String, Object> status = new HashMap<>();
    status.put("available", aiGenerator.isAvailable());
    status.put("provider", aiGenerator.getProviderName());
    status.put("cacheStats", cacheService.getCacheStats());

    return ResponseEntity.ok(status);
  }

  @DeleteMapping("/cache/expired")
  @Operation(summary = "Clear all expired cache entries")
  public ResponseEntity<Map<String, Integer>> clearExpiredCache() {
    int cleared = cacheService.clearExpiredCache();
    return ResponseEntity.ok(Map.of("deletedEntries", cleared));
  }

  @GetMapping("/cache/stats")
  @Operation(summary = "Get cache statistics")
  public ResponseEntity<AICacheService.CacheStats> getCacheStats() {
    return ResponseEntity.ok(cacheService.getCacheStats());
  }


  /**
   * FALLBACK RESPONSES
    */

  private GeneratedStoryResponse createUnavailableStoryResponse() {
    GeneratedStoryResponse response = new GeneratedStoryResponse();
    response.setContent("AI service is currently unavailable. Please try again later.");
    response.setAiProvider("None");
    response.setCached(false);
    return response;
  }

  private GeneratedChoicesResponse createUnavailableChoicesReponse() {
    GeneratedChoicesResponse response = new GeneratedChoicesResponse();
    response.setChoices(java.util.List.of(
      new GeneratedChoicesResponse.GeneratedChoice(
        "Continue",
        "AI service is currently unavailable. Please try again later.")
    ));
    response.setAiProvider("None");
    response.setCached(false);
    return response;
  }
}
