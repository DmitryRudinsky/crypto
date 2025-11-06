#!/bin/bash

echo "=== Запуск демонстрации ==="
echo ""

if [ ! -d "out/production" ]; then
    echo "Проект не скомпилирован. Запускаем компиляцию..."
    ./build.sh
    echo ""
fi

java -cp out/production Demo

