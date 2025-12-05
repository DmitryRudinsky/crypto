public interface SymmetricCipher {
    void setEncryptionKey(byte[] key);
    void setDecryptionKey(byte[] key);
    byte[] encrypt(byte[] block);
    byte[] decrypt(byte[] block);
}

