package com.api.listeners;

import com.api.constants.FrameworkConstants;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int counter = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess() && counter < FrameworkConstants.MAX_RETRY_COUNT) {
            counter++;
            return true;
        }
        return false;
    }
}