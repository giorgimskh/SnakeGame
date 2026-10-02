#!/usr/bin/env bash
set -euo pipefail

mkdir -p bin

echo "Compiling sources..."
javac -d bin -sourcepath src src/App.java

if [ -d src/sounds ]; then
  mkdir -p bin/sounds
  cp -r src/sounds/* bin/sounds/
fi

echo "Running Snake Game..."
java -cp bin App

