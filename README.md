# Enterprise REST Assured API Automation Framework

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-5.5.0-green.svg)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.10.2-red.svg)](https://testng.org/)
[![ExtentReports](https://img.shields.io/badge/ExtentReports-5.1.2-blue.svg)](https://www.extentreports.com/)
[![CI/CD](https://img.shields.io/badge/Jenkins-Pipeline%20Ready-brightgreen.svg)](https://www.jenkins.io/)

A thread-safe, modular, enterprise-grade REST API test automation framework built with **Java 21**, **REST Assured**, **TestNG**, and **Maven**. Designed with a clean separation of concerns, featuring dynamic Maven profile execution (`smoke`, `security`, `regression`), Mockoon API virtualization, thread-safe token caching, unified logging with **Log4j2**, automated test retry mechanisms, and dual reporting via **ExtentReports 5** and **TestNG Emailable Reports**.

---

## 📑 Table of Contents
- [Architecture & Design Principles](#-architecture--design-principles)
- [Directory Layout](#-directory-layout)
- [Tech Stack & Dependencies](#-tech-stack--dependencies)
- [Test Suites & Maven Profiles](#-test-suites--maven-profiles)
- [Authentication & Security Coverage](#-authentication--security-coverage)
- [Reporting & Logging](#-reporting--logging)
- [Getting Started & Local Execution](#-getting-started--local-execution)
- [CI/CD Jenkins Parameterized Pipeline](#-cicd-jenkins-parameterized-pipeline)

---

## 🏛 Architecture & Design Principles

* **Layered Architecture (`src/main/java` vs. `src/test/java`)**: Framework core engines, service clients, request/response models, and utilities reside exclusively in `src/main/java`. Test suites, assertions, and listeners reside under `src/test/java`.
* **Dynamic Suite Execution via Maven Profiles**: The build uses property interpolation (`${suiteXmlFile}`) configured in `pom.xml`, allowing flexible suite execution across Smoke, Security, and Regression without duplicating plugin configurations.
* **Thread-Safe State & Token Management**: `TokenManager` utilizes a thread-safe `ConcurrentHashMap` cache to manage user JWT tokens during concurrent executions without race conditions.
* **Thread-Safe Dashboard Reporting**: `ExtentManager` uses `ThreadLocal<ExtentTest>` to isolate test metrics across parallel execution threads.
* **Automatic Failure Recovery**: Integrated `IAnnotationTransformer` paired with `RetryAnalyzer` automatically retries flaky tests up to configured thresholds.
* **Mock Server Lifecycle Control**: Designed for zero external dependency execution by pairing with **Mockoon CLI** running on port `8089`.

---

## 📂 Directory Layout

```text
APIAutomationFramework/
├── Jenkinsfile                          # Parameterized Jenkins declarative pipeline
├── pom.xml                              # Maven build file with dynamic profiles
├── testng-regression.xml                # Master full regression suite (Default)
├── testng-smoke.xml                     # Core smoke verification suite
├── testng-security.xml                  # Authorization and security test suite
├── mockoon-environment.json             # Mockoon API mock configuration (root)
├── README.md                            # Framework documentation
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
    │               ├── FakerDataFactory.java       # Dynamic test payload generator
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
        │       │   ├── LoginTest.java              # Valid credential login verification
        │       │   ├── UpdateProfileTest.java      # Profile mutation validation
        │       │   ├── UserLifecycleE2ETest.java   # End-to-end full user journey
        │       │   └── UserProfileSecurityTest.java # Token integrity and 401 validation
        │       └── utils/
        │           └── TestDataProvider.java       # Parallel and method-aware DataProviders
        └── resources/
            ├── config.properties                   # Dynamic environment endpoints & credentials
            ├── log4j2.xml                          # Console and rolling file logging configuration
            ├── mockoon-environment.json            # Backup mock environment definition
            └── schemas/
                └── order-schema.json               # JSON Schema definition for contract testing

```

---

## 🛠 Tech Stack & Dependencies

| Tool / Library | Version | Usage |
| --- | --- | --- |
| **Java** | 21 (LTS) | Language runtime |
| **REST Assured** | 5.5.0 | REST API client DSL and assertions |
| **TestNG** | 7.10.2 | Test runner, lifecycle assertions, and parallel execution |
| **Jackson Databind** | 2.18.2 | JSON serialization and deserialization |
| **ExtentReports** | 5.1.2 | Interactive dark-mode dashboard reporting |
| **Log4j2 & SLF4J Bridge** | 2.24.1 / 2.0.16 | Unified console and file logging (`log4j-slf4j2-impl`) |
| **JSON Schema Validator** | 5.5.0 | Contract validation against Draft-04 JSON schemas |
| **Mockoon CLI** | Latest | Local REST API simulation engine (Port 8089) |
| **Jenkins** | 2.x | Continuous integration and parameterized execution |

---

## 🎯 Test Suites & Maven Profiles

The framework utilizes dedicated suite configurations activated via Maven profiles in `pom.xml`:

| Profile ID | Target Suite XML | Focus Area | Command |
| --- | --- | --- | --- |
| **`regression`** *(Default)* | `testng-regression.xml` | Full test execution across all functional, security, and concurrency suites (39 tests). | `mvn test` or `mvn test -Pregression` |
| **`smoke`** | `testng-smoke.xml` | Fast feedback on core authentication, account onboarding, and profile access. | `mvn test -Psmoke` |
| **`security`** | `testng-security.xml` | Authorization mechanisms, negative authentication handling, and token manipulation. | `mvn test -Psecurity` |

---

## 🔐 Authentication & Security Coverage

The framework provides automated validation across all industry-standard authentication and authorization schemes via `SecurityAuthService`:

* **Bearer Token (JWT)**: Automates token capture and header injection (`Authorization: Bearer <token>`).
* **Basic Authentication**: Tests challenged and preemptive basic auth flows using Base64 header encoding.
* **Digest Authentication**: Handles cryptographic digest challenge handshakes and `401 Unauthorized` / `WWW-Authenticate` negotiations.
* **API Key Authentication**: Evaluates delivery through custom request headers (`X-API-KEY`) and query parameters (`?api_key=...`).
* **OAuth 2.0 (Client Credentials Grant)**: Automatically requests access tokens from OAuth token endpoints and validates access to protected resources.
* **HMAC / Custom Signature**: Injects timestamped cryptographic signatures (`X-Signature` & `X-Timestamp`) for zero-trust service communication.

---

## 📊 Reporting & Logging

### Dual Reporting Architecture

1. **ExtentReports 5 HTML Dashboard**: Generated at `target/extent-reports/extentReport.html`. Includes visual test pass rates, device/OS metadata, execution timelines, author tags, and embedded JSON code blocks for all requests and responses.
2. **TestNG Native Reports**: Generated at `target/surefire-reports/emailable-report.html` and `testng-results.xml` for consumption by CI tools and automated email distribution.

### Centralized Logging Configuration

* **Log4j2 Console & File Appender**: Managed via `src/test/resources/log4j2.xml`. All runtime logs are written to standard out and persisted to `logs/test.log`.
* **LoggingFilter Integration**: Captures HTTP methods, request URIs, payload bodies, response codes, and response bodies, feeding both Log4j2 and ExtentReports simultaneously.

---

## 🚀 Getting Started & Local Execution

### Prerequisites

* **Java Development Kit (JDK)**: Version 21 installed and configured in `JAVA_HOME`.
* **Apache Maven**: Version 3.9+ installed and configured in `PATH`.
* **Mockoon CLI**: Installed globally via `npm install -g @mockoon/cli`.

### 1. Start the Mock Server

Start Mockoon pointing to the root configuration on port `8089`:

```bash
mockoon-cli start --data "mockoon-environment.json" --port 8089

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



### 4. Direct XML Suite Override

You can override profile settings at the command line:

```bash
mvn test -DsuiteXmlFile=testng-smoke.xml

```

---

## 🔄 CI/CD Jenkins Parameterized Pipeline

The declarative `Jenkinsfile` provides an on-demand choice parameter dropdown to select the target suite profile upon triggering:

```groovy
pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['regression', 'smoke', 'security'],
            description: 'Select the TestNG suite profile to execute via Maven'
        )
    }

    tools {
        jdk 'JAVA_HOME'
        maven 'MAVEN_HOME'
    }

    environment {
        MOCK_FILE = 'mockoon-environment.json'
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
                    def rootFile = "${env.MOCK_FILE}"
                    def resourceFile = "src/test/resources/${env.MOCK_FILE}"

                    if (fileExists(rootFile)) {
                        bat "mockoon-cli start --data \"%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"
                    } else if (fileExists(resourceFile)) {
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
                bat "mvn test -P${params.TEST_SUITE}"
            }
        }
    }

    post {
        always {
            script {
                bat 'mockoon-cli stop all || mockoon-cli stop --all || exit 0'
            }

            // Publish TestNG XML Results
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // Publish TestNG Emailable HTML Report
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

            // Archive Execution Artifacts
            archiveArtifacts artifacts: 'target/surefire-reports/**, target/extent-reports/**', allowEmptyArchive: true
        }
    }
}
