package com.dzung.search_engine.service;

import com.dzung.search_engine.models.QuoteDocument;
import com.dzung.search_engine.models.Suggestion;
import com.dzung.search_engine.models.WordDocument;
import com.dzung.search_engine.repository.mongo.QuoteAppMongoRepository;
import com.dzung.search_engine.repository.mongo.WordAppMongoRepository;
import com.dzung.search_engine.service.redis.RedisService;
import com.dzung.search_engine.trie.TrieNode;
import com.dzung.search_engine.trie.TrieQuoteSearch;
import com.dzung.search_engine.trie.TrieWordSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpellCheckerService {
    @Autowired
    private WordAppMongoRepository wordRepo;
    @Autowired
    private QuoteAppMongoRepository quoteRepo;
    @Autowired
    private RedisService redisService;
    @Autowired
    private TrieWordSearch trieWord;
    @Autowired
    private TrieQuoteSearch trieQuote;

    public List<String> wordSuggest(String word) {
        String[] words = word.split("\\s+");
        String keys = words[words.length - 1];

        for (int i = keys.length() - 1; i >= 0; i--) {
            String key = keys.substring(0, i + 1);
            List<String> ans = redisService.getCompletions(key);
            if (ans.size() > 0) {                                                     // If redis has the data already.
                return ans.stream()                                                   // Keep the sequence in the redis.
                        .filter(str -> str.split("\\s+").length == 1)           // Just filter word.
                        .limit(3)
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    public List<String> quoteSuggest(String word) {
        String[] keys = word.split("\\s+");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            builder.append(keys[i]).append(" ");
        }

        int len = builder.length();
        builder.delete(builder.length() - 1, len);
        for (int i = keys.length - 1; i >= 0; i--) {
            String key = builder.toString().trim();
            List<String> ans = redisService.getCompletions(key);
            if (ans.size() > 0) {
                return ans.stream()                                                  // Keep the sequence in the redis.
                        .filter(str -> str.split("\\s+").length > 1)            // Just filter quote.
                        .collect(Collectors.toList());
            }

            len = builder.length();
            int keyslength = keys[i].length();
            if (len > keyslength)
                builder.delete(len - keyslength - 1, len);
        }
        return new ArrayList<>();
    }

    public List<String> suggest(String word) {
        String keyWord = word.toLowerCase().trim();
        List<String> suggests = new ArrayList<>();

        suggests.addAll(wordSuggest(keyWord));
        suggests.addAll(quoteSuggest(keyWord));

        return suggests;
    }

    public void saveWordSuggestionsDB(String key, TrieNode curr) {
        if (curr.value.getSuggestions().size() > 0) {
            List<Suggestion> value = curr.value.getSuggestions().stream().collect(Collectors.toList());
            WordDocument doc = new WordDocument(key, value);
            wordRepo.save(doc);
        }

        for (String keyValue : curr.getChild().keySet()) {
            saveWordSuggestionsDB(key + keyValue, curr.child.get(keyValue));
        }
    }

    public void saveQuoteSuggestionsDB(String key, TrieNode curr) {
        if (curr.value.getSuggestions().size() > 0) {
            List<Suggestion> value = curr.value.getSuggestions().stream().collect(Collectors.toList());
            QuoteDocument doc = new QuoteDocument(key, value);
            quoteRepo.save(doc);
        }

        for (String keyValue : curr.getChild().keySet()) {
            saveQuoteSuggestionsDB((key + " " + keyValue).trim(), curr.child.get(keyValue));
        }
    }

    public boolean saveSuggestions(TrieWordSearch trieWord, TrieQuoteSearch trieQuote) {
        try {
            saveWordSuggestionsDB("", trieWord.getRoot());
            saveQuoteSuggestionsDB("", trieQuote.getRoot());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean saveDb() {
        return saveSuggestions(trieWord, trieQuote);
    }
}
