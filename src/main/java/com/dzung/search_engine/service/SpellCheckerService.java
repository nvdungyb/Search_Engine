package com.dzung.search_engine.service;

import com.dzung.search_engine.document.QuoteDocument;
import com.dzung.search_engine.document.Suggestion;
import com.dzung.search_engine.document.WordDocument;
import com.dzung.search_engine.repository.QuoteRepository;
import com.dzung.search_engine.repository.WordRepository;
import com.dzung.search_engine.trie.TrieNode;
import com.dzung.search_engine.trie.TrieQuoteSearch;
import com.dzung.search_engine.trie.TrieWordSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpellCheckerService {
    @Autowired
    private WordRepository suggestRepo;
    @Autowired
    private WordRepository wordRepo;
    @Autowired
    private QuoteRepository quoteRepo;
    @Value("${max_completions}")
    private int maxCompletions;

    public List<String> wordSuggest(Jedis conn, String word) {
        String[] words = word.split("\\s+");
        String keys = words[words.length - 1];

        for (int i = keys.length() - 1; i >= 0; i--) {
            String key = keys.substring(0, i + 1);
            List<String> ans = conn.zrevrange(key, 0, -1);
            if (ans.size() > 0)
                return ans.stream()                                                   // Keep the sequence in the redis.
                        .filter(str -> str.split("\\s+").length == 1)           // Just filter word.
                        .limit(3)
                        .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<String> quoteSuggest(Jedis conn, String word) {
        String[] keys = word.split("\\s+");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            builder.append(keys[i]).append(" ");
        }

        int len = builder.length();
        builder.delete(builder.length() - 1, len);
        for (int i = keys.length - 1; i >= 0; i--) {
            String key = builder.toString().trim();
            List<String> ans = conn.zrevrange(key, 0, -1);
            if (ans.size() > 0)
                return ans.stream()                                                  // Keep the sequence in the redis.
                        .filter(str -> str.split("\\s+").length > 1)            // Just filter quote.
                        .collect(Collectors.toList());

            len = builder.length();
            int keyslength = keys[i].length();
            if (len > keyslength)
                builder.delete(len - keyslength - 1, len);
        }
        return new ArrayList<>();
    }

    public List<String> suggest(Jedis conn, String word) {
        String keyWord = word.toLowerCase().trim();
        List<String> suggests = new ArrayList<>();

        suggests.addAll(wordSuggest(conn, keyWord));
        suggests.addAll(quoteSuggest(conn, keyWord));

        return suggests;
    }

    public void updateWordScore(Jedis conn, String completion) {
        StringBuilder builder = new StringBuilder();
        String word = completion.toLowerCase();
        for (int i = 0; i < word.length(); i++) {
            builder.append(word.charAt(i));
            String key = builder.toString();

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
    }

    public void updateQuoteScore(Jedis conn, String completion) {
        String[] words = completion.toLowerCase().split(" ");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            builder.append(words[i]).append(" ");
            String key = builder.toString().trim();

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
                        conn.zadd(key, minScore + 1, completion);
                    }
                }
            } else {
                conn.zadd(key, 1, completion);
            }
        }
    }

    public void updateScore(Jedis conn, String message) {
        String completion = message.trim().replace("\\s+", " ");

        String[] words = completion.split(" ");
        if (words.length == 1)
            updateWordScore(conn, words[0]);
        else
            updateQuoteScore(conn, completion);
    }

    public void saveWordSuggestions(String key, TrieNode curr) {
        if (curr.value.getSuggestions().size() > 0) {
            List<Suggestion> value = curr.value.getSuggestions().stream().collect(Collectors.toList());
            WordDocument doc = new WordDocument(key, value);
            wordRepo.save(doc);
        }

        for (String keyValue : curr.getChild().keySet()) {
            saveWordSuggestions(key + keyValue, curr.child.get(keyValue));
        }
    }

    public void saveQuoteSuggestions(String key, TrieNode curr) {
        if (curr.value.getSuggestions().size() > 0) {
            List<Suggestion> value = curr.value.getSuggestions().stream().collect(Collectors.toList());
            QuoteDocument doc = new QuoteDocument(key, value);
            quoteRepo.save(doc);
        }

        for (String keyValue : curr.getChild().keySet()) {
            saveQuoteSuggestions((key + " " + keyValue).trim(), curr.child.get(keyValue));
        }
    }

    public boolean saveSuggestions(TrieWordSearch trieWord, TrieQuoteSearch trieQuote) {
        try {
            saveWordSuggestions("", trieWord.getRoot());
            saveQuoteSuggestions("", trieQuote.getRoot());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean saveDb() {
//        return saveSuggestions(trieWord, trieQuote);
        return true;
    }
}
