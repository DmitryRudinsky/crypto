import java.security.SecureRandom;

public enum PaddingMode {
    ZEROS {
        @Override
        public byte[] pad(byte[] data, int blockSize) {
            int paddingSize = blockSize - data.length % blockSize;
            if (paddingSize == blockSize) {
                paddingSize = 0;
            }

            byte[] padded = new byte[data.length + paddingSize];
            System.arraycopy(data, 0, padded, 0, data.length);
            return padded;
        }

        @Override
        public byte[] unpad(byte[] data, int blockSize) throws IllegalArgumentException {
            int i = data.length - 1;
            while (i >= 0 && data[i] == 0) {
                i--;
            }
            byte[] result = new byte[i + 1];
            System.arraycopy(data, 0, result, 0, i + 1);
            return result;
        }
    },

    ANSI_X923 {
        @Override
        public byte[] pad(byte[] data, int blockSize) {
            int paddingSize = blockSize - data.length % blockSize;
            if (paddingSize == blockSize) {
                paddingSize = blockSize;
            }

            byte[] padded = new byte[data.length + paddingSize];
            System.arraycopy(data, 0, padded, 0, data.length);
            padded[padded.length - 1] = (byte) paddingSize;
            return padded;
        }

        @Override
        public byte[] unpad(byte[] data, int blockSize) throws IllegalArgumentException {
            if (data.length == 0) {
                throw new IllegalArgumentException("Empty data");
            }
            int paddingSize = data[data.length - 1] & 0xFF;
            if (paddingSize > data.length || paddingSize > blockSize) {
                throw new IllegalArgumentException("Invalid padding");
            }

            for (int i = data.length - paddingSize; i < data.length - 1; i++) {
                if (data[i] != 0) {
                    throw new IllegalArgumentException("Invalid ANSI X.923 padding");
                }
            }

            byte[] result = new byte[data.length - paddingSize];
            System.arraycopy(data, 0, result, 0, data.length - paddingSize);
            return result;
        }
    },

    PKCS7 {
        @Override
        public byte[] pad(byte[] data, int blockSize) {
            int paddingSize = blockSize - data.length % blockSize;
            if (paddingSize == blockSize) {
                paddingSize = blockSize;
            }

            byte[] padded = new byte[data.length + paddingSize];
            System.arraycopy(data, 0, padded, 0, data.length);
            for (int i = data.length; i < padded.length; i++) {
                padded[i] = (byte) paddingSize;
            }
            return padded;
        }

        @Override
        public byte[] unpad(byte[] data, int blockSize) throws IllegalArgumentException {
            if (data.length == 0) {
                throw new IllegalArgumentException("Empty data");
            }
            int paddingSize = data[data.length - 1] & 0xFF;
            if (paddingSize > data.length || paddingSize > blockSize) {
                throw new IllegalArgumentException("Invalid padding");
            }

            for (int i = data.length - paddingSize; i < data.length; i++) {
                if (data[i] != (byte) paddingSize) {
                    throw new IllegalArgumentException("Invalid PKCS7 padding");
                }
            }

            byte[] result = new byte[data.length - paddingSize];
            System.arraycopy(data, 0, result, 0, data.length - paddingSize);
            return result;
        }
    },

    ISO_10126 {
        @Override
        public byte[] pad(byte[] data, int blockSize) {
            int paddingSize = blockSize - data.length % blockSize;
            if (paddingSize == blockSize) {
                paddingSize = blockSize;
            }

            byte[] padded = new byte[data.length + paddingSize];
            System.arraycopy(data, 0, padded, 0, data.length);

            if (paddingSize > 1) {
                byte[] randomBytes = new byte[paddingSize - 1];
                new SecureRandom().nextBytes(randomBytes);
                System.arraycopy(randomBytes, 0, padded, data.length, paddingSize - 1);
            }

            padded[padded.length - 1] = (byte) paddingSize;
            return padded;
        }

        @Override
        public byte[] unpad(byte[] data, int blockSize) throws IllegalArgumentException {
            if (data.length == 0) {
                throw new IllegalArgumentException("Empty data");
            }
            int paddingSize = data[data.length - 1] & 0xFF;
            if (paddingSize > data.length || paddingSize > blockSize) {
                throw new IllegalArgumentException("Invalid padding");
            }

            byte[] result = new byte[data.length - paddingSize];
            System.arraycopy(data, 0, result, 0, data.length - paddingSize);
            return result;
        }
    };

    public abstract byte[] pad(byte[] data, int blockSize);
    public abstract byte[] unpad(byte[] data, int blockSize) throws IllegalArgumentException;
}

