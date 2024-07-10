//package benchmark;
//
//import dzung.trie.spell_checker.controller.FilePath;
//import dzung.trie.spell_checker.trie.TrieQuoteSearch;
//import dzung.trie.spell_checker.trie.TrieWordSearch;
//import org.openjdk.jmh.annotations.*;
//import org.springframework.beans.factory.annotation.Value;
//
//@State(Scope.Benchmark)
//public class DatabaseTest {
//    private TrieWordSearch trieWord;
//    private TrieQuoteSearch trieQuote;
//    private FilePath filePath;
//
//    @Setup
//    public void setUp() {
//        filePath = new FilePath();
//        filePath.setWordsPath("E:\\TrieApplication\\spell_checker\\src\\main\\java\\dzung\\trie\\spell_checker\\trie\\words.txt");
//        filePath.setQuotesPath("E:\\TrieApplication\\spell_checker\\src\\main\\java\\dzung\\trie\\spell_checker\\trie\\quotes.txt");
//        trieWord = new TrieWordSearch(filePath);
//        trieQuote = new TrieQuoteSearch(filePath);
//    }
//
//    @Benchmark
//    @Fork(value = 1, warmups = 1)
//    public Integer saveDb() {
//        return trieQuote.countNode() + trieWord.countNode();
//    }
//}
