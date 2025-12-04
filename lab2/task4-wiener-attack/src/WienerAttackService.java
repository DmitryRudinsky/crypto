import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class WienerAttackService {
    
    public static class Fraction {
        public final BigInteger numerator;
        public final BigInteger denominator;
        
        public Fraction(BigInteger numerator, BigInteger denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }
        
        @Override
        public String toString() {
            return numerator + "/" + denominator;
        }
    }
    
    public static class AttackResult {
        public final BigInteger d;
        public final BigInteger phi;
        public final List<Fraction> convergents;
        public final boolean success;
        
        public AttackResult(BigInteger d, BigInteger phi, List<Fraction> convergents, boolean success) {
            this.d = d;
            this.phi = phi;
            this.convergents = convergents;
            this.success = success;
        }
    }
    
    public AttackResult attack(BigInteger e, BigInteger n) {
        List<Fraction> convergents = computeConvergents(e, n);
        
        for (Fraction convergent : convergents) {
            BigInteger k = convergent.numerator;
            BigInteger d = convergent.denominator;
            
            if (k.equals(BigInteger.ZERO)) {
                continue;
            }
            
            BigInteger phi = e.multiply(d).subtract(BigInteger.ONE).divide(k);
            
            if (isValidPhi(phi, n)) {
                if (verifyDecryptionExponent(e, d, n, phi)) {
                    return new AttackResult(d, phi, convergents, true);
                }
            }
        }
        
        return new AttackResult(null, null, convergents, false);
    }
    
    private List<Fraction> computeConvergents(BigInteger e, BigInteger n) {
        List<Fraction> convergents = new ArrayList<>();
        List<BigInteger> continuedFraction = computeContinuedFraction(e, n);
        
        if (continuedFraction.isEmpty()) {
            return convergents;
        }
        
        BigInteger h0 = continuedFraction.get(0);
        BigInteger h1 = continuedFraction.size() > 1 ? 
            continuedFraction.get(0).multiply(continuedFraction.get(1)).add(BigInteger.ONE) : 
            BigInteger.ONE;
        
        BigInteger k0 = BigInteger.ONE;
        BigInteger k1 = continuedFraction.size() > 1 ? continuedFraction.get(1) : BigInteger.ZERO;
        
        convergents.add(new Fraction(h0, k0));
        if (continuedFraction.size() > 1) {
            convergents.add(new Fraction(h1, k1));
        }
        
        for (int i = 2; i < continuedFraction.size(); i++) {
            BigInteger a = continuedFraction.get(i);
            BigInteger h = a.multiply(h1).add(h0);
            BigInteger k = a.multiply(k1).add(k0);
            
            convergents.add(new Fraction(h, k));
            
            h0 = h1;
            h1 = h;
            k0 = k1;
            k1 = k;
        }
        
        return convergents;
    }
    
    private List<BigInteger> computeContinuedFraction(BigInteger e, BigInteger n) {
        List<BigInteger> continuedFraction = new ArrayList<>();
        BigInteger a = e;
        BigInteger b = n;
        
        while (!b.equals(BigInteger.ZERO) && continuedFraction.size() < 1000) {
            BigInteger q = a.divide(b);
            continuedFraction.add(q);
            
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
        }
        
        return continuedFraction;
    }
    
    private boolean isValidPhi(BigInteger phi, BigInteger n) {
        if (phi.compareTo(BigInteger.ZERO) <= 0) {
            return false;
        }
        
        if (phi.compareTo(n) >= 0) {
            return false;
        }
        
        return true;
    }
    
    private boolean verifyDecryptionExponent(BigInteger e, BigInteger d, BigInteger n, BigInteger phi) {
        BigInteger ed = e.multiply(d);
        BigInteger remainder = ed.mod(phi);
        
        if (!remainder.equals(BigInteger.ONE)) {
            return false;
        }
        
        BigInteger message = new BigInteger("42");
        if (message.compareTo(n) >= 0) {
            message = BigInteger.TWO;
        }
        
        BigInteger encrypted = NumberTheoryService.modPow(message, e, n);
        BigInteger decrypted = NumberTheoryService.modPow(encrypted, d, n);
        
        return decrypted.equals(message);
    }
    
    public AttackResult attackWithDetails(BigInteger e, BigInteger n) {
        return attack(e, n);
    }
}

