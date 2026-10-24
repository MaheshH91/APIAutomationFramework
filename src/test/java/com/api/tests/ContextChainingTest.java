package com.api.tests;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.listeners.TestListener;
import com.api.models.request.SignUpRequest;
import com.api.services.AuthService;
import com.api.services.UserProfileManagementService;

import io.restassured.response.Response;

@Listeners(TestListener.class)
public class ContextChainingTest {

    @Test(priority = 1, description = "Producer Step: Register User and Store Details in ITestContext")
    public void step1_RegisterUser(ITestContext context) {
        long timestamp = System.currentTimeMillis();
        String generatedUser = "user_" + timestamp;
        String generatedEmail = "test." + timestamp + "@mocktest.com";

        AuthService authService = new AuthService();
        SignUpRequest payload = new SignUpRequest.Builder()
                .userName(generatedUser)
                .password("SecurePass@123")
                .email(generatedEmail)
                .firstName("Test")
                .lastName("Automation")
                .mobileNumber("9876543210")
                .build();

        Response response = authService.signUp(payload);
        Assert.assertEquals(response.getStatusCode(), 200);

        // Chain data dynamically into suite execution context
        context.setAttribute("CHAINED_USER", generatedUser);
        context.setAttribute("CHAINED_EMAIL", generatedEmail);
    }

    @Test(priority = 2, dependsOnMethods = "step1_RegisterUser", description = "Consumer Step: Retrieve Profile Using Injected Context Variables")
    public void step2_VerifyRegisteredUser(ITestContext context) {
        String chainedUser = (String) context.getAttribute("CHAINED_USER");
        String chainedEmail = (String) context.getAttribute("CHAINED_EMAIL");

        Assert.assertNotNull(chainedUser, "Chained username should exist in test context");
        Assert.assertNotNull(chainedEmail, "Chained email should exist in test context");

        UserProfileManagementService profileService = new UserProfileManagementService();
        Response response = profileService.getProfile("mocked-jwt-token-xyz123");

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("username"), "holkarmahesh");
    }
}