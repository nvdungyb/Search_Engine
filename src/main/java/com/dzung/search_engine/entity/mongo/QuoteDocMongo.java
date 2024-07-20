package com.dzung.search_engine.entity.mongo;

import com.dzung.search_engine.document.QuoteDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_data")
@Data
public class QuoteDocMongo {
    @Id
    private String email;
    private String name;
    private List<QuoteDocument> quoteDocuments;
}
