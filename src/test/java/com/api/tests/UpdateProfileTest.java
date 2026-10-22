package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.models.request.ProfileRequest;
import com.api.models.response.UserProfileResponse;
import com.api.services.UserProfileManagementService;
import com.api.utils.TokenManager;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class UpdateProfileTest {

    @Test(description = "Verify profile update flow")
    public void updateProfileInfoTest() {
        String token = TokenManager.getDefaultToken();
        UserProfileManagementService profileService = new UserProfileManagementService();

        ProfileRequest payload = new ProfileRequest.Builder()
                .firstName("Mayraji")
                .lastName("Holee")
                .email("holkarmahesh1@gmail.com")
                .mobileNumber("7777777772")
                .build();

        Response response = profileService.updateProfile(token, payload);

        Assert.assertEquals(response.getStatusCode(), 200);
        UserProfileResponse profileResponse = response.as(UserProfileResponse.class);
        Assert.assertEquals(profileResponse.getFirstName(), "Mayraji");
    }
}