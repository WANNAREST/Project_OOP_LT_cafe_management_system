@echo off
if "%~1"=="" (
    echo Usage: run-current-file.bat YourClassName
    echo Example: run-current-file.bat Shop
    echo Example: run-current-file.bat main.Shop
    pause
    exit /b 1
)

echo 🚀 Running %1...
echo.

REM Compile first if needed
call "C:\Program Files\Apache\maven\bin\mvn.cmd" compile -q

REM Run the Java file with proper classpath and JavaFX modules
java -cp "target\classes;target\dependency\*" --module-path "target\dependency" --add-modules javafx.controls,javafx.fxml %1

echo.
echo ✅ Execution completed!
pause 