#!/bin/bash

# Скрипт для запуска тестов

echo "=== Запуск тестов ==="
echo ""

# Проверяем, что проект скомпилирован
if [ ! -d "out/test" ] || [ ! -d "out/production" ]; then
    echo "Проект не скомпилирован. Запускаем компиляцию..."
    ./build.sh
    echo ""
fi

# Запускаем тесты
java -cp out/test:out/production BitPermutationTest

