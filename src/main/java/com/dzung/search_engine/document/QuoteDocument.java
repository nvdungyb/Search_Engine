package com.dzung.search_engine.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@org.springframework.data.mongodb.core.mapping.Document(collection = "quote_documents")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuoteDocument implements Document {
    @Id
    @JsonIgnore
    private String id;
    private String prefix;
    private List<Suggestion> value;

    public QuoteDocument(String prefix, List<Suggestion> value) {
        this.prefix = prefix;
        this.value = value;
    }
}
