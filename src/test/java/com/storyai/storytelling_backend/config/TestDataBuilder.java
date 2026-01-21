package com.storyai.storytelling_backend.config;

import com.storyai.storytelling_backend.DTO.LoginRequest;
import com.storyai.storytelling_backend.DTO.RegisterRequest;
import com.storyai.storytelling_backend.entity.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import com.storyai.storytelling_backend.DTO.LoginRequest;

public class TestDataBuilder {
  private static final PasswordEncoder password = new BCryptPasswordEncoder();
  private static int userCounter = 0;
  private static int storyCounter = 0;
  private static int chapterCounter = 1;

  public record UserWithPlainPassword(User user, String plainPassword) {}
  /**
   * Create a basic user with default values
   * perfect for simple user tests
   */
  public static User createUser() {
    userCounter++;
    User user = new User();
    user.setUsername("testuser" + userCounter);
    user.setEmail("testuser" + userCounter + "@example.com");
    user.setPasswordHash(password.encode("TestPass123"));
    user.setEmailVerified(true);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    return user;
  }

  /**
   * Create an unverified user
   * perfect for testing email verification flow
   */
  public static User createUnverifiedUser(){
    User user = createUser();
    user.setEmailVerified(false);
    return user;
  }

  /**
   * Create a user with a plain password (before encoding)
   * perfect for testing authentication
   */
  public static UserWithPlainPassword createUserWithPlainPassword() {
    String plainPassword = "TestPass123!";
    User user = createUser();
    user.setPasswordHash(password.encode(plainPassword));
    return new UserWithPlainPassword(user, plainPassword);
  }

  /**
   * Create a valid registration request
   * perfect for testing successful registration
   */
  public static RegisterRequest createValidRegisterRequest() {
    userCounter++;
    return new RegisterRequest(
      "testuser" + userCounter,
      "testuser" + userCounter + "@example.com",
      "TestPass123!",
      "TestPass123!"
    );
  }

  /**
   * Create a registration request with a weak password
   * perfect for testing password validation
   */
  public static RegisterRequest createWeakPasswordRequest(){
    userCounter++;
    return new RegisterRequest(
      "testuser" + userCounter,
      "testuser" + userCounter + "@example.com",
      "weak",
      "weak"
    );
  }

  /**
   * Create a registration request with a mismatched password
   * perfect for testing password confirmation
   */
  public static RegisterRequest createMismatchedPasswordRequest(){
    userCounter++;
    return new RegisterRequest(
      "testuser" + userCounter,
      "testuser" + userCounter + "@example.com",
      "TestPass123!",
      "DifferentPass123"
    );
  }

  /**
   * Create a valid login request
   * perfect for testing successful login
   */
  public static LoginRequest createValidLoginRequest() {
    UserWithPlainPassword userWithPassword = createUserWithPlainPassword();
    return new LoginRequest(
      userWithPassword.user().getUsername(),
      userWithPassword.plainPassword()
    );
  }

  /**
   * Create login request with wrong password
   * perfect for testing failed authentication
   */
  public static LoginRequest createInvalidLoginRequest() {
    UserWithPlainPassword userWithPassword = createUserWithPlainPassword();
    return new LoginRequest(
      userWithPassword.user().getUsername(),
      "wrongPassword123!"  // Intentionally wrong password
    );
  }

  /**
   * Create a basic story
   * perfect for Story CRUD Tests
   */

  public static Story createStory(User author) {
    storyCounter++;
    Story story = new Story();
    story.setTitle("Test Story " + storyCounter);
    story.setDescription("A test story description");
    story.setGenre("Fantasy");
    story.setCreatedBy(author);
    story.setIsPublic(false);
    story.setCreatedAt(LocalDateTime.now());
    story.setUpdatedAt(LocalDateTime.now());
    return story;
  }

  /**
   * Create a published story
   * perfect for testing published story queries
   */
  public static Story createIsPublicStory(){
    Story story = createStory(createUser());
    story.setIsPublic(true);
    return story;
  }

  /**
   * Create a basic story chapter
   * @param story The story this chapter belongs to
   * @return A new StoryChapter instance
   */
  public static StoryChapter createChapter(Story story) {
    StoryChapter chapter = new StoryChapter();
    chapter.setStory(story);
    chapter.setChapterNumber(chapterCounter++);
    chapter.setTitle("Chapter " + chapter.getChapterNumber());
    chapter.setContent("This is the content for chapter " + chapter.getChapterNumber() + ". It contains some sample text for testing purposes.");
    chapter.setAiGenerated(false);
    chapter.setCreatedAt(LocalDateTime.now());
    return chapter;
  }

  /**
   * Create a story with a single chapter
   * perfect for testing story-chapter relationships
   * @return A story with one chapter
   */
  public static Story createStoryWithChapter() {
    User author = createUser();
    Story story = createStory(author);
    StoryChapter chapter = createChapter(story);

    // If you have a chapters collection in Story, you can add the chapter
    // story.getChapters().add(chapter);

    return story;
  }

  // 1. Session-Related Test Data
  /**
   * Creates a new story session for testing
   * Perfect for testing session management and story progression
   */
  public static StorySession createSession(User user, Story story) {
    StorySession session = new StorySession();
    session.setUser(user);
    session.setStory(story);
    session.setCurrentChapter(1);
    session.setSessionData("{}");
    session.setStartedAt(LocalDateTime.now());
    session.setLastActivityAt(LocalDateTime.now());
    session.setIsCompleted(false);
    return session;
  }

  /**
   * Creates a story with multiple chapters
   * Perfect for testing story with complete structure
   * @param author The author of the story
   * @param chapterCount Number of chapters to create
   * @return Story with the specified number of chapters
   */
  public static Story createStoryWithChapters(User author, int chapterCount) {
    if (chapterCount < 1) {
      throw new IllegalArgumentException("Chapter count must be at least 1");
    }

    Story story = createStory(author);
    story.setIsPublic(true);

    // Create chapters
    for (int i = 0; i < chapterCount; i++) {
      StoryChapter chapter = createChapter(story);
      chapter.setChapterNumber(i + 1);
      chapter.setTitle("Chapter " + (i + 1));
      // If you have a chapters collection in Story, you can add the chapter
      // story.getChapters().add(chapter);
    }

    return story;
  }

  /**
   * Creates a chapter with predefined choices
   * Perfect for interactive story tests
   * @param story The story this chapter belongs to
   * @param choiceCount Number of choices to create (1-3)
   * @return Chapter with the specified number of choices
   */
  public static StoryChapter createChapterWithChoices(Story story, int choiceCount) {
    StoryChapter chapter = createChapter(story);
    chapter.setContent("This chapter has " + choiceCount + " possible choices.");

    // Create choices (1-3 choices per chapter)
    int numChoices = Math.min(Math.max(1, choiceCount), 3);
    for (int i = 0; i < numChoices; i++) {
      UserChoice choice = new UserChoice();
      choice.setChapter(chapter);
      choice.setChoiceText("Choice " + (i + 1));
      choice.setOptionNumber(i + 1);
      choice.setNextChapterNumber(chapter.getChapterNumber() + 1 + i); // Branch to different chapters
      // If you have a choices collection in StoryChapter, you can add the choice
      // chapter.getChoices().add(choice);
    }

    return chapter;
  }

  /**
   * Resets all counters
   * Call this in @BeforeEach if needed to ensure clean test state
   */
  public static void resetCounters() {
    userCounter = 0;
    storyCounter = 0;
    chapterCounter = 1;
  }

  /**
   * Creates an active session (in-progress)
   * Perfect for testing session continuation
   */
  public static StorySession createActiveSession(User user, Story story) {
    StorySession session = createSession(user, story);
    session.setLastActivityAt(LocalDateTime.now().minusMinutes(5));
    return session;
  }

  /**
   * Creates a completed session
   * Perfect for testing completed story flows
   */
  public static StorySession createCompletedSession(User user, Story story) {
    StorySession session = createSession(user, story);
    session.setIsCompleted(true);
    session.setCompletedAt(LocalDateTime.now());
    session.setLastActivityAt(LocalDateTime.now());
    return session;
  }

// 2. UserChoice Test Data
  /**
   * Creates a user choice for a chapter
   * Perfect for testing choice-based story progression
   */
  public static UserChoice createUserChoice(StorySession session, StoryChapter chapter, String choiceText) {
    UserChoice choice = new UserChoice();
    choice.setSession(session);
    choice.setChapter(chapter);
    choice.setChoiceText(choiceText);
    choice.setOptionNumber(1);
    choice.setCreatedAt(LocalDateTime.now());
    return choice;
  }

  /**
   * Creates a user choice that leads to a specific chapter
   * Perfect for testing branching story paths
   */
  public static UserChoice createUserChoiceWithNextChapter(StorySession session, StoryChapter chapter,
                                                           String choiceText, Integer nextChapterNumber) {
    UserChoice choice = createUserChoice(session, chapter, choiceText);
    choice.setNextChapterNumber(nextChapterNumber);
    return choice;
  }

// 3. Email Verification Test Data
  /**
   * Creates a valid email verification
   * Perfect for testing email verification flows
   */
  public static EmailVerification createEmailVerification(User user) {
    String code = String.format("%06d", (int)(Math.random() * 1000000));
    EmailVerification verification = new EmailVerification(user, code);
    verification.setExpiresAt(LocalDateTime.now().plusMinutes(15));
    return verification;
  }

  /**
   * Creates an expired email verification
   * Perfect for testing verification expiration
   */
  public static EmailVerification createExpiredEmailVerification(User user) {
    EmailVerification verification = createEmailVerification(user);
    verification.setExpiresAt(LocalDateTime.now().minusMinutes(1));
    return verification;
  }

// 4. Refresh Token Test Data
  /**
   * Creates a valid refresh token
   * Perfect for testing authentication flows
   */
  public static RefreshToken createRefreshToken(User user) {
    RefreshToken token = new RefreshToken(user);
    token.setExpiresAt(LocalDateTime.now().plusDays(7));
    return token;
  }

  /**
   * Creates an expired refresh token
   * Perfect for testing token expiration
   */
  public static RefreshToken createExpiredRefreshToken(User user) {
    RefreshToken token = createRefreshToken(user);
    token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
    return token;
  }
}
