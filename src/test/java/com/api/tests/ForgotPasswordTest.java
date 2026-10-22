package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.config.ConfigManager;
import com.api.services.AuthService;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class ForgotPasswordTest {

    @Test(description = "Verify forgot password endpoint")
    public void forgotPasswordTest() {
        AuthService authService = new AuthService();
        Response response = authService.forgotPassword(ConfigManager.get("forgot.email", "holkarmahesh1@gmail.com"));
        Assert.assertTrue(response.getStatusCode() == 200 || response.getStatusCode() == 404);
    }
}