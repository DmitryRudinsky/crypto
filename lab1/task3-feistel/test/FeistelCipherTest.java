import java.util.Arrays;

public class FeistelCipherTest {
    
    public static void main(String[] args) {
        System.out.println("=== Тестирование сети Фейстеля ===\n");
        
        int passed = 0;
        int failed = 0;

        passed += testBasicEncryptDecrypt() ? 1 : 0;
        failed += testBasicEncryptDecrypt() ? 0 : 1;

        passed += testMultipleRounds() ? 1 : 0;
        failed += testMultipleRounds() ? 0 : 1;

        passed += testDifferentBlockSizes() ? 1 : 0;
        failed += testDifferentBlockSizes() ? 0 : 1;

        passed += testWithCipherContext() ? 1 : 0;
        failed += testWithCipherContext() ? 0 : 1;

        passed += testInvalidBlockSize() ? 1 : 0;
        failed += testInvalidBlockSize() ? 0 : 1;

        passed += testKeyNotSet() ? 1 : 0;
        failed += testKeyNotSet() ? 0 : 1;

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
            KeySchedule keySchedule = new SimpleKeySchedule(4);
            RoundFunction roundFunction = new SimpleRoundFunction();
            FeistelCipher cipher = new FeistelCipher(keySchedule, roundFunction);

            byte[] key = {0x01, 0x02, 0x03, 0x04};
            cipher.setEncryptionKey(key);
            cipher.setDecryptionKey(key);

            byte[] plaintext = {0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x21, 0x21, 0x21};
            byte[] encrypted = cipher.encrypt(plaintext);
            byte[] decrypted = cipher.decrypt(encrypted);

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
            return false;
        } finally {
            System.out.println();
        }
    }

    private static boolean testMultipleRounds() {
        System.out.println("Тест 2: Разное количество раундов");
        try {
            byte[] key = {0x01, 0x02, 0x03, 0x04};
            byte[] plaintext = {0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0};

            int[] roundCounts = {2, 4, 8, 16};
            
            for (int rounds : roundCounts) {
                KeySchedule keySchedule = new SimpleKeySchedule(rounds);
                RoundFunction roundFunction = new SimpleRoundFunction();
                FeistelCipher cipher = new FeistelCipher(keySchedule, roundFunction);

                cipher.setEncryptionKey(key);
                cipher.setDecryptionKey(key);

                byte[] encrypted = cipher.encrypt(plaintext);
                byte[] decrypted = cipher.decrypt(encrypted);

                if (!Arrays.equals(plaintext, decrypted)) {
                    System.out.println("✗ FAILED: Ошибка при " + rounds + " раундах");
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

    private static boolean testDifferentBlockSizes() {
        System.out.println("Тест 3: Различные размеры блоков");
        try {
            KeySchedule keySchedule = new SimpleKeySchedule(4);
            RoundFunction roundFunction = new SimpleRoundFunction();
            FeistelCipher cipher = new FeistelCipher(keySchedule, roundFunction);

            byte[] key = {0x01, 0x02, 0x03, 0x04};
            cipher.setEncryptionKey(key);
            cipher.setDecryptionKey(key);

            int[] blockSizes = {8, 16, 32, 64};
            
            for (int size : blockSizes) {
                byte[] plaintext = new byte[size];
                for (int i = 0; i < size; i++) {
                    plaintext[i] = (byte) (i & 0xFF);
                }

                byte[] encrypted = cipher.encrypt(plaintext);
                byte[] decrypted = cipher.decrypt(encrypted);

                if (!Arrays.equals(plaintext, decrypted)) {
                    System.out.println("✗ FAILED: Ошибка для блока размером " + size);
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

    private static boolean testWithCipherContext() {
        System.out.println("Тест 4: Интеграция с CipherContext");
        try {
            KeySchedule keySchedule = new SimpleKeySchedule(8);
            RoundFunction roundFunction = new SimpleRoundFunction();
            FeistelCipher cipher = new FeistelCipher(keySchedule, roundFunction);

            byte[] key = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
            byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            
            cipher.setEncryptionKey(key);
            cipher.setDecryptionKey(key);

            CipherContext ctx = new CipherContext(
                cipher,
                CipherMode.CBC,
                PaddingMode.PKCS7,
                8,
                iv
            );

            String message = "Feistel network test";
            byte[] encrypted = ctx.encryptAsync(message.getBytes()).join();
            byte[] decrypted = ctx.decryptAsync(encrypted).join();
            
            ctx.shutdown();

            if (!message.equals(new String(decrypted))) {
                System.out.println("✗ FAILED: Сообщение не совпадает");
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

    private static boolean testInvalidBlockSize() {
        System.out.println("Тест 5: Обработка нечетного размера блока");
        try {
            KeySchedule keySchedule = new SimpleKeySchedule(4);
            RoundFunction roundFunction = new SimpleRoundFunction();
            FeistelCipher cipher = new FeistelCipher(keySchedule, roundFunction);

            byte[] key = {0x01, 0x02, 0x03, 0x04};
            cipher.setEncryptionKey(key);
            cipher.setDecryptionKey(key);

            byte[] oddBlock = {0x01, 0x02, 0x03, 0x04, 0x05};
            
            try {
                cipher.encrypt(oddBlock);
                System.out.println("✗ FAILED: Должно было выброситься исключение");
                return false;
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("even")) {
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

    private static boolean testKeyNotSet() {
        System.out.println("Тест 6: Попытка шифрования без установки ключа");
        try {
            KeySchedule keySchedule = new SimpleKeySchedule(4);
            RoundFunction roundFunction = new SimpleRoundFunction();
            FeistelCipher cipher = new FeistelCipher(keySchedule, roundFunction);

            byte[] block = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
            
            try {
                cipher.encrypt(block);
                System.out.println("✗ FAILED: Должно было выброситься исключение");
                return false;
            } catch (IllegalStateException e) {
                if (e.getMessage().contains("key not set")) {
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
}

