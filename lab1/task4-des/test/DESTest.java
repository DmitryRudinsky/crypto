import java.util.Arrays;

public class DESTest {
    
    public static void main(String[] args) {
        System.out.println("=== Тестирование DES ===\n");
        
        int passed = 0;
        int failed = 0;

        passed += testBasicEncryptDecrypt() ? 1 : 0;
        failed += testBasicEncryptDecrypt() ? 0 : 1;

        passed += testZeroKey() ? 1 : 0;
        failed += testZeroKey() ? 0 : 1;

        passed += testDifferentKeys() ? 1 : 0;
        failed += testDifferentKeys() ? 0 : 1;

        passed += testWithCipherContext() ? 1 : 0;
        failed += testWithCipherContext() ? 0 : 1;

        passed += testInvalidKeySize() ? 1 : 0;
        failed += testInvalidKeySize() ? 0 : 1;

        passed += testInvalidBlockSize() ? 1 : 0;
        failed += testInvalidBlockSize() ? 0 : 1;

        passed += testMultipleBlocks() ? 1 : 0;
        failed += testMultipleBlocks() ? 0 : 1;

        System.out.println("\n=== Результаты тестирования ===");
        System.out.printf("Пройдено: %d%n", passed);
        System.out.printf("Провалено: %d%n", failed);
        System.out.printf("Всего: %d%n", passed + failed);
        
        if (failed == 0) {
            System.out.println("\n🎉 Все тесты пройдены успешно!");
        }
    }

    private static boolean testBasicEncryptDecrypt() {
        System.out.println("Тест 1: Базовое шифрование и дешифрование");
        try {
            DES des = new DES();
            
            byte[] key = {(byte)0x13, (byte)0x34, (byte)0x57, (byte)0x79,
                         (byte)0x9B, (byte)0xBC, (byte)0xDF, (byte)0xF1};
            des.setEncryptionKey(key);
            des.setDecryptionKey(key);

            byte[] plaintext = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
                               (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF};
            
            byte[] encrypted = des.encrypt(plaintext);
            byte[] decrypted = des.decrypt(encrypted);

            if (!Arrays.equals(plaintext, decrypted)) {
                System.out.println("✗ FAILED: Данные не совпадают после дешифрования");
                return false;
            }

            if (Arrays.equals(plaintext, encrypted)) {
                System.out.println("✗ FAILED: Зашифрованные данные совпадают с исходными");
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

    private static boolean testZeroKey() {
        System.out.println("Тест 2: Нулевой ключ и текст");
        try {
            DES des = new DES();
            
            byte[] key = new byte[8];
            byte[] plaintext = new byte[8];
            
            des.setEncryptionKey(key);
            des.setDecryptionKey(key);

            byte[] encrypted = des.encrypt(plaintext);
            byte[] decrypted = des.decrypt(encrypted);

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

    private static boolean testDifferentKeys() {
        System.out.println("Тест 3: Различные ключи");
        try {
            DES des = new DES();
            
            byte[] plaintext = {(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67,
                               (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF};
            
            byte[] key1 = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
            byte[] key2 = {0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01};
            
            des.setEncryptionKey(key1);
            byte[] encrypted1 = des.encrypt(plaintext);
            
            des.setEncryptionKey(key2);
            byte[] encrypted2 = des.encrypt(plaintext);
            
            if (Arrays.equals(encrypted1, encrypted2)) {
                System.out.println("✗ FAILED: Разные ключи дали одинаковый результат");
                return false;
            }
            
            des.setDecryptionKey(key1);
            byte[] decrypted1 = des.decrypt(encrypted1);
            
            des.setDecryptionKey(key2);
            byte[] decrypted2 = des.decrypt(encrypted2);

            if (!Arrays.equals(plaintext, decrypted1) || !Arrays.equals(plaintext, decrypted2)) {
                System.out.println("✗ FAILED: Дешифрование не работает");
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

    private static boolean testWithCipherContext() {
        System.out.println("Тест 4: Интеграция с CipherContext");
        try {
            DES des = new DES();
            
            byte[] key = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
            byte[] iv = new byte[8];

            CipherContext ctx = new CipherContext(
                des,
                key,
                CipherMode.CBC,
                PaddingMode.PKCS7,
                8,
                iv
            );

            String message = "DES test message for CBC mode!";
            byte[][] encryptedResult = new byte[1][];
            ctx.encryptAsync(message.getBytes(), encryptedResult).join();
            byte[] encrypted = encryptedResult[0];
            
            byte[][] decryptedResult = new byte[1][];
            ctx.decryptAsync(encrypted, decryptedResult).join();
            byte[] decrypted = decryptedResult[0];
            
            ctx.shutdown();

            if (!message.equals(new String(decrypted))) {
                System.out.println("✗ FAILED: Сообщение не совпадает");
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

    private static boolean testInvalidKeySize() {
        System.out.println("Тест 5: Обработка неверного размера ключа");
        try {
            DES des = new DES();
            
            byte[] invalidKey = {0x01, 0x02, 0x03, 0x04};
            
            try {
                des.setEncryptionKey(invalidKey);
                System.out.println("✗ FAILED: Должно было выброситься исключение");
                return false;
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("8 bytes")) {
                    System.out.println("✓ PASSED");
                    return true;
                } else {
                    System.out.println("✗ FAILED: Неверное сообщение об ошибке");
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            return false;
        } finally {
            System.out.println();
        }
    }

    private static boolean testInvalidBlockSize() {
        System.out.println("Тест 6: Обработка неверного размера блока");
        try {
            DES des = new DES();
            
            byte[] key = new byte[8];
            des.setEncryptionKey(key);
            des.setDecryptionKey(key);

            byte[] invalidBlock = {0x01, 0x02, 0x03, 0x04};
            
            try {
                des.encrypt(invalidBlock);
                System.out.println("✗ FAILED: Должно было выброситься исключение");
                return false;
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("8 bytes")) {
                    System.out.println("✓ PASSED");
                    return true;
                } else {
                    System.out.println("✗ FAILED: Неверное сообщение об ошибке");
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("✗ FAILED: " + e.getMessage());
            return false;
        } finally {
            System.out.println();
        }
    }

    private static boolean testMultipleBlocks() {
        System.out.println("Тест 7: Шифрование нескольких блоков");
        try {
            DES des = new DES();
            
            byte[] key = {(byte)0xAA, (byte)0xBB, (byte)0xCC, (byte)0xDD,
                         (byte)0xEE, (byte)0xFF, (byte)0x00, (byte)0x11};
            des.setEncryptionKey(key);
            des.setDecryptionKey(key);

            byte[][] blocks = {
                {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08},
                {0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18},
                {0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28}
            };
            
            for (int i = 0; i < blocks.length; i++) {
                byte[] encrypted = des.encrypt(blocks[i]);
                byte[] decrypted = des.decrypt(encrypted);
                
                if (!Arrays.equals(blocks[i], decrypted)) {
                    System.out.println("✗ FAILED: Блок " + i + " не совпадает");
                    return false;
                }
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

