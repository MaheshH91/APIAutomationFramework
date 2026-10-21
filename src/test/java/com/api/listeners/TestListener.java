package com.api.listeners;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.api.reporting.ExtentManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);
    private static final ExtentReports extent = ExtentManager.getInstance();

    @Override
    public void onStart(ITestContext context) {
        log.info("Test Suite Started: {}", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();

        // Create test node with description if available
        ExtentTest test = (description != null && !description.isBlank())
                ? extent.createTest(testName, description)
                : extent.createTest(testName);

        // Assign author and groups / categories
        test.assignAuthor("SDET Automation Lead");
        String[] groups = result.getMethod().getGroups();
        if (groups != null && groups.length > 0) {
            test.assignCategory(groups);
        } else {
            test.assignCategory("Regression");
        }
        test.assignDevice(System.getProperty("os.name"));

        ExtentManager.setTest(test);
        log.info("Started Test: {}", testName);
        ExtentManager.getTest().log(Status.INFO, "Execution initiated for test: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("Passed Test: {}", result.getMethod().getMethodName());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.PASS, MarkupHelper.createLabel(result.getMethod().getMethodName() + " - PASSED", ExtentColor.GREEN));
        }
        ExtentManager.unload();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("Failed Test: {}", result.getMethod().getMethodName(), result.getThrowable());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, MarkupHelper.createLabel(result.getMethod().getMethodName() + " - FAILED", ExtentColor.RED));
            test.log(Status.FAIL, result.getThrowable());
        }
        ExtentManager.unload();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("Skipped Test: {}", result.getMethod().getMethodName());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, MarkupHelper.createLabel(result.getMethod().getMethodName() + " - SKIPPED", ExtentColor.ORANGE));
            if (result.getThrowable() != null) {
                test.log(Status.SKIP, result.getThrowable());
            }
        }
        ExtentManager.unload();
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("Test Suite Completed: {}", context.getName());
        if (extent != null) {
            extent.flush();
        }
    }
}