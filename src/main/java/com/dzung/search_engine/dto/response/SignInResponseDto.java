package com.dzung.search_engine.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInResponseDto {
    private String username;
    private String email;
    private String id;
    private String token;
    private String type;
    private List<String> roles;
}
