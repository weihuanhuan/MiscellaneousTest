@echo off
set a=1
echo call callchild.bat
call "%~dp0%\callchild.bat"
echo end call
echo %b%
pause>nul
