
pipeline {
    agent any

    environment {
        ACR_NAME = 'mallparkingacr1793939759'
        ACR_LOGIN = 'mallparkingacr1793939759.azurecr.io'
        IMAGE_NAME = 'mall-parking-app'
        AKS_NAME = 'mall-parking-aks'
        RESOURCE_GROUP = 'mall-parking-rg'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Application') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t %ACR_LOGIN%/%IMAGE_NAME%:v%BUILD_NUMBER% .'
            }
        }

        stage('Login to Azure') {
            steps {
                bat 'az acr login --name %ACR_NAME%'
            }
        }

        stage('Push to ACR') {
            steps {
                bat 'docker push %ACR_LOGIN%/%IMAGE_NAME%:v%BUILD_NUMBER%'
            }
        }

        stage('Deploy to AKS') {
            steps {
                bat '''
                az aks get-credentials --resource-group %RESOURCE_GROUP% --name %AKS_NAME% --overwrite-existing
                kubectl apply -f k8s/
                '''
            }
        }
    }

    post {
        success {
            echo 'Mall Parking CI/CD Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
