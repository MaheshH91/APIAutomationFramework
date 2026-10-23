package com.api.reporting;

import com.aventstack.extentreports.ExtentTest;

public final class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    private ExtentTestManager() {}

    public static ExtentTest getTest() {
        return testNode.get();
    }

    public static void setTest(ExtentTest test) {
        testNode.set(test);
    }

    public static void removeTest() {
        testNode.remove();
    }
}