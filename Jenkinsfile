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
                    echo "Starting Mockoon mock server on port ${MOCK_PORT}..."
                    // Start Mockoon via Windows Batch
                    bat "mockoon-cli start --data \"%MOCK_FILE%\" --port %MOCK_PORT% || exit 0"
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
                // Executes the test suite against the running Mockoon instance
                bat 'mvn test -DsuiteXmlFile=testng.xml'
            }
        }
    }

    post {
        always {
            script {
                echo "Terminating Mockoon mock server instances..."
                bat 'mockoon-cli stop all || exit 0'
            }

            // Publish TestNG XML Results
            testng testResultsPattern: '**/target/surefire-reports/testng-results.xml'

            // Publish JUnit XML Fallback
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

            // Archive Reports Artifacts
            archiveArtifacts artifacts: 'target/surefire-reports/**, target/*.html, target/extent-reports/**', allowEmptyArchive: true
        }
        success {
            echo '✅ All API tests executed and passed successfully!'
        }
        failure {
            echo '❌ Build failed or test assertions broke. Check Extent Report or Console Output.'
        }
    }
}