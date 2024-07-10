package dzung.trie.spell_checker.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WordDocument {
    @Id
    private String id;
    private String key;
    private List<Suggestion> value;

    public WordDocument(String key, List<Suggestion> value) {
        this.key = key;
        this.value = value;
    }
}
