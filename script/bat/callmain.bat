@echo off
set a=1
echo call callchild.bat
call "%~dp0%\callchild.bat"
echo end call
echo %b%
pause>nul

REM 注意：call执行后会返回原来的批处理文件，
REM 如果直接调用批处理文件，不用call的话，程序控制权将转义至新的批处理脚本中，
REM 新的脚本执行完就结束了，不会返回到调用者的脚本中执行剩余的语句

REM 如下示例
echo start not call，will echo end not call when finsih. 
"%~dp0%\callchild.bat"
echo end not call