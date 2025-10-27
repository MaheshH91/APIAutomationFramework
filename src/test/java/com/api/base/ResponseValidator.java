package com.api.base;

import org.testng.Assert;
import io.restassured.response.Response;

public class ResponseValidator {
    public static void validateStatus(Response response, int expected) {
        Assert.assertEquals(response.getStatusCode(), expected,
            "Expected HTTP status: " + expected + " but got: " + response.getStatusCode());
    }
    public static void validateField(Response response, String jsonPath, String expected) {
        String actual = response.jsonPath().getString(jsonPath);
        Assert.assertEquals(actual, expected, "Mismatch at path: " + jsonPath);
    }
    public static void validateContains(Response response, String jsonPath, String expectedSubstring) {
        String actual = response.jsonPath().getString(jsonPath);
        Assert.assertTrue(actual != null && actual.contains(expectedSubstring),
            "Expected '" + expectedSubstring + "' to be part of '" + actual + "'");
    }
}
