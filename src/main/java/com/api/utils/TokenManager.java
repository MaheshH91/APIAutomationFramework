package com.api.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.api.config.ConfigManager;
import com.api.models.request.LoginRequest;
import com.api.models.response.LoginResponse;
import com.api.services.AuthService;

import io.restassured.response.Response;

public final class TokenManager {
    private static final Map<String, String> TOKEN_CACHE = new ConcurrentHashMap<>();

    private TokenManager() {}

    public static String getDefaultToken() {
        return getTokenForUser(ConfigManager.get("username"), ConfigManager.get("password"));
    }

    public static String getTokenForUser(String username, String password) {
        return TOKEN_CACHE.computeIfAbsent(username, user -> {
            AuthService authService = new AuthService();
            Response response = authService.login(new LoginRequest(user, password));
            if (response.getStatusCode() != 200) {
                throw new IllegalStateException("Authentication failed for user: " + user + " (HTTP " + response.getStatusCode() + ")");
            }
            return response.as(LoginResponse.class).getToken();
        });
    }

    public static void clearTokenCache() {
        TOKEN_CACHE.clear();
    }
}