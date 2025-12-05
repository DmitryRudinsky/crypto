#!/bin/bash

cd "$(dirname "$0")"

echo "Compiling source files..."
javac -d out/production src/*.java

if [ $? -eq 0 ]; then
    echo "Compiling test files..."
    javac -cp out/production -d out/test test/*.java
    
    if [ $? -eq 0 ]; then
        echo "Build successful!"
    else
        echo "Test compilation failed!"
        exit 1
    fi
else
    echo "Source compilation failed!"
    exit 1
fi
