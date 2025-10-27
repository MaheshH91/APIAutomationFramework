package com.api.base;

import com.api.models.request.LoginRequest;
import com.api.models.response.LoginResponse;
import com.api.utils.ConfigManager;
import io.restassured.response.Response;

public class TokenManager {
    private static String token;

    public static String getToken() {
        if (token == null) {
            AuthService authService = new AuthService();
            Response res = authService.login(new LoginRequest(
                    ConfigManager.get("username"),
                    ConfigManager.get("password")
            ));
            LoginResponse lr = res.as(LoginResponse.class);
            token = lr.getToken();
        }
        return token;
    }
}
