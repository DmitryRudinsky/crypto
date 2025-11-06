#!/bin/bash

echo "=== Компиляция задания 6: DEAL ==="

# Создание директорий для выходных файлов
mkdir -p out/production
mkdir -p out/test

# Сначала компилируем все зависимости
echo "Компиляция зависимостей..."

# Задание 2
javac -d out/production ../task2-interfaces/src/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 2!"
    exit 1
fi

# Задание 3
javac -d out/production -cp out/production ../task3-feistel/src/FeistelCipher.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 3!"
    exit 1
fi

# Задание 4 (DES для адаптера)
javac -d out/production -cp out/production ../task4-des/src/BitIndexMode.java ../task4-des/src/BitPermutation.java
javac -d out/production -cp out/production ../task4-des/src/DESConstants.java ../task4-des/src/DESKeySchedule.java ../task4-des/src/DESRoundFunction.java
javac -d out/production -cp out/production ../task4-des/src/DES.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 4!"
    exit 1
fi

# Компиляция исходников задания 6
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

