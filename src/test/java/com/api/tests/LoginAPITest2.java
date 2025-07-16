package com.api.tests;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import io.restassured.response.Response;
@Listeners(com.api.listeners.TestListener.class)
public class LoginAPITest2 {

	@Test(description = "Verifying login api is working....")
	public void loginTest() {
		
		Response response = given().baseUri("http://64.227.160.186:8080")

				.header("Content-Type", "application/json")
				.body("{\"username\": \"holkarmahesh\",\"password\": \"Raj@1234\"}").post("/api/auth/login");
		System.out.println(response.asPrettyString());

		Assert.assertEquals(response.getStatusCode(), 200, "Status code is not 200");
	}
}
