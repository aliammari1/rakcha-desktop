@echo off
:: Docker management script for Windows users

:: Check if Docker Desktop is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker is not running! Please start Docker Desktop first.
    exit /b 1
)

if "%1"=="start" (
    echo Starting Docker containers...
    docker-compose up -d
    echo Docker containers started successfully!
    exit /b 0
)

if "%1"=="stop" (
    echo Stopping Docker containers...
    docker-compose down
    echo Docker containers stopped successfully!
    exit /b 0
)

if "%1"=="restart" (
    echo Restarting Docker containers...
    docker-compose down
    docker-compose up -d
    echo Docker containers restarted successfully!
    exit /b 0
)

if "%1"=="logs" (
    echo Showing logs for services...
    docker-compose logs -f
    exit /b 0
)

echo Usage: %0 {start^|stop^|restart^|logs}
exit /b 1
