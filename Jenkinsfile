
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
                echo 'Logging in to Azure Container Registry'

                bat '''
                    "%AZURE_CLI%" acr login --name %ACR_NAME%
                '''
            }
        }

        stage('Push Image to ACR') {
            steps {
                echo 'Pushing Docker image to ACR'

                bat '''
                    docker push %ACR_LOGIN%/%IMAGE_NAME%:v%BUILD_NUMBER%
                '''
            }
        }

        stage('Deploy to AKS') {
            steps {
                echo 'Deploying application to Azure Kubernetes Service'

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
                        @echo off

                        echo Installing Kubernetes CLI tools...

                        "%AZURE_CLI%" aks install-cli ^
                        --install-location "%WORKSPACE%\\kubectl.exe" ^
                        --kubelogin-install-location "%WORKSPACE%\\kubelogin.exe"

                        if errorlevel 1 exit /b 1

                        echo Connecting to AKS...

                        "%AZURE_CLI%" aks get-credentials ^
                        --resource-group %RESOURCE_GROUP% ^
                        --name %AKS_NAME% ^
                        --overwrite-existing

                        if errorlevel 1 exit /b 1

                        echo Configuring Kubernetes authentication...

                        set "PATH=%WORKSPACE%;%PATH%"

                        "%WORKSPACE%\\kubelogin.exe" convert-kubeconfig -l azurecli

                        if errorlevel 1 exit /b 1

                        echo Updating Mall Parking application...

                        "%WORKSPACE%\\kubectl.exe" set image ^
                        deployment/mall-parking-app ^
                        mall-parking-app=%ACR_LOGIN%/%IMAGE_NAME%:v%BUILD_NUMBER%

                        if errorlevel 1 exit /b 1

                        echo Waiting for deployment rollout...

                        "%WORKSPACE%\\kubectl.exe" rollout status ^
                        deployment/mall-parking-app ^
                        --timeout=300s

                        if errorlevel 1 exit /b 1

                        echo Checking Kubernetes resources...

                        "%WORKSPACE%\\kubectl.exe" get pods
                        "%WORKSPACE%\\kubectl.exe" get services
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'SUCCESS: Application built, pushed to ACR and deployed to AKS!'
            echo 'Mall Parking Management CI/CD Pipeline completed successfully.'
        }

        failure {
            echo 'FAILED: Check the failed stage in Jenkins Console Output.'
        }

        always {
            echo 'Mall Parking Management Pipeline execution completed.'
        }
    }
}
