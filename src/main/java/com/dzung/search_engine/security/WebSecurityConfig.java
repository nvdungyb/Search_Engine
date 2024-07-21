package com.dzung.search_engine.security;

import com.dzung.search_engine.service.mongo.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}

/**
 * Step-by-Step authentication process.
 * <p>
 * 1. Intercepting the request:
 * + The UsernamePasswordAuthenticationFilter intercepts HttpRequest at a specified url (default=/login).
 * + If the filter checks if the request is a POST request and the correct URL. If not, it passes the request along the filter chain without doing anything.
 * <p>
 * 2. Extracting Credential:
 * + If the request matches the expected URL and method. the filter extracts the username and password from the request parameters (default: username and password).
 * <p>
 * 3. Creating Authentication:
 * + The extracted username and password are used to create an 'UsernamePasswordAuthenticationToken'. This token is not yet authenticated but carries the credentials.
 * <p>
 * 4. Delegating Authentication:
 * + The token is passed to the 'AuthenticationManager', which is responsible for the actual authentication process.
 * + The 'AuthenticationManager' usually delegates to an 'AuthenticationProvider' (such as 'DaoAuthenticationProvider') to perform the authentication.
 * <p>
 * 5. AuthenticationProvider:
 * + The 'AuthenticationProvider' uses a 'UserDetailsService' to load user details (including password) from a db.
 * + It then compares the provided password with stored password.
 * <p>
 * 6. Successful Authentication:
 * + If authentication is successful, The 'AuthenticationProvider' return a fully authenticated 'Authentication' Object.
 * + The filter then store this authentication object in the 'SecurityContext', which is associated with the current session.
 */
