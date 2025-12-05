public class Rijndael implements SymmetricCipher, KeySchedule {
    private final int blockSize;
    private final int keySize;
    private final byte modulus;
    private final int Nb;
    private final int Nk;
    private final int Nr;
    
    private byte[] sBox;
    private byte[] invSBox;
    private byte[][] encryptionRoundKeys;
    
    public Rijndael(int blockSizeBits, int keySizeBits, byte modulus) {
        if (blockSizeBits != 128 && blockSizeBits != 192 && blockSizeBits != 256) {
            throw new IllegalArgumentException("Block size must be 128, 192, or 256 bits");
        }
        if (keySizeBits != 128 && keySizeBits != 192 && keySizeBits != 256) {
            throw new IllegalArgumentException("Key size must be 128, 192, or 256 bits");
        }
        
        this.blockSize = blockSizeBits / 8;
        this.keySize = keySizeBits / 8;
        this.modulus = modulus;
        this.Nb = blockSizeBits / 32;
        this.Nk = keySizeBits / 32;
        this.Nr = Math.max(Nb, Nk) + 6;
        
        initializeSBoxes();
    }
    
    private void initializeSBoxes() {
        sBox = new byte[256];
        invSBox = new byte[256];
        
        sBox[0] = 0x63;
        invSBox[0x63] = 0;
        
        for (int i = 1; i < 256; i++) {
            byte inv = GF256Service.inverse((byte) i, modulus);
            byte transformed = affineTransform(inv);
            sBox[i] = transformed;
            invSBox[transformed & 0xFF] = (byte) i;
        }
    }
    
    private byte affineTransform(byte b) {
        int val = b & 0xFF;
        int result = 0;
        
        for (int i = 0; i < 8; i++) {
            int bit = 0;
            bit ^= (val >> i) & 1;
            bit ^= (val >> ((i + 4) % 8)) & 1;
            bit ^= (val >> ((i + 5) % 8)) & 1;
            bit ^= (val >> ((i + 6) % 8)) & 1;
            bit ^= (val >> ((i + 7) % 8)) & 1;
            bit ^= (0x63 >> i) & 1;
            
            result |= (bit << i);
        }
        
        return (byte) result;
    }
    
    @Override
    public void setEncryptionKey(byte[] key) {
        if (key.length != keySize) {
            throw new IllegalArgumentException("Invalid key size");
        }
        encryptionRoundKeys = expandKey(key);
    }
    
    @Override
    public void setDecryptionKey(byte[] key) {
        setEncryptionKey(key);
    }
    
    @Override
    public byte[][] expandKey(byte[] key) {
        int roundKeyCount = Nr + 1;
        byte[][] roundKeys = new byte[roundKeyCount][blockSize];
        
        byte[][] w = new byte[Nb * roundKeyCount][4];
        
        for (int i = 0; i < Nk; i++) {
            for (int j = 0; j < 4; j++) {
                w[i][j] = key[4 * i + j];
            }
        }
        
        for (int i = Nk; i < Nb * roundKeyCount; i++) {
            byte[] temp = w[i - 1].clone();
            
            if (i % Nk == 0) {
                temp = subWord(rotWord(temp));
                temp[0] ^= rcon(i / Nk);
            } else if (Nk > 6 && i % Nk == 4) {
                temp = subWord(temp);
            }
            
            for (int j = 0; j < 4; j++) {
                w[i][j] = (byte) (w[i - Nk][j] ^ temp[j]);
            }
        }
        
        for (int round = 0; round < roundKeyCount; round++) {
            for (int col = 0; col < Nb; col++) {
                for (int row = 0; row < 4; row++) {
                    roundKeys[round][row * Nb + col] = w[round * Nb + col][row];
                }
            }
        }
        
        return roundKeys;
    }
    
    private byte[] rotWord(byte[] word) {
        byte[] result = new byte[4];
        result[0] = word[1];
        result[1] = word[2];
        result[2] = word[3];
        result[3] = word[0];
        return result;
    }
    
    private byte[] subWord(byte[] word) {
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = sBox[word[i] & 0xFF];
        }
        return result;
    }
    
    private byte rcon(int i) {
        if (i == 1) return 0x01;
        byte result = 0x02;
        for (int j = 1; j < i - 1; j++) {
            result = GF256Service.multiplyMod(result, (byte) 0x02, modulus);
        }
        return result;
    }
    
    @Override
    public byte[] encrypt(byte[] block) {
        if (block.length != blockSize) {
            throw new IllegalArgumentException("Invalid block size");
        }
        
        byte[][] state = new byte[4][Nb];
        for (int col = 0; col < Nb; col++) {
            for (int row = 0; row < 4; row++) {
                state[row][col] = block[col * 4 + row];
            }
        }
        
        addRoundKey(state, encryptionRoundKeys[0]);
        
        for (int round = 1; round < Nr; round++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, encryptionRoundKeys[round]);
        }
        
        subBytes(state);
        shiftRows(state);
        addRoundKey(state, encryptionRoundKeys[Nr]);
        
        byte[] result = new byte[blockSize];
        for (int col = 0; col < Nb; col++) {
            for (int row = 0; row < 4; row++) {
                result[col * 4 + row] = state[row][col];
            }
        }
        
        return result;
    }
    
    @Override
    public byte[] decrypt(byte[] block) {
        if (block.length != blockSize) {
            throw new IllegalArgumentException("Invalid block size");
        }
        
        byte[][] state = new byte[4][Nb];
        for (int col = 0; col < Nb; col++) {
            for (int row = 0; row < 4; row++) {
                state[row][col] = block[col * 4 + row];
            }
        }
        
        addRoundKey(state, encryptionRoundKeys[Nr]);
        
        for (int round = Nr - 1; round >= 1; round--) {
            invShiftRows(state);
            invSubBytes(state);
            addRoundKey(state, encryptionRoundKeys[round]);
            invMixColumns(state);
        }
        
        invShiftRows(state);
        invSubBytes(state);
        addRoundKey(state, encryptionRoundKeys[0]);
        
        byte[] result = new byte[blockSize];
        for (int col = 0; col < Nb; col++) {
            for (int row = 0; row < 4; row++) {
                result[col * 4 + row] = state[row][col];
            }
        }
        
        return result;
    }
    
    private void subBytes(byte[][] state) {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < Nb; col++) {
                state[row][col] = sBox[state[row][col] & 0xFF];
            }
        }
    }
    
    private void invSubBytes(byte[][] state) {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < Nb; col++) {
                state[row][col] = invSBox[state[row][col] & 0xFF];
            }
        }
    }
    
    private void shiftRows(byte[][] state) {
        for (int row = 1; row < 4; row++) {
            byte[] temp = new byte[Nb];
            for (int col = 0; col < Nb; col++) {
                temp[col] = state[row][(col + row) % Nb];
            }
            state[row] = temp;
        }
    }
    
    private void invShiftRows(byte[][] state) {
        for (int row = 1; row < 4; row++) {
            byte[] temp = new byte[Nb];
            for (int col = 0; col < Nb; col++) {
                temp[col] = state[row][(col - row + Nb) % Nb];
            }
            state[row] = temp;
        }
    }
    
    private void mixColumns(byte[][] state) {
        for (int col = 0; col < Nb; col++) {
            byte[] column = new byte[4];
            for (int row = 0; row < 4; row++) {
                column[row] = state[row][col];
            }
            
            byte[] mixed = mixColumn(column);
            
            for (int row = 0; row < 4; row++) {
                state[row][col] = mixed[row];
            }
        }
    }
    
    private byte[] mixColumn(byte[] column) {
        byte[] result = new byte[4];
        
        result[0] = (byte) (
            GF256Service.multiplyMod((byte) 0x02, column[0], modulus) ^
            GF256Service.multiplyMod((byte) 0x03, column[1], modulus) ^
            column[2] ^
            column[3]
        );
        
        result[1] = (byte) (
            column[0] ^
            GF256Service.multiplyMod((byte) 0x02, column[1], modulus) ^
            GF256Service.multiplyMod((byte) 0x03, column[2], modulus) ^
            column[3]
        );
        
        result[2] = (byte) (
            column[0] ^
            column[1] ^
            GF256Service.multiplyMod((byte) 0x02, column[2], modulus) ^
            GF256Service.multiplyMod((byte) 0x03, column[3], modulus)
        );
        
        result[3] = (byte) (
            GF256Service.multiplyMod((byte) 0x03, column[0], modulus) ^
            column[1] ^
            column[2] ^
            GF256Service.multiplyMod((byte) 0x02, column[3], modulus)
        );
        
        return result;
    }
    
    private void invMixColumns(byte[][] state) {
        for (int col = 0; col < Nb; col++) {
            byte[] column = new byte[4];
            for (int row = 0; row < 4; row++) {
                column[row] = state[row][col];
            }
            
            byte[] mixed = invMixColumn(column);
            
            for (int row = 0; row < 4; row++) {
                state[row][col] = mixed[row];
            }
        }
    }
    
    private byte[] invMixColumn(byte[] column) {
        byte[] result = new byte[4];
        
        result[0] = (byte) (
            GF256Service.multiplyMod((byte) 0x0E, column[0], modulus) ^
            GF256Service.multiplyMod((byte) 0x0B, column[1], modulus) ^
            GF256Service.multiplyMod((byte) 0x0D, column[2], modulus) ^
            GF256Service.multiplyMod((byte) 0x09, column[3], modulus)
        );
        
        result[1] = (byte) (
            GF256Service.multiplyMod((byte) 0x09, column[0], modulus) ^
            GF256Service.multiplyMod((byte) 0x0E, column[1], modulus) ^
            GF256Service.multiplyMod((byte) 0x0B, column[2], modulus) ^
            GF256Service.multiplyMod((byte) 0x0D, column[3], modulus)
        );
        
        result[2] = (byte) (
            GF256Service.multiplyMod((byte) 0x0D, column[0], modulus) ^
            GF256Service.multiplyMod((byte) 0x09, column[1], modulus) ^
            GF256Service.multiplyMod((byte) 0x0E, column[2], modulus) ^
            GF256Service.multiplyMod((byte) 0x0B, column[3], modulus)
        );
        
        result[3] = (byte) (
            GF256Service.multiplyMod((byte) 0x0B, column[0], modulus) ^
            GF256Service.multiplyMod((byte) 0x0D, column[1], modulus) ^
            GF256Service.multiplyMod((byte) 0x09, column[2], modulus) ^
            GF256Service.multiplyMod((byte) 0x0E, column[3], modulus)
        );
        
        return result;
    }
    
    private void addRoundKey(byte[][] state, byte[] roundKey) {
        for (int col = 0; col < Nb; col++) {
            for (int row = 0; row < 4; row++) {
                state[row][col] ^= roundKey[col * 4 + row];
            }
        }
    }
}

