import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Test suite for the DEAL cipher implementation.
 * 
 * Tests cover:
 * - Basic encryption/decryption
 * - Key validation
 * - Block size validation
 * - Round variations
 * - Integration with CipherContext
 * - Adapter functionality
 */
public class DEALTest {
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("DEAL Cipher Test Suite");
        System.out.println("=".repeat(80));
        System.out.println();
        
        // Run all tests
        testBasicEncryptionDecryption();
        testInvalidKeySize();
        testInvalidBlockSize();
        testDifferentRounds();
        testEncryptionUniqueness();
        testDecryptionWithDifferentKey();
        testDESAdapterDirectly();
        testDEALKeySchedule();
        testCipherContextIntegration();
        testMultipleBlocks();
        
        // Print summary
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("Test Results:");
        System.out.println("  Passed: " + testsPassed);
        System.out.println("  Failed: " + testsFailed);
        System.out.println("  Total:  " + (testsPassed + testsFailed));
        System.out.println("=".repeat(80));
        
        if (testsFailed == 0) {
            System.out.println("✓ All tests passed!");
            System.exit(0);
        } else {
            System.out.println("✗ Some tests failed!");
            System.exit(1);
        }
    }
    
    private static void testBasicEncryptionDecryption() {
        System.out.println("Test: Basic Encryption/Decryption");
        System.out.println("-".repeat(80));
        
        try {
            byte[] key = new byte[24];
            for (int i = 0; i < 24; i++) {
                key[i] = (byte) i;
            }
            
            byte[] plaintext = "Hello DEAL Test!".getBytes(StandardCharsets.UTF_8);
            
            DEAL deal = new DEAL();
            deal.setEncryptionKey(key);
            deal.setDecryptionKey(key);
            
            byte[] ciphertext = deal.encrypt(plaintext);
            byte[] decrypted = deal.decrypt(ciphertext);
            
            // Verify ciphertext is different from plaintext
            boolean ciphertextDifferent = !Arrays.equals(plaintext, ciphertext);
            // Verify decryption recovers original plaintext
            boolean decryptionCorrect = Arrays.equals(plaintext, decrypted);
            
            if (ciphertextDifferent && decryptionCorrect) {
                System.out.println("✓ PASSED");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED: Encryption/decryption not working correctly");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    private static void testInvalidKeySize() {
        System.out.println("Test: Invalid Key Size");
        System.out.println("-".repeat(80));
        
        try {
            byte[] shortKey = new byte[16]; // Too short
            DEAL deal = new DEAL();
            
            boolean exceptionThrown = false;
            try {
                deal.setEncryptionKey(shortKey);
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
            }
            
            if (exceptionThrown) {
                System.out.println("✓ PASSED: Correctly rejected short key");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED: Should have rejected short key");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    private static void testInvalidBlockSize() {
        System.out.println("Test: Invalid Block Size");
        System.out.println("-".repeat(80));
        
        try {
            byte[] key = new byte[24];
            byte[] shortBlock = new byte[8]; // Should be 16 bytes
            
            DEAL deal = new DEAL();
            deal.setEncryptionKey(key);
            
            boolean exceptionThrown = false;
            try {
                deal.encrypt(shortBlock);
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
            }
            
            if (exceptionThrown) {
                System.out.println("✓ PASSED: Correctly rejected wrong block size");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED: Should have rejected wrong block size");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    private static void testDifferentRounds() {
        System.out.println("Test: Different Number of Rounds");
        System.out.println("-".repeat(80));
        
        try {
            byte[] key = new byte[24];
            Arrays.fill(key, (byte) 0x42);
            byte[] plaintext = "DEAL Round Test!".getBytes(StandardCharsets.UTF_8);
            
            boolean allPassed = true;
            int[] rounds = {3, 6, 9, 12};
            
            for (int numRounds : rounds) {
                DEAL deal = new DEAL(numRounds);
                deal.setEncryptionKey(key);
                deal.setDecryptionKey(key);
                
                byte[] ciphertext = deal.encrypt(plaintext);
                byte[] decrypted = deal.decrypt(ciphertext);
                
                if (!Arrays.equals(plaintext, decrypted)) {
                    System.out.println("  ✗ Failed for " + numRounds + " rounds");
                    allPassed = false;
                }
            }
            
            if (allPassed) {
                System.out.println("✓ PASSED: All round configurations work correctly");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED: Some round configurations failed");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    private static void testEncryptionUniqueness() {
        System.out.println("Test: Encryption Produces Different Outputs for Different Inputs");
        System.out.println("-".repeat(80));
        
        try {
            byte[] key = new byte[24];
            Arrays.fill(key, (byte) 0x55);
            
            byte[] plaintext1 = "Block Number 001".getBytes(StandardCharsets.UTF_8);
            byte[] plaintext2 = "Block Number 002".getBytes(StandardCharsets.UTF_8);
            
            DEAL deal = new DEAL();
            deal.setEncryptionKey(key);
            
            byte[] ciphertext1 = deal.encrypt(plaintext1);
            byte[] ciphertext2 = deal.encrypt(plaintext2);
            
            boolean different = !Arrays.equals(ciphertext1, ciphertext2);
            
            if (different) {
                System.out.println("✓ PASSED: Different plaintexts produce different ciphertexts");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED: Different plaintexts produced same ciphertext");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    private static void testDecryptionWithDifferentKey() {
        System.out.println("Test: Decryption with Different Key Produces Different Result");
        System.out.println("-".repeat(80));
        
        try {
            byte[] key1 = new byte[24];
            byte[] key2 = new byte[24];
            Arrays.fill(key1, (byte) 0xAA);
            Arrays.fill(key2, (byte) 0xBB);
            
            byte[] plaintext = "Secret Message!!".getBytes(StandardCharsets.UTF_8);
            
            DEAL deal1 = new DEAL();
            deal1.setEncryptionKey(key1);
            byte[] ciphertext = deal1.encrypt(plaintext);
            
            DEAL deal2 = new DEAL();
            deal2.setDecryptionKey(key2);
            byte[] decrypted = deal2.decrypt(ciphertext);
            
            boolean different = !Arrays.equals(plaintext, decrypted);
            
            if (different) {
                System.out.println("✓ PASSED: Wrong key produces different plaintext");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED: Wrong key somehow recovered correct plaintext");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    private static void testDESAdapterDirectly() {
        System.out.println("Test: DES Adapter Functionality");
        System.out.println("-".repeat(80));
        
        try {
            DESAdapter adapter = new DESAdapter();
            
            byte[] block = "DES_BLOK".getBytes(StandardCharsets.UTF_8);
            byte[] key = "DESK_KEY".getBytes(StandardCharsets.UTF_8);
            
            byte[] encrypted = adapter.encryptBlock(block, key);
            byte[] decrypted = adapter.decryptBlock(encrypted, key);
            
            boolean works = Arrays.equals(block, decrypted);
            
            if (works) {
                System.out.println("✓ PASSED: DES adapter works correctly");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED: DES adapter encryption/decryption failed");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    private static void testDEALKeySchedule() {
        System.out.println("Test: DEAL Key Schedule");
        System.out.println("-".repeat(80));
        
        try {
            byte[] key = new byte[24];
            for (int i = 0; i < 24; i++) {
                key[i] = (byte) i;
            }
            
            DEALKeySchedule schedule = new DEALKeySchedule(6);
            byte[][] roundKeys = schedule.expandKey(key);
            
            boolean correctCount = (roundKeys.length == 6);
            boolean correctSize = true;
            for (byte[] roundKey : roundKeys) {
                if (roundKey.length != 8) {
                    correctSize = false;
                    break;
                }
            }
            
            // Verify keys cycle through K1, K2, K3
            boolean correctPattern = true;
            byte[] k1 = Arrays.copyOfRange(key, 0, 8);
            byte[] k2 = Arrays.copyOfRange(key, 8, 16);
            byte[] k3 = Arrays.copyOfRange(key, 16, 24);
            
            if (!Arrays.equals(roundKeys[0], k1) || 
                !Arrays.equals(roundKeys[1], k2) ||
                !Arrays.equals(roundKeys[2], k3) ||
                !Arrays.equals(roundKeys[3], k1) ||
                !Arrays.equals(roundKeys[4], k2) ||
                !Arrays.equals(roundKeys[5], k3)) {
                correctPattern = false;
            }
            
            if (correctCount && correctSize && correctPattern) {
                System.out.println("✓ PASSED: Key schedule generates correct round keys");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED: Key schedule issues detected");
                if (!correctCount) System.out.println("  - Wrong number of round keys");
                if (!correctSize) System.out.println("  - Wrong size of round keys");
                if (!correctPattern) System.out.println("  - Wrong key pattern");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
    
    private static void testCipherContextIntegration() {
        System.out.println("Test: Integration with CipherContext");
        System.out.println("-".repeat(80));
        
        CipherContext ctx = null;
        try {
            byte[] key = "DEAL_KEY_192_BITS_LONG!!".getBytes(StandardCharsets.UTF_8);
            String message = "This is a longer message that spans multiple DEAL blocks!";
            byte[] plaintext = message.getBytes(StandardCharsets.UTF_8);
            
            DEAL deal = new DEAL();
            deal.setEncryptionKey(key);
            deal.setDecryptionKey(key);
            
            ctx = new CipherContext(deal, CipherMode.ECB, PaddingMode.PKCS7, 16);
            
            byte[] ciphertext = ctx.encryptAsync(plaintext).join();
            byte[] decrypted = ctx.decryptAsync(ciphertext).join();
            
            String decryptedMessage = new String(decrypted, StandardCharsets.UTF_8);
            boolean matches = message.equals(decryptedMessage);
            
            if (matches) {
                System.out.println("✓ PASSED: CipherContext integration works correctly");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED: CipherContext integration failed");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            e.printStackTrace();
            testsFailed++;
        } finally {
            if (ctx != null) {
                ctx.shutdown();
            }
        }
        System.out.println();
    }
    
    private static void testMultipleBlocks() {
        System.out.println("Test: Multiple Sequential Blocks");
        System.out.println("-".repeat(80));
        
        try {
            byte[] key = new byte[24];
            Arrays.fill(key, (byte) 0x33);
            
            DEAL deal = new DEAL();
            deal.setEncryptionKey(key);
            deal.setDecryptionKey(key);
            
            boolean allPassed = true;
            for (int i = 0; i < 5; i++) {
                byte[] plaintext = new byte[16];
                String msg = String.format("Block %d Test!!", i);
                byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
                System.arraycopy(msgBytes, 0, plaintext, 0, Math.min(msgBytes.length, 16));
                
                byte[] ciphertext = deal.encrypt(plaintext);
                byte[] decrypted = deal.decrypt(ciphertext);
                
                if (!Arrays.equals(plaintext, decrypted)) {
                    allPassed = false;
                    break;
                }
            }
            
            if (allPassed) {
                System.out.println("✓ PASSED: Multiple blocks encrypted/decrypted correctly");
                testsPassed++;
            } else {
                System.out.println("✗ FAILED: Some blocks failed to encrypt/decrypt");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
}

