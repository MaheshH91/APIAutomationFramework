package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.config.ConfigManager;
import com.api.models.request.LoginRequest;
import com.api.models.response.LoginResponse;
import com.api.services.AuthService;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class LoginTest {

    @Test(description = "Verify login API with valid credentials from config")
    public void loginTest() {
        AuthService authService = new AuthService();
        LoginRequest loginRequest = new LoginRequest(
                ConfigManager.get("username"),
                ConfigManager.get("password")
        );

        Response response = authService.login(loginRequest);
        Assert.assertEquals(response.getStatusCode(), 200);

        LoginResponse loginResponse = response.as(LoginResponse.class);
        Assert.assertNotNull(loginResponse.getToken());
        Assert.assertEquals(loginResponse.getUsername(), "holkarmahesh");
    }
}