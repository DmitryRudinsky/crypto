#!/bin/bash

echo "=== Компиляция задания 2: Интерфейсы и CipherContext ==="

# Создание директорий для выходных файлов
mkdir -p out/production
mkdir -p out/test

# Компиляция исходников
echo "Компиляция исходников..."
javac -d out/production src/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции исходников!"
    exit 1
fi

# Компиляция тестов
echo "Компиляция тестов..."
javac -d out/test -cp out/production test/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции тестов!"
    exit 1
fi

echo "✓ Компиляция завершена успешно!"

