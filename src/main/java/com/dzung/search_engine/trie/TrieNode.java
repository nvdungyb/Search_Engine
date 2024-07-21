package com.dzung.search_engine.trie;

import com.dzung.search_engine.models.Suggestion;
import lombok.Data;

import java.util.HashMap;

@Data
public class TrieNode {
    public HashMap<String, TrieNode> child;
    public boolean isEnd;
    public int score;
    public SizeLimitedSet<Suggestion> value;

    public TrieNode() {
        this.child = new HashMap<>();
        this.isEnd = false;
        this.value = new SizeLimitedSet<>(5);
    }
}
