import java.util.Arrays;

/**
 * Тестовый класс для проверки функции перестановки битов.
 */
public class BitPermutationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Тестирование перестановки битов ===\n");
        
        int passed = 0;
        int failed = 0;
        
        // Тест 1: Простая реверсия битов (MSB режим)
        try {
            byte[] data1 = {(byte) 0b11001010};
            int[] pBlock1 = {7, 6, 5, 4, 3, 2, 1, 0};
            byte[] expected1 = {(byte) 0b01010011};
            byte[] result1 = BitPermutation.permutateBits(
                data1, pBlock1, BitIndexMode.INVERTED, true
            );
            
            if (Arrays.equals(result1, expected1)) {
                System.out.println("✓ Тест 1 (реверсия MSB): PASSED");
                System.out.printf("  Вход:  %s%n", toBinaryString(data1));
                System.out.printf("  Выход: %s%n", toBinaryString(result1));
                passed++;
            } else {
                System.out.println("✗ Тест 1 (реверсия MSB): FAILED");
                System.out.printf("  Ожидалось: %s%n", toBinaryString(expected1));
                System.out.printf("  Получено:  %s%n", toBinaryString(result1));
                failed++;
            }
        } catch (Exception e) {
            System.out.println("✗ Тест 1: FAILED с ошибкой: " + e.getMessage());
            failed++;
        }
        System.out.println();
        
        // Тест 2: Простая реверсия битов (LSB режим)
        try {
            byte[] data2 = {(byte) 0b11001010};
            int[] pBlock2 = {7, 6, 5, 4, 3, 2, 1, 0};
            byte[] expected2 = {(byte) 0b01010011};
            byte[] result2 = BitPermutation.permutateBits(
                data2, pBlock2, BitIndexMode.NORMAL, true
            );
            
            if (Arrays.equals(result2, expected2)) {
                System.out.println("✓ Тест 2 (реверсия LSB): PASSED");
                System.out.printf("  Вход:  %s%n", toBinaryString(data2));
                System.out.printf("  Выход: %s%n", toBinaryString(result2));
                passed++;
            } else {
                System.out.println("✗ Тест 2 (реверсия LSB): FAILED");
                System.out.printf("  Ожидалось: %s%n", toBinaryString(expected2));
                System.out.printf("  Получено:  %s%n", toBinaryString(result2));
                failed++;
            }
        } catch (Exception e) {
            System.out.println("✗ Тест 2: FAILED с ошибкой: " + e.getMessage());
            failed++;
        }
        System.out.println();
        
        // Тест 3: Идентичное отображение
        try {
            byte[] data3 = {(byte) 0b10101010};
            int[] pBlock3 = {0, 1, 2, 3, 4, 5, 6, 7};
            byte[] expected3 = {(byte) 0b10101010};
            byte[] result3 = BitPermutation.permutateBits(
                data3, pBlock3, BitIndexMode.INVERTED, true
            );
            
            if (Arrays.equals(result3, expected3)) {
                System.out.println("✓ Тест 3 (идентичность): PASSED");
                System.out.printf("  Вход:  %s%n", toBinaryString(data3));
                System.out.printf("  Выход: %s%n", toBinaryString(result3));
                passed++;
            } else {
                System.out.println("✗ Тест 3 (идентичность): FAILED");
                System.out.printf("  Ожидалось: %s%n", toBinaryString(expected3));
                System.out.printf("  Получено:  %s%n", toBinaryString(result3));
                failed++;
            }
        } catch (Exception e) {
            System.out.println("✗ Тест 3: FAILED с ошибкой: " + e.getMessage());
            failed++;
        }
        System.out.println();
        
        // Тест 4: Перестановка двух байтов
        try {
            byte[] data4 = {(byte) 0b11110000, (byte) 0b00001111};
            int[] pBlock4 = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
            byte[] expected4 = {(byte) 0b11110000, (byte) 0b00001111};
            byte[] result4 = BitPermutation.permutateBits(
                data4, pBlock4, BitIndexMode.INVERTED, true
            );
            
            if (Arrays.equals(result4, expected4)) {
                System.out.println("✓ Тест 4 (два байта): PASSED");
                System.out.printf("  Вход:  %s%n", toBinaryString(data4));
                System.out.printf("  Выход: %s%n", toBinaryString(result4));
                passed++;
            } else {
                System.out.println("✗ Тест 4 (два байта): FAILED");
                System.out.printf("  Ожидалось: %s%n", toBinaryString(expected4));
                System.out.printf("  Получено:  %s%n", toBinaryString(result4));
                failed++;
            }
        } catch (Exception e) {
            System.out.println("✗ Тест 4: FAILED с ошибкой: " + e.getMessage());
            failed++;
        }
        System.out.println();
        
        // Тест 5: Нумерация с 1 (не с 0)
        try {
            byte[] data5 = {(byte) 0b11001010};
            int[] pBlock5 = {8, 7, 6, 5, 4, 3, 2, 1}; // Индексы начинаются с 1
            byte[] expected5 = {(byte) 0b01010011};
            byte[] result5 = BitPermutation.permutateBits(
                data5, pBlock5, BitIndexMode.INVERTED, false
            );
            
            if (Arrays.equals(result5, expected5)) {
                System.out.println("✓ Тест 5 (индексация с 1): PASSED");
                System.out.printf("  Вход:  %s%n", toBinaryString(data5));
                System.out.printf("  Выход: %s%n", toBinaryString(result5));
                passed++;
            } else {
                System.out.println("✗ Тест 5 (индексация с 1): FAILED");
                System.out.printf("  Ожидалось: %s%n", toBinaryString(expected5));
                System.out.printf("  Получено:  %s%n", toBinaryString(result5));
                failed++;
            }
        } catch (Exception e) {
            System.out.println("✗ Тест 5: FAILED с ошибкой: " + e.getMessage());
            failed++;
        }
        System.out.println();
        
        // Тест 6: Обработка ошибки (выход за границы)
        try {
            byte[] data6 = {(byte) 0b00001111};
            int[] pBlock6 = {8, 9, 10}; // Индексы за пределами
            BitPermutation.permutateBits(data6, pBlock6, BitIndexMode.INVERTED, true);
            System.out.println("✗ Тест 6 (выход за границы): FAILED - ошибка не выброшена");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Тест 6 (выход за границы): PASSED");
            System.out.println("  Корректно выброшено исключение: " + e.getMessage());
            passed++;
        }
        System.out.println();
        
        // Итоговая статистика
        System.out.println("=== Результаты тестирования ===");
        System.out.printf("Пройдено: %d%n", passed);
        System.out.printf("Провалено: %d%n", failed);
        System.out.printf("Всего: %d%n", passed + failed);
        
        if (failed == 0) {
            System.out.println("\n🎉 Все тесты пройдены успешно!");
        }
    }
    
    /**
     * Вспомогательная функция для красивого вывода байтов в двоичном формате.
     */
    private static String toBinaryString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(String.format("0b%8s", Integer.toBinaryString(bytes[i] & 0xFF))
                      .replace(' ', '0'));
        }
        return sb.toString();
    }
}

