public class GaloisFieldService {
    
    public static byte add(byte a, byte b) {
        return (byte) (a ^ b);
    }
    
    public static byte multiply(byte a, byte b, byte modulus) {
        if (!isIrreducible(modulus)) {
            throw new IllegalArgumentException("Modulus must be irreducible");
        }
        
        int result = 0;
        int aa = a & 0xFF;
        int bb = b & 0xFF;
        int mod = modulus & 0xFF;
        
        for (int i = 0; i < 8; i++) {
            if ((bb & 1) != 0) {
                result ^= aa;
            }
            
            boolean highBitSet = (aa & 0x80) != 0;
            aa <<= 1;
            
            if (highBitSet) {
                aa ^= mod;
            }
            
            bb >>= 1;
        }
        
        return (byte) result;
    }
    
    public static byte inverse(byte a, byte modulus) {
        if (!isIrreducible(modulus)) {
            throw new IllegalArgumentException("Modulus must be irreducible");
        }
        
        int aa = a & 0xFF;
        if (aa == 0) {
            throw new IllegalArgumentException("Zero has no inverse");
        }
        
        int mod = modulus & 0xFF;
        int u = aa;
        int v = mod;
        int g1 = 1;
        int g2 = 0;
        
        while (u != 1) {
            int j = degree(u) - degree(v);
            
            if (j < 0) {
                int temp = u;
                u = v;
                v = temp;
                
                temp = g1;
                g1 = g2;
                g2 = temp;
                
                j = -j;
            }
            
            u ^= (v << j);
            g1 ^= (g2 << j);
        }
        
        return (byte) g1;
    }
    
    public static boolean isIrreducible(byte polynomial) {
        int poly = polynomial & 0xFF;
        poly |= 0x100;
        
        if ((poly & 1) == 0) {
            return false;
        }
        
        for (int divisor = 2; divisor < poly; divisor++) {
            if (degree(divisor) > 4) {
                break;
            }
            
            int remainder = mod(poly, divisor);
            if (remainder == 0) {
                return false;
            }
        }
        
        return true;
    }
    
    public static byte[] getAllIrreduciblePolynomials() {
        byte[] result = new byte[30];
        int count = 0;
        
        for (int i = 0x100; i <= 0x1FF; i++) {
            byte poly = (byte) i;
            if (isIrreducible(poly)) {
                result[count++] = poly;
            }
        }
        
        return result;
    }
    
    public static int[] factorize(int polynomial) {
        if (polynomial < 2) {
            return new int[0];
        }
        
        java.util.List<Integer> factors = new java.util.ArrayList<>();
        
        int current = polynomial;
        
        for (int divisor = 2; divisor <= current; divisor++) {
            while (current > 1) {
                int quotient = divide(current, divisor);
                int remainder = current ^ multiplyPolynomials(quotient, divisor, 0);
                
                if (remainder == 0) {
                    factors.add(divisor);
                    current = quotient;
                } else {
                    break;
                }
            }
        }
        
        if (factors.isEmpty()) {
            factors.add(polynomial);
        }
        
        int[] result = new int[factors.size()];
        for (int i = 0; i < factors.size(); i++) {
            result[i] = factors.get(i);
        }
        
        return result;
    }
    
    private static int degree(int polynomial) {
        int deg = -1;
        while (polynomial != 0) {
            deg++;
            polynomial >>>= 1;
        }
        return deg;
    }
    
    private static int multiplyPolynomials(int a, int b, int modulus) {
        int result = 0;
        
        while (b != 0) {
            if ((b & 1) != 0) {
                result ^= a;
            }
            
            b >>>= 1;
            a <<= 1;
            
            if (modulus != 0 && degree(a) >= degree(modulus)) {
                a ^= modulus;
            }
        }
        
        return result;
    }
    
    private static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = mod(a, b);
            a = temp;
        }
        return a;
    }
    
    private static int mod(int dividend, int divisor) {
        if (divisor == 0) {
            return dividend;
        }
        
        while (degree(dividend) >= degree(divisor)) {
            int shift = degree(dividend) - degree(divisor);
            dividend ^= (divisor << shift);
        }
        
        return dividend;
    }
    
    private static int divide(int dividend, int divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        
        int quotient = 0;
        
        while (degree(dividend) >= degree(divisor)) {
            int shift = degree(dividend) - degree(divisor);
            quotient ^= (1 << shift);
            dividend ^= (divisor << shift);
        }
        
        return quotient;
    }
}

