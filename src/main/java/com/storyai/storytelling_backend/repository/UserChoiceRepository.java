package com.storyai.storytelling_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyai.storytelling_backend.entity.StoryChapter;
import com.storyai.storytelling_backend.entity.StorySession;
import com.storyai.storytelling_backend.entity.UserChoice;

public interface UserChoiceRepository extends JpaRepository<UserChoice, Long> {
  List<UserChoice> findBySessionOrderByTimestamp(StorySession session);

  List<UserChoice> findBySessionIdOrderByTimestamp(Long sessionId);

  // Use StoryChapter entity instead of Long
  List<UserChoice> findBySessionAndChapterOrderByChoiceOrder(
      StorySession session, StoryChapter chapter);

  // Additional useful methods
  List<UserChoice> findByChapterOrderByChoiceOrder(StoryChapter chapter);
}
