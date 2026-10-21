pipeline {
    agent any

    tools {
        jdk 'JDK_21'          // Match your configured JDK name under Manage Jenkins -> Tools
        maven 'Maven_3.9.9'    // Match your configured Maven name under Manage Jenkins -> Tools
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

        stage('Compile & Validate') {
            steps {
                bat 'mvn test-compile'
            }
        }

        stage('Run API Test Automation Suite') {
            steps {
                // Runs the full suite configured in testng.xml
                bat 'mvn clean test'
            }
        }
    }

    post {
        always {
            // Publish standard Surefire XML test results
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

            // Publish interactive ExtentReports 5 Dashboard
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/extent-reports',
                reportFiles: 'extentReport.html',
                reportName: 'Extent Report',
                reportTitles: 'Enterprise API Test Automation Report'
            ])
        }
        success {
            echo 'All API tests executed and passed successfully!'
        }
        failure {
            echo 'Build failed or test assertions broke. Check the Extent Report or Console Output.'
        }
    }
}