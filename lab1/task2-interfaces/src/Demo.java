import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class Demo {
    
    public static void main(String[] args) {
        System.out.println("=== Демонстрация Cipher Framework ===\n");

        byte[] key = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
        byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        
        demoNewAPI(key, iv);
        demoECB(key);
        demoCBC(key, iv);
        demoPadding();
        demoAsync(key, iv);
        demoFileEncryption(key, iv);

        System.out.println("\n=== Демонстрация завершена ===");
    }

    private static void demoNewAPI(byte[] key, byte[] iv) {
        System.out.println("Пример 1: Новый API (конструктор с ключом и out-параметры)");
        String plaintext = "New API Demo!";
        byte[] data = plaintext.getBytes();

        // Используем новый конструктор, принимающий ключ
        CipherContext ctx = new CipherContext(new DummyCipher(), key, CipherMode.CBC, PaddingMode.PKCS7, 8, iv);

        try {
            byte[][] encryptedResult = new byte[1][];
            ctx.encryptAsync(data, encryptedResult).join();
            System.out.println("Исходный текст: " + plaintext);
            System.out.println("Зашифровано: " + toHex(encryptedResult[0]));

            byte[][] decryptedResult = new byte[1][];
            ctx.decryptAsync(encryptedResult[0], decryptedResult).join();
            System.out.println("Расшифровано: " + new String(decryptedResult[0]));
        } finally {
            ctx.shutdown();
        }
        System.out.println();
    }

    private static void demoECB(byte[] key) {
        System.out.println("Пример 2: ECB режим (с параллельной обработкой)");
        String plaintext = "Hello World!";
        byte[] data = plaintext.getBytes();

        CipherContext ctx = new CipherContext(new DummyCipher(), key, CipherMode.ECB, PaddingMode.PKCS7, 8);

        try {
            byte[][] encryptedResult = new byte[1][];
            ctx.encryptAsync(data, encryptedResult).join();
            System.out.println("Исходный текст: " + plaintext);
            System.out.println("Зашифровано: " + toHex(encryptedResult[0]));

            byte[][] decryptedResult = new byte[1][];
            ctx.decryptAsync(encryptedResult[0], decryptedResult).join();
            System.out.println("Расшифровано: " + new String(decryptedResult[0]));
        } finally {
            ctx.shutdown();
        }
        System.out.println();
    }

    private static void demoCBC(byte[] key, byte[] iv) {
        System.out.println("Пример 3: CBC режим с IV");
        String plaintext = "Symmetric Encryption";
        byte[] data = plaintext.getBytes();

        CipherContext ctx = new CipherContext(new DummyCipher(), key, CipherMode.CBC, PaddingMode.PKCS7, 8, iv);

        try {
            byte[][] encryptedResult = new byte[1][];
            ctx.encryptAsync(data, encryptedResult).join();
            System.out.println("Исходный текст: " + plaintext);
            System.out.println("Зашифровано: " + toHex(encryptedResult[0]));

            byte[][] decryptedResult = new byte[1][];
            ctx.decryptAsync(encryptedResult[0], decryptedResult).join();
            System.out.println("Расшифровано: " + new String(decryptedResult[0]));
        } finally {
            ctx.shutdown();
        }
        System.out.println();
    }

    private static void demoPadding() {
        System.out.println("Пример 4: Различные режимы набивки");
        byte[] data = "Test".getBytes();

        System.out.println("Исходные данные (" + data.length + " байт): " + toHex(data));

        PaddingMode[] modes = {PaddingMode.ZEROS, PaddingMode.ANSI_X923, PaddingMode.PKCS7, PaddingMode.ISO_10126};
        
        for (PaddingMode mode : modes) {
            byte[] padded = mode.pad(data, 8);
            System.out.println(mode + ": " + toHex(padded) + " (" + padded.length + " байт)");
        }
        System.out.println();
    }

    private static void demoAsync(byte[] key, byte[] iv) {
        System.out.println("Пример 5: Асинхронное шифрование с out-параметрами");
        
        String[] texts = {"Message 1", "Message 2", "Message 3"};
        CipherContext ctx = new CipherContext(new DummyCipher(), key, CipherMode.CBC, PaddingMode.PKCS7, 8, iv);

        try {
            CompletableFuture<?>[] futures = new CompletableFuture[texts.length];

            for (int i = 0; i < texts.length; i++) {
                final int index = i;
                byte[][] result = new byte[1][];
                futures[i] = ctx.encryptAsync(texts[i].getBytes(), result)
                    .thenRun(() -> {
                        System.out.println("Зашифровано [" + index + "]: " + toHex(result[0]));
                    });
            }

            CompletableFuture.allOf(futures).join();
            System.out.println("Все операции завершены асинхронно");
        } finally {
            ctx.shutdown();
        }
        System.out.println();
    }

    private static void demoFileEncryption(byte[] key, byte[] iv) {
        System.out.println("Пример 6: Шифрование файлов");
        
        CipherContext ctx = new CipherContext(new DummyCipher(), key, CipherMode.ECB, PaddingMode.PKCS7, 8);

        try {
            System.out.println("Демонстрация работы с файлами:");
            System.out.println("- ctx.encryptFileAsync(\"input.txt\", \"output.enc\")");
            System.out.println("- ctx.decryptFileAsync(\"output.enc\", \"decrypted.txt\")");
            System.out.println("(Методы доступны для работы с файлами)");
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
