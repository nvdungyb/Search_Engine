package com.dzung.search_engine.entity.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @Id
    private String id;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @DBRef
    private Set<Role> roles = new HashSet<>();

    private boolean enabled;
}
