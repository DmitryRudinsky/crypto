import java.math.BigInteger;
import java.security.SecureRandom;

public class VulnerableRSAGenerator {
    
    private final SecureRandom random;
    
    public static class KeyPair {
        public final BigInteger n;
        public final BigInteger e;
        public final BigInteger d;
        public final BigInteger p;
        public final BigInteger q;
        public final BigInteger phi;
        
        public KeyPair(BigInteger n, BigInteger e, BigInteger d, BigInteger p, BigInteger q, BigInteger phi) {
            this.n = n;
            this.e = e;
            this.d = d;
            this.p = p;
            this.q = q;
            this.phi = phi;
        }
    }
    
    public VulnerableRSAGenerator() {
        this.random = new SecureRandom();
    }
    
    public KeyPair generateVulnerableKeyPair(int bitLength) {
        BigInteger p = generatePrime(bitLength);
        BigInteger q = generatePrime(bitLength);
        
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        
        BigInteger threshold = sqrt(sqrt(n));
        BigInteger d = generateSmallD(phi, threshold);
        
        BigInteger e = computePublicExponent(d, phi);
        
        return new KeyPair(n, e, d, p, q, phi);
    }
    
    private BigInteger generatePrime(int bitLength) {
        return BigInteger.probablePrime(bitLength, random);
    }
    
    private BigInteger generateSmallD(BigInteger phi, BigInteger threshold) {
        BigInteger d;
        int attempts = 0;
        do {
            d = new BigInteger(threshold.bitLength() - 1, random);
            if (d.compareTo(BigInteger.ONE) <= 0) {
                continue;
            }
            attempts++;
        } while (!NumberTheoryService.gcd(d, phi).equals(BigInteger.ONE) && attempts < 10000);
        
        if (!NumberTheoryService.gcd(d, phi).equals(BigInteger.ONE)) {
            d = BigInteger.valueOf(3);
            while (!NumberTheoryService.gcd(d, phi).equals(BigInteger.ONE)) {
                d = d.add(BigInteger.TWO);
            }
        }
        
        return d;
    }
    
    private BigInteger computePublicExponent(BigInteger d, BigInteger phi) {
        NumberTheoryService.ExtendedGcdResult result = NumberTheoryService.extendedGcd(d, phi);
        BigInteger e = result.x.mod(phi);
        if (e.compareTo(BigInteger.ZERO) < 0) {
            e = e.add(phi);
        }
        return e;
    }
    
    private BigInteger sqrt(BigInteger n) {
        if (n.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("Cannot compute square root of negative number");
        }
        if (n.equals(BigInteger.ZERO) || n.equals(BigInteger.ONE)) {
            return n;
        }
        
        BigInteger left = BigInteger.ONE;
        BigInteger right = n;
        BigInteger result = n;
        
        while (left.compareTo(right) <= 0) {
            BigInteger mid = left.add(right).divide(BigInteger.TWO);
            BigInteger square = mid.multiply(mid);
            
            int cmp = square.compareTo(n);
            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                left = mid.add(BigInteger.ONE);
                result = mid;
            } else {
                right = mid.subtract(BigInteger.ONE);
            }
        }
        
        return result;
    }
}

