CREATE TABLE stories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    genre VARCHAR(50),
    difficulty_level VARCHAR(20) DEFAULT 'MEDIUM',
    is_public BOOLEAN DEFAULT false,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stories_created_by ON stories (created_by);
CREATE INDEX idx_stories_public On stories (is_public) WHERE is_public = true;
CREATE INDEX idx_stories_genre ON stories (genre);