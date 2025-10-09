package com.storyai.storytelling_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.StoryChapter;
import com.storyai.storytelling_backend.entity.StorySession;
import com.storyai.storytelling_backend.entity.UserChoice;

@Repository
public interface UserChoiceRepository extends JpaRepository<UserChoice, Long> {

  // ========== LEGACY METHODS (Updated for new field names) ==========

  /** Find choices by session ordered by creation time UPDATED: timestamp → createdAt */
  List<UserChoice> findBySessionOrderByCreatedAt(StorySession session);

  /** Find choices by session ID ordered by creation time UPDATED: timestamp → createdAt */
  List<UserChoice> findBySessionIdOrderByCreatedAt(Long sessionId);

  /**
   * Find choices by session and chapter ordered by option number UPDATED: choiceOrder →
   * optionNumber
   */
  List<UserChoice> findBySessionAndChapterOrderByOptionNumber(
      StorySession session, StoryChapter chapter);

  /** Find all choices for a chapter ordered by option number UPDATED: choiceOrder → optionNumber */
  List<UserChoice> findByChapterOrderByOptionNumber(StoryChapter chapter);

  // ========== NEW METHODS (For Choice System) ==========

  /**
   * Find all choices for a specific chapter, ordered by option number Used to display available
   * choices to user
   */
  List<UserChoice> findByChapterOrderByOptionNumberAsc(StoryChapter chapter);

  /** Find all choices that have been made (chosen) for a specific story Used for choice history */
  List<UserChoice> findByChapterStoryAndChosenAtIsNotNull(Story story);

  /** Find choices by chapter that haven't been chosen yet (available choices) */
  List<UserChoice> findByChapterAndChosenAtIsNull(StoryChapter chapter);

  /** Find choices that have been chosen in a specific session */
  List<UserChoice> findBySessionAndChosenAtIsNotNullOrderByChosenAt(StorySession session);

  /** Count how many choices are available for a chapter */
  long countByChapter(StoryChapter chapter);

  /** Count how many choices have been made in a session */
  long countBySessionAndChosenAtIsNotNull(StorySession session);

  /** Find the most recent choice made in a session */
  UserChoice findFirstBySessionOrderByChosenAtDesc(StorySession session);

  /** Check if a specific choice has been chosen */
  boolean existsByIdAndChosenAtIsNotNull(Long choiceId);

  /** Find choices by chapter and next chapter number Useful for determining where a choice leads */
  List<UserChoice> findByChapterAndNextChapterNumber(
      StoryChapter chapter, Integer nextChapterNumber);

  /** Find choices that lead to story endings (nextChapterNumber is null) */
  List<UserChoice> findByChapterAndNextChapterNumberIsNull(StoryChapter chapter);

  /** Custom query to get choice statistics for a story */
  @Query(
      "SELECT c FROM UserChoice c WHERE c.chapter.story = :story AND c.chosenAt IS NOT NULL ORDER BY c.chosenAt DESC")
  List<UserChoice> findChosenChoicesByStory(Story story);

  /** Find all available (unchosen) choices for a specific chapter */
  @Query(
      "SELECT c FROM UserChoice c WHERE c.chapter = :chapter AND c.chosenAt IS NULL ORDER BY c.optionNumber ASC")
  List<UserChoice> findAvailableChoicesByChapter(StoryChapter chapter);

  /** Get choice history for a session with full details */
  @Query(
      "SELECT c FROM UserChoice c WHERE c.session = :session AND c.chosenAt IS NOT NULL ORDER BY c.chosenAt ASC")
  List<UserChoice> findChoiceHistoryBySession(StorySession session);
}
