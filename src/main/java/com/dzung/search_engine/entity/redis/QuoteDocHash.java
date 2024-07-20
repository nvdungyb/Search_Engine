package com.dzung.search_engine.entity.redis;

import com.dzung.search_engine.document.QuoteDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash(value = "user", timeToLive = 60)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuoteDocHash {
    @Id
    private String key;                                 // Key = User identifier + prefix.
    private QuoteDocument quoteDocument;
}
