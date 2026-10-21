package com.api.utils;

import java.lang.reflect.Method;
import org.testng.annotations.DataProvider;

public class TestDataProvider {

    @DataProvider(name = "authCredentialsData", parallel = true)
    public static Object[][] getAuthCredentials() {
        return new Object[][] {
            { "invalidUser", "wrongPass", 401 },
            { "", "Raj@1234", 400 },
            { "holkarmahesh", "", 400 }
        };
    }

    // Method-aware dynamic DataProvider: feeds different data based on caller test method
    @DataProvider(name = "dynamicOrderData")
    public static Object[][] getDynamicOrderData(Method method) {
        if (method.getName().equals("testHighPriorityOrder")) {
            return new Object[][] { { 101, "Laptop", 1200.00 } };
        } else {
            return new Object[][] { { 102, "Mouse", 25.50 } };
        }
    }
}