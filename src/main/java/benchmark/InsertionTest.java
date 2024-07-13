package benchmark;//package benchmark;
//
//import dzung.trie.spell_checker.trie.QuoteNode;
//import org.openjdk.jmh.annotations.*;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.stream.Stream;
//
//import static dzung.trie.spell_checker.trie.TrieQuoteSearch.insert;
//
//@State(Scope.Benchmark)
//public class InsertionTest {
//    private static String wordsPath;
//    private static String quotesPath;
//
//    @Setup
//    public void init() {
//        wordsPath = "E:\\TrieApplication\\spell_checker\\src\\main\\java\\dzung\\trie\\spell_checker\\trie\\words.txt";
//        quotesPath = "E:\\TrieApplication\\spell_checker\\src\\main\\java\\dzung\\trie\\spell_checker\\trie\\quotes.txt";
//    }
//
//    @Benchmark
//    @Fork(value = 1, warmups = 1)
//    public static void createWordTrie() {
////        WordNode trieWord = new WordNode();
////
////        List<String> words;
////        try (Stream<String> lines = Files.lines(Paths.get(wordsPath))) {
////            words = lines.flatMap(line -> Stream.of(line.replace(",", " ").toLowerCase().split("\\s+"))).collect(Collectors.toList());
////        } catch (IOException e) {
////            throw new RuntimeException(e);
////        }
////
////        words.parallelStream().forEach(word -> insertWord(trieWord, word));
//
//        QuoteNode trieQuote = new QuoteNode();
//        try (Stream<String> lines = Files.lines(Paths.get(quotesPath))) {
//            lines.forEach(line -> {
//                line = line.replace("\\s+", " ").trim();
//                if (!line.isEmpty())
//                    insert(trieQuote, line);
//            });
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
