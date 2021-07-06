@echo off
setlocal EnableDelayedExpansion

echo ####
echo #### call start ####
call testResultSvaeToVariable.bat argument1
echo #### call start ####
echo ####

echo ####################################################
echo ####################################################

echo ####
echo #### caller execCmd ####
echo %execCmd%
echo #### caller execCmd ####
echo ####

REM  延迟变量可以在 sub 中 set 的变量，然后在 main 中使用，需要 EnableDelayedExpansion
echo ####
echo #### caller output ####
echo !output!
echo #### caller output ####
echo ####

REM  延迟变量可以使用 参数 来传递，并在 sub 中 set 该 参数，然后在 main 中使用，需要 EnableDelayedExpansion
echo ####
echo #### caller argument1 ####
echo !argument1!
echo #### caller argument1 ####
echo ####

REM  非延迟变量可以直接在 sub 中 set，然后在 main 中使用，不需要 EnableDelayedExpansion 
echo ####
echo #### caller DIRECT_VAR ####
echo %DIRECT_VAR%
echo #### caller DIRECT_VAR ####
echo ####

pause