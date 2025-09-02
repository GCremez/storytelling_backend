package com.storyai.storytelling_backend.repository;

import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.StorySession;
import com.storyai.storytelling_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StorySessionRepository extends JpaRepository<StorySession, Long> {

    List<StorySession> findByUserOrderByLastPlayedDesc(User user);

    Optional<StorySession> findByUserAndStoryAndIsCompletedFalse(User user, Story story);

    List<StorySession> findByUserAndIsCompletedFalse(User user);

    List<StorySession> findByStoryIdOrderByLastPlayedDesc(Long storyId);
}