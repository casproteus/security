@echo off

if "%OS%"=="Windows_NT" goto nt
echo This script only works with NT-based versions of Windows.
goto :eof

:nt
rem copy new files

rem xcopy "%~dp0..\cache\*.*" %~dp0.. /s/y
rem del  "%~dp0..\cache\*.*" /s/q
rem rd "%~dp0..\cache\." /s/q

set WRAPPER_CONF_FILE=statistics.wrapper.conf
call "%~dp0\SecurityCommand.bat" %1
