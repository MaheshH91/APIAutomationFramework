package com.api.base;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.RestAssured;

public class RequestBuilder {
    private final RequestSpecification request;

    public RequestBuilder(String baseUri) {
        this.request = RestAssured.given().baseUri(baseUri).contentType(ContentType.JSON);
    }

    public RequestBuilder withAuth(String token) {
        request.header("Authorization", "Bearer " + token);
        return this;
    }

    public RequestBuilder withBody(Object body) {
        request.body(body);
        return this;
    }

    public Response post(String endpoint) { return request.post(endpoint); }
    public Response get(String endpoint) { return request.get(endpoint); }
    public Response put(String endpoint) { return request.put(endpoint); }
    public Response delete(String endpoint) { return request.delete(endpoint); }
}
