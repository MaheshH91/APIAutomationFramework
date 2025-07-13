package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
@Listeners(com.api.listeners.TestListener.class)
public class LoginTest {

    @Test(description = "Verifying login API is working....")
    public void loginTest() {
        Response response = given()
            .baseUri("http://64.227.160.186:8080")
            .basePath("/api/auth/login")
            .header("Content-type", "application/json")
            .body("{\"username\": \"holkarmahesh\", \"password\": \"Raj@1234\"}")
        .when()
            .post();

        System.out.println(response.asPrettyString());
        Assert.assertEquals(response.getStatusCode(), 200);
    }
}