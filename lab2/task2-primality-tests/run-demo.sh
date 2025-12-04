#!/bin/bash

./build.sh

if [ $? -eq 0 ]; then
    echo ""
    echo "Running Demo..."
    echo "============================================"
    java -cp out/production Demo
fi

