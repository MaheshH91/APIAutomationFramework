# Enterprise REST Assured API Automation Framework

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-5.5.0-green.svg)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.10.2-red.svg)](https://testng.org/)
[![ExtentReports](https://img.shields.io/badge/ExtentReports-5.1.2-blue.svg)](https://www.extentreports.com/)
[![CI/CD](https://img.shields.io/badge/Jenkins-Pipeline%20Ready-brightgreen.svg)](https://www.jenkins.io/)

A thread-safe, modular, enterprise-grade REST API test automation framework built with **Java 21**, **REST Assured**, **TestNG**, and **Maven**. Designed with a clean separation of concerns, featuring dynamic Maven profile execution (`smoke`, `security`, `regression`), stateful multi-step API chaining workflows, Mockoon API virtualization, thread-safe token caching and reporting, unified logging with **Log4j2**, automated test retry mechanisms, and dual reporting via **ExtentReports 5** and **TestNG Emailable Reports**.

---

## 📑 Table of Contents
- [Architecture & Design Principles](#-architecture--design-principles)
- [Directory Layout](#-directory-layout)
- [Tech Stack & Dependencies](#-tech-stack--dependencies)
- [Test Suites & Maven Profiles](#-test-suites--maven-profiles)
- [Advanced API Chaining Scenarios](#-advanced-api-chaining-scenarios)
- [Authentication & Security Coverage](#-authentication--security-coverage)
- [Reporting & Logging](#-reporting--logging)
- [Getting Started & Local Execution](#-getting-started--local-execution)
- [CI/CD Jenkins Parameterized Pipeline](#-cicd-jenkins-parameterized-pipeline)

---

## 🏛 Architecture & Design Principles

* **Layered Architecture (`src/main/java` vs. `src/test/java`)**: Framework engines, service clients, request/response models, and utilities reside exclusively in `src/main/java`. Test suites, assertions, and execution listeners reside under `src/test/java`.
* **Stateful API Chaining**: Supports multi-stage test journeys where downstream steps dynamically consume context, identifiers, or tokens extracted from previous requests (via TestNG `dependsOnMethods` and `ITestContext`).
* **Dynamic Suite Execution via Maven Profiles**: Property interpolation (`${suiteXmlFile}`) configured in `pom.xml` allows seamless switching across Smoke, Security, and Regression suites without redundant plugin declarations.
* **Thread-Safe State & Token Management**: `TokenManager` utilizes a thread-safe `ConcurrentHashMap` cache to manage user JWT tokens during concurrent executions without race conditions, backed by a global `clearCache()` teardown hook.
* **Thread-Safe Dashboard Reporting**: `ExtentTestManager` leverages `ThreadLocal<ExtentTest>` to isolate test metrics across parallel execution threads.
* **Automatic Failure Recovery**: Integrated `AnnotationTransformer` paired with `RetryAnalyzer` automatically re-executes transient test failures up to configured thresholds.
* **Mock Server Virtualization**: Self-contained local simulation using **Mockoon CLI** on port `8089` supporting strict header-based auth rules and 401 fallback mechanisms.

---

## 📂 Directory Layout

```text
APIAutomationFramework/
├── Jenkinsfile                          # Parameterized Jenkins declarative pipeline
├── pom.xml                              # Maven build file with dynamic profiles
├── mockoon-environment.json             # Mockoon API mock configuration (root)
├── README.md                            # Framework documentation
├── testng-suites/                       # Centralized TestNG XML suites directory
│   ├── testng-regression.xml            # Master full regression suite (Default)
│   ├── testng-smoke.xml                 # Core smoke verification suite
│   └── testng-security.xml              # Authorization and security test suite
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
    │           │   ├── ExtentManager.java          # ExtentReports 5 engine initialization
    │           │   └── ExtentTestManager.java      # ThreadLocal ExtentTest worker context
    │           ├── services/                       # API service layer (extending BaseService)
    │           │   ├── BaseService.java
    │           │   ├── AuthService.java
    │           │   ├── OrderService.java
    │           │   ├── SecurityAuthService.java
    │           │   └── UserProfileManagementService.java
    │           └── utils/
    │               ├── FakerDataFactory.java       # Dynamic synthetic payload generator
    │               ├── RequestBuilder.java         # Fluent request assembly utility
    │               ├── ResponseValidator.java      # Reusable assertion helper
    │               └── TokenManager.java           # Concurrent token cache with clearCache()
    └── test/
        ├── java/
        │   └── com/api/
        │       ├── listeners/
        │       │   ├── AnnotationTransformer.java  # Injects retry analyzer at runtime
        │       │   ├── RetryAnalyzer.java          # Configurable test retry policy
        │       │   └── TestListener.java           # TestNG execution listener for Extent
        │       ├── tests/
        │       │   ├── BaseTest.java               # Global @AfterSuite cleanup hooks
        │       │   ├── AccountCreationTest.java    # User signup validation
        │       │   ├── AdvancedApiTestingScenariosTest.java # Pagination, SLA, Multipart, Schema
        │       │   ├── AuthNegativeTest.java       # Data-driven negative auth validations
        │       │   ├── AuthorizationMechanismsTest.java # Basic, Digest, API Key, OAuth2, Signature
        │       │   ├── ComprehensiveTestNGFeaturesTest.java # XML params, parallel data provider
        │       │   ├── ContextChainingTest.java    # ITestContext suite-level data propagation
        │       │   ├── ForgotPasswordTest.java     # Password recovery API verification
        │       │   ├── GetProfileRequestTest.java  # Authenticated profile retrieval
        │       │   ├── LoginTest.java              # Valid credential login verification
        │       │   ├── OAuth2TokenExchangeChainTest.java # OAuth token exchange & consumption
        │       │   ├── OrderFulfillmentChainTest.java  # 5-step order fulfillment chaining
        │       │   ├── UpdateProfileTest.java      # Profile mutation validation
        │       │   ├── UserLifecycleE2ETest.java   # End-to-end full user journey
        │       │   └── UserProfileSecurityTest.java # Token integrity and 401 validation
        │       └── utils/
        │           └── TestDataProvider.java       # Parallel and method-aware DataProviders
        └── resources/
            ├── API Automation Mock.json            # Mockoon environment definition
            ├── config.properties                   # Dynamic environment endpoints & credentials
            ├── log4j2.xml                          # Console and rolling file logging configuration
            ├── mockoon-environment.json            # Consolidated mock environment definition
            └── schemas/
                └── order-schema.json               # JSON Schema definition for contract testing
```

---

## 🛠 Tech Stack & Dependencies

| Tool / Library | Version | Usage |
| --- | --- | --- |
| **Java** | 21 (LTS) | Language runtime |
| **REST Assured** | 5.5.0 | REST API client DSL and assertions |
| **TestNG** | 7.10.2 | Test runner, lifecycle assertions, and concurrency |
| **Jackson Databind** | 2.18.2 | JSON serialization and deserialization |
| **ExtentReports** | 5.1.2 | Interactive dark-mode dashboard reporting |
| **Log4j2 & SLF4J Bridge** | 2.24.1 / 2.0.16 | Unified console and file logging (`log4j-slf4j2-impl`) |
| **JSON Schema Validator** | 5.5.0 | Contract validation against Draft-04 JSON schemas |
| **Mockoon CLI** | Latest | Local REST API virtualization engine (Port 8089) |
| **Jenkins** | 2.x | Continuous integration and parameterized execution |

---

## 🎯 Test Suites & Maven Profiles

The framework provides targeted XML suite descriptors triggered through Maven profiles:

| Profile ID | Target Suite XML | Focus Area | Command |
| --- | --- | --- | --- |
| **`regression`** *(Default)* | `testng-regression.xml` | Comprehensive execution across all functional, security, concurrency, and API chaining suites (49+ tests). | `mvn test` or `mvn test -Pregression` |
| **`smoke`** | `testng-smoke.xml` | Fast feedback on core authentication, onboarding, and essential profile access. | `mvn test -Psmoke` |
| **`security`** | `testng-security.xml` | Authorization mechanisms, negative authentication handling, and token manipulation. | `mvn test -Psecurity` |

---

## 🔗 Advanced API Chaining Scenarios

The framework demonstrates enterprise-grade state propagation across several patterns:

### 1. Order Lifecycle Chain (`OrderFulfillmentChainTest`)
Executes an end-to-end business transaction where each step depends on the previous step's output:
```text
[GET Catalog Items]
       ↓ (Extract Target Item)
[POST /api/orders]
       ↓ (Extract Generated orderId: 101)
[POST /api/orders/101/invoice]
       ↓ (Multipart File Attachment)
[PATCH /api/orders/101]
       ↓ (Transition Status to SHIPPED)
[DELETE /api/orders/101]
       ↓ (Teardown & Verification)
```

### 2. OAuth2 Token Exchange Chain (`OAuth2TokenExchangeChainTest`)
* **Step 1:** Dispatches client credentials to `/api/oauth/token` and extracts `access_token` and `token_type`.
* **Step 2:** Formats the token as a dynamic Bearer authorization header and consumes the downstream resource `/api/secure/oauth-resource`.

### 3. Suite Context Chaining (`ContextChainingTest`)
Demonstrates suite-wide, thread-safe variable exchange using TestNG's `ITestContext`:
```java
// Producer Step: Injects generated test state into the suite context
context.setAttribute("CHAINED_USER", generatedUser);

// Consumer Step: Retrieves context state across separate test methods
String targetUser = (String) context.getAttribute("CHAINED_USER");
```

---

## 🔐 Authentication & Security Coverage

Automated validation across all industry-standard schemes via `SecurityAuthService`:

* **Bearer Token (JWT)**: Automates token capture, concurrent caching, and header injection (`Authorization: Bearer <token>`).
* **Basic Authentication**: Tests challenged and preemptive basic auth flows using Base64 header encoding.
* **Digest Authentication**: Handles cryptographic digest challenge handshakes and `401 Unauthorized` / `WWW-Authenticate` negotiations.
* **API Key Authentication**: Evaluates delivery through custom request headers (`X-API-KEY`) and query parameters (`?api_key=...`).
* **OAuth 2.0 (Client Credentials Grant)**: Automatically requests access tokens from token endpoints and validates access to protected resources.
* **HMAC / Custom Signature**: Injects timestamped cryptographic signatures (`X-Signature` & `X-Timestamp`) for zero-trust service communication.

---

## 📊 Reporting & Logging

### Dual Reporting Architecture

1. **ExtentReports 5 HTML Dashboard**: Generated at `target/extent-reports/extentReport.html`. Utilizes `ExtentTestManager` (`ThreadLocal<ExtentTest>`) to ensure report threads remain isolated under heavy parallel execution. Captures device/OS metadata, execution timelines, author tags, and embedded JSON code blocks.
2. **TestNG Native Reports**: Generated at `target/surefire-reports/emailable-report.html` and `testng-results.xml` for CI metrics and automated email distribution.

### Centralized Logging Configuration

* **Log4j2 Console & File Appender**: Managed via `src/test/resources/log4j2.xml`. Logs write simultaneously to standard out and `logs/test.log`.
* **LoggingFilter Integration**: Intercepts HTTP methods, request URIs, payload bodies, headers, response status codes, and response bodies, feeding both Log4j2 and ExtentReports.

---

## 🚀 Getting Started & Local Execution

### Prerequisites

* **Java Development Kit (JDK)**: Version 21 installed and configured in `JAVA_HOME`.
* **Apache Maven**: Version 3.9+ installed and configured in `PATH`.
* **Mockoon CLI**: Installed globally via `npm install -g @mockoon/cli`.

### 1. Start the Mock Server

Start Mockoon pointing to the mock environment on port `8089`:

```bash
mockoon-cli stop all
mockoon-cli start --data "src/test/resources/mockoon-environment.json" --port 8089
```

Verify mock status via terminal:
```bash
curl -X POST http://localhost:8089/api/orders -H "Content-Type: application/json" -d "{\"item\":\"Laptop\"}"
```

### 2. Compile Framework Dependencies

```bash
mvn clean compile test-compile
```

### 3. Run by Profile

* **Execute Smoke Tests**:
  ```bash
  mvn test -Psmoke
  ```

* **Execute Security Tests**:
  ```bash
  mvn test -Psecurity
  ```

* **Execute Full Regression Suite**:
  ```bash
  mvn test -Pregression
  ```

* **Execute with Environment Parameter**:
  ```bash
  mvn test -Pregression -Denv=qa
  ```

---

## 🔄 CI/CD Jenkins Parameterized Pipeline

The declarative `Jenkinsfile` includes pre-clean stages, health-check polling loops, and multi-parameter execution:

```groovy
pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['regression', 'smoke', 'security'],
            description: 'Select the TestNG suite profile to execute via Maven'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['qa', 'dev', 'stage'],
            description: 'Target test execution environment'
        )
    }

    tools {
        jdk 'JAVA_HOME'
        maven 'MAVEN_HOME'
    }

    environment {
        MOCK_FILE = 'src/test/resources/mockoon-environment.json'
        MOCK_PORT = '8089'
    }

    options {
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5'))
        disableConcurrentBuilds()
        ansiColor('xterm')
    }

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Pre-clean Mockoon') {
            steps {
                bat 'mockoon-cli stop all || exit 0'
            }
        }

        stage('Start Mock Server') {
            steps {
                script {
                    echo "Starting Mockoon on port ${env.MOCK_PORT}..."
                    bat "mockoon-cli start --data \"%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"

                    // Health check polling loop
                    bat '''
                    @echo off
                    set COUNT=0
                    :poll
                    curl -s -o nul http://localhost:8089/api/orders
                    if %ERRORLEVEL% EQU 0 goto ready
                    set /a COUNT+=1
                    if %COUNT% GEQ 15 (
                        echo Mockoon server failed to start within 15 seconds.
                        exit 1
                    )
                    timeout /t 1 /nobreak >nul
                    goto poll
                    :ready
                    echo Mockoon mock server is fully operational.
                    '''
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
                bat "mvn test -P${params.TEST_SUITE} -Denv=${params.ENVIRONMENT}"
            }
        }
    }

    post {
        always {
            bat 'mockoon-cli stop all || exit 0'
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/surefire-reports',
                reportFiles: 'emailable-report.html',
                reportName: 'TestNG Emailable Report',
                reportTitles: 'TestNG Execution Report'
            ])
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/extent-reports',
                reportFiles: 'extentReport.html',
                reportName: 'Extent Report',
                reportTitles: 'Enterprise API Test Automation Report'
            ])
            archiveArtifacts artifacts: 'target/surefire-reports/**, target/extent-reports/**', allowEmptyArchive: true
        }
        success {
            echo "All tests executed and passed under profile: '${params.TEST_SUITE}'"
        }
        failure {
            echo "Build failed. Inspect ExtentReport, TestNG reports, or console logs for details."
        }
    }
}
```
