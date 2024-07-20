package com.dzung.search_engine.repository.mongo;

import com.dzung.search_engine.entity.mongo.QuoteMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserQuoteMongoRepository extends MongoRepository<QuoteMongo, String> {
    @Query("{'userId': ?0, 'quoteDocument.prefix': ?1}")
    Optional<QuoteMongo> findByPrefix(String userId, String predix);
}
