package com.storyai.storytelling_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIProviderConfig {
    private String provider;
    private OpenAIConfig openai;
    private ClaudeConfig claude;

    // Getters and Setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public OpenAIConfig getOpenai() {
        return openai;
    }

    public void setOpenai(OpenAIConfig openai) {
        this.openai = openai;
    }

    public ClaudeConfig getClaude() {
        return claude;
    }

    public void setClaude(ClaudeConfig claude) {
        this.claude = claude;
    }

    public static class OpenAIConfig {
        private String apiKey;
        private String model;
        private Integer maxTokens;
        private Double temperature;

        // Getters and Setters
        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Integer getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
    }

    public static class ClaudeConfig {
        private String apiKey;
        private String model;
        private Integer maxTokens;
        private Double temperature;

        // Getters and Setters
        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Integer getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
    }
}
