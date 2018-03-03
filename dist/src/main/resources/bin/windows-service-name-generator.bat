@echo off

set WINDOWS_SERVICE_NAME=System Security Monitor Service
set CONSTANT=-
set INSTANCE_COUNTER=0
set _REALPATH=%~dp0
set DESCRIPTION=System Security Monitor Service
call :recursion
@echo # Name of the service> "%_REALPATH%..\etc\%"windows-service-name-generator.wrapper.conf
@echo wrapper.ntservice.name=%WINDOWS_SERVICE_NAME%>> "%_REALPATH%..\etc\%"windows-service-name-generator.wrapper.conf
@echo # Display name of the service>> "%_REALPATH%..\etc\%"windows-service-name-generator.wrapper.conf
@echo wrapper.ntservice.displayname=%WINDOWS_SERVICE_NAME%>> "%_REALPATH%..\etc\%"windows-service-name-generator.wrapper.conf
@echo # Description of the service>> "%_REALPATH%..\etc\%"windows-service-name-generator.wrapper.conf
@echo wrapper.ntservice.description=%DESCRIPTION%>> "%_REALPATH%..\etc\%"windows-service-name-generator.wrapper.conf

GOTO end

:recursion
sc query "%WINDOWS_SERVICE_NAME%" > nul
rem windows service name does not existed
if %errorlevel% == 1060 (call :nameNotExist)
rem windows service name existed
if %errorlevel% neq 1060 (call :nameExist)
goto:eof

:nameNotExist
goto:eof

:nameExist
set /A INSTANCE_COUNTER=INSTANCE_COUNTER+1
set WINDOWS_SERVICE_NAME=System Security Monitor Service
set WINDOWS_SERVICE_NAME=%WINDOWS_SERVICE_NAME%%CONSTANT%%INSTANCE_COUNTER%
call :recursion
goto:eof

:end
