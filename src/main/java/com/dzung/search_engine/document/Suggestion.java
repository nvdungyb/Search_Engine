package dzung.trie.spell_checker.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion implements Comparable<Suggestion> {
    private String suggestion;
    private int score;

    public Suggestion(String value) {
        this.suggestion = value;
        this.score = 0;
    }

    /**
     * We are using TreeSet to contain Suggestion, and we remove the smallest object if size of the treeSet > 5.
     *
     * @param o the object to be compared.
     * @return if this.score == o.score
     * => compare length of this.value with o.value
     * if this.value.length == o.value.length
     * => Compares two strings lexicographically.
     * following reverser order.
     */
    @Override
    public int compareTo(Suggestion o) {
        int cmpInt = Integer.compare(this.score, o.score);
        if (cmpInt == 0) {
            int cmpLen = Integer.compare(this.suggestion.length(), o.suggestion.length());
            if (cmpLen == 0)
                return -this.suggestion.compareTo(o.suggestion);
            return -cmpLen;
        }
        return -cmpInt;
    }
}
