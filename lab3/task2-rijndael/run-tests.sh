#!/bin/bash

cd "$(dirname "$0")"

if [ ! -d "out/test" ]; then
    echo "Building project first..."
    ./build.sh
fi

echo "Running tests..."
java -cp out/production:out/test RijndaelTest

