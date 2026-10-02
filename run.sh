#!/usr/bin/env bash
set -euo pipefail

mkdir -p bin

echo "Compiling sources..."
javac -source 8 -target 8 -Xlint:-options -d bin -sourcepath src src/App.java

# Resources are loaded from the classpath
for dir in sounds fonts; do
  if [ -d "src/$dir" ]; then
    mkdir -p "bin/$dir"
    cp -r "src/$dir/"* "bin/$dir/"
  fi
done

echo "Running Snake Game..."
java -cp bin App

