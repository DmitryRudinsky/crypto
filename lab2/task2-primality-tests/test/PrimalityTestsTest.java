import java.math.BigInteger;

public class PrimalityTestsTest {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Running Primality Tests ===\n");
        
        testFermat();
        testSolovayStrassen();
        testMillerRabin();
        testSmallNumbers();
        testKnownPrimes();
        testKnownComposites();
        testCarmichaelNumbers();
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Total: " + (testsPassed + testsFailed));
        
        if (testsFailed == 0) {
            System.out.println("\nAll tests passed!");
        }
    }
    
    private static void testFermat() {
        System.out.println("Testing Fermat Test...");
        PrimalityTest test = new FermatTest();
        
        assertTrue(test.isProbablyPrime(new BigInteger("2"), 0.99), "Fermat: 2 is prime");
        assertTrue(test.isProbablyPrime(new BigInteger("3"), 0.99), "Fermat: 3 is prime");
        assertTrue(test.isProbablyPrime(new BigInteger("17"), 0.99), "Fermat: 17 is prime");
        assertTrue(test.isProbablyPrime(new BigInteger("97"), 0.99), "Fermat: 97 is prime");
        
        assertFalse(test.isProbablyPrime(new BigInteger("4"), 0.99), "Fermat: 4 is composite");
        assertFalse(test.isProbablyPrime(new BigInteger("15"), 0.99), "Fermat: 15 is composite");
        assertFalse(test.isProbablyPrime(new BigInteger("91"), 0.99), "Fermat: 91 is composite");
        
        System.out.println();
    }
    
    private static void testSolovayStrassen() {
        System.out.println("Testing Solovay-Strassen Test...");
        PrimalityTest test = new SolovayStrassenTest();
        
        assertTrue(test.isProbablyPrime(new BigInteger("2"), 0.99), "Solovay-Strassen: 2 is prime");
        assertTrue(test.isProbablyPrime(new BigInteger("3"), 0.99), "Solovay-Strassen: 3 is prime");
        assertTrue(test.isProbablyPrime(new BigInteger("17"), 0.99), "Solovay-Strassen: 17 is prime");
        assertTrue(test.isProbablyPrime(new BigInteger("97"), 0.99), "Solovay-Strassen: 97 is prime");
        
        assertFalse(test.isProbablyPrime(new BigInteger("4"), 0.99), "Solovay-Strassen: 4 is composite");
        assertFalse(test.isProbablyPrime(new BigInteger("15"), 0.99), "Solovay-Strassen: 15 is composite");
        assertFalse(test.isProbablyPrime(new BigInteger("91"), 0.99), "Solovay-Strassen: 91 is composite");
        assertFalse(test.isProbablyPrime(new BigInteger("561"), 0.99), "Solovay-Strassen: 561 is composite");
        
        System.out.println();
    }
    
    private static void testMillerRabin() {
        System.out.println("Testing Miller-Rabin Test...");
        PrimalityTest test = new MillerRabinTest();
        
        assertTrue(test.isProbablyPrime(new BigInteger("2"), 0.99), "Miller-Rabin: 2 is prime");
        assertTrue(test.isProbablyPrime(new BigInteger("3"), 0.99), "Miller-Rabin: 3 is prime");
        assertTrue(test.isProbablyPrime(new BigInteger("17"), 0.99), "Miller-Rabin: 17 is prime");
        assertTrue(test.isProbablyPrime(new BigInteger("97"), 0.99), "Miller-Rabin: 97 is prime");
        
        assertFalse(test.isProbablyPrime(new BigInteger("4"), 0.99), "Miller-Rabin: 4 is composite");
        assertFalse(test.isProbablyPrime(new BigInteger("15"), 0.99), "Miller-Rabin: 15 is composite");
        assertFalse(test.isProbablyPrime(new BigInteger("91"), 0.99), "Miller-Rabin: 91 is composite");
        assertFalse(test.isProbablyPrime(new BigInteger("561"), 0.99), "Miller-Rabin: 561 is composite");
        
        System.out.println();
    }
    
    private static void testSmallNumbers() {
        System.out.println("Testing Small Numbers...");
        PrimalityTest fermat = new FermatTest();
        
        assertFalse(fermat.isProbablyPrime(new BigInteger("0"), 0.99), "0 is not prime");
        assertFalse(fermat.isProbablyPrime(new BigInteger("1"), 0.99), "1 is not prime");
        assertTrue(fermat.isProbablyPrime(new BigInteger("2"), 0.99), "2 is prime");
        assertFalse(fermat.isProbablyPrime(new BigInteger("4"), 0.99), "4 is not prime");
        
        System.out.println();
    }
    
    private static void testKnownPrimes() {
        System.out.println("Testing Known Primes...");
        PrimalityTest test = new MillerRabinTest();
        
        BigInteger[] primes = {
            new BigInteger("5"), new BigInteger("7"), new BigInteger("11"), 
            new BigInteger("13"), new BigInteger("17"), new BigInteger("19"),
            new BigInteger("23"), new BigInteger("29"), new BigInteger("31"),
            new BigInteger("1009"), new BigInteger("10007"), new BigInteger("104729")
        };
        
        for (BigInteger p : primes) {
            assertTrue(test.isProbablyPrime(p, 0.99), p + " is prime");
        }
        
        System.out.println();
    }
    
    private static void testKnownComposites() {
        System.out.println("Testing Known Composites...");
        PrimalityTest test = new MillerRabinTest();
        
        BigInteger[] composites = {
            new BigInteger("4"), new BigInteger("6"), new BigInteger("8"),
            new BigInteger("9"), new BigInteger("10"), new BigInteger("12"),
            new BigInteger("14"), new BigInteger("15"), new BigInteger("16"),
            new BigInteger("1001"), new BigInteger("10001"), new BigInteger("104730")
        };
        
        for (BigInteger c : composites) {
            assertFalse(test.isProbablyPrime(c, 0.99), c + " is composite");
        }
        
        System.out.println();
    }
    
    private static void testCarmichaelNumbers() {
        System.out.println("Testing Carmichael Numbers...");
        
        PrimalityTest solovay = new SolovayStrassenTest();
        PrimalityTest millerRabin = new MillerRabinTest();
        
        BigInteger[] carmichael = {
            new BigInteger("561"),
            new BigInteger("1105"),
            new BigInteger("1729")
        };
        
        for (BigInteger c : carmichael) {
            assertFalse(solovay.isProbablyPrime(c, 0.99), 
                "Solovay-Strassen detects Carmichael " + c);
            assertFalse(millerRabin.isProbablyPrime(c, 0.99), 
                "Miller-Rabin detects Carmichael " + c);
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
    
    private static void assertFalse(boolean condition, String testName) {
        assertTrue(!condition, testName);
    }
}

