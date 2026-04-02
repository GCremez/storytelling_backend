package com.storyai.storytelling_backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class OpenAPIConfig {

  @Value("${server.port:8080}")
  private String serverPort;

  @Bean
  public OpenAPI storyTellingOpenAPI() {
    Server devServer = new Server();
    devServer.setUrl("http://localhost:" + serverPort);
    devServer.setDescription("Development Server");

    Server prodServer = new Server();
    prodServer.setUrl("https://api.storytelling.com");
    prodServer.setDescription("Production Server");

    return new OpenAPI()
        .servers(List.of(devServer, prodServer))
        .info(apiInfo())
        .tags(List.of(
            new Tag()
                .name("Story Management")
                .description("APIs for managing interactive stories and game sessions"),
            new Tag()
                .name("User Management")
                .description("APIs for user authentication and profile management"),
            new Tag()
                .name("AI Story Generation")
                .description("APIs for AI-powered story content generation")
        ));
  }

  private Info apiInfo() {
    return new Info()
        .title("Storytelling Backend API")
        .description("A comprehensive API for creating and managing interactive stories with AI-powered content generation. This backend supports multiple AI providers (OpenAI GPT and Anthropic Claude) for dynamic story creation.")
        .version("1.0.0")
        .contact(new Contact()
            .name("Storytelling Team")
            .email("support@storytelling.com")
            .url("https://storytelling.com"))
        .license(new License()
            .name("MIT License")
            .url("https://opensource.org/licenses/MIT"));
  }
}
