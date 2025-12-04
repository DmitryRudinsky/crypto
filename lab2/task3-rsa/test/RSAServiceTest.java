import java.math.BigInteger;
import java.util.Arrays;

public class RSAServiceTest {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Running RSA Service Tests ===\n");
        
        testBasicEncryptionDecryption();
        testBigIntegerMessages();
        testByteArrayMessages();
        testKeyRegeneration();
        testDifferentPrimalityTests();
        testDifferentKeySizes();
        testPublicPrivateKeyUsage();
        testEncryptionWithDifferentKeys();
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Total: " + (testsPassed + testsFailed));
        
        if (testsFailed == 0) {
            System.out.println("\nAll tests passed!");
        }
    }
    
    private static void testBasicEncryptionDecryption() {
        System.out.println("Testing Basic Encryption/Decryption...");
        
        RSAService rsa = new RSAService(RSAService.PrimalityTestType.MILLER_RABIN, 0.99, 256);
        BigInteger message = new BigInteger("42");
        
        BigInteger encrypted = rsa.encrypt(message);
        BigInteger decrypted = rsa.decrypt(encrypted);
        
        assertTrue(!encrypted.equals(message), "Encrypted != original");
        assertEqual(decrypted, message, "Decrypted == original");
        
        System.out.println();
    }
    
    private static void testBigIntegerMessages() {
        System.out.println("Testing BigInteger Messages...");
        
        RSAService rsa = new RSAService(RSAService.PrimalityTestType.MILLER_RABIN, 0.99, 512);
        
        BigInteger[] messages = {
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.TWO,
            new BigInteger("100"),
            new BigInteger("12345"),
            new BigInteger("999999"),
            new BigInteger("123456789012345")
        };
        
        for (BigInteger message : messages) {
            BigInteger encrypted = rsa.encrypt(message);
            BigInteger decrypted = rsa.decrypt(encrypted);
            assertEqual(decrypted, message, "Message " + message + " encrypted/decrypted correctly");
        }
        
        System.out.println();
    }
    
    private static void testByteArrayMessages() {
        System.out.println("Testing Byte Array Messages...");
        
        RSAService rsa = new RSAService(RSAService.PrimalityTestType.MILLER_RABIN, 0.99, 512);
        
        String[] textMessages = {
            "Hello",
            "RSA",
            "Test123",
            "Cryptography"
        };
        
        for (String text : textMessages) {
            byte[] original = text.getBytes();
            byte[] encrypted = rsa.encrypt(original);
            byte[] decrypted = rsa.decrypt(encrypted);
            
            assertTrue(Arrays.equals(original, decrypted), 
                "Text '" + text + "' encrypted/decrypted correctly");
        }
        
        System.out.println();
    }
    
    private static void testKeyRegeneration() {
        System.out.println("Testing Key Regeneration...");
        
        RSAService rsa = new RSAService(RSAService.PrimalityTestType.MILLER_RABIN, 0.99, 256);
        
        RSAService.PublicKey key1 = rsa.getPublicKey();
        rsa.regenerateKeyPair();
        RSAService.PublicKey key2 = rsa.getPublicKey();
        
        assertTrue(!key1.n.equals(key2.n), "Keys are different after regeneration");
        
        BigInteger message = new BigInteger("12345");
        BigInteger encrypted = rsa.encrypt(message);
        BigInteger decrypted = rsa.decrypt(encrypted);
        
        assertEqual(decrypted, message, "New key works correctly");
        
        rsa.regenerateKeyPair();
        RSAService.PublicKey key3 = rsa.getPublicKey();
        
        assertTrue(!key2.n.equals(key3.n), "Keys are different after second regeneration");
        
        System.out.println();
    }
    
    private static void testDifferentPrimalityTests() {
        System.out.println("Testing Different Primality Tests...");
        
        RSAService.PrimalityTestType[] testTypes = {
            RSAService.PrimalityTestType.FERMAT,
            RSAService.PrimalityTestType.SOLOVAY_STRASSEN,
            RSAService.PrimalityTestType.MILLER_RABIN
        };
        
        BigInteger message = new BigInteger("777");
        
        for (RSAService.PrimalityTestType testType : testTypes) {
            RSAService rsa = new RSAService(testType, 0.99, 256);
            BigInteger encrypted = rsa.encrypt(message);
            BigInteger decrypted = rsa.decrypt(encrypted);
            
            assertEqual(decrypted, message, testType + " test works correctly");
        }
        
        System.out.println();
    }
    
    private static void testDifferentKeySizes() {
        System.out.println("Testing Different Key Sizes...");
        
        int[] keySizes = {128, 256, 512};
        BigInteger message = new BigInteger("12345");
        
        for (int keySize : keySizes) {
            RSAService rsa = new RSAService(RSAService.PrimalityTestType.MILLER_RABIN, 0.99, keySize);
            
            RSAService.PublicKey publicKey = rsa.getPublicKey();
            int actualBitLength = publicKey.n.bitLength();
            
            assertTrue(actualBitLength >= keySize * 2 - 10 && actualBitLength <= keySize * 2 + 10,
                "Key size " + keySize + " generates appropriate n (got " + actualBitLength + " bits)");
            
            BigInteger encrypted = rsa.encrypt(message);
            BigInteger decrypted = rsa.decrypt(encrypted);
            
            assertEqual(decrypted, message, "Key size " + keySize + " encryption/decryption works");
        }
        
        System.out.println();
    }
    
    private static void testPublicPrivateKeyUsage() {
        System.out.println("Testing Public/Private Key Usage...");
        
        RSAService rsa = new RSAService(RSAService.PrimalityTestType.MILLER_RABIN, 0.99, 256);
        
        RSAService.PublicKey publicKey = rsa.getPublicKey();
        RSAService.PrivateKey privateKey = rsa.getPrivateKey();
        
        BigInteger message = new BigInteger("999");
        
        BigInteger encrypted = rsa.encrypt(message, publicKey);
        BigInteger decrypted = rsa.decrypt(encrypted, privateKey);
        
        assertEqual(decrypted, message, "Explicit public/private key usage works");
        
        System.out.println();
    }
    
    private static void testEncryptionWithDifferentKeys() {
        System.out.println("Testing Encryption with Different Keys...");
        
        RSAService rsa1 = new RSAService(RSAService.PrimalityTestType.MILLER_RABIN, 0.99, 256);
        RSAService rsa2 = new RSAService(RSAService.PrimalityTestType.MILLER_RABIN, 0.99, 256);
        
        BigInteger message = new BigInteger("12345");
        
        BigInteger encrypted1 = rsa1.encrypt(message);
        BigInteger decrypted1 = rsa1.decrypt(encrypted1);
        assertEqual(decrypted1, message, "Service 1 encrypts/decrypts correctly");
        
        BigInteger encrypted2 = rsa2.encrypt(message);
        BigInteger decrypted2 = rsa2.decrypt(encrypted2);
        assertEqual(decrypted2, message, "Service 2 encrypts/decrypts correctly");
        
        assertTrue(!encrypted1.equals(encrypted2), "Different keys produce different ciphertexts");
        
        RSAService.PublicKey pub1 = rsa1.getPublicKey();
        RSAService.PrivateKey priv2 = rsa2.getPrivateKey();
        
        BigInteger crossEncrypted = rsa1.encrypt(message, pub1);
        BigInteger crossDecrypted = rsa1.decrypt(crossEncrypted, rsa1.getPrivateKey());
        assertEqual(crossDecrypted, message, "Cross-service encryption works with matching keys");
        
        System.out.println();
    }
    
    private static void assertTrue(boolean condition, String testName) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.out.println("  [FAIL] " + testName);
            testsFailed++;
        }
    }
    
    private static void assertEqual(BigInteger actual, BigInteger expected, String testName) {
        if (actual.equals(expected)) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.out.println("  [FAIL] " + testName + " - Expected: " + expected + ", Actual: " + actual);
            testsFailed++;
        }
    }
}

