### DES (Data Encryption Standard)

## Описание

DES - это симметричный блочный шифр, разработанный в IBM и принятый как стандарт NIST в 1977 году. Реализация построена на базе сети Фейстеля с использованием функции перестановки битов из задания 1.

## Структура реализации

```
DES
├── DESConstants.java      - Константы (IP, E, P, S-блоки, PC1, PC2)
├── DESKeySchedule.java    - Расширение ключа (16 раундовых ключей)
├── DESRoundFunction.java  - Функция Фейстеля (E, S-блоки, P)
└── DES.java               - Основной класс (IP, сеть Фейстеля, IP⁻¹)
```

## Параметры DES

- **Размер блока**: 64 бита (8 байт)
- **Размер ключа**: 56 бит эффективных + 8 бит четности = 64 бита (8 байт)
- **Количество раундов**: 16
- **Структура**: Сеть Фейстеля

## Алгоритм работы

### Шифрование

```
1. Начальная перестановка (IP)
   block = IP(plaintext)

2. Разделение на половины
   L₀ = левые 32 бита
   R₀ = правые 32 бита

3. 16 раундов Фейстеля
   for i = 1 to 16:
       L_i = R_{i-1}
       R_i = L_{i-1} ⊕ F(R_{i-1}, K_i)

4. Обмен половин
   preoutput = R₁₆ || L₁₆

5. Конечная перестановка (IP⁻¹)
   ciphertext = IP⁻¹(preoutput)
```

### Дешифрование

Идентично шифрованию, но раундовые ключи применяются в обратном порядке: K₁₆, K₁₅, ..., K₂, K₁

## Компоненты

### 1. DESConstants

Содержит все константы DES:

#### IP (Initial Permutation)
Начальная перестановка 64 бит:
```
58 50 42 34 26 18 10  2
60 52 44 36 28 20 12  4
...
```

#### IP⁻¹ (Inverse Initial Permutation)
Обратная начальная перестановка.

#### E (Expansion)
Расширение 32 бит → 48 бит:
```
32  1  2  3  4  5
 4  5  6  7  8  9
...
```

#### S-boxes (Substitution boxes)
8 S-блоков, каждый: 6 бит → 4 бита
- Вход: 6 бит (b₁b₂b₃b₄b₅b₆)
- Строка: b₁b₆ (2 бита)
- Столбец: b₂b₃b₄b₅ (4 бита)
- Выход: 4 бита из таблицы

#### P (Permutation)
Перестановка 32 бит:
```
16  7 20 21 29 12 28 17
 1 15 23 26  5 18 31 10
...
```

#### PC1 (Permuted Choice 1)
Выбор 56 бит из 64 (отбрасывает биты четности):
```
57 49 41 33 25 17  9
 1 58 50 42 34 26 18
...
```

#### PC2 (Permuted Choice 2)
Выбор 48 бит из 56 для раундового ключа:
```
14 17 11 24  1  5
 3 28 15  6 21 10
...
```

#### SHIFTS
Количество циклических сдвигов для каждого раунда:
```
1 1 2 2 2 2 2 2 1 2 2 2 2 2 2 1
```

### 2. DESKeySchedule

Реализация `KeySchedule` для генерации 16 раундовых ключей.

**Алгоритм:**

```
1. Применить PC1 к 64-битному ключу → 56 бит
2. Разделить на C₀ (28 бит) и D₀ (28 бит)
3. Для каждого раунда i = 1 to 16:
   - C_i = циклический_сдвиг_влево(C_{i-1}, SHIFTS[i])
   - D_i = циклический_сдвиг_влево(D_{i-1}, SHIFTS[i])
   - K_i = PC2(C_i || D_i) → 48 бит
```

**Использование BitPermutation из задания 1:**
```java
byte[] permutedKey = BitPermutation.permutateBits(
    key,
    DESConstants.PC1,
    BitIndexMode.INVERTED,
    false
);
```

### 3. DESRoundFunction

Реализация `RoundFunction` - функция Фейстеля для DES.

**Алгоритм F(R, K):**

```
1. Расширение: E(R) → 48 бит
2. XOR с ключом: E(R) ⊕ K → 48 бит
3. S-блоки: 48 бит → 32 бита (8 S-блоков, каждый 6→4)
4. Перестановка P: 32 бита → 32 бита
```

**Применение S-блоков:**
```java
для каждого из 8 S-блоков:
  - взять 6 бит
  - row = b₁b₆
  - col = b₂b₃b₄b₅
  - output = S[sbox_num][row][col] (4 бита)
```

**Использование BitPermutation:**
```java
byte[] expanded = BitPermutation.permutateBits(
    block,
    DESConstants.E,
    BitIndexMode.INVERTED,
    false
);

byte[] result = BitPermutation.permutateBits(
    sBoxOutput,
    DESConstants.P,
    BitIndexMode.INVERTED,
    false
);
```

### 4. DES

Основной класс, объединяющий все компоненты.

**Использует:**
- `FeistelCipher` из задания 3
- `DESKeySchedule` (реализация интерфейса 1)
- `DESRoundFunction` (реализация интерфейса 2)

**Конструктор:**
```java
public DES() {
    KeySchedule keySchedule = new DESKeySchedule();
    RoundFunction roundFunction = new DESRoundFunction();
    this.feistelCipher = new FeistelCipher(keySchedule, roundFunction);
}
```

**Шифрование:**
```java
public byte[] encrypt(byte[] block) {
    // 1. Начальная перестановка
    byte[] permuted = BitPermutation.permutateBits(block, DESConstants.IP, ...);
    
    // 2. Сеть Фейстеля (16 раундов)
    byte[] feistelOutput = feistelCipher.encrypt(permuted);
    
    // 3. Конечная перестановка
    byte[] result = BitPermutation.permutateBits(feistelOutput, DESConstants.IP_INV, ...);
    
    return result;
}
```

## Использование

### Базовый пример

```java
DES des = new DES();

byte[] key = {(byte)0x13, (byte)0x34, (byte)0x57, (byte)0x79,
              (byte)0x9B, (byte)0xBC, (byte)0xDF, (byte)0xF1};
des.setEncryptionKey(key);
des.setDecryptionKey(key);

byte[] plaintext = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
                   (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF};

byte[] encrypted = des.encrypt(plaintext);
byte[] decrypted = des.decrypt(encrypted);
```

### С CipherContext (режим CBC)

```java
DES des = new DES();

byte[] key = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
byte[] iv = new byte[8];

des.setEncryptionKey(key);
des.setDecryptionKey(key);

CipherContext ctx = new CipherContext(
    des,
    CipherMode.CBC,
    PaddingMode.PKCS7,
    8,
    iv
);

String message = "Secret message for DES encryption";
byte[] encrypted = ctx.encryptAsync(message.getBytes()).join();
byte[] decrypted = ctx.decryptAsync(encrypted).join();

ctx.shutdown();
```

## Связь с предыдущими заданиями

### Задание 1: BitPermutation
Используется для всех перестановок:
- IP (начальная перестановка)
- IP⁻¹ (конечная перестановка)
- E (расширение)
- P (перестановка в F)
- PC1, PC2 (расширение ключа)

### Задание 2: Интерфейсы
Реализованы:
- `KeySchedule` → `DESKeySchedule`
- `RoundFunction` → `DESRoundFunction`

### Задание 3: Сеть Фейстеля
Используется `FeistelCipher` с DES-специфичными реализациями.

## Тестирование

Запуск тестов:
```bash
./run-des-tests.sh
```

**7 тестов покрывают:**
1. ✅ Базовое шифрование/дешифрование
2. ✅ Нулевой ключ и текст
3. ✅ Различные ключи
4. ✅ Интеграция с CipherContext
5. ✅ Обработка неверного размера ключа
6. ✅ Обработка неверного размера блока
7. ✅ Шифрование нескольких блоков

## Демонстрация

Запуск демонстрации:
```bash
./run-des-demo.sh
```

**3 примера:**
1. Базовое использование DES
2. DES с CipherContext (CBC режим)
3. Известные тестовые векторы

## Безопасность DES

### Сильные стороны
- ✅ Проверенная структура (сеть Фейстеля)
- ✅ Хорошая диффузия и конфузия
- ✅ Стандарт на протяжении 20+ лет

### Слабые стороны
- ❌ **Короткий ключ**: 56 бит → атака полным перебором возможна
- ❌ Слабые ключи (weak keys) существуют
- ❌ Устарел для современных приложений

### Рекомендации
- Использовать 3DES (Triple DES) для лучшей безопасности
- Для новых систем использовать AES
- Подходит только для образовательных целей

## Производительность

- **Блоки:** 8 байт (64 бита)
- **Раунды:** 16
- **Операции на раунд:**
  - 1 расширение (E)
  - 1 XOR с ключом
  - 8 S-блоков
  - 1 перестановка (P)

## Сравнение с исходной реализацией (Go)

Наша Java-реализация полностью соответствует Go-версии из папки `crypto`:
- ✅ Те же константы
- ✅ Та же структура
- ✅ Те же алгоритмы
- ✅ Использует те же интерфейсы

## История DES

- **1973**: IBM разработала Lucifer
- **1977**: NIST принял DES как стандарт (FIPS 46)
- **1997**: Первая успешная атака полным перебором
- **1999**: Deep Crack взломал DES за 22 часа
- **2005**: DES официально устарел
- **Сейчас**: Используется только 3DES и AES

## Заключение

Реализация DES демонстрирует:
- ✅ Применение сети Фейстеля
- ✅ Использование перестановок битов
- ✅ Реализацию интерфейсов KeySchedule и RoundFunction
- ✅ Интеграцию всех компонентов фреймворка
- ✅ Исторически важный алгоритм в криптографии

Хотя DES больше не рекомендуется для реального использования, его изучение помогает понять принципы современной криптографии.

