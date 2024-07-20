package com.dzung.search_engine.service;

import com.dzung.search_engine.controller.FilePath;
import com.dzung.search_engine.document.QuoteDocument;
import com.dzung.search_engine.document.Suggestion;
import com.dzung.search_engine.entity.mongo.UserDoc;
import com.dzung.search_engine.repository.mongo.UserMongoRepository;
import com.dzung.search_engine.repository.redis.UseRedisRepository;
import com.dzung.search_engine.trie.TrieNode;
import com.dzung.search_engine.trie.TrieQuoteSearch;
import com.dzung.search_engine.entity.redis.UserHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UseRedisRepository userRedisRepo;
    @Autowired
    private UserMongoRepository userMongoRepo;

    public UserHash saveUser() {
        UserHash user = new UserHash();
        user.setKey("123");
        QuoteDocument listQuotes;
        listQuotes = (new QuoteDocument("he", List.of(new Suggestion("hello", 0), new Suggestion("hell", 0), new Suggestion("heat", 0))));
        user.setQuoteDocument(listQuotes);

        return userRedisRepo.save(user);
    }

    public UserHash getUserById(String id) {
        return userRedisRepo.findById(id).get();
    }

    public void deleteUserById(String id) {
        userRedisRepo.deleteById(id);
    }

    public void saveQuoteSuggestionsDB(String key, TrieNode curr, List<QuoteDocument> quoteDocuments) {
        if (curr.value.getSuggestions().size() > 0) {
            List<Suggestion> value = curr.value.getSuggestions().stream().collect(Collectors.toList());
            QuoteDocument doc = new QuoteDocument(key, value);
            quoteDocuments.add(doc);
        }

        for (String keyValue : curr.getChild().keySet()) {
            saveQuoteSuggestionsDB((key + " " + keyValue).trim(), curr.child.get(keyValue), quoteDocuments);
        }
    }

    public boolean saveUserDB(UserDoc user, TrieQuoteSearch quoteTrie) {
        try {
            List<QuoteDocument> quoteDocuments = new ArrayList<>();
            saveQuoteSuggestionsDB("", quoteTrie.getRoot(), quoteDocuments);
            user.setQuoteDocuments(quoteDocuments);
            userMongoRepo.save(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean createUser() {
        FilePath filePath = new FilePath();
        filePath.setQuotesPath("E:\\TrieApplication\\Search_Engine\\Search_Engine\\src\\main\\java\\com\\dzung\\search_engine\\trie\\divice_name.txt");

        TrieQuoteSearch trieQuoteSearch = new TrieQuoteSearch(filePath);

        UserDoc user = new UserDoc();
        user.setName("DungNv");
        user.setEmail("dung@gmail.com");
        this.saveUserDB(user, trieQuoteSearch);

        return true;
    }
}
