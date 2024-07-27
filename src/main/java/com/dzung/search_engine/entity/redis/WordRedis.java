package com.dzung.search_engine.entity.redis;

import com.dzung.search_engine.models.WordDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "user")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WordRedis {
    @Id
    private String key;                                 // Key = User identifier + prefix.
    private WordDocument wordDocument;
}
