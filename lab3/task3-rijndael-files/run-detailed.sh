#!/bin/bash

cd "$(dirname "$0")"

if [ ! -d "out/production" ]; then
    echo "Building project first..."
    ./build.sh
fi

echo "Running detailed demo..."
java -cp out/production DetailedDemo

