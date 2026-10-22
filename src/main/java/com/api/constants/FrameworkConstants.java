package com.api.constants;

import java.time.Duration;

public final class FrameworkConstants {
    private FrameworkConstants() {}

    private static final String USER_DIR = System.getProperty("user.dir");
    public static final String SRC_TEST_RESOURCES_PATH = USER_DIR + "/src/test/resources";
    public static final String CONFIG_FILE_PATH = SRC_TEST_RESOURCES_PATH + "/config.properties";
    public static final String SCHEMAS_PATH = SRC_TEST_RESOURCES_PATH + "/schemas";

    public static final String EXTENT_REPORT_DIR = USER_DIR + "/target/extent-reports/";
    public static final String EXTENT_REPORT_FILE_NAME = "extentReport.html";
    public static final String EXTENT_REPORT_TITLE = "Enterprise API Automation Report";
    public static final String EXTENT_REPORT_NAME = "REST Assured Test Execution Results";

    public static final String LOGS_DIR = USER_DIR + "/logs/";
    public static final String APPLICATION_LOG_FILE = LOGS_DIR + "test.log";

    public static final long DEFAULT_TIMEOUT_MS = 5000L;
    public static final long RESPONSE_TIME_SLA_MS = 2000L;
    public static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration READ_TIMEOUT = Duration.ofSeconds(10);

    public static final int MAX_RETRY_COUNT = 1;

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_BEARER_PREFIX = "Bearer ";
    public static final String HEADER_API_KEY = "X-API-KEY";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_SIGNATURE = "X-Signature";
    public static final String HEADER_TIMESTAMP = "X-Timestamp";
}