import java.util.Arrays;

public class DES implements SymmetricCipher {
    private final FeistelCipher feistelCipher;
    
    public DES() {
        KeySchedule keySchedule = new DESKeySchedule();
        RoundFunction roundFunction = new DESRoundFunction();
        this.feistelCipher = new FeistelCipher(keySchedule, roundFunction);
    }
    
    @Override
    public void setEncryptionKey(byte[] key) {
        if (key == null || key.length != 8) {
            throw new IllegalArgumentException("DES key must be 8 bytes (64 bits)");
        }
        feistelCipher.setEncryptionKey(key);
    }
    
    @Override
    public void setDecryptionKey(byte[] key) {
        if (key == null || key.length != 8) {
            throw new IllegalArgumentException("DES key must be 8 bytes (64 bits)");
        }
        feistelCipher.setDecryptionKey(key);
    }
    
    @Override
    public byte[] encrypt(byte[] block) {
        if (block == null || block.length != 8) {
            throw new IllegalArgumentException("DES block must be 8 bytes (64 bits)");
        }
        
        byte[] permuted = BitPermutation.permutateBits(
            block,
            DESConstants.IP,
            BitIndexMode.INVERTED,
            false
        );
        
        byte[] feistelOutput = feistelCipher.encrypt(permuted);
        
        byte[] result = BitPermutation.permutateBits(
            feistelOutput,
            DESConstants.IP_INV,
            BitIndexMode.INVERTED,
            false
        );
        
        return result;
    }
    
    @Override
    public byte[] decrypt(byte[] block) {
        if (block == null || block.length != 8) {
            throw new IllegalArgumentException("DES block must be 8 bytes (64 bits)");
        }
        
        byte[] permuted = BitPermutation.permutateBits(
            block,
            DESConstants.IP,
            BitIndexMode.INVERTED,
            false
        );
        
        byte[] feistelOutput = feistelCipher.decrypt(permuted);
        
        byte[] result = BitPermutation.permutateBits(
            feistelOutput,
            DESConstants.IP_INV,
            BitIndexMode.INVERTED,
            false
        );
        
        return result;
    }
}

