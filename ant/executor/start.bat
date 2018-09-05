@echo off
setlocal

set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "BIN_HOME=%~dp0%"

set CLASSPATH=
set "CLASSPATH=%CLASSPATH%"

"%JAVA_EXE%"  -version "%*"

endlocal
if %ERRORLEVEL% == "1" goto error
echo 0
exit /b 0

:error
echo 1
exit  /b 1