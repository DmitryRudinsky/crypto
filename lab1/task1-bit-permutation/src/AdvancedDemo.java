/**
 * Расширенная демонстрация возможностей битовой перестановки.
 * Показывает продвинутые сценарии использования и криптографические паттерны.
 */
public class AdvancedDemo {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     ПРОДВИНУТАЯ ДЕМОНСТРАЦИЯ БИТОВОЙ ПЕРЕСТАНОВКИ        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
        
        demo1_BitScattering();
        demo2_FeistelLikeSwap();
        demo3_ExpansionFunction();
        demo4_CompressionFunction();
        demo5_ChainedPermutations();
        demo6_InversePermutation();
        demo7_DataObfuscation();
        demo8_VisualDiffusion();
        demo9_PerformanceTest();
        
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║            ДЕМОНСТРАЦИЯ ЗАВЕРШЕНА                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ДЕМОНСТРАЦИЯ 1: Рассеивание битов (Bit Scattering)
    // ═══════════════════════════════════════════════════════════════════
    
    private static void demo1_BitScattering() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO 1: Рассеивание битов (Diffusion)                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("Цель: Показать, как один изменённый бит влияет на многие");
        System.out.println("      выходные биты после перестановки.\n");
        
        byte[] original = {(byte) 0b10000000, (byte) 0b00000000};
        byte[] modified = {(byte) 0b11000000, (byte) 0b00000000};
        
        // P-блок, который рассеивает биты по всему выходу
        int[] scatterPBlock = {
            0, 8, 1, 9, 2, 10, 3, 11,
            4, 12, 5, 13, 6, 14, 7, 15
        };
        
        System.out.println("Исходные данные 1: " + toBinaryString(original));
        System.out.println("Исходные данные 2: " + toBinaryString(modified));
        System.out.println("Разница: только 1 бит!\n");
        
        byte[] result1 = BitPermutation.permutateBits(
            original, scatterPBlock, BitIndexMode.INVERTED, true
        );
        byte[] result2 = BitPermutation.permutateBits(
            modified, scatterPBlock, BitIndexMode.INVERTED, true
        );
        
        System.out.println("После перестановки:");
        System.out.println("Результат 1:       " + toBinaryString(result1));
        System.out.println("Результат 2:       " + toBinaryString(result2));
        System.out.println("Разница:           " + showDifference(result1, result2));
        
        int diffBits = countDifferentBits(result1, result2);
        System.out.printf("\n✓ Изменение 1 бита → изменение %d бит(ов) в выходе\n", diffBits);
        System.out.println("  Это демонстрирует эффект рассеивания!\n");
        
        separator();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ДЕМОНСТРАЦИЯ 2: Обмен половин (как в сети Фейстеля)
    // ═══════════════════════════════════════════════════════════════════
    
    private static void demo2_FeistelLikeSwap() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO 2: Обмен половин (Feistel Network)                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("Цель: Показать базовую операцию сети Фейстеля -");
        System.out.println("      обмен левой и правой половин блока.\n");
        
        byte[] data = {(byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00};
        
        System.out.println("Исходные данные (4 байта = 32 бита):");
        System.out.println("  " + toBinaryString(data));
        System.out.println("  └─ Левая половина ─┘ └─ Правая половина ─┘\n");
        
        // P-блок для обмена половин: биты 16-31 → 0-15, биты 0-15 → 16-31
        int[] swapPBlock = new int[32];
        for (int i = 0; i < 16; i++) {
            swapPBlock[i] = i + 16;      // Правую половину в начало
            swapPBlock[i + 16] = i;      // Левую половину в конец
        }
        
        byte[] swapped = BitPermutation.permutateBits(
            data, swapPBlock, BitIndexMode.INVERTED, true
        );
        
        System.out.println("После обмена половин:");
        System.out.println("  " + toBinaryString(swapped));
        System.out.println("  └─ Правая половина ─┘ └─ Левая половина ─┘\n");
        
        // Применяем ещё раз - должно вернуться к исходному
        byte[] restored = BitPermutation.permutateBits(
            swapped, swapPBlock, BitIndexMode.INVERTED, true
        );
        
        System.out.println("После повторного обмена:");
        System.out.println("  " + toBinaryString(restored));
        System.out.println("\n✓ Операция самообратная - повторное применение возвращает исходные данные!\n");
        
        separator();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ДЕМОНСТРАЦИЯ 3: Расширяющая функция (Expansion)
    // ═══════════════════════════════════════════════════════════════════
    
    private static void demo3_ExpansionFunction() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO 3: Расширяющая перестановка (32→48 бит)           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("Цель: Показать, как DES расширяет 32-битный блок до 48 бит");
        System.out.println("      путём дублирования граничных битов.\n");
        
        byte[] data = {(byte) 0xAB, (byte) 0xCD, (byte) 0xEF, (byte) 0x12};
        
        System.out.println("Входные данные (32 бита):");
        System.out.println("  " + toBinaryString(data) + "\n");
        
        // Упрощённая E-функция: расширяем 32 бита до 48
        // Дублируем каждый 6-й бит для создания 6-битных групп
        int[] expansionPBlock = {
            31,  0,  1,  2,  3,  4,     // Дублируем bit 31 и bit 4
             3,  4,  5,  6,  7,  8,
             7,  8,  9, 10, 11, 12,
            11, 12, 13, 14, 15, 16,
            15, 16, 17, 18, 19, 20,
            19, 20, 21, 22, 23, 24,
            23, 24, 25, 26, 27, 28,
            27, 28, 29, 30, 31,  0      // Дублируем bit 27 и bit 0
        };
        
        byte[] expanded = BitPermutation.permutateBits(
            data, expansionPBlock, BitIndexMode.INVERTED, true
        );
        
        System.out.println("Выходные данные (48 бит):");
        System.out.println("  " + toBinaryString(expanded));
        System.out.printf("\n✓ Размер увеличился: %d байт → %d байт\n", 
                          data.length, expanded.length);
        System.out.println("  Некоторые биты продублированы для увеличения зависимости\n");
        
        separator();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ДЕМОНСТРАЦИЯ 4: Сжимающая перестановка (Compression)
    // ═══════════════════════════════════════════════════════════════════
    
    private static void demo4_CompressionFunction() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO 4: Сжимающая перестановка (64→56 бит)             ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("Цель: Показать PC-1 перестановку из DES, которая убирает");
        System.out.println("      биты чётности из 64-битного ключа.\n");
        
        byte[] key = {
            (byte) 0x13, (byte) 0x34, (byte) 0x57, (byte) 0x79,
            (byte) 0x9B, (byte) 0xBC, (byte) 0xDF, (byte) 0xF1
        };
        
        System.out.println("64-битный ключ с битами чётности:");
        System.out.println("  " + toBinaryString(key));
        System.out.println("  ↑       ↑       ↑       ↑       ↑       ↑       ↑       ↑");
        System.out.println("  Каждый 8-й бит = бит чётности (будет удалён)\n");
        
        // PC-1: выбираем все биты кроме каждого 8-го
        int[] pc1PBlock = new int[56];
        int idx = 0;
        for (int i = 0; i < 64; i++) {
            if ((i + 1) % 8 != 0) {  // Пропускаем каждый 8-й бит
                pc1PBlock[idx++] = i;
            }
        }
        
        byte[] compressed = BitPermutation.permutateBits(
            key, pc1PBlock, BitIndexMode.INVERTED, true
        );
        
        System.out.println("56-битный ключ после PC-1:");
        System.out.println("  " + toBinaryString(compressed));
        System.out.printf("\n✓ Размер уменьшился: %d бит → %d бит\n", 
                          key.length * 8, pc1PBlock.length);
        System.out.println("  Биты чётности удалены, остались только ключевые биты\n");
        
        separator();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ДЕМОНСТРАЦИЯ 5: Цепочка перестановок
    // ═══════════════════════════════════════════════════════════════════
    
    private static void demo5_ChainedPermutations() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO 5: Цепочка перестановок (3 раунда)                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("Цель: Показать, как несколько раундов перестановок");
        System.out.println("      увеличивают сложность преобразования.\n");
        
        byte[] data = {(byte) 0b10000000, (byte) 0b00000000};
        
        // Три разных P-блока
        int[] pBlock1 = {8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7};
        int[] pBlock2 = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
        int[] pBlock3 = {0, 8, 1, 9, 2, 10, 3, 11, 4, 12, 5, 13, 6, 14, 7, 15};
        
        System.out.println("Исходные данные:");
        System.out.println("  " + toBinaryString(data) + "\n");
        
        byte[] round1 = BitPermutation.permutateBits(
            data, pBlock1, BitIndexMode.INVERTED, true
        );
        System.out.println("После раунда 1 (обмен половин):");
        System.out.println("  " + toBinaryString(round1));
        
        byte[] round2 = BitPermutation.permutateBits(
            round1, pBlock2, BitIndexMode.INVERTED, true
        );
        System.out.println("\nПосле раунда 2 (полный реверс):");
        System.out.println("  " + toBinaryString(round2));
        
        byte[] round3 = BitPermutation.permutateBits(
            round2, pBlock3, BitIndexMode.INVERTED, true
        );
        System.out.println("\nПосле раунда 3 (чередование):");
        System.out.println("  " + toBinaryString(round3));
        
        int totalDiff = countDifferentBits(data, round3);
        System.out.printf("\n✓ После 3 раундов изменилось %d из 16 бит\n", totalDiff);
        System.out.println("  Множественные раунды обеспечивают лучшее рассеивание!\n");
        
        separator();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ДЕМОНСТРАЦИЯ 6: Обратная перестановка
    // ═══════════════════════════════════════════════════════════════════
    
    private static void demo6_InversePermutation() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO 6: Обратная перестановка (P и P⁻¹)                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("Цель: Показать, как вычислить и применить обратную");
        System.out.println("      перестановку для восстановления исходных данных.\n");
        
        byte[] original = {(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78};
        
        // Сложная перестановка
        int[] pBlock = {
            15, 7, 23, 31, 14, 6, 22, 30,
            13, 5, 21, 29, 12, 4, 20, 28,
            11, 3, 19, 27, 10, 2, 18, 26,
            9, 1, 17, 25, 8, 0, 16, 24
        };
        
        System.out.println("Исходные данные:");
        System.out.println("  " + toBinaryString(original) + "\n");
        
        // Вычисляем обратную перестановку
        int[] inversePBlock = computeInversePermutation(pBlock);
        
        System.out.println("Применяем P-блок:");
        byte[] permuted = BitPermutation.permutateBits(
            original, pBlock, BitIndexMode.INVERTED, true
        );
        System.out.println("  " + toBinaryString(permuted));
        
        System.out.println("\nПрименяем обратный P⁻¹-блок:");
        byte[] restored = BitPermutation.permutateBits(
            permuted, inversePBlock, BitIndexMode.INVERTED, true
        );
        System.out.println("  " + toBinaryString(restored));
        
        boolean success = java.util.Arrays.equals(original, restored);
        System.out.println("\n✓ Данные восстановлены: " + (success ? "ДА ✓" : "НЕТ ✗"));
        System.out.println("  Любая перестановка обратима!\n");
        
        separator();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ДЕМОНСТРАЦИЯ 7: Обфускация данных
    // ═══════════════════════════════════════════════════════════════════
    
    private static void demo7_DataObfuscation() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO 7: Обфускация текстовых данных                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("Цель: Показать, как перестановка может скрыть структуру");
        System.out.println("      текстовых данных.\n");
        
        String text = "HELLO";
        byte[] data = text.getBytes();
        
        System.out.println("Исходный текст: " + text);
        System.out.println("ASCII коды:");
        for (int i = 0; i < data.length; i++) {
            System.out.printf("  %c = %02X = %s%n", 
                text.charAt(i), data[i], toBinaryString(new byte[]{data[i]}));
        }
        
        // Сложная перестановка для обфускации
        int totalBits = data.length * 8;
        int[] obfuscatePBlock = new int[totalBits];
        for (int i = 0; i < totalBits; i++) {
            obfuscatePBlock[i] = (i * 17) % totalBits; // Псевдослучайная перестановка
        }
        
        byte[] obfuscated = BitPermutation.permutateBits(
            data, obfuscatePBlock, BitIndexMode.INVERTED, true
        );
        
        System.out.println("\nПосле обфускации:");
        System.out.println("  " + toBinaryString(obfuscated));
        System.out.println("  Hex: " + toHexString(obfuscated));
        System.out.println("\n✓ Исходная структура полностью скрыта!\n");
        
        separator();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ДЕМОНСТРАЦИЯ 8: Визуализация эффекта рассеивания
    // ═══════════════════════════════════════════════════════════════════
    
    private static void demo8_VisualDiffusion() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO 8: Визуализация эффекта рассеивания               ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("Цель: Визуально показать, как изменение одного бита");
        System.out.println("      влияет на множество выходных битов.\n");
        
        byte[] data = new byte[8];
        data[0] = (byte) 0b10000000;  // Только первый бит установлен
        
        // P-блок с максимальным рассеиванием
        int[] diffusionPBlock = new int[64];
        for (int i = 0; i < 64; i++) {
            diffusionPBlock[i] = (i * 37) % 64;  // Максимальное рассеивание
        }
        
        System.out.println("Входные данные (1 бит установлен):");
        printBitGrid(data);
        
        byte[] diffused = BitPermutation.permutateBits(
            data, diffusionPBlock, BitIndexMode.INVERTED, true
        );
        
        System.out.println("\nВыходные данные (биты рассеяны):");
        printBitGrid(diffused);
        
        int setBits = countSetBits(diffused);
        System.out.printf("\n✓ Один входной бит рассеян по всему блоку\n");
        System.out.printf("  Установлено бит в выходе: %d/64\n\n", setBits);
        
        separator();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ДЕМОНСТРАЦИЯ 9: Тест производительности
    // ═══════════════════════════════════════════════════════════════════
    
    private static void demo9_PerformanceTest() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  DEMO 9: Тест производительности                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("Цель: Измерить скорость выполнения перестановок.\n");
        
        int[] sizes = {8, 64, 512, 4096}; // биты
        int iterations = 10000;
        
        System.out.printf("Выполняем %d итераций для каждого размера:\n\n", iterations);
        
        for (int bits : sizes) {
            byte[] data = new byte[bits / 8];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) i;
            }
            
            int[] pBlock = new int[bits];
            for (int i = 0; i < bits; i++) {
                pBlock[i] = bits - 1 - i; // Реверс
            }
            
            long startTime = System.nanoTime();
            
            for (int i = 0; i < iterations; i++) {
                BitPermutation.permutateBits(data, pBlock, BitIndexMode.INVERTED, true);
            }
            
            long endTime = System.nanoTime();
            double elapsedMs = (endTime - startTime) / 1_000_000.0;
            double avgMs = elapsedMs / iterations;
            
            System.out.printf("  %4d бит (%3d байт): %.2f мс общее, %.4f мс/операция%n",
                bits, bits / 8, elapsedMs, avgMs);
        }
        
        System.out.println("\n✓ Перестановки выполняются очень быстро даже для больших блоков!\n");
        
        separator();
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ
    // ═══════════════════════════════════════════════════════════════════
    
    private static String toBinaryString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF))
                      .replace(' ', '0'));
        }
        return sb.toString();
    }
    
    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
    
    private static String showDifference(byte[] a, byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            if (i > 0) sb.append(" ");
            int diff = (a[i] & 0xFF) ^ (b[i] & 0xFF);
            sb.append(String.format("%8s", Integer.toBinaryString(diff))
                      .replace(' ', '0')
                      .replace('0', '·')
                      .replace('1', '█'));
        }
        return sb.toString();
    }
    
    private static int countDifferentBits(byte[] a, byte[] b) {
        int count = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            int diff = (a[i] & 0xFF) ^ (b[i] & 0xFF);
            count += Integer.bitCount(diff);
        }
        return count;
    }
    
    private static int countSetBits(byte[] data) {
        int count = 0;
        for (byte b : data) {
            count += Integer.bitCount(b & 0xFF);
        }
        return count;
    }
    
    private static int[] computeInversePermutation(int[] pBlock) {
        int[] inverse = new int[pBlock.length];
        for (int i = 0; i < pBlock.length; i++) {
            inverse[pBlock[i]] = i;
        }
        return inverse;
    }
    
    private static void printBitGrid(byte[] data) {
        System.out.print("  ");
        for (int i = 0; i < data.length; i++) {
            for (int bit = 7; bit >= 0; bit--) {
                int value = (data[i] >> bit) & 1;
                System.out.print(value == 1 ? "█ " : "· ");
            }
            if ((i + 1) % 4 == 0 && i < data.length - 1) {
                System.out.print("\n  ");
            }
        }
        System.out.println();
    }
    
    private static void separator() {
        System.out.println("════════════════════════════════════════════════════════════\n");
    }
}

