import java.io.*;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Arrays;

public class RijndaelFileDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Rijndael File Encryption Demo ===\n");
        
        try {
            testTextFile();
            testBinaryFile();
            testDifferentBlockSizes();
            testDifferentKeySizes();
            testDifferentModes();
            testDifferentPaddings();
            testDifferentModuli();
            testLargeRandomData();
            
            System.out.println("\n=== All tests completed successfully ===");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testTextFile() throws Exception {
        System.out.println("--- Testing Text File Encryption ---");
        
        byte[] key = generateKey(16);
        byte[] iv = generateIV(16);
        
        Rijndael cipher = new Rijndael(128, 128, (byte) 0x1B);
        CipherContext ctx = new CipherContext(cipher, key, CipherMode.CBC, PaddingMode.PKCS7, 16, iv);
        
        String inputPath = "test-files/test.txt";
        String encPath = "test-files/test.txt.enc";
        String decPath = "test-files/test.txt.dec";
        
        encryptFile(inputPath, encPath, ctx);
        decryptFile(encPath, decPath, ctx);
        
        boolean match = compareFiles(inputPath, decPath);
        System.out.println("Text file encryption/decryption: " + (match ? "PASS" : "FAIL"));
        
        ctx.shutdown();
        System.out.println();
    }
    
    private static void testBinaryFile() throws Exception {
        System.out.println("--- Testing Binary File Encryption ---");
        
        byte[] key = generateKey(16);
        byte[] iv = generateIV(16);
        
        Rijndael cipher = new Rijndael(128, 128, (byte) 0x1B);
        CipherContext ctx = new CipherContext(cipher, key, CipherMode.CBC, PaddingMode.PKCS7, 16, iv);
        
        String inputPath = "test-files/random.bin";
        String encPath = "test-files/random.bin.enc";
        String decPath = "test-files/random.bin.dec";
        
        encryptFile(inputPath, encPath, ctx);
        decryptFile(encPath, decPath, ctx);
        
        boolean match = compareFiles(inputPath, decPath);
        System.out.println("Binary file encryption/decryption: " + (match ? "PASS" : "FAIL"));
        
        ctx.shutdown();
        System.out.println();
    }
    
    private static void testDifferentBlockSizes() throws Exception {
        System.out.println("--- Testing Different Block Sizes ---");
        
        int[] blockSizes = {128, 192, 256};
        
        for (int blockSize : blockSizes) {
            byte[] key = generateKey(16);
            byte[] iv = generateIV(blockSize / 8);
            
            Rijndael cipher = new Rijndael(blockSize, 128, (byte) 0x1B);
            CipherContext ctx = new CipherContext(cipher, key, CipherMode.CBC, PaddingMode.PKCS7, blockSize / 8, iv);
            
            byte[] data = generateRandomData(1024);
            byte[] encrypted = encryptData(data, ctx);
            byte[] decrypted = decryptData(encrypted, ctx);
            
            boolean match = Arrays.equals(data, decrypted);
            System.out.println("Block size " + blockSize + " bits: " + (match ? "PASS" : "FAIL"));
            
            ctx.shutdown();
        }
        System.out.println();
    }
    
    private static void testDifferentKeySizes() throws Exception {
        System.out.println("--- Testing Different Key Sizes ---");
        
        int[] keySizes = {128, 192, 256};
        
        for (int keySize : keySizes) {
            byte[] key = generateKey(keySize / 8);
            byte[] iv = generateIV(16);
            
            Rijndael cipher = new Rijndael(128, keySize, (byte) 0x1B);
            CipherContext ctx = new CipherContext(cipher, key, CipherMode.CBC, PaddingMode.PKCS7, 16, iv);
            
            byte[] data = generateRandomData(1024);
            byte[] encrypted = encryptData(data, ctx);
            byte[] decrypted = decryptData(encrypted, ctx);
            
            boolean match = Arrays.equals(data, decrypted);
            System.out.println("Key size " + keySize + " bits: " + (match ? "PASS" : "FAIL"));
            
            ctx.shutdown();
        }
        System.out.println();
    }
    
    private static void testDifferentModes() throws Exception {
        System.out.println("--- Testing Different Cipher Modes ---");
        
        CipherMode[] modes = {CipherMode.ECB, CipherMode.CBC, CipherMode.PCBC, 
                              CipherMode.CFB, CipherMode.OFB, CipherMode.CTR};
        
        for (CipherMode mode : modes) {
            byte[] key = generateKey(16);
            byte[] iv = mode.requiresIV() ? generateIV(16) : null;
            
            Rijndael cipher = new Rijndael(128, 128, (byte) 0x1B);
            CipherContext ctx = new CipherContext(cipher, key, mode, PaddingMode.PKCS7, 16, iv);
            
            byte[] data = generateRandomData(1024);
            byte[] encrypted = encryptData(data, ctx);
            byte[] decrypted = decryptData(encrypted, ctx);
            
            boolean match = Arrays.equals(data, decrypted);
            System.out.println(mode + ": " + (match ? "PASS" : "FAIL"));
            
            ctx.shutdown();
        }
        System.out.println();
    }
    
    private static void testDifferentPaddings() throws Exception {
        System.out.println("--- Testing Different Padding Modes ---");
        
        PaddingMode[] paddings = {PaddingMode.ZEROS, PaddingMode.PKCS7, 
                                  PaddingMode.ANSI_X923, PaddingMode.ISO_10126};
        
        for (PaddingMode padding : paddings) {
            byte[] key = generateKey(16);
            byte[] iv = generateIV(16);
            
            Rijndael cipher = new Rijndael(128, 128, (byte) 0x1B);
            CipherContext ctx = new CipherContext(cipher, key, CipherMode.CBC, padding, 16, iv);
            
            byte[] data = generateRandomData(1024);
            byte[] encrypted = encryptData(data, ctx);
            byte[] decrypted = decryptData(encrypted, ctx);
            
            boolean match = Arrays.equals(data, decrypted);
            System.out.println(padding + ": " + (match ? "PASS" : "FAIL"));
            
            ctx.shutdown();
        }
        System.out.println();
    }
    
    private static void testDifferentModuli() throws Exception {
        System.out.println("--- Testing Different GF(2^8) Moduli ---");
        
        byte[] moduli = {(byte) 0x1B, (byte) 0x1D, (byte) 0x2B, (byte) 0x2D};
        
        for (byte modulus : moduli) {
            byte[] key = generateKey(16);
            byte[] iv = generateIV(16);
            
            Rijndael cipher = new Rijndael(128, 128, modulus);
            CipherContext ctx = new CipherContext(cipher, key, CipherMode.CBC, PaddingMode.PKCS7, 16, iv);
            
            byte[] data = generateRandomData(1024);
            byte[] encrypted = encryptData(data, ctx);
            byte[] decrypted = decryptData(encrypted, ctx);
            
            boolean match = Arrays.equals(data, decrypted);
            System.out.printf("Modulus 0x%02X: %s\n", (modulus & 0xFF) | 0x100, match ? "PASS" : "FAIL");
            
            ctx.shutdown();
        }
        System.out.println();
    }
    
    private static void testLargeRandomData() throws Exception {
        System.out.println("--- Testing Large Random Data ---");
        
        int[] sizes = {1024, 10240, 102400};
        
        for (int size : sizes) {
            byte[] key = generateKey(16);
            byte[] iv = generateIV(16);
            
            Rijndael cipher = new Rijndael(128, 128, (byte) 0x1B);
            CipherContext ctx = new CipherContext(cipher, key, CipherMode.CBC, PaddingMode.PKCS7, 16, iv);
            
            byte[] data = generateRandomData(size);
            byte[] encrypted = encryptData(data, ctx);
            byte[] decrypted = decryptData(encrypted, ctx);
            
            boolean match = Arrays.equals(data, decrypted);
            System.out.println(size + " bytes: " + (match ? "PASS" : "FAIL"));
            
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
    
    private static void encryptFile(String inputPath, String outputPath, CipherContext ctx) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get(inputPath));
        byte[][] result = new byte[1][];
        ctx.encryptAsync(data, result).join();
        Files.write(Paths.get(outputPath), result[0]);
    }
    
    private static void decryptFile(String inputPath, String outputPath, CipherContext ctx) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get(inputPath));
        byte[][] result = new byte[1][];
        ctx.decryptAsync(data, result).join();
        Files.write(Paths.get(outputPath), result[0]);
    }
    
    private static byte[] encryptData(byte[] data, CipherContext ctx) throws Exception {
        byte[][] result = new byte[1][];
        ctx.encryptAsync(data, result).join();
        return result[0];
    }
    
    private static byte[] decryptData(byte[] data, CipherContext ctx) throws Exception {
        byte[][] result = new byte[1][];
        ctx.decryptAsync(data, result).join();
        return result[0];
    }
    
    private static boolean compareFiles(String path1, String path2) throws Exception {
        byte[] data1 = Files.readAllBytes(Paths.get(path1));
        byte[] data2 = Files.readAllBytes(Paths.get(path2));
        return Arrays.equals(data1, data2);
    }
}

