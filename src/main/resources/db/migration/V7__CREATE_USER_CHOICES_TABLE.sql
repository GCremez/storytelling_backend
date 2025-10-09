CREATE TABLE user_choices (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT REFERENCES story_sessions(id) ON DELETE CASCADE,
    chapter_id BIGINT REFERENCES story_chapters(id) ON DELETE CASCADE,
    choice_text TEXT NOT NULL,
    choice_order INTEGER DEFAULT 1,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_choices_session ON user_choices(session_id);
CREATE INDEX idx_choices_chapter ON user_choices(chapter_id);
CREATE INDEX idx_choices_session_timestamp ON user_choices(session_id, timestamp);