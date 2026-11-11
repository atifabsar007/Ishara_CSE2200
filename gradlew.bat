@echo off
where gradle >nul 2>nul
if errorlevel 1 (
  echo Gradle is not installed. Install Gradle 8.11.1 or configure a local Gradle installation in Android Studio.
  exit /b 1
)
gradle %*
