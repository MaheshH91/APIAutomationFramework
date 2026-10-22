package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.models.request.ProfileRequest;
import com.api.services.UserProfileManagementService;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class UserProfileSecurityTest {

    private final UserProfileManagementService profileService = new UserProfileManagementService();

    @Test(description = "Verify GET profile fails when authorization token is omitted")
    public void verifyGetProfileWithoutToken() {
        Response response = profileService.getProfile("");
        Assert.assertEquals(response.getStatusCode(), 401);
    }

    @Test(description = "Verify GET profile fails when invalid token is provided")
    public void verifyGetProfileWithInvalidToken() {
        Response response = profileService.getProfile("malformed_or_expired_jwt_token_123");
        Assert.assertEquals(response.getStatusCode(), 401);
    }

    @Test(description = "Verify PUT profile fails without valid authentication")
    public void verifyUpdateProfileUnauthorized() {
        ProfileRequest updateBody = new ProfileRequest.Builder()
                .firstName("HackedName")
                .lastName("HackedLast")
                .email("hacked@email.com")
                .mobileNumber("1111111111")
                .build();

        Response response = profileService.updateProfile("invalid_token", updateBody);
        Assert.assertEquals(response.getStatusCode(), 401);
    }
}