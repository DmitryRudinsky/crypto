#!/bin/bash

echo "=== Компиляция задания 7: Демонстрация DEAL ==="

# Создание директорий для выходных файлов
mkdir -p out/production
mkdir -p test-files

# Компиляция зависимостей
echo "Компиляция зависимостей..."

# Task 2 - интерфейсы
javac -d out/production ../task2-interfaces/src/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 2!"
    exit 1
fi

# Task 3 - Feistel
javac -d out/production -cp out/production ../task3-feistel/src/FeistelCipher.java ../task3-feistel/src/SimpleKeySchedule.java ../task3-feistel/src/SimpleRoundFunction.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 3!"
    exit 1
fi

# Task 4 - DES
javac -d out/production -cp out/production ../task4-des/src/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 4!"
    exit 1
fi

# Task 6 - DEAL
javac -d out/production -cp out/production ../task6-deal/src/DEAL.java ../task6-deal/src/DEALKeySchedule.java ../task6-deal/src/DESAdapter.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 6!"
    exit 1
fi

# Компиляция исходников задания 7
echo "Компиляция исходников..."
javac -d out/production -cp out/production src/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции исходников!"
    exit 1
fi

echo "✓ Компиляция завершена успешно!"

