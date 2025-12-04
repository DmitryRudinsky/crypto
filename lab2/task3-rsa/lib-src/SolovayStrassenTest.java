import java.math.BigInteger;

public class SolovayStrassenTest extends AbstractPrimalityTest {
    
    @Override
    protected boolean performSingleTest(BigInteger n) {
        BigInteger a = generateRandomWitness(n);
        
        int jacobi = NumberTheoryService.jacobiSymbol(a, n);
        
        if (jacobi == 0) {
            return false;
        }
        
        BigInteger exponent = n.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        BigInteger modResult = NumberTheoryService.modPow(a, exponent, n);
        
        BigInteger jacobiMod = BigInteger.valueOf(jacobi).mod(n);
        if (jacobiMod.compareTo(BigInteger.ZERO) < 0) {
            jacobiMod = jacobiMod.add(n);
        }
        
        return modResult.equals(jacobiMod);
    }
    
    @Override
    protected double getSingleTestErrorProbability() {
        return 0.5;
    }
}

