package com.api.filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.api.reporting.ExtentLogger;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class LoggingFilter implements Filter {

    private static final Logger log = LogManager.getLogger(LoggingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        String method = requestSpec.getMethod();
        String uri = requestSpec.getURI();
        String requestBody = requestSpec.getBody() != null ? requestSpec.getBody().toString() : "";

        // Log to Log4j2
        log.info("Request: {} {}", method, uri);
        if (!requestBody.isBlank()) {
            log.info("Request Body: {}", requestBody);
        }

        // Log to ExtentReport
        ExtentLogger.info("API Request: <b>" + method + "</b> " + uri);
        if (!requestBody.isBlank()) {
            ExtentLogger.logJson("Request Payload", requestBody);
        }

        Response response = ctx.next(requestSpec, responseSpec);

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody() != null ? response.getBody().asPrettyString() : "";

        // Log to Log4j2
        log.info("Response Status: {}", statusCode);
        if (!responseBody.isBlank()) {
            log.info("Response Body: {}", responseBody);
        }

        // Log to ExtentReport
        ExtentLogger.logResponse(statusCode, responseBody);

        return response;
    }
}