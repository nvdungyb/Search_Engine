package com.dzung.search_engine.service;

import com.dzung.search_engine.FileUploadUtil;
import com.dzung.search_engine.dto.request.SignInRequestDto;
import com.dzung.search_engine.dto.request.SignUpRequestDto;
import com.dzung.search_engine.dto.response.ApiResponseDto;
import com.dzung.search_engine.dto.response.SignInResponseDto;
import com.dzung.search_engine.entity.mongo.Role;
import com.dzung.search_engine.entity.mongo.User;
import com.dzung.search_engine.entity.mongo.UserDetailsImpl;
import com.dzung.search_engine.exception.RoleNotFoundException;
import com.dzung.search_engine.exception.UserAlreadyExistsException;
import com.dzung.search_engine.security.JWTUtils;
import com.dzung.search_engine.service.mongo.RoleFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleFactory roleFactory;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTUtils jwtUtils;

    private User createUser(SignUpRequestDto signUpRequestDto) throws RoleNotFoundException {
        return User.builder()
                .username(signUpRequestDto.getUsername())
                .email(signUpRequestDto.getEmail())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .enabled(true)
                .roles(determineRoles(signUpRequestDto.getRoles()))
                .build();
    }

    private Set<Role> determineRoles(Set<String> strRoles) throws RoleNotFoundException {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(roleFactory.getInstance("user"));
        } else {
            for (String role : strRoles) {
                roles.add(roleFactory.getInstance(role));
            }
        }
        return roles;
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> signUpUser(SignUpRequestDto signUpRequestDto) throws UserAlreadyExistsException, RoleNotFoundException {
        if (userService.existsByEmail(signUpRequestDto.getEmail())) {
            throw new UserAlreadyExistsException("Registration failed: Provided email already exists!");
        }

        try {
            User user = this.createUser(signUpRequestDto);
            String srcFilePath = signUpRequestDto.getFilePath();
            user.setFileName(Paths.get(srcFilePath).getFileName().toString());

            User savedUser = userService.save(user);

            String dir = "E:\\TrieApplication\\Search_Engine\\Search_Engine\\user_data\\" + savedUser.getId();
            FileUploadUtil.saveFile(dir, srcFilePath);
        } catch (Exception e) {
            System.out.println("Can not save user data!");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .message("User account has been successfully created!")
                                .build()
                );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> signInUser(SignInRequestDto signInRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequestDto.getEmail(), signInRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .id(userDetails.getId())
                .token(jwt)
                .type("Bearer")
                .roles(roles)
                .build();

        return ResponseEntity
                .ok(
                        ApiResponseDto.builder()
                                .isSuccess(true)
                                .message("Sign in successfully!")
                                .response(signInResponseDto)
                                .build()
                );
    }
}
