package dzung.trie.spell_checker.service;

import dzung.trie.spell_checker.document.Document;
import dzung.trie.spell_checker.document.QuoteDocument;
import dzung.trie.spell_checker.document.Suggestion;
import dzung.trie.spell_checker.document.WordDocument;
import dzung.trie.spell_checker.repository.QuoteRepository;
import dzung.trie.spell_checker.repository.WordRepository;
import dzung.trie.spell_checker.trie.TrieNode;
import dzung.trie.spell_checker.trie.TrieQuoteSearch;
import dzung.trie.spell_checker.trie.TrieWordSearch;
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
    @Autowired
    private TrieQuoteSearch trieQuote;
    @Autowired
    private TrieWordSearch trieWord;


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
        return saveSuggestions(trieWord, trieQuote);
    }
}
