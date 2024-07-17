package com.dzung.search_engine.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion implements Comparable<Suggestion>, Serializable {
    private String completion;
    private int score;

    public Suggestion(String value) {
        this.completion = value;
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
            int cmpLen = Integer.compare(this.completion.length(), o.completion.length());
            if (cmpLen == 0)
                return -this.completion.compareTo(o.completion);
            return -cmpLen;
        }
        return -cmpInt;
    }
}
