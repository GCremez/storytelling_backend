package com.storyai.storytelling_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.storyai.storytelling_backend.DTO.CreateChapterRequest;
import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.StoryChapter;
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
            .orElseThrow(() -> new RuntimeException("Story not found"));

    // Optional: check for duplicate chapter number
    chapterRepository
        .findByStoryIdAndChapterNumber(storyId, request.getChapterNumber())
        .ifPresent(
            c -> {
              throw new RuntimeException("Chapter number already exists for this story");
            });

    StoryChapter chapter =
        new StoryChapter(story, request.getChapterNumber(), request.getTitle(), request.getContent());
    return chapterRepository.save(chapter);
  }

  @Transactional(readOnly = true)
  public List<StoryChapter> listChapters(Long storyId) {
    return chapterRepository.findByStoryIdOrderByChapterNumber(storyId);
  }

  @Transactional(readOnly = true)
  public Optional<StoryChapter> getChapter(Long id) {
    return chapterRepository.findById(id);
  }
}
