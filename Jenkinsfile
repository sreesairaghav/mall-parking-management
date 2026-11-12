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

        // Branch-aware image tags (multibranch pipeline sets BRANCH_NAME)
        //   main          -> main-v12,          main-latest
        //   dockerfileopt -> dockerfileopt-v3,  dockerfileopt-latest
        IMAGE_TAG = "${env.BRANCH_NAME}-v${env.BUILD_NUMBER}"
        LATEST_TAG = "${env.BRANCH_NAME}-latest"

        // Branches compared in the image size report
        BASE_BRANCH = 'main'
        OPT_BRANCH = 'dockerfileopt'

        // Azure Kubernetes Service
        RESOURCE_GROUP = 'mall-parking-rg'
        AKS_NAME = 'mall-parking-aks'
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out branch ${env.BRANCH_NAME} from GitHub"
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
                echo "Building Docker image ${env.IMAGE_NAME}:${env.IMAGE_TAG}"

                bat '''
                    docker build -t %ACR_LOGIN%/%IMAGE_NAME%:%IMAGE_TAG% -t %ACR_LOGIN%/%IMAGE_NAME%:%LATEST_TAG% .
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
                echo "Pushing ${env.IMAGE_TAG} and ${env.LATEST_TAG} to ACR"

                bat '''
                    docker push %ACR_LOGIN%/%IMAGE_NAME%:%IMAGE_TAG%
                    docker push %ACR_LOGIN%/%IMAGE_NAME%:%LATEST_TAG%
                '''
            }
        }

        stage('Compare Image Sizes') {
            steps {
                echo "Comparing ${env.BASE_BRANCH} vs ${env.OPT_BRANCH} image sizes"

                powershell '''
                    $ErrorActionPreference = "Continue"
                    $repo = "$env:ACR_LOGIN/$env:IMAGE_NAME"

                    # The image just built on this branch is local; pull the other branch's latest from ACR
                    if ($env:BRANCH_NAME -eq $env:BASE_BRANCH) {
                        $baseImg = "${repo}:$env:IMAGE_TAG"
                        $optImg  = "${repo}:$env:OPT_BRANCH-latest"
                        $otherImg = $optImg
                    } else {
                        $baseImg = "${repo}:$env:BASE_BRANCH-latest"
                        $optImg  = "${repo}:$env:IMAGE_TAG"
                        $otherImg = $baseImg
                    }

                    docker pull $otherImg | Out-Null
                    if ($LASTEXITCODE -ne 0) {
                        Write-Host "Image $otherImg is not in ACR yet. Build the other branch once, then re-run to get the comparison."
                        exit 0
                    }

                    $baseSize = [int64](docker image inspect -f "{{.Size}}" $baseImg)
                    $optSize  = [int64](docker image inspect -f "{{.Size}}" $optImg)
                    $saved    = $baseSize - $optSize
                    $pct      = [math]::Round(($saved / $baseSize) * 100, 2)

                    $report = @()
                    $report += "=========== IMAGE SIZE COMPARISON ==========="
                    $report += "Build      : $env:BRANCH_NAME #$env:BUILD_NUMBER"
                    $report += ("{0,-14}: {1,10:N2} MB   ({2})" -f "$env:BASE_BRANCH", ($baseSize / 1MB), $baseImg)
                    $report += ("{0,-14}: {1,10:N2} MB   ({2})" -f "$env:OPT_BRANCH", ($optSize / 1MB), $optImg)
                    $report += ("{0,-14}: {1,10:N2} MB   ({2} % smaller)" -f "Saved", ($saved / 1MB), $pct)
                    $report += "=============================================="
                    $report += ""
                    $report += "--- Layers: $env:BASE_BRANCH ---"
                    $report += (docker history --format "{{.Size}}`t{{.CreatedBy}}" $baseImg)
                    $report += ""
                    $report += "--- Layers: $env:OPT_BRANCH ---"
                    $report += (docker history --format "{{.Size}}`t{{.CreatedBy}}" $optImg)
                    $report += ""
                    $report += "--- Local images ---"
                    $report += (docker images $repo --format "table {{.Repository}}:{{.Tag}}`t{{.Size}}")
                    $report += ""
                    $report += "--- Tags in ACR ---"
                    $report += (& $env:AZURE_CLI acr repository show-tags --name $env:ACR_NAME --repository $env:IMAGE_NAME --orderby time_desc --output table)

                    $report | ForEach-Object { Write-Host $_ }
                    $report | Out-File -Encoding utf8 image-size-report.txt
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'image-size-report.txt', allowEmptyArchive: true
                }
            }
        }

        stage('Deploy to AKS') {
            // Only the main branch is deployed; feature branches stop after pushing to ACR
            when {
                branch 'main'
            }
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

                        call "%AZURE_CLI%" aks install-cli ^
                        --install-location "%WORKSPACE%\\kubectl.exe" ^
                        --kubelogin-install-location "%WORKSPACE%\\kubelogin.exe"

                        if errorlevel 1 exit /b 1

                        echo Connecting to AKS...

                        call "%AZURE_CLI%" aks get-credentials ^
                        --resource-group %RESOURCE_GROUP% ^
                        --name %AKS_NAME% ^
                        --overwrite-existing

                        if errorlevel 1 exit /b 1

                        echo Configuring Kubernetes authentication...

                        set "PATH=%WORKSPACE%;%PATH%"

                        "%WORKSPACE%\\kubelogin.exe" convert-kubeconfig -l azurecli

                        if errorlevel 1 exit /b 1

                        echo Applying database manifest...

                        "%WORKSPACE%\\kubectl.exe" apply -f k8s\\postgres.yaml

                        if errorlevel 1 exit /b 1

                        echo Deploying Mall Parking application %IMAGE_TAG%...

                        powershell -NoProfile -Command "(Get-Content k8s\\app.yaml) -replace ':main-latest', ':%IMAGE_TAG%' | & '%WORKSPACE%\\kubectl.exe' apply -f -"

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
            echo "SUCCESS: ${env.BRANCH_NAME} built and pushed to ACR as ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
            echo 'See image-size-report.txt in the build artifacts for the size comparison.'
        }

        failure {
            echo 'FAILED: Check the failed stage in Jenkins Console Output.'
        }

        always {
            echo 'Mall Parking Management Pipeline execution completed.'
        }
    }
}
