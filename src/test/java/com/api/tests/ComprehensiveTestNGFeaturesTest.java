package com.api.tests;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.api.base.OrderService;
import com.api.base.UserProfileManagementService;
import com.api.models.request.LoginRequest;
import com.api.models.response.UserProfileResponse;
import com.api.utils.TestDataProvider;

import io.restassured.response.Response;

public class ComprehensiveTestNGFeaturesTest extends BaseTest {

    private final OrderService orderService = new OrderService();
    private final UserProfileManagementService profileService = new UserProfileManagementService();

    // 1. TestNG XML Parameters
    @Test(groups = { "smoke", "config" }, description = "Verify XML parameter injection")
    @Parameters({ "environment", "defaultTimeout" })
    public void testXmlParameters(String env, String timeout) {
        Assert.assertNotNull(env, "XML Environment parameter should not be null");
        Assert.assertNotNull(timeout, "XML Timeout parameter should not be null");
        System.out.println("Injected XML Params: Env = " + env + ", Timeout = " + timeout);
    }

    // 2. Parallel External DataProvider
    @Test(dataProvider = "authCredentialsData", dataProviderClass = TestDataProvider.class, groups = { "regression" }, description = "Verify external parallel DataProvider execution")
    public void testNegativeAuthWithParallelDataProvider(String username, String password, int expectedStatus) {
        Response response = authService.login(new LoginRequest(username, password));
        Assert.assertEquals(response.getStatusCode(), expectedStatus);
    }

    // 3. Dynamic Method-Aware DataProvider
    @Test(dataProvider = "dynamicOrderData", dataProviderClass = TestDataProvider.class, groups = { "regression" })
    public void testHighPriorityOrder(int orderId, String item, double amount) {
        Response response = orderService.getOrderById(orderId, jwtToken);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("orderId"), orderId);
    }

    // 4. Method Dependency & Priority
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

    // 5. Invocation Count & Thread Pool (Stress / Concurrency Simulation)
    @Test(invocationCount = 3, threadPoolSize = 2, groups = { "performance" }, description = "Simulate concurrent requests via invocationCount & threadPoolSize")
    public void testConcurrentProfileRequests() {
        long threadId = Thread.currentThread().threadId();
        Response response = profileService.getProfile(jwtToken);
        Assert.assertEquals(response.getStatusCode(), 200);
        System.out.println("Thread ID [" + threadId + "] successfully fetched profile.");
    }

    // 6. Expected Exception Validation
    @Test(expectedExceptions = { IllegalArgumentException.class }, groups = { "unit" }, description = "Verify expected exception handling")
    public void testExpectedExceptionAssertion() {
        throw new IllegalArgumentException("Deliberate exception for TestNG contract validation");
    }
}