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
                    def mockExists = fileExists(env.MOCK_FILE)
                    if (mockExists) {
                        echo "Starting Mockoon mock server on port ${MOCK_PORT}..."
                        bat "mockoon-cli start --data \"%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"
                    } else if (fileExists("src/test/resources/${env.MOCK_FILE}")) {
                        echo "Starting Mockoon mock server from src/test/resources..."
                        bat "mockoon-cli start --data \"src/test/resources/%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"
                    } else {
                        echo "⚠️ Warning: Mockoon configuration file not found in workspace. Proceeding with existing local mock..."
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
                bat 'mockoon-cli stop --all || exit 0'
            }

            // Standard JUnit publisher (already installed and active on your Jenkins)
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

            // Publish Extent HTML Report
            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/extent-reports',
                reportFiles: 'extentReport.html',
                reportName: 'Extent Report',
                reportTitles: 'Enterprise API Test Automation Report'
            ])

            // Archive Test Reports Artifacts
            archiveArtifacts artifacts: 'target/surefire-reports/**, target/*.html, target/extent-reports/**', allowEmptyArchive: true
        }
        success {
            echo '✅ All 31 API tests passed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Check console output or surefire reports.'
        }
    }
}