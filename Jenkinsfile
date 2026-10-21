pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'MAVEN_HOME'
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
                bat 'mvn clean test'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

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