package com.storyai.storytelling_backend.service;

import com.storyai.storytelling_backend.entity.User;
import com.storyai.storytelling_backend.repository.UserRepository;
import com.storyai.storytelling_backend.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    return new CustomUserDetails(user);
  }

  /**
   * Load User by email ( For login with email)
   */
  @Transactional(readOnly = true)
  public UserDetails loadUserByEmail(String email)
    throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

    return new CustomUserDetails(user);
  }

  /**
   * Load user by username or email
   * @param usernameOrEmail
   * @return
   * @throws UsernameNotFoundException
   */
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsernameOrEmail(String usernameOrEmail)
    throws UsernameNotFoundException {
    User user = userRepository.findByUsername(usernameOrEmail)
      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

    return new CustomUserDetails(user);
  }
}
