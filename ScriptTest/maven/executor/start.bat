@echo off
setlocal

set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "BIN_HOME=%~dp0%"

set CLASSPATH=
set "CLASSPATH=%CLASSPATH%" 

"%JAVA_EXE%" -Djava.util.logging.config.file="C:\apache-tomcat-8.5.32\conf\logging.properties" -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager  "-Djdk.tls.ephemeralDHKeySize=2048" -Djava.protocol.handler.pkgs=org.apache.catalina.webresources  -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=28003 -Dignore.endorsed.dirs="" -classpath "C:\apache-tomcat-8.5.32\bin\bootstrap.jar;C:\apache-tomcat-8.5.32\bin\tomcat-juli.jar" -Dcatalina.base="C:\apache-tomcat-8.5.32" -Dcatalina.home="C:\apache-tomcat-8.5.32" -Djava.io.tmpdir="C:\apache-tomcat-8.5.32\temp" org.apache.catalina.startup.Bootstrap  start

REM 不使用 start 命令，相当于执行了一个dos命令，就像执行 echo XXX 一样
REM 则会在【本cmd实例】中，即同一个进程执行 java 命令，会阻塞住本CMD的执行
REM 该 JAVA程序 的输入输出流会自动的使用 CMD实例 的输入输出流。
REM 此时如果CMD正常结束，那么JAVA也会结束，但是因为阻塞，所以不会出现这个情况；
REM 如果 CMD进程 意外停止，则 JAVA程序 变为孤儿进程，并持有死去的CMD进程的IO流

REM 使用 start 命令则会在【本cmd实例】开启【一个新的CMD实例】，即新的进程中来执行 java 命令，不会阻塞住本CMD的执行。格式 start "Title" command
REM 新的子进程的输入输出流是其自己的独立的，不使用父进程的。
REM 同时如果父进程正常结束，那么子进程仍然会保持运行，成为一个孤儿进程。

REM 可以使用 ProcessExplorer 查看 结束一个CMD实例时，会一起杀死的关联进程。
REM 同时这里的windows上进程的父子关系组织有些奇怪，和命令的单多进程对应的不明朗，为什么呢？
REM 可以使用 pause 来观察在 start 启动时，如果父进程活着，新的子进程属于他

endlocal
if %ERRORLEVEL% == "1" goto error
echo cmd exitCode 0 print by start.bat
exit /b 0

:error
echo 1
exit  /b 1