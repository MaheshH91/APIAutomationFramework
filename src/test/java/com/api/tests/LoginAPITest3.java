package com.api.tests;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.AuthService;
import com.api.models.request.LoginRequest;

import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class LoginAPITest3 {

	@Test(description = "Verifying login api is working....")
	public void loginTest() {
		
		//AuthService authService= new AuthService();
		//Response response=authService.login("{\"username\":\"uday1234\",\"password\":\"uday1234\"}");
		//System.out.println(response.asPrettyString());
	}
}
