package com.dzung.search_engine.service.redis;

import com.dzung.search_engine.entity.redis.QuoteRedis;
import com.dzung.search_engine.entity.redis.WordRedis;
import com.dzung.search_engine.models.Suggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> template;

    @Value("${max_completions}")
    private int maxCompletions;

    @Value("${public_time_to_live}")
    private int publicTimeToLive;

    public Optional<List<String>> findByKey(String prefix) {
        try {
            Set<ZSetOperations.TypedTuple<Object>> tuples = template.opsForZSet().reverseRangeWithScores(prefix, 0, -1);

            List<String> res = new ArrayList<>();
            for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
                res.add((String) tuple.getValue());
            }

            if (res.size() > 0)
                return Optional.of(res);
        } catch (Exception e) {
            template.delete(prefix);
        }
        return Optional.empty();
    }

    public void updateByKey(String key, String completion) {
        if (template.hasKey(key) && !template.type(key).equals(DataType.ZSET))
            template.delete(key);

        if (template.opsForZSet().zCard(key) >= maxCompletions / 2) {
            if (template.opsForZSet().score(key, completion) != null) {
                template.opsForZSet().incrementScore(key, completion, 1);
            } else {
                Set<ZSetOperations.TypedTuple<Object>> tuples = template.opsForZSet().rangeWithScores(key, 0, 0);
                for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
                    String minCompletion = (String) tuple.getValue();
                    double minScore = tuple.getScore();
                    template.opsForZSet().remove(key, minCompletion);
                    template.opsForZSet().add(key, completion, minScore + 1);           // More information at notes: Guideline
                }
            }
        } else {
            template.opsForZSet().incrementScore(key, completion, 1);
        }
    }

    public void updateWordScore(String completion) {
        StringBuilder builder = new StringBuilder();
        String word = completion.toLowerCase();
        for (int i = 0; i < word.length(); i++) {
            builder.append(word.charAt(i));
            String key = builder.toString();

            this.updateByKey("word:" + key, completion);
        }
    }

    public void updateQuoteScore(String completion) {
        String[] words = completion.toLowerCase().split(" ");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            builder.append(words[i]).append(" ");
            String key = builder.toString().trim();

            this.updateByKey("quote:" + key, completion);
        }
    }

    public void updateScore(String message) {
        String completion = message.trim().replace("\\s+", " ");

        String[] words = completion.split(" ");
        if (words.length == 1)
            this.updateWordScore(words[0]);
        else
            this.updateQuoteScore(completion);
    }

    public void saveToRedis(QuoteRedis quoteHash) {
        String key = "quote:" + quoteHash.getKey();
        int prefixLen = quoteHash.getKey().length();
        for (Suggestion suggestion : quoteHash.getQuoteDocument().getValue()) {
            String completion = suggestion.getCompletion();
            double score = suggestion.getScore();
            template.opsForZSet().add(key, completion, score);

            while (prefixLen < completion.length()) {
                String extraKey = "quote:" + completion.substring(0, prefixLen + 1).toLowerCase();
                template.opsForZSet().add(extraKey, completion, score);
                expireKey(extraKey);
                prefixLen += 1;
            }
        }
        expireKey(key);
    }

    public void saveToRedis(WordRedis wordHash) {
        String key = "word:" + wordHash.getKey();
        int prefixLen = wordHash.getKey().length();
        for (Suggestion suggestion : wordHash.getWordDocument().getValue()) {
            template.opsForZSet().add(key, suggestion.getCompletion(), suggestion.getScore());

            String completion = suggestion.getCompletion();
            while (prefixLen < completion.length()) {
                String extraKey = "word:" + completion.substring(0, prefixLen + 1).toLowerCase();
                template.opsForZSet().add(extraKey, completion, suggestion.getScore());
                expireKey(extraKey);
                prefixLen += 1;
            }
        }
        expireKey(key);
    }

    public void expireKey(String key) {
        if (!template.expire(key, Duration.ofHours(publicTimeToLive))) {
            System.out.println("Can not set time to live for : " + key);
        }
    }
}
