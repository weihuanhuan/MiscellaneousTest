@echo off

setlocal
set BIN_HOME="%~dp0"

set CLASSPAT=bootstrap.jar
REM 修改环境里面之前设置的 LIB 值，否则可能会受到环境之前存在的影响。

if not exist %BIN_HOME%\extlib\*.jar goto finishedLib
set EXTLIB=
for /r %BIN_HOME%\extlib   %%f  in (*.jar) do (
    echo %%~f	
    call set EXTLIB=%%EXTLIB%%;%%~f

    REM call echo %%EXTLIB%%
    REM 这个 echo 会输出 echo is off ，这里使用了 call，而call传递引用需要双重%%对，如%XXX%时，应该使用%%XXX%%
 
)
:finishedLib

set CLASSPAT=%CLASSPAT%%EXTLIB%
echo ----------EXTLIB--------------------------
echo %EXTLIB%
echo ----------CLASSPATH--------------------
echo %CLASSPAT%

endlocal