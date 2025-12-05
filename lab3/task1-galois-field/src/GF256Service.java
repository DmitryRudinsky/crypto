import java.util.ArrayList;
import java.util.List;

public class GF256Service {

    public static byte add(byte a, byte b) {
        return (byte) (a ^ b);
    }

    public static byte multiply(byte a, byte modulus) throws IllegalArgumentException {
        if (!isIrreducible((modulus & 0xFF) | 0x100)) {
            throw new IllegalArgumentException("Modulus must be irreducible");
        }
        return multiplyMod(a, a, modulus);
    }

    public static byte multiplyMod(byte a, byte b, byte modulus) throws IllegalArgumentException {
        if (!isIrreducible((modulus & 0xFF) | 0x100)) {
            throw new IllegalArgumentException("Modulus must be irreducible");
        }
        
        int result = 0;
        int aInt = a & 0xFF;
        int bInt = b & 0xFF;
        int mod = (modulus & 0xFF) | 0x100;
        
        for (int i = 0; i < 8; i++) {
            if ((bInt & 1) != 0) {
                result ^= aInt;
            }
            
            boolean highBitSet = (aInt & 0x80) != 0;
            aInt <<= 1;
            
            if (highBitSet) {
                aInt ^= mod;
            }
            
            bInt >>= 1;
        }
        
        return (byte) result;
    }

    public static byte inverse(byte a, byte modulus) throws IllegalArgumentException {
        if (!isIrreducible((modulus & 0xFF) | 0x100)) {
            throw new IllegalArgumentException("Modulus must be irreducible");
        }
        
        if (a == 0) {
            throw new IllegalArgumentException("Zero has no inverse");
        }
        
        int t = 0;
        int newT = 1;
        int r = (modulus & 0xFF) | 0x100;
        int newR = a & 0xFF;
        
        while (newR != 0) {
            int quotient = divide(r, newR);
            
            int tempT = t;
            t = newT;
            newT = tempT ^ multiplyPoly(quotient, newT);
            
            int tempR = r;
            r = newR;
            newR = tempR ^ multiplyPoly(quotient, newR);
        }
        
        if (r > 1) {
            throw new IllegalArgumentException("Element is not invertible");
        }
        
        return (byte) t;
    }

    public static boolean isIrreducible(int poly) {
        int p = poly & 0x1FF;
        
        if (p < 0x100) {
            return false;
        }
        
        int deg = degree(p);
        if (deg != 8) {
            return false;
        }
        
        if ((p & 1) == 0) {
            return false;
        }
        
        int[] divisors = {1, 2, 4};
        
        for (int d : divisors) {
            int x = 2;
            
            int powerOf2 = 1 << d;
            for (int i = 1; i < powerOf2; i++) {
                x = modPoly(multiplyPoly(x, 2), p);
            }
            
            int gcd = gcdPoly(x ^ 2, p);
            if (gcd != 1) {
                return false;
            }
        }
        
        int x = 2;
        for (int i = 1; i < 256; i++) {
            x = modPoly(multiplyPoly(x, 2), p);
        }
        
        if (x != 2) {
            return false;
        }
        
        return true;
    }

    public static List<Byte> getAllIrreduciblePolynomials() {
        List<Byte> result = new ArrayList<>();
        
        for (int i = 0x100; i <= 0x1FF; i++) {
            if (isIrreducible(i)) {
                result.add((byte) i);
            }
        }
        
        return result;
    }

    public static List<Byte> factorize(int poly) {
        List<Byte> factors = new ArrayList<>();
        
        if (poly == 0 || poly == 1) {
            return factors;
        }
        
        poly = poly & 0xFFFFFF;
        
        List<Byte> irreducibles = getAllIrreduciblePolynomials();
        
        for (byte irreducible : irreducibles) {
            int irr = (irreducible & 0xFF) | 0x100;
            
            while (poly != 0 && poly != 1) {
                int remainder = modPoly(poly, irr);
                if (remainder == 0) {
                    factors.add(irreducible);
                    poly = dividePoly(poly, irr);
                } else {
                    break;
                }
            }
            
            if (poly == 1) {
                break;
            }
        }
        
        if (poly > 1 && degree(poly) > 0) {
            if (degree(poly) <= 8) {
                factors.add((byte) poly);
            }
        }
        
        return factors;
    }

    private static int degree(int poly) {
        if (poly == 0) return -1;
        int deg = 0;
        while (poly > 1) {
            poly >>= 1;
            deg++;
        }
        return deg;
    }

    private static int multiplyPoly(int a, int b) {
        int result = 0;
        while (b != 0) {
            if ((b & 1) != 0) {
                result ^= a;
            }
            a <<= 1;
            b >>= 1;
        }
        return result;
    }

    private static int divide(int a, int b) {
        if (b == 0) return 0;
        
        int quotient = 0;
        int degA = degree(a);
        int degB = degree(b);
        
        while (degA >= degB && a != 0) {
            int shift = degA - degB;
            quotient ^= (1 << shift);
            a ^= (b << shift);
            degA = degree(a);
        }
        
        return quotient;
    }

    private static int modPoly(int a, int b) {
        if (b == 0) return a;
        
        int degA = degree(a);
        int degB = degree(b);
        
        while (degA >= degB && a != 0) {
            int shift = degA - degB;
            a ^= (b << shift);
            degA = degree(a);
        }
        
        return a;
    }

    private static int dividePoly(int a, int b) {
        if (b == 0) return 0;
        
        int quotient = 0;
        int degA = degree(a);
        int degB = degree(b);
        
        while (degA >= degB && a != 0) {
            int shift = degA - degB;
            quotient ^= (1 << shift);
            a ^= (b << shift);
            degA = degree(a);
        }
        
        return quotient;
    }

    private static int gcdPoly(int a, int b) {
        while (b != 0) {
            int temp = modPoly(a, b);
            a = b;
            b = temp;
        }
        return a;
    }
}

