package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.models.request.SignUpRequest;
import com.api.services.AuthService;
import com.api.utils.FakerDataFactory;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class AccountCreationTest {

    @Test(description = "Verify that the Create Account API registers new users successfully")
    public void createAccountTest() {
        SignUpRequest signUpRequest = FakerDataFactory.createRandomSignUpPayload();

        AuthService authService = new AuthService();
        Response response = authService.signUp(signUpRequest);

        Assert.assertEquals(response.getStatusCode(), 200, "Status code mismatch");
        JsonPath jsonPath = response.jsonPath();
        Assert.assertEquals(jsonPath.getString("message"), "User registered successfully!");
        Assert.assertNotNull(jsonPath.getString("email"));
    }
}