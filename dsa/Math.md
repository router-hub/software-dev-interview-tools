
## L1: Count Digits
**Question:** Count number of digits in a number N.
**Intuition:** Keep dividing by 10 until number becomes 0. Each division removes one digit.
**Logic:** Divide by 10 repeatedly OR use logarithm: digits = floor(log10(N)) + 1.

**Java:**
```java
// Iterative approach
int countDigits(int n) {
 if (n == 0) return 1;
 int count = 0;
 while (n > 0) {
 count++;
 n /= 10;
 }
 return count;
}

// Math approach
int countDigitsLog(int n) {
 if (n == 0) return 1;
 return (int)Math.floor(Math.log10(n)) + 1;
}
// Time: O(log10(n)) for iterative, O(1) for log approach
```

---

## L2: Reverse a Number
**Question:** Reverse the digits of a number.
**Intuition:** Extract last digit repeatedly (n%10), build reversed number by multiplying by 10 and adding digit.
**Logic:** reversed = reversed * 10 + (n % 10), then n = n / 10.

**Java:**
```java
int reverseNumber(int n) {
 int reversed = 0;
 while (n > 0) {
 int digit = n % 10;
 reversed = reversed * 10 + digit;
 n /= 10;
 }
 return reversed;
}

// Handle negative numbers
int reverseWithSign(int n) {
 boolean isNegative = n < 0;
 n = Math.abs(n);

 int reversed = 0;
 while (n > 0) {
 // Check for overflow before multiplication
 if (reversed > Integer.MAX_VALUE / 10) return 0;
 reversed = reversed * 10 + n % 10;
 n /= 10;
 }
 return isNegative ? -reversed : reversed;
}
```

---

## L3: Check Palindrome Number
**Question:** Check if number reads same forwards and backwards.
**Intuition:** Reverse the number and compare with original. If same, it's a palindrome.
**Logic:** Reverse and compare OR reverse only half and compare.

**Java:**
```java
// Simple approach
boolean isPalindrome(int n) {
 if (n < 0) return false;
 int original = n;
 int reversed = 0;

 while (n > 0) {
 reversed = reversed * 10 + n % 10;
 n /= 10;
 }
 return original == reversed;
}

// Optimal: Reverse only half
boolean isPalindromeOptimal(int n) {
 // Negative numbers and numbers ending in 0 (except 0) are not palindromes
 if (n < 0 || (n % 10 == 0 && n != 0)) return false;

 int reversedHalf = 0;
 while (n > reversedHalf) {
 reversedHalf = reversedHalf * 10 + n % 10;
 n /= 10;
 }

 // For even length: n == reversedHalf
 // For odd length: n == reversedHalf/10 (middle digit doesn't matter)
 return n == reversedHalf || n == reversedHalf / 10;
}
```

---

## L4: GCD (Greatest Common Divisor) / HCF
**Question:** Find GCD of two numbers.
**Intuition:** Euclidean Algorithm - GCD(a,b) = GCD(b, a%b). Keep reducing until one becomes 0.
**Logic:** Repeatedly replace larger number with remainder until one is 0.

**Java:**
```java
// Euclidean Algorithm (Iterative)
int gcd(int a, int b) {
 while (b != 0) {
 int temp = b;
 b = a % b;
 a = temp;
 }
 return a;
}

// Recursive
int gcdRecursive(int a, int b) {
 if (b == 0) return a;
 return gcdRecursive(b, a % b);
}

// Time: O(log(min(a,b)))
```

---

## L5: Armstrong Number
**Question:** Check if sum of cubes of digits equals the number.
**Intuition:** For n-digit number, if sum of nth power of each digit equals number, it's Armstrong.
**Logic:** Extract digits, compute power sum, compare with original.

**Java:**
```java
boolean isArmstrong(int n) {
 int original = n;
 int digits = (int)Math.log10(n) + 1;
 int sum = 0;

 while (n > 0) {
 int digit = n % 10;
 sum += Math.pow(digit, digits);
 n /= 10;
 }
 return sum == original;
}

// Example: 153 = 1Â³ + 5Â³ + 3Â³ = 1 + 125 + 27 = 153
```

---

## L6: Print All Divisors
**Question:** Print all divisors of a number N.
**Intuition:** Check from 1 to N. Optimization: check only till ˆšN because divisors come in pairs.
**Logic:** If i divides N, both i and N/i are divisors.

**Java:**
```java
// Brute force: O(N)
void printDivisorsBrute(int n) {
 for (int i = 1; i <= n; i++) {
 if (n % i == 0) {
 System.out.print(i + " ");
 }
 }
}

// Optimal: O(ˆšN)
ArrayList<Integer> findDivisors(int n) {
 ArrayList<Integer> divisors = new ArrayList<>();

 for (int i = 1; i <= Math.sqrt(n); i++) {
 if (n % i == 0) {
 divisors.add(i);
 if (i != n / i) { // Avoid duplicate for perfect squares
 divisors.add(n / i);
 }
 }
 }
 Collections.sort(divisors);
 return divisors;
}
```

---

## L7: Check for Prime Number
**Question:** Check if N is prime (divisible only by 1 and itself).
**Intuition:** Check divisibility from 2 to ˆšN. If any number divides, not prime.
**Logic:** Prime has exactly 2 divisors. Check till ˆšN for efficiency.

**Java:**
```java
boolean isPrime(int n) {
 if (n <= 1) return false;
 if (n <= 3) return true;
 if (n % 2 == 0 || n % 3 == 0) return false;

 // Check for divisors of form 6kÂ±1 up to ˆšn
 for (int i = 5; i * i <= n; i += 6) {
 if (n % i == 0 || n % (i + 2) == 0) {
 return false;
 }
 }
 return true;
}
// Time: O(ˆšn)
```

---

## L8: Prime Factorization
**Question:** Find all prime factors of N.
**Intuition:** Divide by smallest primes repeatedly. Start from 2, move to odd numbers.
**Logic:** Divide by 2 till possible, then check odd numbers till ˆšN.

**Java:**
```java
ArrayList<Integer> primeFactors(int n) {
 ArrayList<Integer> factors = new ArrayList<>();

 // Handle 2 separately
 while (n % 2 == 0) {
 factors.add(2);
 n /= 2;
 }

 // Check odd numbers from 3 onwards
 for (int i = 3; i * i <= n; i += 2) {
 while (n % i == 0) {
 factors.add(i);
 n /= i;
 }
 }

 // If n is still > 1, it's a prime factor
 if (n > 1) {
 factors.add(n);
 }

 return factors;
}
// Time: O(ˆšn)
```

---

## L9: Sieve of Eratosthenes (All Primes up to N)
**Question:** Find all prime numbers up to N.
**Intuition:** Mark multiples of each prime as composite. Start from 2, mark 4,6,8... then 3 marks 6,9,12... Unmarked numbers are primes.
**Logic:** Create boolean array, mark composites, collect primes.

**Java:**
```java
ArrayList<Integer> sieveOfEratosthenes(int n) {
 boolean[] isPrime = new boolean[n + 1];
 Arrays.fill(isPrime, true);
 isPrime[0] = isPrime[1] = false;

 for (int i = 2; i * i <= n; i++) {
 if (isPrime[i]) {
 // Mark all multiples of i as composite
 for (int j = i * i; j <= n; j += i) {
 isPrime[j] = false;
 }
 }
 }

 // Collect all primes
 ArrayList<Integer> primes = new ArrayList<>();
 for (int i = 2; i <= n; i++) {
 if (isPrime[i]) {
 primes.add(i);
 }
 }
 return primes;
}
// Time: O(n log log n), Space: O(n)
```

---

## L10: Power(x, n) - Fast Exponentiation
**Question:** Calculate x^n efficiently.
**Intuition:** Binary exponentiation - if n is even: x^n = (x^(n/2))^2, if odd: x^n = x * x^(n-1).
**Logic:** Reduce problem size by half each time using bit manipulation.

**Java:**
```java
// Recursive
double power(double x, int n) {
 if (n == 0) return 1;
 if (n < 0) {
 x = 1 / x;
 n = -n;
 }

 if (n % 2 == 0) {
 double half = power(x, n / 2);
 return half * half;
 } else {
 return x * power(x, n - 1);
 }
}

// Iterative (Binary Exponentiation)
double powerIterative(double x, int n) {
 long N = n;
 if (N < 0) {
 x = 1 / x;
 N = -N;
 }

 double result = 1;
 double current = x;

 while (N > 0) {
 if (N % 2 == 1) {
 result *= current;
 }
 current *= current;
 N /= 2;
 }
 return result;
}
// Time: O(log n)
```

---

## L11: Count Primes (Sieve variant)
**Question:** Count number of primes less than N.
**Intuition:** Use Sieve of Eratosthenes, count unmarked numbers.
**Logic:** Same as sieve, just count instead of storing.

**Java:**
```java
int countPrimes(int n) {
 if (n <= 2) return 0;

 boolean[] isPrime = new boolean[n];
 Arrays.fill(isPrime, true);
 isPrime[0] = isPrime[1] = false;

 for (int i = 2; i * i < n; i++) {
 if (isPrime[i]) {
 for (int j = i * i; j < n; j += i) {
 isPrime[j] = false;
 }
 }
 }

 int count = 0;
 for (boolean prime : isPrime) {
 if (prime) count++;
 }
 return count;
}
```

---

## L12: LCM (Least Common Multiple)
**Question:** Find LCM of two numbers.
**Intuition:** LCM * GCD = a * b. So LCM = (a * b) / GCD(a, b).
**Logic:** Calculate GCD first, then use formula.

**Java:**
```java
int gcd(int a, int b) {
 return b == 0 ? a : gcd(b, a % b);
}

int lcm(int a, int b) {
 return (a * b) / gcd(a, b);
}

// To avoid overflow
long lcmSafe(int a, int b) {
 return ((long)a * b) / gcd(a, b);
}
```

---

## L13: Modular Arithmetic Basics
**Question:** Understand (a + b) % m, (a * b) % m, (a^n) % m.
**Intuition:** Taking mod at each step prevents overflow and gives same result.
**Logic:** Properties: (a+b)%m = ((a%m)+(b%m))%m, (a*b)%m = ((a%m)*(b%m))%m.

**Java:**
```java
// Addition under modulo
long addMod(long a, long b, long mod) {
 return ((a % mod) + (b % mod)) % mod;
}

// Multiplication under modulo
long mulMod(long a, long b, long mod) {
 return ((a % mod) * (b % mod)) % mod;
}

// Power under modulo
long powerMod(long x, long n, long mod) {
 long result = 1;
 x %= mod;

 while (n > 0) {
 if (n % 2 == 1) {
 result = (result * x) % mod;
 }
 x = (x * x) % mod;
 n /= 2;
 }
 return result;
}
```

---

## L14: Modular Multiplicative Inverse
**Question:** Find x such that (a * x) % m = 1.
**Intuition:** When m is prime, use Fermat's Little Theorem: a^(m-1) ‰¡ 1 (mod m), so a^(-1) = a^(m-2) mod m.
**Logic:** For prime m: inverse(a) = power(a, m-2, m).

**Java:**
```java
// Works only when m is prime
long modInverse(long a, long m) {
 return powerMod(a, m - 2, m);
}

long powerMod(long x, long n, long mod) {
 long result = 1;
 x %= mod;
 while (n > 0) {
 if (n % 2 == 1) {
 result = (result * x) % mod;
 }
 x = (x * x) % mod;
 n /= 2;
 }
 return result;
}

// Example: Find (2^-1) mod 5
// inverse(2, 5) = 2^(5-2) mod 5 = 2^3 mod 5 = 8 mod 5 = 3
// Verify: (2 * 3) mod 5 = 6 mod 5 = 1 œ“
```

---

## L15: Factorial Modulo M
**Question:** Calculate n! % m efficiently.
**Intuition:** Precompute factorials with modulo at each step to prevent overflow.
**Logic:** fact[i] = (fact[i-1] * i) % m.

**Java:**
```java
long[] precomputeFactorials(int n, long mod) {
 long[] fact = new long[n + 1];
 fact[0] = 1;

 for (int i = 1; i <= n; i++) {
 fact[i] = (fact[i - 1] * i) % mod;
 }
 return fact;
}

// For nCr calculations with modulo
long nCrMod(int n, int r, long mod) {
 if (r > n) return 0;

 long[] fact = precomputeFactorials(n, mod);

 // nCr = n! / (r! * (n-r)!)
 // Under modulo: n! * inverse(r!) * inverse((n-r)!)
 long numerator = fact[n];
 long denominator = (fact[r] * fact[n - r]) % mod;
 long inverse = modInverse(denominator, mod);

 return (numerator * inverse) % mod;
}
```

---

## L16: Perfect Square Check
**Question:** Check if N is a perfect square.
**Intuition:** If ˆšN is integer, then N is perfect square.
**Logic:** Take square root, check if squaring it gives back N.

**Java:**
```java
boolean isPerfectSquare(int n) {
 if (n < 0) return false;

 int sqrt = (int)Math.sqrt(n);
 return sqrt * sqrt == n;
}

// Without using Math.sqrt (Binary Search)
boolean isPerfectSquareBinarySearch(int n) {
 if (n < 0) return false;
 if (n == 0 || n == 1) return true;

 long left = 1, right = n;

 while (left <= right) {
 long mid = left + (right - left) / 2;
 long square = mid * mid;

 if (square == n) return true;
 else if (square < n) left = mid + 1;
 else right = mid - 1;
 }
 return false;
}
```

---

## L17: Count Trailing Zeros in Factorial
**Question:** Count number of trailing zeros in n!.
**Intuition:** Trailing zeros = number of times 10 divides n!. Since 10 = 2×5 and 2s are abundant, count 5s.
**Logic:** Count multiples of 5, 25, 125... (5^1, 5^2, 5^3...).

**Java:**
```java
int trailingZeros(int n) {
 int count = 0;

 // Count factors of 5
 for (int i = 5; n / i > 0; i *= 5) {
 count += n / i;
 // Check for overflow before next iteration
 if (i > n / 5) break;
 }

 return count;
}

// Alternative (safer for large n)
int trailingZerosSafe(int n) {
 int count = 0;

 while (n >= 5) {
 n /= 5;
 count += n;
 }

 return count;
}

// Example: 25! has 25/5 + 25/25 = 5 + 1 = 6 trailing zeros
```

---

## L18: Nth Fibonacci Number
**Question:** Find nth Fibonacci number.
**Intuition:** F(n) = F(n-1) + F(n-2). Use iterative approach or matrix exponentiation for optimal.
**Logic:** Iterative O(n), Matrix exponentiation O(log n).

**Java:**
```java
// Iterative: O(n)
int fibonacci(int n) {
 if (n <= 1) return n;

 int a = 0, b = 1;
 for (int i = 2; i <= n; i++) {
 int temp = a + b;
 a = b;
 b = temp;
 }
 return b;
}

// With modulo for large numbers
long fibonacciMod(int n, long mod) {
 if (n <= 1) return n;

 long a = 0, b = 1;
 for (int i = 2; i <= n; i++) {
 long temp = (a + b) % mod;
 a = b;
 b = temp;
 }
 return b;
}

// Matrix Exponentiation: O(log n)
long fibMatrix(int n) {
 if (n <= 1) return n;

 long[][] base = {{1, 1}, {1, 0}};
 long[][] result = matrixPower(base, n - 1);
 return result[0][0];
}

long[][] matrixPower(long[][] matrix, int n) {
 long[][] result = {{1, 0}, {0, 1}}; // Identity matrix

 while (n > 0) {
 if (n % 2 == 1) {
 result = matrixMultiply(result, matrix);
 }
 matrix = matrixMultiply(matrix, matrix);
 n /= 2;
 }
 return result;
}

long[][] matrixMultiply(long[][] a, long[][] b) {
 long[][] c = new long[2][2];
 c[0][0] = a[0][0] * b[0][0] + a[0][1] * b[1][0];
 c[0][1] = a[0][0] * b[0][1] + a[0][1] * b[1][1];
 c[1][0] = a[1][0] * b[0][0] + a[1][1] * b[1][0];
 c[1][1] = a[1][0] * b[0][1] + a[1][1] * b[1][1];
 return c;
}
```

---

## L19: Sum of Divisors
**Question:** Find sum of all divisors from 1 to N.
**Intuition:** For each number i, it contributes to sum for all its multiples. Count how many times each number appears.
**Logic:** i appears in 1*i, 2*i, 3*i... up to (N/i)*i. So i contributes i * (N/i) to total sum.

**Java:**
```java
// Brute force: O(N²)
long sumOfDivisorsBrute(int n) {
 long sum = 0;
 for (int i = 1; i <= n; i++) {
 for (int j = 1; j <= i; j++) {
 if (i % j == 0) {
 sum += j;
 }
 }
 }
 return sum;
}

// Optimal: O(N)
long sumOfDivisors(int n) {
 long sum = 0;

 for (int i = 1; i <= n; i++) {
 // i is a divisor of all multiples: i, 2i, 3i, ..., (n/i)*i
 sum += (long)i * (n / i);
 }

 return sum;
}
// Example: For n=4, sum = 1+1+2+1+2+3+4 = 14
// Or: 1*(4/1) + 2*(4/2) + 3*(4/3) + 4*(4/4) = 4+4+3+4 = 15
```

---

## L20: Smallest Prime Factor (SPF)
**Question:** Find smallest prime factor for all numbers up to N.
**Intuition:** Modified Sieve. Instead of marking composite, store the smallest prime that divides it.
**Logic:** For each prime, mark all its multiples with that prime (if not already marked).

**Java:**
```java
int[] smallestPrimeFactor(int n) {
 int[] spf = new int[n + 1];

 // Initialize: each number's SPF is itself
 for (int i = 1; i <= n; i++) {
 spf[i] = i;
 }

 for (int i = 2; i * i <= n; i++) {
 if (spf[i] == i) { // i is prime
 for (int j = i * i; j <= n; j += i) {
 if (spf[j] == j) { // not yet marked
 spf[j] = i;
 }
 }
 }
 }
 return spf;
}

// Use SPF for fast prime factorization
ArrayList<Integer> primeFactorsUsingSPF(int n, int[] spf) {
 ArrayList<Integer> factors = new ArrayList<>();

 while (n > 1) {
 factors.add(spf[n]);
 n /= spf[n];
 }
 return factors;
}
// Time: Precompute O(n log log n), Query O(log n)
```

---

## L21: Count Primes in Range [L, R]
**Question:** Count primes between L and R.
**Intuition:** Use segmented sieve. Mark composites in range [L, R] using primes up to ˆšR.
**Logic:** Find all primes up to ˆšR, use them to mark composites in [L, R].

**Java:**
```java
int countPrimesInRange(int L, int R) {
 if (R < 2) return 0;

 // Find all primes up to ˆšR
 int sqrtR = (int)Math.sqrt(R);
 ArrayList<Integer> primes = sieveOfEratosthenes(sqrtR);

 // Segmented sieve for [L, R]
 boolean[] isPrime = new boolean[R - L + 1];
 Arrays.fill(isPrime, true);

 if (L == 1) isPrime[0] = false; // 1 is not prime

 for (int prime : primes) {
 // Find first multiple of prime >= L
 int start = Math.max(prime * prime, (L + prime - 1) / prime * prime);

 for (int j = start; j <= R; j += prime) {
 isPrime[j - L] = false;
 }
 }

 int count = 0;
 for (boolean prime : isPrime) {
 if (prime) count++;
 }
 return count;
}
```

---

## L22: Euler's Totient Function Ï†(n)
**Question:** Count numbers from 1 to n that are coprime with n (GCD = 1).
**Intuition:** Ï†(n) = n * (1 - 1/p1) * (1 - 1/p2) * ... for all prime factors p.
**Logic:** Find prime factors, multiply by (1 - 1/prime).

**Java:**
```java
int eulerTotient(int n) {
 int result = n;

 // Handle factor 2
 if (n % 2 == 0) {
 result -= result / 2;
 while (n % 2 == 0) {
 n /= 2;
 }
 }

 // Handle odd factors
 for (int i = 3; i * i <= n; i += 2) {
 if (n % i == 0) {
 result -= result / i;
 while (n % i == 0) {
 n /= i;
 }
 }
 }

 // If n is still > 1, it's a prime factor
 if (n > 1) {
 result -= result / n;
 }

 return result;
}
// Example: Ï†(12) = 12 * (1-1/2) * (1-1/3) = 12 * 1/2 * 2/3 = 4
// Numbers coprime with 12: 1, 5, 7, 11 → count = 4
```

---

## L23: nCr (Binomial Coefficient)
**Question:** Calculate n choose r = n! / (r! * (n-r)!).
**Intuition:** Use Pascal's triangle property: C(n,r) = C(n-1,r-1) + C(n-1,r).
**Logic:** DP approach or direct calculation with care for overflow.

**Java:**
```java
// Using Pascal's triangle (DP)
int nCr(int n, int r) {
 if (r > n) return 0;
 if (r == 0 || r == n) return 1;
 if (r > n - r) r = n - r; // Optimization

 int[] dp = new int[r + 1];
 dp[0] = 1;

 for (int i = 1; i <= n; i++) {
 for (int j = Math.min(i, r); j > 0; j--) {
 dp[j] = dp[j] + dp[j - 1];
 }
 }
 return dp[r];
}

// Direct calculation (careful with overflow)
long nCrDirect(int n, int r) {
 if (r > n) return 0;
 if (r == 0 || r == n) return 1;
 if (r > n - r) r = n - r;

 long result = 1;
 for (int i = 0; i < r; i++) {
 result *= (n - i);
 result /= (i + 1);
 }
 return result;
}
```

---

## L24: Catalan Numbers
**Question:** Find nth Catalan number. Applications: valid parentheses, BST count, etc.
**Intuition:** C(n) = (2n)! / ((n+1)! * n!) OR C(n) = sum of C(i)*C(n-1-i) for i=0 to n-1.
**Logic:** Use DP or binomial coefficient formula.

**Java:**
```java
// Using DP
long catalanDP(int n) {
 long[] dp = new long[n + 1];
 dp[0] = dp[1] = 1;

 for (int i = 2; i <= n; i++) {
 for (int j = 0; j < i; j++) {
 dp[i] += dp[j] * dp[i - 1 - j];
 }
 }
 return dp[n];
}

// Using binomial coefficient
long catalan(int n) {
 // C(n) = C(2n, n) / (n + 1)
 long c = nCrDirect(2 * n, n);
 return c / (n + 1);
}
// First few: 1, 1, 2, 5, 14, 42, 132...
```

---