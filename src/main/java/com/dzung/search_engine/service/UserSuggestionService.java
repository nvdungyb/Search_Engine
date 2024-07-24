package com.dzung.search_engine.service;

import com.dzung.search_engine.entity.mongo.UserDetailsImpl;
import com.dzung.search_engine.entity.mongo.QuoteMongo;
import com.dzung.search_engine.entity.redis.QuoteRedis;
import com.dzung.search_engine.service.mongo.UserMongoService;
import com.dzung.search_engine.service.redis.UserRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSuggestionService {
    @Autowired
    private UserMongoService userMongoService;
    @Autowired
    private UserRedisService userRedisService;

    public QuoteRedis getSuggestion(String prefix) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();

        String key = userId + ":" + prefix;
        Optional<QuoteRedis> optionalRedis = userRedisService.findByKey(key);
        if (optionalRedis.isPresent()) {
            System.out.println("Retrieve data in Redis");
            return optionalRedis.get();
        } else {
            System.out.println("Retrieve data in db");
            Optional<QuoteMongo> optionalMongo = userMongoService.findByPrefix(userId, prefix);
            if (optionalMongo.isPresent()) {
                QuoteMongo docMongo = optionalMongo.get();
                QuoteRedis quoteHash = new QuoteRedis(userId + ":" + prefix, docMongo.getQuoteDocument());
                userRedisService.saveToRedis(quoteHash);

                return quoteHash;
            }
        }
        return new QuoteRedis();
    }
}
