#!/bin/bash

cd "$(dirname "$0")"

echo "Compiling library files..."
javac -d out/production lib-src/*.java

if [ $? -ne 0 ]; then
    echo "Library compilation failed!"
    exit 1
fi

echo "Compiling source files..."
javac -cp out/production -d out/production src/*.java

if [ $? -eq 0 ]; then
    echo "Build successful!"
else
    echo "Source compilation failed!"
    exit 1
fi

