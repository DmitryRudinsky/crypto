import java.io.*;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Arrays;

public class DetailedDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Detailed Rijndael Encryption Demo ===\n");
        
        try {
            demonstrateModesComparison();
            demonstratePaddingComparison();
            demonstrateModulusImpact();
            demonstrateBlockKeyVariations();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void demonstrateModesComparison() throws Exception {
        System.out.println("--- Cipher Modes Comparison ---");
        System.out.println("Encrypting same plaintext with different modes\n");
        
        byte[] plaintext = "Hello World! This is a test message.".getBytes();
        byte[] key = generateKey(16);
        byte[] iv = generateIV(16);
        
        System.out.println("Plaintext: " + new String(plaintext));
        System.out.println("Plaintext hex: " + bytesToHex(plaintext));
        System.out.println();
        
        CipherMode[] modes = {CipherMode.ECB, CipherMode.CBC, CipherMode.CTR};
        
        for (CipherMode mode : modes) {
            byte[] modeIV = mode.requiresIV() ? iv : null;
            
            Rijndael cipher = new Rijndael(128, 128, (byte) 0x1B);
            CipherContext ctx = new CipherContext(cipher, key, mode, PaddingMode.PKCS7, 16, modeIV);
            
            byte[][] result = new byte[1][];
            ctx.encryptAsync(plaintext, result).join();
            byte[] ciphertext = result[0];
            
            System.out.println(mode + " ciphertext: " + bytesToHex(ciphertext));
            
            ctx.shutdown();
        }
        System.out.println();
    }
    
    private static void demonstratePaddingComparison() throws Exception {
        System.out.println("--- Padding Modes Comparison ---");
        System.out.println("Same plaintext with different padding schemes\n");
        
        byte[] plaintext = "Test".getBytes();
        byte[] key = generateKey(16);
        byte[] iv = generateIV(16);
        
        System.out.println("Plaintext: " + new String(plaintext) + " (" + plaintext.length + " bytes)");
        System.out.println();
        
        PaddingMode[] paddings = {PaddingMode.ZEROS, PaddingMode.PKCS7, PaddingMode.ANSI_X923};
        
        for (PaddingMode padding : paddings) {
            Rijndael cipher = new Rijndael(128, 128, (byte) 0x1B);
            CipherContext ctx = new CipherContext(cipher, key, CipherMode.ECB, padding, 16, null);
            
            byte[][] result = new byte[1][];
            ctx.encryptAsync(plaintext, result).join();
            byte[] ciphertext = result[0];
            
            System.out.println(padding + " - ciphertext length: " + ciphertext.length + " bytes");
            System.out.println("  Ciphertext: " + bytesToHex(ciphertext));
            
            ctx.shutdown();
        }
        System.out.println();
    }
    
    private static void demonstrateModulusImpact() throws Exception {
        System.out.println("--- Different GF(2^8) Moduli Impact ---");
        System.out.println("Same plaintext/key with different field polynomials\n");
        
        byte[] plaintext = "Modulus test".getBytes();
        byte[] key = generateKey(16);
        byte[] iv = generateIV(16);
        
        System.out.println("Plaintext: " + new String(plaintext));
        System.out.println();
        
        byte[] moduli = {(byte) 0x1B, (byte) 0x1D, (byte) 0x2B};
        
        for (byte modulus : moduli) {
            Rijndael cipher = new Rijndael(128, 128, modulus);
            CipherContext ctx = new CipherContext(cipher, key, CipherMode.CBC, PaddingMode.PKCS7, 16, iv);
            
            byte[][] result = new byte[1][];
            ctx.encryptAsync(plaintext, result).join();
            byte[] ciphertext = result[0];
            
            System.out.printf("Modulus 0x%03X: %s\n", (modulus & 0xFF) | 0x100, bytesToHex(ciphertext));
            
            ctx.shutdown();
        }
        System.out.println();
    }
    
    private static void demonstrateBlockKeyVariations() throws Exception {
        System.out.println("--- Block Size and Key Size Variations ---");
        System.out.println();
        
        byte[] plaintext = generateRandomData(48);
        
        int[][] configs = {{128, 128}, {128, 192}, {128, 256}, 
                          {192, 192}, {256, 256}};
        
        for (int[] config : configs) {
            int blockSize = config[0];
            int keySize = config[1];
            
            byte[] key = generateKey(keySize / 8);
            byte[] iv = generateIV(blockSize / 8);
            
            Rijndael cipher = new Rijndael(blockSize, keySize, (byte) 0x1B);
            CipherContext ctx = new CipherContext(cipher, key, CipherMode.CBC, PaddingMode.PKCS7, blockSize / 8, iv);
            
            byte[][] result = new byte[1][];
            ctx.encryptAsync(plaintext, result).join();
            byte[] ciphertext = result[0];
            
            ctx.decryptAsync(ciphertext, result).join();
            byte[] decrypted = result[0];
            
            boolean match = Arrays.equals(plaintext, decrypted);
            
            System.out.printf("Block: %3d bits, Key: %3d bits - Encrypted: %3d bytes - %s\n",
                blockSize, keySize, ciphertext.length, match ? "PASS" : "FAIL");
            
            ctx.shutdown();
        }
        System.out.println();
    }
    
    private static byte[] generateKey(int size) {
        byte[] key = new byte[size];
        new SecureRandom().nextBytes(key);
        return key;
    }
    
    private static byte[] generateIV(int size) {
        byte[] iv = new byte[size];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    
    private static byte[] generateRandomData(int size) {
        byte[] data = new byte[size];
        new SecureRandom().nextBytes(data);
        return data;
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(32, bytes.length);
        for (int i = 0; i < limit; i++) {
            sb.append(String.format("%02X", bytes[i] & 0xFF));
            if ((i + 1) % 16 == 0 && i < limit - 1) {
                sb.append("\n");
                sb.append("                  ");
            } else if (i < limit - 1) {
                sb.append(" ");
            }
        }
        if (bytes.length > 32) {
            sb.append(" ...");
        }
        return sb.toString();
    }
}

