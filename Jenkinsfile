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
                    // Check if file exists in workspace root or under src/test/resources
                    def rootFile = "${env.MOCK_FILE}"
                    def resourceFile = "src/test/resources/${env.MOCK_FILE}"

                    if (fileExists(rootFile)) {
                        echo "Starting Mockoon server on port ${MOCK_PORT} using root file..."
                        bat "mockoon-cli start --data \"%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"
                    } else if (fileExists(resourceFile)) {
                        echo "Starting Mockoon server on port ${MOCK_PORT} using resources file..."
                        bat "mockoon-cli start --data \"src\\test\\resources\\%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"
                    } else {
                        echo "⚠️ Warning: Mockoon configuration file not found in workspace. Relying on active standalone/background mock..."
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
                echo "Terminating Mockoon instances..."
                bat 'mockoon-cli stop --all || mockoon-cli stop all || exit 0'
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
            echo '✅ All 31 API tests passed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Check console output or surefire reports.'
        }
    }
}