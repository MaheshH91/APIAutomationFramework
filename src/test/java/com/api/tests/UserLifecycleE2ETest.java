package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.models.request.LoginRequest;
import com.api.models.request.ProfileRequest;
import com.api.models.request.SignUpRequest;
import com.api.models.response.LoginResponse;
import com.api.models.response.UserProfileResponse;
import com.api.services.AuthService;
import com.api.services.UserProfileManagementService;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class UserLifecycleE2ETest {

    @Test(description = "E2E: Complete user journey from account creation to profile update")
    public void testUserLifecycleE2E() {
        String username = "holkarmahesh";
        String email = "holkarmahesh1@gmail.com";
        String password = "Raj@1234";

        AuthService authService = new AuthService();
        UserProfileManagementService profileService = new UserProfileManagementService();

        SignUpRequest signUpRequest = new SignUpRequest.Builder()
                .userName(username)
                .email(email)
                .firstName("Mahesh")
                .lastName("Holkar")
                .password(password)
                .mobileNumber("7777777772")
                .build();

        Response signUpRes = authService.signUp(signUpRequest);
        Assert.assertEquals(signUpRes.getStatusCode(), 200);

        Response loginRes = authService.login(new LoginRequest(username, password));
        Assert.assertEquals(loginRes.getStatusCode(), 200);
        String jwtToken = loginRes.as(LoginResponse.class).getToken();
        Assert.assertNotNull(jwtToken);

        Response getProfileRes = profileService.getProfile(jwtToken);
        Assert.assertEquals(getProfileRes.getStatusCode(), 200);

        ProfileRequest updatePayload = new ProfileRequest.Builder()
                .firstName("Mayraji")
                .lastName("Holee")
                .email(email)
                .mobileNumber("7777777772")
                .build();

        Response updateRes = profileService.updateProfile(jwtToken, updatePayload);
        Assert.assertEquals(updateRes.getStatusCode(), 200);
        UserProfileResponse updatedProfile = updateRes.as(UserProfileResponse.class);
        Assert.assertEquals(updatedProfile.getFirstName(), "Mayraji");
    }
}