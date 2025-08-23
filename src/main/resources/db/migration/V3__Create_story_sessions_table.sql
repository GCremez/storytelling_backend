CREATE TABLE story_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    story_id BIGINT REFERENCES stories(id),
    session_data JSONB DEFAULT '{}',
    current_chapter INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_played TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_completed BOOLEAN DEFAULT FALSE,

    UNIQUE(user_id, story_id) -- One active session per user per story.
);

CREATE INDEX idx_sessions_user ON story_sessions(user_id);
CREATE INDEX idx_sessions_last__played ON story_sessions(last_played);