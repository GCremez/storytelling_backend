package com.storyai.storytelling_backend.service;

import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.repository.StoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StoryService {

    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public Story createStory(String title, String description, String genre, User creator) {
        Story story = new Story(title, description, genre, creator);
        return storyRepository.save(story);
    }

    @Transactional(readOnly = true)
    public List<Story> getPublicStories() {
        return storyRepository.findByIsPublicTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Story> getStoriesByGenre(String genre) {
        return storyRepository.findByGenreAndIsPublicTrueOrderByCreatedAtDesc(genre);
    }

    @Transactional(readOnly = true)
    public Optional<Story> getStoryById(Long id) {
        return storyRepository.findById(id);
    }

    public Story PublishStory(Long storyId, User owner) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Story not found"));

        if (!story.getCreatedBy().getId().equals(owner.getId())) {
            throw new RuntimeException("Not authorized to publish this story");
        }

        story.setIsPublic(true);
        return storyRepository.save(story);
    }
}
