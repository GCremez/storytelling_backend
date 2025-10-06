package com.storyai.storytelling_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.storyai.storytelling_backend.DTO.CreateChapterRequest;
import com.storyai.storytelling_backend.DTO.UpdateChapterRequest;
import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.StoryChapter;
import com.storyai.storytelling_backend.exception.NotFoundException;
import com.storyai.storytelling_backend.repository.StoryChapterRepository;
import com.storyai.storytelling_backend.repository.StoryRepository;

@Service
@Transactional
public class ChapterService {

  private final StoryRepository storyRepository;
  private final StoryChapterRepository chapterRepository;

  public ChapterService(StoryRepository storyRepository, StoryChapterRepository chapterRepository) {
    this.storyRepository = storyRepository;
    this.chapterRepository = chapterRepository;
  }

  public StoryChapter createChapter(Long storyId, CreateChapterRequest request) {
    Story story =
        storyRepository
            .findById(storyId)
            .orElseThrow(() -> new NotFoundException("Story not found with id: " + storyId));

    // Check for duplicate chapter number
    chapterRepository
        .findByStoryIdAndChapterNumber(storyId, request.getChapterNumber())
        .ifPresent(
            c -> {
              throw new IllegalStateException(
                  "Chapter number "
                      + request.getChapterNumber()
                      + " already exists for this story");
            });

    StoryChapter chapter =
        new StoryChapter(
            story, request.getChapterNumber(), request.getTitle(), request.getContent());
    return chapterRepository.save(chapter);
  }

  @Transactional(readOnly = true)
  public List<StoryChapter> listChapters(Long storyId) {
    if (!storyRepository.existsById(storyId)) {
      throw new NotFoundException("Story not found with id: " + storyId);
    }
    return chapterRepository.findByStoryIdOrderByChapterNumber(storyId);
  }

  @Transactional(readOnly = true)
  public Optional<StoryChapter> getChapter(Long id) {
    return chapterRepository.findById(id);
  }

  public StoryChapter updateChapter(Long id, UpdateChapterRequest request) {
    StoryChapter chapter =
        chapterRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Chapter not found with id: " + id));

    // If chapter number is being updated, check for conflicts
    if (request.getChapterNumber() != null
        && !request.getChapterNumber().equals(chapter.getChapterNumber())) {
      chapterRepository
          .findByStoryIdAndChapterNumber(chapter.getStory().getId(), request.getChapterNumber())
          .ifPresent(
              c -> {
                throw new IllegalStateException(
                    "Chapter number "
                        + request.getChapterNumber()
                        + " already exists for this story");
              });
      chapter.setChapterNumber(request.getChapterNumber());
    }

    // Update title if provided
    if (request.getTitle() != null) {
      chapter.setTitle(request.getTitle());
    }

    // Update content if provided
    if (request.getContent() != null) {
      chapter.setContent(request.getContent());
    }

    return chapterRepository.save(chapter);
  }

  public void deleteChapter(Long id) {
    if (!chapterRepository.existsById(id)) {
      throw new NotFoundException("Chapter not found with id: " + id);
    }
    chapterRepository.deleteById(id);
  }
}
