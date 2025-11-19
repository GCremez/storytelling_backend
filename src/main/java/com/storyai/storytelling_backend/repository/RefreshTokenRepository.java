package com.storyai.storytelling_backend.repository;

import com.storyai.storytelling_backend.entity.RefreshToken;
import com.storyai.storytelling_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  void deleteByUser(User user);

  void deleteByExpiresAtBeforeOrRevokedTrue(LocalDateTime dataTime);

  Optional<RefreshToken> findByUserAndRevokedFalse(User user);
}
