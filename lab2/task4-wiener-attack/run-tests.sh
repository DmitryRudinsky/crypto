#!/bin/bash

./build.sh

if [ $? -eq 0 ]; then
    echo ""
    echo "Running Tests..."
    echo "============================================"
    java -cp out/production:out/test WienerAttackTest
fi

