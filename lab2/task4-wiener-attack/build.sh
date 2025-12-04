#!/bin/bash

echo "Building Wiener Attack Service..."

mkdir -p out/production
mkdir -p out/test

javac -d out/production lib-src/*.java

if [ $? -ne 0 ]; then
    echo "Build failed for lib-src!"
    exit 1
fi

javac -cp out/production -d out/production src/*.java

if [ $? -eq 0 ]; then
    echo "Build successful!"
    
    javac -cp out/production -d out/test test/*.java
    
    if [ $? -eq 0 ]; then
        echo "Test build successful!"
    else
        echo "Test build failed!"
        exit 1
    fi
else
    echo "Build failed!"
    exit 1
fi

