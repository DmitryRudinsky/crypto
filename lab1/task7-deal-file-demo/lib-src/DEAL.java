import java.util.Arrays;

public class DEAL implements SymmetricCipher {
    private final FeistelCipher feistelCipher;
    private final int blockSize;
    public DEAL() {
        this(6);
    }
    public DEAL(int numberOfRounds) {
        if (numberOfRounds <= 0) {
            throw new IllegalArgumentException("Number of rounds must be positive");
        }
        
        RoundFunction desAdapter = new DESAdapter();
        
        KeySchedule keySchedule = new DEALKeySchedule(numberOfRounds);
        
        this.feistelCipher = new FeistelCipher(keySchedule, desAdapter);
        
        this.blockSize = 16;
    }
    
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
    
    public int getBlockSize() {
        return blockSize;
    }
}

