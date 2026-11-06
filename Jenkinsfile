
pipeline {
    agent any

    environment {
        // Windows tool paths
        MAVEN_HOME = 'C:\\Program Files\\Apache\\Maven\\apache-maven-3.9.16'
        AZURE_CLI = 'C:\\Program Files\\Microsoft SDKs\\Azure\\CLI2\\wbin\\az.cmd'

        // Azure Container Registry
        ACR_NAME = 'mallparkingacr1793939759'
        ACR_LOGIN = 'mallparkingacr1793939759.azurecr.io'
        IMAGE_NAME = 'mall-parking-app'

        // Azure Kubernetes Service
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
                echo 'Verifying Maven, Docker and Azure CLI'

                bat '''
                    "%MAVEN_HOME%\\bin\\mvn.cmd" -version
                    docker --version
                    "%AZURE_CLI%" --version
                '''
            }
        }

        stage('Build Application') {
            steps {
                echo 'Building Spring Boot application'

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

        stage('Login to Azure') {
            steps {
                echo 'Authenticating with Azure Service Principal'

                withCredentials([
                    usernamePassword(
                        credentialsId: 'azure-sp',
                        usernameVariable: 'AZURE_CLIENT_ID',
                        passwordVariable: 'AZURE_CLIENT_SECRET'
                    ),
                    string(
                        credentialsId: 'azure-tenant-id',
                        variable: 'AZURE_TENANT_ID'
                    )
                ]) {
                    bat '''
                        "%AZURE_CLI%" login --service-principal ^
                        -u "%AZURE_CLIENT_ID%" ^
                        -p "%AZURE_CLIENT_SECRET%" ^
                        --tenant "%AZURE_TENANT_ID%"
                    '''
                }
            }
        }

        stage('Login to ACR') {
            steps {
                echo 'Authenticating with Azure Container Registry'

                bat '''
                    "%AZURE_CLI%" acr login --name %ACR_NAME%
                '''
            }
        }

        stage('Push Image to ACR') {
            steps {
                echo 'Pushing Docker image to Azure Container Registry'

                bat '''
                    docker push %ACR_LOGIN%/%IMAGE_NAME%:v%BUILD_NUMBER%
                '''
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Application built and Docker image pushed to ACR!'
        }

        failure {
            echo 'FAILED: Check the failed stage in Jenkins Console Output.'
        }

        always {
            echo 'Mall Parking CI/CD Pipeline execution completed.'
        }
    }
}
