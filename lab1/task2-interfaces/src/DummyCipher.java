import java.util.Arrays;

public class DummyCipher implements SymmetricCipher {
    private byte[] encryptionKey;
    private byte[] decryptionKey;

    @Override
    public void setEncryptionKey(byte[] key) {
        this.encryptionKey = Arrays.copyOf(key, key.length);
    }

    @Override
    public void setDecryptionKey(byte[] key) {
        this.decryptionKey = Arrays.copyOf(key, key.length);
    }

    @Override
    public byte[] encrypt(byte[] block) {
        if (encryptionKey == null) {
            throw new IllegalStateException("Encryption key not set");
        }
        byte[] result = new byte[block.length];
        for (int i = 0; i < block.length; i++) {
            result[i] = (byte) (block[i] ^ encryptionKey[i % encryptionKey.length]);
        }
        return result;
    }

    @Override
    public byte[] decrypt(byte[] block) {
        if (decryptionKey == null) {
            throw new IllegalStateException("Decryption key not set");
        }
        byte[] result = new byte[block.length];
        for (int i = 0; i < block.length; i++) {
            result[i] = (byte) (block[i] ^ decryptionKey[i % decryptionKey.length]);
        }
        return result;
    }
}

