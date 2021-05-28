@echo off

REM 在 batch 中 REM 表示的注释，如果末尾出现了中文句号，那么接连的两行注释中的前一行会有解析问题.
REM 这个问题的原因未知，所以我们最好直接将他们按照空格隔开，避免奇怪的问题, 当于不使用中文的句号也是可以的。但是需要使用时就只能分开了。

REM 对于复杂的命令，我们可以使用 set 定义一个变量来表示他，并在 for 中用使用单引号结构 ('%variable-name%') 来调用，此时命令的修改和使用都比较方便。

REM 而当其中包含空格之类特殊字符的，需要在 for 结构的原有形式中添加这样的 ('"%variable-name%"') 双引号结构进行额外的包装才能正确的表示命令含义。

set execCmd="%JAVA_HOME%/bin/java" -version 2^>^&1
echo ####
echo #### execCmd start ####
echo %execCmd%
echo #### execCmd end ####
echo ####

REM 我们可以通过连接一个 LF 来将多行结果的内容输出拼接到一个变量中，再供之后使用这个变量。

setlocal EnableDelayedExpansion
set LF=^


REM The two empty lines are required here
set "output="
for /F "delims=" %%f in ('"%execCmd%"') do (
    if defined output set "output=!output!!LF!"
    set "output=!output!%%f"
)

echo ####
echo #### output start ####
echo !output!
echo #### output end ####
echo ####

pause