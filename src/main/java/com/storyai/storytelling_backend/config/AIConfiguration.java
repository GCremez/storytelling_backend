package com.storyai.storytelling_backend.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/** AI Configuration for AI Services and REST clients */
@Configuration
public class AIConfiguration {

  /** RestTemplate configured for AI API calls with appropriate timeouts */
  @Bean
  public RestTemplate aiRestTemplate(RestTemplateBuilder builder) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Duration.ofSeconds(10));
    factory.setReadTimeout(Duration.ofSeconds(60)); // AI CALLS CAN TAKE A LONG TIME
    return builder.requestFactory(() -> factory).build();
  }

  /** ObjectMapper for JSON processing */
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
