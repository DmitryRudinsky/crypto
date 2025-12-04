import java.math.BigInteger;
import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=== RSA Encryption/Decryption Demo ===\n");
        
        System.out.println("1. Creating RSA Service with Miller-Rabin test");
        System.out.println("   Bit length: 512");
        System.out.println("   Min probability: 0.999");
        RSAService rsa = new RSAService(RSAService.PrimalityTestType.MILLER_RABIN, 0.999, 512);
        
        RSAService.PublicKey publicKey = rsa.getPublicKey();
        RSAService.PrivateKey privateKey = rsa.getPrivateKey();
        
        System.out.println("\n2. Generated Key Pair:");
        System.out.println("   n (modulus): " + publicKey.n);
        System.out.println("   e (public exponent): " + publicKey.e);
        System.out.println("   d (private exponent): " + privateKey.d);
        System.out.println("   Bit length of n: " + publicKey.n.bitLength());
        
        System.out.println("\n3. Encrypting and Decrypting BigInteger Messages:");
        System.out.println("   -----------------------------------------------");
        
        BigInteger[] messages = {
            new BigInteger("42"),
            new BigInteger("12345"),
            new BigInteger("999999"),
            new BigInteger("123456789012345")
        };
        
        for (BigInteger message : messages) {
            BigInteger encrypted = rsa.encrypt(message);
            BigInteger decrypted = rsa.decrypt(encrypted);
            
            System.out.println("   Original:  " + message);
            System.out.println("   Encrypted: " + encrypted);
            System.out.println("   Decrypted: " + decrypted);
            System.out.println("   Match: " + message.equals(decrypted));
            System.out.println();
        }
        
        System.out.println("4. Encrypting and Decrypting Byte Arrays:");
        System.out.println("   ---------------------------------------");
        
        String[] textMessages = {
            "Hello, RSA!",
            "Cryptography is awesome",
            "12345"
        };
        
        for (String text : textMessages) {
            byte[] original = text.getBytes();
            byte[] encrypted = rsa.encrypt(original);
            byte[] decrypted = rsa.decrypt(encrypted);
            
            System.out.println("   Original:  " + text);
            System.out.println("   Encrypted (hex): " + bytesToHex(encrypted));
            System.out.println("   Decrypted: " + new String(decrypted));
            System.out.println("   Match: " + Arrays.equals(original, decrypted));
            System.out.println();
        }
        
        System.out.println("5. Testing Key Regeneration:");
        System.out.println("   --------------------------");
        
        RSAService.PublicKey oldPublicKey = rsa.getPublicKey();
        System.out.println("   Old public key (n): " + oldPublicKey.n);
        
        rsa.regenerateKeyPair();
        
        RSAService.PublicKey newPublicKey = rsa.getPublicKey();
        System.out.println("   New public key (n): " + newPublicKey.n);
        System.out.println("   Keys are different: " + !oldPublicKey.n.equals(newPublicKey.n));
        
        BigInteger testMessage = new BigInteger("777");
        BigInteger encrypted = rsa.encrypt(testMessage);
        BigInteger decrypted = rsa.decrypt(encrypted);
        System.out.println("   New key works correctly: " + testMessage.equals(decrypted));
        
        System.out.println("\n6. Testing with Different Primality Tests:");
        System.out.println("   ----------------------------------------");
        
        RSAService.PrimalityTestType[] testTypes = {
            RSAService.PrimalityTestType.FERMAT,
            RSAService.PrimalityTestType.SOLOVAY_STRASSEN,
            RSAService.PrimalityTestType.MILLER_RABIN
        };
        
        for (RSAService.PrimalityTestType testType : testTypes) {
            RSAService service = new RSAService(testType, 0.99, 256);
            BigInteger msg = new BigInteger("123456");
            BigInteger enc = service.encrypt(msg);
            BigInteger dec = service.decrypt(enc);
            
            System.out.println("   Test: " + testType);
            System.out.println("   Key bit length: " + service.getPublicKey().n.bitLength());
            System.out.println("   Encryption/Decryption works: " + msg.equals(dec));
            System.out.println();
        }
        
        System.out.println("7. Large Message Test:");
        System.out.println("   -------------------");
        
        RSAService largeRsa = new RSAService(RSAService.PrimalityTestType.MILLER_RABIN, 0.999, 1024);
        BigInteger largeMessage = new BigInteger("987654321098765432109876543210");
        BigInteger largeEncrypted = largeRsa.encrypt(largeMessage);
        BigInteger largeDecrypted = largeRsa.decrypt(largeEncrypted);
        
        System.out.println("   Message: " + largeMessage);
        System.out.println("   Decrypted: " + largeDecrypted);
        System.out.println("   Match: " + largeMessage.equals(largeDecrypted));
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, 32); i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        if (bytes.length > 32) {
            sb.append("...");
        }
        return sb.toString();
    }
}

