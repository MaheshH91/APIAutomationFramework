package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.base.UserProfileManagementService;
import com.api.models.request.LoginRequest;
import com.api.models.request.ProfileRequest;
import com.api.models.response.LoginResponse;
import com.api.models.response.UserProfileResponse;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class UpdateProfileTest {

	@Test
	public void updateProfileInfoTest() {

		AuthService authService = new AuthService();
		Response response = authService.login(new LoginRequest("holkarmahesh", "Raj@1234"));
		LoginResponse loginResponse = response.as(LoginResponse.class);
		System.out.println(response.asPrettyString());

		System.out.println("------------------");

		UserProfileManagementService userProfileManagementService = new UserProfileManagementService();
		response = userProfileManagementService.getProfile(loginResponse.getToken());
		System.out.println(response.asPrettyString());
		UserProfileResponse userProfileResponse = response.as(UserProfileResponse.class);
		Assert.assertEquals(userProfileResponse.getUsername(), "holkarmahesh");
		
		System.out.println("-------------------");
		ProfileRequest profileRequest= new ProfileRequest.Builder()
											.firstName("Mayraji")
											.lastName("Holee")
											.email("holkarmahesh1@gmail.com")
											.mobileNumber("7777777772")
											.build();
		response= userProfileManagementService.updateProfile(loginResponse.getToken(),profileRequest);
		System.out.println(response.asPrettyString());
	}
}
