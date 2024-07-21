package com.dzung.search_engine.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@org.springframework.data.mongodb.core.mapping.Document(collection = "word_documents")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WordDocument implements Document{
    @Id
    private String id;
    private String key;
    private List<Suggestion> value;

    public WordDocument(String key, List<Suggestion> value) {
        this.key = key;
        this.value = value;
    }
}
