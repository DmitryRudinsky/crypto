import java.util.Arrays;

public class SimpleKeySchedule implements KeySchedule {
    private final int rounds;

    public SimpleKeySchedule(int rounds) {
        if (rounds <= 0) {
            throw new IllegalArgumentException("Number of rounds must be positive");
        }
        this.rounds = rounds;
    }

    @Override
    public byte[][] expandKey(byte[] key) {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        byte[][] roundKeys = new byte[rounds][];

        for (int i = 0; i < rounds; i++) {
            roundKeys[i] = new byte[key.length];
            for (int j = 0; j < key.length; j++) {
                roundKeys[i][j] = (byte) (key[j] ^ (i + 1));
            }
        }

        return roundKeys;
    }
}

