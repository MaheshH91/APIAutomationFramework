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
    protected static final String BASE_URL = ConfigManager.getBaseUrl();
    private static final RequestSpecification BASE_SPEC;

    static {
        System.setProperty("log4j.skipJansi", "true");
        BASE_SPEC = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new LoggingFilter())
                .build();
    }

    // 1. Standard / Bearer Token Request Specification
    protected RequestSpecification getRequestSpec(String token) {
        RequestSpecification spec = RestAssured.given().spec(BASE_SPEC);
        if (token != null && !token.isBlank()) {
            spec.header("Authorization", "Bearer " + token);
        }
        return spec;
    }

    // 2. Basic Authentication (Challenged & Preemptive)
    protected RequestSpecification getBasicAuthSpec(String username, String password, boolean preemptive) {
        RequestSpecification spec = RestAssured.given().spec(BASE_SPEC);
        if (preemptive) {
            return spec.auth().preemptive().basic(username, password);
        }
        return spec.auth().basic(username, password);
    }

    // 3. Digest Authentication
    protected RequestSpecification getDigestAuthSpec(String username, String password) {
        return RestAssured.given().spec(BASE_SPEC).auth().digest(username, password);
    }

    // 4. API Key Authentication (Via Header or Query Parameter)
    protected RequestSpecification getApiKeySpec(String keyName, String keyValue, boolean inHeader) {
        RequestSpecification spec = RestAssured.given().spec(BASE_SPEC);
        if (inHeader) {
            return spec.header(keyName, keyValue);
        }
        return spec.queryParam(keyName, keyValue);
    }

    // 5. OAuth 2.0 Token Injection
    protected RequestSpecification getOAuth2Spec(String accessToken) {
        return RestAssured.given().spec(BASE_SPEC).auth().oauth2(accessToken);
    }

    // --- Core HTTP Operations ---
    protected Response getRequest(String endpoint, String token) {
        return getRequestSpec(token).get(endpoint);
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
    
    
 // --- Overloaded 2-argument methods for public endpoints (no token) ---
    protected Response postRequest(Object payload, String endpoint) {
        return postRequest(payload, endpoint, null);
    }

    protected Response getRequest(String endpoint) {
        return getRequest(endpoint, null);
    }

    protected Response putRequest(Object payload, String endpoint) {
        return putRequest(payload, endpoint, null);
    }

    protected Response deleteRequest(String endpoint) {
        return deleteRequest(endpoint, null);
    }
    
 // Query Parameters Support (With Token)
    protected Response getRequestWithQueryParams(String endpoint, Map<String, ?> queryParams, String token) {
        return getRequestSpec(token).queryParams(queryParams).get(endpoint);
    }

    // Overload for Query Parameters without Token (Public endpoints)
    protected Response getRequestWithQueryParams(String endpoint, Map<String, ?> queryParams) {
        return getRequestWithQueryParams(endpoint, queryParams, null);
    }
}