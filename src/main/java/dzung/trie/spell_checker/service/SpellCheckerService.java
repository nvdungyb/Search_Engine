package dzung.trie.spell_checker.service;

import dzung.trie.spell_checker.document.Document;
import dzung.trie.spell_checker.document.Suggestion;
import dzung.trie.spell_checker.repository.DatabaseRepository;
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
    private TrieWordSearch trieWord;
    @Autowired
    private TrieQuoteSearch trieQuote;
    @Autowired
    private DatabaseRepository respo;

    public List<String> suggest(String word) {
        List<String> suggests = new ArrayList<>();
        suggests.addAll(trieWord.getSuggest(word));
        suggests.addAll(trieQuote.getSuggest(word));

        return suggests;
    }

    public void saveWordSuggestions(String key, TrieNode curr) {
        if (curr.value.getSuggestions().size() > 0) {
            List<Suggestion> value = curr.value.getSuggestions().stream().collect(Collectors.toList());
            Document doc = new Document(key, value);
            respo.save(doc);
        }

        for (String keyValue : curr.getChild().keySet()) {
            saveWordSuggestions(key + keyValue, curr.child.get(keyValue));
        }
    }

    public void saveQuoteSuggestions(String key, TrieNode curr) {
        if (curr.value.getSuggestions().size() > 0) {
            List<Suggestion> value = curr.value.getSuggestions().stream().collect(Collectors.toList());
            Document doc = new Document(key, value);
            respo.save(doc);
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
