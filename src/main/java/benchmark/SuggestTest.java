package benchmark;

import dzung.trie.spell_checker.controller.FilePath;
import dzung.trie.spell_checker.trie.TrieQuoteSearch;
import dzung.trie.spell_checker.trie.TrieWordSearch;
import org.openjdk.jmh.annotations.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@State(Scope.Benchmark)
public class SuggestTest {
    private TrieWordSearch trieWord;
    private TrieQuoteSearch trieQuote;
    private String word = "INDICTMENT";

    @Setup                  // The @Setup annotated method is invoked before each invocation of the benchmark
    public void init() {
        FilePath filePath = new FilePath();
        filePath.setQuotesPath("E:\\TrieApplication\\spell_checker\\src\\main\\java\\dzung\\trie\\spell_checker\\trie\\quotes.txt");
        filePath.setWordsPath("E:\\TrieApplication\\spell_checker\\src\\main\\java\\dzung\\trie\\spell_checker\\trie\\words.txt");

        trieWord = new TrieWordSearch(filePath);
        trieQuote = new TrieQuoteSearch(filePath);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    public List<String> suggest() {
        List<String> suggests = new ArrayList<>();
        suggests.addAll(trieWord.getSuggest(word));
        suggests.addAll(trieQuote.getSuggest(word));

        return suggests;
    }
}