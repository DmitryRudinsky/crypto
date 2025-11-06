import java.util.Arrays;

public class DESRoundFunction implements RoundFunction {
    
    @Override
    public byte[] encryptBlock(byte[] block, byte[] roundKey) {
        if (block == null || block.length != 4) {
            throw new IllegalArgumentException("Block must be 4 bytes (32 bits)");
        }
        if (roundKey == null || roundKey.length != 6) {
            throw new IllegalArgumentException("Round key must be 6 bytes (48 bits)");
        }
        
        byte[] expanded = BitPermutation.permutateBits(
            block,
            DESConstants.E,
            BitIndexMode.INVERTED,
            false
        );
        
        byte[] xored = xor(expanded, roundKey);
        
        byte[] sBoxOutput = applySBoxes(xored);
        
        byte[] result = BitPermutation.permutateBits(
            sBoxOutput,
            DESConstants.P,
            BitIndexMode.INVERTED,
            false
        );
        
        return result;
    }
    
    @Override
    public byte[] decryptBlock(byte[] block, byte[] roundKey) {
        return encryptBlock(block, roundKey);
    }
    
    private byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
    
    private byte[] applySBoxes(byte[] input) {
        byte[] output = new byte[4];
        
        for (int i = 0; i < 8; i++) {
            int startBit = i * 6;
            
            int b1 = getBit(input, startBit);
            int b2 = getBit(input, startBit + 1);
            int b3 = getBit(input, startBit + 2);
            int b4 = getBit(input, startBit + 3);
            int b5 = getBit(input, startBit + 4);
            int b6 = getBit(input, startBit + 5);
            
            int row = (b1 << 1) | b6;
            int col = (b2 << 3) | (b3 << 2) | (b4 << 1) | b5;
            
            int sBoxValue = DESConstants.S_BOXES[i][row][col];
            
            int outputBitStart = i * 4;
            setBit(output, outputBitStart, (sBoxValue >> 3) & 1);
            setBit(output, outputBitStart + 1, (sBoxValue >> 2) & 1);
            setBit(output, outputBitStart + 2, (sBoxValue >> 1) & 1);
            setBit(output, outputBitStart + 3, sBoxValue & 1);
        }
        
        return output;
    }
    
    private int getBit(byte[] data, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitPos = 7 - (bitIndex % 8);
        return (data[byteIndex] >> bitPos) & 1;
    }
    
    private void setBit(byte[] data, int bitIndex, int value) {
        int byteIndex = bitIndex / 8;
        int bitPos = 7 - (bitIndex % 8);
        
        if (value == 1) {
            data[byteIndex] |= (1 << bitPos);
        } else {
            data[byteIndex] &= ~(1 << bitPos);
        }
    }
}

