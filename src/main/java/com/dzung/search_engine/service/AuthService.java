package com.dzung.search_engine.service;

import com.dzung.search_engine.dto.response.ApiResponseDto;
import com.dzung.search_engine.dto.request.SignInRequestDto;
import com.dzung.search_engine.dto.request.SignUpRequestDto;
import com.dzung.search_engine.exception.RoleNotFoundException;
import com.dzung.search_engine.exception.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    ResponseEntity<ApiResponseDto<?>> signUpUser(SignUpRequestDto signUpRequestDto) throws UserAlreadyExistsException, RoleNotFoundException;

    ResponseEntity<ApiResponseDto<?>> signInUser(SignInRequestDto signInRequestDto);
}
