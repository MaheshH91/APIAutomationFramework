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

    options {
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
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
                        echo "Starting Mockoon server on port ${MOCK_PORT} using workspace root configuration..."
                        bat "mockoon-cli start --data \"%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"
                    } else if (fileExists(resourceFile)) {
                        echo "Starting Mockoon server on port ${MOCK_PORT} using resources configuration..."
                        bat "mockoon-cli start --data \"src\\test\\resources\\%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"
                    } else {
                        echo "Warning: Mockoon environment file not found in workspace. Relying on active standalone/background mock server..."
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
                    echo "Executing API Test Suite with Profile: ${params.TEST_SUITE}"
                    bat "mvn test -P${params.TEST_SUITE}"
                }
            }
        }
    }

    post {
        always {
            script {
                echo "Terminating Mockoon instances..."
                bat 'mockoon-cli stop all || mockoon-cli stop --all || exit 0'
            }

            // 1. Process standard Surefire/TestNG XML test results
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // 2. Publish TestNG Native HTML Report
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

            // 4. Archive HTML and XML artifacts for download
            archiveArtifacts artifacts: 'target/surefire-reports/**, target/extent-reports/**', allowEmptyArchive: true
        }
        success {
            echo "All tests executed and passed under the '${params.TEST_SUITE}' profile!"
        }
        failure {
            echo "Pipeline failed. Check the console output, ExtentReport, or surefire reports for failure details."
        }
    }
}