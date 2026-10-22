# Enterprise REST Assured API Automation Framework

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-5.5.0-green.svg)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.10.2-red.svg)](https://testng.org/)
[![ExtentReports](https://img.shields.io/badge/ExtentReports-5.1.2-blue.svg)](https://www.extentreports.com/)
[![CI/CD](https://img.shields.io/badge/Jenkins-Pipeline%20Ready-brightgreen.svg)](https://www.jenkins.io/)

A thread-safe, modular, and CI/CD-ready REST API test automation framework built with **Java 21**, **REST Assured**, **TestNG**, and **Maven**. Designed with a clean separation of concerns following industry standards, featuring thread-safe token caching, comprehensive logging with **Log4j2**, automated retries, and dual test reporting with **ExtentReports 5** and **TestNG Emailable Reports**.

## 👤 Author & Profiles

- **GitHub:** [MaheshH91](https://github.com/MaheshH91)
- **LinkedIn:** [Add your LinkedIn profile URL](https://www.linkedin.com/in/maheshholkarqaengineer/)

> Replace the LinkedIn link above with your personal LinkedIn profile URL before publishing.


---

## 📑 Table of Contents
- [Architecture & Design Principles](#-architecture--design-principles)
- [Directory Layout](#-directory-layout)
- [Tech Stack & Dependencies](#-tech-stack--dependencies)
- [Authentication & Security Coverage](#-authentication--security-coverage)
- [Test Suites & Functional Coverage](#-test-suites--functional-coverage)
- [Reporting & Logging](#-reporting--logging)
- [Getting Started & Local Execution](#-getting-started--local-execution)
- [CI/CD Jenkins Pipeline](#-cicd-jenkins-pipeline)

---

## 🏛 Architecture & Design Principles

* **Layered Architecture (`src/main/java` vs. `src/test/java`)**: Framework engines, service clients, and utilities are strictly isolated in `src/main/java`, while test cases, data providers, and assertions reside under `src/test/java`.
* **Centralized Configuration**: All endpoint routes, headers, and timeouts are controlled via `Endpoints.java` and `FrameworkConstants.java`, eliminating hardcoded literals across test classes.
* **Thread-Safe State & Token Management**: `TokenManager` leverages a `ConcurrentHashMap` cache to manage user JWT tokens during parallel test execution without race conditions.
* **Thread-Safe Dashboard Reporting**: `ExtentManager` uses `ThreadLocal<ExtentTest>` to isolate test steps across concurrent threads.
* **Automatic Failure Recovery**: Includes an `IAnnotationTransformer` paired with `RetryAnalyzer` to automatically retry flaky tests up to configured threshold limits.
* **Dynamic Test Data Generation**: Uses `FakerDataFactory` to dynamically construct unique payloads for registration and profile updates, avoiding database collisions.

---

## 📂 Directory Layout

```text
APIAutomationFramework/
├── .github/
│   └── workflows/
│       └── ci.yml
├── Jenkinsfile                          # Jenkins declarative pipeline configuration
├── pom.xml                              # Maven build file with resolved compile/test scopes
├── testng.xml                           # Master test suite runner with parallel execution
├── API Automation Mock.json             # Mockoon API mock configuration (root)
├── README.md                            # Project documentation
└── src/
    ├── main/
    │   └── java/
    │       └── com/api/
    │           ├── config/
    │           │   └── ConfigManager.java          # Environment-aware properties loader
    │           ├── constants/
    │           │   ├── Endpoints.java              # Centralized API endpoint routes
    │           │   └── FrameworkConstants.java     # Timeouts, SLAs, paths, and headers
    │           ├── filters/
    │           │   └── LoggingFilter.java          # Request/response logger for Log4j2 & Extent
    │           ├── models/
    │           │   ├── request/                    # Request POJOs using Builder Pattern
    │           │   │   ├── LoginRequest.java
    │           │   │   ├── ProfileRequest.java
    │           │   │   └── SignUpRequest.java
    │           │   └── response/                   # Response deserialization POJOs
    │           │       ├── LoginResponse.java
    │           │       └── UserProfileResponse.java
    │           ├── reporting/
    │           │   ├── ExtentLogger.java           # Formatted HTML/JSON logger
    │           │   └── ExtentManager.java          # ThreadLocal ExtentReports 5 engine
    │           ├── services/                       # API service layer (extending BaseService)
    │           │   ├── BaseService.java
    │           │   ├── AuthService.java
    │           │   ├── OrderService.java
    │           │   ├── SecurityAuthService.java
    │           │   └── UserProfileManagementService.java
    │           └── utils/
    │               ├── FakerDataFactory.java       # Dynamic timestamp-based data factory
    │               ├── RequestBuilder.java         # Fluent request assembly utility
    │               ├── ResponseValidator.java      # Reusable assertion helper
    │               └── TokenManager.java           # Concurrent token cache manager
    └── test/
        ├── java/
        │   └── com/api/
        │       ├── listeners/
        │       │   ├── AnnotationTransformer.java  # Injects retry analyzer at runtime
        │       │   ├── RetryAnalyzer.java          # Configurable test retry policy
        │       │   └── TestListener.java           # TestNG execution listener for Extent
        │       ├── tests/
        │       │   ├── BaseTest.java               # Global before/after hooks & suite setup
        │       │   ├── AccountCreationTest.java    # User signup validation
        │       │   ├── AdvancedApiTestingScenariosTest.java # Pagination, SLA, Multipart, Schema
        │       │   ├── AuthNegativeTest.java       # Data-driven negative auth validations
        │       │   ├── AuthorizationMechanismsTest.java # Basic, Digest, API Key, OAuth2, Signature
        │       │   ├── ComprehensiveTestNGFeaturesTest.java # XML params, parallel data provider
        │       │   ├── ForgotPasswordTest.java     # Password recovery API verification
        │       │   ├── GetProfileRequestTest.java  # Authenticated profile retrieval
        │       │   ├── LoginTest.java              # Happy-path login verification
        │       │   ├── UpdateProfileTest.java      # Profile mutation validation
        │       │   ├── UserLifecycleE2ETest.java   # End-to-end full user journey
        │       │   └── UserProfileSecurityTest.java # Token integrity and 401 validation
        │       └── utils/
        │           └── TestDataProvider.java       # Parallel and method-aware DataProviders
        └── resources/
            ├── config.properties                   # Dynamic environment endpoints & credentials
            ├── log4j2.xml                          # Console and rolling file logging configuration
            ├── API Automation Mock.json            # Mockoon mock environment backup
            └── schemas/
                └── order-schema.json               # JSON Schema definition for contract testing

```

---

## 🛠 Tech Stack & Dependencies

| Tool / Library | Version | Usage |
| --- | --- | --- |
| **Java** | 21 (LTS) | Programming Language |
| **REST Assured** | 5.5.0 | RESTful API Client and DSL |
| **TestNG** | 7.10.2 | Test Runner, Assertions, and Parallel Execution |
| **Jackson Databind** | 2.18.2 | POJO Serialization and Deserialization |
| **ExtentReports** | 5.1.2 | HTML Dashboard Reporting |
| **Log4j2** | 2.24.1 | Asynchronous Application and Execution Logging |
| **JSON Schema Validator** | 5.5.0 | Contract Validation against Draft-04 JSON Schemas |
| **Mockoon CLI** | Latest | Lightweight Local REST API Mocking Engine |
| **Jenkins** | 2.x | Continuous Integration and Pipeline Orchestration |

---

## 🔐 Authentication & Security Coverage

The framework contains production-grade implementations and tests across all major API authorization schemes via `SecurityAuthService`:

1. **Bearer Token (JWT)**: Dynamically captured and injected via `TokenManager` into headers (`Authorization: Bearer <token>`).
2. **Basic Authentication**: Supports both **Preemptive** and **Challenged** basic auth flows with valid and invalid credential matrices.
3. **Digest Authentication**: Validates challenge handshakes and `WWW-Authenticate` realm handling.
4. **API Key Authentication**: Tests key delivery across both **Custom Headers** (`X-API-KEY`) and **Query Parameters** (`?api_key=...`).
5. **OAuth 2.0 (Client Credentials Grant)**: Posts client credentials to fetch an access token, then verifies resource consumption using the issued bearer token.
6. **Custom HMAC / Signature**: Injects timestamped cryptographic signatures (`X-Signature` & `X-Timestamp`) for zero-trust service-to-service validation.

---

## 🧪 Test Suites & Functional Coverage

| Test Class | Focus Area | Key Scenarios |
| --- | --- | --- |
| `AccountCreationTest` | User Management | Dynamic user registration with unique emails and usernames. |
| `LoginTest` | Authentication | Validates successful credential authentication and JWT receipt. |
| `AuthNegativeTest` | Error Handling | Parallel data-driven test for 400 Bad Request and 401 Unauthorized handling. |
| `UserProfileSecurityTest` | Security | Verifies access rejection on omitted, expired, or malformed tokens. |
| `GetProfileRequestTest` | Data Retrieval | Retrieves and deserializes profile data into `UserProfileResponse`. |
| `UpdateProfileTest` | Data Mutation | Updates user fields and asserts changes via `ProfileRequest.Builder`. |
| `UserLifecycleE2ETest` | Full E2E Flow | Registration -> Login -> Fetch Profile -> Update Profile. |
| `AuthorizationMechanismsTest` | Security Schemes | Basic, Digest, API Key, OAuth 2.0, and HMAC signature validations. |
| `AdvancedApiTestingScenariosTest` | Advanced REST | Pagination, SLA assertions (< 2000ms), Multipart file upload, and JSON Schema contract validation. |
| `ComprehensiveTestNGFeaturesTest` | Framework Engine | TestNG XML parameter injection, method dependencies, priority ordering, and concurrency simulation. |

---

## 📊 Reporting & Logging

### Dual Reporting Architecture

1. **ExtentReports 5 Dashboard**: Generated at `target/extent-reports/extentReport.html`. Includes dark-mode dashboard metrics, test execution timestamps, author tagging, categorization, and formatted JSON code blocks for all requests and responses.
2. **TestNG Emailable Report**: Generated at `target/surefire-reports/emailable-report.html`. A lightweight, self-contained single-page report ideal for CI email notifications.
3. **Surefire XML Results**: Stored at `target/surefire-reports/testng-results.xml` for consumption by the Jenkins JUnit publisher.

### Centralized Logging

* **Log4j2 Console & File Appender**: Configured via `src/test/resources/log4j2.xml`. All API interactions are logged both to standard console output and saved to `logs/test.log`.
* **LoggingFilter**: Captures HTTP methods, request URIs, payload contents, response codes, and bodies, forwarding entries to both Log4j2 and ExtentReports.

---

## 🚀 Getting Started & Local Execution

### Prerequisites

* **Java Development Kit (JDK)**: 21 or higher installed and configured in `JAVA_HOME`.
* **Apache Maven**: 3.9+ installed and configured in `PATH`.
* **Mockoon CLI** (Optional for CI/Local): Run `npm install -g @mockoon/cli` or start the Mockoon Desktop App on port `8089`.

### 1. Start the Local Mock Server

If using Mockoon CLI:

```bash
mockoon-cli start --data "API Automation Mock.json" --port 8089

```

### 2. Compile the Framework

```bash
mvn clean compile test-compile

```

### 3. Run the Complete Test Suite

```bash
mvn test -DsuiteXmlFile=testng.xml

```

### 4. Run with Specific Environment

Pass dynamic parameters to switch configurations:

```bash
mvn test -DsuiteXmlFile=testng.xml -Denv=qa

```

---

## 🔄 CI/CD Jenkins Pipeline

The project includes an enterprise declarative `Jenkinsfile` featuring automated dependency provisioning, mock server lifecycle control, and report publishing:

```groovy
pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'MAVEN_HOME'
    }

    environment {
        MOCK_FILE = 'API Automation Mock.json'
        MOCK_PORT = '8089'
    }

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Start Mock Server') {
            steps {
                script {
                    if (fileExists("${env.MOCK_FILE}")) {
                        bat "mockoon-cli start --data \"%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"
                    } else if (fileExists("src/test/resources/${env.MOCK_FILE}")) {
                        bat "mockoon-cli start --data \"src\\test\\resources\\%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"
                    }
                }
            }
        }

        stage('Compile & Validate') {
            steps {
                bat 'mvn clean test-compile'
            }
        }

        stage('Run API Test Suite') {
            steps {
                bat 'mvn test -DsuiteXmlFile=testng.xml'
            }
        }
    }

    post {
        always {
            script {
                bat 'mockoon-cli stop --all || mockoon-cli stop all || exit 0'
            }

            // JUnit / Surefire Trend Processing
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // Publish TestNG Native HTML Report
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/surefire-reports',
                reportFiles: 'emailable-report.html',
                reportName: 'TestNG Emailable Report',
                reportTitles: 'TestNG Execution Report'
            ])

            // Publish ExtentReports Dashboard
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/extent-reports',
                reportFiles: 'extentReport.html',
                reportName: 'Extent Report',
                reportTitles: 'Enterprise API Test Automation Report'
            ])

            // Archive Build Artifacts
            archiveArtifacts artifacts: 'target/surefire-reports/**, target/extent-reports/**', allowEmptyArchive: true
        }
    }
}

```

```
