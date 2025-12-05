# Task 2: Rijndael (AES) Implementation

## Overview

Rijndael cipher implementation with configurable GF(2^8) modulus. Supports multiple block and key sizes (128/192/256 bits). Uses GF256Service from task1 for all field arithmetic. Implements lab1 interfaces (SymmetricCipher, KeySchedule).

## Implementation: Rijndael.java

### Constructor

**Rijndael(int blockSizeBits, int keySizeBits, byte modulus)**
- Validates block and key sizes (128, 192, or 256 bits)
- Configures cipher parameters: Nb (columns), Nk (key words), Nr (rounds)
- Calls initializeSBoxes() with specified modulus

### S-Box Generation

**initializeSBoxes()**
- Lazy initialization based on constructor modulus
- Direct computation: sBox[x] = affineTransform(inverse(x))
- Inverse S-box: invSBox[sBox[x]] = x
- No circular dependency between forward/inverse S-boxes

**affineTransform(byte b)**
- Applies AES affine transformation: b' = Ab ⊕ 0x63
- Bitwise rotation and XOR operations
- Matrix multiplication in GF(2)

### Key Expansion

**expandKey(byte[] key)**
- Generates Nr+1 round keys from master key
- Uses rotWord, subWord, and Rcon for key schedule
- Special handling for Nk > 6 (AES-256)
- Returns array of round keys reusable across multiple encryptions

**rcon(int i)**
- Round constant generation in GF(2^8)
- Powers of x: x^(i-1) computed using modulus

### Encryption

**encrypt(byte[] block)**
1. Convert block to 4×Nb state matrix (column-major)
2. Initial round: AddRoundKey(state, roundKey[0])
3. Rounds 1 to Nr-1:
   - SubBytes (S-box substitution)
   - ShiftRows (cyclic row shifts)
   - MixColumns (GF(2^8) matrix multiplication)
   - AddRoundKey
4. Final round (no MixColumns):
   - SubBytes
   - ShiftRows
   - AddRoundKey(state, roundKey[Nr])
5. Convert state back to byte array

### Decryption

**decrypt(byte[] block)**
1. Convert block to state matrix
2. Initial: AddRoundKey(state, roundKey[Nr])
3. Rounds Nr-1 to 1:
   - InvShiftRows
   - InvSubBytes
   - AddRoundKey
   - InvMixColumns
4. Final round:
   - InvShiftRows
   - InvSubBytes
   - AddRoundKey(state, roundKey[0])
5. Convert state back to byte array

### Core Operations

**subBytes(state)** / **invSubBytes(state)**
- Applies S-box / inverse S-box to each state byte

**shiftRows(state)** / **invShiftRows(state)**
- Row 0: no shift
- Row 1: left shift by 1
- Row 2: left shift by 2
- Row 3: left shift by 3
- Inverse performs right shifts

**mixColumns(state)** / **invMixColumns(state)**
- Matrix multiplication in GF(2^8) using configured modulus
- Forward matrix: [02 03 01 01; 01 02 03 01; 01 01 02 03; 03 01 01 02]
- Inverse matrix: [0E 0B 0D 09; 09 0E 0B 0D; 0D 09 0E 0B; 0B 0D 09 0E]
- Uses GF256Service.multiplyMod() for all multiplications

**addRoundKey(state, roundKey)**
- XOR state with round key

## Tests: RijndaelTest.java

**testAES128()**
- Encrypts/decrypts 16-byte block with 128-bit key
- Verifies plaintext = decrypt(encrypt(plaintext))
- Confirms ciphertext ≠ plaintext

**testAES192()**
- Tests 128-bit block with 192-bit key
- Standard AES-192 configuration

**testAES256()**
- Tests 128-bit block with 256-bit key
- Standard AES-256 configuration

**testRijndaelVariants()**
- Tests 6 non-standard configurations:
  - 192-bit blocks: 128/192/256-bit keys
  - 256-bit blocks: 128/192/256-bit keys
- Verifies all combinations work correctly

**testKeyReuse()**
- Sets key once, encrypts/decrypts 5 different blocks
- Validates round key reuse without re-expansion

**testCustomModulus()**
- Tests 4 different irreducible polynomials: 0x11D, 0x12B, 0x12D, 0x139
- Verifies cipher works with any valid GF(2^8) modulus
- Each produces different S-boxes and ciphertext

## Demo: RijndaelDemo.java

1. **AES-128**: 16-byte block, 16-byte key, modulus 0x11B
2. **AES-192**: 16-byte block, 24-byte key, modulus 0x11B
3. **AES-256**: 16-byte block, 32-byte key, modulus 0x11B
4. **Rijndael-128/192**: 16-byte block, 24-byte key
5. **Rijndael-192/256**: 24-byte block, 32-byte key
6. **Custom Modulus**: AES-128 with modulus 0x12B

Each test:
- Generates deterministic key and plaintext
- Encrypts plaintext
- Decrypts ciphertext
- Displays hex values and match status

## Usage

```bash
./build.sh       # Compile
./run-tests.sh   # Run 19 tests
./run-demo.sh    # Run demonstration
```

## Technical Details

### State Matrix Layout
- Column-major order: block[c*4 + r] = state[r][c]
- Enables efficient column operations in MixColumns

### Round Count
- Nr = max(Nb, Nk) + 6
- AES-128: 10 rounds
- AES-192: 12 rounds
- AES-256: 14 rounds

### GF(2^8) Operations
- All field arithmetic delegated to GF256Service
- Multiplication constants: 0x02, 0x03 (forward); 0x09, 0x0B, 0x0D, 0x0E (inverse)
- Configurable reduction polynomial

### Key Reuse
- expandKey() called once in setEncryptionKey()
- Round keys stored in encryptionRoundKeys[][]
- Both encryption and decryption use same round keys (different order)

### Modulus Configuration
- S-boxes computed at construction time
- Different moduli produce different S-boxes
- Must use same modulus for encrypt/decrypt

