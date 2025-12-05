import java.util.List;

public class GF256ServiceTest {
    
    private static int passed = 0;
    private static int failed = 0;
    
    public static void main(String[] args) {
        System.out.println("Running GF256Service tests...\n");
        
        testAddition();
        testMultiplication();
        testInverse();
        testIrreducibility();
        testGetAllIrreducibles();
        testFactorization();
        testExceptions();
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        
        if (failed == 0) {
            System.out.println("\nAll tests passed!");
        }
    }
    
    private static void testAddition() {
        System.out.println("Testing addition...");
        
        assertEqual(GF256Service.add((byte) 0x53, (byte) 0xCA), (byte) 0x99, "0x53 + 0xCA");
        assertEqual(GF256Service.add((byte) 0xFF, (byte) 0xFF), (byte) 0x00, "0xFF + 0xFF");
        assertEqual(GF256Service.add((byte) 0x00, (byte) 0xAB), (byte) 0xAB, "0x00 + 0xAB");
        assertEqual(GF256Service.add((byte) 0x57, (byte) 0x83), (byte) 0xD4, "0x57 + 0x83");
    }
    
    private static void testMultiplication() {
        System.out.println("\nTesting multiplication...");
        
        byte mod = (byte) 0x1B;
        
        assertEqual(GF256Service.multiplyMod((byte) 0x53, (byte) 0xCA, mod), (byte) 0x01, "0x53 * 0xCA mod 0x11B");
        assertEqual(GF256Service.multiplyMod((byte) 0x02, (byte) 0x02, mod), (byte) 0x04, "0x02 * 0x02 mod 0x11B");
        assertEqual(GF256Service.multiplyMod((byte) 0x01, (byte) 0xFF, mod), (byte) 0xFF, "0x01 * 0xFF mod 0x11B");
        assertEqual(GF256Service.multiplyMod((byte) 0x00, (byte) 0xAB, mod), (byte) 0x00, "0x00 * 0xAB mod 0x11B");
    }
    
    private static void testInverse() {
        System.out.println("\nTesting inverse...");
        
        byte mod = (byte) 0x1B;
        
        byte inv53 = GF256Service.inverse((byte) 0x53, mod);
        byte check1 = GF256Service.multiplyMod((byte) 0x53, inv53, mod);
        assertEqual(check1, (byte) 0x01, "inverse of 0x53 verification");
        
        byte invCA = GF256Service.inverse((byte) 0xCA, mod);
        byte check2 = GF256Service.multiplyMod((byte) 0xCA, invCA, mod);
        assertEqual(check2, (byte) 0x01, "inverse of 0xCA verification");
        
        byte inv02 = GF256Service.inverse((byte) 0x02, mod);
        byte check3 = GF256Service.multiplyMod((byte) 0x02, inv02, mod);
        assertEqual(check3, (byte) 0x01, "inverse of 0x02 verification");
    }
    
    private static void testIrreducibility() {
        System.out.println("\nTesting irreducibility check...");
        
        assertTrue(GF256Service.isIrreducible(0x11B), "0x11B is irreducible");
        assertTrue(GF256Service.isIrreducible(0x11D), "0x11D is irreducible");
        assertTrue(GF256Service.isIrreducible(0x12B), "0x12B is irreducible");
        assertTrue(GF256Service.isIrreducible(0x12D), "0x12D is irreducible");
        
        assertFalse(GF256Service.isIrreducible(0x11A), "0x11A is reducible");
        assertFalse(GF256Service.isIrreducible(0x100), "0x100 is reducible");
        assertFalse(GF256Service.isIrreducible(0x11C), "0x11C is reducible");
    }
    
    private static void testGetAllIrreducibles() {
        System.out.println("\nTesting getting all irreducible polynomials...");
        
        List<Byte> irreducibles = GF256Service.getAllIrreduciblePolynomials();
        
        assertEqual(irreducibles.size(), 30, "count of irreducible polynomials");
        
        for (byte poly : irreducibles) {
            int fullPoly = (poly & 0xFF) | 0x100;
            assertTrue(GF256Service.isIrreducible(fullPoly), 
                String.format("0x%02X in list should be irreducible", poly & 0xFF));
        }
        
        assertTrue(irreducibles.contains((byte) 0x1B), "list contains 0x1B (0x11B)");
        assertTrue(irreducibles.contains((byte) 0x1D), "list contains 0x1D (0x11D)");
    }
    
    private static void testFactorization() {
        System.out.println("\nTesting factorization...");
        
        List<Byte> factors1 = GF256Service.factorize(0x11B);
        assertEqual(factors1.size(), 1, "0x11B should have 1 factor (itself)");
        
        List<Byte> factors2 = GF256Service.factorize(0);
        assertEqual(factors2.size(), 0, "0 should have 0 factors");
        
        List<Byte> factors3 = GF256Service.factorize(1);
        assertEqual(factors3.size(), 0, "1 should have 0 factors");
    }
    
    private static void testExceptions() {
        System.out.println("\nTesting exceptions...");
        
        byte reducible = (byte) 0x1A;
        
        try {
            GF256Service.multiplyMod((byte) 0x53, (byte) 0xCA, reducible);
            fail("multiply with reducible modulus should throw");
        } catch (IllegalArgumentException e) {
            pass("multiply with reducible modulus throws exception");
        }
        
        try {
            GF256Service.inverse((byte) 0x53, reducible);
            fail("inverse with reducible modulus should throw");
        } catch (IllegalArgumentException e) {
            pass("inverse with reducible modulus throws exception");
        }
        
        try {
            GF256Service.inverse((byte) 0x00, (byte) 0x1B);
            fail("inverse of zero should throw");
        } catch (IllegalArgumentException e) {
            pass("inverse of zero throws exception");
        }
    }
    
    private static void assertEqual(byte actual, byte expected, String message) {
        if (actual == expected) {
            pass(message);
        } else {
            fail(message + " (expected: 0x" + String.format("%02X", expected & 0xFF) + 
                 ", got: 0x" + String.format("%02X", actual & 0xFF) + ")");
        }
    }
    
    private static void assertEqual(int actual, int expected, String message) {
        if (actual == expected) {
            pass(message);
        } else {
            fail(message + " (expected: " + expected + ", got: " + actual + ")");
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

