public class Demo {
    public static void main(String[] args) {
        System.out.println("=== Galois Field GF(2^8) Operations Demo ===\n");
        
        byte a = (byte) 0x57;
        byte b = (byte) 0x83;
        byte modulus = (byte) 0x11B;
        
        System.out.println("1. Addition in GF(2^8)");
        System.out.println("   -------------------");
        System.out.println("   a = 0x" + String.format("%02X", a & 0xFF));
        System.out.println("   b = 0x" + String.format("%02X", b & 0xFF));
        byte sum = GaloisFieldService.add(a, b);
        System.out.println("   a + b = 0x" + String.format("%02X", sum & 0xFF));
        System.out.println();
        
        System.out.println("2. Multiplication in GF(2^8)");
        System.out.println("   -------------------------");
        System.out.println("   a = 0x" + String.format("%02X", a & 0xFF));
        System.out.println("   b = 0x" + String.format("%02X", b & 0xFF));
        System.out.println("   modulus = 0x" + String.format("%02X", modulus & 0xFF));
        byte product = GaloisFieldService.multiply(a, b, modulus);
        System.out.println("   a * b mod m = 0x" + String.format("%02X", product & 0xFF));
        System.out.println();
        
        System.out.println("3. Multiplicative Inverse");
        System.out.println("   ----------------------");
        System.out.println("   a = 0x" + String.format("%02X", a & 0xFF));
        System.out.println("   modulus = 0x" + String.format("%02X", modulus & 0xFF));
        byte inv = GaloisFieldService.inverse(a, modulus);
        System.out.println("   a^(-1) = 0x" + String.format("%02X", inv & 0xFF));
        
        byte verification = GaloisFieldService.multiply(a, inv, modulus);
        System.out.println("   Verification: a * a^(-1) = 0x" + String.format("%02X", verification & 0xFF));
        System.out.println();
        
        System.out.println("4. Checking Irreducibility");
        System.out.println("   -----------------------");
        byte[] testPolynomials = {(byte) 0x11B, (byte) 0x11D, (byte) 0x120, (byte) 0x12D};
        for (byte poly : testPolynomials) {
            boolean irreducible = GaloisFieldService.isIrreducible(poly);
            System.out.println("   0x" + String.format("%02X", poly & 0xFF) + " is " + 
                             (irreducible ? "irreducible" : "reducible"));
        }
        System.out.println();
        
        System.out.println("5. All Irreducible Polynomials of Degree 8");
        System.out.println("   ----------------------------------------");
        byte[] irreducibles = GaloisFieldService.getAllIrreduciblePolynomials();
        System.out.println("   Count: " + irreducibles.length);
        System.out.println("   First 10:");
        for (int i = 0; i < 10; i++) {
            System.out.print("   0x" + String.format("%02X", irreducibles[i] & 0xFF));
            if (i < 9) System.out.print(", ");
            if ((i + 1) % 5 == 0) System.out.println();
        }
        System.out.println("\n   All 30 irreducible polynomials:");
        System.out.print("   ");
        for (int i = 0; i < irreducibles.length; i++) {
            System.out.print("0x" + String.format("%02X", irreducibles[i] & 0xFF));
            if (i < irreducibles.length - 1) System.out.print(", ");
            if ((i + 1) % 6 == 0 && i < irreducibles.length - 1) System.out.print("\n   ");
        }
        System.out.println("\n");
        
        System.out.println("6. Polynomial Factorization");
        System.out.println("   ------------------------");
        int[] polynomialsToFactor = {0x11F, 0x18F, 0x1C3};
        for (int poly : polynomialsToFactor) {
            System.out.print("   0x" + String.format("%03X", poly) + " = ");
            int[] factors = GaloisFieldService.factorize(poly);
            for (int i = 0; i < factors.length; i++) {
                System.out.print("0x" + String.format("%X", factors[i]));
                if (i < factors.length - 1) System.out.print(" * ");
            }
            System.out.println();
        }
        System.out.println();
        
        System.out.println("7. AES Modulus (Rijndael)");
        System.out.println("   ----------------------");
        byte aesModulus = (byte) 0x11B;
        System.out.println("   Standard AES modulus: 0x" + String.format("%02X", aesModulus & 0xFF));
        System.out.println("   Is irreducible: " + GaloisFieldService.isIrreducible(aesModulus));
        System.out.println();
        
        System.out.println("8. Operations with Different Elements");
        System.out.println("   ----------------------------------");
        byte[] elements = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x53};
        for (byte elem : elements) {
            byte inv_elem = GaloisFieldService.inverse(elem, aesModulus);
            byte check = GaloisFieldService.multiply(elem, inv_elem, aesModulus);
            System.out.println("   0x" + String.format("%02X", elem & 0xFF) + 
                             "^(-1) = 0x" + String.format("%02X", inv_elem & 0xFF) +
                             ", verification: 0x" + String.format("%02X", check & 0xFF));
        }
    }
}

