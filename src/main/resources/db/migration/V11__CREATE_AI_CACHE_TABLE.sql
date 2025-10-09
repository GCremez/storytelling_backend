CREATE TABLE IF NOT EXISTS ai_cache (
    id BIGSERIAL PRIMARY KEY,
    cache_key VARCHAR(255) NOT NULL UNIQUE,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    last_accessed_at TIMESTAMP,
    hit_count INT NOT NULL DEFAULT 0
);


-- indexes
CREATE INDEX IF NOT EXISTS idx_ai_cache_key ON ai_cache(cache_key);
CREATE INDEX IF NOT EXISTS idx_ai_cache_expires_at ON ai_cache(expires_at);
CREATE INDEX IF NOT EXISTS idx_ai_cache_last_accessed ON ai_cache(last_accessed_at);

-- comment to table
COMMENT ON TABLE ai_cache IS 'Caches AI-generated content to reduce API calls and costs';

