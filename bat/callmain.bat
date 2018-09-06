@echo off
set a=1
pause>nul
echo call callchild.bat
call callchild.bat
echo end call
pause>nul
echo %b%
pause>nul
