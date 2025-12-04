package com.storyai.storytelling_backend.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class jwtService {

  private static final Logger logger = LoggerFactory.getLogger(jwtService.class);

  @Value("${jwt.secret}")
  private String secreat;

  @Value("${jwt.expiration:900000}")
  private Long expiration;

  @Value("${jwt.refresh.expiration:604800000}") // 7 days default
  private Long refreshExpiration;
}
