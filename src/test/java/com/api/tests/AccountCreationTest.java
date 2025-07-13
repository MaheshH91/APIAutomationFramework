package com.api.tests;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.models.request.SignUpRequest;

import io.restassured.response.Response;
@Listeners(com.api.listeners.TestListener.class)
public class AccountCreationTest {
	@Test(description = "Verifying create acccount api is working....")
	public void createAccountTest() {
		// Generate time-based suffix
		String timeStamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());


		SignUpRequest signUpRequest = new SignUpRequest.Builder()
				.userName("Moksha1"+timeStamp)
				.email("moksha"+timeStamp+"@yahoo.com")
				.firstName("Moksha")
				.password("moksha123")
				.lastName("Bhatkar")
				.mobileNumber("7777777776")
				.build();

		AuthService authService = new AuthService();
		Response response = authService.signUp(signUpRequest);
		System.out.println(response.asPrettyString());
		
		Assert.assertEquals(response.asPrettyString(),  "User registered successfully!");
		Assert.assertEquals(response.statusCode(),200);
	}
}
