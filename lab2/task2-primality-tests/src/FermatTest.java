import java.math.BigInteger;

public class FermatTest extends AbstractPrimalityTest {
    
    @Override
    protected boolean performSingleTest(BigInteger n) {
        BigInteger a = generateRandomWitness(n);
        BigInteger result = NumberTheoryService.modPow(a, n.subtract(BigInteger.ONE), n);
        return result.equals(BigInteger.ONE);
    }
    
    @Override
    protected double getSingleTestErrorProbability() {
        return 0.5;
    }
}

