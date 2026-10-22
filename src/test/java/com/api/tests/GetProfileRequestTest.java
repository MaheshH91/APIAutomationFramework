package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.models.response.UserProfileResponse;
import com.api.services.UserProfileManagementService;
import com.api.utils.TokenManager;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class GetProfileRequestTest {

    @Test(description = "Verify get profile info API works as expected")
    public void getProfileInfoTest() {
        String token = TokenManager.getDefaultToken();
        UserProfileManagementService profileService = new UserProfileManagementService();
        Response response = profileService.getProfile(token);

        Assert.assertEquals(response.getStatusCode(), 200);
        UserProfileResponse profile = response.as(UserProfileResponse.class);
        Assert.assertEquals(profile.getUsername(), "holkarmahesh");
    }
}