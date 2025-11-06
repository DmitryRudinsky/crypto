# Сеть Фейстеля (Feistel Network)

## Описание

Сеть Фейстеля — это симметричная структура, используемая в построении блочных шифров. Названа в честь немецкого криптографа Хорста Фейстеля, работавшего в IBM.

## Класс FeistelCipher

### Конструктор

```java
public FeistelCipher(KeySchedule keySchedule, RoundFunction roundFunction)
```

**Параметры:**
- `keySchedule` - реализация интерфейса `KeySchedule` для расширения ключа
- `roundFunction` - реализация интерфейса `RoundFunction` для раунд-функции

### Алгоритм работы

#### Шифрование

1. **Инициализация:**
   ```
   Блок данных делится пополам:
   L₀ = левая половина
   R₀ = правая половина
   ```

2. **Раунды (i = 1 до n):**
   ```
   L_i = R_{i-1}
   R_i = L_{i-1} ⊕ F(R_{i-1}, K_i)
   
   где:
   - K_i - раундовый ключ для раунда i
   - F - раунд-функция
   - ⊕ - операция XOR
   ```

3. **Финализация:**
   ```
   Результат = R_n || L_n  (конкатенация с обменом)
   ```

#### Дешифрование

Дешифрование выполняется той же структурой, но с раундовыми ключами в обратном порядке:

```
Для дешифрования используются ключи: K_n, K_{n-1}, ..., K_2, K_1
```

## Визуализация

```
Шифрование одного раунда:

    L₀        R₀
    │         │
    │    ┌────┤
    │    │    │
    │    │  ┌─▼─┐
    │    │  │ F │◄─── K₁
    │    │  └─┬─┘
    │    │    │
    └────┼────⊕
         │    │
         ▼    ▼
         L₁   R₁
```

## Математическое описание

### Шифрование

Для блока данных M разделенного на (L₀, R₀):

```
Раунд i:
  L_i = R_{i-1}
  R_i = L_{i-1} ⊕ F(R_{i-1}, K_i)

После n раундов:
  C = R_n || L_n
```

### Дешифрование

Для зашифрованного блока C = (L₀, R₀):

```
Раунд i (обратный порядок ключей):
  R_i = L_{i-1}
  L_i = R_{i-1} ⊕ F(L_{i-1}, K_{n-i+1})

После n раундов:
  M = L_n || R_n
```

## Свойства сети Фейстеля

### 1. Обратимость
- ✅ Операция всегда обратима, независимо от свойств функции F
- ✅ F не обязательно должна быть обратимой

### 2. Симметричность структуры
- ✅ Одна и та же структура для шифрования и дешифрования
- ✅ Только порядок ключей меняется

### 3. Безопасность
Зависит от:
- Количества раундов (обычно 8-32)
- Сложности функции F
- Качества расширения ключа

### 4. Производительность
- ✅ Параллелизация на уровне блоков
- ✅ Простая аппаратная реализация
- ❌ Последовательная обработка раундов

## Реализация в проекте

### 1. FeistelCipher

```java
public class FeistelCipher implements SymmetricCipher {
    private final KeySchedule keySchedule;
    private final RoundFunction roundFunction;
    
    @Override
    public byte[] encrypt(byte[] block) {
        // Деление блока на L и R
        byte[] L = Arrays.copyOfRange(block, 0, halfSize);
        byte[] R = Arrays.copyOfRange(block, halfSize, block.length);
        
        // Раунды шифрования
        byte[][] roundKeys = keySchedule.expandKey(encryptionKey);
        for (byte[] roundKey : roundKeys) {
            byte[] newL = R;
            byte[] functionOutput = roundFunction.encryptBlock(R, roundKey);
            byte[] newR = xorBytes(L, functionOutput);
            L = newL;
            R = newR;
        }
        
        // Объединение с обменом
        return concat(R, L);
    }
}
```

### 2. SimpleKeySchedule

Простая реализация расширения ключа:

```java
public class SimpleKeySchedule implements KeySchedule {
    private final int rounds;
    
    @Override
    public byte[][] expandKey(byte[] key) {
        byte[][] roundKeys = new byte[rounds][];
        for (int i = 0; i < rounds; i++) {
            roundKeys[i] = new byte[key.length];
            for (int j = 0; j < key.length; j++) {
                roundKeys[i][j] = (byte) (key[j] ^ (i + 1));
            }
        }
        return roundKeys;
    }
}
```

### 3. SimpleRoundFunction

Простая реализация раунд-функции:

```java
public class SimpleRoundFunction implements RoundFunction {
    @Override
    public byte[] encryptBlock(byte[] block, byte[] roundKey) {
        byte[] result = new byte[block.length];
        for (int i = 0; i < block.length; i++) {
            int keyIndex = i % roundKey.length;
            result[i] = (byte) ((block[i] ^ roundKey[keyIndex]) + keyIndex);
        }
        return result;
    }
}
```

## Примеры использования

### Базовый пример

```java
KeySchedule keySchedule = new SimpleKeySchedule(4);
RoundFunction roundFunction = new SimpleRoundFunction();
FeistelCipher cipher = new FeistelCipher(keySchedule, roundFunction);

byte[] key = {0x01, 0x02, 0x03, 0x04};
cipher.setEncryptionKey(key);
cipher.setDecryptionKey(key);

byte[] plaintext = "Hello!!!".getBytes();  // 8 байт
byte[] encrypted = cipher.encrypt(plaintext);
byte[] decrypted = cipher.decrypt(encrypted);

System.out.println(new String(decrypted));  // "Hello!!!"
```

### С CipherContext и режимом CBC

```java
KeySchedule keySchedule = new SimpleKeySchedule(8);
RoundFunction roundFunction = new SimpleRoundFunction();
FeistelCipher cipher = new FeistelCipher(keySchedule, roundFunction);

byte[] key = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

cipher.setEncryptionKey(key);
cipher.setDecryptionKey(key);

CipherContext ctx = new CipherContext(
    cipher,
    CipherMode.CBC,
    PaddingMode.PKCS7,
    8,
    iv
);

String message = "Feistel network demonstration!";
byte[] encrypted = ctx.encryptAsync(message.getBytes()).join();
byte[] decrypted = ctx.decryptAsync(encrypted).join();

ctx.shutdown();
```

## Известные алгоритмы на основе сети Фейстеля

### 1. DES (Data Encryption Standard)
- 16 раундов
- Размер блока: 64 бит
- Размер ключа: 56 бит (+ 8 бит четности)

### 2. 3DES (Triple DES)
- 48 раундов (3 × DES)
- Размер блока: 64 бит
- Размер ключа: 168 бит

### 3. Blowfish
- 16 раундов
- Размер блока: 64 бит
- Размер ключа: 32-448 бит

### 4. Twofish
- 16 раундов
- Размер блока: 128 бит
- Размер ключа: 128, 192 или 256 бит

### 5. Camellia
- 18 или 24 раунда
- Размер блока: 128 бит
- Размер ключа: 128, 192 или 256 бит

## Преимущества

1. ✅ **Простота реализации**
   - Понятная структура
   - Легко реализовать в hardware и software

2. ✅ **Доказуемая обратимость**
   - Не зависит от свойств функции F
   - Гарантированное дешифрование

3. ✅ **Гибкость**
   - Легко изменить количество раундов
   - Можно использовать разные функции F

4. ✅ **Хорошо изучена**
   - Много исследований безопасности
   - Проверена временем

## Недостатки

1. ❌ **Последовательность раундов**
   - Невозможно распараллелить раунды одного блока
   - Медленнее чем некоторые современные алгоритмы

2. ❌ **Увеличение размера блока**
   - Половина блока просто копируется
   - Можно оптимизировать

3. ❌ **Диффузия**
   - Требуется несколько раундов для хорошей диффузии
   - Меньше 4 раундов - небезопасно

## Безопасность

### Минимальное количество раундов

- **4 раунда**: минимум для базовой безопасности
- **8 раундов**: рекомендуется для практического применения
- **16+ раундов**: для высокой безопасности

### Криптоанализ

Основные атаки:
1. **Дифференциальный криптоанализ**
2. **Линейный криптоанализ**
3. **Атаки на слабые ключи**

Защита:
- Достаточное количество раундов
- Качественная функция F
- Хорошее расширение ключа

## Тестирование

Запуск тестов:
```bash
./run-feistel-tests.sh
```

Тесты покрывают:
1. ✅ Базовое шифрование/дешифрование
2. ✅ Различное количество раундов (2, 4, 8, 16)
3. ✅ Различные размеры блоков (8, 16, 32, 64 байт)
4. ✅ Интеграция с CipherContext
5. ✅ Обработка ошибок (нечетный размер блока)
6. ✅ Валидация (ключ не установлен)

## Демонстрация

Запуск демонстрации:
```bash
./run-feistel-demo.sh
```

Примеры:
1. Базовое использование сети Фейстеля
2. Интеграция с CipherContext (режим CBC)
3. Влияние количества раундов на результат

## Ссылки и источники

1. **Horst Feistel** - "Cryptography and Computer Privacy" (1973)
2. **William Stallings** - "Cryptography and Network Security"
3. **Bruce Schneier** - "Applied Cryptography"
4. NIST FIPS 46-3 (DES Standard)

## Заключение

Сеть Фейстеля - это проверенная временем структура для построения блочных шифров. Наша реализация предоставляет гибкий и расширяемый фреймворк для:
- Экспериментов с различными раунд-функциями
- Исследования влияния количества раундов
- Интеграции с различными режимами шифрования
- Обучения принципам симметричной криптографии

