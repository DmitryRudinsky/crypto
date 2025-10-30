#!/bin/bash

echo "=== Запуск тестов ==="
echo ""

if [ ! -d "out/test" ] || [ ! -d "out/production" ]; then
    echo "Проект не скомпилирован. Запускаем компиляцию..."
    ./build.sh
    echo ""
fi

java -cp out/test:out/production CipherFrameworkTest

