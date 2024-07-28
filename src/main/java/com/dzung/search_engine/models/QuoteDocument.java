package com.dzung.search_engine.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@org.springframework.data.mongodb.core.mapping.Document(collection = "quote_documents")
@AllArgsConstructor
@Data
public class QuoteDocument implements Document {
    @Id
    @JsonIgnore
    private String id;
    @Field(name = "key")
    private String prefix;
    private List<Suggestion> value;

    public QuoteDocument(String prefix, List<Suggestion> value) {
        this.prefix = prefix;
        this.value = value;
    }

    public QuoteDocument() {
        this.id = "";
        this.prefix = "";
        this.value = new ArrayList<>();
    }
}
