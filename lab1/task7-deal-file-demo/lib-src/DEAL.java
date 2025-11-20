import java.util.Arrays;

/**
 * DEAL (Data Encryption Algorithm with Larger blocks) cipher implementation.
 * 
 * DEAL is a 128-bit block cipher that uses DES as a building block in a Feistel network.
 * It was designed to create a larger block cipher by reusing the well-studied DES algorithm.
 * 
 * Key features:
 * - Block size: 128 bits (16 bytes)
 * - Key size: 192 bits (24 bytes) - three DES keys
 * - Number of rounds: 6 (default)
 * - Uses DES encryption as the round function F
 * 
 * This implementation uses an adapter pattern to integrate DES into the Feistel cipher framework.
 */
public class DEAL implements SymmetricCipher {
    private final FeistelCipher feistelCipher;
    private final int blockSize;
    
    /**
     * Creates a DEAL cipher with the default configuration (6 rounds).
     */
    public DEAL() {
        this(6);
    }
    
    /**
     * Creates a DEAL cipher with a specified number of rounds.
     * 
     * @param numberOfRounds the number of Feistel rounds (must be positive)
     */
    public DEAL(int numberOfRounds) {
        if (numberOfRounds <= 0) {
            throw new IllegalArgumentException("Number of rounds must be positive");
        }
        
        // Use the DES adapter as the round function
        RoundFunction desAdapter = new DESAdapter();
        
        // Use the DEAL key schedule
        KeySchedule keySchedule = new DEALKeySchedule(numberOfRounds);
        
        // Create the Feistel cipher with DES as the round function
        this.feistelCipher = new FeistelCipher(keySchedule, desAdapter);
        
        // DEAL uses 128-bit (16 bytes) blocks
        this.blockSize = 16;
    }
    
    /**
     * Sets the encryption key for DEAL.
     * The key must be at least 24 bytes (192 bits) containing three DES keys.
     * 
     * @param key the encryption key (at least 24 bytes)
     */
    @Override
    public void setEncryptionKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (key.length < 24) {
            throw new IllegalArgumentException(
                "DEAL key must be at least 24 bytes (192 bits). Provided: " + key.length + " bytes"
            );
        }
        feistelCipher.setEncryptionKey(key);
    }
    
    /**
     * Sets the decryption key for DEAL.
     * The key must be at least 24 bytes (192 bits) containing three DES keys.
     * 
     * @param key the decryption key (at least 24 bytes)
     */
    @Override
    public void setDecryptionKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (key.length < 24) {
            throw new IllegalArgumentException(
                "DEAL key must be at least 24 bytes (192 bits). Provided: " + key.length + " bytes"
            );
        }
        feistelCipher.setDecryptionKey(key);
    }
    
    /**
     * Encrypts a 128-bit (16 bytes) block using DEAL.
     * 
     * @param block the plaintext block (must be 16 bytes)
     * @return the encrypted ciphertext block (16 bytes)
     */
    @Override
    public byte[] encrypt(byte[] block) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        if (block.length != blockSize) {
            throw new IllegalArgumentException(
                "DEAL block must be " + blockSize + " bytes (128 bits). Provided: " + block.length + " bytes"
            );
        }
        
        return feistelCipher.encrypt(block);
    }
    
    /**
     * Decrypts a 128-bit (16 bytes) block using DEAL.
     * 
     * @param block the ciphertext block (must be 16 bytes)
     * @return the decrypted plaintext block (16 bytes)
     */
    @Override
    public byte[] decrypt(byte[] block) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        if (block.length != blockSize) {
            throw new IllegalArgumentException(
                "DEAL block must be " + blockSize + " bytes (128 bits). Provided: " + block.length + " bytes"
            );
        }
        
        return feistelCipher.decrypt(block);
    }
    
    /**
     * Gets the block size used by DEAL.
     * 
     * @return the block size in bytes (16)
     */
    public int getBlockSize() {
        return blockSize;
    }
}

