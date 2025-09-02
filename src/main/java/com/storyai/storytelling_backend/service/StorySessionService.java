package com.storyai.storytelling_backend.service;


import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.StorySession;
import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.repository.StorySessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StorySessionService {

    private final StorySessionRepository storySessionRepository;

    public StorySessionService(StorySessionRepository storySessionRepository) {
        this.storySessionRepository = storySessionRepository;
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

    @Transactional(readOnly = true)
    public Optional<StorySession> getSessionById(Long id) {
        return storySessionRepository.findById(id);
    }
}
