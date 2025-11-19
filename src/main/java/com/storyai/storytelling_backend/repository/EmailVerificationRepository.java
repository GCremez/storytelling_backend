package com.storyai.storytelling_backend.repository;

import com.storyai.storytelling_backend.entity.EmailVerification;
import com.storyai.storytelling_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
  Optional<EmailVerification> findByUserAndVerifiedAtIsNull(User user);

  Optional<EmailVerification> findByUserEmailAndVerificationCodeAndVerifiedAtIsNull(
    String email, String verificationCode);

  void deleteByExpiresAtBefore(LocalDateTime dateTime);

  long countByUserAndCreatedAtAfter(User user, LocalDateTime dateTime);
}
