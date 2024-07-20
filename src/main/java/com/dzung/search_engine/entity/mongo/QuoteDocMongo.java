package com.dzung.search_engine.entity.mongo;

import com.dzung.search_engine.document.QuoteDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_data")
@Data
public class QuoteDocMongo {
    @Id
    @JsonIgnore
    private String id;
    private String userId;
    private QuoteDocument quoteDocument;

    public QuoteDocMongo(String userId, QuoteDocument quoteDocument) {
        this.userId = userId;
        this.quoteDocument = quoteDocument;
    }
}
