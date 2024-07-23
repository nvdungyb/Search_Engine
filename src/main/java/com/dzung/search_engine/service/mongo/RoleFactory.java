package com.dzung.search_engine.service.mongo;

import com.dzung.search_engine.entity.mongo.ERole;
import com.dzung.search_engine.entity.mongo.Role;
import com.dzung.search_engine.exception.RoleNotFoundException;
import com.dzung.search_engine.repository.mongo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleFactory {
    @Autowired
    RoleRepository roleRepo;

    public Role getInstance(String role) throws RoleNotFoundException {
        switch (role) {
            case "admin" -> {
                return roleRepo.findByName(ERole.ROLE_ADMIN.name()).get();
            }
            case "user" -> {
                return roleRepo.findByName(ERole.ROLE_USER.name()).get();
            }
            default -> throw new RoleNotFoundException("No role found for " + role);
        }
    }
}
