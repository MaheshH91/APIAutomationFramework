package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.models.request.LoginRequest;
import com.api.models.response.LoginResponse;
import com.api.utils.ConfigManager;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class LoginTestRefined extends BaseTest {

    @Test(description = "Verify Login API works with valid credentials")
    public void loginApiTest() {
        // Create payload using config data
        LoginRequest login = new LoginRequest(
                ConfigManager.get("username"),
                ConfigManager.get("password")
        );

        // Execute login API
        AuthService authService = new AuthService();
        Response response = authService.login(login);

        // Deserialize
        LoginResponse loginResponse = response.as(LoginResponse.class);

        // Assertions
        Assert.assertEquals(response.statusCode(), 200, "Status code mismatch");
        Assert.assertNotNull(loginResponse.getToken(), "Token is null");
        Assert.assertEquals(loginResponse.getEmail(), "holkarmahesh1@gmail.com", "Email mismatch");

        System.out.println("✅ Login successful for: " + loginResponse.getUsername());
    }
}
