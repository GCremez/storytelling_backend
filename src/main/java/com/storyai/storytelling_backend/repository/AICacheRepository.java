package com.storyai.storytelling_backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.storyai.storytelling_backend.entity.AICache;

@Repository
public interface AICacheRepository extends JpaRepository<AICache, Long> {

  /** Find cache entry by key */
  Optional<AICache> findByCacheKey(String cacheKey);

  /** Delete all expired cache entries */
  int deleteByExpiresAtBefore(LocalDateTime dateTime);

  /** Sum all hit counts for statistics */
  @Query("SELECT COALESCE(SUM(c.hitCount), 0) FROM AICache c")
  long sumAllHitCounts();

  /** Check if cache key exists */
  boolean existsByCacheKey(String cacheKey);
}
