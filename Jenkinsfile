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
            description: 'Target test environment'
        )
    }

    tools {
        jdk 'JAVA_HOME'
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
                    echo "Ensuring no orphaned Mockoon processes occupy port ${env.MOCK_PORT}..."
                    bat 'mockoon-cli stop all || exit 0'
                }
            }
        }

        stage('Start Mock Server') {
            steps {
                script {
                    // Check all standard mock file locations in order of priority
                    def mockCandidates = [
                        'mockoon-environment.json',
                        'src/test/resources/mockoon-environment.json',
                        'src/test/resources/API Automation Mock.json'
                    ]

                    def selectedMock = mockCandidates.find { fileExists(it) }

                    if (selectedMock) {
                        echo "Starting Mockoon server on port ${env.MOCK_PORT} using data file: ${selectedMock}"
                        bat "mockoon-cli start --data \"${selectedMock}\" --port %MOCK_PORT% || exit 0"
                        
                        // Brief pause to allow the port binding to settle
                        sleep(time: 2, unit: 'SECONDS')
                    } else {
                        echo "Warning: No Mockoon environment JSON file found. Assuming an external mock server or standalone service is active."
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
                script {
                    echo "Executing API Test Suite | Profile: ${params.TEST_SUITE} | Env: ${params.ENVIRONMENT}"
                    bat "mvn test -P${params.TEST_SUITE} -Denv=${params.ENVIRONMENT}"
                }
            }
        }
    }

    post {
        always {
            script {
                echo "Terminating Mockoon instances..."
                bat 'mockoon-cli stop all || exit 0'
            }

            // 1. Process Surefire TestNG XML test results
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // 2. Publish TestNG Emailable HTML Report
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

            // 4. Archive HTML reports and XML artifacts
            archiveArtifacts artifacts: 'target/surefire-reports/**, target/extent-reports/**', allowEmptyArchive: true
        }
        success {
            echo "All tests executed successfully under the '${params.TEST_SUITE}' profile!"
        }
        failure {
            echo "Pipeline failed. Review console logs, TestNG reports, or ExtentReport dashboard."
        }
    }
}