package com.dzung.search_engine.service;

import com.dzung.search_engine.document.Suggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisService {
    @Autowired
    private JedisPool jedisPool;

    @Value("${max_completions}")
    private int maxCompletions;

    @Value("${time_to_live}")
    private int ttl;

    public List<String> getCompletions(String key) {
        try (Jedis conn = jedisPool.getResource()) {
            List<String> docs = conn.zrevrange(key, 0, -1);
            if (docs != null)
                return docs;
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    public void addToRedis(String key, List<Suggestion> suggestions) {
        try (Jedis conn = jedisPool.getResource()) {
            suggestions.forEach(val -> {
                conn.zadd(key, val.getScore(), val.getCompletion());
                conn.expire(key, ttl);
            });
        } catch (Exception e) {
        }
    }

    public void updateByKey(Jedis conn, String key, String completion) {
        if (conn.exists(key) && !conn.type(key).equals("zset"))
            conn.del(key);

        if (conn.zcard(key) >= maxCompletions / 2) {
            if (conn.zscore(key, completion) != null) {
                conn.zincrby(key, 1, completion);
            } else {
                List<Tuple> tuples = conn.zrangeWithScores(key, 0, 0);
                for (Tuple tuple : tuples) {
                    String minCompletion = tuple.getElement();
                    int minScore = (int) tuple.getScore();
                    conn.zrem(key, minCompletion);
                    conn.zincrby(key, minScore + 1, completion);           // More information at notes: Guideline
                }
            }
        } else {
            conn.zincrby(key, 1, completion);
        }
    }

    public void updateWordScore(Jedis conn, String completion) {
        StringBuilder builder = new StringBuilder();
        String word = completion.toLowerCase();
        for (int i = 0; i < word.length(); i++) {
            builder.append(word.charAt(i));
            String key = builder.toString();

            this.updateByKey(conn, key, completion);
        }
    }

    public void updateQuoteScore(Jedis conn, String completion) {
        String[] words = completion.toLowerCase().split(" ");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            builder.append(words[i]).append(" ");
            String key = builder.toString().trim();

            this.updateByKey(conn, key, completion);
        }
    }

    public void updateScore(String message) {
        try (Jedis conn = jedisPool.getResource()) {
            String completion = message.trim().replace("\\s+", " ");

            String[] words = completion.split(" ");
            if (words.length == 1)
                this.updateWordScore(conn, words[0]);
            else
                this.updateQuoteScore(conn, completion);
        } catch (Exception e) {
        }
    }
}
