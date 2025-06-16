@echo off
echo 🎬 Launching RAKCHA Desktop for Demo...

REM Check if application is built
if not exist "target\classes" (
    echo 🔄 Building application first...
    mvn clean compile -q
)

REM Launch application
echo 🚀 Starting RAKCHA Desktop...
mvn javafx:run
