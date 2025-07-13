package com.api.tests;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.UserProfileManagementService;
import com.api.models.request.LoginRequest;
import com.api.models.response.LoginResponse;
import com.api.models.response.UserProfileResponse;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class GetProfileRequestTest {

	@Test(description = "Verifying get profile info API is working as expected.")
	public void getProfileInfoTest() {
		AuthService authService = new AuthService();
		Response response = authService.login(new LoginRequest("holkarmahesh","Raj@1234"));
		LoginResponse loginResponse = response.as(LoginResponse.class);
		System.out.println(loginResponse.getToken());
		
		
		UserProfileManagementService userProfileManagementService = new UserProfileManagementService();
		response= userProfileManagementService.getProfile(loginResponse.getToken());
//		System.out.println(response.asPrettyString());
		UserProfileResponse userProfileResponse = response.as(UserProfileResponse.class);
		System.out.println(userProfileResponse.getUsername());
		
	}
}
