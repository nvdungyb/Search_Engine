package com.dzung.search_engine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {
    @Autowired
    private JedisPool jedisPool;

    public Jedis getConnection() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
