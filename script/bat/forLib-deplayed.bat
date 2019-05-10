@echo off

setlocal
set BIN_HOME="%~dp0"

set CLASSPAT=bootstrap.jar
REM 修改环境里面之前设置的 LIB 值，否则可能会受到环境之前存在的影响。

if not exist %BIN_HOME%\extlib\*.jar goto finishedLib
set EXTLIB=
setlocal enabledelayedexpansion
for /r %BIN_HOME%\extlib   %%f  in (*.jar) do (
    echo %%~f	
    set EXTLIB=!EXTLIB!;%%~f

    REM echo !EXTLIB!
    REM 这个 echo 会输出 echo is off ，这里使用了 延迟变量扩展，所以输出引用的变量，如%XXX%时，应该使用!XXX!
    REM 否则使用 %XXX%，因为在预处理阶段会被替换成这条语句之前所定义的值，而之前值为空，所以显示 echo is off
 
)
:finishedLib

set CLASSPAT=%CLASSPAT%%EXTLIB%
echo ----------EXTLIB--------------------------
echo %EXTLIB%
echo ----------CLASSPATH--------------------
echo %CLASSPAT%

endlocal