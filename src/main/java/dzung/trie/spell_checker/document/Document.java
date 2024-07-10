package dzung.trie.spell_checker.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Document {
    @Id
    private String id;
    private String key;
    private List<Suggestion> value;

    public Document(String key, List<Suggestion> value) {
        this.key = key;
        this.value = value;
    }
}
