package com.api.services;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import com.api.config.ConfigManager;
import com.api.constants.FrameworkConstants;
import com.api.filters.LoggingFilter;

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

    protected RequestSpecification getRequestSpec(String token) {
        RequestSpecification spec = RestAssured.given().spec(BASE_SPEC);
        if (token != null && !token.isBlank()) {
            spec.header(FrameworkConstants.HEADER_AUTHORIZATION, FrameworkConstants.HEADER_BEARER_PREFIX + token);
        }
        return spec;
    }

    protected RequestSpecification getBasicAuthSpec(String username, String password, boolean preemptive) {
        RequestSpecification spec = RestAssured.given().spec(BASE_SPEC);
        return preemptive ? spec.auth().preemptive().basic(username, password)
                          : spec.auth().basic(username, password);
    }

    protected RequestSpecification getDigestAuthSpec(String username, String password) {
        return RestAssured.given().spec(BASE_SPEC).auth().digest(username, password);
    }

    protected RequestSpecification getApiKeySpec(String keyName, String keyValue, boolean inHeader) {
        RequestSpecification spec = RestAssured.given().spec(BASE_SPEC);
        return inHeader ? spec.header(keyName, keyValue) : spec.queryParam(keyName, keyValue);
    }

    protected RequestSpecification getOAuth2Spec(String accessToken) {
        return RestAssured.given().spec(BASE_SPEC).auth().oauth2(accessToken);
    }

    // Public calls
    protected Response getRequest(String endpoint) {
        return getRequest(endpoint, Collections.emptyMap(), null);
    }

    protected Response postRequest(Object payload, String endpoint) {
        return postRequest(payload, endpoint, null);
    }

    protected Response putRequest(Object payload, String endpoint) {
        return putRequest(payload, endpoint, null);
    }

    protected Response deleteRequest(String endpoint) {
        return deleteRequest(endpoint, null);
    }

    // Authenticated calls
    protected Response getRequest(String endpoint, String token) {
        return getRequest(endpoint, Collections.emptyMap(), token);
    }

    protected Response getRequest(String endpoint, Map<String, ?> queryParams, String token) {
        return getRequestSpec(token).queryParams(queryParams).get(endpoint);
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
            spec.header(FrameworkConstants.HEADER_AUTHORIZATION, FrameworkConstants.HEADER_BEARER_PREFIX + token);
        }
        return spec.post(endpoint);
    }
}