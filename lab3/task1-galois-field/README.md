# Task 1: Galois Field GF(2^8) Operations

## Overview

Stateless service for operations on binary polynomials in GF(2^8). Elements and moduli are represented as single-byte values. All computations use bitwise operations.

## Implementation: GF256Service.java

### Core Operations

**add(byte a, byte b)**
- XOR operation for polynomial addition in GF(2^8)
- Returns a ⊕ b

**multiplyMod(byte a, byte b, byte modulus)**
- Polynomial multiplication modulo irreducible polynomial
- Uses shift-and-XOR algorithm with 8 iterations
- Validates modulus irreducibility before operation
- Throws IllegalArgumentException for reducible modulus

**inverse(byte a, byte modulus)**
- Computes multiplicative inverse using Extended Euclidean Algorithm
- Returns element b such that a × b ≡ 1 (mod modulus)
- Validates modulus irreducibility
- Throws IllegalArgumentException for zero or reducible modulus

**isIrreducible(int poly)**
- Tests if degree-8 polynomial is irreducible over GF(2)
- Checks proper divisors {1, 2, 4} of degree 8
- Verifies gcd(x^(2^d) - x, p) = 1 for each divisor d
- Confirms x^256 ≡ x (mod p)

**getAllIrreduciblePolynomials()**
- Enumerates all irreducible polynomials of degree 8
- Iterates 0x100 to 0x1FF, testing each with isIrreducible()
- Returns exactly 30 polynomials

**factorize(int poly)**
- Decomposes arbitrary-degree polynomial into irreducible factors
- Trial division by all degree-8 irreducible polynomials
- Returns list of irreducible factors

### Helper Methods

- **degree(int poly)**: Returns polynomial degree
- **multiplyPoly(int a, int b)**: XOR-based polynomial multiplication
- **modPoly(int a, int b)**: Polynomial modulo operation
- **dividePoly(int a, int b)**: Polynomial division (quotient)
- **gcdPoly(int a, int b)**: Euclidean algorithm for polynomial GCD

## Tests: GF256ServiceTest.java

**testAddition()**
- 0x53 + 0xCA = 0x99
- 0xFF + 0xFF = 0x00
- 0x00 + 0xAB = 0xAB
- Identity and commutative properties

**testMultiplication()**
- Multiplication mod 0x11B (AES polynomial)
- 0x53 × 0xCA ≡ 0x01 (mod 0x11B)
- Multiplicative identity
- Zero property

**testInverse()**
- Verifies a × a^(-1) ≡ 1 (mod 0x11B)
- Tests inverse of 0x53, 0xCA, 0x02
- Confirms 0x53 and 0xCA are multiplicative inverses

**testIrreducibility()**
- Validates known irreducible polynomials: 0x11B, 0x11D, 0x12B, 0x12D
- Confirms reducible polynomials: 0x11A, 0x100, 0x11C

**testGetAllIrreducibles()**
- Verifies exactly 30 irreducible degree-8 polynomials
- Validates each polynomial in list with isIrreducible()
- Confirms presence of 0x1B (0x11B) and 0x1D (0x11D)

**testFactorization()**
- Factorizes composite polynomials
- Edge cases: 0 and 1

**testExceptions()**
- Multiplication with reducible modulus throws
- Inverse with reducible modulus throws
- Inverse of zero throws

## Demo: GF256Demo.java

1. Addition: 0x53 + 0xCA = 0x99
2. Irreducibility check: 0x11B is irreducible
3. Multiplication: 0x53 × 0xCA mod 0x11B = 0x01
4. Inverse computation and verification: 0x53^(-1) = 0xCA
5. Lists all 30 irreducible polynomials (first 10 shown)
6. Factorizes polynomial 0x1E2F
7. Demonstrates exception handling for reducible modulus

## Usage

```bash
./build.sh       # Compile
./run-tests.sh   # Run 57 tests
./run-demo.sh    # Run demonstration
```

## Technical Notes

- Polynomials of degree 8 require 9 bits (e.g., 0x11B)
- Byte representation stores lower 8 bits (0x1B for 0x11B)
- Methods accepting modulus internally reconstruct 9-bit form: (modulus & 0xFF) | 0x100
- All arithmetic operations use XOR (no carry/borrow)
- Irreducibility test has O(n^2) complexity for degree n

