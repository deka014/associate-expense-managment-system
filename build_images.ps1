Write-Host "Building Docker Images using Spring Boot Buildpacks..."

$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

$services = @("ApiGateway", "IdentityService", "UserService", "ExpenseService")

foreach ($service in $services) {
    Write-Host "`n=============================================="
    Write-Host "Building image for $service..."
    Write-Host "==============================================`n"
    
    Push-Location "d:\associate-expense-managment-system\micro-services\$service"
    
    # We use -Dspring-boot.build-image.imageName to give it a simple name
    $imageName = $service.ToLower()
    
    # The image name uses hyphen style e.g. api-gateway
    if ($imageName -eq "apigateway") { $imageName = "api-gateway" }
    elseif ($imageName -eq "identityservice") { $imageName = "identity-service" }
    elseif ($imageName -eq "userservice") { $imageName = "user-service" }
    elseif ($imageName -eq "expenseservice") { $imageName = "expense-service" }
    
    cmd.exe /c "mvnw.cmd clean package -DskipTests"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Maven build failed for $service" -ForegroundColor Red
        Pop-Location
        exit 1
    }

    $dockerfile = @"
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
"@
    Set-Content -Path "Dockerfile" -Value $dockerfile

    docker build -t "${imageName}:latest" .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Failed to build image for $service" -ForegroundColor Red
        Pop-Location
        exit 1
    }
    
    Pop-Location
}

Write-Host "`nAll images built successfully!" -ForegroundColor Green
