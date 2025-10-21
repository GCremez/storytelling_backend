package com.storyai.storytelling_backend.service;

import com.storyai.storytelling_backend.entity.AICache;
import com.storyai.storytelling_backend.repository.AICacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Service for caching AI-generated content to reduce API calls and costs
 */
@Service
@Transactional
public class AICacheService {

  private static final Logger logger = LoggerFactory.getLogger(AICacheService.class);
  private static final int DEFAULT_CACHE_HOURS = 24 * 7; // 7 DAYS

  private final AICacheRepository cacheRepository;

  public AICacheService (AICacheRepository cacheRepository){
    this.cacheRepository = cacheRepository;
  }

  /**
   * Get cached content if available and not expired
   */


@Transactional(readOnly = true)
  public Optional<String> getCachedContent(String cacheKey){
  Optional<AICache>cached =cacheRepository.findByCacheKey(cacheKey);

  if (cached.isEmpty()) {
    logger.debug("Cache miss for key: {}", cacheKey);
    return Optional.empty();
  }

  AICache cache = cached.get();

  // Check if expired
  if (cache.getExpiresAt() != null && cache.getExpiresAt().isBefore(LocalDateTime.now())) {
    logger.debug("Cache Expired for the key: {}", cacheKey);
    cacheRepository.delete(cache);
    return Optional.empty();
  }

  // Update hit count and last access time

  cache.setHitCount(cache.getHitCount() + 1);
  cache.setLastAccessedAt(LocalDateTime.now());
  cacheRepository.save(cache);

  logger.debug("Cache hit for key: {}", cacheKey, cache.getHitCount());
  return Optional.of(cache.getContent());
  }

  /**
   * Cache content with default expiration time
   */
  public void cacheContent(String cacheKey, String content) {
    cacheContent(cacheKey, content, DEFAULT_CACHE_HOURS);
  }

  /**
   * Cache content with custom expiration time
   */
  public void cacheContent(String cacheKey, String content, int expirationHours) {
    // Check if already exists
    Optional<AICache> existing = cacheRepository.findByCacheKey(cacheKey);

    AICache cache;
    if (existing.isPresent()) {
      cache = existing.get();
      cache.setCacheKey(content);
      cache.setExpiresAt(LocalDateTime.now().plusHours(expirationHours));
      logger.debug("Updated cache for Key: {}", cacheKey);
    } else {
      cache = new AICache();
      cache.setCacheKey(cacheKey);
      cache.setContent(content);
      cache.setCreatedAt(LocalDateTime.now());
      cache.setExpiresAt(LocalDateTime.now().plusHours(expirationHours));
      cache.setHitCount(0);
      logger.debug("Created new cache entry for the Key: {}", cacheKey);
    }

    cache.setLastAccessedAt(LocalDateTime.now());
    cacheRepository.save(cache);
  }

  /**
   * Invalidate (delete) cache content
   */
  public  void invalidateCache(String cacheKey) {
    cacheRepository.findByCacheKey(cacheKey).ifPresent(cache -> {
      cacheRepository.delete(cache);
      logger.debug("Invalidated cache for key: {}", cacheKey);
    });
  }

  /**
   * Clear all expired cache entries
   */
  public int clearExpiredCache() {
    int deleted = cacheRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    logger.info("cleared {} expired cache entries", deleted);
    return deleted;
  }

  /**
   * Get cache statistics
   */
  @Transactional(readOnly = true)
  public CacheStats getCacheStats() {
    long totalEntries = cacheRepository.count();
    long totalHits = cacheRepository.sumAllHitCounts();

    CacheStats stats = new CacheStats();
    stats.setTotalEntries(totalEntries);
    stats.setTotalHits(totalHits);
    stats.setAverageHitsPerEntry(totalEntries > 0 ? (double) totalHits / totalEntries : 0);

    return stats;
  }

  /**
   * Clear all cache (use with caution!)
   */
  public void clearAllCache() {
    cacheRepository.deleteAll();
    logger.warn("Cleared ALL cache entries");
  }


  /**
   * INNER CLASS
   */

  public static class CacheStats {
    private long totalEntries;
    private long totalHits;
    private double averageHitsPerEntry;

    public CacheStats() {
      this.totalEntries = 0;
      this.totalHits = 0;
      this.averageHitsPerEntry = 0.0;
    }

    public CacheStats(long totalEntries, long totalHits, double averageHitsPerEntry){
      this.totalEntries = totalEntries;
      this.totalHits = totalHits;
      this.averageHitsPerEntry = averageHitsPerEntry;
    }

    // GETTERS AND SETTERS
    public long getTotalEntries() {
      return totalEntries;
    }
    public void setTotalEntries(long totalEntries) {
      this.totalEntries = totalEntries;
    }
    public long getTotalHits() {
      return totalHits;
    }
    public void setTotalHits(long totalHits) {
      this.totalHits = totalHits;
    }
    public double getAverageHitsPerEntry() {
      return averageHitsPerEntry;
    }
    public void setAverageHitsPerEntry(double averageHitsPerEntry) {
      this.averageHitsPerEntry = averageHitsPerEntry;
    }
  }
}
