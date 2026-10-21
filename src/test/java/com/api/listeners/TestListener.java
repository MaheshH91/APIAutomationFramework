package com.api.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);
    private static final ExtentReports extent = new ExtentReports();
    private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    static {
        ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-reports/extentReport.html");
        spark.config().setReportName("API Test Automation Execution Report");
        extent.attachReporter(spark);
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Suite Started: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test Suite Completed: {}", context.getName());
        extent.flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Started Test: {}", result.getMethod().getMethodName());
        ExtentTest test = extent.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        testNode.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Passed Test: {}", result.getMethod().getMethodName());
        testNode.get().log(Status.PASS, "Test Passed");
        testNode.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Failed Test: {}", result.getMethod().getMethodName(), result.getThrowable());
        testNode.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());
        testNode.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Skipped Test: {}", result.getMethod().getMethodName());
        if (testNode.get() != null) {
            testNode.get().log(Status.SKIP, "Test Skipped: " + result.getThrowable());
            testNode.remove();
        }
    }
}