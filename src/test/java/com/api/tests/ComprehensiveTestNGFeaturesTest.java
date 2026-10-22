package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.api.models.request.LoginRequest;
import com.api.models.response.UserProfileResponse;
import com.api.services.OrderService;
import com.api.services.UserProfileManagementService;
import com.api.utils.TestDataProvider;

import io.restassured.response.Response;

public class ComprehensiveTestNGFeaturesTest extends BaseTest {

    private final OrderService orderService = new OrderService();
    private final UserProfileManagementService profileService = new UserProfileManagementService();

    @Test(groups = { "smoke", "config" }, description = "Verify XML parameter injection")
    @Parameters({ "environment", "defaultTimeout" })
    public void testXmlParameters(String env, String timeout) {
        Assert.assertNotNull(env);
        Assert.assertNotNull(timeout);
    }

    @Test(dataProvider = "authCredentialsData", dataProviderClass = TestDataProvider.class, groups = { "regression" })
    public void testNegativeAuthWithParallelDataProvider(String username, String password, int expectedStatus) {
        Response response = authService.login(new LoginRequest(username, password));
        Assert.assertEquals(response.getStatusCode(), expectedStatus);
    }

    @Test(dataProvider = "dynamicOrderData", dataProviderClass = TestDataProvider.class, groups = { "regression" })
    public void testHighPriorityOrder(int orderId, String item, double amount) {
        Response response = orderService.getOrderById(orderId, jwtToken);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("orderId"), orderId);
    }

    @Test(groups = { "smoke", "dependency" }, priority = 1)
    public void parentCreateStep() {
        Response response = profileService.getProfile(jwtToken);
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = { "parentCreateStep" }, groups = { "smoke", "dependency" }, priority = 2)
    public void dependentVerifyStep() {
        Response response = profileService.getProfile(jwtToken);
        UserProfileResponse profile = response.as(UserProfileResponse.class);
        Assert.assertEquals(profile.getUsername(), "holkarmahesh");
    }

    @Test(invocationCount = 3, threadPoolSize = 2, groups = { "performance" })
    public void testConcurrentProfileRequests() {
        Response response = profileService.getProfile(jwtToken);
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = { "unit" })
    public void testExpectedExceptionAssertion() {
        throw new IllegalArgumentException("Deliberate contract assertion exception");
    }
}