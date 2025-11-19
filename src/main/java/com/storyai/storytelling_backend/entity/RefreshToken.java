package com.storyai.storytelling_backend.entity;

import  jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "refresh_token")
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, unique = true)
  private String token;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "expired_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(nullable = false)
  private Boolean revoked = false;

  // CONSTRUCTOR
  public RefreshToken() {}

  public RefreshToken(User user) {
    this.user =user;
    this.token = UUID.randomUUID().toString();
    this.expiresAt = LocalDateTime.now().plusMinutes(7); // 7 days expiry
  }

  // Helper Methods
  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }
  public boolean isValid() {
    return !isExpired() && !revoked;
  }
  // Getters and Setters
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id; }

  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }

  public String getToken() {
    return token;
  }
  public void setToken(String token) {
    this.token = token;
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

  public Boolean getRevoked() {
    return revoked;
  }
  public void setRevoked(Boolean revoked) {
    this.revoked = revoked;
  }
}
