pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['regression', 'smoke', 'security'],
            description: 'Select the TestNG profile (maps to testng-suites/testng-*.xml)'
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
                script {
                    echo "Ensuring port ${env.MOCK_PORT} is free from previous runs..."
                    bat 'mockoon-cli stop all || exit 0'
                }
            }
        }

        stage('Start Mock Server') {
            steps {
                script {
                    echo "Starting Mockoon on port ${env.MOCK_PORT} using ${env.MOCK_FILE}..."
                    bat "mockoon-cli start --data \"%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"

                    // Dynamic polling loop to ensure server is responding before launching tests
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
                script {
                    echo "Running TestNG Profile: ${params.TEST_SUITE} | Target Env: ${params.ENVIRONMENT}"
                    bat "mvn test -P${params.TEST_SUITE} -Denv=${params.ENVIRONMENT}"
                }
            }
        }
    }

    post {
        always {
            script {
                echo "Terminating running Mockoon instances..."
                bat 'mockoon-cli stop all || exit 0'
            }

            // 1. Publish standard TestNG / Surefire XML results
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // 2. Publish native TestNG emailable HTML report
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/surefire-reports',
                reportFiles: 'emailable-report.html',
                reportName: 'TestNG Emailable Report',
                reportTitles: 'TestNG Execution Report'
            ])

            // 3. Publish ExtentReports 5 Dashboard
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/extent-reports',
                reportFiles: 'extentReport.html',
                reportName: 'Extent Report',
                reportTitles: 'Enterprise API Test Automation Report'
            ])

            // 4. Archive HTML reports and test artifacts
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