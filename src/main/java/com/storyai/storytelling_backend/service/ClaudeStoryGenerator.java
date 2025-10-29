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
 * Claude (Anthropic) implementation of AI story generation
 * Uses Claude Sonnet or Opus models for content generation
 */
@Service
@ConditionalOnProperty(name = "ai.provider", havingValue = "claude")
public class ClaudeStoryGenerator implements AIStoryGenerator {

  private static final Logger logger = LoggerFactory.getLogger(ClaudeStoryGenerator.class);
  private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";

  private final AIProviderConfig.ClaudeConfig config;
  private final String apiKey;
  private final String model;
  private final Integer maxTokens;
  private final Double temperature;

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final AICacheService cacheService;

  public ClaudeStoryGenerator(RestTemplate restTemplate,
                            ObjectMapper objectMapper,
                            AICacheService cacheService,
                            AIProviderConfig config) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.cacheService = cacheService;
    this.config = config.getClaude();
    this.apiKey = this.config.getApiKey();
    this.model = this.config.getModel() != null ? this.config.getModel() : "claude-sonnet-4-5-20250929";
    this.maxTokens = this.config.getMaxTokens() != null ? this.config.getMaxTokens() : 2000;
    this.temperature = this.config.getTemperature() != null ? this.config.getTemperature() : 0.7;
  }

  @Override
  public GeneratedStoryResponse generateStory(GenerateStoryRequest request) {
    logger.info("Generating story with Claude for session: {}", request.getSessionId());

    // Check cache
    String cacheKey = buildStoryCacheKey(request);
    Optional<String> cached = cacheService.getCachedContent(cacheKey);

    if (cached.isPresent()) {
      logger.info("Returning cached story content");
      return buildCachedStoryResponse(cached.get());
    }

    // Build prompt
    String prompt = buildStoryPrompt(request);

    // Call Claude API
    String content = callClaude(prompt);

    // Cache result
    cacheService.cacheContent(cacheKey, content);

    GeneratedStoryResponse response = new GeneratedStoryResponse();
    response.setContent(content);
    response.setWordCount(countWords(content));
    response.setAiProvider("Claude - " + model);
    response.setCached(false);

    return response;
  }

  @Override
  public GeneratedChoicesResponse generateChoices(GenerateChoicesRequest request) {
    logger.info("Generating choices with Claude for chapter: {}", request.getChapterId());

    // Check cache
    String cacheKey = buildChoicesCacheKey(request);
    Optional<String> cached = cacheService.getCachedContent(cacheKey);

    if (cached.isPresent()) {
      logger.info("Returning cached choices");
      return parseCachedChoices(cached.get());
    }

    // Build prompt
    String prompt = buildChoicesPrompt(request);

    // Call Claude API
    String content = callClaude(prompt);

    // Parse choices
    List<GeneratedChoicesResponse.GeneratedChoice> choices = parseChoices(content,
      request.getNumberOfChoices() != null ? request.getNumberOfChoices() : 3);

    // Cache result
    cacheService.cacheContent(cacheKey, content);

    GeneratedChoicesResponse response = new GeneratedChoicesResponse();
    response.setChoices(choices);
    response.setAiProvider("Claude - " + model);
    response.setCached(false);

    return response;
  }

  @Override
  public boolean isAvailable() {
    return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("your-api-key-here");
  }

  @Override
  public String getProviderName() {
    return "Claude - " + model;
  }

  // ========== PRIVATE METHODS ==========

  private String callClaude(String prompt) {
    if (!isAvailable()) {
      throw new IllegalStateException("Claude API key not configured");
    }

    try {
      // Build request body (Claude's format)
      Map<String, Object> requestBody = new HashMap<>();
      requestBody.put("model", model);
      requestBody.put("max_tokens", maxTokens);
      requestBody.put("temperature", temperature);

      // Claude uses a messages array format
      List<Map<String, String>> messages = new ArrayList<>();
      messages.add(Map.of(
        "role", "user",
        "content", prompt
      ));
      requestBody.put("messages", messages);

      // Set headers (Claude-specific)
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("x-api-key", apiKey);
      headers.set("anthropic-version", "2023-06-01");

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

      // Make request
      ResponseEntity<String> response = restTemplate.exchange(
        CLAUDE_API_URL,
        HttpMethod.POST,
        entity,
        String.class
      );

      // Parse response (Claude's format)
      JsonNode root = objectMapper.readTree(response.getBody());
      JsonNode content = root.path("content");

      if (content.isArray() && content.size() > 0) {
        return content.get(0).path("text").asText();
      }

      throw new RuntimeException("Unexpected response format from Claude API");

    } catch (Exception e) {
      logger.error("Error calling Claude API", e);
      throw new RuntimeException("Failed to generate content from Claude", e);
    }
  }

  private String buildStoryPrompt(GenerateStoryRequest request) {
    StringBuilder prompt = new StringBuilder();
    prompt.append("You are a masterful storyteller creating an interactive fiction experience.\n\n");
    prompt.append("Generate an engaging story chapter with these specifications:\n\n");
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
      prompt.append("\nStory context and previous events:\n");
      request.getContext().forEach((key, value) ->
        prompt.append("- ").append(key).append(": ").append(value).append("\n")
      );
    }

    prompt.append("\nWrite a vivid, immersive chapter that:\n");
    prompt.append("- Engages the reader with rich descriptions\n");
    prompt.append("- Advances the plot naturally\n");
    prompt.append("- Creates tension and anticipation\n");
    prompt.append("- Leaves room for meaningful player choices\n\n");
    prompt.append("Begin the chapter:");

    return prompt.toString();
  }

  private String buildChoicesPrompt(GenerateChoicesRequest request) {
    int numChoices = request.getNumberOfChoices() != null ? request.getNumberOfChoices() : 3;

    StringBuilder prompt = new StringBuilder();
    prompt.append("You are creating choices for an interactive story.\n\n");
    prompt.append("Current situation:\n");
    prompt.append(request.getCurrentSituation()).append("\n\n");

    prompt.append("Generate ").append(numChoices).append(" distinct, meaningful choices for the player.\n\n");

    if (request.getDifficultyLevel() != null) {
      prompt.append("Difficulty level: ").append(request.getDifficultyLevel()).append("\n");
    }

    prompt.append("\nFormat each choice exactly as follows:\n");
    prompt.append("CHOICE: [Clear, action-oriented choice text]\n");
    prompt.append("CONSEQUENCE: [Brief hint about potential outcome]\n");
    prompt.append("TONE: [Emotional tone: brave/cautious/clever/aggressive/diplomatic]\n\n");

    prompt.append("Make each choice:\n");
    prompt.append("- Distinct and meaningful\n");
    prompt.append("- Lead to different story paths\n");
    prompt.append("- Reflect different character traits or strategies\n");
    prompt.append("- Feel natural to the situation\n\n");

    prompt.append("Generate the choices now:");

    return prompt.toString();
  }

  private List<GeneratedChoicesResponse.GeneratedChoice> parseChoices(String content, int expectedCount) {
    List<GeneratedChoicesResponse.GeneratedChoice> choices = new ArrayList<>();

    String[] lines = content.split("\n");
    String currentChoice = null;
    String currentConsequence = null;
    String currentTone = null;

    for (String line : lines) {
      line = line.trim();

      if (line.startsWith("CHOICE:")) {
        currentChoice = line.substring(7).trim();
      } else if (line.startsWith("CONSEQUENCE:")) {
        currentConsequence = line.substring(12).trim();
      } else if (line.startsWith("TONE:")) {
        currentTone = line.substring(5).trim();

        // We have all parts, create the choice
        if (currentChoice != null && currentConsequence != null) {
          GeneratedChoicesResponse.GeneratedChoice choice =
            new GeneratedChoicesResponse.GeneratedChoice(currentChoice, currentConsequence);
          choice.setEmotionalTone(currentTone);
          choices.add(choice);

          // Reset for next choice
          currentChoice = null;
          currentConsequence = null;
          currentTone = null;
        }
      }
    }

    // Add any remaining choice that might not have tone
    if (currentChoice != null && currentConsequence != null) {
      choices.add(new GeneratedChoicesResponse.GeneratedChoice(currentChoice, currentConsequence));
    }

    // Ensure we have expected count with fallbacks if needed
    while (choices.size() < expectedCount) {
      choices.add(new GeneratedChoicesResponse.GeneratedChoice(
        "Continue forward",
        "The story continues..."
      ));
    }

    return choices.stream().limit(expectedCount).collect(Collectors.toList());
  }

  private String buildStoryCacheKey(GenerateStoryRequest request) {
    return String.format("claude:story:%d:%d:%s",
      request.getStoryId(),
      request.getSessionId(),
      Objects.hash(request.getGenre(), request.getTheme(), request.getTone())
    );
  }

  private String buildChoicesCacheKey(GenerateChoicesRequest request) {
    return String.format("claude:choices:%d:%d:%s",
      request.getChapterId(),
      request.getSessionId(),
      Objects.hash(request.getCurrentSituation())
    );
  }

  private GeneratedStoryResponse buildCachedStoryResponse(String content) {
    GeneratedStoryResponse response = new GeneratedStoryResponse();
    response.setContent(content);
    response.setWordCount(countWords(content));
    response.setAiProvider("Claude - " + model + " (cached)");
    response.setCached(true);
    return response;
  }

  private GeneratedChoicesResponse parseCachedChoices(String content) {
    List<GeneratedChoicesResponse.GeneratedChoice> choices = parseChoices(content, 3);
    GeneratedChoicesResponse response = new GeneratedChoicesResponse();
    response.setChoices(choices);
    response.setAiProvider("Claude - " + model + " (cached)");
    response.setCached(true);
    return response;
  }

  private int countWords(String text) {
    if (text == null || text.isEmpty()) return 0;
    return text.split("\\s+").length;
  }
}
