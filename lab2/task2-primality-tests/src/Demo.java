import java.math.BigInteger;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=== Primality Tests Demo ===\n");
        
        PrimalityTest fermat = new FermatTest();
        PrimalityTest solovayStrassen = new SolovayStrassenTest();
        PrimalityTest millerRabin = new MillerRabinTest();
        
        double probability = 0.99;
        
        BigInteger[] primes = {
            new BigInteger("17"),
            new BigInteger("97"),
            new BigInteger("1009"),
            new BigInteger("10007"),
            new BigInteger("104729"),
            new BigInteger("15485863"),
            new BigInteger("2147483647")
        };
        
        BigInteger[] composites = {
            new BigInteger("15"),
            new BigInteger("91"),
            new BigInteger("1001"),
            new BigInteger("10001"),
            new BigInteger("104730"),
            new BigInteger("15485862"),
            new BigInteger("561")
        };
        
        System.out.println("Testing with probability >= " + probability + "\n");
        
        System.out.println("Known Prime Numbers:");
        System.out.println("-------------------");
        for (BigInteger n : primes) {
            System.out.println("n = " + n);
            System.out.println("  Fermat:           " + fermat.isProbablyPrime(n, probability));
            System.out.println("  Solovay-Strassen: " + solovayStrassen.isProbablyPrime(n, probability));
            System.out.println("  Miller-Rabin:     " + millerRabin.isProbablyPrime(n, probability));
            System.out.println();
        }
        
        System.out.println("Known Composite Numbers:");
        System.out.println("-----------------------");
        for (BigInteger n : composites) {
            System.out.println("n = " + n);
            System.out.println("  Fermat:           " + fermat.isProbablyPrime(n, probability));
            System.out.println("  Solovay-Strassen: " + solovayStrassen.isProbablyPrime(n, probability));
            System.out.println("  Miller-Rabin:     " + millerRabin.isProbablyPrime(n, probability));
            System.out.println();
        }
        
        System.out.println("Special case - Carmichael number 561:");
        System.out.println("--------------------------------------");
        BigInteger carmichael = new BigInteger("561");
        System.out.println("  Fermat:           " + fermat.isProbablyPrime(carmichael, probability));
        System.out.println("  Solovay-Strassen: " + solovayStrassen.isProbablyPrime(carmichael, probability));
        System.out.println("  Miller-Rabin:     " + millerRabin.isProbablyPrime(carmichael, probability));
        System.out.println();
        
        System.out.println("Large prime (2^31 - 1):");
        System.out.println("-----------------------");
        BigInteger largePrime = new BigInteger("2147483647");
        System.out.println("  Fermat:           " + fermat.isProbablyPrime(largePrime, probability));
        System.out.println("  Solovay-Strassen: " + solovayStrassen.isProbablyPrime(largePrime, probability));
        System.out.println("  Miller-Rabin:     " + millerRabin.isProbablyPrime(largePrime, probability));
    }
}

