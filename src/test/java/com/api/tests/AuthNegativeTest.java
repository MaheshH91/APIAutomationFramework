package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.models.request.LoginRequest;
import com.api.models.request.SignUpRequest;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class AuthNegativeTest {

    private final AuthService authService = new AuthService();

    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() {
        return new Object[][] {
            { "invalidUser", "wrongPass", 401, "Invalid credentials" },
            { "", "Raj@1234", 400, "Username cannot be empty" },
            { "holkarmahesh", "", 400, "Password cannot be empty" }
        };
    }

    @Test(dataProvider = "invalidLoginData", description = "Verify Login API error handling with invalid credentials")
    public void verifyLoginWithInvalidCredentials(String username, String password, int expectedStatus, String expectedMessage) {
        LoginRequest payload = new LoginRequest(username, password);
        Response response = authService.login(payload);

        Assert.assertEquals(response.getStatusCode(), expectedStatus, "Status code mismatch for user: " + username);
        if (response.jsonPath().get("message") != null) {
            Assert.assertTrue(response.jsonPath().getString("message").contains(expectedMessage));
        }
    }

    @Test(description = "Verify Registration fails when email format is invalid")
    public void verifyRegistrationWithInvalidEmail() {
        SignUpRequest request = new SignUpRequest.Builder()
                .userName("user_test")
                .password("Test@123")
                .email("not-an-email-address")
                .firstName("Test")
                .lastName("User")
                .mobileNumber("9999999999")
                .build();

        Response response = authService.signUp(request);
        Assert.assertEquals(response.getStatusCode(), 400, "Expected HTTP 400 for bad email format");
    }
}