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

import javax.print.Doc;
import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

//    public List<String> suggest(String word) {
//        List<String> suggests = new ArrayList<>();
//        suggests.addAll(trieWord.getSuggest(word));
//        suggests.addAll(trieQuote.getSuggest(word));
//
//        return suggests;
//    }

    public WordDocument wordSuggest(String word) {
        WordDocument ans = null;
        String[] keys = word.split("\\s+");
        String key = keys[keys.length - 1];

        for (int i = 0; i < key.length(); i++) {
            WordDocument doc = wordRepo.findByKey(key.substring(0, i + 1));
            if (doc != null)
                ans = doc;
        }
        return ans;
    }

    public List<QuoteDocument> quoteSuggest(String word) {
        List<QuoteDocument> ans = null;
        String[] keys = word.split("\\s+");

        StringBuilder builder = new StringBuilder();
        for (String x : keys) {
            builder.append(x).append(" ");
            String key = builder.toString().trim();

            List<QuoteDocument> docs = quoteRepo.findByKey(key);
            if (docs.size() > 0)
                ans = docs;
        }
        return ans;
    }

    /**
     * @param word
     * @return list document cause we implements two trie (wordTrie and quoteTrie). if we store document in same collection that can be duplicate key.
     * Two collection would be chosen, but in quote_collection still got be duplicate key.
     * I didn't figure out yet.
     */
    public List<Document> suggest(String word) {
        String keyWord = word.toLowerCase().trim();
        List<Document> suggests = new ArrayList<>();
        suggests.add(wordSuggest(keyWord));
        suggests.addAll(quoteSuggest(keyWord));

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
