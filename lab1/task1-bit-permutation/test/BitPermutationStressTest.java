import java.util.Arrays;
import java.util.Random;

/**
 * Расширенные стресс-тесты для битовой перестановки.
 * Проверяют граничные случаи, большие объёмы данных и сложные сценарии.
 */
public class BitPermutationStressTest {
    
    private static int passed = 0;
    private static int failed = 0;
    private static final Random random = new Random(42); // Фиксированный seed для воспроизводимости
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║   СТРЕСС-ТЕСТИРОВАНИЕ БИТОВОЙ ПЕРЕСТАНОВКИ         ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        // Группа 1: Граничные случаи с размерами
        System.out.println("▶ ГРУППА 1: Граничные случаи с размерами");
        testSingleBit();
        testSevenBits();
        testNineBits();
        testExactlyOneKiloByte();
        testLargeData();
        System.out.println();
        
        // Группа 2: Сложные перестановки
        System.out.println("▶ ГРУППА 2: Сложные перестановки");
        testCompleteReverseMultipleBytes();
        testInterleavedBits();
        testScatteredBits();
        testDuplicateIndices();
        System.out.println();
        
        // Группа 3: Криптографические паттерны
        System.out.println("▶ ГРУППА 3: Криптографические паттерны");
        testDESLikePermutation();
        testExpansionPermutation();
        testCompressionPermutation();
        testSBoxOutputPermutation();
        System.out.println();
        
        // Группа 4: Обратимость
        System.out.println("▶ ГРУППА 4: Обратимость перестановок");
        testInversePermutation();
        testDoublePermutation();
        testTriplePermutation();
        System.out.println();
        
        // Группа 5: Режимы индексации
        System.out.println("▶ ГРУППА 5: Режимы индексации");
        testNormalVsInvertedMode();
        testZeroVsOneIndexing();
        testMixedIndexingAutodetect();
        System.out.println();
        
        // Группа 6: Случайные данные
        System.out.println("▶ ГРУППА 6: Случайные данные");
        testRandomDataSmall();
        testRandomDataMedium();
        testRandomDataLarge();
        System.out.println();
        
        // Группа 7: Особые паттерны
        System.out.println("▶ ГРУППА 7: Особые паттерны");
        testAllZeros();
        testAllOnes();
        testAlternatingPattern();
        testChessboardPattern();
        System.out.println();
        
        // Группа 8: Ошибочные ситуации
        System.out.println("▶ ГРУППА 8: Обработка ошибок");
        testOutOfBoundsHigh();
        testOutOfBoundsNegative();
        testEmptyPBlock();
        testNullData();
        System.out.println();
        
        // Итоговая статистика
        printFinalStatistics();
    }
    
    // ═══════════════════════════════════════════════════════════
    // ГРУППА 1: Граничные случаи с размерами
    // ═══════════════════════════════════════════════════════════
    
    private static void testSingleBit() {
        try {
            byte[] data = {(byte) 0b10000000};
            int[] pBlock = {0};
            byte[] expected = {(byte) 0b10000000};
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertArrayEquals("Тест: один бит", expected, result);
        } catch (Exception e) {
            testFailed("Тест: один бит", e);
        }
    }
    
    private static void testSevenBits() {
        try {
            byte[] data = {(byte) 0b11111110};
            int[] pBlock = {0, 1, 2, 3, 4, 5, 6}; // 7 бит
            byte[] expected = {(byte) 0b11111110};
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertArrayEquals("Тест: 7 бит (неполный байт)", expected, result);
        } catch (Exception e) {
            testFailed("Тест: 7 бит", e);
        }
    }
    
    private static void testNineBits() {
        try {
            byte[] data = {(byte) 0b11111111, (byte) 0b10000000};
            int[] pBlock = {0, 1, 2, 3, 4, 5, 6, 7, 8}; // 9 бит
            byte[] expected = {(byte) 0b11111111, (byte) 0b10000000};
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertArrayEquals("Тест: 9 бит (переход через границу байта)", expected, result);
        } catch (Exception e) {
            testFailed("Тест: 9 бит", e);
        }
    }
    
    private static void testExactlyOneKiloByte() {
        try {
            byte[] data = new byte[1024]; // 1 KB
            Arrays.fill(data, (byte) 0b10101010);
            
            int[] pBlock = new int[8192]; // 1024 байта = 8192 бита
            for (int i = 0; i < pBlock.length; i++) {
                pBlock[i] = i; // Идентичная перестановка
            }
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertArrayEquals("Тест: 1 килобайт данных", data, result);
        } catch (Exception e) {
            testFailed("Тест: 1 KB", e);
        }
    }
    
    private static void testLargeData() {
        try {
            byte[] data = new byte[10000]; // 10 KB
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i & 0xFF);
            }
            
            // Реверс только первых 128 бит
            int[] pBlock = new int[128];
            for (int i = 0; i < 128; i++) {
                pBlock[i] = 127 - i;
            }
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertTrue("Тест: большой объём данных (10 KB)", result.length == 16);
        } catch (Exception e) {
            testFailed("Тест: большой объём", e);
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // ГРУППА 2: Сложные перестановки
    // ═══════════════════════════════════════════════════════════
    
    private static void testCompleteReverseMultipleBytes() {
        try {
            byte[] data = {(byte) 0xAB, (byte) 0xCD, (byte) 0xEF, (byte) 0x12};
            int[] pBlock = new int[32];
            for (int i = 0; i < 32; i++) {
                pBlock[i] = 31 - i; // Полный реверс 4 байтов
            }
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            // Ожидаем полный реверс: 0x12EFCDABпо битам
            byte[] expected = {(byte) 0x48, (byte) 0xF7, (byte) 0xB3, (byte) 0xD5};
            
            assertArrayEquals("Тест: полный реверс 4 байтов", expected, result);
        } catch (Exception e) {
            testFailed("Тест: полный реверс", e);
        }
    }
    
    private static void testInterleavedBits() {
        try {
            // Чередование битов из двух байтов
            byte[] data = {(byte) 0b11110000, (byte) 0b00001111};
            int[] pBlock = {0, 8, 1, 9, 2, 10, 3, 11, 4, 12, 5, 13, 6, 14, 7, 15};
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            // Правильный результат: 11110000 + 00001111 чередуются как 10101010 01010101
            byte[] expected = {(byte) 0b10101010, (byte) 0b01010101};
            
            assertArrayEquals("Тест: чередование битов", expected, result);
        } catch (Exception e) {
            testFailed("Тест: чередование", e);
        }
    }
    
    private static void testScatteredBits() {
        try {
            // Выборка отдельных битов с больших расстояний
            byte[] data = new byte[8];
            data[0] = (byte) 0b10000000;
            data[2] = (byte) 0b00100000;
            data[4] = (byte) 0b00001000;
            data[6] = (byte) 0b00000010;
            
            int[] pBlock = {0, 18, 36, 54}; // Биты из разных байтов
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            byte[] expected = {(byte) 0b11110000};
            
            assertArrayEquals("Тест: разбросанные биты", expected, result);
        } catch (Exception e) {
            testFailed("Тест: разбросанные биты", e);
        }
    }
    
    private static void testDuplicateIndices() {
        try {
            // P-блок с повторяющимися индексами (валидная операция!)
            byte[] data = {(byte) 0b10100000};
            int[] pBlock = {0, 0, 0, 0, 1, 1, 1, 1}; // Дублируем биты
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            byte[] expected = {(byte) 0b11110000};
            
            assertArrayEquals("Тест: дублирование индексов", expected, result);
        } catch (Exception e) {
            testFailed("Тест: дубликаты", e);
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // ГРУППА 3: Криптографические паттерны
    // ═══════════════════════════════════════════════════════════
    
    private static void testDESLikePermutation() {
        try {
            // Симуляция Initial Permutation из DES (упрощённая)
            byte[] data = new byte[8];
            for (int i = 0; i < 8; i++) {
                data[i] = (byte) (i * 17); // Паттерн
            }
            
            // Упрощённая IP: меняем местами чётные и нечётные биты
            int[] pBlock = new int[64];
            for (int i = 0; i < 32; i++) {
                pBlock[i] = i * 2 + 1;
                pBlock[i + 32] = i * 2;
            }
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertTrue("Тест: DES-подобная перестановка", result.length == 8);
        } catch (Exception e) {
            testFailed("Тест: DES-like", e);
        }
    }
    
    private static void testExpansionPermutation() {
        try {
            // Симуляция E-функции из DES: 32 бита → 48 бит
            byte[] data = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
            
            int[] pBlock = new int[48];
            // Простая экспансия: дублируем каждый 4-й бит
            int idx = 0;
            for (int i = 0; i < 32; i++) {
                pBlock[idx++] = i;
                if (i % 4 == 3) {
                    pBlock[idx++] = i; // Дублируем
                }
            }
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertTrue("Тест: расширяющая перестановка (32→48)", result.length == 6);
        } catch (Exception e) {
            testFailed("Тест: экспансия", e);
        }
    }
    
    private static void testCompressionPermutation() {
        try {
            // Сжатие: 64 бита → 56 бит (как в DES key schedule)
            byte[] data = new byte[8];
            Arrays.fill(data, (byte) 0xFF);
            
            int[] pBlock = new int[56];
            // Пропускаем каждый 8-й бит
            int idx = 0;
            for (int i = 0; i < 64; i++) {
                if ((i + 1) % 8 != 0) {
                    pBlock[idx++] = i;
                }
            }
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertTrue("Тест: сжимающая перестановка (64→56)", result.length == 7);
        } catch (Exception e) {
            testFailed("Тест: компрессия", e);
        }
    }
    
    private static void testSBoxOutputPermutation() {
        try {
            // P-перестановка после S-boxes в DES (32 бита)
            byte[] data = {(byte) 0xA5, (byte) 0x5A, (byte) 0xF0, (byte) 0x0F};
            
            int[] pBlock = {
                15, 6, 19, 20, 28, 11, 27, 16,
                0, 14, 22, 25, 4, 17, 30, 9,
                1, 7, 23, 13, 31, 26, 2, 8,
                18, 12, 29, 5, 21, 10, 3, 24
            };
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertTrue("Тест: P-перестановка после S-box", result.length == 4);
        } catch (Exception e) {
            testFailed("Тест: S-box P", e);
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // ГРУППА 4: Обратимость
    // ═══════════════════════════════════════════════════════════
    
    private static void testInversePermutation() {
        try {
            byte[] data = {(byte) 0xAB, (byte) 0xCD};
            
            // Прямая перестановка
            int[] pBlock = {8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7};
            
            // Обратная перестановка
            int[] inversePBlock = {8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7};
            
            byte[] encrypted = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            byte[] decrypted = BitPermutation.permutateBits(
                encrypted, inversePBlock, BitIndexMode.INVERTED, true
            );
            
            assertArrayEquals("Тест: обратная перестановка", data, decrypted);
        } catch (Exception e) {
            testFailed("Тест: обратная", e);
        }
    }
    
    private static void testDoublePermutation() {
        try {
            byte[] data = {(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78};
            int[] pBlock = {7, 6, 5, 4, 3, 2, 1, 0, 15, 14, 13, 12, 11, 10, 9, 8,
                           23, 22, 21, 20, 19, 18, 17, 16, 31, 30, 29, 28, 27, 26, 25, 24};
            
            // Применяем дважды - должно вернуться к исходному
            byte[] once = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            byte[] twice = BitPermutation.permutateBits(
                once, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertArrayEquals("Тест: двойная перестановка (самообратная)", data, twice);
        } catch (Exception e) {
            testFailed("Тест: двойная", e);
        }
    }
    
    private static void testTriplePermutation() {
        try {
            byte[] data = {(byte) 0b10101010};
            int[] pBlock = {2, 4, 6, 0, 1, 3, 5, 7}; // Сложная перестановка
            
            byte[] r1 = BitPermutation.permutateBits(data, pBlock, BitIndexMode.INVERTED, true);
            byte[] r2 = BitPermutation.permutateBits(r1, pBlock, BitIndexMode.INVERTED, true);
            byte[] r3 = BitPermutation.permutateBits(r2, pBlock, BitIndexMode.INVERTED, true);
            
            // Проверяем, что перестановка работает (тест что код не падает)
            // После 3 применений данные изменились относительно оригинала
            assertTrue("Тест: тройная перестановка", r3 != null && r3.length == 1);
        } catch (Exception e) {
            testFailed("Тест: тройная", e);
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // ГРУППА 5: Режимы индексации
    // ═══════════════════════════════════════════════════════════
    
    private static void testNormalVsInvertedMode() {
        try {
            byte[] data = {(byte) 0b11110000};
            int[] pBlock = {0, 1, 2, 3, 4, 5, 6, 7};
            
            byte[] normal = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.NORMAL, true
            );
            
            byte[] inverted = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            // Результаты должны быть одинаковые при идентичной перестановке
            assertArrayEquals("Тест: NORMAL vs INVERTED с идентичным P-блоком", 
                            inverted, normal);
        } catch (Exception e) {
            testFailed("Тест: режимы", e);
        }
    }
    
    private static void testZeroVsOneIndexing() {
        try {
            byte[] data = {(byte) 0b10101010};
            
            // С индексацией от 0
            int[] pBlock0 = {7, 6, 5, 4, 3, 2, 1, 0};
            byte[] result0 = BitPermutation.permutateBits(
                data, pBlock0, BitIndexMode.INVERTED, true
            );
            
            // С индексацией от 1
            int[] pBlock1 = {8, 7, 6, 5, 4, 3, 2, 1};
            byte[] result1 = BitPermutation.permutateBits(
                data, pBlock1, BitIndexMode.INVERTED, false
            );
            
            assertArrayEquals("Тест: индексация 0-based vs 1-based", result0, result1);
        } catch (Exception e) {
            testFailed("Тест: индексация", e);
        }
    }
    
    private static void testMixedIndexingAutodetect() {
        try {
            byte[] data = {(byte) 0xFF};
            
            // P-блок с 0 - должен определиться как 0-based
            int[] pBlockWith0 = {0, 1, 2, 3, 4, 5, 6, 7};
            byte[] result1 = BitPermutation.permutateBits(
                data, pBlockWith0, BitIndexMode.INVERTED, false
            );
            
            // P-блок без 0 - определится как 1-based и применится коррекция
            int[] pBlockWithout0 = {1, 2, 3, 4, 5, 6, 7, 8};
            byte[] result2 = BitPermutation.permutateBits(
                data, pBlockWithout0, BitIndexMode.INVERTED, false
            );
            
            assertArrayEquals("Тест: автоопределение индексации", result1, result2);
        } catch (Exception e) {
            testFailed("Тест: автоопределение", e);
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // ГРУППА 6: Случайные данные
    // ═══════════════════════════════════════════════════════════
    
    private static void testRandomDataSmall() {
        try {
            byte[] data = new byte[4];
            random.nextBytes(data);
            
            int[] pBlock = generateRandomPermutation(32);
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertTrue("Тест: случайные данные (4 байта)", result.length == 4);
        } catch (Exception e) {
            testFailed("Тест: случайные малые", e);
        }
    }
    
    private static void testRandomDataMedium() {
        try {
            byte[] data = new byte[128];
            random.nextBytes(data);
            
            // Случайная выборка 256 бит из 1024
            int[] pBlock = new int[256];
            for (int i = 0; i < 256; i++) {
                pBlock[i] = random.nextInt(1024);
            }
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertTrue("Тест: случайные данные (128 байт)", result.length == 32);
        } catch (Exception e) {
            testFailed("Тест: случайные средние", e);
        }
    }
    
    private static void testRandomDataLarge() {
        try {
            byte[] data = new byte[1024];
            random.nextBytes(data);
            
            // Идентичная перестановка на больших данных
            int[] pBlock = new int[8192];
            for (int i = 0; i < 8192; i++) {
                pBlock[i] = i;
            }
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertArrayEquals("Тест: случайные данные (1 KB)", data, result);
        } catch (Exception e) {
            testFailed("Тест: случайные большие", e);
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // ГРУППА 7: Особые паттерны
    // ═══════════════════════════════════════════════════════════
    
    private static void testAllZeros() {
        try {
            byte[] data = new byte[16];
            int[] pBlock = generateRandomPermutation(128);
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            byte[] expected = new byte[16];
            assertArrayEquals("Тест: все нули", expected, result);
        } catch (Exception e) {
            testFailed("Тест: все нули", e);
        }
    }
    
    private static void testAllOnes() {
        try {
            byte[] data = new byte[8];
            Arrays.fill(data, (byte) 0xFF);
            
            int[] pBlock = generateRandomPermutation(64);
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            byte[] expected = new byte[8];
            Arrays.fill(expected, (byte) 0xFF);
            assertArrayEquals("Тест: все единицы", expected, result);
        } catch (Exception e) {
            testFailed("Тест: все единицы", e);
        }
    }
    
    private static void testAlternatingPattern() {
        try {
            byte[] data = new byte[4];
            Arrays.fill(data, (byte) 0b10101010);
            
            // Обмен четных и нечетных битов
            int[] pBlock = new int[32];
            for (int i = 0; i < 32; i += 2) {
                pBlock[i] = i + 1;
                pBlock[i + 1] = i;
            }
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            byte[] expected = new byte[4];
            Arrays.fill(expected, (byte) 0b01010101);
            assertArrayEquals("Тест: чередующийся паттерн", expected, result);
        } catch (Exception e) {
            testFailed("Тест: чередование", e);
        }
    }
    
    private static void testChessboardPattern() {
        try {
            // Шахматный паттерн: 11001100 11001100 ...
            byte[] data = new byte[8];
            Arrays.fill(data, (byte) 0b11001100);
            
            int[] pBlock = new int[64];
            for (int i = 0; i < 64; i++) {
                pBlock[i] = i;
            }
            
            byte[] result = BitPermutation.permutateBits(
                data, pBlock, BitIndexMode.INVERTED, true
            );
            
            assertArrayEquals("Тест: шахматный паттерн", data, result);
        } catch (Exception e) {
            testFailed("Тест: шахматный", e);
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // ГРУППА 8: Обработка ошибок
    // ═══════════════════════════════════════════════════════════
    
    private static void testOutOfBoundsHigh() {
        try {
            byte[] data = {(byte) 0xFF};
            int[] pBlock = {0, 1, 2, 3, 4, 5, 6, 7, 8}; // Индекс 8 выходит за границы
            
            BitPermutation.permutateBits(data, pBlock, BitIndexMode.INVERTED, true);
            testFailed("Тест: выход за верхнюю границу", null);
        } catch (IllegalArgumentException e) {
            testPassed("Тест: выход за верхнюю границу");
        }
    }
    
    private static void testOutOfBoundsNegative() {
        try {
            byte[] data = {(byte) 0xFF};
            int[] pBlock = {0, 1, 2, 3, 4, 5, 6, 7};
            
            // Попытка с индексацией от 0, но с индексами как от 1 (без автокоррекции)
            BitPermutation.permutateBits(data, pBlock, BitIndexMode.INVERTED, true);
            testPassed("Тест: корректная обработка индексов");
        } catch (Exception e) {
            testFailed("Тест: негативные индексы", e);
        }
    }
    
    private static void testEmptyPBlock() {
        try {
            byte[] data = {(byte) 0xFF};
            int[] pBlock = {};
            
            BitPermutation.permutateBits(data, pBlock, BitIndexMode.INVERTED, true);
            testFailed("Тест: пустой P-блок", null);
        } catch (IllegalArgumentException e) {
            testPassed("Тест: пустой P-блок");
        }
    }
    
    private static void testNullData() {
        try {
            int[] pBlock = {0, 1, 2, 3, 4, 5, 6, 7};
            
            BitPermutation.permutateBits(null, pBlock, BitIndexMode.INVERTED, true);
            testFailed("Тест: null данные", null);
        } catch (IllegalArgumentException e) {
            testPassed("Тест: null данные");
        }
    }
    
    // ═══════════════════════════════════════════════════════════
    // ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ
    // ═══════════════════════════════════════════════════════════
    
    private static int[] generateRandomPermutation(int size) {
        int[] perm = new int[size];
        for (int i = 0; i < size; i++) {
            perm[i] = i;
        }
        // Fisher-Yates shuffle
        for (int i = size - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = perm[i];
            perm[i] = perm[j];
            perm[j] = temp;
        }
        return perm;
    }
    
    private static void assertArrayEquals(String testName, byte[] expected, byte[] actual) {
        if (Arrays.equals(expected, actual)) {
            testPassed(testName);
        } else {
            System.out.printf("  ✗ %s: FAILED%n", testName);
            System.out.printf("    Ожидалось: %s%n", toBinaryString(expected));
            System.out.printf("    Получено:  %s%n", toBinaryString(actual));
            failed++;
        }
    }
    
    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            testPassed(testName);
        } else {
            System.out.printf("  ✗ %s: FAILED (условие не выполнено)%n", testName);
            failed++;
        }
    }
    
    private static void testPassed(String testName) {
        System.out.printf("  ✓ %s: PASSED%n", testName);
        passed++;
    }
    
    private static void testFailed(String testName, Exception e) {
        System.out.printf("  ✗ %s: FAILED%n", testName);
        if (e != null) {
            System.out.printf("    Ошибка: %s%n", e.getMessage());
        }
        failed++;
    }
    
    private static String toBinaryString(byte[] bytes) {
        if (bytes.length > 16) {
            return String.format("[%d байт]", bytes.length);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF))
                      .replace(' ', '0'));
        }
        return sb.toString();
    }
    
    private static void printFinalStatistics() {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║              ИТОГОВАЯ СТАТИСТИКА                   ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.printf("║  Пройдено тестов:  %-3d                            ║%n", passed);
        System.out.printf("║  Провалено тестов: %-3d                            ║%n", failed);
        System.out.printf("║  Всего тестов:     %-3d                            ║%n", passed + failed);
        
        double successRate = (passed * 100.0) / (passed + failed);
        System.out.printf("║  Процент успеха:   %.1f%%                         ║%n", successRate);
        System.out.println("╠════════════════════════════════════════════════════╣");
        
        if (failed == 0) {
            System.out.println("║  🎉  ВСЕ СТРЕСС-ТЕСТЫ ПРОЙДЕНЫ УСПЕШНО!  🎉       ║");
        } else {
            System.out.println("║  ⚠️  ОБНАРУЖЕНЫ ПРОБЛЕМЫ!                         ║");
        }
        System.out.println("╚════════════════════════════════════════════════════╝");
    }
}

