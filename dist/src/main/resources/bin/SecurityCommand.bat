@echo off

if "%OS%"=="Windows_NT" goto nt
echo This script only works with NT-based versions of Windows.
goto :eof

:nt
call "%~dp0/setEnv.bat"

:validate
rem Find the requested command.
set COMMAND=
for /F %%v in ('echo %1^|findstr "^console$ ^start$ ^pause$ ^resume$ ^stop$ ^restart$ ^install$ ^remove$ ^status"') do call :exec set COMMAND=%%v

if "%COMMAND%" == "" (
    echo Usage: %0 { console : start : pause : resume : stop : restart : install : remove : status }
    goto :eof
) else (
    shift
)

rem
rem Run the application.
rem At runtime, the current directory will be that of wrapper.exe
rem
call :%COMMAND%
goto :eof

:console
"%_WRAPPER_EXE%" -c %_WRAPPER_CONF% %WRAPPER_ARGS%
goto :eof

:start
"%_WRAPPER_EXE%" -t %_WRAPPER_CONF% %WRAPPER_ARGS%
goto :eof

:pause
"%_WRAPPER_EXE%" -a %_WRAPPER_CONF% %WRAPPER_ARGS%
goto :eof

:resume
"%_WRAPPER_EXE%" -e %_WRAPPER_CONF% %WRAPPER_ARGS%
goto :eof

:stop
"%_WRAPPER_EXE%" -p %_WRAPPER_CONF% %WRAPPER_ARGS%
goto :eof

:install
"%_WRAPPER_EXE%" -i %_WRAPPER_CONF% %WRAPPER_ARGS%
goto :eof

:remove
"%_WRAPPER_EXE%" -r %_WRAPPER_CONF% %WRAPPER_ARGS%
goto :eof

:status
"%_WRAPPER_EXE%" -q %_WRAPPER_CONF% %WRAPPER_ARGS%
goto :eof

:restart
call :stop
call :start
goto :eof

:exec
%*
goto :eof
