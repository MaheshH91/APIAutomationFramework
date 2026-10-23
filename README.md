# Enterprise REST Assured API Automation Framework

<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java 21" />
  <img src="https://img.shields.io/badge/REST%20Assured-5.5.0-green.svg" alt="REST Assured 5.5.0" />
  <img src="https://img.shields.io/badge/TestNG-7.10.2-red.svg" alt="TestNG 7.10.2" />
  <img src="https://img.shields.io/badge/ExtentReports-5.1.2-blue.svg" alt="ExtentReports 5.1.2" />
  <img src="https://img.shields.io/badge/Jenkins-Pipeline%20Ready-brightgreen.svg" alt="Jenkins" />
  <img src="https://img.shields.io/badge/GitHub%20Actions-CI%2FCD%20Automated-blue.svg" alt="GitHub Actions" />
</div>

A thread-safe, modular, enterprise-grade REST API test automation framework built with Java 21, REST Assured, TestNG, and Maven. It is designed around clean separation of concerns, enabling reliable API validation for modern distributed systems.

---

## Author & Profiles

- GitHub Profile: [MaheshH91](https://github.com/MaheshH91)
- LinkedIn Profile: [Mahesh Holkar](https://www.linkedin.com/in/maheshholkarqaengineer/)

---

## Table of Contents

- [Architecture & Design Principles](#architecture--design-principles)
- [Directory Layout](#directory-layout)
- [Tech Stack & Dependencies](#tech-stack--dependencies)
- [Test Suites & Maven Profiles](#test-suites--maven-profiles)
- [Advanced API Chaining Scenarios](#advanced-api-chaining-scenarios)
- [Authentication & Security Coverage](#authentication--security-coverage)
- [Reporting & Logging](#reporting--logging)
- [Getting Started & Local Execution](#getting-started--local-execution)
- [CI/CD Parameterized Pipelines](#cicd-parameterized-pipelines)
- [Jenkins Declarative Pipeline](#jenkins-declarative-pipeline)
- [GitHub Actions Workflow](#github-actions-workflow)
- [Version Control & Secrets Hygiene](#version-control--secrets-hygiene)
- [Troubleshooting](#troubleshooting)
- [Configuration & Secret Management](#configuration-and-secret-management)
- [Suggested LinkedIn Project Description](#suggested-linkedin-project-description)

---

## Architecture & Design Principles

- Layered Architecture (`src/main/java` vs `src/test/java`): Framework engines, service clients, request/response models, and utilities live in `src/main/java`. Test suites and execution concerns stay in `src/test/java`.
- Stateful API Chaining: Supports multi-stage journeys where downstream steps dynamically consume context, identifiers, or tokens extracted from earlier requests using TestNG `dependsOnMethods` and reusable test state.
- Dynamic Suite Execution via Maven Profiles: Property interpolation (`${suiteXmlFile}`) in `pom.xml` allows smooth switching between smoke, security, and regression execution.
- Thread-Safe State & Token Management: `TokenManager` uses a `ConcurrentHashMap` to manage JWT tokens safely during concurrent runs.
- Thread-Safe Dashboard Reporting: `ExtentTestManager` uses `ThreadLocal<ExtentTest>` to isolate report state across parallel threads.
- Automatic Failure Recovery: `AnnotationTransformer` and `RetryAnalyzer` automatically re-run transient failures up to a configured threshold.
- Mock Server Virtualization: Self-contained local and CI simulation using Mockoon CLI on port `8089`, with resilient file path detection and background lifecycle management.

---

## Directory Layout

```text
APIAutomationFramework/
├── .github/
│   └── workflows/
│       └── api-automation-ci.yml      # GitHub Actions CI workflow
├── Jenkinsfile                         # Jenkins pipeline with email notifications
├── pom.xml                             # Maven build configuration
├── mockoon-environment.json            # Mockoon API mock configuration
├── README.md                           # Framework documentation
├── testng-suites/                      # Centralized TestNG suite definitions
│   ├── testng-regression.xml           # Full regression suite
│   ├── testng-smoke.xml                # Smoke verification suite
│   └── testng-security.xml             # Security validation suite
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/api/
│   │           ├── config/
│   │           │   └── ConfigManager.java
│   │           ├── constants/
│   │           │   ├── Endpoints.java
│   │           │   └── FrameworkConstants.java
│   │           ├── filters/
│   │           │   └── LoggingFilter.java
│   │           ├── models/
│   │           │   ├── request/
│   │           │   │   ├── LoginRequest.java
│   │           │   │   ├── ProfileRequest.java
│   │           │   │   └── SignUpRequest.java
│   │           │   └── response/
│   │           │       ├── LoginResponse.java
│   │           │       └── UserProfileResponse.java
│   │           ├── reporting/
│   │           │   ├── ExtentLogger.java
│   │           │   ├── ExtentManager.java
│   │           │   └── ExtentTestManager.java
│   │           ├── services/
│   │           │   ├── BaseService.java
│   │           │   ├── AuthService.java
│   │           │   ├── OrderService.java
│   │           │   ├── SecurityAuthService.java
│   │           │   └── UserProfileManagementService.java
│   │           └── utils/
│   │               ├── FakerDataFactory.java
│   │               ├── RequestBuilder.java
│   │               ├── ResponseValidator.java
│   │               └── TokenManager.java
│   └── test/
│       ├── java/
│       │   └── com/api/
│       │       ├── listeners/
│       │       │   ├── AnnotationTransformer.java
│       │       │   ├── RetryAnalyzer.java
│       │       │   └── TestListener.java
│       │       ├── tests/
│       │       │   ├── BaseTest.java
│       │       │   ├── AccountCreationTest.java
│       │       │   ├── AdvancedApiTestingScenariosTest.java
│       │       │   ├── AuthNegativeTest.java
│       │       │   ├── AuthorizationMechanismsTest.java
│       │       │   ├── ComprehensiveTestNGFeaturesTest.java
│       │       │   ├── ContextChainingTest.java
│       │       │   ├── ForgotPasswordTest.java
│       │       │   ├── GetProfileRequestTest.java
│       │       │   ├── LoginTest.java
│       │       │   ├── OAuth2TokenExchangeChainTest.java
│       │       │   ├── OrderFulfillmentChainTest.java
│       │       │   ├── UpdateProfileTest.java
│       │       │   ├── UserLifecycleE2ETest.java
│       │       │   └── UserProfileSecurityTest.java
│       │       └── utils/
│       │           └── TestDataProvider.java
│       └── resources/
│           ├── API Automation Mock.json
│           ├── config.properties
│           ├── log4j2.xml
│           ├── mockoon-environment.json
│           └── schemas/
│               └── order-schema.json
└──
```

---

## Tech Stack & Dependencies

| Tool / Library | Version | Usage |
| --- | --- | --- |
| Java | 21 (LTS) | Modern language runtime |
| REST Assured | 5.5.0 | REST API client DSL and assertions |
| TestNG | 7.10.2 | Test runner, lifecycle assertions, concurrency |
| Jackson Databind | 2.18.2 | JSON serialization and POJO mapping |
| ExtentReports | 5.1.2 | Interactive HTML dashboard reporting |
| Log4j2 & SLF4J Bridge | 2.24.1 / 2.0.16 | Unified console/file logging |
| JSON Schema Validator | 5.5.0 | Contract validation against JSON schemas |
| Mockoon CLI | Latest | Local and CI mock virtualization |
| Jenkins | 2.x | Distributed pipeline execution |
| GitHub Actions | Latest | Containerized CI/CD automation |

---

## Test Suites & Maven Profiles

The framework defines targeted TestNG suite descriptors executed via Maven profiles.

| Profile ID | Target Suite XML | Focus Area | Command |
| --- | --- | --- | --- |
| `regression` (default) | `testng-regression.xml` | Full functional, security, concurrency, and chaining coverage | `mvn test` or `mvn test -Pregression` |
| `smoke` | `testng-smoke.xml` | Core onboarding and authentication validation | `mvn test -Psmoke` |
| `security` | `testng-security.xml` | Authorization, token handling, and negative auth validation | `mvn test -Psecurity` |

---

## Advanced API Chaining Scenarios

The framework demonstrates enterprise-grade state propagation across several patterns.

### 1. Order Lifecycle Chain

`OrderFulfillmentChainTest` executes a multi-step business transaction where each action depends on the previous step's output:

```text
[GET Catalog Items]
   ↓
[POST /api/orders]
   ↓
[Extract orderId: 101]
   ↓
[POST /api/orders/101/invoice]
   ↓
[PATCH /api/orders/101]
   ↓
[DELETE /api/orders/101]
```

### 2. OAuth2 Token Exchange Chain

- Step 1: Requests a token from `/api/oauth/token`.
- Step 2: Extracts `access_token` and `token_type`.
- Step 3: Injects the token into a Bearer authorization header for downstream secure resource calls.

### 3. Suite Context Chaining

Demonstrates suite-wide state propagation using TestNG `ITestContext`:

```java
// Producer step
context.setAttribute("CHAINED_USER", generatedUser);

// Consumer step
String targetUser = (String) context.getAttribute("CHAINED_USER");
```

---

## Authentication & Security Coverage

Automated validation across standard and custom security schemes through `SecurityAuthService`:

- Bearer Token (JWT): token capture, concurrency-safe caching, and header injection
- Basic Authentication: preemptive and challenged flows using Base64 encoding
- Digest Authentication: handshake-based challenge handling and `401` negotiation
- API Key Authentication: custom header and query parameter validation
- OAuth 2.0 Client Credentials: token exchange and protected resource access
- HMAC / Custom Signature: timestamped request signing via `X-Signature` and `X-Timestamp`

---

## Reporting & Logging

### Dual Reporting Architecture

1. ExtentReports 5 HTML dashboard at `target/extent-reports/extentReport.html`
2. TestNG native reports at `target/surefire-reports/emailable-report.html` and `testng-results.xml`

### Centralized Logging Configuration

- Log4j2 console and file appenders via `src/test/resources/log4j2.xml`
- Request/response logging through `LoggingFilter` for HTTP methods, payloads, headers, and statuses
- Results automatically integrated into ExtentReports and test output streams

---

## Getting Started & Local Execution

### Prerequisites

- Java Development Kit (JDK) 21 configured in `JAVA_HOME`
- Apache Maven 3.9+ configured in `PATH`
- Mockoon CLI installed globally via:

```bash
npm install -g @mockoon/cli
```

### 1. Start the Mock Server

```bash
mockoon-cli start --data "src/test/resources/mockoon-environment.json" --port 8089
```

Verify status:

```bash
curl -s -o /dev/null http://localhost:8089/api/orders
```

### 2. Compile Framework Dependencies

```bash
mvn clean compile test-compile
```

### 3. Run by Profile

#### Smoke tests

```bash
mvn test -Psmoke
```

#### Security tests

```bash
mvn test -Psecurity
```

#### Full regression suite

```bash
mvn test -Pregression
```

#### Run with environment parameter

```bash
mvn test -Pregression -Denv=qa
```

---

## CI/CD Parameterized Pipelines

### Jenkins Declarative Pipeline

The Jenkins pipeline executes on Windows agents, manages the Mockoon server in the background, and publishes both TestNG and ExtentReports outputs.

```groovy
pipeline {
    agent any

    parameters {
        choice(name: 'TEST_SUITE', choices: ['regression', 'smoke', 'security'])
        choice(name: 'ENVIRONMENT', choices: ['qa', 'dev', 'stage'])
        string(name: 'RECIPIENTS', defaultValue: 'qa-reports@company.local')
    }

    tools {
        jdk 'JAVA_HOME'
        maven 'MAVEN_HOME'
    }

    environment {
        MOCK_PORT = '8089'
    }

    stages {
        stage('Checkout Source') {
            steps { checkout scm }
        }

        stage('Start Mock Server') {
            steps {
                script {
                    bat "start /B mockoon-cli start --data \"src/test/resources/mockoon-environment.json\" --port %MOCK_PORT%"
                }
            }
        }

        stage('Run API Test Suite') {
            steps {
                bat "mvn test -P${params.TEST_SUITE} -Denv=${params.ENVIRONMENT}"
            }
        }
    }
}
```

### GitHub Actions Workflow

Configured at `.github/workflows/api-automation-ci.yml` for push, pull request, nightly schedule, and manual runs.

```yaml
name: API Test Automation Suite

on:
  push:
    branches: ["main"]
  pull_request:
    branches: ["main"]
  schedule:
    - cron: '0 18 * * *'
  workflow_dispatch:
    inputs:
      test_suite:
        description: 'Select TestNG suite profile'
        required: true
        default: 'regression'
        type: choice
        options:
          - regression
          - smoke
          - security
      environment:
        description: 'Target test environment'
        required: true
        default: 'qa'
        type: choice
        options:
          - qa
          - dev
          - stage

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Install and Start Mockoon CLI
        run: |
          npm install -g @mockoon/cli
          mockoon-cli start --data "src/test/resources/mockoon-environment.json" --port 8089

      - name: Execute Maven Tests
        run: |
          SUITE="${{ github.event.inputs.test_suite || 'regression' }}"
          ENV="${{ github.event.inputs.environment || 'qa' }}"
          mvn clean test -P${SUITE} -Denv=${ENV}

      - name: Upload Extent Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: Extent-Report
          path: target/extent-reports/
          if-no-files-found: ignore
```

---

## Version Control & Secrets Hygiene

- Untracked IDE artifacts are excluded via `.gitignore`.
- Local logs such as `mockoon.log` and `logs/` are kept out of source control.
- Credentials, SMTP settings, and environment-specific secrets are managed through Jenkins credentials or environment variables.
- Local secret overrides should stay in ignored local config files and never be committed.

---

## Troubleshooting

- Wrong Java version: run `java -version` and `mvn -version` and confirm JDK 21 is active.
- Maven cannot find a suite file: verify the selected profile matches the intended TestNG XML file.
- Mock API unavailable: confirm Mockoon CLI is installed and port `8089` is free.
- Tests fail due to credentials or endpoints: review `src/test/resources/config.properties` and use environment-specific overrides.
- Report missing: check Maven logs and confirm the configured report directory was generated.
- Jenkins `bat` step fails on Linux: replace Windows `bat` usage with `sh` commands and adapt the Mockoon lifecycle logic.

---

## Configuration and Secret Management

Keep credentials and tokens out of source control. Prefer Jenkins Credentials, environment variables, or local ignored configuration files for secure runtime values. Use synthetic/mock data in development and test environments whenever possible.

---

## Suggested LinkedIn Project Description

**Enterprise REST Assured API Automation Framework | Java 21 | REST Assured | TestNG | Maven | Jenkins**

A modular API automation framework built around reusable service classes, centralized configuration, authentication coverage, API chaining, JSON schema validation, retry handling, and CI/CD integration. It is designed for enterprise-grade validation of REST services with strong reporting, test isolation, and maintainable scalability.

**Core skills:** REST API testing, Java, REST Assured, TestNG, Maven, API security testing, JSON validation, reporting, and CI/CD.

> Before publishing, verify that the listed dependencies, suite names, report paths, Mockoon commands, and test counts align with the current project files.

---

This README was refined for improved readability, cleaner markdown structure, and more consistent formatting across sections and code examples.
