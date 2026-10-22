package com.api.utils;

import java.lang.reflect.Method;
import org.testng.annotations.DataProvider;

public final class TestDataProvider {

    private TestDataProvider() {}

    @DataProvider(name = "authCredentialsData", parallel = true)
    public static Object[][] getAuthCredentials() {
        return new Object[][] {
            { "invalidUser", "wrongPass", 401 },
            { "", "Raj@1234", 400 },
            { "holkarmahesh", "", 400 }
        };
    }

    @DataProvider(name = "dynamicOrderData")
    public static Object[][] getDynamicOrderData(Method method) {
        if ("testHighPriorityOrder".equals(method.getName())) {
            return new Object[][] { { 101, "Laptop", 1200.00 } };
        }
        return new Object[][] { { 102, "Mouse", 25.50 } };
    }
}