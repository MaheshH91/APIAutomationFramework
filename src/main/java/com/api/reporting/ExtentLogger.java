package com.api.reporting;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public final class ExtentLogger {

    private ExtentLogger() {}

    public static void info(String message) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.INFO, message);
        }
    }

    public static void pass(String message) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.PASS, message);
        }
    }

    public static void fail(String message) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.FAIL, message);
        }
    }

    public static void logJson(String title, String jsonContent) {
        if (ExtentManager.getTest() != null) {
            if (jsonContent != null && !jsonContent.isBlank()) {
                ExtentManager.getTest().log(Status.INFO, title + ":");
                ExtentManager.getTest().log(Status.INFO, MarkupHelper.createCodeBlock(jsonContent, CodeLanguage.JSON));
            } else {
                ExtentManager.getTest().log(Status.INFO, title + ": [Empty]");
            }
        }
    }

    public static void logResponse(int statusCode, String responseBody) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.INFO, "Response Status Code: <b>" + statusCode + "</b>");
            logJson("Response Body", responseBody);
        }
    }
}