package com.dzung.search_engine.repository.mongo;

import com.dzung.search_engine.models.QuoteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteAppMongoRepository extends MongoRepository<QuoteDocument, String> {
    @Query("{'key': ?0}")
    Optional<List<QuoteDocument>> findByKey(String key);
}
