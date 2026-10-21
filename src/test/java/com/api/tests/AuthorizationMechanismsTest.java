package com.api.tests;

import java.time.Instant;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.base.SecurityAuthService;
import io.restassured.response.Response;

@Listeners(com.api.listeners.TestListener.class)
public class AuthorizationMechanismsTest {

    private SecurityAuthService securityAuthService;

    @BeforeClass
    public void setup() {
        securityAuthService = new SecurityAuthService();
    }

    // --- 1. Basic Auth Tests ---
    @Test(description = "Verify preemptive basic authentication with valid credentials", groups = {"security"})
    public void testPreemptiveBasicAuthSuccess() {
        Response response = securityAuthService.accessWithBasicAuth("admin", "admin123", true);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("authType"), "BASIC_PREEMPTIVE");
    }

    @Test(description = "Verify basic authentication rejection with invalid credentials", groups = {"security"})
    public void testBasicAuthInvalidCredentials() {
        Response response = securityAuthService.accessWithBasicAuth("admin", "wrongPass", false);
        Assert.assertEquals(response.getStatusCode(), 401);
    }

    // --- 2. Digest Auth Tests ---
    @Test(description = "Verify digest authentication challenge handshake", groups = {"security"})
    public void testDigestAuthSuccess() {
        Response response = securityAuthService.accessWithDigestAuth("digestUser", "secret123");
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "AUTHENTICATED_DIGEST");
    }

    // --- 3. API Key Tests ---
    @Test(description = "Verify access using API Key in custom header", groups = {"security"})
    public void testApiKeyInHeaderSuccess() {
        Response response = securityAuthService.accessWithApiKeyHeader("valid-secret-api-key-xyz");
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("message"), "API Key authorized via header");
    }

    @Test(description = "Verify access rejection with invalid API Key header", groups = {"security"})
    public void testApiKeyInHeaderUnauthorized() {
        Response response = securityAuthService.accessWithApiKeyHeader("invalid-key");
        Assert.assertEquals(response.getStatusCode(), 401);
    }

    @Test(description = "Verify access using API Key in query parameter", groups = {"security"})
    public void testApiKeyInQueryParamSuccess() {
        Response response = securityAuthService.accessWithApiKeyQueryParam("valid-secret-query-key-123");
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("message"), "API Key authorized via query parameter");
    }

    // --- 4. OAuth 2.0 Client Credentials Grant & Access ---
    @Test(description = "Verify OAuth 2.0 token generation and protected resource consumption", groups = {"security"})
    public void testOAuth2ClientCredentialsFlow() {
        // Step A: Request Token
        Response tokenResponse = securityAuthService.requestOAuth2Token("client_id_qa", "client_secret_qa");
        Assert.assertEquals(tokenResponse.getStatusCode(), 200);
        String accessToken = tokenResponse.jsonPath().getString("access_token");
        Assert.assertNotNull(accessToken);

        // Step B: Access Protected Resource
        Response resourceResponse = securityAuthService.accessWithOAuth2(accessToken);
        Assert.assertEquals(resourceResponse.getStatusCode(), 200);
        Assert.assertEquals(resourceResponse.jsonPath().getString("scope"), "read:reports");
    }

    // --- 5. Signature / HMAC Header Validation ---
    @Test(description = "Verify HMAC / timestamped signature authentication", groups = {"security"})
    public void testCustomSignatureAuth() {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String dummySignature = "sha256-mocked-signature-hash";

        Response response = securityAuthService.accessWithCustomSignature(dummySignature, timestamp);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("message"), "Signature verified successfully");
    }
}