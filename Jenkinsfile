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
        jdk 'JDK_25'
        maven 'MAVEN_HOME'
    }

    environment {
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
                    echo "Freeing port ${env.MOCK_PORT} from previous runs..."
                    bat '''
                    @echo off
                    for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8089 ^| findstr LISTENING') do (
                        echo Terminating process %%a on port 8089...
                        taskkill /F /PID %%a >nul 2>&1
                    )
                    exit 0
                    '''
                }
            }
        }

        stage('Start Mock Server') {
            steps {
                script {
                    def mockCandidates = [
                        'src/test/resources/mockoon-environment.json',
                        'mockoon-environment.json',
                        'src/test/resources/API Automation Mock.json'
                    ]

                    def selectedMock = mockCandidates.find { fileExists(it) }

                    if (!selectedMock) {
                        error("No Mockoon environment JSON file found in workspace!")
                    }

                    echo "Starting Mockoon in background on port ${env.MOCK_PORT} using ${selectedMock}..."
                    
                    // Run Mockoon asynchronously in the background so it does not block the pipeline
                    bat "start /B mockoon-cli start --data \"${selectedMock}\" --port %MOCK_PORT% > mockoon.log 2>&1"

                    // Non-interactive health check polling
                    bat '''
                    @echo off
                    set COUNT=0
                    :poll
                    curl -s -o nul http://localhost:8089/api/orders
                    if %ERRORLEVEL% EQU 0 goto ready
                    set /a COUNT+=1
                    if %COUNT% GEQ 20 (
                        echo Mockoon server failed to bind and respond within 20 seconds.
                        type mockoon.log
                        exit 1
                    )
                    ping 127.0.0.1 -n 2 >nul
                    goto poll
                    :ready
                    echo Mockoon mock server is active and responding on port 8089.
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
                echo "Terminating Mockoon on port ${env.MOCK_PORT}..."
                bat '''
                @echo off
                for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8089 ^| findstr LISTENING') do (
                    taskkill /F /PID %%a >nul 2>&1
                )
                exit 0
                '''
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


// Send Email Notification
            emailext (
                to: 'holkarmahesh@outlook.com',
                subject: "[Jenkins] ${currentBuild.currentResult}: Job '${env.JOB_NAME}' (Build #${env.BUILD_NUMBER})",
                body: """
                    <h2>Build Status: ${currentBuild.currentResult}</h2>
                    <p><b>Job:</b> ${env.JOB_NAME}</p>
                    <p><b>Build Number:</b> #${env.BUILD_NUMBER}</p>
                    <p><b>Test Profile:</b> ${params.TEST_SUITE}</p>
                    <p><b>Environment:</b> ${params.ENVIRONMENT}</p>
                    <p><b>Console Log:</b> <a href="${env.BUILD_URL}console">${env.BUILD_URL}console</a></p>
                    <p><b>Extent Report:</b> <a href="${env.BUILD_URL}Extent_20Report/">View Extent Dashboard</a></p>
                    <p><b>TestNG Report:</b> <a href="${env.BUILD_URL}TestNG_20Emailable_20Report/">View TestNG Report</a></p>
                """,
                mimeType: 'text/html',
                attachmentsPattern: 'target/extent-reports/extentReport.html, target/surefire-reports/emailable-report.html',
                recipientProviders: [developers(), requestor()]
            )



        }
        success {
            echo "All tests executed and passed under profile: '${params.TEST_SUITE}'"
        }
        failure {
            echo "Build failed. Inspect ExtentReport, TestNG reports, or console logs for details."
        }
    }
}