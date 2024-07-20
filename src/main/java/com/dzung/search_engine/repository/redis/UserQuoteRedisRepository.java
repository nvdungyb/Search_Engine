package com.dzung.search_engine.repository.redis;

import com.dzung.search_engine.entity.redis.QuoteDocHash;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

@EnableRedisRepositories(basePackages = "com.dzung.search_engine.repository.redis")
public interface UserQuoteRedisRepository extends CrudRepository<QuoteDocHash, String> {
}
