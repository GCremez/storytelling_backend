package com.storyai.storytelling_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.storyai.storytelling_backend.DTO.*;
import com.storyai.storytelling_backend.entity.*;
import com.storyai.storytelling_backend.exception.NotFoundException;
import com.storyai.storytelling_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StorySessionService {

  private final StorySessionRepository storySessionRepository;
  private final StoryChapterRepository chapterRepository;
  private final UserChoiceRepository choiceRepository;
  private final StoryRepository storyRepository;
  private final UserRepository userRepository;

  public StorySessionService(
      StorySessionRepository storySessionRepository,
      StoryChapterRepository chapterRepository,
      UserChoiceRepository choiceRepository,
      StoryRepository storyRepository,
      UserRepository userRepository) {
    this.storySessionRepository = storySessionRepository;
    this.chapterRepository = chapterRepository;
    this.choiceRepository = choiceRepository;
    this.storyRepository = storyRepository;
    this.userRepository = userRepository;
  }

  // New Story Session User
  public StorySession startNewSession(User user, Story story) {
    StorySession session = new StorySession(user, story);
    session.setSessionData("{\"character_name\": \"\", \"health\": 100, \"inventory\": []}");
    return storySessionRepository.save(session);
  }

  // Get Active(Non-Completed) Session User Story
  @Transactional(readOnly = true)
  public Optional<StorySession> getActiveSession(User user, Story story) {
    return storySessionRepository.findByUserAndStoryAndIsCompletedFalse(user, story);
  }

  // Get User Sessions for user, ordered by last played
  @Transactional(readOnly = true)
  public List<StorySession> getUserSessions(User user) {
    return storySessionRepository.findByUserOrderByLastPlayedDesc(user);
  }

  // Update session progress with new chapter and session data
  public StorySession updateSession(StorySession session, Integer newChapter, String sessionData) {
    session.setCurrentChapter(newChapter);
    session.setSessionData(sessionData);
    session.setLastPlayed(LocalDateTime.now());
    return storySessionRepository.save(session);
  }

  // Mark session as completed
  public StorySession completeSession(StorySession session) {
    session.setIsCompleted(true);
    return storySessionRepository.save(session);
  }

  // Advance to next chapter Automatically if user has not completed the story
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

  // Get session by id
  @Transactional(readOnly = true)
  public Optional<StorySession> getSessionById(Long id) {
    return storySessionRepository.findById(id);
  }

  // Start a session with DTO request
  public StorySession startSession(StartSessionRequest request, User user) {
    Story story = storyRepository.findById(request.getStoryId())
      .orElseThrow(() -> new RuntimeException("Story not found with id: " + request.getStoryId()));

    // Use existing session if available
    return startNewSession(user, story);
  }

  // Get current chapter with available choices
  @Transactional(readOnly = true)
  public CurrentChapterResponse getCurrentChapter(Long sessionId) {
    StorySession session = storySessionRepository.findById(sessionId)
      .orElseThrow(() -> new NotFoundException("Session not found with id" + sessionId));

    // Get current chapter
    Integer currentChapterNumber = session.getCurrentChapter() != null ? session.getCurrentChapter() : 1;
    StoryChapter chapter = chapterRepository
      .findByStoryAndChapterNumber(session.getStory(), currentChapterNumber)
      .orElseThrow(() -> new NotFoundException("Chapter " + currentChapterNumber + "not found with id"));

    // Get available choices for this chapter
    List<UserChoice> choices = choiceRepository.findByChapterOrderByOptionNumberAsc(chapter);

    CurrentChapterResponse response = new CurrentChapterResponse();
    response.setChapterId(chapter.getId());
    response.setChapterNumber(chapter.getChapterNumber());
    response.setContent(chapter.getContent());
    response.setComplete(session.getIsCompleted() != null && session.getIsCompleted());
    response.setTimestamp(LocalDateTime.now());

    // Map choices to CHOICEOption
    List<CurrentChapterResponse.ChoiceOption> choiceOptions = choices.stream()
      .map(choice -> new CurrentChapterResponse.ChoiceOption(
        choice.getId(),
        choice.getChoiceText(),
        choice.getOptionNumber()
      ))
      .collect(Collectors.toList());

    response.setAvailableChoices(choiceOptions);
    return response;
  }

  // Make a choice and advance the story

  public CurrentChapterResponse makeChoice(Long sessionId, MakeChoiceRequest request) {
    StorySession session = storySessionRepository.findById(sessionId)
      .orElseThrow(() -> new NotFoundException("Session not found with id: " + sessionId));

    // Check if session is completed
    if (session.getIsCompleted() != null && session.getIsCompleted()) {
      throw new IllegalStateException("This Story Session is already completed");
    }

    // Validate the choice exists
    UserChoice choice = choiceRepository.findById(request.getChoiceId())
      .orElseThrow(() -> new NotFoundException("Choice not found with id: " + request.getChoiceId()));

    // Get Current Chapter
    Integer currentChapterNumber = session.getCurrentChapter() != null ? session.getCurrentChapter() : 1;
    StoryChapter currentChapter = chapterRepository
      .findByStoryAndChapterNumber(session.getStory(), currentChapterNumber)
      .orElseThrow(() -> new NotFoundException("Current chapter not found"));

    // Check if choice is valid for current chapter
    if (!choice.getChapter().getId().equals(currentChapter.getId())) {
      throw new IllegalStateException("Choice does not belong to current chapter");
    }
    //Record the choice
    choice.setChosenAt(LocalDateTime.now());
    choiceRepository.save(choice);

    //Advance to next chapter
    Integer nextChapterNumber = choice.getNextChapterNumber();
    if (nextChapterNumber != null) {
      session.setCurrentChapter(nextChapterNumber);
      session.setLastPlayed(LocalDateTime.now());
    } else {
      // Story Completed
      session.setIsCompleted(true);
      session.setLastPlayed(LocalDateTime.now());
    }

    storySessionRepository.save(session);

    // Return the new current chapter (or completion status)
    return getCurrentChapter(sessionId);
  }


/**
 * Get choice history for a session
 */

@Transactional(readOnly = true)
public ChoiceHistoryResponse getChoiceHistory(Long sessionId) {
  StorySession session = storySessionRepository.findById(sessionId)
    .orElseThrow(() -> new NotFoundException("Session not found with id: " + sessionId));

  // Get all choices made in this story
  List<UserChoice> madeChoices = choiceRepository
    .findByChapterStoryAndChosenAtIsNotNull(session.getStory());

  ChoiceHistoryResponse response = new ChoiceHistoryResponse();
  response.setSessionId(sessionId);
  response.setTotalChoicesMade(madeChoices.size());

  List<ChoiceHistoryResponse.ChoiceRecord> choiceRecords = madeChoices.stream()
    .map(choice -> {
      ChoiceHistoryResponse.ChoiceRecord record =
        new ChoiceHistoryResponse.ChoiceRecord();
      record.setChoiceId(choice.getId());
      record.setChapterNumber(choice.getChapter().getChapterNumber());
      record.setChapterTitle(choice.getChapter().getTitle());
      record.setChoiceText(choice.getChoiceText());
      record.setChosenAt(choice.getChosenAt());
      return record;
    })
    .collect(Collectors.toList());

  response.setChoices(choiceRecords);

  return response;
}

/**
 * Get all sessions for a user by user ID
 */

@Transactional(readOnly = true)
public List<StorySession> getUserSessionsByUserId(Long userId) {
  User user = userRepository.findById(userId)
    .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
  return getUserSessions(user);
}

/**
 * Delete a session
 */
public void deleteSession(Long sessionId) {
  if (!storySessionRepository.existsById(sessionId)) {
    throw new NotFoundException("Session not found with id: " + sessionId);
  }
  storySessionRepository.deleteById(sessionId);
}
}
