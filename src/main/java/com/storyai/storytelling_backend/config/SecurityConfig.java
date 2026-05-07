package com.storyai.storytelling_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.storyai.storytelling_backend.security.JwtAuthenticationFilter;
import com.storyai.storytelling_backend.service.CustomUserDetailsService;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;

  public SecurityConfig(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(System.getenv().getOrDefault("CORS_ALLOWED_ORIGINS", "http://localhost:3000")));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of(
      HttpHeaders.AUTHORIZATION,
      HttpHeaders.CONTENT_TYPE,
      HttpHeaders.ACCEPT,
      HttpHeaders.ORIGIN
    ));
    configuration.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    return source -> configuration;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .csrf(csrf -> csrf.disable())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        // Public endpoints
        .requestMatchers("/api/v1/auth/**").permitAll()
        .requestMatchers("/api-docs/**").permitAll()
        .requestMatchers("/swagger-ui/**").permitAll()
        .requestMatchers("/swagger-ui.html").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/v1/stories").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/v1/stories/**").permitAll()
        .requestMatchers("/actuator/health").permitAll()
        // Protected endpoints
        .anyRequest().authenticated()
      )
      .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)
      .headers(headers -> headers
        .frameOptions(frame -> frame.deny())
        .contentTypeOptions(contentType -> {})
        .httpStrictTransportSecurity(hsts -> hsts
          .maxAgeInSeconds(31536000)
          .includeSubDomains(true))
      )
      .build();
  }
}
