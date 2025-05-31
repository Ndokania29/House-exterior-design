@echo off
echo Building the application...
call mvn clean package

if %ERRORLEVEL% EQU 0 (
    echo Build successful!
    echo Starting the application...
    java -jar target\designEx-0.0.1-SNAPSHOT.jar
) else (
    echo Build failed. Please check the errors above.
    exit /b 1
) 