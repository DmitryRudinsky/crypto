import java.util.Arrays;

public class CipherFrameworkTest {
    
    public static void main(String[] args) {
        System.out.println("=== Тестирование Cipher Framework ===\n");
        
        int passed = 0;
        int failed = 0;

        passed += testPaddingZeros() ? 1 : 0;
        failed += testPaddingZeros() ? 0 : 1;

        passed += testPaddingPKCS7() ? 1 : 0;
        failed += testPaddingPKCS7() ? 0 : 1;

        passed += testPaddingANSIX923() ? 1 : 0;
        failed += testPaddingANSIX923() ? 0 : 1;

        passed += testECBMode() ? 1 : 0;
        failed += testECBMode() ? 0 : 1;

        passed += testCBCMode() ? 1 : 0;
        failed += testCBCMode() ? 0 : 1;

        passed += testEncryptDecrypt() ? 1 : 0;
        failed += testEncryptDecrypt() ? 0 : 1;

        passed += testAsyncEncryption() ? 1 : 0;
        failed += testAsyncEncryption() ? 0 : 1;

        System.out.println("\n=== Результаты тестирования ===");
        System.out.printf("Пройдено: %d%n", passed);
        System.out.printf("Провалено: %d%n", failed);
        System.out.printf("Всего: %d%n", passed + failed);
        
        if (failed == 0) {
            System.out.println("\n🎉 Все тесты пройдены успешно!");
        }
    }

    private static boolean testPaddingZeros() {
        System.out.println("Тест 1: Padding ZEROS");
        try {
            byte[] data = {1, 2, 3, 4, 5};
            byte[] padded = PaddingMode.ZEROS.pad(data, 8);
            
            if (padded.length != 8) {
                System.out.println("✗ FAILED: Неверная длина после набивки");
                return false;
            }

            byte[] unpadded = PaddingMode.ZEROS.unpad(padded, 8);
            if (!Arrays.equals(data, unpadded)) {
                System.out.println("✗ FAILED: Данные не совпадают после unpad");
                return false;
            }

            System.out.println("✓ PASSED");
            return true;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            return false;
        } finally {
            System.out.println();
        }
    }

    private static boolean testPaddingPKCS7() {
        System.out.println("Тест 2: Padding PKCS7");
        try {
            byte[] data = {1, 2, 3, 4, 5};
            byte[] padded = PaddingMode.PKCS7.pad(data, 8);
            
            if (padded.length != 8) {
                System.out.println("✗ FAILED: Неверная длина после набивки");
                return false;
            }

            if (padded[7] != 3) {
                System.out.println("✗ FAILED: Неверное значение набивки");
                return false;
            }

            byte[] unpadded = PaddingMode.PKCS7.unpad(padded, 8);
            if (!Arrays.equals(data, unpadded)) {
                System.out.println("✗ FAILED: Данные не совпадают после unpad");
                return false;
            }

            System.out.println("✓ PASSED");
            return true;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            return false;
        } finally {
            System.out.println();
        }
    }

    private static boolean testPaddingANSIX923() {
        System.out.println("Тест 3: Padding ANSI X.923");
        try {
            byte[] data = {1, 2, 3, 4, 5};
            byte[] padded = PaddingMode.ANSI_X923.pad(data, 8);
            
            if (padded.length != 8) {
                System.out.println("✗ FAILED: Неверная длина после набивки");
                return false;
            }

            byte[] unpadded = PaddingMode.ANSI_X923.unpad(padded, 8);
            if (!Arrays.equals(data, unpadded)) {
                System.out.println("✗ FAILED: Данные не совпадают после unpad");
                return false;
            }

            System.out.println("✓ PASSED");
            return true;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            return false;
        } finally {
            System.out.println();
        }
    }

    private static boolean testECBMode() {
        System.out.println("Тест 4: ECB режим");
        try {
            byte[] key = {1, 2, 3, 4, 5, 6, 7, 8};
            DummyCipher cipher = new DummyCipher();
            cipher.setEncryptionKey(key);
            cipher.setDecryptionKey(key);

            byte[] plaintext = "12345678".getBytes();
            
            CipherContext ctx = new CipherContext(cipher, CipherMode.ECB, PaddingMode.PKCS7, 8);
            
            byte[] encrypted = ctx.encryptAsync(plaintext).join();
            byte[] decrypted = ctx.decryptAsync(encrypted).join();
            
            ctx.shutdown();

            if (!Arrays.equals(plaintext, decrypted)) {
                System.out.println("✗ FAILED: Данные не совпадают");
                return false;
            }

            System.out.println("✓ PASSED");
            return true;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            System.out.println();
        }
    }

    private static boolean testCBCMode() {
        System.out.println("Тест 5: CBC режим");
        try {
            byte[] key = {1, 2, 3, 4, 5, 6, 7, 8};
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0};
            
            DummyCipher cipher = new DummyCipher();
            cipher.setEncryptionKey(key);
            cipher.setDecryptionKey(key);

            byte[] plaintext = "Test CBC Mode".getBytes();
            
            CipherContext ctx = new CipherContext(cipher, CipherMode.CBC, PaddingMode.PKCS7, 8, iv);
            
            byte[] encrypted = ctx.encryptAsync(plaintext).join();
            byte[] decrypted = ctx.decryptAsync(encrypted).join();
            
            ctx.shutdown();

            if (!Arrays.equals(plaintext, decrypted)) {
                System.out.println("✗ FAILED: Данные не совпадают");
                return false;
            }

            System.out.println("✓ PASSED");
            return true;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            return false;
        } finally {
            System.out.println();
        }
    }

    private static boolean testEncryptDecrypt() {
        System.out.println("Тест 6: Шифрование и дешифрование");
        try {
            byte[] key = {1, 2, 3, 4, 5, 6, 7, 8};
            byte[] iv = {9, 8, 7, 6, 5, 4, 3, 2};
            
            DummyCipher cipher = new DummyCipher();
            cipher.setEncryptionKey(key);
            cipher.setDecryptionKey(key);

            String original = "Hello, Cryptography!";
            byte[] plaintext = original.getBytes();
            
            CipherContext ctx = new CipherContext(cipher, CipherMode.CBC, PaddingMode.PKCS7, 8, iv);
            
            byte[] encrypted = ctx.encryptAsync(plaintext).join();
            
            if (Arrays.equals(plaintext, encrypted)) {
                System.out.println("✗ FAILED: Зашифрованные данные совпадают с исходными");
                ctx.shutdown();
                return false;
            }

            byte[] decrypted = ctx.decryptAsync(encrypted).join();
            String result = new String(decrypted);
            
            ctx.shutdown();

            if (!original.equals(result)) {
                System.out.println("✗ FAILED: Расшифрованный текст не совпадает");
                System.out.println("  Ожидалось: " + original);
                System.out.println("  Получено: " + result);
                return false;
            }

            System.out.println("✓ PASSED");
            return true;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            return false;
        } finally {
            System.out.println();
        }
    }

    private static boolean testAsyncEncryption() {
        System.out.println("Тест 7: Асинхронное шифрование");
        try {
            byte[] key = {1, 2, 3, 4, 5, 6, 7, 8};
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0};
            
            DummyCipher cipher = new DummyCipher();
            cipher.setEncryptionKey(key);
            cipher.setDecryptionKey(key);

            byte[] plaintext = "Async test".getBytes();
            
            CipherContext ctx = new CipherContext(cipher, CipherMode.CBC, PaddingMode.PKCS7, 8, iv);
            
            byte[] encrypted = ctx.encryptAsync(plaintext).join();
            byte[] decrypted = ctx.decryptAsync(encrypted).join();
            
            ctx.shutdown();

            if (!Arrays.equals(plaintext, decrypted)) {
                System.out.println("✗ FAILED: Асинхронная операция вернула неверные данные");
                return false;
            }

            System.out.println("✓ PASSED");
            return true;
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            return false;
        } finally {
            System.out.println();
        }
    }
}

