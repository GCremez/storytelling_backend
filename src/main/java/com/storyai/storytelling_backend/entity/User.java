package com.storyai.storytelling_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, length = 50)
  private String username;

  @Column(unique = true, nullable = false, length = 100)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  // NEW: Email verification field
  @Column(name = "is_verified", nullable = false)
  private Boolean isVerified = false;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  // NEW: Timestamp fields
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  @Column(name = "verification_sent_at")
  private LocalDateTime verificationSentAt;

  // Constructors
  public User() {}


  public User(Long id, String username, String email, String passwordHash, boolean active) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.isActive = active;
  }


  public User(String username, String email, String passwordHash, boolean isActive) {
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.isActive = isActive;
  }

  // Constructor for registration
  public User(String username, String email, String passwordHash) {
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.isActive = true;
    this.isVerified = false;
  }

  // Getters and Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

  // NEW: isVerified getters/setters
  public Boolean getIsVerified() { return isVerified; }
  public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

  // Updated to use Boolean instead of boolean (for consistency)
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }

  // Keep for backward compatibility
  public boolean isActive() { return isActive != null && isActive; }
  public void setActive(boolean active) { this.isActive = active; }

  // NEW: Timestamp getters/setters
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  public LocalDateTime getLastLogin() { return lastLogin; }
  public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

  public LocalDateTime getVerificationSentAt() { return verificationSentAt; }
  public void setVerificationSentAt(LocalDateTime verificationSentAt) {
    this.verificationSentAt = verificationSentAt;
  }
}
