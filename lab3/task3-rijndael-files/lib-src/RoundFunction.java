public interface RoundFunction {
    byte[] encryptBlock(byte[] block, byte[] roundKey);
    byte[] decryptBlock(byte[] block, byte[] roundKey);
}

