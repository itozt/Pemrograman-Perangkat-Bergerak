@echo off
cd /d "%~dp0"
echo Compiling unit tests...
call gradlew.bat compileDebugUnitTestKotlin -x check
pause
