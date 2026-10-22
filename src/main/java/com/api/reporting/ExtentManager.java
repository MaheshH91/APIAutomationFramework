package com.api.reporting;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.api.config.ConfigManager;
import com.api.constants.FrameworkConstants;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public final class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    private ExtentManager() {}

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            File directory = new File(FrameworkConstants.EXTENT_REPORT_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fullReportPath = FrameworkConstants.EXTENT_REPORT_DIR + FrameworkConstants.EXTENT_REPORT_FILE_NAME;
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(fullReportPath);

            sparkReporter.config().setDocumentTitle(FrameworkConstants.EXTENT_REPORT_TITLE);
            sparkReporter.config().setReportName(FrameworkConstants.EXTENT_REPORT_NAME);
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            sparkReporter.config().setEncoding("UTF-8");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            extent.setSystemInfo("Environment", ConfigManager.get("env", "qa").toUpperCase());
            extent.setSystemInfo("Base URL", ConfigManager.getBaseUrl());
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Execution User", System.getProperty("user.name"));
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