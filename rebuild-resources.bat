@echo off
echo 🔄 Rebuilding Project Resources...
echo.
echo This will recompile the project to include newly added images in the classpath.
echo.

REM Process resources to include new images
call "C:\Program Files\Apache\maven\bin\mvn.cmd" process-resources -q

echo.
echo ✅ Resources rebuilt successfully!
echo 📁 Newly added images are now available in the classpath
echo.
echo You can now:
echo 1. Restart the application to see new images properly
echo 2. Or continue using - images will load from file system temporarily
echo.
pause 