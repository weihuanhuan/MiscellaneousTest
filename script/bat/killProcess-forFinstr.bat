@echo off

set JPS_EXEC="%JAVA_HOME%\bin\jps.exe"

set PID=
for /f "usebackq tokens=1" %%v in ( `%%JPS_EXEC%% ^| findstr "Bootstrap"` ) do set PID=%%v
REM 注意 for语句的选项部分要放在双引号中，且使用空格分隔每个选项，而不是使用逗号;因为每个选项的参数使用逗号分隔。
REM  | 和 % 需要转义，而cmd使用 ^来转义绝大部分字符，而 % 需要使用其自身来转义，即 %%。

echo %PID%
taskkill /f /pid %PID% > nul 2>&1 && echo Killtask Tomcat successfully || echo Tomcat isn't running.
