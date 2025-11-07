import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CipherContext {
    private final SymmetricCipher cipher;
    private final CipherMode mode;
    private final PaddingMode padding;
    private final byte[] iv;
    private final int blockSize;
    private final ExecutorService executor;

    /**
     * Конструктор в соответствии с заданием (принимает ключ шифрования).
     * 
     * @param cipher реализация симметричного алгоритма (будет настроена с ключом)
     * @param key ключ шифрования (массив байтов)
     * @param mode режим шифрования (объект перечисления)
     * @param padding режим набивки (объект перечисления)
     * @param blockSize размер блока в байтах
     * @param iv вектор инициализации (опционально)
     * @param additionalParams дополнительные параметры для указанного режима (коллекция аргументов переменной длины)
     */
    public CipherContext(
            SymmetricCipher cipher,
            byte[] key,
            CipherMode mode,
            PaddingMode padding,
            int blockSize,
            byte[] iv,
            Object... additionalParams
    ) {
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (cipher == null) {
            throw new IllegalArgumentException("Cipher cannot be null");
        }
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }
        if (padding == null) {
            throw new IllegalArgumentException("Padding cannot be null");
        }
        if (blockSize <= 0) {
            throw new IllegalArgumentException("Block size must be positive");
        }
        if (mode.requiresIV() && (iv == null || iv.length == 0)) {
            throw new IllegalArgumentException("IV is required for " + mode + " mode");
        }

        // Настраиваем ключи шифрования
        cipher.setEncryptionKey(key);
        cipher.setDecryptionKey(key);

        this.cipher = cipher;
        this.mode = mode;
        this.padding = padding;
        this.blockSize = blockSize;
        this.iv = iv != null ? Arrays.copyOf(iv, iv.length) : null;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        // Дополнительные параметры могут быть обработаны здесь при необходимости
        // (для будущих расширений)
    }

    /**
     * Упрощённый конструктор без IV и дополнительных параметров.
     */
    public CipherContext(
            SymmetricCipher cipher,
            byte[] key,
            CipherMode mode,
            PaddingMode padding,
            int blockSize
    ) {
        this(cipher, key, mode, padding, blockSize, null);
    }

    /**
     * Асинхронное шифрование данных с записью в out-параметр (по заданию).
     * 
     * @param data данные для шифрования
     * @param result массив-обёртка для результата (result[0] будет содержать зашифрованные данные)
     * @return CompletableFuture для отслеживания завершения операции
     */
    public CompletableFuture<Void> encryptAsync(byte[] data, byte[][] result) {
        return CompletableFuture.runAsync(() -> {
            byte[] paddedData = padding.pad(data, blockSize);
            byte[][] blocks = splitIntoBlocks(paddedData, blockSize);
            byte[][] encryptedBlocks = mode.encrypt(blocks, cipher, iv);
            result[0] = mergeBlocks(encryptedBlocks);
        }, executor);
    }

    /**
     * Асинхронное дешифрование данных с записью в out-параметр (по заданию).
     * 
     * @param data данные для дешифрования
     * @param result массив-обёртка для результата (result[0] будет содержать расшифрованные данные)
     * @return CompletableFuture для отслеживания завершения операции
     */
    public CompletableFuture<Void> decryptAsync(byte[] data, byte[][] result) {
        return CompletableFuture.runAsync(() -> {
            byte[][] blocks = splitIntoBlocks(data, blockSize);
            byte[][] decryptedBlocks = mode.decrypt(blocks, cipher, iv);
            byte[] merged = mergeBlocks(decryptedBlocks);
            result[0] = padding.unpad(merged, blockSize);
        }, executor);
    }

    /**
     * Асинхронное шифрование файла (по заданию).
     * 
     * @param inputPath путь к входному файлу
     * @param outputPath путь к выходному файлу
     * @return CompletableFuture для отслеживания завершения операции
     */
    public CompletableFuture<Void> encryptFileAsync(String inputPath, String outputPath) {
        return CompletableFuture.runAsync(() -> {
            try {
                byte[] inputData = Files.readAllBytes(Paths.get(inputPath));
                byte[][] result = new byte[1][];
                encryptAsync(inputData, result).join();
                Files.write(Paths.get(outputPath), result[0]);
            } catch (IOException e) {
                throw new RuntimeException("Error processing file: " + e.getMessage(), e);
            }
        }, executor);
    }

    /**
     * Асинхронное дешифрование файла (по заданию).
     * 
     * @param inputPath путь к входному файлу
     * @param outputPath путь к выходному файлу
     * @return CompletableFuture для отслеживания завершения операции
     */
    public CompletableFuture<Void> decryptFileAsync(String inputPath, String outputPath) {
        return CompletableFuture.runAsync(() -> {
            try {
                byte[] inputData = Files.readAllBytes(Paths.get(inputPath));
                byte[][] result = new byte[1][];
                decryptAsync(inputData, result).join();
                Files.write(Paths.get(outputPath), result[0]);
            } catch (IOException e) {
                throw new RuntimeException("Error processing file: " + e.getMessage(), e);
            }
        }, executor);
    }

    private byte[][] splitIntoBlocks(byte[] data, int blockSize) {
        if (blockSize <= 0 || data.length == 0) {
            return new byte[0][];
        }

        int blockCount = data.length / blockSize;
        byte[][] blocks = new byte[blockCount][];

        for (int i = 0; i < blockCount; i++) {
            blocks[i] = Arrays.copyOfRange(data, i * blockSize, (i + 1) * blockSize);
        }

        return blocks;
    }

    private byte[] mergeBlocks(byte[][] blocks) {
        int totalSize = 0;
        for (byte[] block : blocks) {
            totalSize += block.length;
        }

        byte[] result = new byte[totalSize];
        int offset = 0;

        for (byte[] block : blocks) {
            System.arraycopy(block, 0, result, offset, block.length);
            offset += block.length;
        }

        return result;
    }

    public void shutdown() {
        executor.shutdown();
    }
}

