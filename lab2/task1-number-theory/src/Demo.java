import java.math.BigInteger;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=== Number Theory Service Demo ===\n");
        
        System.out.println("1. Legendre Symbol");
        System.out.println("-------------------");
        BigInteger a1 = new BigInteger("2");
        BigInteger p1 = new BigInteger("7");
        int legendre1 = NumberTheoryService.legendreSymbol(a1, p1);
        System.out.println("L(2, 7) = " + legendre1);
        
        a1 = new BigInteger("3");
        p1 = new BigInteger("11");
        legendre1 = NumberTheoryService.legendreSymbol(a1, p1);
        System.out.println("L(3, 11) = " + legendre1);
        
        a1 = new BigInteger("5");
        p1 = new BigInteger("11");
        legendre1 = NumberTheoryService.legendreSymbol(a1, p1);
        System.out.println("L(5, 11) = " + legendre1);
        
        System.out.println();
        
        System.out.println("2. Jacobi Symbol");
        System.out.println("----------------");
        BigInteger a2 = new BigInteger("1001");
        BigInteger n2 = new BigInteger("9907");
        int jacobi1 = NumberTheoryService.jacobiSymbol(a2, n2);
        System.out.println("J(1001, 9907) = " + jacobi1);
        
        a2 = new BigInteger("19");
        n2 = new BigInteger("45");
        jacobi1 = NumberTheoryService.jacobiSymbol(a2, n2);
        System.out.println("J(19, 45) = " + jacobi1);
        
        a2 = new BigInteger("8");
        n2 = new BigInteger("21");
        jacobi1 = NumberTheoryService.jacobiSymbol(a2, n2);
        System.out.println("J(8, 21) = " + jacobi1);
        
        System.out.println();
        
        System.out.println("3. GCD (Euclidean Algorithm)");
        System.out.println("-----------------------------");
        BigInteger x1 = new BigInteger("48");
        BigInteger y1 = new BigInteger("18");
        BigInteger gcd1 = NumberTheoryService.gcd(x1, y1);
        System.out.println("gcd(48, 18) = " + gcd1);
        
        x1 = new BigInteger("1071");
        y1 = new BigInteger("462");
        gcd1 = NumberTheoryService.gcd(x1, y1);
        System.out.println("gcd(1071, 462) = " + gcd1);
        
        x1 = new BigInteger("123456789");
        y1 = new BigInteger("987654321");
        gcd1 = NumberTheoryService.gcd(x1, y1);
        System.out.println("gcd(123456789, 987654321) = " + gcd1);
        
        System.out.println();
        
        System.out.println("4. Extended GCD (Bezout's Identity)");
        System.out.println("------------------------------------");
        BigInteger a3 = new BigInteger("240");
        BigInteger b3 = new BigInteger("46");
        NumberTheoryService.ExtendedGcdResult result1 = NumberTheoryService.extendedGcd(a3, b3);
        System.out.println("extgcd(240, 46):");
        System.out.println("  gcd = " + result1.gcd);
        System.out.println("  x = " + result1.x);
        System.out.println("  y = " + result1.y);
        System.out.println("  Verification: " + a3 + " * " + result1.x + " + " + b3 + " * " + result1.y + " = " + 
                          a3.multiply(result1.x).add(b3.multiply(result1.y)));
        
        a3 = new BigInteger("1071");
        b3 = new BigInteger("462");
        result1 = NumberTheoryService.extendedGcd(a3, b3);
        System.out.println("\nextgcd(1071, 462):");
        System.out.println("  gcd = " + result1.gcd);
        System.out.println("  x = " + result1.x);
        System.out.println("  y = " + result1.y);
        System.out.println("  Verification: " + a3 + " * " + result1.x + " + " + b3 + " * " + result1.y + " = " + 
                          a3.multiply(result1.x).add(b3.multiply(result1.y)));
        
        System.out.println();
        
        System.out.println("5. Modular Exponentiation");
        System.out.println("-------------------------");
        BigInteger base1 = new BigInteger("3");
        BigInteger exp1 = new BigInteger("7");
        BigInteger mod1 = new BigInteger("13");
        BigInteger modPow1 = NumberTheoryService.modPow(base1, exp1, mod1);
        System.out.println("3^7 mod 13 = " + modPow1);
        
        base1 = new BigInteger("2");
        exp1 = new BigInteger("100");
        mod1 = new BigInteger("1000000007");
        modPow1 = NumberTheoryService.modPow(base1, exp1, mod1);
        System.out.println("2^100 mod 1000000007 = " + modPow1);
        
        base1 = new BigInteger("123456789");
        exp1 = new BigInteger("987654321");
        mod1 = new BigInteger("1000000009");
        modPow1 = NumberTheoryService.modPow(base1, exp1, mod1);
        System.out.println("123456789^987654321 mod 1000000009 = " + modPow1);
    }
}

