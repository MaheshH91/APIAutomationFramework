package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.api.constants.Endpoints;
import com.api.listeners.TestListener;
import com.api.services.BaseService;

import io.restassured.response.Response;

@Listeners(TestListener.class)
public class OAuth2TokenExchangeChainTest extends BaseService {

    private String extractedOAuthToken;
    private String tokenType;

    @Test(priority = 1, description = "Step 1: Exchange Client Credentials for OAuth2 Access Token")
    public void testExchangeCredentialsForToken() {
        String tokenPayload = """
            {
                "client_id": "client_enterprise_app",
                "client_secret": "secret_key_xyz987",
                "grant_type": "client_credentials"
            }
            """;

        Response response = getRequestSpec()
                .body(tokenPayload)
                .when()
                .post("/api/oauth/token");

        Assert.assertEquals(response.getStatusCode(), 200);

        extractedOAuthToken = response.jsonPath().getString("access_token");
        tokenType = response.jsonPath().getString("token_type");

        Assert.assertNotNull(extractedOAuthToken, "OAuth2 access_token must not be null");
        Assert.assertEquals(tokenType, "Bearer");
    }

    @Test(priority = 2, dependsOnMethods = "testExchangeCredentialsForToken", description = "Step 2: Access Protected Resource with Chained OAuth2 Token")
    public void testConsumeProtectedResourceWithOAuthToken() {
        Response response = getRequestSpec()
                .header("Authorization", tokenType + " " + extractedOAuthToken)
                .when()
                .get(Endpoints.AUTH_OAUTH_RESOURCE_ENDPOINT);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("scope"), "read:reports");
        Assert.assertEquals(response.jsonPath().getString("data"), "Confidential Report Data");
    }
}