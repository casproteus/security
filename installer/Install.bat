@echo off

if "%OS%"=="Windows_NT" goto nt
echo This script only works with NT-based versions of Windows.
goto :eof

:nt
md c:\SanjiPos
"%~dp0\jre\bin\java" -jar stgo.jar