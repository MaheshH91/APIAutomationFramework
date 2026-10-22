package com.api.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.api.config.ConfigManager;
import com.api.models.request.LoginRequest;
import com.api.models.response.LoginResponse;
import com.api.services.AuthService;

import io.restassured.response.Response;

public class TokenManager {

    private static final Logger log = LogManager.getLogger(TokenManager.class);
    
    // Concurrent map to cache tokens per username for multi-threaded/parallel execution
    private static final Map<String, String> tokenCache = new ConcurrentHashMap<>();

    private TokenManager() {
        // Prevent direct instantiation
    }

    /**
     * Retrieves the default admin/user JWT token using credentials from config.properties.
     */
  
    public static String getDefaultToken() {
        String defaultUsername = ConfigManager.getProperty("valid.username", "holkarmahesh");
        String defaultPassword = ConfigManager.getProperty("valid.password", "Raj@1234");
        return getToken(defaultUsername, defaultPassword);
    }

    /**
     * Retrieves or generates a JWT Bearer token for the given credentials.
     * Caches the token to avoid redundant network login calls across tests.
     */
    public static synchronized String getToken(String username, String password) {
        if (tokenCache.containsKey(username) && tokenCache.get(username) != null) {
            log.info("Fetching cached JWT token for user: {}", username);
            return tokenCache.get(username);
        }

        log.info("Requesting new JWT token for user: {}", username);
        AuthService authService = new AuthService();
        LoginRequest loginPayload = new LoginRequest.Builder()
                .username(username)
                .password(password)
                .build();

        Response response = authService.login(loginPayload);

        if (response.getStatusCode() == 200) {
            LoginResponse loginResponse = response.as(LoginResponse.class);
            String token = loginResponse.getToken();

            if (token != null && !token.isBlank()) {
                tokenCache.put(username, token);
                log.info("JWT token successfully generated and cached for user: {}", username);
                return token;
            }
        }

        log.error("Failed to acquire JWT token. Response Status: {}", response.getStatusCode());
        throw new RuntimeException("JWT token extraction failed. API Status Code: " + response.getStatusCode());
    }

    /**
     * Clears the cached tokens (useful between test cycles).
     */
    public static void clearTokenCache() {
        tokenCache.clear();
        log.info("Token cache cleared.");
    }
}