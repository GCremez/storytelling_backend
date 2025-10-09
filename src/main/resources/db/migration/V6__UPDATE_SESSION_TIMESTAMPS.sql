-- Add new columns to story_sessions if they don't exist
ALTER TABLE story_sessions
ADD COLUMN IF NOT EXISTS started_at TIMESTAMP;

ALTER TABLE story_sessions
ADD COLUMN IF NOT EXISTS last_activity_at TIMESTAMP;

ALTER TABLE story_sessions
ADD COLUMN IF NOT EXISTS completed_at TIMESTAMP;

-- Update timestamps for existing records
UPDATE story_sessions
SET started_at = COALESCE(started_at, created_at, CURRENT_TIMESTAMP)
WHERE started_at IS NULL;

UPDATE story_sessions
SET last_activity_at = COALESCE(last_activity_at, last_played, created_at, CURRENT_TIMESTAMP)
WHERE last_activity_at IS NULL;

-- Set completed_at for completed sessions
UPDATE story_sessions
SET completed_at = COALESCE(completed_at, last_activity_at, last_played)
WHERE is_completed = true AND completed_at IS NULL;
