@echo off
echo 🚀 Starting Cafe Shop Management System...
echo.

REM Compile the project
call "C:\Program Files\Apache\maven\bin\mvn.cmd" clean compile -q

REM Run the main application
call "C:\Program Files\Apache\maven\bin\mvn.cmd" exec:java -Dexec.mainClass="main.Shop" -q

echo.
echo ✅ Application completed!
pause 