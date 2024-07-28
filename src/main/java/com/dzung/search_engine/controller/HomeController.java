package com.dzung.search_engine.controller;

import com.dzung.search_engine.configuration.AzureConfiguration;
import com.dzung.search_engine.dto.request.SignInRequestDto;
import com.dzung.search_engine.dto.response.ApiResponseDto;
import com.dzung.search_engine.dto.request.SignUpRequestDto;
import com.dzung.search_engine.entity.mongo.UserDetailsImpl;
import com.dzung.search_engine.exception.RoleNotFoundException;
import com.dzung.search_engine.exception.UserAlreadyExistsException;
import com.dzung.search_engine.service.*;
import com.dzung.search_engine.service.redis.RedisService;
import com.dzung.search_engine.service.mongo.UserMongoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private SearchService searchService;
    @Autowired
    private UserSearchService userSearchService;
    @Autowired
    private AzureConfiguration configuration;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserMongoService userMongoService;
    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/api/auth/signup")
    public @ResponseBody ResponseEntity<ApiResponseDto<?>> registerUser(@RequestBody @Valid SignUpRequestDto signUpRequestDto) throws RoleNotFoundException, UserAlreadyExistsException {
        return authService.signUpUser(signUpRequestDto);
    }

    @PostMapping("/api/auth/signin")
    public @ResponseBody ResponseEntity<ApiResponseDto<?>> signinUser(@RequestBody SignInRequestDto signInRequestDto) {
        return authService.signInUser(signInRequestDto);
    }

    @GetMapping("")
    public String homePage() {
        return "index";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login_page";
    }

    @GetMapping("/suggestions")
    public @ResponseBody List<String> spellCheck(@RequestParam("word") String word) {
        return searchService.getSuggestions(word);
    }

    @GetMapping("/api/users")
    public String userPage(Model model) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("user", user);

        return "user_page";
    }

    @GetMapping("/api/users/suggestions")
    public @ResponseBody List<String> getSuggestions(@RequestParam("prefix") String prefix) {
        return userSearchService.getSuggestions(prefix);
    }

    @PostMapping("/api/users/data")
    public @ResponseBody boolean insertUserData() {
        return userMongoService.insertUserData();
    }

    @GetMapping("/savedb")
    public @ResponseBody boolean saveToMongoDb() {
        return searchService.saveDb();
    }

    @PostMapping("/azure")
    public @ResponseBody String getTranslation(@RequestParam("message") String message) throws IOException, InterruptedException {
        String azureKey = configuration.getAzureKey();
        String apiUrl = configuration.getApiUrl();
        String region = configuration.getRegion();

        HttpClient client = HttpClient.newHttpClient();

        String resquestBody = "[{\"Text\": \"" + message + "\"}]";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Ocp-Apim-Subscription-Key", azureKey)
                .header("Ocp-Apim-Subscription-Region", region)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(resquestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            String translation = jsonNode.get(0).get("translations").get(0).get("text").asText();

            // Update completion score after translating.
            try {
                UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String userId = userDetails.getId();
                redisService.updateScore(userId, message);
            } catch (Exception e) {
                redisService.updateScore(message);
            }

            return translation;
        } else {
            System.out.println("Error: " + response.statusCode());
            return "Error";
        }
    }
}
