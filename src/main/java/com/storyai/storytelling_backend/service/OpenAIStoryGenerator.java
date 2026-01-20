package com.storyai.storytelling_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.storyai.storytelling_backend.DTO.*;
import com.storyai.storytelling_backend.config.AIProviderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OpenAI implementation of AI story generation
 * Uses GPT-4 or GPT-3.5-turbo for content generation
 */
@Service
@ConditionalOnProperty(
  prefix = "ai",
  name = "provider",
  havingValue = "openai",
  matchIfMissing = true
)
public class OpenAIStoryGenerator implements AIStoryGenerator {

  private static final Logger logger = LoggerFactory.getLogger(OpenAIStoryGenerator.class);
  private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

  private final AIProviderConfig.OpenAIConfig config;
  private final String apiKey;
  private final String model;
  private final Integer maxTokens;
  private final Double temperature;

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final AICacheService cacheService;

  public OpenAIStoryGenerator(RestTemplate restTemplate,
                            ObjectMapper objectMapper,
                            AICacheService cacheService,
                            AIProviderConfig config) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.cacheService = cacheService;

    if (config.getOpenai() == null) {
      throw new IllegalStateException("OpenAI configuration is missing.");
    }

    this.config = config.getOpenai();
    if (this.config.getApiKey() == null) {
      throw new IllegalStateException(
        "OpenAI API key is missing"
      );
    }

    this.apiKey = this.config.getApiKey();
    this.model = this.config.getModel() != null ? this.config.getModel() : "gpt-4";
    this.maxTokens = this.config.getMaxTokens() != null ? this.config.getMaxTokens() : 1000;
    this.temperature = this.config.getTemperature() != null ? this.config.getTemperature() : 0.7;
  }

  @Override
  public GeneratedStoryResponse generateStory(GenerateStoryRequest request) {
    logger.info("Generating story for session: {}", request.getSessionId());

    // Check cache first
    String cacheKey = buildStoryCacheKey(request);
    Optional<String> cached = cacheService.getCachedContent(cacheKey);

    if (cached.isPresent()) {
      logger.info("Returning cached story content");
      return buildCachedStoryResponse(cached.get());
    }

    // Build prompt
    String prompt = buildStoryPrompt(request);

    // Call OpenAI API
    String content = callOpenAI(prompt, maxTokens);

    // Cache the result
    cacheService.cacheContent(cacheKey, content);

    // Build response
    GeneratedStoryResponse response = new GeneratedStoryResponse();
    response.setContent(content);
    response.setWordCount(countWords(content));
    response.setAiProvider("OpenAI - " + model);
    response.setCached(false);

    return response;
  }

  @Override
  public GeneratedChoicesResponse generateChoices(GenerateChoicesRequest request) {
    logger.info("Generating choices for chapter: {}", request.getChapterId());

    // Check cache
    String cacheKey = buildChoicesCacheKey(request);
    Optional<String> cached = cacheService.getCachedContent(cacheKey);

    if (cached.isPresent()) {
      logger.info("Returning cached choices");
      return parseCachedChoices(cached.get());
    }

    // Build prompt
    String prompt = buildChoicesPrompt(request);

    // Call OpenAI API
    String content = callOpenAI(prompt, 500);

    // Parse choices from response
    List<GeneratedChoicesResponse.GeneratedChoice> choices = parseChoices(content,
      request.getNumberOfChoices() != null ? request.getNumberOfChoices() : 3);

    // Cache the result
    cacheService.cacheContent(cacheKey, content);

    GeneratedChoicesResponse response = new GeneratedChoicesResponse();
    response.setChoices(choices);
    response.setAiProvider("OpenAI - " + model);
    response.setCached(false);

    return response;
  }

  @Override
  public boolean isAvailable() {
    return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-api-key-here");
  }

  @Override
  public String getProviderName() {
    return "OpenAI - " + model;
  }

  // ========== PRIVATE METHODS ==========

  private String callOpenAI(String prompt, Integer tokens) {
    if (!isAvailable()) {
      throw new IllegalStateException("OpenAI API key not configured");
    }

    try {
      // Build request body
      Map<String, Object> requestBody = new HashMap<>();
      requestBody.put("model", model);
      requestBody.put("max_tokens", tokens);
      requestBody.put("temperature", temperature);

      List<Map<String, String>> messages = new ArrayList<>();
      messages.add(Map.of("role", "system", "content", "You are a creative storyteller specializing in interactive fiction."));
      messages.add(Map.of("role", "user", "content", prompt));
      requestBody.put("messages", messages);

      // Set headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(apiKey);

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

      // Make request
      ResponseEntity<String> response = restTemplate.exchange(
        OPENAI_API_URL,
        HttpMethod.POST,
        entity,
        String.class
      );

      // Parse response
      JsonNode root = objectMapper.readTree(response.getBody());
      return root.path("choices").get(0).path("message").path("content").asText();

    } catch (Exception e) {
      logger.error("Error calling OpenAI API", e);
      throw new RuntimeException("Failed to generate content from OpenAI", e);
    }
  }

  private String buildStoryPrompt(GenerateStoryRequest request) {
    StringBuilder prompt = new StringBuilder();
    prompt.append("Generate an engaging story chapter with the following specifications:\n\n");
    prompt.append("Genre: ").append(request.getGenre()).append("\n");

    if (request.getTheme() != null) {
      prompt.append("Theme: ").append(request.getTheme()).append("\n");
    }
    if (request.getTone() != null) {
      prompt.append("Tone: ").append(request.getTone()).append("\n");
    }
    if (request.getTargetLength() != null) {
      prompt.append("Target length: approximately ").append(request.getTargetLength()).append(" words\n");
    }

    if (request.getContext() != null && !request.getContext().isEmpty()) {
      prompt.append("\nStory context:\n");
      request.getContext().forEach((key, value) ->
        prompt.append("- ").append(key).append(": ").append(value).append("\n")
      );
    }

    prompt.append("\nWrite an immersive chapter that advances the story naturally.");
    return prompt.toString();
  }

  private String buildChoicesPrompt(GenerateChoicesRequest request) {
    int numChoices = request.getNumberOfChoices() != null ? request.getNumberOfChoices() : 3;

    StringBuilder prompt = new StringBuilder();
    prompt.append("Based on this story situation:\n\n");
    prompt.append(request.getCurrentSituation()).append("\n\n");
    prompt.append("Generate ").append(numChoices).append(" distinct choice options for the player.\n");
    prompt.append("Format each choice as:\n");
    prompt.append("CHOICE: [action text]\n");
    prompt.append("CONSEQUENCE: [brief description of potential outcome]\n\n");

    if (request.getDifficultyLevel() != null) {
      prompt.append("Difficulty level: ").append(request.getDifficultyLevel()).append("\n");
    }

    prompt.append("Make choices meaningful, diverse, and engaging.");
    return prompt.toString();
  }

  private List<GeneratedChoicesResponse.GeneratedChoice> parseChoices(String content, int expectedCount) {
    List<GeneratedChoicesResponse.GeneratedChoice> choices = new ArrayList<>();

    String[] lines = content.split("\n");
    String currentChoice = null;

    for (String line : lines) {
      line = line.trim();
      if (line.startsWith("CHOICE:")) {
        currentChoice = line.substring(7).trim();
      } else if (line.startsWith("CONSEQUENCE:") && currentChoice != null) {
        String consequence = line.substring(12).trim();
        choices.add(new GeneratedChoicesResponse.GeneratedChoice(currentChoice, consequence));
        currentChoice = null;
      }
    }

    // Ensure we have the expected number of choices
    while (choices.size() < expectedCount) {
      choices.add(new GeneratedChoicesResponse.GeneratedChoice(
        "Continue the journey",
        "The story continues..."
      ));
    }

    return choices.stream().limit(expectedCount).collect(Collectors.toList());
  }

  private String buildStoryCacheKey(GenerateStoryRequest request) {
    return String.format("story:%d:%d:%s",
      request.getStoryId(),
      request.getSessionId(),
      Objects.hash(request.getGenre(), request.getTheme(), request.getTone())
    );
  }

  private String buildChoicesCacheKey(GenerateChoicesRequest request) {
    return String.format("choices:%d:%d:%s",
      request.getChapterId(),
      request.getSessionId(),
      Objects.hash(request.getCurrentSituation())
    );
  }

  private GeneratedStoryResponse buildCachedStoryResponse(String content) {
    GeneratedStoryResponse response = new GeneratedStoryResponse();
    response.setContent(content);
    response.setWordCount(countWords(content));
    response.setAiProvider("OpenAI - " + model + " (cached)");
    response.setCached(true);
    return response;
  }

  private GeneratedChoicesResponse parseCachedChoices(String content) {
    List<GeneratedChoicesResponse.GeneratedChoice> choices = parseChoices(content, 3);
    GeneratedChoicesResponse response = new GeneratedChoicesResponse();
    response.setChoices(choices);
    response.setAiProvider("OpenAI - " + model + " (cached)");
    response.setCached(true);
    return response;
  }

  private int countWords(String text) {
    if (text == null || text.isEmpty()) return 0;
    return text.split("\\s+").length;
  }
}
