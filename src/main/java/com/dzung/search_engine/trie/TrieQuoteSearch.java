package com.dzung.search_engine.trie;

import com.dzung.search_engine.controller.FilePath;
import com.dzung.search_engine.models.Suggestion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class TrieQuoteSearch implements Trie {
    private TrieNode root;

    public TrieQuoteSearch(FilePath filePath) {
        root = new TrieNode();
        this.createTrie(root, filePath.getQuotesPath());
    }

    @Override
    public void insert(String quote) {
        TrieNode root = this.root;
        String[] words = quote.toLowerCase().split("\\s+");     // Tất cả key đều viết thường tiện cho tìm kiếm.

        TrieNode temp = root;
        for (int i = 0; i < words.length; i++) {
            if (!temp.child.containsKey(words[i])) {
                temp.child.put(words[i], new TrieNode());            // Thêm từ mới vào cây.
            }
            if (temp != root)
                temp.value.add(new Suggestion(quote));
            temp = temp.child.get(words[i]);
        }

        temp.value.add(new Suggestion(quote));
        temp.isEnd = true;
    }

    @Override
    public TrieNode nodeEnd(String quote) {
        TrieNode root = this.root;
        List<String> words = List.of(quote.toLowerCase().split("\\s+"));

        TrieNode temp = root;
        for (String word : words) {
            if (!temp.child.containsKey(word)) {
                if (temp == root)                   // Nếu không có từ nào trong của quote trong cây.
                    return null;
                return temp;                        // Trả về từ cuối cùng trong quote có trong cây.
            }
            temp = temp.child.get(word);
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
    public List<String> getSuggest(String quote) {
        List<String> suggestions = new ArrayList<>();
        TrieNode start = nodeEnd(quote);

        if (start == null) {
            return suggestions;
        }

        findSuggestions(start, suggestions);

        return suggestions;
    }

    @Override
    public void createTrie(TrieNode root, String filePath) {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line -> {
                String quote = line.replace("\\s+", " ").trim();
                if (!quote.isEmpty())
                    insert(quote);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

