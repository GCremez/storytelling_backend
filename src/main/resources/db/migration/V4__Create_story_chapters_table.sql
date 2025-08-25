CREATE TABLE story_chapters (
    id BIGSERIAL PRIMARY KEY,
    story_id BIGINT REFERENCES stories(id) ON DELETE CASCADE,
    chapter_number INTEGER NOT NULL,
    title VARCHAR(200),
    content TEXT NOT NULL,
    ai_generated BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Ensure chapter numbers are unique within a story
    UNIQUE(story_id, chapter_number)
);

-- Indexes for performance
CREATE INDEX idx_chapters_story ON story_chapters(story_id);
CREATE INDEX idx_chapters_story_number ON story_chapters(story_id, chapter_number);