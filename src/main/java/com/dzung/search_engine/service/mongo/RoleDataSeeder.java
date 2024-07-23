package com.dzung.search_engine.service.mongo;

import com.dzung.search_engine.entity.mongo.ERole;
import com.dzung.search_engine.entity.mongo.Role;
import com.dzung.search_engine.repository.mongo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleDataSeeder {
    @Autowired
    private RoleRepository roleRepo;

    @EventListener
    @Transactional
    public void LoadRoles(ContextRefreshedEvent event) {
        List<ERole> roles = Arrays.stream(ERole.values()).collect(Collectors.toList());

        for (ERole role : roles) {
            if (roleRepo.findByName(role.name()).isEmpty())
                roleRepo.save(new Role(role));
        }
    }
}
/**
 * Initialing data to work with when we deploy a new application or set up a new database.
 */