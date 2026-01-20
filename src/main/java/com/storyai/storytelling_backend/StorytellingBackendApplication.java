package com.storyai.storytelling_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class StorytellingBackendApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context =
      SpringApplication.run(StorytellingBackendApplication.class, args);

    Environment env = context.getEnvironment();
    System.out.println("=== Loaded Configuration ===");
    System.out.println("DB_USERNAME: " + env.getProperty("DB_USERNAME"));
    System.out.println("DB_URL: " + env.getProperty("DB_URL"));
    System.out.println("Datasource URL: " + env.getProperty("spring.datasource.url"));
    System.out.println("Datasource Username: " + env.getProperty("spring.datasource.username"));
  }
}
