package com.storyai.storytelling_backend.repository;

import com.storyai.storytelling_backend.entity.PasswordResetToken;
import com.storyai.storytelling_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  Optional<PasswordResetToken> findByToken(String token);

  void deleteByUser(User user);

  void deleteByExpiresAtBeforeOrUsedAtIsNotNull(LocalDateTime dateTime);
}
