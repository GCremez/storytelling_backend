-- ========================================
-- Migration V3.3: Update User Choices Table
-- ========================================

-- Add new columns to user_choices
ALTER TABLE user_choices
ADD COLUMN IF NOT EXISTS next_chapter_number INTEGER;

ALTER TABLE user_choices
ADD COLUMN IF NOT EXISTS consequence TEXT;

ALTER TABLE user_choices
ADD COLUMN IF NOT EXISTS chosen_at TIMESTAMP;

-- Rename column from choice_order to option_number (if it exists)
-- Check if old column exists and new one doesn't
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_choices' AND column_name = 'choice_order'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_choices' AND column_name = 'option_number'
    ) THEN
        ALTER TABLE user_choices RENAME COLUMN choice_order TO option_number;
    END IF;
END $$;

-- If option_number doesn't exist but choice_order doesn't exist either, add it
ALTER TABLE user_choices
ADD COLUMN IF NOT EXISTS option_number INTEGER DEFAULT 1;

-- Rename timestamp to created_at (if it exists and created_at doesn't)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_choices' AND column_name = 'timestamp'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_choices' AND column_name = 'created_at'
    ) THEN
        ALTER TABLE user_choices RENAME COLUMN timestamp TO created_at;
    END IF;
END $$;

-- Add created_at if it doesn't exist
ALTER TABLE user_choices
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Make session_id nullable (remove NOT NULL constraint)
ALTER TABLE user_choices
ALTER COLUMN session_id DROP NOT NULL;

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_user_choices_chapter_id ON user_choices(chapter_id);
CREATE INDEX IF NOT EXISTS idx_user_choices_session_id ON user_choices(session_id);
CREATE INDEX IF NOT EXISTS idx_user_choices_chosen_at ON user_choices(chosen_at);
CREATE INDEX IF NOT EXISTS idx_user_choices_option_number ON user_choices(option_number);
