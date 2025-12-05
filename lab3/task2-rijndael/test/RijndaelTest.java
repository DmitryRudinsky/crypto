import java.util.Arrays;

public class RijndaelTest {
    
    private static int passed = 0;
    private static int failed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running Rijndael tests...\n");
        
        testAES128();
        testAES192();
        testAES256();
        testRijndaelVariants();
        testKeyReuse();
        testCustomModulus();
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        
        if (failed == 0) {
            System.out.println("\nAll tests passed!");
        }
    }
    
    private static void testAES128() {
        System.out.println("Testing AES-128...");
        
        Rijndael cipher = new Rijndael(128, 128, (byte) 0x1B);
        
        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) {
            key[i] = (byte) i;
        }
        
        byte[] plaintext = new byte[16];
        for (int i = 0; i < 16; i++) {
            plaintext[i] = (byte) (i * 2);
        }
        
        cipher.setEncryptionKey(key);
        cipher.setDecryptionKey(key);
        
        byte[] ciphertext = cipher.encrypt(plaintext);
        byte[] decrypted = cipher.decrypt(ciphertext);
        
        assertTrue(Arrays.equals(plaintext, decrypted), "AES-128 encryption/decryption");
        assertFalse(Arrays.equals(plaintext, ciphertext), "AES-128 ciphertext differs from plaintext");
    }
    
    private static void testAES192() {
        System.out.println("\nTesting AES-192...");
        
        Rijndael cipher = new Rijndael(128, 192, (byte) 0x1B);
        
        byte[] key = new byte[24];
        for (int i = 0; i < 24; i++) {
            key[i] = (byte) (i + 10);
        }
        
        byte[] plaintext = new byte[16];
        for (int i = 0; i < 16; i++) {
            plaintext[i] = (byte) (0xFF - i);
        }
        
        cipher.setEncryptionKey(key);
        cipher.setDecryptionKey(key);
        
        byte[] ciphertext = cipher.encrypt(plaintext);
        byte[] decrypted = cipher.decrypt(ciphertext);
        
        assertTrue(Arrays.equals(plaintext, decrypted), "AES-192 encryption/decryption");
    }
    
    private static void testAES256() {
        System.out.println("\nTesting AES-256...");
        
        Rijndael cipher = new Rijndael(128, 256, (byte) 0x1B);
        
        byte[] key = new byte[32];
        for (int i = 0; i < 32; i++) {
            key[i] = (byte) (i * 3);
        }
        
        byte[] plaintext = new byte[16];
        Arrays.fill(plaintext, (byte) 0x42);
        
        cipher.setEncryptionKey(key);
        cipher.setDecryptionKey(key);
        
        byte[] ciphertext = cipher.encrypt(plaintext);
        byte[] decrypted = cipher.decrypt(ciphertext);
        
        assertTrue(Arrays.equals(plaintext, decrypted), "AES-256 encryption/decryption");
    }
    
    private static void testRijndaelVariants() {
        System.out.println("\nTesting Rijndael variants...");
        
        int[][] configs = {
            {192, 128}, {192, 192}, {192, 256},
            {256, 128}, {256, 192}, {256, 256}
        };
        
        for (int[] config : configs) {
            int blockSize = config[0];
            int keySize = config[1];
            
            Rijndael cipher = new Rijndael(blockSize, keySize, (byte) 0x1B);
            
            byte[] key = new byte[keySize / 8];
            for (int i = 0; i < key.length; i++) {
                key[i] = (byte) i;
            }
            
            byte[] plaintext = new byte[blockSize / 8];
            for (int i = 0; i < plaintext.length; i++) {
                plaintext[i] = (byte) (i + 1);
            }
            
            cipher.setEncryptionKey(key);
            cipher.setDecryptionKey(key);
            
            byte[] ciphertext = cipher.encrypt(plaintext);
            byte[] decrypted = cipher.decrypt(ciphertext);
            
            assertTrue(Arrays.equals(plaintext, decrypted), 
                String.format("Rijndael-%d/%d", blockSize, keySize));
        }
    }
    
    private static void testKeyReuse() {
        System.out.println("\nTesting key reuse...");
        
        Rijndael cipher = new Rijndael(128, 128, (byte) 0x1B);
        
        byte[] key = new byte[16];
        for (int i = 0; i < 16; i++) {
            key[i] = (byte) (i * 5);
        }
        
        cipher.setEncryptionKey(key);
        cipher.setDecryptionKey(key);
        
        for (int i = 0; i < 5; i++) {
            byte[] plaintext = new byte[16];
            for (int j = 0; j < 16; j++) {
                plaintext[j] = (byte) (i * 16 + j);
            }
            
            byte[] ciphertext = cipher.encrypt(plaintext);
            byte[] decrypted = cipher.decrypt(ciphertext);
            
            assertTrue(Arrays.equals(plaintext, decrypted), 
                String.format("Key reuse iteration %d", i + 1));
        }
    }
    
    private static void testCustomModulus() {
        System.out.println("\nTesting custom moduli...");
        
        byte[] moduli = {(byte) 0x1D, (byte) 0x2B, (byte) 0x2D, (byte) 0x39};
        
        for (byte modulus : moduli) {
            Rijndael cipher = new Rijndael(128, 128, modulus);
            
            byte[] key = new byte[16];
            Arrays.fill(key, (byte) 0x55);
            
            byte[] plaintext = new byte[16];
            Arrays.fill(plaintext, (byte) 0xAA);
            
            cipher.setEncryptionKey(key);
            cipher.setDecryptionKey(key);
            
            byte[] ciphertext = cipher.encrypt(plaintext);
            byte[] decrypted = cipher.decrypt(ciphertext);
            
            assertTrue(Arrays.equals(plaintext, decrypted), 
                String.format("Custom modulus 0x%02X", (modulus & 0xFF) | 0x100));
        }
    }
    
    private static void assertTrue(boolean condition, String message) {
        if (condition) {
            pass(message);
        } else {
            fail(message);
        }
    }
    
    private static void assertFalse(boolean condition, String message) {
        if (!condition) {
            pass(message);
        } else {
            fail(message);
        }
    }
    
    private static void pass(String message) {
        System.out.println("  ✓ " + message);
        passed++;
    }
    
    private static void fail(String message) {
        System.out.println("  ✗ " + message);
        failed++;
    }
}

