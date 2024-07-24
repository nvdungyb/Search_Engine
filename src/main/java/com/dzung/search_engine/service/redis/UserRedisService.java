package com.dzung.search_engine.service.redis;

import com.dzung.search_engine.models.QuoteDocument;
import com.dzung.search_engine.models.Suggestion;
import com.dzung.search_engine.entity.redis.QuoteRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
public class UserRedisService {
    @Value("${time_to_live}")
    private long timeToLive;
    @Autowired
    private RedisTemplate<String, Object> template;

    public Optional<QuoteRedis> findByKey(String key) {
        QuoteRedis docHash = new QuoteRedis();
        QuoteDocument quoteDoc = new QuoteDocument();
        docHash.setKey(key);

        String prefix = key.split(":")[1];
        quoteDoc.setPrefix(prefix);

        try {
            Set<ZSetOperations.TypedTuple<Object>> ans = template.opsForZSet().reverseRangeWithScores(key, 0, -1);
            if (ans != null && ans.size() != 0) {
                for (ZSetOperations.TypedTuple<Object> tuple : ans) {
                    String completion = (String) tuple.getValue();
                    double score = tuple.getScore();
                    quoteDoc.getValue().add(new Suggestion(completion, (int) score));
                }
            } else {
                return Optional.empty();
            }

            docHash.setQuoteDocument(quoteDoc);
            return Optional.of(docHash);
        } catch (Exception e) {                                 // Some keys contain other object type.
            template.delete(key);
            return Optional.empty();
        }
    }

    public void saveToRedis(QuoteRedis docHash) {
        String key = docHash.getKey();

        if (template.hasKey(key)) {
            if (template.type(key).equals("zset")) {
                template.delete(key);
            }
        }

        for (Suggestion suggestion : docHash.getQuoteDocument().getValue()) {
            template.opsForZSet().add(key, suggestion.getCompletion(), suggestion.getScore());
        }

        Duration ttl = Duration.ofSeconds(timeToLive);
        Boolean success = template.expire(key, ttl);
        if (success == null || !success)
            System.out.println("Failed to set ttl for key: " + key);
    }
}
