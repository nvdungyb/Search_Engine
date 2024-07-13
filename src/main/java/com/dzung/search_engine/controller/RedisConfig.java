package com.dzung.search_engine.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {
    @Value("${redis_host}")
    private String redisHost;
    @Value("${redis_port}")
    private int redisPort;

    @Bean
    public Jedis redisConnection() {
        return new Jedis(redisHost, redisPort);
    }
}
