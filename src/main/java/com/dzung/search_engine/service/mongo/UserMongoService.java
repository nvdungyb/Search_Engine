package com.dzung.search_engine.service.mongo;

import com.dzung.search_engine.configuration.FilePath;
import com.dzung.search_engine.entity.mongo.UserData;
import com.dzung.search_engine.entity.mongo.UserDetailsImpl;
import com.dzung.search_engine.models.QuoteDocument;
import com.dzung.search_engine.models.Suggestion;
import com.dzung.search_engine.repository.mongo.UserQuoteMongoRepository;
import com.dzung.search_engine.trie.TrieNode;
import com.dzung.search_engine.trie.TrieQuoteSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMongoService {
    @Autowired
    private UserQuoteMongoRepository userMongoRepo;

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

    public boolean saveUserDB(String userId, TrieQuoteSearch quoteTrie) {
        try {
            List<QuoteDocument> quoteDocuments = new ArrayList<>();
            saveQuoteSuggestionsDB("", quoteTrie.getRoot(), quoteDocuments);

            quoteDocuments.forEach(e -> userMongoRepo.save(new UserData(userId, e)));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean insertUserData() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getId();

        FilePath filePath = new FilePath();
        String srcFile = "E:\\TrieApplication\\Search_Engine\\Search_Engine\\user_data\\" + user.getId() + "\\" + user.getFileName();
        filePath.setQuotesPath(srcFile);

        TrieQuoteSearch trieQuoteSearch = new TrieQuoteSearch(filePath);

        return this.saveUserDB(userId, trieQuoteSearch);
    }
}
