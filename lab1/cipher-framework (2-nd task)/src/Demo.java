import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class Demo {
    
    public static void main(String[] args) {
        System.out.println("=== Демонстрация Cipher Framework ===\n");

        byte[] key = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
        byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        
        DummyCipher cipher = new DummyCipher();
        cipher.setEncryptionKey(key);
        cipher.setDecryptionKey(key);

        demoECB(cipher);
        demoCBC(cipher, iv);
        demoPadding();
        demoAsync(cipher, iv);

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demoECB(SymmetricCipher cipher) {
        System.out.println("Пример 1: ECB режим");
        String plaintext = "Hello World!";
        byte[] data = plaintext.getBytes();

        CipherContext ctx = new CipherContext(cipher, CipherMode.ECB, PaddingMode.PKCS7, 8);

        try {
            byte[] encrypted = ctx.encryptAsync(data).join();
            System.out.println("Исходный текст: " + plaintext);
            System.out.println("Зашифровано: " + toHex(encrypted));

            byte[] decrypted = ctx.decryptAsync(encrypted).join();
            System.out.println("Расшифровано: " + new String(decrypted));
        } finally {
            ctx.shutdown();
        }
        System.out.println();
    }

    private static void demoCBC(SymmetricCipher cipher, byte[] iv) {
        System.out.println("Пример 2: CBC режим с IV");
        String plaintext = "Symmetric Encryption";
        byte[] data = plaintext.getBytes();

        CipherContext ctx = new CipherContext(cipher, CipherMode.CBC, PaddingMode.PKCS7, 8, iv);

        try {
            byte[] encrypted = ctx.encryptAsync(data).join();
            System.out.println("Исходный текст: " + plaintext);
            System.out.println("Зашифровано: " + toHex(encrypted));

            byte[] decrypted = ctx.decryptAsync(encrypted).join();
            System.out.println("Расшифровано: " + new String(decrypted));
        } finally {
            ctx.shutdown();
        }
        System.out.println();
    }

    private static void demoPadding() {
        System.out.println("Пример 3: Различные режимы набивки");
        byte[] data = "Test".getBytes();

        System.out.println("Исходные данные (" + data.length + " байт): " + toHex(data));

        PaddingMode[] modes = {PaddingMode.ZEROS, PaddingMode.ANSI_X923, PaddingMode.PKCS7, PaddingMode.ISO_10126};
        
        for (PaddingMode mode : modes) {
            byte[] padded = mode.pad(data, 8);
            System.out.println(mode + ": " + toHex(padded) + " (" + padded.length + " байт)");
        }
        System.out.println();
    }

    private static void demoAsync(SymmetricCipher cipher, byte[] iv) {
        System.out.println("Пример 4: Асинхронное шифрование");
        
        String[] texts = {"Message 1", "Message 2", "Message 3"};
        CipherContext ctx = new CipherContext(cipher, CipherMode.CBC, PaddingMode.PKCS7, 8, iv);

        try {
            CompletableFuture<?>[] futures = new CompletableFuture[texts.length];

            for (int i = 0; i < texts.length; i++) {
                final int index = i;
                futures[i] = ctx.encryptAsync(texts[i].getBytes())
                    .thenAccept(encrypted -> {
                        System.out.println("Зашифровано [" + index + "]: " + toHex(encrypted));
                    });
            }

            CompletableFuture.allOf(futures).join();
            System.out.println("Все операции завершены асинхронно");
        } finally {
            ctx.shutdown();
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

