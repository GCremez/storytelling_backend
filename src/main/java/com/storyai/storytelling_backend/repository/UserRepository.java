package com.storyai.storytelling_backend.repository;

import com.storyai.storytelling_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Find user by username
   */
  Optional<User> findByUsername(String username);

  /**
   * Find user by email
   */
  Optional<User> findByEmail(String email);

  /**
   * Find user by username or email
   */
  Optional<User> findByUsernameOrEmail(String username, String email);

  /**
   * Check if username exists
   */
  boolean existsByUsername(String username);

  /**
   * Check if email exists
   */
  boolean existsByEmail(String email);
}
