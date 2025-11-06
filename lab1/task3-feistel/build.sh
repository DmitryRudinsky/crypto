#!/bin/bash

echo "=== Компиляция задания 3: Сеть Фейстеля ==="

# Создание директорий для выходных файлов
mkdir -p out/production
mkdir -p out/test

# Сначала компилируем зависимости из task2
echo "Компиляция зависимостей из задания 2..."
javac -d out/production ../task2-interfaces/src/KeySchedule.java ../task2-interfaces/src/RoundFunction.java ../task2-interfaces/src/SymmetricCipher.java
if [ $? -ne 0 ]; then
    echo "Ошибка компиляции зависимостей!"
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

