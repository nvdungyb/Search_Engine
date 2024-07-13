package com.dzung.search_engine.repository;

import com.dzung.search_engine.document.QuoteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends MongoRepository<QuoteDocument, String> {
    @Query("{'key': ?0}")
    List<QuoteDocument> findByKey(String substring);
}
