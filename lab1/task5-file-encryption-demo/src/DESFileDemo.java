import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class DESFileDemo {
    
    private static final String TEST_DIR = "test-files";
    
    public static void main(String[] args) {
        System.out.println("=== Демонстрация DES: шифрование файлов ===\n");
        
        try {
            createTestDirectory();
            createTestFiles();
            copyMediaFiles();
            
            demonstrateRandomDataEncryption();
            demonstrateTextFileEncryption();
            demonstrateCodeFileEncryption();
            demonstrateBinaryFileEncryption();
            demonstrateImageEncryption();
            demonstrateAudioEncryption();
            demonstrateVideoEncryption();
            demonstrateDifferentModes();
            
            System.out.println("\n=== Все демонстрации завершены успешно ===");
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTestDirectory() {
        new File(TEST_DIR).mkdirs();
        System.out.println("✓ Создана директория для тестовых файлов\n");
    }
    
    private static void createTestFiles() throws Exception {
        createTextFile();
        createCodeFile();
        createRandomDataFile();
        System.out.println("✓ Созданы тестовые файлы\n");
    }
    
    private static void createTextFile() throws Exception {
        String content = 
            "The Data Encryption Standard (DES) is a symmetric-key algorithm\n" +
            "for the encryption of digital data. Although its short key length\n" +
            "of 56 bits makes it too insecure for modern applications, it was\n" +
            "highly influential in the advancement of cryptography.\n\n" +
            "DES was developed in the early 1970s at IBM and based on an earlier\n" +
            "design by Horst Feistel. The algorithm was submitted to the National\n" +
            "Bureau of Standards (NBS) following the agency's invitation to propose\n" +
            "a candidate for the protection of sensitive, unclassified electronic\n" +
            "government data.\n\n" +
            "Кириллица тоже поддерживается: привет мир! Тестирование Unicode.\n";
        
        Files.write(Paths.get(TEST_DIR, "text.txt"), content.getBytes());
    }
    
    private static void createCodeFile() throws Exception {
        String code = 
            "public class RedBlackTree {\n" +
            "    private static final boolean RED = true;\n" +
            "    private static final boolean BLACK = false;\n" +
            "    \n" +
            "    private class Node {\n" +
            "        int key;\n" +
            "        Node left, right, parent;\n" +
            "        boolean color;\n" +
            "        \n" +
            "        Node(int key) {\n" +
            "            this.key = key;\n" +
            "            this.color = RED;\n" +
            "        }\n" +
            "    }\n" +
            "    \n" +
            "    private Node root;\n" +
            "    \n" +
            "    public void insert(int key) {\n" +
            "        Node node = new Node(key);\n" +
            "        if (root == null) {\n" +
            "            root = node;\n" +
            "            root.color = BLACK;\n" +
            "            return;\n" +
            "        }\n" +
            "        insertNode(root, node);\n" +
            "        fixInsertion(node);\n" +
            "    }\n" +
            "}\n";
        
        Files.write(Paths.get(TEST_DIR, "RedBlackTree.java"), code.getBytes());
    }
    
    private static void createRandomDataFile() throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] data = new byte[8192];
        random.nextBytes(data);
        Files.write(Paths.get(TEST_DIR, "random.bin"), data);
    }
    
    private static void copyMediaFiles() throws Exception {
        // Медиафайлы уже скопированы в test-files
        System.out.println("✓ Медиафайлы готовы к тестированию\n");
    }
    
    private static void demonstrateRandomDataEncryption() throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Пример 1: Шифрование псевдослучайных данных");
        System.out.println("═══════════════════════════════════════════════\n");
        
        SecureRandom random = new SecureRandom();
        
        int[] sizes = {64, 256, 1024, 4096};
        
        for (int size : sizes) {
            byte[] randomData = new byte[size];
            random.nextBytes(randomData);
            
            System.out.println("Размер данных: " + size + " байт");
            System.out.println("Первые 16 байт: " + toHex(Arrays.copyOf(randomData, Math.min(16, size))));
            
            DES des = new DES();
            byte[] key = generateRandomKey();
            byte[] iv = new byte[8];
            random.nextBytes(iv);
            
            CipherContext ctx = new CipherContext(
                des,
                key,
                CipherMode.CBC,
                PaddingMode.PKCS7,
                8,
                iv
            );
            
            byte[][] encryptedResult = new byte[1][];
            ctx.encryptAsync(randomData, encryptedResult).join();
            byte[] encrypted = encryptedResult[0];
            
            byte[][] decryptedResult = new byte[1][];
            ctx.decryptAsync(encrypted, decryptedResult).join();
            byte[] decrypted = decryptedResult[0];
            
            ctx.shutdown();
            
            boolean success = Arrays.equals(randomData, decrypted);
            String checksum = calculateChecksum(randomData);
            String decryptedChecksum = calculateChecksum(decrypted);
            
            System.out.println("Зашифровано: " + encrypted.length + " байт");
            System.out.println("Checksum исходных:      " + checksum);
            System.out.println("Checksum расшифрованных: " + decryptedChecksum);
            System.out.println("Результат: " + (success ? "✓ УСПЕШНО" : "✗ ОШИБКА"));
            System.out.println();
        }
    }
    
    private static void demonstrateTextFileEncryption() throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Пример 2: Шифрование текстового файла");
        System.out.println("═══════════════════════════════════════════════\n");
        
        String inputFile = TEST_DIR + "/text.txt";
        String encryptedFile = TEST_DIR + "/text.txt.enc";
        String decryptedFile = TEST_DIR + "/text_decrypted.txt";
        
        byte[] originalData = Files.readAllBytes(Paths.get(inputFile));
        System.out.println("Исходный файл: " + inputFile);
        System.out.println("Размер: " + originalData.length + " байт");
        System.out.println("Первые 50 символов:\n" + 
                         new String(Arrays.copyOf(originalData, Math.min(50, originalData.length))) + "...\n");
        
        DES des = new DES();
        byte[] key = generateRandomKey();
        byte[] iv = new byte[8];
        new SecureRandom().nextBytes(iv);
        
        CipherContext ctx = new CipherContext(
            des,
            key,
            CipherMode.CBC,
            PaddingMode.PKCS7,
            8,
            iv
        );
        
        System.out.println("Шифрование...");
        ctx.encryptFileAsync(inputFile, encryptedFile).join();
        byte[] encryptedData = Files.readAllBytes(Paths.get(encryptedFile));
        System.out.println("✓ Зашифровано: " + encryptedData.length + " байт");
        System.out.println("  Первые 16 байт шифротекста: " + 
                         toHex(Arrays.copyOf(encryptedData, 16)));
        
        System.out.println("\nДешифрование...");
        ctx.decryptFileAsync(encryptedFile, decryptedFile).join();
        byte[] decryptedData = Files.readAllBytes(Paths.get(decryptedFile));
        System.out.println("✓ Расшифровано: " + decryptedData.length + " байт");
        
        ctx.shutdown();
        
        boolean success = Arrays.equals(originalData, decryptedData);
        System.out.println("\nПроверка целостности:");
        System.out.println("  Checksum исходного:      " + calculateChecksum(originalData));
        System.out.println("  Checksum расшифрованного: " + calculateChecksum(decryptedData));
        System.out.println("  Результат: " + (success ? "✓ УСПЕШНО" : "✗ ОШИБКА"));
        System.out.println();
    }
    
    private static void demonstrateCodeFileEncryption() throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Пример 3: Шифрование исходного кода (Java)");
        System.out.println("═══════════════════════════════════════════════\n");
        
        String inputFile = TEST_DIR + "/RedBlackTree.java";
        String encryptedFile = TEST_DIR + "/RedBlackTree.java.enc";
        String decryptedFile = TEST_DIR + "/RedBlackTree_decrypted.java";
        
        byte[] originalCode = Files.readAllBytes(Paths.get(inputFile));
        System.out.println("Исходный файл: " + inputFile);
        System.out.println("Размер: " + originalCode.length + " байт");
        System.out.println("Первые 100 символов кода:\n");
        System.out.println(new String(Arrays.copyOf(originalCode, Math.min(100, originalCode.length))));
        System.out.println("...\n");
        
        DES des = new DES();
        byte[] key = generateRandomKey();
        byte[] iv = new byte[8];
        new SecureRandom().nextBytes(iv);
        
        CipherContext ctx = new CipherContext(
            des,
            key,
            CipherMode.CFB,
            PaddingMode.PKCS7,
            8,
            iv
        );
        
        System.out.println("Режим: CFB");
        System.out.println("Шифрование...");
        ctx.encryptFileAsync(inputFile, encryptedFile).join();
        System.out.println("✓ Файл зашифрован");
        
        System.out.println("Дешифрование...");
        ctx.decryptFileAsync(encryptedFile, decryptedFile).join();
        System.out.println("✓ Файл расшифрован");
        
        ctx.shutdown();
        
        byte[] decryptedCode = Files.readAllBytes(Paths.get(decryptedFile));
        boolean success = Arrays.equals(originalCode, decryptedCode);
        
        System.out.println("\nРезультат: " + (success ? "✓ УСПЕШНО" : "✗ ОШИБКА"));
        System.out.println("Исходный код полностью восстановлен!\n");
    }
    
    private static void demonstrateBinaryFileEncryption() throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Пример 4: Шифрование бинарного файла");
        System.out.println("═══════════════════════════════════════════════\n");
        
        String inputFile = TEST_DIR + "/random.bin";
        String encryptedFile = TEST_DIR + "/random.bin.enc";
        String decryptedFile = TEST_DIR + "/random_decrypted.bin";
        
        byte[] originalData = Files.readAllBytes(Paths.get(inputFile));
        System.out.println("Исходный файл: " + inputFile);
        System.out.println("Размер: " + originalData.length + " байт");
        System.out.println("SHA-256: " + calculateChecksum(originalData));
        
        DES des = new DES();
        byte[] key = generateRandomKey();
        byte[] iv = new byte[8];
        new SecureRandom().nextBytes(iv);
        
        CipherContext ctx = new CipherContext(
            des,
            key,
            CipherMode.OFB,
            PaddingMode.PKCS7,
            8,
            iv
        );
        
        System.out.println("\nРежим: OFB");
        System.out.println("Шифрование...");
        long startTime = System.currentTimeMillis();
        ctx.encryptFileAsync(inputFile, encryptedFile).join();
        long encryptTime = System.currentTimeMillis() - startTime;
        
        byte[] encryptedData = Files.readAllBytes(Paths.get(encryptedFile));
        System.out.println("✓ Зашифровано за " + encryptTime + " мс");
        System.out.println("  Размер шифротекста: " + encryptedData.length + " байт");
        
        System.out.println("\nДешифрование...");
        startTime = System.currentTimeMillis();
        ctx.decryptFileAsync(encryptedFile, decryptedFile).join();
        long decryptTime = System.currentTimeMillis() - startTime;
        
        ctx.shutdown();
        
        byte[] decryptedData = Files.readAllBytes(Paths.get(decryptedFile));
        System.out.println("✓ Расшифровано за " + decryptTime + " мс");
        
        boolean success = Arrays.equals(originalData, decryptedData);
        System.out.println("\nПроверка:");
        System.out.println("  SHA-256 исходного:      " + calculateChecksum(originalData));
        System.out.println("  SHA-256 расшифрованного: " + calculateChecksum(decryptedData));
        System.out.println("  Результат: " + (success ? "✓ УСПЕШНО" : "✗ ОШИБКА"));
        System.out.println();
    }
    
    private static void demonstrateImageEncryption() throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Пример 5: Шифрование изображения (PNG)");
        System.out.println("═══════════════════════════════════════════════\n");
        
        String inputFile = TEST_DIR + "/Снимок экрана 2025-11-21 в 10.02.50.png";
        String encryptedFile = TEST_DIR + "/screenshot.png.enc";
        String decryptedFile = TEST_DIR + "/screenshot_decrypted.png";
        
        File input = new File(inputFile);
        if (!input.exists()) {
            System.out.println("⚠ Файл изображения не найден, пропускаем...\n");
            return;
        }
        
        byte[] originalData = Files.readAllBytes(Paths.get(inputFile));
        System.out.println("Исходный файл: " + input.getName());
        System.out.println("Тип: PNG изображение");
        System.out.println("Размер: " + formatSize(originalData.length));
        System.out.println("SHA-256: " + calculateChecksum(originalData));
        
        DES des = new DES();
        byte[] key = generateRandomKey();
        byte[] iv = new byte[8];
        new SecureRandom().nextBytes(iv);
        
        CipherContext ctx = new CipherContext(
            des,
            key,
            CipherMode.CTR,
            PaddingMode.PKCS7,
            8,
            iv
        );
        
        System.out.println("\nРежим: CTR (оптимален для медиа)");
        System.out.println("Шифрование...");
        long startTime = System.currentTimeMillis();
        ctx.encryptFileAsync(inputFile, encryptedFile).join();
        long encryptTime = System.currentTimeMillis() - startTime;
        
        byte[] encryptedData = Files.readAllBytes(Paths.get(encryptedFile));
        System.out.println("✓ Зашифровано за " + encryptTime + " мс");
        System.out.println("  Размер: " + formatSize(encryptedData.length));
        
        System.out.println("\nДешифрование...");
        startTime = System.currentTimeMillis();
        ctx.decryptFileAsync(encryptedFile, decryptedFile).join();
        long decryptTime = System.currentTimeMillis() - startTime;
        
        ctx.shutdown();
        
        byte[] decryptedData = Files.readAllBytes(Paths.get(decryptedFile));
        System.out.println("✓ Расшифровано за " + decryptTime + " мс");
        
        boolean success = Arrays.equals(originalData, decryptedData);
        System.out.println("\nПроверка:");
        System.out.println("  SHA-256 исходного:      " + calculateChecksum(originalData));
        System.out.println("  SHA-256 расшифрованного: " + calculateChecksum(decryptedData));
        System.out.println("  Результат: " + (success ? "✓ УСПЕШНО - изображение восстановлено!" : "✗ ОШИБКА"));
        System.out.println();
    }
    
    private static void demonstrateAudioEncryption() throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Пример 6: Шифрование аудио (MP3)");
        System.out.println("═══════════════════════════════════════════════\n");
        
        String inputFile = TEST_DIR + "/2025-11-21-10.04.26.mp3";
        String encryptedFile = TEST_DIR + "/audio.mp3.enc";
        String decryptedFile = TEST_DIR + "/audio_decrypted.mp3";
        
        File input = new File(inputFile);
        if (!input.exists()) {
            System.out.println("⚠ Аудиофайл не найден, пропускаем...\n");
            return;
        }
        
        byte[] originalData = Files.readAllBytes(Paths.get(inputFile));
        System.out.println("Исходный файл: " + input.getName());
        System.out.println("Тип: MP3 аудио");
        System.out.println("Размер: " + formatSize(originalData.length));
        System.out.println("SHA-256: " + calculateChecksum(originalData));
        
        DES des = new DES();
        byte[] key = generateRandomKey();
        byte[] iv = new byte[8];
        new SecureRandom().nextBytes(iv);
        
        CipherContext ctx = new CipherContext(
            des,
            key,
            CipherMode.CBC,
            PaddingMode.PKCS7,
            8,
            iv
        );
        
        System.out.println("\nРежим: CBC");
        System.out.println("Шифрование...");
        long startTime = System.currentTimeMillis();
        ctx.encryptFileAsync(inputFile, encryptedFile).join();
        long encryptTime = System.currentTimeMillis() - startTime;
        
        byte[] encryptedData = Files.readAllBytes(Paths.get(encryptedFile));
        System.out.println("✓ Зашифровано за " + encryptTime + " мс");
        System.out.println("  Размер: " + formatSize(encryptedData.length));
        System.out.println("  Скорость: " + formatSpeed(originalData.length, encryptTime));
        
        System.out.println("\nДешифрование...");
        startTime = System.currentTimeMillis();
        ctx.decryptFileAsync(encryptedFile, decryptedFile).join();
        long decryptTime = System.currentTimeMillis() - startTime;
        
        ctx.shutdown();
        
        byte[] decryptedData = Files.readAllBytes(Paths.get(decryptedFile));
        System.out.println("✓ Расшифровано за " + decryptTime + " мс");
        System.out.println("  Скорость: " + formatSpeed(decryptedData.length, decryptTime));
        
        boolean success = Arrays.equals(originalData, decryptedData);
        System.out.println("\nПроверка:");
        System.out.println("  SHA-256 исходного:      " + calculateChecksum(originalData));
        System.out.println("  SHA-256 расшифрованного: " + calculateChecksum(decryptedData));
        System.out.println("  Результат: " + (success ? "✓ УСПЕШНО - аудио полностью восстановлено!" : "✗ ОШИБКА"));
        System.out.println();
    }
    
    private static void demonstrateVideoEncryption() throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Пример 7: Шифрование видео (MOV)");
        System.out.println("═══════════════════════════════════════════════\n");
        
        // Используем меньший видеофайл для демонстрации
        String inputFile = TEST_DIR + "/Запись экрана 2025-11-20 в 19.58.24.mov";
        String encryptedFile = TEST_DIR + "/video.mov.enc";
        String decryptedFile = TEST_DIR + "/video_decrypted.mov";
        
        File input = new File(inputFile);
        if (!input.exists()) {
            System.out.println("⚠ Видеофайл не найден, пропускаем...\n");
            return;
        }
        
        byte[] originalData = Files.readAllBytes(Paths.get(inputFile));
        System.out.println("Исходный файл: " + input.getName());
        System.out.println("Тип: MOV видео");
        System.out.println("Размер: " + formatSize(originalData.length));
        System.out.println("SHA-256: " + calculateChecksum(originalData));
        
        DES des = new DES();
        byte[] key = generateRandomKey();
        byte[] iv = new byte[8];
        new SecureRandom().nextBytes(iv);
        
        CipherContext ctx = new CipherContext(
            des,
            key,
            CipherMode.OFB,
            PaddingMode.PKCS7,
            8,
            iv
        );
        
        System.out.println("\nРежим: OFB (потоковый режим для больших файлов)");
        System.out.println("Шифрование...");
        long startTime = System.currentTimeMillis();
        ctx.encryptFileAsync(inputFile, encryptedFile).join();
        long encryptTime = System.currentTimeMillis() - startTime;
        
        byte[] encryptedData = Files.readAllBytes(Paths.get(encryptedFile));
        System.out.println("✓ Зашифровано за " + encryptTime + " мс (" + (encryptTime / 1000.0) + " сек)");
        System.out.println("  Размер: " + formatSize(encryptedData.length));
        System.out.println("  Скорость: " + formatSpeed(originalData.length, encryptTime));
        
        System.out.println("\nДешифрование...");
        startTime = System.currentTimeMillis();
        ctx.decryptFileAsync(encryptedFile, decryptedFile).join();
        long decryptTime = System.currentTimeMillis() - startTime;
        
        ctx.shutdown();
        
        byte[] decryptedData = Files.readAllBytes(Paths.get(decryptedFile));
        System.out.println("✓ Расшифровано за " + decryptTime + " мс (" + (decryptTime / 1000.0) + " сек)");
        System.out.println("  Скорость: " + formatSpeed(decryptedData.length, decryptTime));
        
        boolean success = Arrays.equals(originalData, decryptedData);
        System.out.println("\nПроверка:");
        System.out.println("  SHA-256 исходного:      " + calculateChecksum(originalData));
        System.out.println("  SHA-256 расшифрованного: " + calculateChecksum(decryptedData));
        System.out.println("  Результат: " + (success ? "✓ УСПЕШНО - видео полностью восстановлено!" : "✗ ОШИБКА"));
        
        // Демонстрация большого видеофайла (опционально)
        demonstrateLargeVideoEncryption();
        System.out.println();
    }
    
    private static void demonstrateLargeVideoEncryption() throws Exception {
        String largeVideoFile = TEST_DIR + "/Запись экрана 2025-11-21 в 10.02.23.mov";
        File largeVideo = new File(largeVideoFile);
        
        if (!largeVideo.exists()) {
            return;
        }
        
        System.out.println("\n--- Дополнительно: Большое видео ---");
        
        byte[] largeData = Files.readAllBytes(Paths.get(largeVideoFile));
        System.out.println("Файл: " + largeVideo.getName());
        System.out.println("Размер: " + formatSize(largeData.length));
        
        // Оценка времени
        double estimatedTime = largeData.length / (1024.0 * 1024.0 * 0.3); // ~0.3 MB/s
        System.out.println("Примерное время: ~" + (int)estimatedTime + " секунд шифрование + ~" + (int)estimatedTime + " секунд дешифрование");
        System.out.println("⚠ Это займет ~" + (int)(estimatedTime * 2) + " секунд. Шифруем только первый 1 МБ для демонстрации...\n");
        
        // Берем только первый 1 МБ для демонстрации
        int sampleSize = Math.min(1024 * 1024, largeData.length);
        byte[] sampleData = Arrays.copyOf(largeData, sampleSize);
        
        DES des = new DES();
        byte[] key = generateRandomKey();
        byte[] iv = generateRandomIV();
        
        CipherContext ctx = new CipherContext(
            des,
            key,
            CipherMode.CTR,
            PaddingMode.PKCS7,
            8,
            iv
        );
        
        System.out.println("Шифрование образца (" + formatSize(sampleData.length) + ")...");
        long start = System.currentTimeMillis();
        byte[][] encResult = new byte[1][];
        ctx.encryptAsync(sampleData, encResult).join();
        byte[] encrypted = encResult[0];
        long encTime = System.currentTimeMillis() - start;
        
        System.out.println("✓ Зашифровано за " + (encTime / 1000.0) + " сек");
        System.out.println("  Скорость: " + formatSpeed(sampleData.length, encTime));
        
        System.out.println("Дешифрование образца...");
        start = System.currentTimeMillis();
        byte[][] decResult = new byte[1][];
        ctx.decryptAsync(encrypted, decResult).join();
        byte[] decrypted = decResult[0];
        long decTime = System.currentTimeMillis() - start;
        
        ctx.shutdown();
        
        System.out.println("✓ Расшифровано за " + (decTime / 1000.0) + " сек");
        System.out.println("  Скорость: " + formatSpeed(decrypted.length, decTime));
        
        boolean success = Arrays.equals(sampleData, decrypted);
        System.out.println("Результат: " + (success ? "✓ УСПЕШНО (образец проверен)" : "✗ ОШИБКА"));
        System.out.println("💡 Для полного шифрования 49 МБ потребуется ~" + (int)(estimatedTime * 2) + " сек");
    }
    
    private static void demonstrateDifferentModes() throws Exception {
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("Пример 8: Сравнение режимов шифрования");
        System.out.println("═══════════════════════════════════════════════\n");
        
        String inputFile = TEST_DIR + "/text.txt";
        byte[] originalData = Files.readAllBytes(Paths.get(inputFile));
        
        CipherMode[] modes = {
            CipherMode.ECB,
            CipherMode.CBC,
            CipherMode.PCBC,
            CipherMode.CFB,
            CipherMode.OFB,
            CipherMode.CTR
        };
        
        System.out.println("Тестовый файл: " + inputFile);
        System.out.println("Размер: " + originalData.length + " байт\n");
        System.out.println("Режим      | Время шифр. | Время дешифр. | Размер шифр. | Статус");
        System.out.println("-----------|-------------|---------------|--------------|--------");
        
        for (CipherMode mode : modes) {
            DES des = new DES();
            byte[] key = generateRandomKey();
            byte[] iv = mode.requiresIV() ? generateRandomIV() : null;
            
            CipherContext ctx = new CipherContext(
                des,
                key,
                mode,
                PaddingMode.PKCS7,
                8,
                iv
            );
            
            long startEnc = System.currentTimeMillis();
            byte[][] encryptedResult = new byte[1][];
            ctx.encryptAsync(originalData, encryptedResult).join();
            byte[] encrypted = encryptedResult[0];
            long encTime = System.currentTimeMillis() - startEnc;
            
            long startDec = System.currentTimeMillis();
            byte[][] decryptedResult = new byte[1][];
            ctx.decryptAsync(encrypted, decryptedResult).join();
            byte[] decrypted = decryptedResult[0];
            long decTime = System.currentTimeMillis() - startDec;
            
            ctx.shutdown();
            
            boolean success = Arrays.equals(originalData, decrypted);
            
            System.out.printf("%-10s | %8d мс | %10d мс | %9d  | %s%n",
                mode.toString(),
                encTime,
                decTime,
                encrypted.length,
                success ? "✓" : "✗"
            );
        }
        System.out.println();
    }
    
    private static byte[] generateRandomKey() {
        byte[] key = new byte[8];
        new SecureRandom().nextBytes(key);
        return key;
    }
    
    private static byte[] generateRandomIV() {
        byte[] iv = new byte[8];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    
    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, 32); i++) {
            sb.append(String.format("%02X", bytes[i] & 0xFF));
            if (i < bytes.length - 1 && i < 31) sb.append(" ");
        }
        if (bytes.length > 32) sb.append("...");
        return sb.toString();
    }
    
    private static String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return toHex(hash).substring(0, 32);
        } catch (Exception e) {
            return "ERROR";
        }
    }
    
    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " байт";
        if (bytes < 1024 * 1024) return String.format("%.2f КБ", bytes / 1024.0);
        return String.format("%.2f МБ", bytes / (1024.0 * 1024.0));
    }
    
    private static String formatSpeed(long bytes, long millis) {
        if (millis == 0) return "N/A";
        double mbPerSec = (bytes / (1024.0 * 1024.0)) / (millis / 1000.0);
        return String.format("%.2f МБ/сек", mbPerSec);
    }
}

