CREATE TABLE story_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENTCES users(id),
    story_id BIGINT REFERENCES stories(id),
    session_data JSONB DEFAULT '{}',

)