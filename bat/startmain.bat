@echo off
set a=1
echo start startchild.bat
start "startchild.bat" "%~dp0%\startchild.bat"
echo end start
echo %b%
pause>nul

REM start命令会将第一个带双引号的参数理解为新cmd的标题，start /? 精简版 START ["title"]  [command/program] [parameters]，
REM 注意第一个可选的参数，call 命令没有这个问题。
REM so，直接 start "%~dp0%\startchild.bat" 相当与开启一个 标题为 %~dp0%\startchild.bat 的新窗口，而不是运行 %~dp0%\startchild.bat 脚本

REM 同时，也要注意窗口的名字不能是 【变量扩展+字母】的组合  如：%~dp0%abc不可以，但是数字可以，如：%~dp0%123,
REM 否则，这个选项的字母部分忽略，同时和不执行扩展的下一个选项连接起来一起作为标题，为什么？
REM 例如，"%~dp0%abc" "%~dp0%\startchild123.bat"，标题为 F:\JetBrains\IntelliJ IDEA\BEStest\bat\-dp0\startchild123bat
REM 但是%后边的第一个数字会被吞掉，为什么？

REM 最终作为判断是否调用 指定脚本的方式是，将脚本改名为错误的，如果这时确实调用脚本，则会报出 windows cannot find XXX脚本 的错误
REM 所以为了避免目前这些未知的麻烦，【不要】在标题中使用 【扩展变量】 。