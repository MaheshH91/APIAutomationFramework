# Enterprise REST Assured API Automation Framework

<div align="center">
  <img src="https://img.shields.io/badge/Java-25-orange.svg" alt="Java 25" />
  <img src="https://img.shields.io/badge/REST%20Assured-5.5.0-green.svg" alt="REST Assured 5.5.0" />
  <img src="https://img.shields.io/badge/TestNG-7.10.2-red.svg" alt="TestNG 7.10.2" />
  <img src="https://img.shields.io/badge/ExtentReports-5.1.2-blue.svg" alt="ExtentReports 5.1.2" />
  <img src="https://img.shields.io/badge/Datafaker-2.7.0-purple.svg" alt="Datafaker 2.7.0" />
  <img src="https://img.shields.io/badge/Jenkins-Pipeline%20Ready-brightgreen.svg" alt="Jenkins" />
  <img src="https://img.shields.io/badge/GitHub%20Actions-CI%2FCD%20Automated-blue.svg" alt="GitHub Actions" />
</div>

A modular, thread-safe, enterprise-ready REST API test automation framework built with Java 25, REST Assured, TestNG, and Maven. It is designed around layered architecture, reusable service classes, strong configuration management, and CI/CD-friendly execution patterns for realistic API validation and security testing.

This project demonstrates modern API testing practices across authentication, data-driven testing, contract validation, multi-step chaining, parallel execution, and production-like delivery automation.

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

- Layered architecture: `src/main/java` contains reusable framework engines, service clients, request/response POJOs, utilities, and constants; `src/test/java` keeps the test cases and execution logic separate.
- Configuration-driven runtime behavior: `ConfigManager` resolves properties from the classpath and fallback local paths, honoring JVM system properties before defaults.
- State-aware API chaining: downstream tests consume tokens, identifiers, and context values captured by earlier steps using TestNG dependencies and `ITestContext`.
- Thread-safe execution: `TokenManager` caches bearer tokens using `ConcurrentHashMap`, ensuring safe concurrent use across parallel test threads.
- Parallel and retry-aware TestNG setup: listeners and retry strategy support recovery from flaky execution without changing business logic in the tests.
- Modular service layer: each API domain has a dedicated service class (`AuthService`, `OrderService`, `UserProfileManagementService`, `SecurityAuthService`) to keep endpoint logic reusable and maintainable.
- Mock server virtualization: the framework starts a local or CI mock API via Mockoon, allowing deterministic testing without depending on external backends.
- Contract validation: JSON schema assertions are included for payload integrity checks using REST Assured’s schema validator.
- Data generation and realistic payloads: datafaker is used to create dynamic test data for realistic user and order scenarios.

---

## Directory Layout

```text
APIAutomationFramework/
├── .github/
│   └── workflows/
│       └── api-automation-ci.yml        # GitHub Actions pipeline
├── .gitignore                            # Repository hygiene rules
├── Jenkinsfile                           # Jenkins declarative pipeline
├── pom.xml                               # Maven build and profile configuration
├── README.md                             # Project documentation
├── testng.xml                            # Direct suite runner fallback
├── testng-suites/
│   ├── testng-regression.xml             # Full functional + security suite
│   ├── testng-smoke.xml                  # Smoke validation suite
│   └── testng-security.xml               # Security-specific suite
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
│   │           │   ├── AuthService.java
│   │           │   ├── BaseService.java
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
│       │       │   ├── AccountCreationTest.java
│       │       │   ├── AdvancedApiTestingScenariosTest.java
│       │       │   ├── AuthNegativeTest.java
│       │       │   ├── AuthorizationMechanismsTest.java
│       │       │   ├── BaseTest.java
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
| Java | 25 (LTS) | Modern language runtime and compiler target |
| REST Assured | 5.5.0 | API requests, validations, and schema-driven checks |
| TestNG | 7.10.2 | Test runner, dependency logic, parallel execution, data providers |
| Jackson Databind | 2.18.2 | JSON serialization and POJO mapping |
| ExtentReports | 5.1.2 | Interactive HTML reporting dashboard |
| Log4j2 & SLF4J Bridge | 2.24.1 / 2.0.16 | Console and file logging |
| JSON Schema Validator | 5.5.0 | Runtime contract validation |
| Datafaker | 2.7.0 | Realistic random test data generation |
| Mockoon CLI | Latest | Local and CI mock API hosting |
| Jenkins | 2.x | Declarative pipeline automation |
| GitHub Actions | Latest | CI/CD and artifact publishing |

---

## Test Suites & Maven Profiles

The framework is configured with targeted TestNG suites and Maven profiles for different execution scopes.

| Profile ID | Target Suite XML | Focus Area | Command |
| --- | --- | --- | --- |
| `regression` (default) | `testng-suites/testng-regression.xml` | Full functional, security, concurrency, and chaining validation | `mvn test` or `mvn test -Pregression` |
| `smoke` | `testng-suites/testng-smoke.xml` | Core onboarding, auth, and critical path verification | `mvn test -Psmoke` |
| `security` | `testng-suites/testng-security.xml` | Authorization, secret handling, and negative auth validation | `mvn test -Psecurity` |

Example direct suite override:

```bash
mvn clean test -Psmoke -DsuiteXmlFile="testng-suites/testng-smoke.xml"
```

---

## Advanced API Chaining Scenarios

### 1. Order Lifecycle Chain

`OrderFulfillmentChainTest` validates a multi-step order journey where each step depends on the previous result.

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

### 2. OAuth2 Client Credentials Flow

The framework requests a token via `/api/oauth/token`, extracts the token payload, and forwards it into secure downstream requests.

### 3. Suite Context Chaining

The tests demonstrate passing dynamic context between dependent methods using TestNG `ITestContext` and `dependsOnMethods`.

```java
// Producer step
context.setAttribute("CHAINED_USER", generatedUser);

// Consumer step
String targetUser = (String) context.getAttribute("CHAINED_USER");
```

### 4. Data-Driven and Parallel Execution

`ComprehensiveTestNGFeaturesTest` illustrates data providers, parameter injection, thread-based execution, and dependent steps in a single test class.

---

## Authentication & Security Coverage

This framework covers multiple modern and legacy authentication scenarios through `SecurityAuthService` and supporting service APIs:

- Bearer Token (JWT): token acquisition, in-memory cache, and header injection
- Basic Authentication: preemptive and challenge-based authentication flows
- Digest Authentication: handshake-based negotiation and 401 challenge handling
- API Key Authentication: custom header and query parameter validation
- OAuth 2.0 Client Credentials: protected resource access after token exchange
- HMAC / Custom Signature: timestamped request signing via `X-Signature` and `X-Timestamp`
- Negative authentication checks: invalid credentials, token misuse, and rejected endpoints

---

## Reporting & Logging

### Dual Reporting Architecture

1. ExtentReports dashboard at `target/extent-reports/extentReport.html`
2. TestNG suite reports at `target/surefire-reports/emailable-report.html` and `target/surefire-reports/testng-results.xml`
3. Execution logs at `logs/execution.log`

### Centralized Logging Configuration

- `log4j2.xml` config is loaded from test resources
- Request/response logging and assertion traces are handled through `LoggingFilter`
- CI pipelines also publish logs and reports as GitHub Actions artifacts and Gmail email attachments

---

## Getting Started & Local Execution

### Prerequisites

- Java Development Kit (JDK) 25 configured in `JAVA_HOME`
- Apache Maven 3.9+ configured in `PATH`
- Node.js and Mockoon CLI installed globally:

```bash
npm install -g @mockoon/cli
```

### 1. Start the Mock Server

```bash
mockoon-cli start --data "src/test/resources/mockoon-environment.json" --port 8089
```

Verify connectivity:

```bash
curl -s -o /dev/null http://localhost:8089/api/orders
```

### 2. Compile Dependencies

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

The Jenkins pipeline executes on a standard build agent, starts Mockoon in the background, and publishes TestNG and Extent reports.

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

The workflow is defined in `.github/workflows/api-automation-ci.yml` and supports push, pull request, nightly schedule, and manual execution.

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
    timeout-minutes: 15
    env:
      ACTIONS_RUNNER_FORCE_ACTIONS_NODE_VERSION: "node24"
    permissions:
      contents: read
      checks: write
      issues: write
      pull-requests: write

    steps:
      - name: Checkout Source Code
        uses: actions/checkout@v4.2.2

      - name: Set up JDK 25
        uses: actions/setup-java@v4.7.0
        with:
          java-version: '25'
          distribution: 'temurin'
          cache: maven

      - name: Install and Start Mockoon CLI
        run: |
          npm install -g @mockoon/cli

          MOCK_FILE="src/test/resources/mockoon-environment.json"
          if [ ! -f "$MOCK_FILE" ]; then
            MOCK_FILE="mockoon-environment.json"
          fi
          echo "Using mock file: $MOCK_FILE"

          mockoon-cli start --data "$MOCK_FILE" --port 8089 --daemonOff false || \
          nohup mockoon-cli start --data "$MOCK_FILE" --port 8089 > mockoon.log 2>&1 &

          echo "Waiting for Mockoon to respond on port 8089..."
          READY=0
          for i in {1..15}; do
            STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8089/api/orders || true)
            if [ "$STATUS" != "000" ] && [ -n "$STATUS" ]; then
              echo "Mockoon is up and healthy (HTTP $STATUS) on attempt $i."
              READY=1
              break
            fi
            sleep 1
          done

          if [ $READY -eq 0 ]; then
            echo "Mockoon failed to start in time. Mockoon log output:"
            cat mockoon.log 2>/dev/null || true
            exit 1
          fi

      - name: Execute Maven Tests
        run: |
          SUITE="${{ github.event.inputs.test_suite || 'regression' }}"
          ENV="${{ github.event.inputs.environment || 'qa' }}"
          echo "Executing Suite Profile: $SUITE | Env: $ENV"
          mvn clean test -P${SUITE} -Denv=${ENV} -DsuiteXmlFile="testng-suites/testng-${SUITE}.xml"

      - name: Upload Execution Logs
        if: always()
        uses: actions/upload-artifact@v4.6.1
        with:
          name: Execution-Logs
          path: logs/
          if-no-files-found: warn

      - name: Upload Extent Report
        if: always()
        uses: actions/upload-artifact@v4.6.1
        with:
          name: Extent-Report
          path: |
            **/target/extent-reports/**
            **/extent-reports/**
            **/extentReport.html
          if-no-files-found: warn

      - name: Upload TestNG Emailable Report
        if: always()
        uses: actions/upload-artifact@v4.6.1
        with:
          name: TestNG-Reports
          path: |
            **/target/surefire-reports/emailable-report.html
            **/target/surefire-reports/testng-results.xml
          if-no-files-found: warn

      - name: Publish TestNG Results to Checks Tab
        if: always()
        uses: dorny/test-reporter@v3
        with:
          name: TestNG Execution Results
          path: 'target/surefire-reports/**/TEST-*.xml'
          reporter: java-junit
          fail-on-error: false

      - name: Send Test Execution Email Report via Gmail
        if: always()
        uses: dawidd6/action-send-mail@v3
        with:
          server_address: smtp.gmail.com
          server_port: 465
          secure: true
          username: ${{ secrets.MAIL_USERNAME }}
          password: ${{ secrets.MAIL_PASSWORD }}
          subject: "GitHub Actions: API Test Report - ${{ job.status }}"
          to: ${{ secrets.MAIL_TO }}
          from: "API Automation Suite <${{ secrets.MAIL_USERNAME }}>"
          body: |
            Hi Team,

            The automated API test execution on GitHub Actions has completed.

            - Status: ${{ job.status }}
            - Repository: ${{ github.repository }}
            - Workflow Run: https://github.com/${{ github.repository }}/actions/runs/${{ github.run_id }}

            Extent Reports, TestNG Reports, and execution logs are attached below.
          attachments: |
            target/extent-reports/extentReport.html,
            target/surefire-reports/emailable-report.html,
            logs/execution.log
```

---

## Version Control & Secrets Hygiene

- `.gitignore` excludes IDE, logs, and local temporary artifacts
- Mock server logs and generated files stay out of source control
- Credentials, SMTP configuration, and environment-specific secrets must be managed through GitHub Secrets or Jenkins Credentials
- Local override files should remain outside the repository and should never be committed
- Keep `config.properties` values environment-specific and avoid storing production or real secrets in checked-in files

---

## Troubleshooting

- Wrong Java version: run `java -version` and `mvn -version` and confirm JDK 25 is active
- Maven cannot find a suite file: verify the selected profile matches `testng-suites/testng-<suite>.xml`
- Mock API unavailable: confirm Mockoon CLI is installed and port `8089` is free
- Tests fail due to credentials or endpoints: review `src/test/resources/config.properties` and use environment overrides
- Report missing: check the Maven logs and ensure the report directories are generated in `target/`
- Jenkins `bat` step fails on Linux: replace Windows syntax with `sh` and adapt the Mockoon lifecycle logic
- GitHub Actions fails on mock startup: review the `mockoon-cli` startup checks and ensure the JSON file path resolves correctly

---

## Configuration and Secret Management

Keep credentials and tokens out of source control. Prefer GitHub Secrets, Jenkins Credentials, or local ignored config files for runtime values. Use mock or synthetic data for local execution and ensure real production values are never stored in repository files.

The framework already supports environment-specific values through `ConfigManager`, system properties, and `-Denv=<value>` runtime flags.

---

## Suggested LinkedIn Project Description

**Enterprise REST Assured API Automation Framework | Java 25 | REST Assured | TestNG | Maven | Jenkins**

A modular API automation framework built around reusable service classes, centralized configuration, JWT and OAuth2 security coverage, API chaining, JSON schema validation, retry handling, data-driven execution, and CI/CD integration.

**Core skills:** REST API testing, Java, REST Assured, TestNG, Maven, API security testing, JSON validation, reporting, Mockoon, and CI/CD.

> Before publishing, verify the listed dependencies, suite names, report paths, Mockoon commands, and execution patterns align with the current repository implementation.

---

This README has been updated to reflect the project’s current Java 25 setup, Mockoon-driven API validation workflow, advanced security and chaining scenarios, and the actual GitHub Actions/Jenkins automation implemented in the repository.
