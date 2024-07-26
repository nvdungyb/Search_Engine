package com.dzung.search_engine.service;

import com.dzung.search_engine.configuration.FilePath;
import com.dzung.search_engine.entity.mongo.QuoteMongo;
import com.dzung.search_engine.entity.mongo.UserDetailsImpl;
import com.dzung.search_engine.entity.redis.QuoteRedis;
import com.dzung.search_engine.entity.redis.WordRedis;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private WordAppMongoRepository wordRepo;
    @Autowired
    private QuoteAppMongoRepository quoteRepo;
    @Autowired
    private RedisService redisService;
    @Autowired
    private FilePath filePath;

    public List<String> quoteSuggestions(String prefix) {
        Optional<List<String>> optionalRedis = redisService.findByKey("quote:" + prefix);
        if (optionalRedis.isPresent()) {
            System.out.println("Retrieve data in Redis");
            return optionalRedis.get();
        } else {
            System.out.println("Retrieve data in db");
            Optional<List<QuoteDocument>> optionalMongo = quoteRepo.findByKey(prefix);
            if (optionalMongo.isPresent()) {
                QuoteRedis quoteHash;
                if (optionalMongo.get().size() > 0) {
                    QuoteDocument quoteDoc = optionalMongo.get().get(0);
                    quoteHash = new QuoteRedis(quoteDoc.getPrefix(), quoteDoc);
                } else {
                    ArrayList<Suggestion> value = new ArrayList<>();
                    value.add(new Suggestion(prefix, 0));
                    quoteHash = new QuoteRedis(prefix, new QuoteDocument(prefix, value));
                }

                redisService.saveToRedis(quoteHash);

                return quoteHash.getQuoteDocument().getValue().stream()
                        .map(val -> val.getCompletion())
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    public List<String> wordSuggestions(String prefix) {
        String[] keys = prefix.split("\\s+");
        String key = keys[keys.length - 1];

        Optional<List<String>> optionalRedis = redisService.findByKey("word:" + key);
        if (optionalRedis.isPresent()) {
            return optionalRedis.get();
        } else {
            Optional<WordDocument> optionalMongo = wordRepo.findByKey(key);
            WordRedis wordHash;
            if (optionalMongo.isPresent()) {
                WordDocument wordDoc = optionalMongo.get();
                wordHash = new WordRedis(key, wordDoc);
            } else {
                ArrayList<Suggestion> value = new ArrayList<>();
                value.add(new Suggestion(key, 0));
                wordHash = new WordRedis(key, new WordDocument(key, value));
            }

            redisService.saveToRedis(wordHash);

            return wordHash.getWordDocument().getValue().stream()
                    .map(val -> val.getCompletion())
                    .limit(3)
                    .collect(Collectors.toList());
        }
    }

    public List<String> getSuggestions(String message) {
        String prefix = process(message);
        List<String> suggestions = new ArrayList<>();
        suggestions.addAll(wordSuggestions(prefix));
        suggestions.addAll(quoteSuggestions(prefix));
        return suggestions;
    }

    public String process(String message) {
        StringBuilder builder = new StringBuilder();
        String[] strings = message.trim().toLowerCase().split("\\s+");
        for (String str : strings) {
            builder.append(str).append(" ");
        }
        return builder.toString().trim();
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
        TrieWordSearch trieWord = new TrieWordSearch(filePath);
        TrieQuoteSearch trieQuote = new TrieQuoteSearch(filePath);
        return saveSuggestions(trieWord, trieQuote);
    }
}
