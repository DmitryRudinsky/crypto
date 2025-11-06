#!/bin/bash

echo "======================================"
echo "  Запуск всех демонстраций Lab1"
echo "======================================"
echo

# Задание 1
echo "==================================="
echo "Задание 1: Перестановка битов"
echo "==================================="
cd task1-bit-permutation
chmod +x run-demo.sh
./run-demo.sh
cd ..
echo
echo "Нажмите Enter для продолжения..."
read

# Задание 2
echo "==================================="
echo "Задание 2: Интерфейсы и CipherContext"
echo "==================================="
cd task2-interfaces
chmod +x run-demo.sh
./run-demo.sh
cd ..
echo
echo "Нажмите Enter для продолжения..."
read

# Задание 3
echo "==================================="
echo "Задание 3: Сеть Фейстеля"
echo "==================================="
cd task3-feistel
chmod +x run-demo.sh
./run-demo.sh
cd ..
echo
echo "Нажмите Enter для продолжения..."
read

# Задание 4
echo "==================================="
echo "Задание 4: DES"
echo "==================================="
cd task4-des
chmod +x run-demo.sh
./run-demo.sh
cd ..
echo
echo "Нажмите Enter для продолжения..."
read

# Задание 5
echo "==================================="
echo "Задание 5: Демонстрация шифрования файлов"
echo "==================================="
cd task5-file-encryption-demo
chmod +x run-demo.sh
./run-demo.sh
cd ..
echo
echo "Нажмите Enter для продолжения..."
read

# Задание 6
echo "==================================="
echo "Задание 6: DEAL"
echo "==================================="
cd task6-deal
chmod +x run-demo.sh
./run-demo.sh
cd ..
echo

echo "======================================"
echo "✓ Все демонстрации завершены!"
echo "======================================"

