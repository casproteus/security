@echo off
setlocal

set OLDCD=%CD%
cd %~dp0\..
set TOOL_HOME="%CD%"
set CONF_PATH=%CD%
cd %OLDCD%

set TOOL_LIB_HOME=%TOOL_HOME%\lib

set CLASSPATH=%TOOL_LIB_HOME%\*

rem set MAIN_CLASS=org.cas.client.platform.bar.dialog.BarFrame
set MAIN_CLASS=com.opentext.otsp.server.OTMuleServer
rem Run tool with -P option to print OTAC home path, the purpose is to check if it's writable, and to get the OTAC_HOME parameter.
rem %TOOL_HOME%\jre\bin\java -cp %CLASSPATH% %MAIN_CLASS%
%TOOL_HOME%\jre\bin\java -cp %CLASSPATH% %MAIN_CLASS% -config %CONF_PATH%\etc\mule\security

endlocal