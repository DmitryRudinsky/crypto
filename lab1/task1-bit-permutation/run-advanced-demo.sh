#!/bin/bash

cd "$(dirname "$0")"

echo "Компиляция продвинутой демонстрации..."
javac -d out/production src/AdvancedDemo.java src/BitPermutation.java src/BitIndexMode.java

echo ""
echo "Запуск продвинутой демонстрации..."
echo ""

java -cp out/production AdvancedDemo

