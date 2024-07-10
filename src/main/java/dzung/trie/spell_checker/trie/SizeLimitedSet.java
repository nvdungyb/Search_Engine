package dzung.trie.spell_checker.trie;

import lombok.Getter;

import java.util.TreeSet;

@Getter
public class SizeLimitedSet<T> {
    private TreeSet<T> suggestions;
    public int treeSetSize;

    /**
     * We neet to take the biggest priority in the TreeSet.
     * T must be implemented Comparable interface.
     *
     * @param size
     */
    public SizeLimitedSet(int size) {
        suggestions = new TreeSet<>();
        this.treeSetSize = size;
    }

    public boolean add(T t) {
        this.suggestions.add(t);

        if (this.suggestions.size() > treeSetSize) {
            this.suggestions.pollFirst();
            return true;
        }

        return false;
    }
}
