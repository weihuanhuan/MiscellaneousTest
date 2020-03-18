@echo off
setlocal enabledelayedexpansion

::模拟数组
set files[0]=callchild.bat
set files[1]=callmain.bat
set files[2]=copy.bat

::遍历当前目录文件，for 循环的变量 只支持 单字母 变量。
for /r %%t in (*) do (

    set exist=false

    ::遍历数组变量，使用逗号分割域，提取第二个域作为参数 %%e，其实索引为1
    for /f "tokens=2 delims==" %%e in ('set files[') do (

        ::变量扩展， n 代表文件名 x 代表扩展名 t 外围循环变量
        if %%e == %%~nxt (
            set exist=true
        )
    )

    ::非循环内部变量，需要延迟扩展
    if !exist! == false (
        echo %%t
    )

)

endlocal
pause
