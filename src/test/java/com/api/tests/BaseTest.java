package com.api.tests;

import org.testng.annotations.BeforeSuite;
import com.api.utils.ConfigManager;
import io.restassured.RestAssured;

public class BaseTest {

    @BeforeSuite
    public void beforeSuiteSetup() {
        // Disable Jansi globally (avoids console warnings)
        System.setProperty("log4j.skipJansi", "true");

        // Load base URL from config
        RestAssured.baseURI = ConfigManager.get("base.url");

        System.out.println("✅ Global setup completed for API Test Suite");
    }
}
