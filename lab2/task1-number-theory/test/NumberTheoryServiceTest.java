import java.math.BigInteger;

public class NumberTheoryServiceTest {
    
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    
    public static void main(String[] args) {
        System.out.println("=== Running Number Theory Service Tests ===\n");
        
        testLegendreSymbol();
        testJacobiSymbol();
        testGcd();
        testExtendedGcd();
        testModPow();
        
        System.out.println("\n=== Test Summary ===");
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Total: " + (testsPassed + testsFailed));
        
        if (testsFailed == 0) {
            System.out.println("\nAll tests passed!");
        }
    }
    
    private static void testLegendreSymbol() {
        System.out.println("Testing Legendre Symbol...");
        
        assertEqual(NumberTheoryService.legendreSymbol(new BigInteger("1"), new BigInteger("7")), 1, "L(1, 7)");
        assertEqual(NumberTheoryService.legendreSymbol(new BigInteger("2"), new BigInteger("7")), 1, "L(2, 7)");
        assertEqual(NumberTheoryService.legendreSymbol(new BigInteger("3"), new BigInteger("7")), -1, "L(3, 7)");
        assertEqual(NumberTheoryService.legendreSymbol(new BigInteger("4"), new BigInteger("7")), 1, "L(4, 7)");
        assertEqual(NumberTheoryService.legendreSymbol(new BigInteger("0"), new BigInteger("7")), 0, "L(0, 7)");
        assertEqual(NumberTheoryService.legendreSymbol(new BigInteger("14"), new BigInteger("7")), 0, "L(14, 7)");
        assertEqual(NumberTheoryService.legendreSymbol(new BigInteger("3"), new BigInteger("11")), 1, "L(3, 11)");
        assertEqual(NumberTheoryService.legendreSymbol(new BigInteger("5"), new BigInteger("11")), 1, "L(5, 11)");
        assertEqual(NumberTheoryService.legendreSymbol(new BigInteger("2"), new BigInteger("11")), -1, "L(2, 11)");
        
        System.out.println();
    }
    
    private static void testJacobiSymbol() {
        System.out.println("Testing Jacobi Symbol...");
        
        assertEqual(NumberTheoryService.jacobiSymbol(new BigInteger("1"), new BigInteger("3")), 1, "J(1, 3)");
        assertEqual(NumberTheoryService.jacobiSymbol(new BigInteger("2"), new BigInteger("15")), 1, "J(2, 15)");
        assertEqual(NumberTheoryService.jacobiSymbol(new BigInteger("5"), new BigInteger("9")), 1, "J(5, 9)");
        assertEqual(NumberTheoryService.jacobiSymbol(new BigInteger("8"), new BigInteger("21")), -1, "J(8, 21)");
        assertEqual(NumberTheoryService.jacobiSymbol(new BigInteger("1001"), new BigInteger("9907")), -1, "J(1001, 9907)");
        assertEqual(NumberTheoryService.jacobiSymbol(new BigInteger("19"), new BigInteger("45")), 1, "J(19, 45)");
        assertEqual(NumberTheoryService.jacobiSymbol(new BigInteger("0"), new BigInteger("9")), 0, "J(0, 9)");
        
        System.out.println();
    }
    
    private static void testGcd() {
        System.out.println("Testing GCD...");
        
        assertEqual(NumberTheoryService.gcd(new BigInteger("48"), new BigInteger("18")), new BigInteger("6"), "gcd(48, 18)");
        assertEqual(NumberTheoryService.gcd(new BigInteger("1071"), new BigInteger("462")), new BigInteger("21"), "gcd(1071, 462)");
        assertEqual(NumberTheoryService.gcd(new BigInteger("100"), new BigInteger("35")), new BigInteger("5"), "gcd(100, 35)");
        assertEqual(NumberTheoryService.gcd(new BigInteger("0"), new BigInteger("5")), new BigInteger("5"), "gcd(0, 5)");
        assertEqual(NumberTheoryService.gcd(new BigInteger("5"), new BigInteger("0")), new BigInteger("5"), "gcd(5, 0)");
        assertEqual(NumberTheoryService.gcd(new BigInteger("17"), new BigInteger("19")), new BigInteger("1"), "gcd(17, 19)");
        assertEqual(NumberTheoryService.gcd(new BigInteger("123456789"), new BigInteger("987654321")), new BigInteger("9"), "gcd(123456789, 987654321)");
        
        System.out.println();
    }
    
    private static void testExtendedGcd() {
        System.out.println("Testing Extended GCD...");
        
        NumberTheoryService.ExtendedGcdResult result;
        
        result = NumberTheoryService.extendedGcd(new BigInteger("240"), new BigInteger("46"));
        assertEqual(result.gcd, new BigInteger("2"), "extgcd(240, 46) - gcd");
        BigInteger verify1 = new BigInteger("240").multiply(result.x).add(new BigInteger("46").multiply(result.y));
        assertEqual(verify1, result.gcd, "extgcd(240, 46) - Bezout verification");
        
        result = NumberTheoryService.extendedGcd(new BigInteger("1071"), new BigInteger("462"));
        assertEqual(result.gcd, new BigInteger("21"), "extgcd(1071, 462) - gcd");
        BigInteger verify2 = new BigInteger("1071").multiply(result.x).add(new BigInteger("462").multiply(result.y));
        assertEqual(verify2, result.gcd, "extgcd(1071, 462) - Bezout verification");
        
        result = NumberTheoryService.extendedGcd(new BigInteger("17"), new BigInteger("13"));
        assertEqual(result.gcd, new BigInteger("1"), "extgcd(17, 13) - gcd");
        BigInteger verify3 = new BigInteger("17").multiply(result.x).add(new BigInteger("13").multiply(result.y));
        assertEqual(verify3, result.gcd, "extgcd(17, 13) - Bezout verification");
        
        result = NumberTheoryService.extendedGcd(new BigInteger("100"), new BigInteger("35"));
        assertEqual(result.gcd, new BigInteger("5"), "extgcd(100, 35) - gcd");
        BigInteger verify4 = new BigInteger("100").multiply(result.x).add(new BigInteger("35").multiply(result.y));
        assertEqual(verify4, result.gcd, "extgcd(100, 35) - Bezout verification");
        
        System.out.println();
    }
    
    private static void testModPow() {
        System.out.println("Testing Modular Exponentiation...");
        
        assertEqual(NumberTheoryService.modPow(new BigInteger("3"), new BigInteger("7"), new BigInteger("13")), 
                   new BigInteger("3"), "3^7 mod 13");
        assertEqual(NumberTheoryService.modPow(new BigInteger("2"), new BigInteger("10"), new BigInteger("1000")), 
                   new BigInteger("24"), "2^10 mod 1000");
        assertEqual(NumberTheoryService.modPow(new BigInteger("5"), new BigInteger("3"), new BigInteger("13")), 
                   new BigInteger("8"), "5^3 mod 13");
        assertEqual(NumberTheoryService.modPow(new BigInteger("7"), new BigInteger("0"), new BigInteger("13")), 
                   new BigInteger("1"), "7^0 mod 13");
        assertEqual(NumberTheoryService.modPow(new BigInteger("2"), new BigInteger("100"), new BigInteger("1000000007")), 
                   new BigInteger("976371285"), "2^100 mod 1000000007");
        assertEqual(NumberTheoryService.modPow(new BigInteger("10"), new BigInteger("6"), new BigInteger("7")), 
                   new BigInteger("1"), "10^6 mod 7");
        
        System.out.println();
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

