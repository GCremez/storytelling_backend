package com.storyai.storytelling_backend.service;

import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Temporary method for development - creates a default user if none exists
    public User getOrCreateDefaultUser() {
        Optional<User> existingUser = userRepository.findByUsername("defaultUser");

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // Create default user for testing
        User defaultUser = new User();
        defaultUser.setUsername("defaultUser");
        defaultUser.setEmail("default@test.com");
        defaultUser.setPasswordHash("temp_hash");
        defaultUser.setIsActive(true);

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