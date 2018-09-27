@echo off

setlocal

set OLDCD=%CD%
cd %~dp0\..
set TOOL_HOME="%CD%"
cd %OLDCD%

set TOOL_LIB_HOME=%TOOL_HOME%\lib

set CLASSPATH=%TOOL_LIB_HOME%\*

rem start up the server mode of db
call %TOOL_HOME%\jre\bin\java -cp %CLASSPATH% org.hsqldb.server.Server --database.0 file:qiji --dbname.0 pim


endlocal