package com.api.tests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.models.request.SignUpRequest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class AccountCreationTest {

    @Test(description = "Verify that the Create Account API registers new users successfully")
    public void createAccountTest() {

        // Generate unique timestamp to avoid duplicate usernames/emails
        String timeStamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());

        // Build sign-up payload dynamically
        SignUpRequest signUpRequest = new SignUpRequest.Builder()
                .userName("moksha" + timeStamp)
                .email("moksha" + timeStamp + "@yahoo.com")
                .firstName("Moksha")
                .lastName("Bhatkar")
                .password("moksha123")
                .mobileNumber("77777" + timeStamp.substring(6)) // generates semi-unique mobile number
                .build();

        // Call the signup endpoint
        AuthService authService = new AuthService();
        Response response = authService.signUp(signUpRequest);

        // Print formatted response for debugging
        System.out.println("Response Body:\n" + response.asPrettyString());

        // Validate HTTP status code
        Assert.assertEquals(response.getStatusCode(), 200, "Status code mismatch");

        // Extract JSON message safely
        JsonPath jsonPath = response.jsonPath();
        String message = jsonPath.getString("message");

        // Validate response message
        Assert.assertEquals(message, "User registered successfully!", "Unexpected signup message");

        // Optional: additional checks (if API returns user ID / email)
        String email = jsonPath.getString("email");
        Assert.assertTrue(email.contains("@yahoo.com"), "Email not returned as expected");
    }
}
