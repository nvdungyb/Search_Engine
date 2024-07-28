package com.dzung.search_engine.repository.mongo;

import com.dzung.search_engine.entity.mongo.UserData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserQuoteMongoRepository extends MongoRepository<UserData, String> {
    @Query("{'userId': ?0, 'quoteDocument.key': ?1}")
    Optional<UserData> findByPrefix(String userId, String prefix);
}
