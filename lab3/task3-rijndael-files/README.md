# Task 3: Rijndael File Encryption Demo

## Overview

Demonstrates Rijndael (AES) encryption/decryption for files and pseudorandom byte sequences. Tests all combinations of cipher modes, padding schemes, block sizes (128/192/256 bits), key sizes (128/192/256 bits), and GF(2^8) irreducible polynomials. Uses implementation from task2 and interfaces from lab1.

## Implementation

### RijndaelFileDemo.java

Main demonstration class testing all configuration combinations.

**testTextFile()**
- Encrypts/decrypts test.txt (134 bytes)
- Configuration: AES-128, CBC mode, PKCS7 padding
- Verifies original = decrypt(encrypt(original))

**testBinaryFile()**
- Encrypts/decrypts random.bin (10 KB)
- Tests binary data integrity
- Same configuration as text test

**testDifferentBlockSizes()**
- Tests Rijndael with 128/192/256-bit blocks
- Fixed 128-bit key, CBC mode, PKCS7 padding
- Encrypts/decrypts 1 KB pseudorandom data
- Validates each block size configuration

**testDifferentKeySizes()**
- Tests AES with 128/192/256-bit keys
- Fixed 128-bit block, CBC mode, PKCS7 padding
- Encrypts/decrypts 1 KB pseudorandom data
- Validates each key size configuration

**testDifferentModes()**
- Tests all 6 cipher modes:
  - ECB (Electronic Codebook)
  - CBC (Cipher Block Chaining)
  - PCBC (Propagating CBC)
  - CFB (Cipher Feedback)
  - OFB (Output Feedback)
  - CTR (Counter)
- Each with 1 KB pseudorandom data
- Validates encrypt/decrypt cycle for each mode

**testDifferentPaddings()**
- Tests all 4 padding schemes:
  - ZEROS (zero padding)
  - PKCS7 (PKCS #7 padding)
  - ANSI_X923 (ANSI X.923 padding)
  - ISO_10126 (ISO 10126 padding)
- Each with 1 KB pseudorandom data
- Validates padding/unpadding correctness

**testDifferentModuli()**
- Tests 4 different GF(2^8) irreducible polynomials:
  - 0x11B (x^8 + x^4 + x^3 + x + 1) - AES standard
  - 0x11D (x^8 + x^4 + x^3 + x^2 + 1)
  - 0x12B (x^8 + x^5 + x^3 + x + 1)
  - 0x12D (x^8 + x^5 + x^3 + x^2 + 1)
- Different moduli produce different S-boxes
- Each encrypts/decrypts 1 KB pseudorandom data

**testLargeRandomData()**
- Tests scalability with 1 KB, 10 KB, 100 KB data
- Validates performance and correctness at scale
- Uses AES-128, CBC, PKCS7

### DetailedDemo.java

Visual demonstration with hex output showing differences.

**demonstrateModesComparison()**
- Encrypts same plaintext with ECB, CBC, CTR
- Shows hex output of each ciphertext
- Demonstrates mode impact: identical plaintext blocks produce different ciphertext in CBC/CTR but not ECB

**demonstratePaddingComparison()**
- Encrypts 4-byte plaintext with ZEROS, PKCS7, ANSI_X923
- Shows padded ciphertext length (all 16 bytes)
- Demonstrates padding differences in hex output

**demonstrateModulusImpact()**
- Encrypts same plaintext/key with 0x11B, 0x11D, 0x12B
- Shows completely different ciphertext outputs
- Proves field polynomial changes entire cipher behavior

**demonstrateBlockKeyVariations()**
- Tests 5 configurations:
  - 128-bit block: 128/192/256-bit keys
  - 192-bit block: 192-bit key
  - 256-bit block: 256-bit key
- Shows encrypted data size varies with block size
- 48-byte plaintext encrypted with each configuration

## Test Files

**test.txt**
- Plain text content (134 bytes)
- Sample: "This is a test text file..."
- Used for text file encryption test

**random.bin**
- Pseudorandom binary data (10,240 bytes)
- Generated with /dev/urandom
- Used for binary file encryption test

**Generated during execution:**
- `*.enc` - Encrypted versions of input files
- `*.dec` - Decrypted versions (should match originals)

## Test Coverage

### RijndaelFileDemo
- **Total tests**: 28
- Text files: 1 test
- Binary files: 1 test
- Block sizes: 3 tests (128, 192, 256)
- Key sizes: 3 tests (128, 192, 256)
- Cipher modes: 6 tests (ECB, CBC, PCBC, CFB, OFB, CTR)
- Padding modes: 4 tests (ZEROS, PKCS7, ANSI_X923, ISO_10126)
- GF(2^8) moduli: 4 tests (0x11B, 0x11D, 0x12B, 0x12D)
- Large data: 3 tests (1KB, 10KB, 100KB)

### DetailedDemo
- Visual demonstrations with hex output
- Mode comparison: 3 modes
- Padding comparison: 3 schemes
- Modulus comparison: 3 polynomials
- Block/key variations: 5 configurations

## Usage

```bash
./build.sh           # Compile all sources
./run-demo.sh        # Run RijndaelFileDemo (28 tests)
./run-detailed.sh    # Run DetailedDemo (visual output)
```

## Output Examples

### RijndaelFileDemo
```
--- Testing Text File Encryption ---
Text file encryption/decryption: PASS

--- Testing Different Cipher Modes ---
ECB: PASS
CBC: PASS
...

=== All tests completed successfully ===
```

### DetailedDemo
```
--- Cipher Modes Comparison ---
Plaintext: Hello World! This is a test message.
ECB ciphertext: AF FF 41 E2 B7 7F F8 0E ...
CBC ciphertext: 44 9D A9 D4 10 6D AF 09 ...
CTR ciphertext: 46 FB 0D 48 15 BC 05 59 ...
```

## Technical Details

### Cipher Mode Characteristics
- **ECB**: Deterministic, parallel encryption/decryption, no IV
- **CBC**: Sequential, requires IV, diffusion across blocks
- **PCBC**: Sequential, plaintext+ciphertext chaining, error propagation
- **CFB**: Stream cipher mode, sequential, self-synchronizing
- **OFB**: Stream cipher mode, keystream generation, no error propagation
- **CTR**: Parallel, converts block cipher to stream, requires counter

### Padding Behavior
- **ZEROS**: Adds zeros, ambiguous if plaintext ends with zero
- **PKCS7**: Adds N bytes of value N (N = padding length)
- **ANSI_X923**: Zeros + final byte = padding length
- **ISO_10126**: Random bytes + final byte = padding length

### GF(2^8) Modulus Impact
- Different moduli produce different multiplicative inverse tables
- S-box values completely different for each modulus
- Ciphertext entirely different even with same key/plaintext
- Decryption requires same modulus as encryption

### Performance
- 1 KB: Instant
- 10 KB: <100ms
- 100 KB: <500ms
- All modes and configurations pass validation

## Dependencies

From task2-rijndael:
- Rijndael.java
- GF256Service.java
- SymmetricCipher.java
- KeySchedule.java
- RoundFunction.java

From lab1:
- CipherMode.java
- PaddingMode.java
- CipherContext.java

