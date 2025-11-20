import java.security.SecureRandom;
import java.util.Arrays;

public enum CipherMode {
    ECB {
        @Override
        public byte[][] encrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            for (int i = 0; i < blocks.length; i++) {
                result[i] = cipher.encrypt(blocks[i]);
            }
            return result;
        }

        @Override
        public byte[][] decrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            for (int i = 0; i < blocks.length; i++) {
                result[i] = cipher.decrypt(blocks[i]);
            }
            return result;
        }

        @Override
        public boolean requiresIV() {
            return false;
        }
    },

    CBC {
        @Override
        public byte[][] encrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            byte[] prev = Arrays.copyOf(iv, iv.length);

            for (int i = 0; i < blocks.length; i++) {
                byte[] xored = xorBytes(blocks[i], prev);
                byte[] encrypted = cipher.encrypt(xored);
                result[i] = encrypted;
                prev = encrypted;
            }
            return result;
        }

        @Override
        public byte[][] decrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            byte[] prev = Arrays.copyOf(iv, iv.length);

            for (int i = 0; i < blocks.length; i++) {
                byte[] decrypted = cipher.decrypt(blocks[i]);
                result[i] = xorBytes(decrypted, prev);
                prev = blocks[i];
            }
            return result;
        }

        @Override
        public boolean requiresIV() {
            return true;
        }
    },

    PCBC {
        @Override
        public byte[][] encrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            byte[] prev = Arrays.copyOf(iv, iv.length);

            for (int i = 0; i < blocks.length; i++) {
                byte[] xored = xorBytes(blocks[i], prev);
                byte[] encrypted = cipher.encrypt(xored);
                result[i] = encrypted;
                prev = xorBytes(blocks[i], encrypted);
            }
            return result;
        }

        @Override
        public byte[][] decrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            byte[] prev = Arrays.copyOf(iv, iv.length);

            for (int i = 0; i < blocks.length; i++) {
                byte[] decrypted = cipher.decrypt(blocks[i]);
                result[i] = xorBytes(decrypted, prev);
                prev = xorBytes(blocks[i], result[i]);
            }
            return result;
        }

        @Override
        public boolean requiresIV() {
            return true;
        }
    },

    CFB {
        @Override
        public byte[][] encrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            byte[] feedback = Arrays.copyOf(iv, iv.length);

            for (int i = 0; i < blocks.length; i++) {
                byte[] encrypted = cipher.encrypt(feedback);
                result[i] = xorBytes(blocks[i], encrypted);
                feedback = result[i];
            }
            return result;
        }

        @Override
        public byte[][] decrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            byte[] feedback = Arrays.copyOf(iv, iv.length);

            for (int i = 0; i < blocks.length; i++) {
                byte[] encrypted = cipher.encrypt(feedback);
                result[i] = xorBytes(blocks[i], encrypted);
                feedback = blocks[i];
            }
            return result;
        }

        @Override
        public boolean requiresIV() {
            return true;
        }
    },

    OFB {
        @Override
        public byte[][] encrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            return process(blocks, cipher, iv);
        }

        @Override
        public byte[][] decrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            return process(blocks, cipher, iv);
        }

        private byte[][] process(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            byte[] feedback = Arrays.copyOf(iv, iv.length);

            for (int i = 0; i < blocks.length; i++) {
                byte[] encrypted = cipher.encrypt(feedback);
                result[i] = xorBytes(blocks[i], encrypted);
                feedback = encrypted;
            }
            return result;
        }

        @Override
        public boolean requiresIV() {
            return true;
        }
    },

    CTR {
        @Override
        public byte[][] encrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            return process(blocks, cipher, iv);
        }

        @Override
        public byte[][] decrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            return process(blocks, cipher, iv);
        }

        private byte[][] process(byte[][] blocks, SymmetricCipher cipher, byte[] counter) {
            byte[][] result = new byte[blocks.length][];
            byte[] currentCounter = Arrays.copyOf(counter, counter.length);

            for (int i = 0; i < blocks.length; i++) {
                byte[] encrypted = cipher.encrypt(currentCounter);
                result[i] = xorBytes(blocks[i], encrypted);
                incrementCounter(currentCounter);
            }
            return result;
        }

        private void incrementCounter(byte[] counter) {
            for (int i = counter.length - 1; i >= 0; i--) {
                counter[i]++;
                if (counter[i] != 0) {
                    break;
                }
            }
        }

        @Override
        public boolean requiresIV() {
            return true;
        }
    },

    RANDOM_DELTA {
        @Override
        public byte[][] encrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            SecureRandom random = new SecureRandom();

            for (int i = 0; i < blocks.length; i++) {
                byte[] delta = new byte[blocks[i].length];
                random.nextBytes(delta);

                byte[] modified = xorBytes(blocks[i], delta);
                byte[] encrypted = cipher.encrypt(modified);
                byte[] encryptedDelta = cipher.encrypt(delta);

                result[i] = new byte[encrypted.length + encryptedDelta.length];
                System.arraycopy(encrypted, 0, result[i], 0, encrypted.length);
                System.arraycopy(encryptedDelta, 0, result[i], encrypted.length, encryptedDelta.length);
            }
            return result;
        }

        @Override
        public byte[][] decrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv) {
            byte[][] result = new byte[blocks.length][];
            int halfSize = blocks[0].length / 2;

            for (int i = 0; i < blocks.length; i++) {
                byte[] dataPart = Arrays.copyOfRange(blocks[i], 0, halfSize);
                byte[] deltaPart = Arrays.copyOfRange(blocks[i], halfSize, blocks[i].length);

                byte[] delta = cipher.decrypt(deltaPart);
                byte[] decrypted = cipher.decrypt(dataPart);

                result[i] = xorBytes(decrypted, delta);
            }
            return result;
        }

        @Override
        public boolean requiresIV() {
            return false;
        }
    };

    public abstract byte[][] encrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv);
    public abstract byte[][] decrypt(byte[][] blocks, SymmetricCipher cipher, byte[] iv);
    public abstract boolean requiresIV();

    protected static byte[] xorBytes(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
}

