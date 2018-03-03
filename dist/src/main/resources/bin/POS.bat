@echo off

setlocal

set OLDCD=%CD%
cd %~dp0\..
set TOOL_HOME=%CD%
cd %OLDCD%

set TOOL_ETC_HOME=%TOOL_HOME%\etc
set TOOL_LIB_HOME=%TOOL_HOME%\lib

set CLASSPATH=%TOOL_ETC_HOME%;%TOOL_LIB_HOME%\*

rem Tool needs to know where it lives
set JAVA_OPT=-Dexporter.tool.home=%TOOL_HOME%

set MAIN_CLASS=com.opentext.otac.exporter.cli.v105.ExporterToolMain

rem Run tool with -P option to print OTAC home path, the purpose is to check if it's writable, and to get the OTAC_HOME parameter.
set OUTPUT_FILE=%TMP%\otac-10.5-exporter-output
java %JAVA_OPT% -cp %CLASSPATH% %MAIN_CLASS% -P %* >%OUTPUT_FILE%

rem User may have specified -h or -v, or an error may have occurred
if errorlevel 1 goto exit_with_output
if errorlevel 2 goto exit_with_output
if errorlevel 3 goto exit_with_output

set /p OTAC_HOME= <%OUTPUT_FILE%
set OTSP_HOME=%OTAC_HOME%\otsp
set OTSP_ETC_HOME=%OTSP_HOME%\etc
set OTSP_LIB_HOME=%OTSP_HOME%\lib
set OTSP_EXT_HOME=%OTSP_LIB_HOME%\extensions

set CLASSPATH=%TOOL_ETC_HOME%;%TOOL_LIB_HOME%\otac-content-exporter-10.5-10.5-rc2.jar;%TOOL_LIB_HOME%\otac-content-exporter-10.5-cli-10.5-rc2.jar;%OTSP_ETC_HOME%;%OTSP_EXT_HOME%\*;%OTSP_LIB_HOME%\*
rem Run tool with OTAC libraries, 
java %JAVA_OPT% -cp %CLASSPATH% %MAIN_CLASS% %*
rem to support debugging, use the following instead
rem java %JAVA_OPT% -Xdebug -Xrunjdwp:transport=dt_socket,address=48000,server=y,suspend=n -cp %CLASSPATH% %MAIN_CLASS% %*
goto done

:exit_with_output

type %OUTPUT_FILE%

:done

del %OUTPUT_FILE%

endlocal
