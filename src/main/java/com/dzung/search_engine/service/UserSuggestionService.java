package com.dzung.search_engine.service;

import com.dzung.search_engine.models.QuoteDocument;
import com.dzung.search_engine.entity.mongo.QuoteMongo;
import com.dzung.search_engine.entity.redis.QuoteRedis;
import com.dzung.search_engine.service.mongo.UserMongoService;
import com.dzung.search_engine.service.redis.UserRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserMongoService userMongoService;
    @Autowired
    private UserRedisService userRedisService;

    public QuoteRedis getSuggestion(String prefix) {
        Optional<QuoteRedis> optionalRedis = userRedisService.findByKey(prefix);
        if (optionalRedis.isPresent()) {
            System.out.println("Retrieve data in Redis");
            return optionalRedis.get();
        } else {
            System.out.println("Retrieve data in db");
            Optional<QuoteMongo> optionalMongo = userMongoService.findByPrefix(prefix);
            if (optionalMongo.isPresent()) {
                QuoteMongo docMongo = optionalMongo.get();
                QuoteDocument quoteDocument = docMongo.getQuoteDocument();

                QuoteRedis quoteHash = new QuoteRedis(quoteDocument.getPrefix(), quoteDocument);
                userRedisService.saveToRedis(quoteHash);

                return quoteHash;
            }
        }
        return new QuoteRedis();
    }
}
