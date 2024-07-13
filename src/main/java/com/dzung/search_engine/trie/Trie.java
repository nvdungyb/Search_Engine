package dzung.trie.spell_checker.trie;

import java.util.List;

public interface Trie {
    void createTrie(dzung.trie.spell_checker.trie.TrieNode root, String filePath);

    void insert(String key);

    List<String> getSuggest(String key);

    TrieNode nodeEnd(String key);

    void findSuggestions(dzung.trie.spell_checker.trie.TrieNode curr, List<String> suggestion);

}
