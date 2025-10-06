package com.storyai.storytelling_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html/**",
                        "/v3/api-docs",
                        "/actuator/**",
                        "/error")
                    .permitAll()
                    .anyRequest()
                    .permitAll() // Allow all other requests for now
            )
        .csrf(
            csrf ->
                csrf.ignoringRequestMatchers(
                    new AntPathRequestMatcher("/**") // Disable CSRF for all endpoints
                    ))
        .headers(
            headers -> headers.frameOptions().disable() // For H2 console if used
            );

    return http.build();
  }
}
