# DEAL (Data Encryption Algorithm with Larger blocks)

## Overview

DEAL is a 128-bit block cipher that uses DES (Data Encryption Standard) as a building block. It was designed to create a larger, more secure block cipher by reusing the well-studied DES algorithm in a Feistel network structure.

## Architecture

### Key Components

1. **DESAdapter** (`DESAdapter.java`)
   - Implements the `RoundFunction` interface
   - Wraps DES encryption to use it as a round function
   - Adapter pattern allows seamless integration into the Feistel framework

2. **DEALKeySchedule** (`DEALKeySchedule.java`)
   - Implements the `KeySchedule` interface
   - Expands a 192-bit master key into round keys
   - Cycles through three DES keys (K1, K2, K3)

3. **DEAL** (`DEAL.java`)
   - Main cipher class implementing `SymmetricCipher` interface
   - Uses `FeistelCipher` with `DESAdapter` as the round function
   - Configurable number of rounds (default: 6)

## Specifications

| Feature | Value |
|---------|-------|
| Block Size | 128 bits (16 bytes) |
| Key Size | 192 bits (24 bytes) |
| Rounds | 6 (default, configurable) |
| Structure | Feistel Network with DES as F-function |

## Design Pattern: Adapter

The implementation demonstrates the **Adapter Pattern** by wrapping DES to work as a round function:

```
┌─────────────────────────────────────────────┐
│           Feistel Cipher Framework          │
│                                             │
│  Requires: RoundFunction interface          │
│    - encryptBlock(block, roundKey)          │
│    - decryptBlock(block, roundKey)          │
└──────────────────┬──────────────────────────┘
                   │
                   │ uses
                   ▼
         ┌─────────────────┐
         │   DESAdapter    │
         │  (Adapter)      │
         │                 │
         │  Implements:    │
         │  RoundFunction  │
         └────────┬────────┘
                  │
                  │ wraps
                  ▼
         ┌─────────────────┐
         │      DES        │
         │  (Adaptee)      │
         │                 │
         │  Original DES   │
         │  cipher class   │
         └─────────────────┘
```

### Why Use an Adapter?

1. **Interface Mismatch**: DES implements `SymmetricCipher` interface, but Feistel network needs a `RoundFunction`
2. **Reusability**: Allows existing DES implementation to be used without modification
3. **Flexibility**: Makes DES compatible with the generic Feistel cipher framework
4. **Separation of Concerns**: Keeps DES and Feistel implementations independent

## How DEAL Works

### Encryption Process

1. **Key Expansion**: 192-bit master key → 6 round keys (K1, K2, K3, K1, K2, K3)
2. **Block Split**: 128-bit plaintext → L0 (64 bits) | R0 (64 bits)
3. **Feistel Rounds** (6 iterations):
   ```
   For i = 1 to 6:
       Li = Ri-1
       Ri = Li-1 ⊕ DES(Ri-1, Ki)
   ```
4. **Final Swap**: Output = R6 | L6

### Decryption Process

Same as encryption but with round keys in reverse order: K3, K2, K1, K3, K2, K1

## Key Schedule

The DEAL key schedule divides the 192-bit master key into three 64-bit DES keys:

```
Master Key (192 bits):
┌──────────┬──────────┬──────────┐
│    K1    │    K2    │    K3    │
│ (64 bits)│ (64 bits)│ (64 bits)│
└──────────┴──────────┴──────────┘

Round Keys:
Round 1: K1
Round 2: K2
Round 3: K3
Round 4: K1  (cycle repeats)
Round 5: K2
Round 6: K3
```

## Usage Examples

### Basic Encryption

```java
// Create a 192-bit (24 bytes) key
byte[] key = "DEAL_SECRET_KEY_192BITS!".getBytes();

// Create a 128-bit (16 bytes) plaintext
byte[] plaintext = "Hello DEAL!     ".getBytes();

// Initialize DEAL cipher
DEAL deal = new DEAL();
deal.setEncryptionKey(key);
deal.setDecryptionKey(key);

// Encrypt and decrypt
byte[] ciphertext = deal.encrypt(plaintext);
byte[] decrypted = deal.decrypt(ciphertext);
```

### Custom Number of Rounds

```java
// Create DEAL with 12 rounds instead of default 6
DEAL deal = new DEAL(12);
deal.setEncryptionKey(key);

byte[] ciphertext = deal.encrypt(plaintext);
```

### With CipherContext (for longer messages)

```java
byte[] key = "DEAL_SECRET_KEY_192BITS!".getBytes();
String message = "This is a longer message that needs padding.";

DEAL deal = new DEAL();
CipherContext ctx = new CipherContext(
    deal,
    CipherMode.ECB,
    PaddingMode.PKCS7
);
ctx.setKey(key);

byte[] ciphertext = ctx.encrypt(message.getBytes());
byte[] decrypted = ctx.decrypt(ciphertext);
```

## Security Considerations

### Advantages
- **Larger Block Size**: 128 bits provides better security than DES's 64-bit blocks
- **Proven Building Block**: Uses DES, a well-studied algorithm
- **Increased Key Size**: 192 bits is more resistant to brute force than DES's 56 effective bits

### Limitations
- **DES Foundation**: Inherits any weaknesses in DES
- **Performance**: Multiple DES operations per round can be slower than native 128-bit ciphers
- **Not a Modern Standard**: For production use, prefer AES or other modern ciphers

### Recommended Use Cases
- Educational purposes
- Understanding cipher construction
- Historical cryptography studies
- Systems requiring DES compatibility with larger blocks

## Running the Code

### Compile
```bash
bash build.sh
```

### Run Demo
```bash
bash run-deal-demo.sh
```

### Run Tests
```bash
bash run-deal-tests.sh
```

## Implementation Details

### File Structure

```
src/
├── DESAdapter.java       # Adapter: DES → RoundFunction
├── DEALKeySchedule.java  # Key expansion for DEAL
├── DEAL.java             # Main DEAL cipher class
└── DEALDemo.java         # Demonstration program

test/
└── DEALTest.java         # Comprehensive test suite
```

### Class Relationships

```
DEAL
├── uses FeistelCipher
│   ├── uses DEALKeySchedule (implements KeySchedule)
│   └── uses DESAdapter (implements RoundFunction)
│       └── uses DES
│           └── uses FeistelCipher
│               ├── uses DESKeySchedule
│               └── uses DESRoundFunction
```

## References

- Knudsen, L. R. (1998). "DEAL - A 128-bit Block Cipher"
- Original paper: https://csrc.nist.gov/projects/block-cipher-techniques/bcm/proposed-modes
- DES specification: FIPS PUB 46-3

## License

This implementation is for educational purposes.

