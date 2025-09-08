package com.storyai.storytelling_backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.repository.UserRepository;

@Service
@Transactional
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getOrCreateDefaultUser() {
    Optional<User> existingUser = userRepository.findByUsername("defaultUser");

    if (existingUser.isPresent()) {
      return existingUser.get();
    }

    // Create default user for testing
    User defaultUser = new User("defaultUser", "default@test.com", "temp_hash", true);

    return userRepository.save(defaultUser);
  }

  @Transactional(readOnly = true)
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }
}
