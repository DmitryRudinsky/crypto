import java.util.Arrays;

public class DESKeySchedule implements KeySchedule {
    
    @Override
    public byte[][] expandKey(byte[] key) {
        if (key == null || key.length != 8) {
            throw new IllegalArgumentException("DES key must be 8 bytes");
        }
        
        byte[] permutedKey = BitPermutation.permutateBits(
            key,
            DESConstants.PC1,
            BitIndexMode.INVERTED,
            false
        );
        
        byte[] C = Arrays.copyOfRange(permutedKey, 0, 3);
        byte[] D = Arrays.copyOfRange(permutedKey, 3, 7);
        
        C = Arrays.copyOf(C, 4);
        D = Arrays.copyOf(D, 4);
        
        byte[][] roundKeys = new byte[16][];
        
        for (int i = 0; i < 16; i++) {
            C = leftShift(C, DESConstants.SHIFTS[i], 28);
            D = leftShift(D, DESConstants.SHIFTS[i], 28);
            
            byte[] CD = concatenate(C, D, 28);
            
            roundKeys[i] = BitPermutation.permutateBits(
                CD,
                DESConstants.PC2,
                BitIndexMode.INVERTED,
                false
            );
        }
        
        return roundKeys;
    }
    
    private byte[] leftShift(byte[] data, int shifts, int totalBits) {
        byte[] result = Arrays.copyOf(data, data.length);
        
        for (int s = 0; s < shifts; s++) {
            int firstBit = getBit(result, 0, totalBits);
            
            for (int i = 0; i < totalBits - 1; i++) {
                int bit = getBit(result, i + 1, totalBits);
                setBit(result, i, bit, totalBits);
            }
            
            setBit(result, totalBits - 1, firstBit, totalBits);
        }
        
        return result;
    }
    
    private int getBit(byte[] data, int bitIndex, int totalBits) {
        if (bitIndex >= totalBits) {
            return 0;
        }
        int byteIndex = bitIndex / 8;
        int bitPos = 7 - (bitIndex % 8);
        return (data[byteIndex] >> bitPos) & 1;
    }
    
    private void setBit(byte[] data, int bitIndex, int value, int totalBits) {
        if (bitIndex >= totalBits) {
            return;
        }
        int byteIndex = bitIndex / 8;
        int bitPos = 7 - (bitIndex % 8);
        
        if (value == 1) {
            data[byteIndex] |= (1 << bitPos);
        } else {
            data[byteIndex] &= ~(1 << bitPos);
        }
    }
    
    private byte[] concatenate(byte[] C, byte[] D, int bitsPerHalf) {
        int totalBits = bitsPerHalf * 2;
        int totalBytes = (totalBits + 7) / 8;
        byte[] result = new byte[totalBytes];
        
        for (int i = 0; i < bitsPerHalf; i++) {
            int bit = getBit(C, i, bitsPerHalf);
            setBit(result, i, bit, totalBits);
        }
        
        for (int i = 0; i < bitsPerHalf; i++) {
            int bit = getBit(D, i, bitsPerHalf);
            setBit(result, i + bitsPerHalf, bit, totalBits);
        }
        
        return result;
    }
}

