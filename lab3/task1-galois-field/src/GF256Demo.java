import java.util.List;

public class GF256Demo {
    
    public static void main(String[] args) {
        System.out.println("=== GF(2^8) Operations Demo ===\n");
        
        byte a = (byte) 0x53;
        byte b = (byte) 0xCA;
        
        System.out.println("Addition (XOR):");
        System.out.printf("0x%02X + 0x%02X = 0x%02X\n\n", a & 0xFF, b & 0xFF, GF256Service.add(a, b) & 0xFF);
        
        byte modulus = (byte) 0x1B;
        
        System.out.println("Checking if 0x11B is irreducible:");
        System.out.println(GF256Service.isIrreducible(0x11B) + "\n");
        
        try {
            System.out.println("Multiplication modulo 0x11B:");
            byte product = GF256Service.multiplyMod(a, b, modulus);
            System.out.printf("0x%02X * 0x%02X mod 0x%02X = 0x%02X\n\n", a & 0xFF, b & 0xFF, modulus & 0xFF, product & 0xFF);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage() + "\n");
        }
        
        try {
            System.out.println("Finding inverse:");
            byte inverse = GF256Service.inverse(a, modulus);
            System.out.printf("Inverse of 0x%02X mod 0x%02X = 0x%02X\n", a & 0xFF, modulus & 0xFF, inverse & 0xFF);
            
            byte check = GF256Service.multiplyMod(a, inverse, modulus);
            System.out.printf("Verification: 0x%02X * 0x%02X mod 0x%02X = 0x%02X\n\n", a & 0xFF, inverse & 0xFF, modulus & 0xFF, check & 0xFF);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage() + "\n");
        }
        
        System.out.println("All irreducible polynomials of degree 8:");
        List<Byte> irreducibles = GF256Service.getAllIrreduciblePolynomials();
        System.out.println("Count: " + irreducibles.size());
        System.out.print("Polynomials: ");
        for (int i = 0; i < Math.min(10, irreducibles.size()); i++) {
            System.out.printf("0x%02X ", irreducibles.get(i) & 0xFF);
        }
        System.out.println("...\n");
        
        int polyToFactor = 0x1E2F;
        System.out.printf("Factorizing polynomial 0x%04X:\n", polyToFactor);
        List<Byte> factors = GF256Service.factorize(polyToFactor);
        System.out.print("Factors: ");
        for (byte factor : factors) {
            System.out.printf("0x%02X ", factor & 0xFF);
        }
        System.out.println();
        
        System.out.println("\n=== Testing with reducible modulus ===");
        byte reducibleMod = (byte) 0x1A;
        System.out.printf("Is 0x11A irreducible? %b\n", GF256Service.isIrreducible(0x11A));
        
        try {
            GF256Service.multiplyMod(a, b, reducibleMod);
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error caught: " + e.getMessage());
        }
    }
}

