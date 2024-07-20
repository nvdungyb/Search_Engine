package com.dzung.search_engine.repository.mongo;

import com.dzung.search_engine.document.WordDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WordAppMongoRepository extends MongoRepository<WordDocument, String> {
    @Query("{'key' : ?0}")
    WordDocument findByKey(String key);
}
