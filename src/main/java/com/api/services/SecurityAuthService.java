package com.api.services;

import com.api.constants.Endpoints;
import com.api.constants.FrameworkConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class SecurityAuthService extends BaseService {

    public Response accessWithBasicAuth(String username, String password, boolean preemptive) {
        return getBasicAuthSpec(username, password, preemptive).get(Endpoints.AUTH_BASIC);
    }

    public Response accessWithDigestAuth(String username, String password) {
        return getDigestAuthSpec(username, password).get(Endpoints.AUTH_DIGEST);
    }

    public Response accessWithApiKeyHeader(String apiKey) {
        return getApiKeySpec(FrameworkConstants.HEADER_API_KEY, apiKey, true).get(Endpoints.AUTH_API_KEY_HEADER);
    }

    public Response accessWithApiKeyQueryParam(String apiKey) {
        return getApiKeySpec("api_key", apiKey, false).get(Endpoints.AUTH_API_KEY_QUERY);
    }

    public Response requestOAuth2Token(String clientId, String clientSecret) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "client_credentials")
                .formParam("client_id", clientId)
                .formParam("client_secret", clientSecret)
                .post(Endpoints.AUTH_OAUTH_TOKEN);
    }

    public Response accessWithOAuth2(String accessToken) {
        return getOAuth2Spec(accessToken).get(Endpoints.SECURE_OAUTH_RESOURCE);
    }

    public Response accessWithCustomSignature(String signature, String timestamp) {
        return RestAssured.given()
                .spec(getRequestSpec(null))
                .header(FrameworkConstants.HEADER_SIGNATURE, signature)
                .header(FrameworkConstants.HEADER_TIMESTAMP, timestamp)
                .get(Endpoints.AUTH_SIGNATURE);
    }
}