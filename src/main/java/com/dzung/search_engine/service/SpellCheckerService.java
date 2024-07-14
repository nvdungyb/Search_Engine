package com.dzung.search_engine.service;

import com.dzung.search_engine.document.Document;
import com.dzung.search_engine.document.QuoteDocument;
import com.dzung.search_engine.document.Suggestion;
import com.dzung.search_engine.document.WordDocument;
import com.dzung.search_engine.repository.QuoteRepository;
import com.dzung.search_engine.repository.WordRepository;
import com.dzung.search_engine.trie.TrieNode;
import com.dzung.search_engine.trie.TrieQuoteSearch;
import com.dzung.search_engine.trie.TrieWordSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

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

    public List<String> wordSuggest(Jedis conn, String word) {
        String[] words = word.split("\\s+");
        String keys = words[words.length - 1];

        for (int i = keys.length() - 1; i >= 0; i--) {
            String key = keys.substring(0, i + 1);
            List<String> ans = conn.zrange(key, 0, -1);
            if (ans.size() > 0)
                return ans.parallelStream()
                        .filter(str -> str.split("\\s+").length == 1)           // Just filter word.
                        .collect(Collectors.toList());
        }
        return null;
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
            List<String> ans = conn.zrange(key, 0, -1);
            if (ans.size() > 0)
                return ans.parallelStream()
                        .filter(str -> str.split("\\s+").length > 1)            // Just filter quote.
                        .collect(Collectors.toList());

            len = builder.length();
            int keyslength = keys[i].length();
            if (len > keyslength)
                builder.delete(len - keyslength - 1, len);
        }
        return null;
    }

    public List<String> suggest(Jedis conn, String word) {
        String keyWord = word.toLowerCase().trim();
        List<String> suggests = new ArrayList<>();

        List<String> wordSuggestions = wordSuggest(conn, word);
        if (wordSuggestions != null)
            suggests.addAll(wordSuggest(conn, word));
        List<String> quoteSuggestions = quoteSuggest(conn, word);
        if (quoteSuggestions != null)
            suggests.addAll(quoteSuggest(conn, word));

        return suggests;
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
