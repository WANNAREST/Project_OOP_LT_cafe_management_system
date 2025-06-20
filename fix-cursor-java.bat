@echo off
echo 🔧 Fixing Cursor Java Support...

echo 🧹 Cleaning old project files...
if exist .project del /f .project
if exist .classpath del /f .classpath
if exist .settings rmdir /s /q .settings

echo 📦 Cleaning Maven cache...
call "C:\Program Files\Apache\maven\bin\mvn.cmd" clean

echo 📥 Downloading dependencies...
call "C:\Program Files\Apache\maven\bin\mvn.cmd" dependency:copy-dependencies -DincludeScope=compile

echo 🔄 Compiling project...
call "C:\Program Files\Apache\maven\bin\mvn.cmd" compile

echo 📁 Generating Eclipse project files...
call "C:\Program Files\Apache\maven\bin\mvn.cmd" eclipse:eclipse

echo ✨ Creating proper .project file...
echo ^<?xml version="1.0" encoding="UTF-8"?^> > .project
echo ^<projectDescription^> >> .project
echo   ^<name^>cafe-management-system^</name^> >> .project
echo   ^<comment^>A comprehensive cafe management system built with JavaFX^</comment^> >> .project
echo   ^<projects/^> >> .project
echo   ^<buildSpec^> >> .project
echo     ^<buildCommand^> >> .project
echo       ^<name^>org.eclipse.jdt.core.javabuilder^</name^> >> .project
echo     ^</buildCommand^> >> .project
echo   ^</buildSpec^> >> .project
echo   ^<natures^> >> .project
echo     ^<nature^>org.eclipse.jdt.core.javanature^</nature^> >> .project
echo   ^</natures^> >> .project
echo ^</projectDescription^> >> .project

echo ✅ Done! Now:
echo 1. Close Cursor completely
echo 2. Reopen your project folder in Cursor
echo 3. Press Ctrl+Shift+P
echo 4. Run: "Java: Reload Projects"
echo 5. Run: "Developer: Reload Window"
echo.
echo Your project should now be recognized as a valid Java project! 🎉
pause 