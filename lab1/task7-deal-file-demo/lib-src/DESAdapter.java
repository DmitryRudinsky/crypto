import java.util.Arrays;

/**
 * Adapter that allows using DES encryption as a round function in a Feistel network.
 * This adapter wraps the DES cipher to conform to the RoundFunction interface,
 * enabling DES to be used as the F-function in larger block ciphers like DEAL.
 */
public class DESAdapter implements RoundFunction {
    private final DES des;
    
    public DESAdapter() {
        this.des = new DES();
    }
    
    /**
     * Encrypts a block using DES with the provided round key.
     * The block must be 8 bytes (64 bits) as required by DES.
     * 
     * @param block the 8-byte block to encrypt
     * @param roundKey the 8-byte DES key for this round
     * @return the encrypted 8-byte block
     */
    @Override
    public byte[] encryptBlock(byte[] block, byte[] roundKey) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        if (roundKey == null) {
            throw new IllegalArgumentException("Round key cannot be null");
        }
        if (block.length != 8) {
            throw new IllegalArgumentException("Block must be 8 bytes for DES");
        }
        if (roundKey.length != 8) {
            throw new IllegalArgumentException("Round key must be 8 bytes for DES");
        }
        
        // Set the key and encrypt the block
        des.setEncryptionKey(roundKey);
        return des.encrypt(block);
    }
    
    /**
     * Decrypts a block using DES with the provided round key.
     * Note: In a Feistel network, decryption uses the same function as encryption,
     * but this is provided for completeness.
     * 
     * @param block the 8-byte block to decrypt
     * @param roundKey the 8-byte DES key for this round
     * @return the decrypted 8-byte block
     */
    @Override
    public byte[] decryptBlock(byte[] block, byte[] roundKey) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        if (roundKey == null) {
            throw new IllegalArgumentException("Round key cannot be null");
        }
        if (block.length != 8) {
            throw new IllegalArgumentException("Block must be 8 bytes for DES");
        }
        if (roundKey.length != 8) {
            throw new IllegalArgumentException("Round key must be 8 bytes for DES");
        }
        
        // Set the key and decrypt the block
        des.setDecryptionKey(roundKey);
        return des.decrypt(block);
    }
}

