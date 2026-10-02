@echo off
setlocal ENABLEDELAYEDEXPANSION

REM Build classes
if exist bin rmdir /S /Q bin
mkdir bin
echo Compiling sources...
javac -source 8 -target 8 -Xlint:-options -d bin -sourcepath src src\App.java
if errorlevel 1 (
  echo Compilation failed.
  exit /b 1
)

REM Prepare dist directory
if exist dist rmdir /S /Q dist
mkdir dist

REM Create manifest
echo Main-Class: App> dist\MANIFEST.MF

REM Create jar with classes
pushd bin
jar cfm ..\dist\SnakeGame.jar ..\dist\MANIFEST.MF .
popd

REM Add resources inside the JAR at sounds/ and fonts/
pushd src
for %%D in (sounds fonts) do (
  if exist %%D jar uf ..\dist\SnakeGame.jar %%D
)
popd

REM Copy for GitHub Pages (docs/)
if not exist docs mkdir docs
copy /Y dist\SnakeGame.jar docs\SnakeGame.jar >nul

echo Built dist\SnakeGame.jar and copied to docs\SnakeGame.jar

endlocal

