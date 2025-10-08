package com.storyai.storytelling_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "user_choices")
public class UserChoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // CHANGED: Made optional for the new choice system (can be null before choice is made)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id")
  private StorySession session;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chapter_id", nullable = false)
  private StoryChapter chapter;

  // RENAMED: From choicesText to choiceText (proper naming)
  @Column(name = "choice_text", columnDefinition = "TEXT", nullable = false)
  private String choiceText;

  // RENAMED: From choiceOrder to optionNumber (matches new system)
  @Column(name = "option_number", nullable = false)
  private Integer optionNumber = 1;

  // ADDED: Next chapter number for branching (null = story ends)
  @Column(name = "next_chapter_number")
  private Integer nextChapterNumber;

  // ADDED: Optional consequence description
  @Column(name = "consequence", columnDefinition = "TEXT")
  private String consequence;

  // RENAMED: From timestamp to createdAt (consistency)
  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // ADDED: When this choice was actually chosen by user (null = not chosen yet)
  @Column(name = "chosen_at")
  private LocalDateTime chosenAt;

  // Constructors
  public UserChoice() {}

  // Legacy constructor - keep for backwards compatibility
  public UserChoice(StorySession session, StoryChapter chapter, String choiceText) {
    this.session = session;
    this.chapter = chapter;
    this.choiceText = choiceText;
  }

  // New constructor without session (for pre-defined choices)
  public UserChoice(StoryChapter chapter, String choiceText, Integer optionNumber, Integer nextChapterNumber) {
    this.chapter = chapter;
    this.choiceText = choiceText;
    this.optionNumber = optionNumber;
    this.nextChapterNumber = nextChapterNumber;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public StorySession getSession() {
    return session;
  }

  public void setSession(StorySession session) {
    this.session = session;
  }

  public StoryChapter getChapter() {
    return chapter;
  }

  public void setChapter(StoryChapter chapter) {
    this.chapter = chapter;
  }

  public String getChoiceText() {
    return choiceText;
  }

  public void setChoiceText(String choiceText) {
    this.choiceText = choiceText;
  }

  public Integer getOptionNumber() {
    return optionNumber;
  }

  public void setOptionNumber(Integer optionNumber) {
    this.optionNumber = optionNumber;
  }

  // NEW GETTER/SETTER
  public Integer getNextChapterNumber() {
    return nextChapterNumber;
  }

  public void setNextChapterNumber(Integer nextChapterNumber) {
    this.nextChapterNumber = nextChapterNumber;
  }

  // NEW GETTER/SETTER
  public String getConsequence() {
    return consequence;
  }

  public void setConsequence(String consequence) {
    this.consequence = consequence;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  // NEW GETTER/SETTER
  public LocalDateTime getChosenAt() {
    return chosenAt;
  }

  public void setChosenAt(LocalDateTime chosenAt) {
    this.chosenAt = chosenAt;
  }
}
