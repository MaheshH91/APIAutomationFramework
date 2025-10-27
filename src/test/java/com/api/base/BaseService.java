package com.api.base;

import com.api.filters.LoggingFilter;
import com.api.utils.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseService {
    private static final String BASE_URL = ConfigManager.getBaseUrl();
    private final RequestSpecification requestSpec;

    static {
        // Disable Jansi before log4j initialization
        System.setProperty("log4j.skipJansi", "true");
        RestAssured.filters(new LoggingFilter());
    }

    public BaseService() {
        this.requestSpec = RestAssured.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON);
    }

    protected void setAuthToken(String token) {
        requestSpec.header("Authorization", "Bearer " + token);
    }

    protected Response getRequest(String endpoint) {
        return requestSpec.get(endpoint);
    }

    protected Response postRequest(Object payload, String endpoint) {
        return requestSpec.body(payload).post(endpoint);
    }

    protected Response putRequest(Object payload, String endpoint) {
        return requestSpec.body(payload).put(endpoint);
    }
}
