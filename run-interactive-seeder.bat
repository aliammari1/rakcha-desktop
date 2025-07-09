@echo off
echo 🎬 RAKCHA Interactive Database Seeder 🎬
echo ========================================
echo.
echo Starting interactive seeder...
echo.

cd /d "%~dp0"

rem Check if Maven is available
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH environment variable
    pause
    exit /b 1
)

rem Compile and run using Maven to include all dependencies
echo Compiling project with Maven...
call mvn clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

rem Run the interactive seeder using Maven exec plugin
echo.
echo Starting Interactive Database Seeder...
echo.
call mvn exec:java -Dexec.mainClass=com.esprit.examples.InteractiveDatabaseSeeder -Dexec.cleanupDaemonThreads=false

echo.
echo Seeding completed!
pause
