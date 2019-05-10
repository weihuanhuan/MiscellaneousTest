@echo off

set JAVA_EXEC="%JAVA_HOME%\bin\java.exe"

set JDK_VERSION=
for /f "usebackq tokens=3" %%v in ( `%%JAVA_EXEC%% -version 2^>^&1 ^| findstr "version"`  ) do set JDK_VERSION=%%v
REM Java命令的version参数输出使用stderr，需要将其重定向到stdout才能截取输出。  
REM  | 和 % 需要转义，而cmd使用 ^来转义绝大部分字符，而 % 需要使用其自身来转义，即 %%。

echo %JDK_VERSION%
echo %JDK_VERSION:~1,-1% | findstr /r  "^1\.[78]" > nul && echo Maybe 7 or 8 || echo Neither 7 or 8
REM 使用正则确定版本的范围，再利用短路达成三元运算。

