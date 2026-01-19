
## L1: Introduction to Bit Manipulation | Basics
**Question:** Understand binary representation, 1's and 2's complement, and basic bitwise operators.  
**Intuition:** Computers store everything in binary (0s and 1s). Bit manipulation is direct interaction with this representation - extremely fast operations.  
**Logic:** Master basic operators: AND (&), OR (|), XOR (^), NOT (~), Left Shift (<<), Right Shift (>>).

**Operators Overview:**
```java
// AND (&) - Both bits must be 1
5 & 3  // 0101 & 0011 = 0001 = 1

// OR (|) - At least one bit must be 1
5 | 3  // 0101 | 0011 = 0111 = 7

// XOR (^) - Bits must be different
5 ^ 3  // 0101 ^ 0011 = 0110 = 6

// NOT (~) - Flip all bits
~5     // ~0101 = 1010 (in 32-bit: large negative number)

// Left Shift (<<) - Multiply by 2^k
5 << 1 // 0101 << 1 = 1010 = 10 (5 * 2)
5 << 2 // 0101 << 2 = 10100 = 20 (5 * 4)

// Right Shift (>>) - Divide by 2^k
5 >> 1 // 0101 >> 1 = 0010 = 2 (5 / 2)
5 >> 2 // 0101 >> 2 = 0001 = 1 (5 / 4)

// Properties:
// x & 0 = 0, x & 1 = x, x & x = x
// x | 0 = x, x | 1 = 1, x | x = x
// x ^ 0 = x, x ^ 1 = ~x, x ^ x = 0
// x << k = x * 2^k
// x >> k = x / 2^k
```

**1's and 2's Complement:**
```java
// 1's Complement: Flip all bits
int onesComplement(int n) {
    return ~n;
}

// 2's Complement: 1's complement + 1 (used for negative numbers)
int twosComplement(int n) {
    return ~n + 1;
}

// Convert decimal to binary
String decimalToBinary(int n) {
    return Integer.toBinaryString(n);
}

// Convert binary to decimal
int binaryToDecimal(String binary) {
    return Integer.parseInt(binary, 2);
}
```

---

## L2: Check if ith Bit is Set
**Question:** Check if the ith bit (0-indexed from right) is set (1) or not.  
**Intuition:** Two approaches - bring the bit to rightmost position and check, OR bring 1 to that position and check.  
**Logic:** Right shift by i and check LSB, OR left shift 1 by i and use AND.

**Java:**
```java
// Approach 1: Right Shift
boolean isSetRightShift(int n, int i) {
    return ((n >> i) & 1) == 1;
}

// Approach 2: Left Shift (Preferred)
boolean isSetLeftShift(int n, int i) {
    return (n & (1 << i)) != 0;
}

// Example: n = 13 (1101), i = 2
// 13 & (1 << 2) = 1101 & 0100 = 0100 = 4 (non-zero, so set)
// Time: O(1), Space: O(1)
```

---

## L3: Set the ith Bit
**Question:** Set the ith bit to 1 (keep other bits unchanged).  
**Intuition:** Create a mask with only ith bit as 1, then OR with number. OR keeps all bits same except makes the ith bit 1.  
**Logic:** n | (1 << i)

**Java:**
```java
int setBit(int n, int i) {
    return n | (1 << i);
}

// Example: n = 9 (1001), i = 2
// 9 | (1 << 2) = 1001 | 0100 = 1101 = 13
// Time: O(1), Space: O(1)
```

---

## L4: Clear the ith Bit
**Question:** Clear the ith bit (set to 0), keep other bits unchanged.  
**Intuition:** Create mask with ith bit as 0 and all others as 1, then AND with number. AND keeps all bits same except makes the ith bit 0.  
**Logic:** n & ~(1 << i)

**Java:**
```java
int clearBit(int n, int i) {
    return n & ~(1 << i);
}

// Example: n = 13 (1101), i = 2
// ~(1 << 2) = ~0100 = ...11111011
// 13 & ~(1 << 2) = 1101 & ...11111011 = 1001 = 9
// Time: O(1), Space: O(1)
```

---

## L5: Toggle the ith Bit
**Question:** Toggle ith bit (0â†’1, 1â†’0).  
**Intuition:** XOR with 1 flips the bit. XOR with 0 keeps it same.  
**Logic:** n ^ (1 << i)

**Java:**
```java
int toggleBit(int n, int i) {
    return n ^ (1 << i);
}

// Example: n = 13 (1101), i = 1
// 13 ^ (1 << 1) = 1101 ^ 0010 = 1111 = 15
// Time: O(1), Space: O(1)
```

---

## L6: Remove Last Set Bit (Rightmost Set Bit)
**Question:** Remove/clear the rightmost set bit.  
**Intuition:** n-1 flips all bits after the rightmost set bit (including it). ANDing with n clears that bit.  
**Logic:** n & (n - 1) - this is a very important trick!

**Java:**
```java
int removeLastSetBit(int n) {
    return n & (n - 1);
}

// Example: n = 12 (1100)
// n - 1 = 11 (1011)
// 12 & 11 = 1100 & 1011 = 1000 = 8

// Example: n = 13 (1101)
// n - 1 = 12 (1100)
// 13 & 12 = 1101 & 1100 = 1100 = 12

// Use case: Count set bits
int countSetBits(int n) {
    int count = 0;
    while (n > 0) {
        n = n & (n - 1);
        count++;
    }
    return count;
}
// Time: O(number of set bits), Space: O(1)
```

---

## L7: Check if Number is Power of 2
**Question:** Check if n is a power of 2.  
**Intuition:** Power of 2 has exactly one set bit. Using n & (n-1) clears that bit, result should be 0.  
**Logic:** n > 0 && (n & (n - 1)) == 0

**Java:**
```java
boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
}

// Example: n = 16 (10000)
// n - 1 = 15 (01111)
// 16 & 15 = 10000 & 01111 = 0 â†’ true

// Example: n = 18 (10010)
// n - 1 = 17 (10001)
// 18 & 17 = 10010 & 10001 = 10000 â‰  0 â†’ false
// Time: O(1), Space: O(1)
```

---

## L8: Count Set Bits (Hamming Weight)
**Question:** Count number of 1s in binary representation.  
**Intuition:** Three approaches - loop and check each bit, use n & (n-1) trick, or use Brian Kernighan's algorithm.  
**Logic:** Keep removing rightmost set bit until n becomes 0.

**Java:**
```java
// Approach 1: Check each bit
int countSetBits1(int n) {
    int count = 0;
    while (n > 0) {
        count += (n & 1);
        n >>= 1;
    }
    return count;
}

// Approach 2: Brian Kernighan's Algorithm (Optimal)
int countSetBits2(int n) {
    int count = 0;
    while (n > 0) {
        n = n & (n - 1); // Remove rightmost set bit
        count++;
    }
    return count;
}

// Approach 3: Using Integer.bitCount() (Java built-in)
int countSetBits3(int n) {
    return Integer.bitCount(n);
}

// Time: O(number of set bits), Space: O(1)
```

---

## L9: Swap Two Numbers
**Question:** Swap two numbers without using third variable.  
**Intuition:** XOR has special property: a ^ a = 0, a ^ 0 = a. Use this for swapping.  
**Logic:** a = a ^ b, b = a ^ b, a = a ^ b

**Java:**
```java
void swap(int a, int b) {
    System.out.println("Before: a = " + a + ", b = " + b);

    a = a ^ b; // a now has a^b
    b = a ^ b; // b = (a^b)^b = a
    a = a ^ b; // a = (a^b)^a = b

    System.out.println("After: a = " + a + ", b = " + b);
}

// Example: a = 5 (101), b = 7 (111)
// a = 5 ^ 7 = 101 ^ 111 = 010 = 2
// b = 2 ^ 7 = 010 ^ 111 = 101 = 5
// a = 2 ^ 5 = 010 ^ 101 = 111 = 7
// Result: a = 7, b = 5
// Time: O(1), Space: O(1)
```

---

## L10: Single Number I
**Question:** Array where every element appears twice except one. Find that single element.  
**Intuition:** XOR of two same numbers is 0. XOR of any number with 0 is the number itself. XOR all elements.  
**Logic:** a ^ a = 0, a ^ 0 = a. XOR is commutative and associative.

**Java:**
```java
int singleNumber(int[] nums) {
    int xor = 0;
    for (int num : nums) {
        xor ^= num;
    }
    return xor;
}

// Example: [4, 1, 2, 1, 2]
// 4 ^ 1 ^ 2 ^ 1 ^ 2 = 4 ^ (1 ^ 1) ^ (2 ^ 2) = 4 ^ 0 ^ 0 = 4
// Time: O(n), Space: O(1)
```

---

## L11: Single Number II
**Question:** Array where every element appears thrice except one. Find that single element.  
**Intuition:** Count set bits at each position. If count % 3 != 0, that bit is set in the single number.  
**Logic:** For each of 32 bit positions, count how many numbers have that bit set. Take modulo 3.

**Java:**
```java
int singleNumber(int[] nums) {
    int result = 0;

    for (int i = 0; i < 32; i++) {
        int count = 0;

        // Count numbers with ith bit set
        for (int num : nums) {
            if ((num & (1 << i)) != 0) {
                count++;
            }
        }

        // If count is not multiple of 3, set this bit in result
        if (count % 3 != 0) {
            result |= (1 << i);
        }
    }

    return result;
}

// Time: O(32 * n) = O(n), Space: O(1)
```

---

## L12: Single Number III
**Question:** Array where every element appears twice except two distinct elements. Find those two.  
**Intuition:** XOR all gives xor of two unique numbers. Find any set bit in this xor (differentiating bit). Partition array based on this bit.  
**Logic:** Use xor & -xor to get rightmost set bit. Partition and XOR each group separately.

**Java:**
```java
int[] singleNumber(int[] nums) {
    // Step 1: XOR all numbers (gives num1 ^ num2)
    int xor = 0;
    for (int num : nums) {
        xor ^= num;
    }

    // Step 2: Find rightmost set bit (differentiating bit)
    int rightmostSetBit = xor & -xor;

    // Step 3: Partition numbers based on this bit and XOR each group
    int num1 = 0, num2 = 0;
    for (int num : nums) {
        if ((num & rightmostSetBit) != 0) {
            num1 ^= num;
        } else {
            num2 ^= num;
        }
    }

    return new int[]{num1, num2};
}

// Example: [1, 2, 1, 3, 2, 5]
// XOR all: 1^2^1^3^2^5 = 3^5 = 011^101 = 110 = 6
// Rightmost set bit: 6 & -6 = 110 & ...11111010 = 010 = 2
// Group 1 (bit set): 2, 3, 2 â†’ XOR = 3
// Group 2 (bit not set): 1, 1, 5 â†’ XOR = 5
// Time: O(n), Space: O(1)
```

---

## L13: XOR of Numbers in Range [L, R]
**Question:** Find XOR of all numbers from L to R.  
**Intuition:** XOR from 0 to n has a pattern. Use: XOR(L, R) = XOR(0, R) ^ XOR(0, L-1).  
**Logic:** Pattern repeats every 4 numbers: n%4 = 0â†’n, 1â†’1, 2â†’n+1, 3â†’0.

**Java:**
```java
int xorFromZeroToN(int n) {
    if (n % 4 == 0) return n;
    if (n % 4 == 1) return 1;
    if (n % 4 == 2) return n + 1;
    return 0; // n % 4 == 3
}

int xorInRange(int L, int R) {
    return xorFromZeroToN(R) ^ xorFromZeroToN(L - 1);
}

// Example: XOR(3, 7)
// XOR(0, 7) = 0 (pattern: 7%4 = 3)
// XOR(0, 2) = 3 (pattern: 2%4 = 2 â†’ 2+1 = 3)
// XOR(3, 7) = 0 ^ 3 = 3
// Time: O(1), Space: O(1)
```

---

## L14: Power Set (All Subsets)
**Question:** Generate all subsets of an array using bit manipulation.  
**Intuition:** n elements â†’ 2^n subsets. Each subset corresponds to a binary number from 0 to 2^n - 1. Bit position represents element inclusion.  
**Logic:** Iterate from 0 to 2^n - 1. For each number, check which bits are set to determine subset.

**Java:**
```java
List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    int n = nums.length;
    int totalSubsets = 1 << n; // 2^n

    for (int i = 0; i < totalSubsets; i++) {
        List<Integer> subset = new ArrayList<>();

        for (int j = 0; j < n; j++) {
            // Check if jth bit is set in i
            if ((i & (1 << j)) != 0) {
                subset.add(nums[j]);
            }
        }

        result.add(subset);
    }

    return result;
}

// Example: [1, 2, 3]
// i=0 (000): []
// i=1 (001): [1]
// i=2 (010): [2]
// i=3 (011): [1, 2]
// i=4 (100): [3]
// i=5 (101): [1, 3]
// i=6 (110): [2, 3]
// i=7 (111): [1, 2, 3]
// Time: O(n * 2^n), Space: O(1) excluding output
```

---

## L15: Find Missing Number
**Question:** Array of n numbers from 0 to n with one missing. Find it using XOR.  
**Intuition:** XOR all numbers from 0 to n, then XOR with all array elements. Duplicates cancel out, missing remains.  
**Logic:** missing = (0^1^2^...^n) ^ (arr[0]^arr[1]^...^arr[n-1])

**Java:**
```java
int missingNumber(int[] nums) {
    int n = nums.length;
    int xor = 0;

    // XOR all numbers from 0 to n
    for (int i = 0; i <= n; i++) {
        xor ^= i;
    }

    // XOR with all array elements
    for (int num : nums) {
        xor ^= num;
    }

    return xor;
}

// Example: [3, 0, 1] (n = 3)
// XOR: 0^1^2^3^3^0^1 = 2
// Time: O(n), Space: O(1)
```

---

## L16: Two Numbers with Odd Occurrences
**Question:** Find two numbers that occur odd times, all others even times.  
**Intuition:** Similar to Single Number III. XOR all to get xor of two numbers. Partition by differentiating bit.  
**Logic:** Same as Single Number III algorithm.

**Java:**
```java
// Same implementation as Single Number III
int[] twoOddOccurrences(int[] nums) {
    int xor = 0;
    for (int num : nums) xor ^= num;

    int rightmostSetBit = xor & -xor;

    int num1 = 0, num2 = 0;
    for (int num : nums) {
        if ((num & rightmostSetBit) != 0) {
            num1 ^= num;
        } else {
            num2 ^= num;
        }
    }

    return new int[]{num1, num2};
}
```

---

## L17: Divide Two Integers (Without Division)
**Question:** Divide two integers without using *, /, or % operators.  
**Intuition:** Use bit shifting. Subtract divisor shifted left as much as possible.  
**Logic:** Find how many times we can subtract (divisor << k) from dividend.

**Java:**
```java
int divide(int dividend, int divisor) {
    if (dividend == Integer.MIN_VALUE && divisor == -1) {
        return Integer.MAX_VALUE;
    }

    boolean negative = (dividend < 0) ^ (divisor < 0);

    long absDividend = Math.abs((long)dividend);
    long absDivisor = Math.abs((long)divisor);

    int result = 0;

    while (absDividend >= absDivisor) {
        long temp = absDivisor;
        int count = 1;

        while (absDividend >= (temp << 1)) {
            temp <<= 1;
            count <<= 1;
        }

        absDividend -= temp;
        result += count;
    }

    return negative ? -result : result;
}
// Time: O(log(dividend)Â²), Space: O(1)
```

---

## L18: Count Total Set Bits from 1 to N
**Question:** Count total set bits in binary representation of all numbers from 1 to N.  
**Intuition:** Pattern observation. At each bit position, bits toggle in groups of 2^(i+1).  
**Logic:** For each bit position, count complete groups + remaining numbers.

**Java:**
```java
int countTotalSetBits(int n) {
    int count = 0;
    int powerOfTwo = 1;

    while (powerOfTwo <= n) {
        // Complete groups
        int totalPairs = (n + 1) / (powerOfTwo * 2);
        count += totalPairs * powerOfTwo;

        // Remaining numbers
        int remainder = (n + 1) % (powerOfTwo * 2);
        count += Math.max(0, remainder - powerOfTwo);

        powerOfTwo <<= 1;
    }

    return count;
}
// Time: O(log n), Space: O(1)
```

---

## L19: Find XOR from L to R
**Question:** Efficiently find XOR of range [L, R].  
**Intuition:** Use prefix XOR trick: XOR(L, R) = XOR(0, R) ^ XOR(0, L-1).  
**Logic:** Leverage the pattern that XOR from 0 to n follows 4-number cycle.

**Java:**
```java
// Same as L13 - XOR of Numbers in Range
int xorRange(int L, int R) {
    return xorUpTo(R) ^ xorUpTo(L - 1);
}

int xorUpTo(int n) {
    switch (n % 4) {
        case 0: return n;
        case 1: return 1;
        case 2: return n + 1;
        default: return 0;
    }
}
```

---

## L20: Minimum Bit Flips to Convert A to B
**Question:** Find minimum bit flips needed to convert number A to B.  
**Intuition:** Bits that differ need to be flipped. XOR gives positions where bits differ.  
**Logic:** Count set bits in A ^ B.

**Java:**
```java
int minBitFlips(int start, int goal) {
    int xor = start ^ goal;

    // Count set bits in XOR
    int count = 0;
    while (xor > 0) {
        xor &= (xor - 1);
        count++;
    }

    return count;
}

// Or simply:
int minBitFlipsSimple(int start, int goal) {
    return Integer.bitCount(start ^ goal);
}
// Time: O(number of different bits), Space: O(1)
```

---

## Important Bit Manipulation Tricks & Patterns

### 1. Get Rightmost Set Bit
```java
int rightmostSetBit = n & -n;
// Example: n = 12 (1100)
// -n = -12 (in 2's complement: ...11110100)
// 12 & -12 = 1100 & ...11110100 = 0100 = 4
```

### 2. Remove Rightmost Set Bit
```java
n = n & (n - 1);
// Very useful for counting set bits
```

### 3. Check if Power of 2
```java
boolean isPowerOfTwo = (n > 0) && ((n & (n - 1)) == 0);
```

### 4. Check if Power of 4
```java
boolean isPowerOfFour = (n > 0) && ((n & (n - 1)) == 0) && ((n & 0xAAAAAAAA) == 0);
```

### 5. Set All Bits After Rightmost Set Bit
```java
int result = n | (n - 1);
```

### 6. Isolate Rightmost 0 Bit
```java
int rightmostZero = ~n & (n + 1);
```

### 7. Get All 1s
```java
int allOnes = ~0;
```

### 8. Turn Off Rightmost Contiguous 1s
```java
n & (n + 1);
```

### 9. Check if Opposite Signs
```java
boolean oppositeSigns = (x ^ y) < 0;
```

### 10. Fast Multiplication/Division by Power of 2
```java
int multiply = n << k;  // n * 2^k
int divide = n >> k;    // n / 2^k
```

---

## Common Patterns Summary

### Pattern 1: Single Element Detection
- Use XOR: a ^ a = 0, a ^ 0 = a
- Single Number I, II, III variations

### Pattern 2: Subset Generation
- Use bits to represent inclusion: 0 to 2^n - 1
- Power set, all combinations

### Pattern 3: Bit Counting
- Brian Kernighan's: n & (n-1) removes rightmost set bit
- Count set bits, check power of 2

### Pattern 4: Range XOR
- Use prefix XOR with pattern recognition
- XOR(L, R) = XOR(0, R) ^ XOR(0, L-1)

### Pattern 5: Bit Position Operations
- Set: n | (1 << i)
- Clear: n & ~(1 << i)
- Toggle: n ^ (1 << i)
- Check: (n & (1 << i)) != 0

---