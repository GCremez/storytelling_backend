package com.storyai.storytelling_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.storyai.storytelling_backend.entity.Story;
import com.storyai.storytelling_backend.entity.User;

public interface StoryRepository extends JpaRepository<Story, Long> {
  List<Story> findByIsPublicTrueOrderByCreatedAtDesc();

  List<Story> findByGenreAndIsPublicTrueOrderByCreatedAtDesc(String genre);

  List<Story> findByCreatedByOrderByCreatedAtDesc(User createdBy);

  @Query(
      "SELECT s FROM Story s WHERE s.isPublic = true AND"
          + "(LOWER(s.title) LIKE LOWER(CONCAT('%', :search, '%')) OR "
          + "LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')))")
  List<Story> searchPublicStories(@Param("search") String search);
}
