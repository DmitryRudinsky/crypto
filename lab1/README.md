# Lab 1 - Криптография

## Структура проекта

```
lab1/
├── bit-permutation (1-st task)/     # Задание 1: Перестановка битов
│   ├── src/                         # Исходный код
│   │   ├── BitIndexMode.java
│   │   ├── BitPermutation.java
│   │   └── Demo.java
│   ├── test/                        # Тесты
│   │   └── BitPermutationTest.java
│   └── README.md
│
├── cipher-framework (2-nd task)/    # Задание 2: Фреймворк шифрования
│   ├── src/                         # Исходный код
│   │   ├── KeySchedule.java         # Интерфейс расширения ключа
│   │   ├── RoundFunction.java       # Интерфейс раунд-функции
│   │   ├── SymmetricCipher.java     # Интерфейс симметричного алгоритма
│   │   ├── CipherMode.java          # Режимы шифрования (ECB, CBC, и др.)
│   │   ├── PaddingMode.java         # Режимы набивки
│   │   ├── CipherContext.java       # Контекст шифрования
│   │   ├── DummyCipher.java         # Демо-реализация
│   │   └── Demo.java
│   ├── test/                        # Тесты
│   │   └── CipherFrameworkTest.java
│   └── README.md
│
└── README.md                        # Этот файл
```

## Задание 1: Перестановка битов

Реализация функции для выполнения перестановки битов в массиве байтов с использованием P-блока.

**Параметры функции:**
- Значение для перестановки (массив байтов)
- Правило перестановки (P-блок)
- Режим индексирования битов (от LSB к MSB или наоборот)
- Начальный индекс (0 или 1)

**Быстрый старт:**
```bash
cd "bit-permutation (1-st task)"
./build.sh
./run-tests.sh    # 6 тестов
./run-demo.sh     # 5 примеров
```

**Результаты:** ✅ Все тесты пройдены (6/6)

## Задание 2: Фреймворк симметричного шифрования

Полнофункциональный фреймворк для работы с симметричными криптографическими алгоритмами.

**Компоненты:**

1. **KeySchedule** - интерфейс для расширения ключа (генерация раундовых ключей)
2. **RoundFunction** - интерфейс для раунд-функции шифрования
3. **SymmetricCipher** - интерфейс для симметричного алгоритма
4. **FeistelCipher** - реализация сети Фейстеля (на базе интерфейсов 1 и 2)
5. **CipherContext** - класс контекста с поддержкой:
   - **Режимов шифрования:** ECB, CBC, PCBC, CFB, OFB, CTR, Random Delta
   - **Режимов набивки:** Zeros, ANSI X.923, PKCS7, ISO 10126
   - **Асинхронных операций** (CompletableFuture)
   - **Распараллеливания** вычислений
   - **Работы с файлами**

**Быстрый старт:**
```bash
cd "cipher-framework (2-nd task)"
./build.sh

# Общие тесты и демо
./run-tests.sh    # 7 тестов
./run-demo.sh     # 4 примера

# Сеть Фейстеля
./run-feistel-tests.sh  # 6 тестов
./run-feistel-demo.sh   # 3 примера
```

**Особенности:**
- ✅ Асинхронное выполнение операций
- ✅ Распараллеливание на все ядра процессора
- ✅ 7 режимов шифрования
- ✅ 4 режима набивки
- ✅ Работа с файлами
- ✅ Без внешних зависимостей

**Пример использования:**
```java
DummyCipher cipher = new DummyCipher();
cipher.setEncryptionKey(key);
cipher.setDecryptionKey(key);

CipherContext ctx = new CipherContext(
    cipher, 
    CipherMode.CBC, 
    PaddingMode.PKCS7, 
    8,
    iv
);

CompletableFuture<byte[]> encrypted = ctx.encryptAsync(data);
CompletableFuture<byte[]> decrypted = ctx.decryptAsync(encrypted.join());

ctx.shutdown();
```

## Требования

- Java 8 или выше
- Никаких внешних зависимостей

## Источники

Реализация основана на Go-библиотеке из репозитория `crypto/`.

## Автор

Лабораторная работа №1 по криптографии
