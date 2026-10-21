package com.api.tests;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.annotations.*;

import com.api.base.AuthService;
import com.api.models.request.LoginRequest;
import com.api.models.response.LoginResponse;
import com.api.utils.ConfigManager;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public abstract class BaseTest {

    protected String jwtToken;
    protected AuthService authService;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteSetup() {
        // Disable Jansi globally (avoids console warnings)
        System.setProperty("log4j.skipJansi", "true");

        // Load base URL from config
        RestAssured.baseURI = ConfigManager.getBaseUrl();

        System.out.println(">>> [BEFORE SUITE] Initializing Global Test Suite & Environment Config");
        System.out.println("✅ Base URI configured: " + RestAssured.baseURI);
    }

    @BeforeTest(alwaysRun = true)
    @Parameters({ "environment" })
    public void beforeTestSetup(@Optional("dev") String environment, ITestContext context) {
        System.out.println(">>> [BEFORE TEST] Executing Test Tag: " + context.getName() + " on Env: " + environment);
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClassSetup() {
        authService = new AuthService();
        Response response = authService.login(new LoginRequest("holkarmahesh", "Raj@1234"));
        jwtToken = response.as(LoginResponse.class).getToken();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethodSetup(Method method) {
        System.out.println(">>> [START TEST METHOD]: " + method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethodTeardown(Method method) {
        System.out.println("<<< [COMPLETED TEST METHOD]: " + method.getName());
    }

    @AfterClass(alwaysRun = true)
    public void afterClassTeardown() {
        System.out.println("<<< [AFTER CLASS] Cleaning Class Context");
    }

    @AfterTest(alwaysRun = true)
    public void afterTestTeardown(ITestContext context) {
        System.out.println("<<< [AFTER TEST] Finished: " + context.getName());
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuiteTeardown() {
        System.out.println("<<< [AFTER SUITE] Flushing ExtentReports & Releasing Mock Server Hooks");
    }
}