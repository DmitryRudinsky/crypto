import java.math.BigInteger;
import java.security.SecureRandom;

public abstract class AbstractPrimalityTest implements PrimalityTest {
    
    protected final SecureRandom random;
    
    public AbstractPrimalityTest() {
        this.random = new SecureRandom();
    }
    
    @Override
    public boolean isProbablyPrime(BigInteger n, double minProbability) {
        if (minProbability < 0.5 || minProbability >= 1.0) {
            throw new IllegalArgumentException("minProbability must be in range [0.5, 1)");
        }
        
        if (n.compareTo(BigInteger.TWO) < 0) {
            return false;
        }
        
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) {
            return true;
        }
        
        if (!n.testBit(0)) {
            return false;
        }
        
        int k = calculateIterations(minProbability);
        
        for (int i = 0; i < k; i++) {
            if (!performSingleTest(n)) {
                return false;
            }
        }
        
        return true;
    }
    
    protected abstract boolean performSingleTest(BigInteger n);
    
    protected int calculateIterations(double minProbability) {
        double errorProbability = 1.0 - minProbability;
        double singleTestErrorProbability = getSingleTestErrorProbability();
        return (int) Math.ceil(Math.log(errorProbability) / Math.log(singleTestErrorProbability));
    }
    
    protected abstract double getSingleTestErrorProbability();
    
    protected BigInteger generateRandomWitness(BigInteger n) {
        BigInteger witness;
        do {
            witness = new BigInteger(n.bitLength(), random);
        } while (witness.compareTo(BigInteger.TWO) < 0 || witness.compareTo(n) >= 0);
        return witness;
    }
}

