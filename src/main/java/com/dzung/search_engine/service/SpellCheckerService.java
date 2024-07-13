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

    public WordDocument wordSuggest(String word) {
        String[] keys = word.split("\\s+");
        String key = keys[keys.length - 1];

        for (int i = key.length() - 1; i >= 0; i--) {
            WordDocument doc = wordRepo.findByKey(key.substring(0, i + 1));
            if (doc != null)
                return doc;
        }
        return null;
    }

    /**
     * I don't know why quoteRepo return list of QuoteDocument, the quote_collection in mongodb contains duplicate keys.
     *
     * @param word
     * @return a QuoteDocument
     */
    public QuoteDocument quoteSuggest(String word) {
        String[] keys = word.split("\\s+");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            builder.append(keys[i]).append(" ");
        }

        int len = builder.length();
        builder.delete(builder.length() - 1, len);
        for (int i = keys.length - 1; i >= 0; i--) {
            String key = builder.toString().trim();
            List<QuoteDocument> docs = quoteRepo.findByKey(key);
            if (docs.size() > 0)
                return docs.stream().findFirst().get();

            if (keys.length == 1) {
                return null;
            }
            len = builder.length();
            builder.delete(len - keys[i].length() - 1, len);
        }
        return null;
    }

    /**
     * @param word
     * @return list document because we implement two trie (wordTrie and quoteTrie). if we store document in same collection that can be duplicate key.
     * Two collection would be chosen, but in quote_collection still got be duplicate key.
     * I didn't figure out the bug yet.
     */
    public List<Document> suggest(String word) {
        String keyWord = word.toLowerCase().trim();
        List<Document> suggests = new ArrayList<>();
        suggests.add(wordSuggest(keyWord));
        suggests.add(quoteSuggest(keyWord));

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
