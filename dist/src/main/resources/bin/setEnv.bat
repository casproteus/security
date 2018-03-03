@echo off

if "%OS%"=="Windows_NT" goto nt
echo This script only works with NT-based versions of Windows.
goto :eof

:nt
rem
rem Find the application home.
rem
rem %~dp0 is location of current script under NT
set _REALPATH=%~dp0

rem
rem Determine the OS bitness
rem
IF "%PROCESSOR_ARCHITECTURE%" == "x86" (
   IF "%PROCESSOR_ARCHITEW6432%" == "" (
rem OS is 32bit
      set OS_TAG=x86-32
   ) ELSE (
rem OS is 64bit
      set OS_TAG=x86-64
   )
) ELSE (
rem OS is 64bit
   set OS_TAG=x86-64
)

rem
rem Set wrapper arguments
rem
set SECURITY_HOME=%~dp0..
rem set LIB_HOME=%SECURITY_HOME%\lib
rem set CONF_HOME=%SECURITY_HOME%\etc
rem set LOG_HOME=%SECURITY_HOME%\log
set BIN_NATIVE_HOME=%SECURITY_HOME%\bin\windows\%OS_TAG%
rem set LIB_NATIVE_HOME=%SECURITY_HOME%\lib\windows\%OS_TAG%

IF NOT EXIST "%SECURITY_HOME%\log" (
	mkdir "%SECURITY_HOME%\log"
)

rem
rem Find Java
rem

rem the assignment below is correct, do not edit -hd
set JDK_PATH=%_REALPATH%..\jre
if EXIST "%JDK_PATH%" (
	echo Using JDK located at "%JDK_PATH%"
	set JAVACMD=%JDK_PATH%\bin\java.exe
)
rem if JDK_PATH not defined, then try JAVA_HOME
if not defined JAVACMD (
    if EXIST "%JAVA_HOME%" (
        echo Using JAVA_HOME as JDK Path: %JAVA_HOME%
        set JAVACMD=%JAVA_HOME%\bin\java.exe
    )
)

if defined WF set JAVACMD=%WF%
if not exist "%JAVACMD%" (
	echo Unable to find Java executable. Please set JAVA_HOME variable.
	exit /b 1
)

set WRAPPER_ARGS=wrapper.console.loglevel=INFO

rem Decide on the wrapper binary.
set _WRAPPER_EXE=%BIN_NATIVE_HOME%\wrapper.exe
if exist "%_WRAPPER_EXE%" goto conf
echo Unable to locate a Wrapper executable using any of the following names:
echo %BIN_NATIVE_HOME%\wrapper.exe
pause
goto :eof

rem
rem Find the wrapper configuration
rem
:conf
set _WRAPPER_CONF="%~f1"
if not %_WRAPPER_CONF%=="" goto :eof
if "%WRAPPER_CONF_FILE%"=="" (
	echo WRAPPER_CONF_FILE is not specified
	exit /b 1
)
set _WRAPPER_CONF="%_REALPATH%..\etc\%WRAPPER_CONF_FILE%"
