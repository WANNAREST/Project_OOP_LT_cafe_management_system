@echo off
echo 🔄 Force refreshing Cursor Java project...
echo.

echo 📦 Rebuilding Maven project...
call "C:\Program Files\Apache\maven\bin\mvn.cmd" clean compile -q

echo 📥 Copying dependencies...
call "C:\Program Files\Apache\maven\bin\mvn.cmd" dependency:copy-dependencies -DincludeScope=compile -q

echo ✅ Done! Now:
echo 1. Close Cursor completely
echo 2. Reopen your project folder in Cursor
echo 3. Press Ctrl+Shift+P and run: "Java: Reload Projects"
echo 4. Press Ctrl+Shift+P and run: "Developer: Reload Window"
echo 5. Try F5 again
echo.
echo If F5 still doesn't work, use: .\run-app.bat
pause 