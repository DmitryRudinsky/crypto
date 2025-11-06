import java.util.Arrays;

/**
 * Key schedule for the DEAL cipher.
 * DEAL uses DES as a building block and requires multiple DES keys.
 * This implementation uses a 192-bit (24 bytes) master key that contains
 * three 64-bit DES keys (K1, K2, K3).
 * 
 * The standard DEAL-6 uses 6 rounds with keys arranged as: K1, K2, K3, K1, K2, K3
 */
public class DEALKeySchedule implements KeySchedule {
    private final int numberOfRounds;
    
    /**
     * Creates a DEAL key schedule with the default number of rounds (6).
     */
    public DEALKeySchedule() {
        this(6);
    }
    
    /**
     * Creates a DEAL key schedule with a specified number of rounds.
     * 
     * @param numberOfRounds the number of Feistel rounds (must be positive)
     */
    public DEALKeySchedule(int numberOfRounds) {
        if (numberOfRounds <= 0) {
            throw new IllegalArgumentException("Number of rounds must be positive");
        }
        this.numberOfRounds = numberOfRounds;
    }
    
    /**
     * Expands the master key into round keys.
     * The master key should be at least 24 bytes (192 bits) containing three DES keys.
     * If the key is longer, only the first 24 bytes are used.
     * Each round key is 8 bytes (64 bits) for DES.
     * 
     * @param key the master key (at least 24 bytes)
     * @return array of round keys, each 8 bytes
     */
    @Override
    public byte[][] expandKey(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (key.length < 24) {
            throw new IllegalArgumentException(
                "DEAL key must be at least 24 bytes (192 bits) to contain three DES keys. " +
                "Provided: " + key.length + " bytes"
            );
        }
        
        // Extract three DES keys from the master key
        byte[] k1 = Arrays.copyOfRange(key, 0, 8);
        byte[] k2 = Arrays.copyOfRange(key, 8, 16);
        byte[] k3 = Arrays.copyOfRange(key, 16, 24);
        
        // Create round keys by cycling through K1, K2, K3
        byte[][] roundKeys = new byte[numberOfRounds][8];
        for (int i = 0; i < numberOfRounds; i++) {
            switch (i % 3) {
                case 0:
                    roundKeys[i] = Arrays.copyOf(k1, 8);
                    break;
                case 1:
                    roundKeys[i] = Arrays.copyOf(k2, 8);
                    break;
                case 2:
                    roundKeys[i] = Arrays.copyOf(k3, 8);
                    break;
            }
        }
        
        return roundKeys;
    }
}

