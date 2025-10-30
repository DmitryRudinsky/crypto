/**
 * Демонстрация работы функции перестановки битов.
 */
public class Demo {
    
    public static void main(String[] args) {
        System.out.println("=== Демонстрация перестановки битов ===\n");
        
        // Пример 1: Простая реверсия байта
        System.out.println("Пример 1: Реверсия всех битов в байте");
        byte[] data1 = {(byte) 0b11001010};
        int[] pBlock1 = {7, 6, 5, 4, 3, 2, 1, 0};
        
        System.out.println("Исходные данные: " + toBinaryString(data1));
        System.out.println("P-блок: [7, 6, 5, 4, 3, 2, 1, 0]");
        
        byte[] result1 = BitPermutation.permutateBits(
            data1, pBlock1, BitIndexMode.INVERTED, true
        );
        
        System.out.println("Результат: " + toBinaryString(result1));
        System.out.println();
        
        // Пример 2: Перестановка битов в двух байтах
        System.out.println("Пример 2: Обмен местами двух байтов");
        byte[] data2 = {(byte) 0b11110000, (byte) 0b00001111};
        int[] pBlock2 = {8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7};
        
        System.out.println("Исходные данные: " + toBinaryString(data2));
        System.out.println("P-блок меняет местами первый и второй байт");
        
        byte[] result2 = BitPermutation.permutateBits(
            data2, pBlock2, BitIndexMode.INVERTED, true
        );
        
        System.out.println("Результат: " + toBinaryString(result2));
        System.out.println();
        
        // Пример 3: Выбор определённых битов
        System.out.println("Пример 3: Выборка только чётных битов");
        byte[] data3 = {(byte) 0b11111111};
        int[] pBlock3 = {0, 2, 4, 6}; // Берём только чётные позиции
        
        System.out.println("Исходные данные: " + toBinaryString(data3));
        System.out.println("P-блок: [0, 2, 4, 6] - выбираем только чётные биты");
        
        byte[] result3 = BitPermutation.permutateBits(
            data3, pBlock3, BitIndexMode.INVERTED, true
        );
        
        System.out.println("Результат (4 бита): " + toBinaryString(result3));
        System.out.println();
        
        // Пример 4: Индексация с 1 (а не с 0)
        System.out.println("Пример 4: Использование индексации с 1");
        byte[] data4 = {(byte) 0b10101010};
        int[] pBlock4 = {1, 2, 3, 4, 5, 6, 7, 8}; // Индексы от 1 до 8
        
        System.out.println("Исходные данные: " + toBinaryString(data4));
        System.out.println("P-блок: [1, 2, 3, 4, 5, 6, 7, 8] (индексация с 1)");
        
        byte[] result4 = BitPermutation.permutateBits(
            data4, pBlock4, BitIndexMode.INVERTED, false
        );
        
        System.out.println("Результат: " + toBinaryString(result4));
        System.out.println();
        
        // Пример 5: Режим Normal (LSB к MSB)
        System.out.println("Пример 5: Разница между режимами Normal и Inverted");
        byte[] data5 = {(byte) 0b11110000};
        int[] pBlock5 = {0, 1, 2, 3, 4, 5, 6, 7};
        
        System.out.println("Исходные данные: " + toBinaryString(data5));
        
        byte[] resultNormal = BitPermutation.permutateBits(
            data5, pBlock5, BitIndexMode.NORMAL, true
        );
        System.out.println("Режим NORMAL:   " + toBinaryString(resultNormal));
        
        byte[] resultInverted = BitPermutation.permutateBits(
            data5, pBlock5, BitIndexMode.INVERTED, true
        );
        System.out.println("Режим INVERTED: " + toBinaryString(resultInverted));
        System.out.println();
        
        System.out.println("=== Демонстрация завершена ===");
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

