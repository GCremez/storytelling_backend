CREATE TABLE ai_generations (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT REFERENCES story_sessions(id) ON DELETE CASCADE,
    prompt_hash VARCHAR(64) UNIQUE NOT NULL,
    generated_content TEXT NOT NULL,
    ai_model VARCHAR(50),
    tokens_used INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_ai_generations_session ON ai_generations(session_id);
CREATE INDEX idx_ai_generations_prompt_hash ON ai_generations(prompt_hash);
CREATE INDEX idx_ai_generations_created_at ON ai_generations(created_at);