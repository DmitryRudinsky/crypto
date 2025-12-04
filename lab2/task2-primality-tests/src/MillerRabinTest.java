import java.math.BigInteger;

public class MillerRabinTest extends AbstractPrimalityTest {
    
    @Override
    protected boolean performSingleTest(BigInteger n) {
        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        
        int s = 0;
        BigInteger d = nMinusOne;
        while (!d.testBit(0)) {
            s++;
            d = d.shiftRight(1);
        }
        
        BigInteger a = generateRandomWitness(n);
        BigInteger x = NumberTheoryService.modPow(a, d, n);
        
        if (x.equals(BigInteger.ONE) || x.equals(nMinusOne)) {
            return true;
        }
        
        for (int i = 0; i < s - 1; i++) {
            x = x.multiply(x).mod(n);
            
            if (x.equals(nMinusOne)) {
                return true;
            }
            
            if (x.equals(BigInteger.ONE)) {
                return false;
            }
        }
        
        return false;
    }
    
    @Override
    protected double getSingleTestErrorProbability() {
        return 0.25;
    }
}

