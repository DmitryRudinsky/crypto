# Задание 2: Интерфейсы и класс контекста шифрования

## Описание

Реализация основных интерфейсов и класса контекста для работы с симметричными криптографическими алгоритмами.

## Компоненты

### 2.1. Интерфейс KeySchedule

Описывает функционал для расширения ключа (генерации раундовых ключей).

```java
public interface KeySchedule {
    byte[][] expandKey(byte[] key);
}
```

**Параметры:**
- `key` - входной ключ (массив байтов)
- **Возвращает:** массив раундовых ключей (каждый - массив байтов)

### 2.2. Интерфейс RoundFunction

Описывает функционал для выполнения шифрующего преобразования одного раунда.

```java
public interface RoundFunction {
    byte[] encryptBlock(byte[] block, byte[] roundKey);
    byte[] decryptBlock(byte[] block, byte[] roundKey);
}
```

**Параметры:**
- `block` - входной блок (массив байтов)
- `roundKey` - раундовый ключ (массив байтов)
- **Возвращает:** выходной блок (массив байтов)

### 2.3. Интерфейс SymmetricCipher

Описывает функционал симметричного алгоритма шифрования/дешифрования.

```java
public interface SymmetricCipher {
    void setEncryptionKey(byte[] key);
    void setDecryptionKey(byte[] key);
    byte[] encrypt(byte[] block);
    byte[] decrypt(byte[] block);
}
```

**Методы:**
- `setEncryptionKey` - установка ключа шифрования
- `setDecryptionKey` - установка ключа дешифрования
- `encrypt` - шифрование блока
- `decrypt` - дешифрование блока

### 2.4. Класс CipherContext

Основной класс для выполнения операций шифрования/дешифрования с поддержкой различных режимов.

```java
public CipherContext(
    SymmetricCipher cipher,
    CipherMode mode,
    PaddingMode padding,
    int blockSize,
    byte[] iv
)
```

**Параметры конструктора:**
- `cipher` - реализация симметричного алгоритма
- `mode` - режим шифрования (ECB, CBC, PCBC, CFB, OFB, CTR, RANDOM_DELTA)
- `padding` - режим набивки (ZEROS, ANSI_X923, PKCS7, ISO_10126)
- `blockSize` - размер блока в байтах
- `iv` - вектор инициализации (опционально)

## Режимы шифрования (CipherMode)

### ECB (Electronic Codebook)
- Самый простой режим
- Каждый блок шифруется независимо
- **Не требует IV**

### CBC (Cipher Block Chaining)
- Каждый блок XOR-ится с предыдущим зашифрованным блоком
- **Требует IV**
- Популярный и безопасный режим

### PCBC (Propagating Cipher Block Chaining)
- Модификация CBC с распространением ошибок
- **Требует IV**

### CFB (Cipher Feedback)
- Превращает блочный шифр в потоковый
- **Требует IV**

### OFB (Output Feedback)
- Потоковый режим
- **Требует IV**
- Шифрование и дешифрование идентичны

### CTR (Counter)
- Режим счетчика
- **Требует IV (счетчик)**
- Позволяет распараллеливание

### RANDOM_DELTA
- Каждый блок XOR-ится со случайной дельтой
- **Не требует IV**
- Удваивает размер зашифрованных данных

## Режимы набивки (PaddingMode)

### ZEROS
- Дополняет блок нулями
- Простой, но может потерять конечные нули

### ANSI_X923
- Дополняет нулями, последний байт = длина набивки
- Стандарт ANSI X9.23

### PKCS7
- Все байты набивки = длина набивки
- Самый распространенный стандарт (RFC 5652)

### ISO_10126
- Дополняет случайными байтами
- Последний байт = длина набивки
- Стандарт ISO/IEC 10126

## Методы CipherContext

### Асинхронные операции с массивами байтов

```java
CompletableFuture<byte[]> encryptAsync(byte[] data)
CompletableFuture<byte[]> decryptAsync(byte[] data)
```

### Асинхронные операции с файлами

```java
CompletableFuture<Void> encryptFileAsync(String inputPath, String outputPath)
CompletableFuture<Void> decryptFileAsync(String inputPath, String outputPath)
```

## Особенности реализации

### ✅ Асинхронность
- Все операции выполняются асинхронно через `CompletableFuture`
- Используется `ExecutorService` с пулом потоков

### ✅ Распараллеливание
- Режимы ECB и CTR поддерживают параллельную обработку блоков
- Автоматическое использование всех доступных ядер процессора

### ✅ Безопасность
- Использование `SecureRandom` для генерации случайных данных
- Копирование массивов для предотвращения изменений

### ✅ Управление ресурсами
- Обязательный вызов `shutdown()` после использования

## Пример использования

### Новый API (соответствует заданию)

```java
// Создание ключа
byte[] key = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

// Создание контекста (передаём ключ)
CipherContext ctx = new CipherContext(
    new DummyCipher(),      // реализация алгоритма
    key,                    // ← ключ шифрования
    CipherMode.CBC,         // режим шифрования
    PaddingMode.PKCS7,      // режим набивки
    8,                      // размер блока
    iv                      // вектор инициализации (опционально)
    // дополнительные параметры (varargs)
);

// Вариант 1: Возврат результата
byte[] encrypted = ctx.encryptAsync("Secret message".getBytes()).join();
byte[] decrypted = ctx.decryptAsync(encrypted).join();

// Вариант 2: Out-параметры (по заданию)
byte[][] encryptedResult = new byte[1][];
ctx.encryptAsync("Secret message".getBytes(), encryptedResult).join();

byte[][] decryptedResult = new byte[1][];
ctx.decryptAsync(encryptedResult[0], decryptedResult).join();

// Вариант 3: Работа с файлами
ctx.encryptFileAsync("input.txt", "output.enc").join();
ctx.decryptFileAsync("output.enc", "decrypted.txt").join();

// Освобождение ресурсов
ctx.shutdown();
```

## Компиляция и запуск

```bash
# Компиляция
javac -d out/production src/*.java

# Компиляция тестов
javac -d out/test -cp out/production test/*.java

# Запуск демонстрации
java -cp out/production Demo

# Запуск тестов
java -cp out/test:out/production CipherFrameworkTest
```

## Тестирование

Набор тестов покрывает:
1. ✅ Padding ZEROS
2. ✅ Padding PKCS7
3. ✅ Padding ANSI X.923
4. ✅ ECB режим
5. ✅ CBC режим
6. ✅ Шифрование и дешифрование
7. ✅ Асинхронные операции

## Требования

- Java 8 или выше (для CompletableFuture)
- Никаких внешних зависимостей

## Автор

Лабораторная работа №1 (задание 2) по криптографии

