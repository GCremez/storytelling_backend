-- Remove the unique constraint
ALTER TABLE Create_stories_sessions DROP CONSTANT Create_story_sessions_user_id_story_id_key;

-- Fix the index name (drop and recreate)
DROP INDEX IF EXISTS idx_sessions_last__played;
CREATE INDEX IF NOT idx_sessions_last_played ON stories_sessions(last_played);

--Add additional useful index
CREATE INDEX IF NOT idx_sessions_user_story ON stories_sessions(user_id, story_id);