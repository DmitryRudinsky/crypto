#!/bin/bash

cd "$(dirname "$0")"

echo "Компиляция стресс-тестов..."
javac -d out/test -cp out/production test/BitPermutationStressTest.java

echo ""
echo "Запуск расширенных стресс-тестов..."
echo ""

java -cp out/test:out/production BitPermutationStressTest

