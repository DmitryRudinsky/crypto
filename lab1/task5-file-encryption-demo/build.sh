#!/bin/bash

echo "=== Компиляция задания 5: Демонстрация шифрования файлов ==="

# Создание директорий для выходных файлов
mkdir -p out/production

# Сначала компилируем все зависимости
echo "Компиляция зависимостей..."

# Задание 2
javac -d out/production ../task2-interfaces/src/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 2!"
    exit 1
fi

# Задание 3
javac -d out/production -cp out/production ../task3-feistel/src/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 3!"
    exit 1
fi

# Задание 4
javac -d out/production -cp out/production ../task4-des/src/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей из задания 4!"
    exit 1
fi

# Компиляция исходников задания 5
echo "Компиляция исходников..."
javac -d out/production -cp out/production src/*.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции исходников!"
    exit 1
fi

echo "✓ Компиляция завершена успешно!"

