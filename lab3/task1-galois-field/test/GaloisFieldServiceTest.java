public class GaloisFieldServiceTest {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Running Galois Field Service Tests ===\n");
        
        testAddition();
        testMultiplication();
        testInverse();
        testIrreducibility();
        testAllIrreduciblePolynomials();
        testFactorization();
        testAESModulus();
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Total: " + (testsPassed + testsFailed));
        
        if (testsFailed == 0) {
            System.out.println("\nAll tests passed!");
        }
    }
    
    private static void testAddition() {
        System.out.println("Testing Addition...");
        
        assertEqual(GaloisFieldService.add((byte) 0x00, (byte) 0x00), (byte) 0x00, "0 + 0 = 0");
        assertEqual(GaloisFieldService.add((byte) 0x00, (byte) 0xFF), (byte) 0xFF, "0 + FF = FF");
        assertEqual(GaloisFieldService.add((byte) 0xFF, (byte) 0xFF), (byte) 0x00, "FF + FF = 0");
        assertEqual(GaloisFieldService.add((byte) 0x57, (byte) 0x83), (byte) 0xD4, "57 + 83 = D4");
        assertEqual(GaloisFieldService.add((byte) 0xAA, (byte) 0x55), (byte) 0xFF, "AA + 55 = FF");
        
        byte a = (byte) 0x12;
        byte b = (byte) 0x34;
        assertEqual(GaloisFieldService.add(GaloisFieldService.add(a, b), b), a, "Addition is self-inverse");
        
        System.out.println();
    }
    
    private static void testMultiplication() {
        System.out.println("Testing Multiplication...");
        
        byte modulus = (byte) 0x11B;
        
        assertEqual(GaloisFieldService.multiply((byte) 0x00, (byte) 0x57, modulus), (byte) 0x00, "0 * 57 = 0");
        assertEqual(GaloisFieldService.multiply((byte) 0x01, (byte) 0x57, modulus), (byte) 0x57, "1 * 57 = 57");
        assertEqual(GaloisFieldService.multiply((byte) 0x57, (byte) 0x01, modulus), (byte) 0x57, "57 * 1 = 57");
        assertEqual(GaloisFieldService.multiply((byte) 0x02, (byte) 0x02, modulus), (byte) 0x04, "2 * 2 = 4");
        assertEqual(GaloisFieldService.multiply((byte) 0x10, (byte) 0x10, modulus), (byte) 0x1B, "10 * 10 = 1B");
        
        byte a = (byte) 0x53;
        byte b = (byte) 0xCA;
        byte ab = GaloisFieldService.multiply(a, b, modulus);
        byte ba = GaloisFieldService.multiply(b, a, modulus);
        assertEqual(ab, ba, "Multiplication is commutative");
        
        System.out.println();
    }
    
    private static void testInverse() {
        System.out.println("Testing Multiplicative Inverse...");
        
        byte modulus = (byte) 0x11B;
        
        byte[] testValues = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x53, (byte) 0xCA, (byte) 0xFF};
        
        for (byte val : testValues) {
            byte inv = GaloisFieldService.inverse(val, modulus);
            byte result = GaloisFieldService.multiply(val, inv, modulus);
            assertEqual(result, (byte) 0x01, 
                String.format("0x%02X * inverse = 1", val & 0xFF));
        }
        
        System.out.println();
    }
    
    private static void testIrreducibility() {
        System.out.println("Testing Irreducibility Check...");
        
        assertTrue(GaloisFieldService.isIrreducible((byte) 0x11B), "0x11B is irreducible");
        assertTrue(GaloisFieldService.isIrreducible((byte) 0x11D), "0x11D is irreducible");
        assertTrue(GaloisFieldService.isIrreducible((byte) 0x12D), "0x12D is irreducible");
        assertTrue(GaloisFieldService.isIrreducible((byte) 0x14D), "0x14D is irreducible");
        assertTrue(GaloisFieldService.isIrreducible((byte) 0x15F), "0x15F is irreducible");
        
        assertFalse(GaloisFieldService.isIrreducible((byte) 0x120), "0x120 is reducible");
        assertFalse(GaloisFieldService.isIrreducible((byte) 0x11F), "0x11F is reducible");
        
        System.out.println();
    }
    
    private static void testAllIrreduciblePolynomials() {
        System.out.println("Testing All Irreducible Polynomials...");
        
        byte[] irreducibles = GaloisFieldService.getAllIrreduciblePolynomials();
        
        assertEqual(irreducibles.length, 30, "Exactly 30 irreducible polynomials of degree 8");
        
        for (byte poly : irreducibles) {
            assertTrue(GaloisFieldService.isIrreducible(poly), 
                String.format("0x%02X is marked as irreducible", poly & 0xFF));
        }
        
        assertTrue(containsPolynomial(irreducibles, (byte) 0x11B), "Contains AES polynomial 0x11B");
        assertTrue(containsPolynomial(irreducibles, (byte) 0x11D), "Contains 0x11D");
        
        System.out.println();
    }
    
    private static void testFactorization() {
        System.out.println("Testing Polynomial Factorization...");
        
        int poly1 = 0x11F;
        int[] factors1 = GaloisFieldService.factorize(poly1);
        assertTrue(factors1.length > 0, "0x11F has factors");
        
        int poly2 = 0x11B;
        int[] factors2 = GaloisFieldService.factorize(poly2);
        assertTrue(factors2.length >= 1, "0x11B has at least one factor (itself if irreducible)");
        
        System.out.println();
    }
    
    private static void testAESModulus() {
        System.out.println("Testing AES Modulus Operations...");
        
        byte aesModulus = (byte) 0x11B;
        
        assertTrue(GaloisFieldService.isIrreducible(aesModulus), "AES modulus is irreducible");
        
        byte a = (byte) 0x57;
        byte b = (byte) 0x83;
        byte product = GaloisFieldService.multiply(a, b, aesModulus);
        assertTrue(product != 0, "Product is non-zero");
        
        byte inv_a = GaloisFieldService.inverse(a, aesModulus);
        byte check = GaloisFieldService.multiply(a, inv_a, aesModulus);
        assertEqual(check, (byte) 0x01, "a * a^(-1) = 1 for AES modulus");
        
        System.out.println();
    }
    
    private static boolean containsPolynomial(byte[] array, byte value) {
        for (byte b : array) {
            if (b == value) {
                return true;
            }
        }
        return false;
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
    
    private static void assertFalse(boolean condition, String testName) {
        assertTrue(!condition, testName);
    }
    
    private static void assertEqual(byte actual, byte expected, String testName) {
        if (actual == expected) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.out.println("  [FAIL] " + testName + 
                " - Expected: 0x" + String.format("%02X", expected & 0xFF) + 
                ", Actual: 0x" + String.format("%02X", actual & 0xFF));
            testsFailed++;
        }
    }
    
    private static void assertEqual(int actual, int expected, String testName) {
        if (actual == expected) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.out.println("  [FAIL] " + testName + " - Expected: " + expected + ", Actual: " + actual);
            testsFailed++;
        }
    }
}

