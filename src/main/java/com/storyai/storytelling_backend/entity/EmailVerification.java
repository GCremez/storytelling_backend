package com.storyai.storytelling_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification")
public class EmailVerification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "verification_code", nullable = false, length = 6)
  private String verificationCode;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "verified_at")
  private LocalDateTime verifiedAt;

  @Column(nullable = false)
  private Integer attempts = 0;

  // CONSTRUCTOR

  public EmailVerification() {}

  public EmailVerification(User user, String verificationCode) {
    this.user = user;
    this.verificationCode = verificationCode;
    this.expiresAt = LocalDateTime.now().plusMinutes(15);
  }

  // HELPER METHODS
  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  public boolean isValid(String code) {
    return !isExpired() &&
      verificationCode.equals(code) &&
      verifiedAt == null &&
      attempts < 3;
  }

  public  void incrementAttempts() {
    this.attempts++;
  }

  // GETTERS AND SETTERS
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public String getVerificationCode() { return verificationCode; }
  public void setVerificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
  }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getExpiresAt() { return expiresAt; }
  public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

  public LocalDateTime getVerifiedAt() { return verifiedAt; }
  public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

  public Integer getAttempts() { return attempts; }
  public void setAttempts(Integer attempts) { this.attempts = attempts; }
}
