#!/bin/bash

# Скрипт для компиляции проекта

echo "=== Компиляция проекта Bit Permutation ==="

# Создаём директории для выходных файлов
mkdir -p out/production
mkdir -p out/test

# Компилируем исходники
echo "Компиляция исходников..."
javac -d out/production src/*.java

if [ $? -eq 0 ]; then
    echo "✓ Исходники скомпилированы успешно"
else
    echo "✗ Ошибка компиляции исходников"
    exit 1
fi

# Компилируем тесты (с зависимостью от исходников)
echo "Компиляция тестов..."
javac -d out/test -cp out/production test/*.java

if [ $? -eq 0 ]; then
    echo "✓ Тесты скомпилированы успешно"
else
    echo "✗ Ошибка компиляции тестов"
    exit 1
fi

echo ""
echo "=== Компиляция завершена ==="
echo ""
echo "Запустите тесты:   ./run-tests.sh"
echo "Запустите demo:    ./run-demo.sh"

