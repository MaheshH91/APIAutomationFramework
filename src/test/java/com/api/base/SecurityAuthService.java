package com.api.base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class SecurityAuthService extends BaseService {

    // 1. Basic Auth Call
    public Response accessWithBasicAuth(String username, String password, boolean preemptive) {
        return getBasicAuthSpec(username, password, preemptive).get("/api/auth/basic");
    }

    // 2. Digest Auth Call
    public Response accessWithDigestAuth(String username, String password) {
        return getDigestAuthSpec(username, password).get("/api/auth/digest");
    }

    // 3. API Key via Header
    public Response accessWithApiKeyHeader(String apiKey) {
        return getApiKeySpec("X-API-KEY", apiKey, true).get("/api/auth/api-key-header");
    }

    // 4. API Key via Query Parameter
    public Response accessWithApiKeyQueryParam(String apiKey) {
        return getApiKeySpec("api_key", apiKey, false).get("/api/auth/api-key-query");
    }

    // 5. OAuth 2.0: Generate Token via Client Credentials Grant
    public Response requestOAuth2Token(String clientId, String clientSecret) {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "client_credentials")
                .formParam("client_id", clientId)
                .formParam("client_secret", clientSecret)
                .post("/api/oauth/token");
    }

    // 6. Access Resource with OAuth 2.0 Bearer Token
    public Response accessWithOAuth2(String accessToken) {
        return getOAuth2Spec(accessToken).get("/api/secure/oauth-resource");
    }

    // 7. Custom Signature / HMAC / AWS Style Header
    public Response accessWithCustomSignature(String signature, String timestamp) {
        return RestAssured.given()
                .spec(getRequestSpec(null))
                .header("X-Signature", signature)
                .header("X-Timestamp", timestamp)
                .get("/api/auth/signature");
    }
}