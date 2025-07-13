package com.api.tests;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class ForgotPasswordTest {
	@Test(description = "Verifying Forgot Password acccount api is working....")
	public void forgotPasswordTest() {

		AuthService authService = new AuthService();
		Response response=authService.forgotPassword("holkarmahesh1@gmail.com");
		System.out.println(response.asPrettyString());
		
		
	}
}
