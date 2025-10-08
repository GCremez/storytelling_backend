-- 1. Add new columns to story_sessions table
ALTER TABLE story_sessions
ADD COLUMN IF NOT EXISTS started_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS last_activity_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP;

-- 2. Populate started_at for existing sessions (use created_at as fallback)
UPDATE story_sessions
SET started_at = COALESCE(created_at, CURRENT_TIMESTAMP)
WHERE started_at IS NULL;

-- 3. Populate last_activity_at for existing sessions
UPDATE story_sessions
SET last_activity_at = COALESCE(last_played, created_at, CURRENT_TIMESTAMP)
WHERE last_activity_at IS NULL;
