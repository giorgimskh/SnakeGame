#!/usr/bin/env bash
set -euo pipefail

rm -rf bin
mkdir -p bin
echo "Compiling sources..."
javac -source 8 -target 8 -Xlint:-options -d bin -sourcepath src src/App.java

rm -rf dist
mkdir -p dist

echo "Main-Class: App" > dist/MANIFEST.MF

(cd bin && jar cfm ../dist/SnakeGame.jar ../dist/MANIFEST.MF .)

# Add resources inside the JAR at sounds/ and fonts/
for dir in sounds fonts; do
  if [ -d "src/$dir" ]; then
    (cd src && jar uf ../dist/SnakeGame.jar "$dir")
  fi
done

# Copy for GitHub Pages (docs/)
mkdir -p docs
cp dist/SnakeGame.jar docs/SnakeGame.jar

echo "Built dist/SnakeGame.jar and copied to docs/SnakeGame.jar"

