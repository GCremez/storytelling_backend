package com.storyai.storytelling_backend.config;

import com.storyai.storytelling_backend.service.AICacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task to clean up expired cache entries
 */
@Component
@ConditionalOnProperty(name = "ai.cache.enabled", havingValue = "true", matchIfMissing = true)
public class CacheCleanupScheduler {

  public static final Logger logger = LoggerFactory.getLogger(CacheCleanupScheduler.class);

  private final AICacheService cacheService;

  public CacheCleanupScheduler(AICacheService cacheService) {
    this.cacheService = cacheService;
  }

  /**
   * Run daily at 3AM To clean Expired Cache
   * Configurable via ai.cache.cleanup.cron property
   */
  @Scheduled(cron = "${ai.cache.cleanup.cron:0 0 3 * * ?}")
  public void cleanupExpiredCache() {
    logger.info("Starting scheduled cache cleanup....");

    try {
      int deleteCount = cacheService.clearExpiredCache();
      logger.info("Cache cleanup completed. Deleted {} expired entries", deleteCount);

      // Log cache stats after cleanup
      AICacheService.CacheStats stats = cacheService.getCacheStats();
      logger.info(
          "Current cache stats - Total entries: {}, Total hits: {}, Average hits per entry: {}",
          stats.getTotalEntries(),
          stats.getTotalHits(),
          String.format("%.2f", stats.getAverageHitsPerEntry()));
    } catch (Exception e) {
      logger.error("Error during cache cleanup", e);
    }
  }
}
