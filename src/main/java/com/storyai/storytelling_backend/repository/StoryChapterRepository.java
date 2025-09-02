package com.storyai.storytelling_backend.repository;

import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.StoryChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface StoryChapterRepository extends JpaRepository<StoryChapter, Long> {
    List<StoryChapter> findByStoryOrderByChapterNumber(Story story);

    Optional<StoryChapter> findByStoryAndChapterNumber(Story story, Integer chapterNumber);

    List<StoryChapter> findByStoryIdOrderByChapterNumber(Long storyId);

    Optional<StoryChapter> findByStoryIdAndChapterNumber(Long storyId, Integer chapterNumber);
}
