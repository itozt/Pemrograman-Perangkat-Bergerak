@echo off
cd /d "d:\Kuliah\Semester 6\Pemrograman Perangkat Bergerak\ETS-PPB"
call gradlew.bat compileDebugKotlin
echo Build completed with exit code: %ERRORLEVEL%
