package com.example.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisRepositoryConfig {
  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;
}
