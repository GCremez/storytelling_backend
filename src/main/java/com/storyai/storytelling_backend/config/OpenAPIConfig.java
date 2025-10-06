package com.storyai.storytelling_backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

  @Value("${server.port:8080}")
  private String serverPort;

  @Bean
  public OpenAPI storyTellingOpenAPI() {
    Server server = new Server();
    server.setUrl("http://localhost:" + serverPort);
    server.setDescription("Development Server");

    return new OpenAPI()
        .servers(List.of(server))
        .info(
            new Info()
                .title("Storytelling Backend API")
                .description("API for managing interactive stories and game sessions")
                .version("1.0.0")
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0")));
  }
}
