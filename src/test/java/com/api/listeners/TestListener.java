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
	private static ExtentReports extent;
	private static ExtentTest test;
	static {
		// Initialize ExtentReports
		extent = new ExtentReports();
		ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-reports/extentReport.html");
		extent.attachReporter(spark);
	}
	public void onStart(ITestContext context) {
		logger.info("Test Suite Started!!!");
		
	}

	@Override
	public void onFinish(ITestContext context) {
		logger.info("Test Suite Completed!!!");
	    extent.flush();
		// Optionally, you can close the ExtentReports instance if needed
	}
	@Override
	public void onTestStart(ITestResult result) {
		logger.info("Started!!"+result.getMethod().getMethodName());
		logger.info("Description!!"+result.getMethod().getDescription());
		test = extent.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
	}
	@Override
	public void onTestSuccess(ITestResult result) {
		logger.info("Passed!!"+result.getMethod().getMethodName());
		test.log(Status.PASS, "Test Passed");
		// Log the description of the test method
		test.log(Status.INFO, "Description: " + result.getMethod().getDescription());
		
//		logger.info("Description!!"+result.getMethod().getDescription());
	}

	@Override
	public void onTestFailure(ITestResult result) {
		
		logger.error("Failed!!"+result.getMethod().getMethodName());
		 test.log(Status.FAIL, "Test Failed: " + result.getThrowable());
		// Log the description of the test method
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		 test.log(Status.SKIP, "Test Skipped");
		logger.warn("Skipped!!"+result.getMethod().getMethodName());
	}

}
