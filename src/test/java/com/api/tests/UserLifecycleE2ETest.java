package com.api.tests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.UserProfileManagementService;
import com.api.models.request.LoginRequest;
import com.api.models.request.ProfileRequest;
import com.api.models.request.SignUpRequest;
import com.api.models.response.LoginResponse;
import com.api.models.response.UserProfileResponse;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class UserLifecycleE2ETest {

    @Test(description = "E2E: Complete user journey from account creation to profile update")
    public void testUserLifecycleE2E() {
        String uid = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String username = "holkarmahesh"; // Aligns with Mockoon's returned profile data
        String email = "holkarmahesh1@gmail.com";
        String password = "Raj@1234";

        AuthService authService = new AuthService();
        UserProfileManagementService profileService = new UserProfileManagementService();

        // 1. Sign Up
        SignUpRequest signUpRequest = new SignUpRequest.Builder()
                .userName(username)
                .email(email)
                .firstName("Mahesh")
                .lastName("Holkar")
                .password(password)
                .mobileNumber("7777777772")
                .build();

        Response signUpRes = authService.signUp(signUpRequest);
        Assert.assertEquals(signUpRes.getStatusCode(), 200, "Registration failed in E2E chain");

        // 2. Login
        Response loginRes = authService.login(new LoginRequest(username, password));
        Assert.assertEquals(loginRes.getStatusCode(), 200, "Login failed in E2E chain");
        LoginResponse loginData = loginRes.as(LoginResponse.class);
        String jwtToken = loginData.getToken();
        Assert.assertNotNull(jwtToken, "JWT Token must not be null");

        // 3. Get Initial Profile
        Response getProfileRes = profileService.getProfile(jwtToken);
        Assert.assertEquals(getProfileRes.getStatusCode(), 200, "Fetching profile failed");
        UserProfileResponse profileData = getProfileRes.as(UserProfileResponse.class);
        Assert.assertEquals(profileData.getUsername(), username);

        // 4. Update Profile
        ProfileRequest updatePayload = new ProfileRequest.Builder()
                .firstName("Mayraji")
                .lastName("Holee")
                .email(email)
                .mobileNumber("7777777772")
                .build();

        Response updateRes = profileService.updateProfile(jwtToken, updatePayload);
        Assert.assertEquals(updateRes.getStatusCode(), 200, "Updating profile failed");
        UserProfileResponse updatedProfile = updateRes.as(UserProfileResponse.class);
        Assert.assertEquals(updatedProfile.getFirstName(), "Mayraji", "First name did not update correctly");
    }
}