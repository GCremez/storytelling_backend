package com.storyai.storytelling_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.StoryChapter;
import com.storyai.storytelling_backend.entity.StorySession;
import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.repository.StoryChapterRepository;
import com.storyai.storytelling_backend.repository.StorySessionRepository;

@Service
@Transactional
public class StorySessionService {

  private final StorySessionRepository storySessionRepository;
  private final StoryChapterRepository chapterRepository;

  public StorySessionService(StorySessionRepository storySessionRepository, StoryChapterRepository chapterRepository) {
    this.storySessionRepository = storySessionRepository;
    this.chapterRepository = chapterRepository;
  }

  public StorySession startNewSession(User user, Story story) {
    StorySession session = new StorySession(user, story);
    session.setSessionData("{\"character_name\": \"\", \"health\": 100, \"inventory\": []}");
    return storySessionRepository.save(session);
  }

  @Transactional(readOnly = true)
  public Optional<StorySession> getActiveSession(User user, Story story) {
    return storySessionRepository.findByUserAndStoryAndIsCompletedFalse(user, story);
  }

  @Transactional(readOnly = true)
  public List<StorySession> getUserSessions(User user) {
    return storySessionRepository.findByUserOrderByLastPlayedDesc(user);
  }

  public StorySession UpdateSession(StorySession session, Integer newChapter, String sessionData) {
    session.setCurrentChapter(newChapter);
    session.setSessionData(sessionData);
    session.setLastPlayed(LocalDateTime.now());
    return storySessionRepository.save(session);
  }

  public StorySession completeSession(StorySession session) {
    session.setIsCompleted(true);
    return storySessionRepository.save(session);
  }

  public Optional<StoryChapter> advanceToNextChapter(StorySession session) {
    int nextNumber = (session.getCurrentChapter() == null ? 1 : session.getCurrentChapter() + 1);
    Optional<StoryChapter> next =
        chapterRepository.findByStoryAndChapterNumber(session.getStory(), nextNumber);

    if (next.isPresent()) {
      session.setCurrentChapter(nextNumber);
      session.setLastPlayed(LocalDateTime.now());
      storySessionRepository.save(session);
      return next;
    }

    // No more chapters -> mark complete
    session.setIsCompleted(true);
    session.setLastPlayed(LocalDateTime.now());
    storySessionRepository.save(session);
    return Optional.empty();
  }

  @Transactional(readOnly = true)
  public Optional<StorySession> getSessionById(Long id) {
    return storySessionRepository.findById(id);
  }
}
