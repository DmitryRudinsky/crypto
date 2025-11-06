import java.util.Arrays;

public class FeistelCipher implements SymmetricCipher {
    private final KeySchedule keySchedule;
    private final RoundFunction roundFunction;
    private byte[] encryptionKey;
    private byte[] decryptionKey;

    public FeistelCipher(KeySchedule keySchedule, RoundFunction roundFunction) {
        if (keySchedule == null) {
            throw new IllegalArgumentException("KeySchedule cannot be null");
        }
        if (roundFunction == null) {
            throw new IllegalArgumentException("RoundFunction cannot be null");
        }
        this.keySchedule = keySchedule;
        this.roundFunction = roundFunction;
    }

    @Override
    public void setEncryptionKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        this.encryptionKey = Arrays.copyOf(key, key.length);
    }

    @Override
    public void setDecryptionKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        this.decryptionKey = Arrays.copyOf(key, key.length);
    }

    @Override
    public byte[] encrypt(byte[] block) {
        if (encryptionKey == null) {
            throw new IllegalStateException("Encryption key not set");
        }
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        if (block.length % 2 != 0) {
            throw new IllegalArgumentException("Block size must be even for Feistel network");
        }

        byte[] blockCopy = Arrays.copyOf(block, block.length);
        int halfSize = blockCopy.length / 2;

        byte[] L = Arrays.copyOfRange(blockCopy, 0, halfSize);
        byte[] R = Arrays.copyOfRange(blockCopy, halfSize, blockCopy.length);

        byte[][] roundKeys = keySchedule.expandKey(encryptionKey);

        for (byte[] roundKey : roundKeys) {
            byte[] newL = R;
            byte[] functionOutput = roundFunction.encryptBlock(R, roundKey);
            byte[] newR = xorBytes(L, functionOutput);

            L = newL;
            R = newR;
        }

        byte[] result = new byte[block.length];
        System.arraycopy(R, 0, result, 0, halfSize);
        System.arraycopy(L, 0, result, halfSize, halfSize);

        return result;
    }

    @Override
    public byte[] decrypt(byte[] block) {
        if (decryptionKey == null) {
            throw new IllegalStateException("Decryption key not set");
        }
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        if (block.length % 2 != 0) {
            throw new IllegalArgumentException("Block size must be even for Feistel network");
        }

        byte[] blockCopy = Arrays.copyOf(block, block.length);
        int halfSize = blockCopy.length / 2;

        byte[] L = Arrays.copyOfRange(blockCopy, 0, halfSize);
        byte[] R = Arrays.copyOfRange(blockCopy, halfSize, blockCopy.length);

        byte[][] roundKeys = keySchedule.expandKey(decryptionKey);

        for (int i = roundKeys.length - 1; i >= 0; i--) {
            byte[] roundKey = roundKeys[i];

            byte[] newR = L;
            byte[] functionOutput = roundFunction.encryptBlock(L, roundKey);
            byte[] newL = xorBytes(R, functionOutput);

            L = newL;
            R = newR;
        }

        byte[] result = new byte[block.length];
        System.arraycopy(L, 0, result, 0, halfSize);
        System.arraycopy(R, 0, result, halfSize, halfSize);

        return result;
    }

    private byte[] xorBytes(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must have equal length");
        }
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
}

