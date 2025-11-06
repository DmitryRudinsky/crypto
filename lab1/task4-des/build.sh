#!/bin/bash

echo "=== Компиляция задания 4: DES ==="

# Создание директорий для выходных файлов
mkdir -p out/production
mkdir -p out/test

# Сначала компилируем зависимости
echo "Компиляция зависимостей..."
javac -d out/production ../task2-interfaces/src/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 2!"
    exit 1
fi

javac -d out/production -cp out/production ../task3-feistel/src/FeistelCipher.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 3!"
    exit 1
fi

# Компиляция исходников
echo "Компиляция исходников..."
javac -d out/production -cp out/production src/*.java
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

