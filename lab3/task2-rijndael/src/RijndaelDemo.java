import java.util.Arrays;

public class RijndaelDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Rijndael (AES) Demo ===\n");
        
        byte modulus = (byte) 0x1B;
        
        testConfiguration(128, 128, modulus, "AES-128");
        testConfiguration(192, 192, modulus, "AES-192");
        testConfiguration(256, 256, modulus, "AES-256");
        
        testConfiguration(128, 192, modulus, "Rijndael-128/192");
        testConfiguration(192, 256, modulus, "Rijndael-192/256");
        
        System.out.println("\n=== Custom Modulus Test ===");
        byte customModulus = (byte) 0x2B;
        testConfiguration(128, 128, customModulus, "AES-128 with custom modulus 0x12B");
    }
    
    private static void testConfiguration(int blockSize, int keySize, byte modulus, String name) {
        System.out.println("--- " + name + " ---");
        
        Rijndael cipher = new Rijndael(blockSize, keySize, modulus);
        
        byte[] key = new byte[keySize / 8];
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) (i * 7 + 13);
        }
        
        byte[] plaintext = new byte[blockSize / 8];
        for (int i = 0; i < plaintext.length; i++) {
            plaintext[i] = (byte) (i * 3 + 5);
        }
        
        cipher.setEncryptionKey(key);
        cipher.setDecryptionKey(key);
        
        byte[] ciphertext = cipher.encrypt(plaintext);
        byte[] decrypted = cipher.decrypt(ciphertext);
        
        System.out.println("Plaintext:  " + bytesToHex(plaintext));
        System.out.println("Ciphertext: " + bytesToHex(ciphertext));
        System.out.println("Decrypted:  " + bytesToHex(decrypted));
        System.out.println("Match: " + Arrays.equals(plaintext, decrypted));
        System.out.println();
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(16, bytes.length); i++) {
            sb.append(String.format("%02X ", bytes[i] & 0xFF));
        }
        if (bytes.length > 16) {
            sb.append("...");
        }
        return sb.toString().trim();
    }
}

