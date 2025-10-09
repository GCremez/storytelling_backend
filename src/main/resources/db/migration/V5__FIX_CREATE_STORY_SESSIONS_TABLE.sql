-- Remove the unique constraint
ALTER TABLE story_sessions DROP CONSTRAINT IF EXISTS story_sessions_user_id_story_id_key;

-- Fix the index name (drop and recreate)
DROP INDEX IF EXISTS idx_sessions_last__played;
CREATE INDEX IF NOT EXISTS idx_sessions_last_played ON story_sessions(last_played);

--Add additional useful index
CREATE INDEX IF NOT EXISTS idx_sessions_user_story ON story_sessions(user_id, story_id);