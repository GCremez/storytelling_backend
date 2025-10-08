ALTER TABLE user_choices
RENAME COLUMN choice_order TO option_number;

ALTER TABLE user_choices
ADD COLUMN IF NOT EXISTS next_chapter_number INT,
ADD COLUMN IF NOT EXISTS consequence TEXT,
ADD COLUMN IF NOT EXISTS chosen_at TIMESTAMP;

ALTER TABLE user_choices
RENAME COLUMN timestamp TO created_at;

ALTER TABLE user_choices
ALTER COLUMN session_id DROP NOT NULL;


CREATE INDEX IF NOT EXISTS idx_user_choices_chapter ON user_choices(chapter_id);
CREATE INDEX IF NOT EXISTS idx_user_choices_session ON user_choices(session_id);
CREATE INDEX IF NOT EXISTS idx_user_choices_chosen ON user_choices(chosen_at);


