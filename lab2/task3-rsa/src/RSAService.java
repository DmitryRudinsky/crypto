import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAService {
    
    private KeyPair currentKeyPair;
    private final KeyGenerator keyGenerator;
    
    public enum PrimalityTestType {
        FERMAT,
        SOLOVAY_STRASSEN,
        MILLER_RABIN
    }
    
    public static class PublicKey {
        public final BigInteger n;
        public final BigInteger e;
        
        public PublicKey(BigInteger n, BigInteger e) {
            this.n = n;
            this.e = e;
        }
    }
    
    public static class PrivateKey {
        public final BigInteger n;
        public final BigInteger d;
        
        public PrivateKey(BigInteger n, BigInteger d) {
            this.n = n;
            this.d = d;
        }
    }
    
    public static class KeyPair {
        public final PublicKey publicKey;
        public final PrivateKey privateKey;
        
        public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }
    
    public class KeyGenerator {
        private final PrimalityTest primalityTest;
        private final double minProbability;
        private final int bitLength;
        private final SecureRandom random;
        
        public KeyGenerator(PrimalityTestType testType, double minProbability, int bitLength) {
            if (minProbability < 0.5 || minProbability >= 1.0) {
                throw new IllegalArgumentException("minProbability must be in [0.5, 1)");
            }
            if (bitLength < 8) {
                throw new IllegalArgumentException("bitLength must be at least 8");
            }
            
            this.primalityTest = createPrimalityTest(testType);
            this.minProbability = minProbability;
            this.bitLength = bitLength;
            this.random = new SecureRandom();
        }
        
        private PrimalityTest createPrimalityTest(PrimalityTestType testType) {
            switch (testType) {
                case FERMAT:
                    return new FermatTest();
                case SOLOVAY_STRASSEN:
                    return new SolovayStrassenTest();
                case MILLER_RABIN:
                    return new MillerRabinTest();
                default:
                    throw new IllegalArgumentException("Unknown test type");
            }
        }
        
        public KeyPair generateKeyPair() {
            BigInteger p = generatePrime();
            BigInteger q = generatePrime();
            
            while (!isSecureAgainstFermatAttack(p, q)) {
                q = generatePrime();
            }
            
            BigInteger n = p.multiply(q);
            BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
            
            BigInteger e = selectPublicExponent(phi, n);
            BigInteger d = computePrivateExponent(e, phi);
            
            while (!isSecureAgainstWienerAttack(e, d, n)) {
                e = selectPublicExponent(phi, n);
                d = computePrivateExponent(e, phi);
            }
            
            return new KeyPair(new PublicKey(n, e), new PrivateKey(n, d));
        }
        
        private BigInteger generatePrime() {
            BigInteger candidate;
            do {
                candidate = new BigInteger(bitLength, random);
                if (candidate.compareTo(BigInteger.TWO) < 0) {
                    continue;
                }
                if (!candidate.testBit(0)) {
                    candidate = candidate.add(BigInteger.ONE);
                }
            } while (!primalityTest.isProbablyPrime(candidate, minProbability));
            return candidate;
        }
        
        private boolean isSecureAgainstFermatAttack(BigInteger p, BigInteger q) {
            BigInteger diff = p.subtract(q).abs();
            BigInteger sum = p.add(q);
            BigInteger threshold = sum.divide(BigInteger.valueOf(3));
            return diff.compareTo(threshold) > 0;
        }
        
        private BigInteger selectPublicExponent(BigInteger phi, BigInteger n) {
            BigInteger e = new BigInteger("65537");
            
            if (e.compareTo(phi) >= 0 || !NumberTheoryService.gcd(e, phi).equals(BigInteger.ONE)) {
                e = BigInteger.valueOf(3);
                while (!NumberTheoryService.gcd(e, phi).equals(BigInteger.ONE)) {
                    e = e.add(BigInteger.TWO);
                }
            }
            
            return e;
        }
        
        private BigInteger computePrivateExponent(BigInteger e, BigInteger phi) {
            NumberTheoryService.ExtendedGcdResult result = NumberTheoryService.extendedGcd(e, phi);
            BigInteger d = result.x.mod(phi);
            if (d.compareTo(BigInteger.ZERO) < 0) {
                d = d.add(phi);
            }
            return d;
        }
        
        private boolean isSecureAgainstWienerAttack(BigInteger e, BigInteger d, BigInteger n) {
            BigInteger threshold = n.pow(1).divide(BigInteger.valueOf(3));
            BigInteger sqrtN = sqrt(n);
            threshold = sqrtN.sqrt();
            
            return d.compareTo(threshold) > 0;
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
    
    public RSAService(PrimalityTestType testType, double minProbability, int bitLength) {
        this.keyGenerator = new KeyGenerator(testType, minProbability, bitLength);
        this.currentKeyPair = keyGenerator.generateKeyPair();
    }
    
    public void regenerateKeyPair() {
        this.currentKeyPair = keyGenerator.generateKeyPair();
    }
    
    public PublicKey getPublicKey() {
        return currentKeyPair.publicKey;
    }
    
    public PrivateKey getPrivateKey() {
        return currentKeyPair.privateKey;
    }
    
    public BigInteger encrypt(BigInteger message, PublicKey publicKey) {
        if (message.compareTo(BigInteger.ZERO) < 0 || message.compareTo(publicKey.n) >= 0) {
            throw new IllegalArgumentException("Message must be in range [0, n)");
        }
        return NumberTheoryService.modPow(message, publicKey.e, publicKey.n);
    }
    
    public BigInteger decrypt(BigInteger ciphertext, PrivateKey privateKey) {
        if (ciphertext.compareTo(BigInteger.ZERO) < 0 || ciphertext.compareTo(privateKey.n) >= 0) {
            throw new IllegalArgumentException("Ciphertext must be in range [0, n)");
        }
        return NumberTheoryService.modPow(ciphertext, privateKey.d, privateKey.n);
    }
    
    public BigInteger encrypt(BigInteger message) {
        return encrypt(message, currentKeyPair.publicKey);
    }
    
    public BigInteger decrypt(BigInteger ciphertext) {
        return decrypt(ciphertext, currentKeyPair.privateKey);
    }
    
    public byte[] encrypt(byte[] data, PublicKey publicKey) {
        BigInteger message = new BigInteger(1, data);
        if (message.compareTo(publicKey.n) >= 0) {
            throw new IllegalArgumentException("Data too large for key size");
        }
        BigInteger encrypted = encrypt(message, publicKey);
        return encrypted.toByteArray();
    }
    
    public byte[] decrypt(byte[] data, PrivateKey privateKey) {
        BigInteger ciphertext = new BigInteger(data);
        BigInteger decrypted = decrypt(ciphertext, privateKey);
        byte[] result = decrypted.toByteArray();
        
        if (result[0] == 0 && result.length > 1) {
            byte[] tmp = new byte[result.length - 1];
            System.arraycopy(result, 1, tmp, 0, tmp.length);
            result = tmp;
        }
        
        return result;
    }
    
    public byte[] encrypt(byte[] data) {
        return encrypt(data, currentKeyPair.publicKey);
    }
    
    public byte[] decrypt(byte[] data) {
        return decrypt(data, currentKeyPair.privateKey);
    }
}

