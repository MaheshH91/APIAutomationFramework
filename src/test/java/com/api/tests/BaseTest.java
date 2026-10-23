package com.api.tests;

import java.lang.reflect.Method;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.api.config.ConfigManager;
import com.api.services.AuthService;
import com.api.utils.TokenManager;
import io.restassured.RestAssured;

public abstract class BaseTest {

    protected String jwtToken;
    protected AuthService authService;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteSetup() {
        System.setProperty("log4j.skipJansi", "true");
        RestAssured.baseURI = ConfigManager.getBaseUrl();
    }

    @BeforeTest(alwaysRun = true)
    @Parameters({ "environment" })
    public void beforeTestSetup(@Optional("dev") String environment, ITestContext context) {
        // Test context tagging logic
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClassSetup() {
        authService = new AuthService();
        jwtToken = TokenManager.getDefaultToken();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethodSetup(Method method) {
        // Method tracking
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethodTeardown(Method method) {
        // Cleanup per method
    }

    @AfterClass(alwaysRun = true)
    public void afterClassTeardown() {
        // Context teardown
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuiteTeardown() {
        TokenManager.clearTokenCache();
    }
    @AfterSuite(alwaysRun = true)
    public void globalSuiteTeardown() {
        RestAssured.reset();
        TokenManager.clearCache();
    }
}