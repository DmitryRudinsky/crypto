public class BitPermutation {
    
    private static final int BYTE_SIZE = 8;
    
    public static byte[] permutateBits(
            byte[] data, 
            int[] pBlock, 
            BitIndexMode indexMode, 
            boolean zeroIndexed
    ) throws IllegalArgumentException {
        
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Данные не могут быть null или пустыми");
        }
        
        if (pBlock == null || pBlock.length == 0) {
            throw new IllegalArgumentException("P-блок не может быть null или пустым");
        }
        
        int idxModifier = 0;
        if (!zeroIndexed && !isZeroIndexed(pBlock)) {
            idxModifier = -1;
        }
        
        int resultBytes = minBytes(pBlock.length);
        byte[] result = new byte[resultBytes];
        
        for (int idx = 0; idx < pBlock.length; idx++) {
            int sourcePos = pBlock[idx] + idxModifier;
            
            if (sourcePos < 0 || sourcePos >= data.length * BYTE_SIZE) {
                throw new IllegalArgumentException(
                    String.format("P-блок выходит за границы: индекс [%d], позиция %d", 
                                  idx, pBlock[idx])
                );
            }
            
            byte bitValue = getBit(data, sourcePos, indexMode);
            setBit(result, idx, bitValue, indexMode);
        }
        
        return result;
    }
    
    private static boolean isZeroIndexed(int[] pBlock) {
        for (int value : pBlock) {
            if (value == 0) {
                return true;
            }
        }
        return false;
    }
    
    private static int minBytes(int bits) {
        return (bits + 7) / BYTE_SIZE;
    }
    
    private static byte getBit(byte[] data, int bitIndex, BitIndexMode mode) {
        int byteIndex = bitIndex / BYTE_SIZE;
        int bitPos = bitIndex % BYTE_SIZE;
        
        if (mode == BitIndexMode.INVERTED) {
            bitPos = 7 - bitPos;
        }
        
        return (byte) ((data[byteIndex] >> bitPos) & 1);
    }
    
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

