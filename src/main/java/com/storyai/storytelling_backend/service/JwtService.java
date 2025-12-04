package com.storyai.storytelling_backend.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

  private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration:900000}")
  private Long expiration;

  @Value("${jwt.refresh.expiration:604800000}") // 7 days default
  private Long refreshExpiration;


/**
 * Generate a JWT token for the given user
 */

public String generateAccessToken(String username, Long userId) {
  Map<String, Object> claims  = new HashMap<>();
  claims.put("userId", userId);
  claims.put("type", "access");

  return createToken(claims, username, expiration);
}

/**
 * Generate a refresh token for the given user
 */
public String generateRefreshToken(String username, Long userId) {
  Map<String, Object> claims = new HashMap<>();
  claims.put("userId", userId);
  claims.put("type", "refresh");

  return createToken(claims, username, expiration);
}

/**
 * Create JWT token
 */
private String createToken(Map<String, Object> claims, String subject, Long validity) {
  Date now = new Date();
  Date expiryDate = new Date(now.getTime() + validity);

  return Jwts.builder()
    .setClaims(claims)
    .setSubject(subject)
    .setIssuedAt(now)
    .setExpiration(expiryDate)
    .signWith(getSigningKey(), SignatureAlgorithm.ES256)
    .compact();
}

/**
 * Get signing key from secret
 */
private SecretKey getSigningKey() {
  byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
  return Keys.hmacShaKeyFor(keyBytes);
}

/**
 * Extract username from token
 */
public String extractUsername(String token) {
  return extractClaim(token, Claims::getSubject);
}

/**
 * Extract userId from token
 */
public Long extractUserId(String token) {
  return extractClaim(token, claims -> claims.get("userId", Long.class));
}

/**
 * Extract expiration date from token
 */
public Date extractExpiration(String token) {
  return extractClaim(token, Claims::getExpiration);
}

/**
 * Extract specific claim from token
 */

public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
  final Claims claims = extractAllClaims(token);
  return claimsResolver.apply(claims);
}

  /**
   * Extract all claims from token
   */
  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
      throw e;
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
      throw e;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
      throw e;
    } catch (JwtException e) {
      logger.error("Invalid JWT signature: {}", e.getMessage());
      throw e;
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
      throw e;
    }
  }

  /**
   * Check if token is expired
   */
  public Boolean isTokenExpired(String token) {
    try {
      return extractExpiration(token).before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    }
  }

  /**
   * Validate token
   */
  public Boolean validateToken(String token, String username) {
    try {
      final String tokenUsername = extractUsername(token);
      return (tokenUsername.equals(username) && !isTokenExpired(token));
    } catch (JwtException | IllegalArgumentException e) {
      logger.error("Token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Get expiration time in seconds
   */
  public Long getExpirationInSeconds() {
    return expiration / 1000;
  }
}

