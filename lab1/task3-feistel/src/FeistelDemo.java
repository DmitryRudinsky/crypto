import java.util.Arrays;

public class FeistelDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Демонстрация сети Фейстеля ===\n");

        demoBasicFeistel();
        demoFeistelWithContext();
        demoFeistelRounds();

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demoBasicFeistel() {
        System.out.println("Пример 1: Базовое использование сети Фейстеля");
        
        KeySchedule keySchedule = new SimpleKeySchedule(4);
        RoundFunction roundFunction = new SimpleRoundFunction();
        
        FeistelCipher feistel = new FeistelCipher(keySchedule, roundFunction);
        
        byte[] key = {0x01, 0x02, 0x03, 0x04};
        feistel.setEncryptionKey(key);
        feistel.setDecryptionKey(key);

        byte[] plaintext = {0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x21, 0x21, 0x21};
        System.out.println("Исходный текст: " + new String(plaintext));
        System.out.println("Исходные байты: " + toHex(plaintext));

        byte[] encrypted = feistel.encrypt(plaintext);
        System.out.println("Зашифровано:    " + toHex(encrypted));

        byte[] decrypted = feistel.decrypt(encrypted);
        System.out.println("Расшифровано:   " + toHex(decrypted));
        System.out.println("Результат:      " + new String(decrypted));
        
        if (Arrays.equals(plaintext, decrypted)) {
            System.out.println("✓ Успешно!");
        }
        System.out.println();
    }

    private static void demoFeistelWithContext() {
        System.out.println("Пример 2: Сеть Фейстеля с CipherContext");
        
        KeySchedule keySchedule = new SimpleKeySchedule(8);
        RoundFunction roundFunction = new SimpleRoundFunction();
        FeistelCipher feistel = new FeistelCipher(keySchedule, roundFunction);
        
        byte[] key = {0x0F, 0x1E, 0x2D, 0x3C, 0x4B, 0x5A, 0x69, 0x78};
        byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        
        feistel.setEncryptionKey(key);
        feistel.setDecryptionKey(key);

        CipherContext ctx = new CipherContext(
            feistel,
            CipherMode.CBC,
            PaddingMode.PKCS7,
            8,
            iv
        );

        String message = "Feistel network demonstration!";
        System.out.println("Сообщение: " + message);

        try {
            byte[] encrypted = ctx.encryptAsync(message.getBytes()).join();
            System.out.println("Зашифровано (CBC): " + toHex(encrypted));

            byte[] decrypted = ctx.decryptAsync(encrypted).join();
            System.out.println("Расшифровано: " + new String(decrypted));
            
            if (message.equals(new String(decrypted))) {
                System.out.println("✓ Успешно!");
            }
        } finally {
            ctx.shutdown();
        }
        System.out.println();
    }

    private static void demoFeistelRounds() {
        System.out.println("Пример 3: Влияние количества раундов");
        
        byte[] key = {0x11, 0x22, 0x33, 0x44};
        byte[] plaintext = {0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0};
        
        int[] rounds = {2, 4, 8, 16};
        
        System.out.println("Исходные данные: " + toHex(plaintext));
        System.out.println();
        
        for (int numRounds : rounds) {
            KeySchedule keySchedule = new SimpleKeySchedule(numRounds);
            RoundFunction roundFunction = new SimpleRoundFunction();
            FeistelCipher feistel = new FeistelCipher(keySchedule, roundFunction);
            
            feistel.setEncryptionKey(key);
            feistel.setDecryptionKey(key);

            byte[] encrypted = feistel.encrypt(plaintext);
            byte[] decrypted = feistel.decrypt(encrypted);
            
            boolean success = Arrays.equals(plaintext, decrypted);
            System.out.printf("Раундов: %2d | Зашифровано: %s | %s%n", 
                             numRounds, 
                             toHex(encrypted).substring(0, 16) + "...",
                             success ? "✓" : "✗");
        }
        System.out.println();
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}

