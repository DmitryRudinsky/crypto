import java.math.BigInteger;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=== Wiener Attack Demo ===\n");
        
        WienerAttackService attackService = new WienerAttackService();
        VulnerableRSAGenerator generator = new VulnerableRSAGenerator();
        
        System.out.println("1. Generating Vulnerable RSA Key (256-bit)");
        System.out.println("   ----------------------------------------");
        VulnerableRSAGenerator.KeyPair keyPair = generator.generateVulnerableKeyPair(256);
        
        System.out.println("   n (modulus): " + keyPair.n);
        System.out.println("   e (public exponent): " + keyPair.e);
        System.out.println("   d (private exponent - VULNERABLE): " + keyPair.d);
        System.out.println("   phi: " + keyPair.phi);
        System.out.println("   n bit length: " + keyPair.n.bitLength());
        System.out.println("   d bit length: " + keyPair.d.bitLength());
        
        BigInteger threshold = sqrt(sqrt(keyPair.n));
        System.out.println("   n^(1/4): " + threshold);
        System.out.println("   d < n^(1/4): " + (keyPair.d.compareTo(threshold) < 0));
        
        System.out.println("\n2. Performing Wiener Attack");
        System.out.println("   -------------------------");
        WienerAttackService.AttackResult result = attackService.attack(keyPair.e, keyPair.n);
        
        if (result.success) {
            System.out.println("   Attack SUCCESS!");
            System.out.println("   Found d: " + result.d);
            System.out.println("   Found phi: " + result.phi);
            System.out.println("   d matches: " + result.d.equals(keyPair.d));
            System.out.println("   phi matches: " + result.phi.equals(keyPair.phi));
        } else {
            System.out.println("   Attack FAILED!");
        }
        
        System.out.println("\n3. Convergents Computed During Attack");
        System.out.println("   -----------------------------------");
        System.out.println("   Total convergents: " + result.convergents.size());
        System.out.println("   First 10 convergents:");
        for (int i = 0; i < Math.min(10, result.convergents.size()); i++) {
            WienerAttackService.Fraction f = result.convergents.get(i);
            System.out.println("     [" + i + "] " + f);
        }
        
        System.out.println("\n4. Verifying Attack Result");
        System.out.println("   -----------------------");
        if (result.success) {
            BigInteger message = new BigInteger("123456");
            BigInteger encrypted = NumberTheoryService.modPow(message, keyPair.e, keyPair.n);
            BigInteger decrypted = NumberTheoryService.modPow(encrypted, result.d, keyPair.n);
            
            System.out.println("   Original message: " + message);
            System.out.println("   Encrypted: " + encrypted);
            System.out.println("   Decrypted with found d: " + decrypted);
            System.out.println("   Decryption successful: " + message.equals(decrypted));
        }
        
        System.out.println("\n5. Multiple Vulnerable Keys Test");
        System.out.println("   ------------------------------");
        
        int[] bitLengths = {128, 256, 512};
        for (int bitLength : bitLengths) {
            VulnerableRSAGenerator.KeyPair kp = generator.generateVulnerableKeyPair(bitLength);
            WienerAttackService.AttackResult res = attackService.attack(kp.e, kp.n);
            
            System.out.println("   Bit length: " + bitLength);
            System.out.println("     n bit length: " + kp.n.bitLength());
            System.out.println("     d bit length: " + kp.d.bitLength());
            System.out.println("     Attack success: " + res.success);
            if (res.success) {
                System.out.println("     d found correctly: " + res.d.equals(kp.d));
                System.out.println("     Convergents computed: " + res.convergents.size());
            }
            System.out.println();
        }
        
        System.out.println("6. Example with Known Vulnerable Parameters");
        System.out.println("   -----------------------------------------");
        
        BigInteger n_example = new BigInteger("90581");
        BigInteger e_example = new BigInteger("17993");
        
        System.out.println("   n: " + n_example);
        System.out.println("   e: " + e_example);
        
        WienerAttackService.AttackResult exampleResult = attackService.attack(e_example, n_example);
        
        if (exampleResult.success) {
            System.out.println("   Attack SUCCESS!");
            System.out.println("   Found d: " + exampleResult.d);
            System.out.println("   Found phi: " + exampleResult.phi);
            System.out.println("   Convergents: " + exampleResult.convergents.size());
            
            BigInteger testMsg = new BigInteger("42");
            BigInteger enc = NumberTheoryService.modPow(testMsg, e_example, n_example);
            BigInteger dec = NumberTheoryService.modPow(enc, exampleResult.d, n_example);
            System.out.println("   Verification (42 -> encrypt -> decrypt): " + dec.equals(testMsg));
        } else {
            System.out.println("   Attack failed (key might not be vulnerable)");
        }
    }
    
    private static BigInteger sqrt(BigInteger n) {
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

