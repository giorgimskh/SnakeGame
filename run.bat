@echo off
setlocal ENABLEDELAYEDEXPANSION

REM Create bin directory
if not exist bin mkdir bin

REM Compile sources
echo Compiling sources...
javac -source 8 -target 8 -Xlint:-options -d bin -sourcepath src src\App.java
if errorlevel 1 (
  echo Compilation failed.
  exit /b 1
)

REM Copy resources, which are loaded from the classpath
for %%D in (sounds fonts) do (
  if exist src\%%D xcopy /E /I /Y src\%%D bin\%%D >nul 2>nul
)

REM Run the app
echo Running Snake Game...
java -cp bin App

endlocal

