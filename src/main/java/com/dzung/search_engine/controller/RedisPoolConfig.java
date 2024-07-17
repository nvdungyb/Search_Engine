package com.dzung.search_engine.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisPoolConfig {
    @Value("${redis_host}")
    private String redisHost;
    @Value("${redis_port}")
    private int redisPort;
    @Value("${redis_max_total}")
    private int maxTotal;
    @Value("${redis_max_idle}")
    private int maxIdle;
    @Value("${redis_min_idle}")
    private int minIdle;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // Set the maximum total connection in the pool (default 8).
        poolConfig.setMaxTotal(maxTotal);

        // Set the maximum total connection in the pool that can be idle,
        // if number of idle connections are greater than this value the pool will close and delete that connection.
        // default(8)
        poolConfig.setMaxIdle(maxIdle);

        // Set the minimum total connection in the pool that can be idle that the pool tries to keep.
        // If number of free connections is less than this value, the pool will create a new connection.
        // default(0)
        poolConfig.setMinIdle(minIdle);

        // Make sure the connection, that the client gets is working well.
        // default(false)
        poolConfig.setTestOnBorrow(true);

        // Only the valid connection can be in the pool.
        // default(false)
        poolConfig.setTestOnReturn(true);

        // Check whether the idle connection is valid or not.
        // default(false)
        poolConfig.setTestWhileIdle(true);

        poolConfig.setJmxEnabled(false);

        return new JedisPool(poolConfig, redisHost, redisPort);
    }
}
