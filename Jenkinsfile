pipeline {
    agent any

    tools {
        jdk 'JDK_21'       // Replace with the name configured in Manage Jenkins -> Tools
        maven 'Maven_3.9.9' // Replace with the name configured in Manage Jenkins -> Tools
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout code from Git
                checkout scm
            }
        }

        stage('Execute API Test Suite') {
            steps {
                // Run the TestNG suite defined in testng.xml
                bat 'mvn clean test -DsuiteXmlFile=testng.xml'
            }
        }
    }

    post {
        always {
            // Archive standard TestNG / Surefire reports
            junit 'target/surefire-reports/*.xml'

            // Publish interactive ExtentReports HTML report
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/extent-reports',
                reportFiles: 'extentReport.html',
                reportName: 'Extent Report',
                reportTitles: 'API Test Execution Report'
            ])
        }
    }
}