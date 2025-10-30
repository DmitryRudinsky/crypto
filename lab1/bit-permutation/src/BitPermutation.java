/**
 * Класс для выполнения перестановки битов в массиве байтов.
 */
public class BitPermutation {
    
    private static final int BYTE_SIZE = 8;
    
    /**
     * Выполняет перестановку битов в массиве байтов согласно P-блоку.
     *
     * @param data Исходные данные для перестановки
     * @param pBlock Правило перестановки (массив индексов)
     * @param indexMode Режим индексации битов (от LSB к MSB или наоборот)
     * @param zeroIndexed Если true, то нумерация начинается с 0, иначе с 1
     * @return Массив байтов после перестановки
     * @throws IllegalArgumentException Если данные или P-блок некорректны
     */
    public static byte[] permutateBits(
            byte[] data, 
            int[] pBlock, 
            BitIndexMode indexMode, 
            boolean zeroIndexed
    ) throws IllegalArgumentException {
        
        // Валидация входных данных
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Данные не могут быть null или пустыми");
        }
        
        if (pBlock == null || pBlock.length == 0) {
            throw new IllegalArgumentException("P-блок не может быть null или пустым");
        }
        
        // Определяем модификатор индекса для корректной нумерации
        int idxModifier = 0;
        if (!zeroIndexed && !isZeroIndexed(pBlock)) {
            idxModifier = -1;
        }
        
        // Создаём результирующий массив
        int resultBytes = minBytes(pBlock.length);
        byte[] result = new byte[resultBytes];
        
        // Выполняем перестановку
        for (int idx = 0; idx < pBlock.length; idx++) {
            int sourcePos = pBlock[idx] + idxModifier;
            
            // Проверка выхода за границы
            if (sourcePos < 0 || sourcePos >= data.length * BYTE_SIZE) {
                throw new IllegalArgumentException(
                    String.format("P-блок выходит за границы: индекс [%d], позиция %d", 
                                  idx, pBlock[idx])
                );
            }
            
            // Получаем бит из исходных данных
            byte bitValue = getBit(data, sourcePos, indexMode);
            
            // Устанавливаем бит в результат
            setBit(result, idx, bitValue, indexMode);
        }
        
        return result;
    }
    
    /**
     * Проверяет, индексируется ли P-блок с нуля.
     */
    private static boolean isZeroIndexed(int[] pBlock) {
        for (int value : pBlock) {
            if (value == 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Вычисляет минимальное количество байтов для хранения n битов.
     */
    private static int minBytes(int bits) {
        return (bits + 7) / BYTE_SIZE;
    }
    
    /**
     * Получает значение бита по индексу из массива байтов.
     */
    private static byte getBit(byte[] data, int bitIndex, BitIndexMode mode) {
        int byteIndex = bitIndex / BYTE_SIZE;
        int bitPos = bitIndex % BYTE_SIZE;
        
        if (mode == BitIndexMode.INVERTED) {
            bitPos = 7 - bitPos;
        }
        
        return (byte) ((data[byteIndex] >> bitPos) & 1);
    }
    
    /**
     * Устанавливает значение бита по индексу в массиве байтов.
     */
    private static void setBit(byte[] data, int bitIndex, byte value, BitIndexMode mode) {
        int byteIndex = bitIndex / BYTE_SIZE;
        int bitPos = bitIndex % BYTE_SIZE;
        
        if (mode == BitIndexMode.INVERTED) {
            bitPos = 7 - bitPos;
        }
        
        if (value == 1) {
            data[byteIndex] |= (1 << bitPos);
        } else {
            data[byteIndex] &= ~(1 << bitPos);
        }
    }
}

