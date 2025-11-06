import java.util.Arrays;

public class SimpleRoundFunction implements RoundFunction {

    @Override
    public byte[] encryptBlock(byte[] block, byte[] roundKey) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        if (roundKey == null) {
            throw new IllegalArgumentException("Round key cannot be null");
        }

        byte[] result = new byte[block.length];
        
        for (int i = 0; i < block.length; i++) {
            int keyIndex = i % roundKey.length;
            result[i] = (byte) ((block[i] ^ roundKey[keyIndex]) + keyIndex);
        }

        return result;
    }

    @Override
    public byte[] decryptBlock(byte[] block, byte[] roundKey) {
        return encryptBlock(block, roundKey);
    }
}

