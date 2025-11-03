@echo off
REM RAKCHA Windows Launcher Script
REM This script launches the RAKCHA application with proper JVM settings

REM Set the application directory
set APP_DIR=%~dp0

REM Launch the application
start "" "%APP_DIR%\RAKCHA.exe"
