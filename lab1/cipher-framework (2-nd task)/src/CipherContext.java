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

    public CipherContext(
            SymmetricCipher cipher,
            CipherMode mode,
            PaddingMode padding,
            int blockSize,
            byte[] iv
    ) {
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

        this.cipher = cipher;
        this.mode = mode;
        this.padding = padding;
        this.blockSize = blockSize;
        this.iv = iv != null ? Arrays.copyOf(iv, iv.length) : null;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public CipherContext(
            SymmetricCipher cipher,
            CipherMode mode,
            PaddingMode padding,
            int blockSize
    ) {
        this(cipher, mode, padding, blockSize, null);
    }

    public CompletableFuture<byte[]> encryptAsync(byte[] data) {
        return CompletableFuture.supplyAsync(() -> {
            byte[] paddedData = padding.pad(data, blockSize);
            byte[][] blocks = splitIntoBlocks(paddedData, blockSize);
            byte[][] encryptedBlocks = mode.encrypt(blocks, cipher, iv);
            return mergeBlocks(encryptedBlocks);
        }, executor);
    }

    public CompletableFuture<byte[]> decryptAsync(byte[] data) {
        return CompletableFuture.supplyAsync(() -> {
            byte[][] blocks = splitIntoBlocks(data, blockSize);
            byte[][] decryptedBlocks = mode.decrypt(blocks, cipher, iv);
            byte[] merged = mergeBlocks(decryptedBlocks);
            return padding.unpad(merged, blockSize);
        }, executor);
    }

    public CompletableFuture<Void> encryptFileAsync(String inputPath, String outputPath) {
        return CompletableFuture.runAsync(() -> {
            try {
                byte[] inputData = Files.readAllBytes(Paths.get(inputPath));
                byte[] encrypted = encryptAsync(inputData).join();
                Files.write(Paths.get(outputPath), encrypted);
            } catch (IOException e) {
                throw new RuntimeException("Error processing file: " + e.getMessage(), e);
            }
        }, executor);
    }

    public CompletableFuture<Void> decryptFileAsync(String inputPath, String outputPath) {
        return CompletableFuture.runAsync(() -> {
            try {
                byte[] inputData = Files.readAllBytes(Paths.get(inputPath));
                byte[] decrypted = decryptAsync(inputData).join();
                Files.write(Paths.get(outputPath), decrypted);
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

