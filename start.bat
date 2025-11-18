@echo off
echo ==================================
echo User Management Service - Quick Start
echo ==================================
echo.

where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Docker is not installed. Please install Docker first.
    exit /b 1
)

where docker-compose >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Docker Compose is not installed. Please install Docker Compose first.
    exit /b 1
)

echo Step 1: Stopping any existing containers...
docker-compose down

echo.
echo Step 2: Building and starting services...
docker-compose up -d --build

echo.
echo Step 3: Waiting for services to be healthy...
echo This may take 30-60 seconds...

set MAX_RETRIES=30
set RETRY_COUNT=0

:wait_loop
if %RETRY_COUNT% geq %MAX_RETRIES% goto timeout

curl -s http://localhost:8080/api/v1/health >nul 2>&1
if %errorlevel% equ 0 goto success

echo|set /p="."
timeout /t 2 /nobreak >nul
set /a RETRY_COUNT+=1
goto wait_loop

:timeout
echo.
echo Failed: Services did not start in time. Please check logs:
echo    docker-compose logs -f app
exit /b 1

:success
echo.
echo.
echo ==================================
echo User Management Service is ready!
echo ==================================
echo.
echo API Documentation: http://localhost:8080/swagger-ui.html
echo Health Check:      http://localhost:8080/api/v1/health
echo API Endpoints:     http://localhost:8080/api/v1/users
echo.
echo ==================================
echo Quick Test Commands:
echo ==================================
echo.
echo 1. Health Check:
echo    curl http://localhost:8080/api/v1/health
echo.
echo 2. Get All Users:
echo    curl http://localhost:8080/api/v1/users
echo.
echo 3. Get User Stats:
echo    curl http://localhost:8080/api/v1/users/stats
echo.
echo ==================================
echo Useful Commands:
echo ==================================
echo.
echo View logs:        docker-compose logs -f app
echo Stop services:    docker-compose down
echo Restart:          docker-compose restart
echo Rebuild:          docker-compose up -d --build
echo.
echo ==================================
