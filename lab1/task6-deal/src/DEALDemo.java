import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Demonstration of the DEAL cipher implementation.
 * 
 * This demo shows:
 * 1. Basic encryption and decryption of a single block
 * 2. Encryption with different round configurations
 * 3. Working with CipherContext for complete messages
 * 4. Comparison with DES block cipher
 */
public class DEALDemo {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("DEAL (Data Encryption Algorithm with Larger blocks) Demonstration");
        System.out.println("=".repeat(80));
        System.out.println();
        
        demoBasicEncryption();
        System.out.println();
        
        demoRoundVariations();
        System.out.println();
        
        demoWithCipherContext();
        System.out.println();
        
        demoComparisonWithDES();
        System.out.println();
        
        System.out.println("=".repeat(80));
        System.out.println("DEAL demonstration completed successfully!");
        System.out.println("=".repeat(80));
    }
    
    /**
     * Demonstrates basic encryption and decryption with DEAL.
     */
    private static void demoBasicEncryption() {
        System.out.println("1. BASIC DEAL ENCRYPTION/DECRYPTION");
        System.out.println("-".repeat(80));
        
        // Create a 192-bit (24 bytes) key containing three DES keys
        byte[] key = new byte[24];
        for (int i = 0; i < 24; i++) {
            key[i] = (byte) (i + 1);
        }
        
        // Create a 128-bit (16 bytes) plaintext block
        byte[] plaintext = "Hello DEAL!     ".getBytes(StandardCharsets.UTF_8);
        
        System.out.println("Key (24 bytes):       " + bytesToHex(key));
        System.out.println("Plaintext (16 bytes): " + bytesToHex(plaintext));
        System.out.println("Plaintext (text):     \"" + new String(plaintext, StandardCharsets.UTF_8) + "\"");
        System.out.println();
        
        // Create DEAL cipher
        DEAL deal = new DEAL();
        deal.setEncryptionKey(key);
        deal.setDecryptionKey(key);
        
        // Encrypt
        byte[] ciphertext = deal.encrypt(plaintext);
        System.out.println("Ciphertext:           " + bytesToHex(ciphertext));
        System.out.println();
        
        // Decrypt
        byte[] decrypted = deal.decrypt(ciphertext);
        System.out.println("Decrypted:            " + bytesToHex(decrypted));
        System.out.println("Decrypted (text):     \"" + new String(decrypted, StandardCharsets.UTF_8) + "\"");
        System.out.println();
        
        // Verify
        boolean matches = Arrays.equals(plaintext, decrypted);
        System.out.println("Decryption successful: " + matches);
    }
    
    /**
     * Demonstrates DEAL with different numbers of rounds.
     */
    private static void demoRoundVariations() {
        System.out.println("2. DEAL WITH DIFFERENT ROUND CONFIGURATIONS");
        System.out.println("-".repeat(80));
        
        byte[] key = "DEAL_SECRET_KEY_192BITS!".getBytes(StandardCharsets.UTF_8);
        byte[] plaintext = "DEAL Test Block!".getBytes(StandardCharsets.UTF_8);
        
        System.out.println("Plaintext: " + bytesToHex(plaintext));
        System.out.println();
        
        // Test with different numbers of rounds
        int[] rounds = {3, 6, 9, 12};
        
        for (int numRounds : rounds) {
            DEAL deal = new DEAL(numRounds);
            deal.setEncryptionKey(key);
            deal.setDecryptionKey(key);
            
            byte[] ciphertext = deal.encrypt(plaintext);
            byte[] decrypted = deal.decrypt(ciphertext);
            
            boolean success = Arrays.equals(plaintext, decrypted);
            
            System.out.printf("Rounds: %2d | Ciphertext: %s | Success: %s%n",
                numRounds, 
                bytesToHex(ciphertext).substring(0, 32) + "...",
                success
            );
        }
    }
    
    /**
     * Demonstrates DEAL with CipherContext for encrypting complete messages.
     */
    private static void demoWithCipherContext() {
        System.out.println("3. DEAL WITH CIPHER CONTEXT (COMPLETE MESSAGES)");
        System.out.println("-".repeat(80));
        
        // Create a longer message
        String message = "DEAL cipher uses DES as a building block to create a 128-bit block cipher!";
        byte[] plaintext = message.getBytes(StandardCharsets.UTF_8);
        
        System.out.println("Original message: \"" + message + "\"");
        System.out.println("Message length:   " + plaintext.length + " bytes");
        System.out.println();
        
        // Setup key
        byte[] key = "DEAL_MASTER_KEY_192BITS!".getBytes(StandardCharsets.UTF_8);
        
        // Create DEAL cipher
        DEAL deal = new DEAL();
        deal.setEncryptionKey(key);
        deal.setDecryptionKey(key);
        
        // Create cipher context with ECB mode and PKCS7 padding
        // DEAL block size is 16 bytes (128 bits)
        CipherContext ctx = new CipherContext(
            deal,
            CipherMode.ECB,
            PaddingMode.PKCS7,
            16  // DEAL block size
        );
        
        // Encrypt
        byte[] ciphertext = ctx.encryptAsync(plaintext).join();
        System.out.println("Encrypted (" + ciphertext.length + " bytes):");
        System.out.println(bytesToHex(ciphertext));
        System.out.println();
        
        // Decrypt
        byte[] decrypted = ctx.decryptAsync(ciphertext).join();
        String decryptedMessage = new String(decrypted, StandardCharsets.UTF_8);
        
        System.out.println("Decrypted message: \"" + decryptedMessage + "\"");
        System.out.println("Decryption successful: " + message.equals(decryptedMessage));
        
        ctx.shutdown();
    }
    
    /**
     * Compares DEAL (128-bit blocks) with DES (64-bit blocks).
     */
    private static void demoComparisonWithDES() {
        System.out.println("4. COMPARISON: DEAL vs DES");
        System.out.println("-".repeat(80));
        
        System.out.println("Feature Comparison:");
        System.out.println("  DES:  Block size = 64 bits (8 bytes),  Key size = 64 bits (8 bytes)");
        System.out.println("  DEAL: Block size = 128 bits (16 bytes), Key size = 192 bits (24 bytes)");
        System.out.println();
        
        // DES encryption
        byte[] desKey = "DES_KEY!".getBytes(StandardCharsets.UTF_8);
        byte[] desPlaintext = "DES_BLOK".getBytes(StandardCharsets.UTF_8);
        
        DES des = new DES();
        des.setEncryptionKey(desKey);
        byte[] desCiphertext = des.encrypt(desPlaintext);
        
        System.out.println("DES Encryption:");
        System.out.println("  Plaintext:  " + bytesToHex(desPlaintext));
        System.out.println("  Ciphertext: " + bytesToHex(desCiphertext));
        System.out.println();
        
        // DEAL encryption
        byte[] dealKey = "DEAL_SECRET_KEY_192BITS!".getBytes(StandardCharsets.UTF_8);
        byte[] dealPlaintext = "DEAL Block 16B!!".getBytes(StandardCharsets.UTF_8);
        
        DEAL deal = new DEAL();
        deal.setEncryptionKey(dealKey);
        byte[] dealCiphertext = deal.encrypt(dealPlaintext);
        
        System.out.println("DEAL Encryption:");
        System.out.println("  Plaintext:  " + bytesToHex(dealPlaintext));
        System.out.println("  Ciphertext: " + bytesToHex(dealCiphertext));
        System.out.println();
        
        System.out.println("DEAL provides:");
        System.out.println("  ✓ Larger block size (128 bits) - better security for modern applications");
        System.out.println("  ✓ Uses proven DES algorithm as a building block");
        System.out.println("  ✓ Adapter pattern allows seamless integration into Feistel framework");
        System.out.println("  ✓ Compatible with standard cipher modes (ECB, CBC, CTR, etc.)");
    }
    
    /**
     * Converts a byte array to a hexadecimal string.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}

