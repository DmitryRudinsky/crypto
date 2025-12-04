import java.util.Arrays;

public class DEALKeySchedule implements KeySchedule {
    private final int numberOfRounds;
    
    public DEALKeySchedule() {
        this(6);
    }
    
    public DEALKeySchedule(int numberOfRounds) {
        if (numberOfRounds <= 0) {
            throw new IllegalArgumentException("Number of rounds must be positive");
        }
        this.numberOfRounds = numberOfRounds;
    }
    
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
        
        byte[] k1 = Arrays.copyOfRange(key, 0, 8);
        byte[] k2 = Arrays.copyOfRange(key, 8, 16);
        byte[] k3 = Arrays.copyOfRange(key, 16, 24);
        
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
