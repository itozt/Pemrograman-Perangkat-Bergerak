@echo off
cd /d "%~dp0"
echo Building APK...
call gradlew.bat assembleDebugAndroidTest > build_output.txt 2>&1
echo Build complete. Check build_output.txt for details.
type build_output.txt | findstr /i "error" 
pause
