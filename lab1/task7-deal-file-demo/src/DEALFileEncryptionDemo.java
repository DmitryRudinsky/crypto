import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HexFormat;

/**
 * Демонстрация шифрования различных типов файлов с помощью алгоритма DEAL.
 * 
 * Данная программа демонстрирует:
 * 1. Шифрование псевдослучайных последовательностей байтов
 * 2. Шифрование текстовых файлов
 * 3. Шифрование различных типов файлов (изображения, аудио и т.д.)
 * 4. Использование различных режимов шифрования (ECB, CBC, CTR и т.д.)
 * 5. Использование различных режимов паддинга (PKCS7, ANSI X.923)
 */
public class DEALFileEncryptionDemo {
    
    private static final String TEST_FILES_DIR = "test-files/";
    
    public static void main(String[] args) {
        System.out.println("═".repeat(80));
        System.out.println("  ДЕМОНСТРАЦИЯ ШИФРОВАНИЯ ФАЙЛОВ С ИСПОЛЬЗОВАНИЕМ АЛГОРИТМА DEAL");
        System.out.println("═".repeat(80));
        System.out.println();
        
        try {
            // Создаем директорию для тестовых файлов
            Files.createDirectories(Paths.get(TEST_FILES_DIR));
            
            // 1. Демонстрация шифрования псевдослучайных последовательностей
            demonstrateRandomDataEncryption();
            
            // 2. Демонстрация шифрования текстовых файлов
            demonstrateTextFileEncryption();
            
            // 3. Демонстрация различных режимов шифрования
            demonstrateCipherModes();
            
            // 4. Демонстрация различных режимов паддинга
            demonstratePaddingModes();
            
            System.out.println("\n" + "═".repeat(80));
            System.out.println("  ✓ ВСЕ ДЕМОНСТРАЦИИ ЗАВЕРШЕНЫ УСПЕШНО");
            System.out.println("═".repeat(80));
            
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Демонстрация шифрования псевдослучайных последовательностей байтов
     */
    private static void demonstrateRandomDataEncryption() throws IOException {
        System.out.println("─".repeat(80));
        System.out.println("1. ШИФРОВАНИЕ ПСЕВДОСЛУЧАЙНЫХ ПОСЛЕДОВАТЕЛЬНОСТЕЙ БАЙТОВ");
        System.out.println("─".repeat(80));
        
        SecureRandom random = new SecureRandom();
        
        // Генерируем ключ для DEAL (192 бита = 24 байта)
        byte[] key = new byte[24];
        random.nextBytes(key);
        System.out.println("Ключ (24 байта): " + formatBytes(key, 24));
        
        // Тестируем различные размеры данных
        int[] sizes = {64, 256, 1024, 4096};
        
        for (int size : sizes) {
            System.out.println("\nТест с данными размером " + size + " байт:");
            
            // Генерируем случайные данные
            byte[] randomData = new byte[size];
            random.nextBytes(randomData);
            
            // Сохраняем в файл
            String filename = TEST_FILES_DIR + "random_" + size + ".bin";
            Files.write(Paths.get(filename), randomData);
            System.out.println("  ✓ Создан файл: " + filename);
            System.out.println("    Первые 16 байт: " + formatBytes(randomData, 16));
            
            // Шифруем
            DEAL deal = new DEAL();
            deal.setEncryptionKey(key);
            deal.setDecryptionKey(key);
            
            byte[] iv = new byte[16];
            random.nextBytes(iv);
            
            CipherContext ctx = new CipherContext(deal, CipherMode.CBC, PaddingMode.PKCS7, 16, iv);
            byte[] encrypted = ctx.encryptAsync(randomData).join();
            ctx.shutdown();
            
            String encFilename = filename + ".enc";
            Files.write(Paths.get(encFilename), encrypted);
            System.out.println("  ✓ Зашифровано: " + encFilename);
            System.out.println("    Размер: " + encrypted.length + " байт");
            
            // Дешифруем
            ctx = new CipherContext(deal, CipherMode.CBC, PaddingMode.PKCS7, 16, iv);
            byte[] decrypted = ctx.decryptAsync(encrypted).join();
            ctx.shutdown();
            
            String decFilename = filename.replace(".bin", "_decrypted.bin");
            Files.write(Paths.get(decFilename), decrypted);
            System.out.println("  ✓ Дешифровано: " + decFilename);
            
            // Проверяем
            if (Arrays.equals(randomData, decrypted)) {
                System.out.println("  ✓ Проверка пройдена: данные совпадают!");
            } else {
                System.out.println("  ✗ ОШИБКА: данные не совпадают!");
            }
        }
        
        System.out.println();
    }
    
    /**
     * Демонстрация шифрования текстовых файлов
     */
    private static void demonstrateTextFileEncryption() throws IOException {
        System.out.println("─".repeat(80));
        System.out.println("2. ШИФРОВАНИЕ ТЕКСТОВЫХ ФАЙЛОВ");
        System.out.println("─".repeat(80));
        
        // Создаем текстовый файл
        String textContent = """
            Лабораторная работа №1
            Тема: Криптографические алгоритмы
            
            Алгоритм DEAL (Data Encryption Algorithm with Larger blocks) является
            128-битным блочным шифром, который использует DES в качестве строительного
            блока в сети Фейстеля.
            
            Особенности DEAL:
            - Размер блока: 128 бит (16 байт)
            - Размер ключа: 192 бита (24 байта) - три ключа DES
            - Количество раундов: 6 (по умолчанию)
            - Функция раунда F: DES
            
            Данная демонстрация показывает применение DEAL для шифрования различных
            типов файлов с использованием разных режимов работы и паддинга.
            """;
        
        String textFile = TEST_FILES_DIR + "document.txt";
        Files.writeString(Paths.get(textFile), textContent);
        System.out.println("✓ Создан текстовый файл: " + textFile);
        System.out.println("  Размер: " + textContent.length() + " байт");
        System.out.println("  Первые 50 символов: " + textContent.substring(0, 50).replace("\n", "\\n") + "...");
        
        // Генерируем ключ
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[24];
        random.nextBytes(key);
        
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        
        // Шифруем текстовый файл
        DEAL deal = new DEAL();
        deal.setEncryptionKey(key);
        deal.setDecryptionKey(key);
        
        CipherContext ctx = new CipherContext(deal, CipherMode.CBC, PaddingMode.PKCS7, 16, iv);
        byte[] textData = Files.readAllBytes(Paths.get(textFile));
        byte[] encrypted = ctx.encryptAsync(textData).join();
        ctx.shutdown();
        
        String encFile = textFile + ".enc";
        Files.write(Paths.get(encFile), encrypted);
        System.out.println("\n✓ Текст зашифрован: " + encFile);
        System.out.println("  Размер зашифрованного файла: " + encrypted.length + " байт");
        
        // Дешифруем
        ctx = new CipherContext(deal, CipherMode.CBC, PaddingMode.PKCS7, 16, iv);
        byte[] decrypted = ctx.decryptAsync(encrypted).join();
        ctx.shutdown();
        
        String decFile = textFile.replace(".txt", "_decrypted.txt");
        Files.write(Paths.get(decFile), decrypted);
        System.out.println("✓ Текст дешифрован: " + decFile);
        
        String decryptedContent = Files.readString(Paths.get(decFile));
        if (textContent.equals(decryptedContent)) {
            System.out.println("✓ Проверка пройдена: текст полностью восстановлен!");
        } else {
            System.out.println("✗ ОШИБКА: текст не совпадает!");
        }
        
        System.out.println();
    }
    
    /**
     * Демонстрация различных режимов шифрования
     */
    private static void demonstrateCipherModes() throws IOException {
        System.out.println("─".repeat(80));
        System.out.println("3. СРАВНЕНИЕ РАЗЛИЧНЫХ РЕЖИМОВ ШИФРОВАНИЯ");
        System.out.println("─".repeat(80));
        
        // Создаем тестовые данные
        String testData = "DEAL cipher demonstration with different modes! ".repeat(10);
        byte[] data = testData.getBytes();
        
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[24];
        random.nextBytes(key);
        
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        
        DEAL deal = new DEAL();
        deal.setEncryptionKey(key);
        deal.setDecryptionKey(key);
        
        // Тестируем разные режимы
        CipherMode[] modes = {CipherMode.ECB, CipherMode.CBC, CipherMode.CTR, CipherMode.OFB, CipherMode.CFB};
        
        for (CipherMode mode : modes) {
            System.out.println("\nРежим: " + mode);
            System.out.println("  Требует IV: " + (mode.requiresIV() ? "Да" : "Нет"));
            
            try {
                CipherContext ctx = mode.requiresIV() 
                    ? new CipherContext(deal, mode, PaddingMode.PKCS7, 16, iv)
                    : new CipherContext(deal, mode, PaddingMode.PKCS7, 16);
                
                long startEnc = System.nanoTime();
                byte[] encrypted = ctx.encryptAsync(data).join();
                long endEnc = System.nanoTime();
                
                long startDec = System.nanoTime();
                byte[] decrypted = ctx.decryptAsync(encrypted).join();
                long endDec = System.nanoTime();
                
                ctx.shutdown();
                
                boolean correct = Arrays.equals(data, decrypted);
                System.out.println("  Размер исходных данных: " + data.length + " байт");
                System.out.println("  Размер зашифрованных: " + encrypted.length + " байт");
                System.out.println("  Время шифрования: " + (endEnc - startEnc) / 1000 + " мкс");
                System.out.println("  Время дешифрования: " + (endDec - startDec) / 1000 + " мкс");
                System.out.println("  Результат: " + (correct ? "✓ Данные восстановлены" : "✗ ОШИБКА"));
                
            } catch (Exception e) {
                System.out.println("  ✗ Ошибка: " + e.getMessage());
            }
        }
        
        System.out.println();
    }
    
    /**
     * Демонстрация различных режимов паддинга
     */
    private static void demonstratePaddingModes() throws IOException {
        System.out.println("─".repeat(80));
        System.out.println("4. СРАВНЕНИЕ РАЗЛИЧНЫХ РЕЖИМОВ ПАДДИНГА");
        System.out.println("─".repeat(80));
        
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[24];
        random.nextBytes(key);
        
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        
        DEAL deal = new DEAL();
        deal.setEncryptionKey(key);
        deal.setDecryptionKey(key);
        
        // Тестируем разные размеры данных и режимы паддинга
        int[] dataSizes = {10, 16, 17, 31, 32, 33};
        PaddingMode[] paddingModes = {PaddingMode.PKCS7, PaddingMode.ANSI_X923, PaddingMode.ZEROS};
        
        for (PaddingMode padding : paddingModes) {
            System.out.println("\nРежим паддинга: " + padding);
            
            for (int size : dataSizes) {
                byte[] data = new byte[size];
                random.nextBytes(data);
                
                try {
                    CipherContext ctx = new CipherContext(deal, CipherMode.CBC, padding, 16, iv);
                    byte[] encrypted = ctx.encryptAsync(data).join();
                    byte[] decrypted = ctx.decryptAsync(encrypted).join();
                    ctx.shutdown();
                    
                    boolean correct = Arrays.equals(data, decrypted);
                    int paddingSize = encrypted.length - size;
                    
                    System.out.printf("  Размер данных: %2d байт → Зашифровано: %2d байт (паддинг: %d байт) %s%n",
                        size, encrypted.length, paddingSize, correct ? "✓" : "✗");
                    
                } catch (Exception e) {
                    System.out.printf("  Размер данных: %2d байт → ✗ Ошибка: %s%n", size, e.getMessage());
                }
            }
        }
        
        System.out.println();
    }
    
    /**
     * Форматирует байты для вывода в hex формате
     */
    private static String formatBytes(byte[] bytes, int maxLength) {
        int len = Math.min(bytes.length, maxLength);
        byte[] truncated = Arrays.copyOf(bytes, len);
        String hex = HexFormat.of().formatHex(truncated);
        
        if (bytes.length > maxLength) {
            return hex + "...";
        }
        return hex;
    }
}

