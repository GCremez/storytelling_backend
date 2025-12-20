package com.storyai.storytelling_backend.security;

import com.storyai.storytelling_backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

//Custom UserDetails implementation for Spring Security
public class CustomUserDetails implements UserDetails {
  private final User user;

  public CustomUserDetails(User user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // All users have ROLE_USER
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {
    return user.getPasswordHash();
  }

  @Override
  public  String getUsername() {
    return user.getUsername();
  }
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return user.isActive();
  }

  // Additional methods to access User entity
  public User getUser() {
    return user;
  }

  public Long getUserId() {
    return user.getId();
  }

  public String getEmail() {
    return user.getEmail();
  }

  public Boolean isVerified() {
    return user.getIsVerified();
  }
}
