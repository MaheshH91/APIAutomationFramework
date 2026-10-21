package com.api.base;

import java.io.File;
import java.util.Map;

import com.api.filters.LoggingFilter;
import com.api.utils.ConfigManager;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class BaseService {
    private static final String BASE_URL = ConfigManager.getBaseUrl();
    private static final RequestSpecification BASE_SPEC;

    static {
        System.setProperty("log4j.skipJansi", "true");
        BASE_SPEC = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new LoggingFilter())
                .build();
    }

    private RequestSpecification getRequestSpec(String token) {
        RequestSpecification spec = RestAssured.given().spec(BASE_SPEC);
        if (token != null && !token.isBlank()) {
            spec.header("Authorization", "Bearer " + token);
        }
        return spec;
    }

    protected Response getRequest(String endpoint, String token) {
        return getRequestSpec(token).get(endpoint);
    }

    protected Response getRequestWithQueryParams(String endpoint, Map<String, ?> queryParams, String token) {
        return getRequestSpec(token).queryParams(queryParams).get(endpoint);
    }

    protected Response postRequest(Object payload, String endpoint) {
        return getRequestSpec(null).body(payload).post(endpoint);
    }

    protected Response postRequest(Object payload, String endpoint, String token) {
        return getRequestSpec(token).body(payload).post(endpoint);
    }

    protected Response putRequest(Object payload, String endpoint, String token) {
        return getRequestSpec(token).body(payload).put(endpoint);
    }

    protected Response patchRequest(Object payload, String endpoint, String token) {
        return getRequestSpec(token).body(payload).patch(endpoint);
    }

    protected Response deleteRequest(String endpoint, String token) {
        return getRequestSpec(token).delete(endpoint);
    }

    protected Response uploadFile(String endpoint, File file, String token) {
        RequestSpecification spec = RestAssured.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.MULTIPART)
                .multiPart("file", file)
                .filter(new LoggingFilter());

        if (token != null && !token.isBlank()) {
            spec.header("Authorization", "Bearer " + token);
        }

        return spec.post(endpoint);
    }
}