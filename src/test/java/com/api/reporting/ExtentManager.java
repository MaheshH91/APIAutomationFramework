package com.api.reporting;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    private static final String REPORT_DIR = System.getProperty("user.dir") + "/target/extent-reports/";
    private static final String REPORT_FILE_NAME = "extentReport.html";

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            File directory = new File(REPORT_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_DIR + REPORT_FILE_NAME);
            sparkReporter.config().setDocumentTitle("API Automation Execution Report");
            sparkReporter.config().setReportName("REST Assured API Test Results");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            sparkReporter.config().setEncoding("UTF-8");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // System & Environment Information
            extent.setSystemInfo("Environment", "QA / Mockoon");
            extent.setSystemInfo("Base URL", "http://localhost:8089");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("User Name", System.getProperty("user.name"));
            extent.setSystemInfo("Execution Timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
        }
        return extent;
    }

    public static ExtentTest getTest() {
        return extentTestThreadLocal.get();
    }

    public static void setTest(ExtentTest test) {
        extentTestThreadLocal.set(test);
    }

    public static void unload() {
        extentTestThreadLocal.remove();
    }
}