
pipeline {
    agent any

    environment {
        MAVEN_HOME = 'C:\\Program Files\\Apache\\Maven\\apache-maven-3.9.16'
        ACR_NAME = 'mallparkingacr1793939759'
        ACR_LOGIN = 'mallparkingacr1793939759.azurecr.io'
        IMAGE_NAME = 'mall-parking-app'
        RESOURCE_GROUP = 'mall-parking-rg'
        AKS_NAME = 'mall-parking-aks'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code from GitHub'
                checkout scm
            }
        }

        stage('Verify Tools') {
            steps {
                bat '''
                    "%MAVEN_HOME%\\bin\\mvn.cmd" -version
                    docker --version
                    az --version
                '''
            }
        }

        stage('Build Application') {
            steps {
                echo 'Building Spring Boot application using Maven'
                bat '''
                    "%MAVEN_HOME%\\bin\\mvn.cmd" clean package -DskipTests
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Mall Parking Docker image'
                bat '''
                    docker build -t %ACR_LOGIN%/%IMAGE_NAME%:v%BUILD_NUMBER% .
                '''
            }
        }

        stage('Login to Azure Container Registry') {
            steps {
                bat '''
                    az acr login --name %ACR_NAME%
                '''
            }
        }

        stage('Push Image to ACR') {
            steps {
                bat '''
                    docker push %ACR_LOGIN%/%IMAGE_NAME%:v%BUILD_NUMBER%
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Application built and Docker image pushed to Azure Container Registry!'
        }

        failure {
            echo 'FAILED: Check the failed stage in Jenkins Console Output.'
        }

        always {
            echo 'Mall Parking CI/CD pipeline execution completed.'
        }
    }
}
