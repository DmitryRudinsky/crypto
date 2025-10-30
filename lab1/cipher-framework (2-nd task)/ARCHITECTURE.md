# Архитектура Cipher Framework

## Диаграмма компонентов

```
┌─────────────────────────────────────────────────────────────┐
│                      CipherContext                          │
│                                                             │
│  - cipher: SymmetricCipher                                 │
│  - mode: CipherMode                                        │
│  - padding: PaddingMode                                    │
│  - executor: ExecutorService                               │
│                                                             │
│  + encryptAsync(byte[]): CompletableFuture<byte[]>        │
│  + decryptAsync(byte[]): CompletableFuture<byte[]>        │
│  + encryptFileAsync(String, String): CompletableFuture    │
│  + decryptFileAsync(String, String): CompletableFuture    │
└─────────────────────────────────────────────────────────────┘
                    │          │          │
        ┌───────────┘          │          └───────────┐
        │                      │                      │
        ▼                      ▼                      ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ Symmetric    │    │ CipherMode   │    │ PaddingMode  │
│ Cipher       │    │              │    │              │
│              │    │ - ECB        │    │ - ZEROS      │
│ + encrypt()  │    │ - CBC        │    │ - ANSI_X923  │
│ + decrypt()  │    │ - PCBC       │    │ - PKCS7      │
│ + setKey()   │    │ - CFB        │    │ - ISO_10126  │
└──────────────┘    │ - OFB        │    └──────────────┘
                    │ - CTR        │
                    │ - RANDOM_Δ   │
                    └──────────────┘
```

## Слои архитектуры

### Уровень 1: Интерфейсы базовых операций

#### KeySchedule
```java
public interface KeySchedule {
    byte[][] expandKey(byte[] key);
}
```
- Отвечает за расширение ключа
- Генерирует раундовые ключи из исходного ключа
- Используется внутри SymmetricCipher

#### RoundFunction
```java
public interface RoundFunction {
    byte[] encryptBlock(byte[] block, byte[] roundKey);
    byte[] decryptBlock(byte[] block, byte[] roundKey);
}
```
- Описывает одну раунд-функцию шифрования
- Работает с одним блоком и одним раундовым ключом
- Базовый строительный блок для алгоритмов

### Уровень 2: Симметричный алгоритм

#### SymmetricCipher
```java
public interface SymmetricCipher {
    void setEncryptionKey(byte[] key);
    void setDecryptionKey(byte[] key);
    byte[] encrypt(byte[] block);
    byte[] decrypt(byte[] block);
}
```
- Объединяет KeySchedule и RoundFunction
- Предоставляет высокоуровневый API для блочного шифрования
- Работает с одним блоком за раз

### Уровень 3: Режимы работы

#### CipherMode (Enum)
- **ECB**: Независимое шифрование каждого блока
- **CBC**: Цепочка блоков с XOR предыдущего
- **PCBC**: Распространяющаяся цепочка блоков
- **CFB**: Обратная связь по шифротексту
- **OFB**: Обратная связь по выходу
- **CTR**: Режим счетчика
- **RANDOM_DELTA**: Случайная дельта для каждого блока

Каждый режим реализует:
```java
byte[][] encrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv);
byte[][] decrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv);
boolean requiresIV();
```

### Уровень 4: Набивка блоков

#### PaddingMode (Enum)
- **ZEROS**: Дополнение нулями
- **ANSI_X923**: Нули + размер в конце
- **PKCS7**: Все байты = размер набивки
- **ISO_10126**: Случайные байты + размер

Каждый режим реализует:
```java
byte[] pad(byte[] data, int blockSize);
byte[] unpad(byte[] data, int blockSize);
```

### Уровень 5: Контекст и оркестрация

#### CipherContext
Главный класс, объединяющий все компоненты:

1. **Управление жизненным циклом**
   - Создание с параметрами
   - Валидация конфигурации
   - Управление пулом потоков

2. **Операции шифрования**
   - Набивка данных (PaddingMode)
   - Разбиение на блоки
   - Шифрование блоков (CipherMode + SymmetricCipher)
   - Объединение блоков

3. **Операции дешифрования**
   - Разбиение на блоки
   - Дешифрование блоков
   - Объединение блоков
   - Удаление набивки

4. **Асинхронность**
   - CompletableFuture для всех операций
   - ExecutorService с пулом потоков
   - Автоматическое распараллеливание

## Поток данных

### Шифрование

```
Исходные данные (byte[])
        ↓
    Padding
        ↓
Разбиение на блоки (byte[][])
        ↓
    CipherMode.encrypt()
        ↓ (для каждого блока)
    SymmetricCipher.encrypt()
        ↓
Зашифрованные блоки (byte[][])
        ↓
Объединение в массив (byte[])
        ↓
Зашифрованные данные (byte[])
```

### Дешифрование

```
Зашифрованные данные (byte[])
        ↓
Разбиение на блоки (byte[][])
        ↓
    CipherMode.decrypt()
        ↓ (для каждого блока)
    SymmetricCipher.decrypt()
        ↓
Дешифрованные блоки (byte[][])
        ↓
Объединение в массив (byte[])
        ↓
    Unpadding
        ↓
Исходные данные (byte[])
```

## Паттерны проектирования

### 1. Strategy Pattern
- **CipherMode**: стратегия для режимов шифрования
- **PaddingMode**: стратегия для набивки
- **SymmetricCipher**: стратегия для алгоритма

### 2. Template Method
- `CipherContext` определяет общий алгоритм шифрования
- Конкретные детали делегируются компонентам

### 3. Facade
- `CipherContext` предоставляет простой интерфейс
- Скрывает сложность взаимодействия компонентов

### 4. Dependency Injection
- Все зависимости передаются через конструктор
- Легко тестировать и заменять компоненты

## Многопоточность

### ExecutorService
```java
ExecutorService executor = Executors.newFixedThreadPool(
    Runtime.getRuntime().availableProcessors()
);
```

- Создается при инициализации CipherContext
- Размер пула = количество ядер процессора
- Используется для всех асинхронных операций

### CompletableFuture
```java
CompletableFuture.supplyAsync(() -> {
    // Операция шифрования
}, executor);
```

- Все операции возвращают CompletableFuture
- Поддержка цепочек операций (.thenApply, .thenCompose)
- Обработка ошибок (.exceptionally, .handle)

### Параллельная обработка блоков

Режимы, поддерживающие параллельность:
- **ECB**: все блоки независимы
- **CTR**: каждый блок использует свой счетчик

```java
// Потенциальная оптимизация для ECB
blocks.parallelStream()
    .map(cipher::encrypt)
    .toArray(byte[][]::new);
```

## Расширяемость

### Добавление нового алгоритма

1. Реализовать `SymmetricCipher`
2. Реализовать внутри `KeySchedule` и `RoundFunction`
3. Использовать в `CipherContext`

```java
public class AESCipher implements SymmetricCipher {
    private KeySchedule keySchedule = new AESKeySchedule();
    private RoundFunction roundFunc = new AESRoundFunction();
    // ...
}
```

### Добавление нового режима

Добавить в enum `CipherMode`:

```java
NEW_MODE {
    @Override
    public byte[][] encrypt(byte[][] blocks, ...) {
        // Реализация
    }
    
    @Override
    public byte[][] decrypt(byte[][] blocks, ...) {
        // Реализация
    }
    
    @Override
    public boolean requiresIV() {
        return true; // или false
    }
}
```

### Добавление новой набивки

Добавить в enum `PaddingMode`:

```java
CUSTOM_PADDING {
    @Override
    public byte[] pad(byte[] data, int blockSize) {
        // Реализация
    }
    
    @Override
    public byte[] unpad(byte[] data, int blockSize) {
        // Реализация
    }
}
```

## Безопасность

### 1. Защита данных
- Копирование ключей и IV при установке
- Предотвращение изменения извне

### 2. Генерация случайных данных
```java
SecureRandom random = new SecureRandom();
random.nextBytes(buffer);
```

### 3. Валидация
- Проверка параметров конструктора
- Проверка требований к IV для режимов
- Валидация размеров блоков

### 4. Очистка ресурсов
```java
ctx.shutdown(); // Обязательный вызов
```

## Производительность

### Оптимизации

1. **Переиспользование буферов**
   ```java
   byte[] result = new byte[expectedSize];
   System.arraycopy(src, 0, result, 0, length);
   ```

2. **Ленивая инициализация**
   - ExecutorService создается один раз
   - Переиспользование для всех операций

3. **Batch processing**
   - Обработка всех блоков за один проход
   - Минимизация синхронизации

4. **Выбор правильного режима**
   - ECB/CTR для параллельности
   - CBC/CFB для безопасности
   - Компромисс между скоростью и безопасностью

## Тестирование

### Unit тесты
- Каждый режим набивки
- Каждый режим шифрования
- Интеграционные тесты

### Критерии успеха
```
encrypt(decrypt(data)) == data
decrypt(encrypt(data)) == data
```

### Граничные случаи
- Пустые данные
- Данные размером с блок
- Данные меньше блока
- Большие объемы данных

## Заключение

Архитектура построена на принципах:
- **SOLID**: каждый компонент имеет одну ответственность
- **Open/Closed**: легко расширять без изменения существующего кода
- **Dependency Inversion**: зависимости от абстракций
- **Composition over Inheritance**: использование композиции интерфейсов

Это обеспечивает:
- ✅ Гибкость
- ✅ Тестируемость
- ✅ Масштабируемость
- ✅ Производительность

