@echo off
cd /d "%~dp0"
REM Run gradle with stacktrace and info to get detailed error
call gradlew.bat compileDebugUnitTestKotlin --stacktrace > build_debug.txt 2>&1
echo Build output saved to build_debug.txt
timeout /t 2
start notepad build_debug.txt
