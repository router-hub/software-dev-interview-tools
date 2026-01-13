# Complete Guide: Cryptographic Token Generation & Password Hashing
## From First Principles to Production Code

This guide follows a structured learning path from basic concepts to implementation details, designed for interview mastery at any level.

---

## Chapter 1: Foundation - Why Cryptography Matters in Auth Systems

Authentication systems need to solve three fundamental problems:

**Password storage**: Users choose weak passwords (low entropy ~40 bits), requiring slow, expensive hashing

**Token generation**: System-generated tokens have high entropy (256 bits), enabling fast hashing

**OTP management**: Time-limited codes balance security with usability

> [!TIP]
> **Interview insight**: "The algorithm choice depends on input entropy. Low-entropy passwords need adaptive algorithms like BCrypt. High-entropy tokens can use fast hashes like SHA-256 without compromising security".

---

## Chapter 2: Randomness - SecureRandom Internals

### How SecureRandom Actually Works

When you call `new SecureRandom()`, Java initializes a cryptographically secure PRNG using system entropy:

**Linux (NativePRNG algorithm)**:
- `nextBytes()` reads from `/dev/urandom` (non-blocking entropy pool)
- `generateSeed()` reads from `/dev/random` (blocking entropy pool)
- Kernel entropy pool is seeded from hardware events (mouse movements, disk I/O, network packets)

**SHA1PRNG algorithm (fallback)**:
- Uses SHA-1 as the core PRNG function
- Initial seed from system properties + entropy gathering device
- Each `nextBytes()` call: `output = SHA1(counter || seed); counter++`

```java
// Internally: SecureRandom state machine
SecureRandom sr = new SecureRandom(); 
// Step 1: Select algorithm (NativePRNG on Linux)
// Step 2: Read 55 bytes from /dev/urandom for initial seed
// Step 3: Initialize internal state array

byte[] randomBytes = new byte[32];
sr.nextBytes(randomBytes);
// Step 4: Call native method (Linux): read(fd_urandom, buffer, 32)
// Step 5: Kernel reads from CSPRNG entropy pool
// Step 6: Return 32 unpredictable bytes
```

### Interview Question: Why Is SecureRandom Slow on First Call?

**Tricky answer**: First call triggers seed initialization from `/dev/random` which may block if system entropy is low. Subsequent calls use `/dev/urandom` (non-blocking). On cloud VMs with low entropy, first call can take 30+ seconds.

**Solution**: Initialize `SecureRandom` as static field during application startup:

```java
// Lazy initialization delays blocking to first usage
private SecureRandom sr = new SecureRandom(); // BAD in constructor

// Eager initialization at class loading time
private static final SecureRandom SECURE_RANDOM = new SecureRandom(); // GOOD
```

### Token Generation Implementation

```java
public String generateSecureToken() {
    byte[] randomBytes = new byte[32]; // 32 bytes = 256 bits
    SECURE_RANDOM.nextBytes(randomBytes);
    
    // URL-safe encoding without padding
    return Base64.getUrlEncoder()
                 .withoutPadding()
                 .encodeToString(randomBytes);
}
```

**Entropy calculation**: 32 bytes = 256 bits = 2^256 possible values = ~10^77 combinations (more than atoms in the universe).

---

## Chapter 3: Base64 Encoding - The 6-to-8 Bit Bridge

### Why Base64 Exists

Binary data (8-bit bytes) breaks in text protocols (HTTP headers, JSON, URLs) because bytes 0-31 and 127-255 are control characters. Base64 maps binary to 64 printable ASCII characters.

### Internal Working: 3-to-4 Conversion

Base64 groups 3 bytes (24 bits) into 4 characters (6 bits each):

```text
Binary input:  [01001101] [01100001] [01101110]  (3 bytes = 24 bits)
               └─────┬────┘└─────┬────┘└─────┬────┘
Regrouped:     [010011][010110][000101][101110]  (4 groups × 6 bits)
Base64:           T       W       F       u

Characters: "TWFu" (4 characters)
```

**Why padding with =**: If input isn't divisible by 3, pad to make output divisible by 4:

```java
// 1 byte input: "M"
byte[] input = {0x4D};  // 8 bits
// After encoding: 6 bits used + 2 bits padding = "TQ"
// Need 4 characters: "TQ==" (2 padding chars)

// 2 bytes input: "Ma"  
byte[] input = {0x4D, 0x61};  // 16 bits
// After encoding: 12 bits used + 4 bits padding = "TWE"
// Need 4 characters: "TWE=" (1 padding char)
```

**Why remove padding for tokens**: Padding is redundant - decoder infers original length from encoded length:

```text
Encoded length 2: (2 × 6) / 8 = 1.5 → 1 byte (after removing 2 pads)
Encoded length 3: (3 × 6) / 8 = 2.25 → 2 bytes (after removing 1 pad)
Encoded length 4: (4 × 6) / 8 = 3 bytes (no padding needed)
```

### Standard vs URL-Safe Base64

```java
byte[] bytes = {62, 63, 64}; // Binary: contains + / characters after encoding

// Standard Base64 (RFC 4648)
String standard = Base64.getEncoder().encodeToString(bytes);
// Output: "Pj9A" but can produce: a-z, A-Z, 0-9, +, /, =
// Problem: + becomes %2B in URLs, / becomes %2F

// URL-Safe Base64 (RFC 4648 §5)
String urlSafe = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
// Output: "Pj9A" with alphabet: a-z, A-Z, 0-9, -, _, (no padding)
// Safe in URLs, filenames, HTTP headers
```

> [!WARNING]
> **Interview trap**: "Can we use standard Base64 for JWT?" Answer: No, JWT signature might contain + or / which break URL transmission. JWTs mandate Base64url encoding (RFC 7515).

---

## Chapter 4: SHA-256 Internals - Merkle-Damgård Construction

### Step-by-Step: How SHA-256 Hashes Data

SHA-256 processes data in 512-bit blocks through a compression function:

#### Step 1: Message Padding

Pad message to be congruent to 448 mod 512:

```text
Original message: "abc" = 24 bits

Padding process:
1. Append bit '1': 24 + 1 = 25 bits
2. Append '0' bits until length ≡ 448 (mod 512)
   Need: 448 - 25 = 423 zero bits
3. Append 64-bit length: 24 in binary = 0x0000000000000018

Total: 24 + 1 + 423 + 64 = 512 bits (exactly 1 block)
```

#### Step 2: Initialize 8 Hash Values (H0-H7)

```java
// SHA-256 initial values (first 32 bits of fractional parts of sqrt of first 8 primes)
int[] H = {
    0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
    0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
};
```

#### Step 3: Compression Function (64 Rounds)

For each 512-bit block:

```java
// Expand 512 bits into 64 words (each 32 bits)
int[] W = new int[64];
// W[0..15] = direct copy of message block
// W[16..63] = calculated using shift/rotate operations

// 64 rounds of mixing
for (int i = 0; i < 64; i++) {
    // Bitwise operations: XOR, AND, rotate, shift
    // Mix current hash values (H) with message schedule (W[i]) and constants (K[i])
    // Update H values
}
```

#### Step 4: Output Final Hash

Concatenate H0-H7 as 256-bit (32-byte) hash.

### SHA-256 in Java: MessageDigest API

```java
MessageDigest digest = MessageDigest.getInstance("SHA-256");
// Internally: Loads SHA-256 provider (usually SUN provider)
// Creates 32-byte internal buffer + 8 hash state variables

byte[] hash = digest.digest(inputBytes);
// Step 1: Applies padding to input
// Step 2: Processes each 512-bit block through compression function
// Step 3: Returns final 32-byte hash
```

> [!IMPORTANT]
> **Interview question**: "Is MessageDigest thread-safe?" Answer: No. `digest()` modifies internal state. Use `ThreadLocal<MessageDigest>` or create new instances per thread.

### Why SHA-256 Is Fast (And Why That's Bad for Passwords)

SHA-256 is optimized for speed: modern CPUs compute 500+ MB/sec. GPUs can compute billions of hashes per second. For a 6-character password (62^6 = 56 billion combinations), GPU cracks SHA-256 in under 1 minute.

---

## Chapter 5: BCrypt Internals - Intentionally Slow Hashing

### The Eksblowfish Key Schedule

BCrypt is based on Blowfish cipher with expensive key setup (Eksblowfish):

#### Step 1: Generate Random Salt (128 bits)

```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
String hash = encoder.encode("password123");

// Internally step 1: Generate salt
byte[] salt = new byte[16];
SecureRandom.getInstanceStrong().nextBytes(salt); // 128-bit random salt
```

#### Step 2: Expensive Key Setup (2^cost iterations)

```java
// cost = 12 means 2^12 = 4096 iterations
int cost = 12;

// Initialize Blowfish state (P-array and S-boxes)
State state = new BlowfishState();

// Iterate 2^cost times
for (int i = 0; i < (1 << cost); i++) {
    // Round 1: Mix password into state
    state.expandKey(password);
    
    // Round 2: Mix salt into state  
    state.expandKey(salt);
}

// Final: Encrypt fixed string "OrpheanBeholderScryDoubt" 64 times
// using the expensive key schedule
```

**Why this is slow**: Each iteration performs 512 key expansion operations. At cost=12, that's 4096 × 512 = 2,097,152 operations.

### BCrypt Output Format

```text
$2b$12$nOUIs5kJ7naTuTFkBy1veu$K0kSxUFXfuaOKdOKf9xYT0KKIGSJwFa
│ │  │  │                    │
│ │  │  └─ Salt (22 chars)   └─ Hash (31 chars)
│ │  └─ Cost factor (12 = 2^12 iterations)
│ └─ Minor version
└─ Algorithm identifier (2b = BCrypt)
```

> [!TIP]
> **Interview trick**: "Where is the salt stored?" Answer: Embedded in the hash output. BCrypt output contains algorithm, cost, salt, and hash - everything needed for verification.

### Cost Factor Tuning

```java
// Cost 10: ~65ms per hash (default)
BCryptPasswordEncoder encoder10 = new BCryptPasswordEncoder(10);

// Cost 12: ~260ms per hash (recommended for 2026)
BCryptPasswordEncoder encoder12 = new BCryptPasswordEncoder(12);

// Cost 14: ~1040ms per hash (high-security)
BCryptPasswordEncoder encoder14 = new BCryptPasswordEncoder(14);
```

**Rule of thumb**: Each +1 to cost doubles execution time. Target 200-500ms for login operations on your hardware.

---

## Chapter 6: Decision Framework - Which Algorithm When?

### Use Case Matrix

| Scenario             | Algorithm        | Why                                | Code Pattern                  |
| -------------------- | ---------------- | ---------------------------------- | ----------------------------- |
| User password        | BCrypt (cost 12) | Low entropy needs adaptive hashing | `BCryptPasswordEncoder`       |
| API token (32 bytes) | SHA-256          | High entropy, fast verification    | `MessageDigest` + `HexFormat` |
| Session token        | SHA-256          | High entropy, frequent lookups     | `MessageDigest` + `HexFormat` |
| 6-digit OTP          | SHA-256          | Short-lived, rate-limited          | `MessageDigest` + Base64      |
| Password reset       | SHA-256          | High entropy token, single-use     | `MessageDigest` + `HexFormat` |

### Complete Token Generation & Verification Flow

**Generation** (return raw token to user once):

```java
public TokenResponse generateApiToken(Long userId) {
    // Step 1: Generate 32 bytes of cryptographic randomness
    byte[] randomBytes = new byte[32];
    SECURE_RANDOM.nextBytes(randomBytes);
    
    // Step 2: Encode as URL-safe string (visible to user)
    String rawToken = Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(randomBytes);
    
    // Step 3: Hash for database storage
    String tokenHash = hashToken(rawToken);
    
    // Step 4: Store hash + metadata in database
    tokenRepository.save(new ApiToken(
        userId,
        tokenHash,
        Instant.now().plus(90, ChronoUnit.DAYS) // Expiry
    ));
    
    // Step 5: Return raw token to user (ONLY TIME IT'S VISIBLE)
    return new TokenResponse(rawToken); 
}

private String hashToken(String rawToken) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
    return HexFormat.of().formatHex(hashBytes); // 64-char hex
}
```

**Verification** (user presents token in API call):

```java
public boolean verifyApiToken(String userProvidedToken) {
    // Step 1: Hash incoming token
    String incomingHash = hashToken(userProvidedToken);
    
    // Step 2: Lookup in database by hash
    Optional<ApiToken> tokenOpt = tokenRepository.findByTokenHash(incomingHash);
    
    if (tokenOpt.isEmpty()) {
        return false; // Token doesn't exist
    }
    
    ApiToken token = tokenOpt.get();
    
    // Step 3: Check expiry
    if (token.getExpiresAt().isBefore(Instant.now())) {
        return false; // Token expired
    }
    
    // Step 4: Check if revoked
    if (token.isRevoked()) {
        return false;
    }
    
    return true;
}
```

**Security property**: Database compromise leaks token hashes, not raw tokens. Attacker cannot reverse SHA-256 hash to recover 256-bit raw token.

---

## Chapter 7: OTP (One-Time Password) System

### OTP Generation with Uniform Distribution

```java
private String generateOtp() {
    // Generate random int in range [100000, 999999]
    int otp = 100000 + SECURE_RANDOM.nextInt(900000);
    return String.valueOf(otp);
}
```

> [!IMPORTANT]
> **Interview question**: "Why not `SECURE_RANDOM.nextInt(1000000)`?" Answer: Would produce OTPs like 000042 (leading zeros). Users expect 6-digit format 100000-999999.

**Entropy calculation**: log₂(900000) ≈ 19.78 bits of entropy. Lower than tokens but acceptable with rate limiting.

### OTP Storage & Verification

```java
public void sendOtp(String phoneNumber) {
    // Generate OTP
    String rawOtp = generateOtp(); // e.g., "482716"
    
    // Hash for storage
    String otpHash = hashOtp(rawOtp);
    
    // Store hash + expiry + attempt count
    otpRepository.save(new OtpRecord(
        phoneNumber,
        otpHash,
        Instant.now().plus(5, ChronoUnit.MINUTES), // 5-min expiry
        0 // attempt count
    ));
    
    // Send raw OTP via SMS
    smsService.send(phoneNumber, "Your OTP: " + rawOtp);
}

public boolean verifyOtp(String phoneNumber, String userOtp) {
    OtpRecord record = otpRepository.findByPhone(phoneNumber);
    
    // Check expiry
    if (record.getExpiresAt().isBefore(Instant.now())) {
        return false;
    }
    
    // Rate limiting: Max 5 attempts
    if (record.getAttemptCount() >= 5) {
        return false;
    }
    
    // Increment attempt counter
    record.incrementAttempts();
    otpRepository.save(record);
    
    // Verify hash
    String userOtpHash = hashOtp(userOtp);
    return userOtpHash.equals(record.getOtpHash());
}

private String hashOtp(String otp) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(hash);
}
```

**Why SHA-256 for OTP**: Time constraint (5 minutes) + rate limiting (5 attempts) + notification on failure = brute-force infeasible despite low entropy.

---

## Chapter 8: Advanced Interview Questions

### Q1: "Why not use HMAC-SHA256 for tokens?"

**Answer**: HMAC requires a shared secret key. For token hashing, we don't have a shared secret - we're verifying that the user-provided token matches our stored hash. HMAC is used when both parties know the secret (e.g., JWT signature verification).

**Code comparison**:

```java
// SHA-256 (one-way hash, no secret needed)
String hash = hashToken(rawToken); // Can't reverse hash to get rawToken

// HMAC-SHA256 (requires secret key)
Mac hmac = Mac.getInstance("HmacSHA256");
SecretKey key = new SecretKeySpec(secretBytes, "HmacSHA256");
hmac.init(key);
byte[] signature = hmac.doFinal(data); // Verifiable with same key
```

### Q2: "Can we use BCrypt for token hashing?"

**Answer**: Technically yes, but it's a poor choice. BCrypt costs 200ms per hash at cost=12. For an API receiving 1000 requests/sec, token verification would consume 200 CPU cores. SHA-256 verifies in <1ms, handling the same load with 1 core.

### Q3: "Should we salt SHA-256 token hashes?"

**Tricky answer**: No, salting provides no security benefit for high-entropy tokens. Salt prevents rainbow table attacks on common inputs. Tokens have 2^256 possible values - no rainbow table can store that many precomputed hashes (would require 10^77 petabytes of storage).

### Q4: "What's the difference between HexFormat and Base64 for hash output?"

```java
byte[] hash = digest.digest(input); // 32 bytes from SHA-256

// Hex encoding: 2 characters per byte
String hex = HexFormat.of().formatHex(hash); // 64 characters
// "a3f5b8c2..." (alphabet: 0-9, a-f)

// Base64 encoding: 1.33 characters per byte  
String base64 = Base64.getEncoder().encodeToString(hash); // 44 characters
// "o/W4wg==" (alphabet: A-Z, a-z, 0-9, +, /, =)
```

**Use hex when**: Database column is `CHAR(64)`, need case-insensitive lookups, want URL-safe output

**Use Base64 when**: Space-constrained (25% smaller), case-sensitive OK, short-lived data (OTPs)

### Q5: "How to handle MessageDigest in multi-threaded environment?"

**Three patterns**:

```java
// Pattern 1: ThreadLocal (best for high-throughput)
private static final ThreadLocal<MessageDigest> DIGEST_THREAD_LOCAL = 
    ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    });

public String hash1(String token) {
    MessageDigest digest = DIGEST_THREAD_LOCAL.get();
    digest.reset(); // Clear previous state
    byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
    return HexFormat.of().formatHex(hash);
}

// Pattern 2: New instance (simple, slight overhead)
public String hash2(String token) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
    return HexFormat.of().formatHex(hash);
}

// Pattern 3: Synchronized (BAD - serializes all threads)
private static final MessageDigest SHARED_DIGEST = 
    MessageDigest.getInstance("SHA-256");

public synchronized String hash3(String token) { // Bottleneck!
    byte[] hash = SHARED_DIGEST.digest(token.getBytes(StandardCharsets.UTF_8));
    return HexFormat.of().formatHex(hash);
}
```

**Benchmark**: ThreadLocal: 1M ops/sec, New instance: 800K ops/sec, Synchronized: 50K ops/sec.

### Q6: "What happens if we don't specify charset in getBytes()?"

```java
// Dangerous: Uses platform default charset
byte[] bytes1 = "password".getBytes(); // ISO-8859-1 on some systems, UTF-8 on others

// Correct: Explicit charset
byte[] bytes2 = "password".getBytes(StandardCharsets.UTF_8);
```

> [!CAUTION]
> **Interview trap**: Password "café" produces different hashes on different systems without explicit charset. Always use `StandardCharsets.UTF_8`.

---

## Chapter 9: Production Checklist

### Security Hardening

```java
public class SecureTokenService {
    // ✅ Static final SecureRandom (initialized once)
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    // ✅ ThreadLocal MessageDigest
    private static final ThreadLocal<MessageDigest> DIGEST = 
        ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 not available", e);
            }
        });
    
    public String generateToken() {
        // ✅ 32 bytes = 256 bits entropy
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        
        // ✅ URL-safe Base64 without padding
        return Base64.getUrlEncoder()
                     .withoutPadding()
                     .encodeToString(randomBytes);
    }
    
    public String hashToken(String rawToken) {
        // ✅ Input validation
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null/empty");
        }
        
        // ✅ Explicit charset
        MessageDigest digest = DIGEST.get();
        digest.reset(); // ✅ Clear previous state
        byte[] hashBytes = digest.digest(
            rawToken.getBytes(StandardCharsets.UTF_8)
        );
        
        // ✅ Hex encoding for database indexing
        return HexFormat.of().formatHex(hashBytes);
    }
}
```

### Common Mistakes to Avoid

- ❌ `new Random()` for tokens (predictable)
- ❌ SHA-256 for passwords (too fast)
- ❌ BCrypt for high-frequency tokens (too slow)
- ❌ Standard Base64 for URLs (breaks on + / =)
- ❌ Shared `MessageDigest` without synchronization (race condition)
- ❌ Storing raw tokens in database (leaks on breach)
- ❌ OTP without rate limiting (brute-force attack)
- ❌ Tokens without expiry (indefinite exposure window)

---

## Summary: Interview Decision Tree

```text
Input type?
├─ User password (low entropy)
│  └─ Use: BCryptPasswordEncoder(12)
│     Reason: Adaptive hashing resists brute-force
│
├─ System-generated token (high entropy)
│  ├─ High traffic API (1000+ req/sec)
│  │  └─ Use: SHA-256 + HexFormat
│  │     Reason: <1ms verification, efficient indexing
│  │
│  └─ Low traffic or space-constrained
│     └─ Use: SHA-256 + Base64
│        Reason: 25% smaller storage
│
└─ OTP (low entropy + time-limited)
   └─ Use: SHA-256 + rate limiting + short expiry
      Reason: Time constraints prevent brute-force
```

This foundation covers 90% of authentication interview questions from junior to staff engineer levels.