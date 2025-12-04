import java.math.BigInteger;

public class NumberTheoryService {

    public static int legendreSymbol(BigInteger a, BigInteger p) {
        if (!p.testBit(0)) {
            throw new IllegalArgumentException("p must be odd");
        }
        
        a = a.mod(p);
        
        if (a.equals(BigInteger.ZERO)) {
            return 0;
        }
        
        BigInteger exponent = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        BigInteger result = modPow(a, exponent, p);
        
        if (result.equals(BigInteger.ONE)) {
            return 1;
        } else if (result.equals(p.subtract(BigInteger.ONE))) {
            return -1;
        } else {
            return 0;
        }
    }

    public static int jacobiSymbol(BigInteger a, BigInteger n) {
        if (n.compareTo(BigInteger.ONE) <= 0 || !n.testBit(0)) {
            throw new IllegalArgumentException("n must be odd and greater than 1");
        }
        
        a = a.mod(n);
        int result = 1;
        
        while (!a.equals(BigInteger.ZERO)) {
            while (!a.testBit(0)) {
                a = a.shiftRight(1);
                BigInteger nMod8 = n.and(BigInteger.valueOf(7));
                if (nMod8.equals(BigInteger.valueOf(3)) || nMod8.equals(BigInteger.valueOf(5))) {
                    result = -result;
                }
            }
            
            BigInteger temp = a;
            a = n;
            n = temp;
            
            if (a.and(BigInteger.valueOf(3)).equals(BigInteger.valueOf(3)) && 
                n.and(BigInteger.valueOf(3)).equals(BigInteger.valueOf(3))) {
                result = -result;
            }
            
            a = a.mod(n);
        }
        
        if (n.equals(BigInteger.ONE)) {
            return result;
        } else {
            return 0;
        }
    }

    public static BigInteger gcd(BigInteger a, BigInteger b) {
        a = a.abs();
        b = b.abs();
        
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
        }
        
        return a;
    }

    public static class ExtendedGcdResult {
        public final BigInteger gcd;
        public final BigInteger x;
        public final BigInteger y;
        
        public ExtendedGcdResult(BigInteger gcd, BigInteger x, BigInteger y) {
            this.gcd = gcd;
            this.x = x;
            this.y = y;
        }
    }

    public static ExtendedGcdResult extendedGcd(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return new ExtendedGcdResult(a, BigInteger.ONE, BigInteger.ZERO);
        }
        
        BigInteger origA = a;
        BigInteger origB = b;
        
        boolean negA = a.compareTo(BigInteger.ZERO) < 0;
        boolean negB = b.compareTo(BigInteger.ZERO) < 0;
        
        a = a.abs();
        b = b.abs();
        
        BigInteger x0 = BigInteger.ONE;
        BigInteger x1 = BigInteger.ZERO;
        BigInteger y0 = BigInteger.ZERO;
        BigInteger y1 = BigInteger.ONE;
        
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger q = a.divide(b);
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
            
            temp = x1;
            x1 = x0.subtract(q.multiply(x1));
            x0 = temp;
            
            temp = y1;
            y1 = y0.subtract(q.multiply(y1));
            y0 = temp;
        }
        
        BigInteger gcd = a;
        BigInteger x = x0;
        BigInteger y = y0;
        
        if (negA) {
            x = x.negate();
        }
        if (negB) {
            y = y.negate();
        }
        
        return new ExtendedGcdResult(gcd, x, y);
    }

    public static BigInteger modPow(BigInteger base, BigInteger exponent, BigInteger modulus) {
        if (modulus.equals(BigInteger.ONE)) {
            return BigInteger.ZERO;
        }
        
        if (exponent.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException("Negative exponent not supported");
        }
        
        BigInteger result = BigInteger.ONE;
        base = base.mod(modulus);
        
        while (exponent.compareTo(BigInteger.ZERO) > 0) {
            if (exponent.testBit(0)) {
                result = result.multiply(base).mod(modulus);
            }
            
            exponent = exponent.shiftRight(1);
            base = base.multiply(base).mod(modulus);
        }
        
        return result;
    }
}

