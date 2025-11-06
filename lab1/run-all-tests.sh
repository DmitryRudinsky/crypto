#!/bin/bash

echo "======================================"
echo "    Запуск всех тестов Lab1"
echo "======================================"
echo

TOTAL_TESTS=0
PASSED_TASKS=0

# Задание 1
echo "--- Задание 1: Перестановка битов (6 тестов) ---"
cd task1-bit-permutation
chmod +x run-tests.sh
./run-tests.sh
if [ $? -eq 0 ]; then
    PASSED_TASKS=$((PASSED_TASKS + 1))
    TOTAL_TESTS=$((TOTAL_TESTS + 6))
fi
cd ..
echo

# Задание 2
echo "--- Задание 2: Интерфейсы и CipherContext (7 тестов) ---"
cd task2-interfaces
chmod +x run-tests.sh
./run-tests.sh
if [ $? -eq 0 ]; then
    PASSED_TASKS=$((PASSED_TASKS + 1))
    TOTAL_TESTS=$((TOTAL_TESTS + 7))
fi
cd ..
echo

# Задание 3
echo "--- Задание 3: Сеть Фейстеля (6 тестов) ---"
cd task3-feistel
chmod +x run-tests.sh
./run-tests.sh
if [ $? -eq 0 ]; then
    PASSED_TASKS=$((PASSED_TASKS + 1))
    TOTAL_TESTS=$((TOTAL_TESTS + 6))
fi
cd ..
echo

# Задание 4
echo "--- Задание 4: DES (7 тестов) ---"
cd task4-des
chmod +x run-tests.sh
./run-tests.sh
if [ $? -eq 0 ]; then
    PASSED_TASKS=$((PASSED_TASKS + 1))
    TOTAL_TESTS=$((TOTAL_TESTS + 7))
fi
cd ..
echo

# Задание 6
echo "--- Задание 6: DEAL (10 тестов) ---"
cd task6-deal
chmod +x run-tests.sh
./run-tests.sh
if [ $? -eq 0 ]; then
    PASSED_TASKS=$((PASSED_TASKS + 1))
    TOTAL_TESTS=$((TOTAL_TESTS + 10))
fi
cd ..
echo

echo "======================================"
echo "✓ Завершено заданий: $PASSED_TASKS/5"
echo "✓ Всего тестов пройдено: $TOTAL_TESTS"
echo "======================================"

