package com.storyai.storytelling_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for caching AI-generated content to reduce API costs
 */
@Entity
@Table(name = "ai_cache", indexes = {
  @Index(name = "idx_cache_key", columnList = "cache_key"),
  @Index(name = "idx_expires_at", columnList = "expires_at")
})
public class AICache {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "cache_key", unique = true, nullable = false, length = 255)
  private String cacheKey;

  @Column(name = "content", columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "last_accessed_at")
  private LocalDateTime lastAccessedAt;

  @Column(name = "hit_count", nullable = false)
  private Integer hitCount = 0;

  // Constructors
  public AICache() {
    this.createdAt = LocalDateTime.now();
    this.hitCount = 0;
  }

  public AICache(String cacheKey, String content) {
    this();
    this.cacheKey = cacheKey;
    this.content = content;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCacheKey() {
    return cacheKey;
  }

  public void setCacheKey(String cacheKey) {
    this.cacheKey = cacheKey;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public LocalDateTime getLastAccessedAt() {
    return lastAccessedAt;
  }

  public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
    this.lastAccessedAt = lastAccessedAt;
  }

  public Integer getHitCount() {
    return hitCount;
  }

  public void setHitCount(Integer hitCount) {
    this.hitCount = hitCount;
  }

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    if (hitCount == null) {
      hitCount = 0;
    }
  }
}
