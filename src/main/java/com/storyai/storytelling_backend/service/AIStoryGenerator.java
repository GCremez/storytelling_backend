package com.storyai.storytelling_backend.service;

import com.storyai.storytelling_backend.DTO.GenerateChoicesRequest;
import com.storyai.storytelling_backend.DTO.GenerateStoryRequest;
import com.storyai.storytelling_backend.DTO.GeneratedStoryResponse;
import com.storyai.storytelling_backend.DTO.GeneratedChoicesResponse;

/**
 * Interface for AI story generation services
 * Allows switching between different AI providers (OpenAI, Claude, etc.)
 */

public interface AIStoryGenerator {
  GeneratedStoryResponse generateStory(GenerateStoryRequest request);

  GeneratedChoicesResponse generateChoices(GenerateChoicesRequest request);

  boolean isAvailable();
  String getProviderName();
}
