import java.math.BigInteger;

public class WienerAttackTest {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Running Wiener Attack Tests ===\n");
        
        testSmallVulnerableKey();
        testMultipleVulnerableKeys();
        testConvergentsComputation();
        testAttackResultStructure();
        testDecryptionVerification();
        testKnownVulnerableKey();
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Total: " + (testsPassed + testsFailed));
        
        if (testsFailed == 0) {
            System.out.println("\nAll tests passed!");
        }
    }
    
    private static void testSmallVulnerableKey() {
        System.out.println("Testing Small Vulnerable Key...");
        
        WienerAttackService attackService = new WienerAttackService();
        VulnerableRSAGenerator generator = new VulnerableRSAGenerator();
        
        VulnerableRSAGenerator.KeyPair keyPair = generator.generateVulnerableKeyPair(128);
        WienerAttackService.AttackResult result = attackService.attack(keyPair.e, keyPair.n);
        
        assertTrue(result.success, "Attack succeeds on vulnerable key");
        assertEqual(result.d, keyPair.d, "Found d matches actual d");
        assertEqual(result.phi, keyPair.phi, "Found phi matches actual phi");
        
        System.out.println();
    }
    
    private static void testMultipleVulnerableKeys() {
        System.out.println("Testing Multiple Vulnerable Keys...");
        
        WienerAttackService attackService = new WienerAttackService();
        VulnerableRSAGenerator generator = new VulnerableRSAGenerator();
        
        int[] bitLengths = {128, 256};
        
        for (int bitLength : bitLengths) {
            VulnerableRSAGenerator.KeyPair keyPair = generator.generateVulnerableKeyPair(bitLength);
            WienerAttackService.AttackResult result = attackService.attack(keyPair.e, keyPair.n);
            
            assertTrue(result.success, "Attack succeeds on " + bitLength + "-bit key");
            assertEqual(result.d, keyPair.d, bitLength + "-bit: Found d matches");
        }
        
        System.out.println();
    }
    
    private static void testConvergentsComputation() {
        System.out.println("Testing Convergents Computation...");
        
        WienerAttackService attackService = new WienerAttackService();
        VulnerableRSAGenerator generator = new VulnerableRSAGenerator();
        
        VulnerableRSAGenerator.KeyPair keyPair = generator.generateVulnerableKeyPair(256);
        WienerAttackService.AttackResult result = attackService.attack(keyPair.e, keyPair.n);
        
        assertTrue(result.convergents != null, "Convergents list is not null");
        assertTrue(result.convergents.size() > 0, "Convergents list is not empty");
        
        for (WienerAttackService.Fraction convergent : result.convergents) {
            assertTrue(convergent.numerator != null, "Convergent numerator is not null");
            assertTrue(convergent.denominator != null, "Convergent denominator is not null");
            assertTrue(convergent.denominator.compareTo(BigInteger.ZERO) > 0, 
                "Convergent denominator is positive");
        }
        
        System.out.println();
    }
    
    private static void testAttackResultStructure() {
        System.out.println("Testing Attack Result Structure...");
        
        WienerAttackService attackService = new WienerAttackService();
        VulnerableRSAGenerator generator = new VulnerableRSAGenerator();
        
        VulnerableRSAGenerator.KeyPair keyPair = generator.generateVulnerableKeyPair(128);
        WienerAttackService.AttackResult result = attackService.attack(keyPair.e, keyPair.n);
        
        if (result.success) {
            assertTrue(result.d != null, "d is not null on success");
            assertTrue(result.phi != null, "phi is not null on success");
            assertTrue(result.d.compareTo(BigInteger.ZERO) > 0, "d is positive");
            assertTrue(result.phi.compareTo(BigInteger.ZERO) > 0, "phi is positive");
            assertTrue(result.convergents != null, "convergents is not null");
            assertTrue(result.convergents.size() > 0, "convergents is not empty");
        }
        
        System.out.println();
    }
    
    private static void testDecryptionVerification() {
        System.out.println("Testing Decryption Verification...");
        
        WienerAttackService attackService = new WienerAttackService();
        VulnerableRSAGenerator generator = new VulnerableRSAGenerator();
        
        VulnerableRSAGenerator.KeyPair keyPair = generator.generateVulnerableKeyPair(256);
        WienerAttackService.AttackResult result = attackService.attack(keyPair.e, keyPair.n);
        
        if (result.success) {
            BigInteger[] messages = {
                new BigInteger("42"),
                new BigInteger("12345"),
                new BigInteger("999")
            };
            
            for (BigInteger message : messages) {
                if (message.compareTo(keyPair.n) >= 0) {
                    continue;
                }
                
                BigInteger encrypted = NumberTheoryService.modPow(message, keyPair.e, keyPair.n);
                BigInteger decrypted = NumberTheoryService.modPow(encrypted, result.d, keyPair.n);
                
                assertEqual(decrypted, message, "Message " + message + " decrypts correctly");
            }
        }
        
        System.out.println();
    }
    
    private static void testKnownVulnerableKey() {
        System.out.println("Testing Known Vulnerable Key...");
        
        WienerAttackService attackService = new WienerAttackService();
        
        BigInteger n = new BigInteger("90581");
        BigInteger e = new BigInteger("17993");
        
        WienerAttackService.AttackResult result = attackService.attack(e, n);
        
        assertTrue(result.success, "Attack succeeds on known vulnerable key");
        
        if (result.success) {
            assertTrue(result.d != null, "d found");
            assertTrue(result.phi != null, "phi found");
            
            BigInteger edMod = e.multiply(result.d).mod(result.phi);
            assertEqual(edMod, BigInteger.ONE, "e*d ≡ 1 (mod phi)");
            
            BigInteger message = new BigInteger("42");
            BigInteger encrypted = NumberTheoryService.modPow(message, e, n);
            BigInteger decrypted = NumberTheoryService.modPow(encrypted, result.d, n);
            assertEqual(decrypted, message, "Known key decryption works");
        }
        
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
        if (actual != null && actual.equals(expected)) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.out.println("  [FAIL] " + testName + " - Expected: " + expected + ", Actual: " + actual);
            testsFailed++;
        }
    }
}

