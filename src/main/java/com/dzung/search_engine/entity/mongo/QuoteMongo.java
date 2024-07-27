package com.dzung.search_engine.entity.mongo;

import com.dzung.search_engine.models.QuoteDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_data")
@Data
public class QuoteMongo {
    @Id
    private String id;
    private String userId;
    private QuoteDocument quoteDocument;

    public QuoteMongo(String userId, QuoteDocument quoteDocument) {
        this.userId = userId;
        this.quoteDocument = quoteDocument;
    }
}
