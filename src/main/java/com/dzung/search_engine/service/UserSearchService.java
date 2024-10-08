package com.dzung.search_engine.service;

import com.dzung.search_engine.entity.mongo.UserData;
import com.dzung.search_engine.entity.mongo.UserDetailsImpl;
import com.dzung.search_engine.entity.redis.QuoteRedis;
import com.dzung.search_engine.models.QuoteDocument;
import com.dzung.search_engine.models.Suggestion;
import com.dzung.search_engine.repository.mongo.UserQuoteMongoRepository;
import com.dzung.search_engine.service.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserSearchService {
    @Autowired
    private UserQuoteMongoRepository quoteRepo;
    @Autowired
    private RedisService redisService;

    public List<String> getSuggestions(String prefix) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getId();

        String key = userId + ":" + this.process(prefix);
        System.out.println(key);
        Optional<List<String>> optionalRedis = redisService.findByKey("quote:" + key);
        if (optionalRedis.isPresent()) {
            System.out.println("Retrieve data in Redis");
            return optionalRedis.get();
        } else {
            System.out.println("Retrieve data in db");
            Optional<UserData> optionalMongo = quoteRepo.findByPrefix(userId, prefix);
            QuoteRedis quoteHash;
            if (optionalMongo.isPresent()) {
                UserData docMongo = optionalMongo.get();
                quoteHash = new QuoteRedis(key, docMongo.getQuoteDocument());
            } else {
                ArrayList<Suggestion> value = new ArrayList<>();
                value.add(new Suggestion(prefix, 0));
                quoteHash = new QuoteRedis(key, new QuoteDocument(prefix, value));
            }

            redisService.saveToRedis(quoteHash);

            return quoteHash.getQuoteDocument().getValue().stream()
                    .map(val -> val.getCompletion())
                    .collect(Collectors.toList());
        }
    }

    public String process(String message) {
        StringBuilder builder = new StringBuilder();
        String[] strings = message.trim().toLowerCase().split("\\s+");
        for (String str : strings) {
            builder.append(str).append(" ");
        }
        return builder.toString().trim();
    }
}