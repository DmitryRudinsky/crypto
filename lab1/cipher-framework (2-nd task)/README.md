# Cipher Framework - Фреймворк для симметричного шифрования

## Описание

Полнофункциональный фреймворк для работы с симметричными криптографическими алгоритмами. Поддерживает различные режимы шифрования, набивки блоков, асинхронную обработку и распараллеливание вычислений.

## Структура проекта

```
cipher-framework (2-nd task)/
├── src/                        # Исходный код
│   ├── KeySchedule.java        # Интерфейс расширения ключа
│   ├── RoundFunction.java      # Интерфейс раунд-функции
│   ├── SymmetricCipher.java    # Интерфейс симметричного алгоритма
│   ├── CipherMode.java         # Enum режимов шифрования
│   ├── PaddingMode.java        # Enum режимов набивки
│   ├── CipherContext.java      # Класс контекста шифрования
│   ├── FeistelCipher.java      # Реализация сети Фейстеля
│   ├── SimpleKeySchedule.java  # Простое расширение ключа
│   ├── SimpleRoundFunction.java # Простая раунд-функция
│   ├── DummyCipher.java        # Демо-реализация шифра
│   ├── Demo.java               # Примеры использования
│   └── FeistelDemo.java        # Примеры сети Фейстеля
├── test/                       # Тесты
│   ├── CipherFrameworkTest.java
│   └── FeistelCipherTest.java
├── build.sh                    # Скрипт компиляции
├── run-tests.sh                # Запуск тестов
├── run-demo.sh                 # Запуск демонстрации
├── run-feistel-demo.sh         # Запуск демо Фейстеля
└── run-feistel-tests.sh        # Запуск тестов Фейстеля
```

## Быстрый старт

```bash
# Сделать скрипты исполняемыми
chmod +x *.sh

# Компиляция
./build.sh

# Запуск тестов
./run-tests.sh          # Общие тесты (7 тестов)
./run-feistel-tests.sh  # Тесты сети Фейстеля (6 тестов)

# Запуск демонстраций
./run-demo.sh           # Общая демонстрация
./run-feistel-demo.sh   # Демонстрация сети Фейстеля
```

## Архитектура

### 1. Интерфейс KeySchedule

Описывает функционал для расширения ключа (генерации раундовых ключей).

```java
public interface KeySchedule {
    byte[][] expandKey(byte[] key);
}
```

**Параметры:**
- `key` - входной ключ (массив байтов)
- **Возвращает:** массив раундовых ключей (каждый - массив байтов)

### 2. Интерфейс RoundFunction

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

### 3. Интерфейс SymmetricCipher

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

### 3.1. Класс FeistelCipher

Реализация сети Фейстеля на базе интерфейсов `KeySchedule` и `RoundFunction`.

```java
public class FeistelCipher implements SymmetricCipher {
    public FeistelCipher(KeySchedule keySchedule, RoundFunction roundFunction);
}
```

**Параметры конструктора:**
- `keySchedule` - реализация расширения ключа (интерфейс 1)
- `roundFunction` - реализация раунд-функции (интерфейс 2)

**Принцип работы сети Фейстеля:**

1. **Шифрование:**
   - Блок делится на две половины: L (левая) и R (правая)
   - Для каждого раунда i:
     - L_new = R_old
     - R_new = L_old ⊕ F(R_old, RoundKey_i)
   - После всех раундов: результат = R || L (половины меняются местами)

2. **Дешифрование:**
   - Аналогично шифрованию, но раундовые ключи применяются в обратном порядке

**Особенности:**
- ✅ Размер блока должен быть четным (для деления пополам)
- ✅ Функция F не обязательно должна быть обратимой
- ✅ Одна и та же структура для шифрования и дешифрования
- ✅ Используется в DES, 3DES, Blowfish, Twofish и других алгоритмах

**Пример использования:**

```java
KeySchedule keySchedule = new SimpleKeySchedule(8);  // 8 раундов
RoundFunction roundFunc = new SimpleRoundFunction();
FeistelCipher cipher = new FeistelCipher(keySchedule, roundFunc);

byte[] key = {0x01, 0x02, 0x03, 0x04};
cipher.setEncryptionKey(key);
cipher.setDecryptionKey(key);

byte[] plaintext = {0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x21, 0x21, 0x21};
byte[] encrypted = cipher.encrypt(plaintext);
byte[] decrypted = cipher.decrypt(encrypted);
```

**Демонстрационные классы:**
- `SimpleKeySchedule` - простая реализация расширения ключа
- `SimpleRoundFunction` - простая реализация раунд-функции

**Запуск:**
```bash
./run-feistel-demo.sh    # Демонстрация
./run-feistel-tests.sh   # 6 тестов
```

### 4. Класс CipherContext

Основной класс для выполнения операций шифрования/дешифрования.

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
- `iv` - вектор инициализации (опционально, для режимов требующих IV)

**Методы:**

```java
CompletableFuture<byte[]> encryptAsync(byte[] data)
CompletableFuture<byte[]> decryptAsync(byte[] data)
CompletableFuture<Void> encryptFileAsync(String inputPath, String outputPath)
CompletableFuture<Void> decryptFileAsync(String inputPath, String outputPath)
```

## Режимы шифрования (CipherMode)

### ECB (Electronic Codebook)
- Самый простой режим
- Каждый блок шифруется независимо
- **Не требует IV**
- Не рекомендуется для реального использования

### CBC (Cipher Block Chaining)
- Каждый блок XOR-ится с предыдущим зашифрованным блоком
- **Требует IV**
- Популярный и безопасный режим

### PCBC (Propagating Cipher Block Chaining)
- Модификация CBC с распространением ошибок
- **Требует IV**
- Используется в Kerberos v4

### CFB (Cipher Feedback)
- Превращает блочный шифр в потоковый
- **Требует IV**
- Не требует дополнения блоков

### OFB (Output Feedback)
- Потоковый режим
- **Требует IV**
- Шифрование и дешифрование идентичны

### CTR (Counter)
- Режим счетчика
- **Требует IV (счетчик)**
- Позволяет распараллеливание
- Шифрование и дешифрование идентичны

### RANDOM_DELTA
- Каждый блок XOR-ится со случайной дельтой
- Дельта шифруется и добавляется к блоку
- **Не требует IV**
- Удваивает размер зашифрованных данных

## Режимы набивки (PaddingMode)

### ZEROS
- Дополняет блок нулями
- Простой, но может потерять конечные нули исходных данных

### ANSI_X923
- Дополняет нулями, последний байт = длина набивки
- Стандарт ANSI X9.23

### PKCS7
- Все байты набивки = длина набивки
- Самый распространенный стандарт
- RFC 5652

### ISO_10126
- Дополняет случайными байтами
- Последний байт = длина набивки
- Стандарт ISO/IEC 10126

## Примеры использования

### Базовое шифрование

```java
byte[] key = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};

DummyCipher cipher = new DummyCipher();
cipher.setEncryptionKey(key);
cipher.setDecryptionKey(key);

CipherContext ctx = new CipherContext(
    cipher, 
    CipherMode.ECB, 
    PaddingMode.PKCS7, 
    8
);

byte[] encrypted = ctx.encryptAsync("Hello World!".getBytes()).join();
byte[] decrypted = ctx.decryptAsync(encrypted).join();

ctx.shutdown();
```

### Использование CBC с IV

```java
byte[] key = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

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

byte[] encrypted = ctx.encryptAsync("Secret message".getBytes()).join();
byte[] decrypted = ctx.decryptAsync(encrypted).join();

ctx.shutdown();
```

### Асинхронное шифрование файлов

```java
CipherContext ctx = new CipherContext(
    cipher, 
    CipherMode.CBC, 
    PaddingMode.PKCS7, 
    8,
    iv
);

CompletableFuture<Void> future = ctx.encryptFileAsync(
    "input.txt", 
    "output.enc"
);

future.thenRun(() -> {
    System.out.println("Файл зашифрован!");
});

ctx.shutdown();
```

### Параллельное шифрование нескольких блоков

```java
String[] messages = {"Message 1", "Message 2", "Message 3"};
CipherContext ctx = new CipherContext(cipher, CipherMode.ECB, PaddingMode.PKCS7, 8);

CompletableFuture<?>[] futures = Arrays.stream(messages)
    .map(msg -> ctx.encryptAsync(msg.getBytes()))
    .toArray(CompletableFuture[]::new);

CompletableFuture.allOf(futures).join();
ctx.shutdown();
```

## Особенности реализации

### Асинхронность
- Все операции шифрования/дешифрования асинхронные
- Используется `CompletableFuture` из Java 8+
- `ExecutorService` с пулом потоков размером = количество ядер процессора

### Распараллеливание
- Режимы ECB и CTR поддерживают параллельную обработку блоков
- Автоматическое использование всех доступных ядер процессора
- Оптимизация для больших объемов данных

### Безопасность
- Использование `SecureRandom` для генерации случайных данных
- Копирование массивов для предотвращения изменений извне
- Валидация параметров конструктора

### Управление ресурсами
- Обязательный вызов `shutdown()` после использования контекста
- Освобождение пула потоков
- Предотвращение утечек памяти

## Тестирование

Набор тестов покрывает:
1. ✅ Padding ZEROS
2. ✅ Padding PKCS7
3. ✅ Padding ANSI X.923
4. ✅ ECB режим
5. ✅ CBC режим
6. ✅ Шифрование и дешифрование
7. ✅ Асинхронные операции

Запуск тестов:
```bash
./run-tests.sh
```

## Требования

- Java 8 или выше (для CompletableFuture)
- Никаких внешних зависимостей

## Расширение фреймворка

### Добавление нового алгоритма

Реализуйте интерфейс `SymmetricCipher`:

```java
public class MyCustomCipher implements SymmetricCipher {
    private byte[] encKey;
    private byte[] decKey;
    
    @Override
    public void setEncryptionKey(byte[] key) {
        this.encKey = processKey(key);
    }
    
    @Override
    public void setDecryptionKey(byte[] key) {
        this.decKey = processKey(key);
    }
    
    @Override
    public byte[] encrypt(byte[] block) {
        // Ваша реализация
    }
    
    @Override
    public byte[] decrypt(byte[] block) {
        // Ваша реализация
    }
}
```

### Добавление нового режима шифрования

Добавьте элемент в enum `CipherMode` с реализацией методов `encrypt` и `decrypt`.

## Производительность

- Использование пула потоков для параллельной обработки
- Минимальное копирование данных
- Эффективное управление памятью
- Оптимизация для больших файлов

## Автор

Лабораторная работа №1 (задание 2) по криптографии

## Лицензия

Educational use only

