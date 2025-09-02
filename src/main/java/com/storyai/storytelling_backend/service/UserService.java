package com.storyai.storytelling_backend.service;

import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getOrCreateDefaultUser() {
        return userRepository.findByUsername("defaultUser")
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .username("defaultUser")
                                .email("default@test.com")
                                .passwordHash("temp_hash")
                                .build()
                ));
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