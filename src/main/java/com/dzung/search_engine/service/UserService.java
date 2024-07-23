package com.dzung.search_engine.service;

import com.dzung.search_engine.entity.mongo.User;
import com.dzung.search_engine.repository.mongo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public boolean existsByUserName(String username) {
        return userRepo.existsByUsername(username);
    }

    public void save(User user) {
        userRepo.save(user);
    }
}
