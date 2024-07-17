package com.dzung.search_engine.trie;

import com.dzung.search_engine.controller.FilePath;
import com.dzung.search_engine.document.Suggestion;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Getter
public class TrieWordSearch implements Trie {
    private TrieNode root;

    public TrieWordSearch(FilePath filePath) {
        root = new TrieNode();
        this.createTrie(root, filePath.getWordsPath());
    }

    @Override
    public void insert(String key) {
        TrieNode root = this.root;
        String[] words = key.toLowerCase().split("");     // Tất cả key đều viết thường tiện cho tìm kiếm.

        TrieNode temp = root;
        for (int i = 0; i < words.length; i++) {
            if (!temp.child.containsKey(words[i])) {
                temp.child.put(words[i], new TrieNode());            // Thêm từ mới vào cây.
            }
            if (temp != root)
                temp.value.add(new Suggestion(key));
            temp = temp.child.get(words[i]);
        }
        temp.value.add(new Suggestion(key));
        temp.isEnd = true;
    }

    public static boolean isValid(String key) {
        return key.matches("^[a-zA-Z]*$");
    }

    @Override
    public TrieNode nodeEnd(String key) {
        TrieNode root = this.root;
        String[] words = key.toLowerCase().split("");

        TrieNode temp = root;
        for (int i = 0; i < words.length; i++) {
            if (!temp.child.containsKey(words[i])) {
                return null;
            }
            temp = temp.child.get(words[i]);
        }
        return temp;
    }

    @Override
    public void findSuggestions(TrieNode currentNode, List<String> suggestions) {
        suggestions.addAll(currentNode.value.getSuggestions().stream()
                .map(e -> e.getCompletion())
                .collect(Collectors.toList())
        );
    }

    @Override
    public List<String> getSuggest(String word) {
        List<String> words = List.of(word.toLowerCase().split("\\s+"));

        List<String> suggestions = new ArrayList<>();
        if (!isValid(words.get(words.size() - 1))) {
            return suggestions;
        }
        TrieNode start = nodeEnd(words.get(words.size() - 1));

        if (start == null) {
            return suggestions;
        }

        findSuggestions(start, suggestions);

        return suggestions;
    }

    @Override
    public void createTrie(TrieNode root, String filePath) {
        List<String> words;
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            words = lines.flatMap(line -> Stream.of(line.replace(",", " ").toLowerCase().split("\\s+"))).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        words.stream().forEach(word -> insert(word));
    }
}

