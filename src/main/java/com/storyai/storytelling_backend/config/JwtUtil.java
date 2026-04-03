package com.storyai.storytelling_backend.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

  @Value("${app.jwt.secret}") // Secret key for JWT
  private String secret;

  @Value("${app.jwt.expiration}") // 24 hours in seconds
  private Long expiration;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String generateToken(String username) {
    return Jwts.builder()
        .subject(username)
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plus(expiration, ChronoUnit.SECONDS)))
        .signWith(getSigningKey())
        .compact();
  }

  public String extractUsername(String token) {
    return getClaims(token).getSubject();
  }

  public Date extractExpiration(String token) {
    return getClaims(token).getExpiration();
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public Boolean validateToken(String token, String username) {
    final String extractedUsername = extractUsername(token);
    return (extractedUsername.equals(username) && !isTokenExpired(token));
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }
}
