#!/bin/bash

echo "======================================"
echo "    Компиляция всех заданий Lab1"
echo "======================================"
echo

# Задание 1
echo "--- Задание 1: Перестановка битов ---"
cd task1-bit-permutation
chmod +x build.sh
./build.sh
if [ $? -ne 0 ]; then
    echo "❌ Ошибка компиляции задания 1!"
    exit 1
fi
cd ..
echo

# Задание 2
echo "--- Задание 2: Интерфейсы и CipherContext ---"
cd task2-interfaces
chmod +x build.sh
./build.sh
if [ $? -ne 0 ]; then
    echo "❌ Ошибка компиляции задания 2!"
    exit 1
fi
cd ..
echo

# Задание 3
echo "--- Задание 3: Сеть Фейстеля ---"
cd task3-feistel
chmod +x build.sh
./build.sh
if [ $? -ne 0 ]; then
    echo "❌ Ошибка компиляции задания 3!"
    exit 1
fi
cd ..
echo

# Задание 4
echo "--- Задание 4: DES ---"
cd task4-des
chmod +x build.sh
./build.sh
if [ $? -ne 0 ]; then
    echo "❌ Ошибка компиляции задания 4!"
    exit 1
fi
cd ..
echo

# Задание 5
echo "--- Задание 5: Демонстрация шифрования файлов ---"
cd task5-file-encryption-demo
chmod +x build.sh
./build.sh
if [ $? -ne 0 ]; then
    echo "❌ Ошибка компиляции задания 5!"
    exit 1
fi
cd ..
echo

# Задание 6
echo "--- Задание 6: DEAL ---"
cd task6-deal
chmod +x build.sh
./build.sh
if [ $? -ne 0 ]; then
    echo "❌ Ошибка компиляции задания 6!"
    exit 1
fi
cd ..
echo

# Задание 7
echo "--- Задание 7: Демонстрация DEAL ---"
cd task7-deal-file-demo
chmod +x build.sh
./build.sh
if [ $? -ne 0 ]; then
    echo "❌ Ошибка компиляции задания 7!"
    exit 1
fi
cd ..
echo

echo "======================================"
echo "✓ Все задания скомпилированы успешно!"
echo "======================================"

