import java.util.Arrays;

public class DESDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Демонстрация DES ===\n");

        demoBasicDES();
        demoDESWithContext();
        demoDESVectors();

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demoBasicDES() {
        System.out.println("Пример 1: Базовое использование DES");
        
        DES des = new DES();
        
        byte[] key = {(byte)0x13, (byte)0x34, (byte)0x57, (byte)0x79, 
                      (byte)0x9B, (byte)0xBC, (byte)0xDF, (byte)0xF1};
        des.setEncryptionKey(key);
        des.setDecryptionKey(key);

        byte[] plaintext = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
                           (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF};
        
        System.out.println("Ключ:        " + toHex(key));
        System.out.println("Открытый:    " + toHex(plaintext));

        byte[] encrypted = des.encrypt(plaintext);
        System.out.println("Зашифрован:  " + toHex(encrypted));

        byte[] decrypted = des.decrypt(encrypted);
        System.out.println("Расшифрован: " + toHex(decrypted));
        
        if (Arrays.equals(plaintext, decrypted)) {
            System.out.println("✓ Успешно!");
        }
        System.out.println();
    }

    private static void demoDESWithContext() {
        System.out.println("Пример 2: DES с CipherContext (CBC режим)");
        
        DES des = new DES();
        
        byte[] key = {(byte)0x0F, (byte)0x1E, (byte)0x2D, (byte)0x3C,
                      (byte)0x4B, (byte)0x5A, (byte)0x69, (byte)0x78};
        byte[] iv = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                     (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};

        CipherContext ctx = new CipherContext(
            des,
            key,
            CipherMode.CBC,
            PaddingMode.PKCS7,
            8,
            iv
        );

        String message = "DES encryption demonstration!";
        System.out.println("Сообщение: " + message);

        try {
            byte[][] encryptedResult = new byte[1][];
            ctx.encryptAsync(message.getBytes(), encryptedResult).join();
            byte[] encrypted = encryptedResult[0];
            System.out.println("Зашифровано (CBC): " + toHex(encrypted));

            byte[][] decryptedResult = new byte[1][];
            ctx.decryptAsync(encrypted, decryptedResult).join();
            byte[] decrypted = decryptedResult[0];
            System.out.println("Расшифровано: " + new String(decrypted));
            
            if (message.equals(new String(decrypted))) {
                System.out.println("✓ Успешно!");
            }
        } finally {
            ctx.shutdown();
        }
        System.out.println();
    }

    private static void demoDESVectors() {
        System.out.println("Пример 3: Известные тестовые векторы DES");
        
        DES des = new DES();
        
        TestVector[] vectors = {
            new TestVector(
                "Нулевой ключ и текст",
                new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}
            ),
            new TestVector(
                "Простой ключ",
                new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, 
                          (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF},
                new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
                          (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF}
            ),
            new TestVector(
                "Смешанный ключ",
                new byte[]{(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
                          (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF},
                new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01}
            )
        };
        
        for (TestVector vector : vectors) {
            System.out.println("Вектор: " + vector.description);
            System.out.println("  Ключ:  " + toHex(vector.key));
            System.out.println("  Текст: " + toHex(vector.plaintext));
            
            des.setEncryptionKey(vector.key);
            des.setDecryptionKey(vector.key);
            
            byte[] encrypted = des.encrypt(vector.plaintext);
            byte[] decrypted = des.decrypt(encrypted);
            
            System.out.println("  Шифр:  " + toHex(encrypted));
            
            boolean success = Arrays.equals(vector.plaintext, decrypted);
            System.out.println("  " + (success ? "✓" : "✗"));
            System.out.println();
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }
    
    private static class TestVector {
        String description;
        byte[] key;
        byte[] plaintext;
        
        TestVector(String description, byte[] key, byte[] plaintext) {
            this.description = description;
            this.key = key;
            this.plaintext = plaintext;
        }
    }
}

