package com.dzung.search_engine.repository.mongo;

import com.dzung.search_engine.entity.mongo.QuoteDocMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMongoRepository extends MongoRepository<QuoteDocMongo, String> {
    @Query("{'userId': ?0, 'quoteDocument.prefix': ?1}")
    QuoteDocMongo findByKey(String userId, String key);
}
