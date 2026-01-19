
## Table of Contents
- **Part 1: Trie Fundamentals (L1-L2)** - Implementation & Basic Operations
- **Part 2: Trie Problems (L3-L5)** - String Problems  
- **Part 3: Bit Manipulation & Trie (L6-L7)** - XOR Problems

---

# Part 1: Trie Fundamentals

## L1: Implement Trie (Prefix Tree)
**Question:** Implement a trie with insert, search, and startsWith operations.  
**Intuition:** Trie (pronounced "try") is a **tree-like data structure** that stores strings efficiently! Key insight: **Common prefixes are shared**. Each node represents a character, and paths from root represent words. Essential for: autocomplete, spell check, IP routing, phone directories. Unlike HashMap, Trie enables **prefix searches** efficiently!  
**Logic:** Each node has: (1) Array of 26 children (for 'a'-'z'), (2) Boolean flag `isEnd` to mark word completion. Insert: traverse creating nodes as needed, mark last as `isEnd=true`. Search: traverse checking existence, return `isEnd` at last node. StartsWith: same as search but ignore `isEnd`.  
**Java:**
```java
class TrieNode {
    TrieNode[] children;
    boolean isEnd;

    TrieNode() {
        children = new TrieNode[26];
        isEnd = false;
    }
}

class Trie {
    TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Insert a word into trie
    public void insert(String word) {
        TrieNode node = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'a';

            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }

            node = node.children[index];
        }

        node.isEnd = true; // Mark word end
    }

    // Search if word exists in trie
    public boolean search(String word) {
        TrieNode node = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'a';

            if (node.children[index] == null) {
                return false;
            }

            node = node.children[index];
        }

        return node.isEnd; // Word must be complete
    }

    // Check if any word starts with given prefix
    public boolean startsWith(String prefix) {
        TrieNode node = root;

        for (char ch : prefix.toCharArray()) {
            int index = ch - 'a';

            if (node.children[index] == null) {
                return false;
            }

            node = node.children[index];
        }

        return true; // Prefix exists, don't check isEnd
    }
}

// Time Complexity: O(L) where L = length of word/prefix
// Space Complexity: O(ALPHABET_SIZE * N * L) worst case
// where N = number of words, L = average length
```

**Key Points:**
- Insert: O(L), Search: O(L), StartsWith: O(L) where L = word length
- Space efficient for large datasets with common prefixes
- Each node can have up to 26 children (for lowercase English)
- `isEnd` flag distinguishes complete words from prefixes

---

## L2: Implement Trie II (with Count Operations)
**Question:** Implement Trie with additional operations: `countWordsEqualTo(word)` and `countWordsStartingWith(prefix)`.  
**Intuition:** Previous Trie only checked existence. Now we need to **count occurrences**! Solution: store two counters at each node: (1) `endsWith` = how many words end at this node, (2) `countPrefix` = how many words pass through this node. This enables counting duplicates and prefix occurrences!  
**Logic:** Each node has: children array, `endsWith` counter, `countPrefix` counter. Insert: increment `countPrefix` for each node traversed, increment `endsWith` at final node. Erase: decrement counters. Count operations: traverse and return appropriate counter.  
**Java:**
```java
class TrieNode {
    TrieNode[] children;
    int endsWith;     // Count of words ending at this node
    int countPrefix;  // Count of words passing through this node

    TrieNode() {
        children = new TrieNode[26];
        endsWith = 0;
        countPrefix = 0;
    }
}

class Trie {
    TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Insert word (can have duplicates)
    public void insert(String word) {
        TrieNode node = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'a';

            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }

            node = node.children[index];
            node.countPrefix++; // Increment prefix count
        }

        node.endsWith++; // Increment word end count
    }

    // Count how many times word was inserted
    public int countWordsEqualTo(String word) {
        TrieNode node = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'a';

            if (node.children[index] == null) {
                return 0;
            }

            node = node.children[index];
        }

        return node.endsWith;
    }

    // Count how many words start with prefix
    public int countWordsStartingWith(String prefix) {
        TrieNode node = root;

        for (char ch : prefix.toCharArray()) {
            int index = ch - 'a';

            if (node.children[index] == null) {
                return 0;
            }

            node = node.children[index];
        }

        return node.countPrefix;
    }

    // Erase one occurrence of word
    public void erase(String word) {
        TrieNode node = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'a';

            if (node.children[index] == null) {
                return; // Word doesn't exist
            }

            node = node.children[index];
            node.countPrefix--; // Decrement prefix count
        }

        node.endsWith--; // Decrement word end count
    }
}

// Time: All operations O(L) where L = word length
// Space: O(ALPHABET_SIZE * N * L)
```

**Key Differences from Trie I:**
- **boolean â†’ int counters** for handling duplicates
- `endsWith` replaces `isEnd` flag
- `countPrefix` enables efficient prefix counting
- Erase operation decrements counters

---

# Part 2: Trie Problems

## L3: Longest Word with All Prefixes (Complete String)
**Question:** Given array of strings, find longest "complete string" - a string where **every prefix** also exists in array. If multiple, return lexicographically smallest.  
**Intuition:** Brilliant Trie application! Insert all words into Trie with `isEnd` marking. For each word, check if **all prefixes are marked** as complete words (have `isEnd=true`). This means every substring from start is a valid word! Track longest valid string, handle lexicographical ordering.  
**Logic:** (1) Build Trie with all words, (2) For each word, traverse Trie checking if `isEnd=true` at every step (meaning all prefixes exist), (3) Track longest (or lex-smallest if tie).  
**Java:**
```java
class TrieNode {
    TrieNode[] children;
    boolean isEnd;

    TrieNode() {
        children = new TrieNode[26];
        isEnd = false;
    }
}

class Solution {
    TrieNode root;

    public String longestCompleteString(String[] words) {
        root = new TrieNode();

        // Insert all words into Trie
        for (String word : words) {
            insert(word);
        }

        String longest = "";

        // Check each word if it's complete
        for (String word : words) {
            if (isCompleteString(word)) {
                // Update if longer, or lex-smaller if same length
                if (word.length() > longest.length() || 
                    (word.length() == longest.length() && word.compareTo(longest) < 0)) {
                    longest = word;
                }
            }
        }

        return longest.isEmpty() ? "None" : longest;
    }

    void insert(String word) {
        TrieNode node = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'a';
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }

        node.isEnd = true;
    }

    // Check if ALL prefixes of word exist as complete words
    boolean isCompleteString(String word) {
        TrieNode node = root;

        for (char ch : word.toCharArray()) {
            int index = ch - 'a';
            node = node.children[index];

            // Every prefix must be a complete word!
            if (!node.isEnd) {
                return false;
            }
        }

        return true;
    }
}

// Time: O(N*L) for building + O(N*L) for checking = O(N*L)
// Space: O(N*L) for Trie
// where N = number of words, L = average length
```

**Example:**
```
Input: ["n", "ninja", "nin", "ni"]
- "n" âœ“ (itself is complete)
- "ni" âœ“ (n exists, ni exists)
- "nin" âœ“ (n exists, ni exists, nin exists)
- "ninja" âœ“ (n exists, ni exists, nin exists, ninj exists, ninja exists)
Output: "ninja" (longest complete string)
```

---

## L4: Number of Distinct Substrings
**Question:** Given string, count number of distinct substrings (including empty string).  
**Intuition:** Naive approach: generate all substrings O(nÂ²), store in set O(nÂ²), answer = set size. **Trie optimization**: Insert all suffixes! Why? When inserting suffix starting at position i, we're essentially adding all substrings starting at i. Each new node created = new distinct substring! Count nodes created during insertion.  
**Logic:** For each starting position i in string, insert suffix starting at i into Trie. Count every NEW node created (means new distinct substring found). Add 1 for empty string at end.  
**Java:**
```java
class TrieNode {
    TrieNode[] children;

    TrieNode() {
        children = new TrieNode[26];
    }
}

class Solution {
    int count = 0;
    TrieNode root;

    public int countDistinctSubstrings(String s) {
        root = new TrieNode();
        int n = s.length();

        // Insert all suffixes
        for (int i = 0; i < n; i++) {
            insertSuffix(s, i);
        }

        return count + 1; // +1 for empty string
    }

    void insertSuffix(String s, int start) {
        TrieNode node = root;

        for (int i = start; i < s.length(); i++) {
            int index = s.charAt(i) - 'a';

            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
                count++; // New node = new distinct substring!
            }

            node = node.children[index];
        }
    }
}

// Time: O(nÂ²) - inserting n suffixes, each of length O(n)
// Space: O(nÂ²) for Trie in worst case
// Better than Set approach: O(nÂ²) time, O(nÂ²) space
```

**Example:**
```
Input: "abc"
Suffixes: "abc", "bc", "c"
- Insert "abc": adds nodes for 'a', 'b', 'c' (count = 3)
- Insert "bc": 'b' exists, adds 'c' from 'b' (count = 4)  
- Insert "c": 'c' exists (count = 4)
- Result: 4 + 1 (empty) = 5
Substrings: "", "a", "ab", "abc", "b", "bc", "c"
Wait, that's 7! Let me recalculate...
Actually: "a", "ab", "abc", "b", "bc", "c" = 6 + empty = 7
```

**Key Insight:** Each NEW TrieNode created represents a NEW distinct substring!

---

## L5: Maximum XOR of Two Numbers in Array
**Question:** Given array of integers, find maximum XOR of any two numbers.  
**Intuition:** Naive O(nÂ²) checks all pairs. **Trie on bits** gives O(n*32)! Key insight: to maximize XOR, for each bit, we want **opposite bit** (0 XOR 1 = 1, same bits = 0). Store all numbers in Trie using their **binary representation**. For each number, traverse Trie trying to go opposite bit at each level - this maximizes XOR!  
**Logic:** (1) Build Trie with 32-bit binary representations of all numbers, (2) For each number, traverse Trie: at each bit, try to go opposite direction (if current bit is 0, try to go to 1 node, vice versa), (3) Calculate XOR and track maximum.  
**Java:**
```java
class TrieNode {
    TrieNode[] children;

    TrieNode() {
        children = new TrieNode[2]; // Only 0 and 1
    }
}

class Solution {
    TrieNode root;

    public int findMaximumXOR(int[] nums) {
        root = new TrieNode();

        // Insert all numbers into Trie (as 32-bit binary)
        for (int num : nums) {
            insert(num);
        }

        int maxXor = 0;

        // For each number, find max XOR possible
        for (int num : nums) {
            maxXor = Math.max(maxXor, getMaxXor(num));
        }

        return maxXor;
    }

    void insert(int num) {
        TrieNode node = root;

        // Process from MSB (bit 31) to LSB (bit 0)
        for (int i = 31; i >= 0; i--) {
            int bit = (num >> i) & 1; // Extract i-th bit

            if (node.children[bit] == null) {
                node.children[bit] = new TrieNode();
            }

            node = node.children[bit];
        }
    }

    int getMaxXor(int num) {
        TrieNode node = root;
        int maxXor = 0;

        for (int i = 31; i >= 0; i--) {
            int bit = (num >> i) & 1;
            int oppositeBit = 1 - bit; // Try to go opposite!

            // If opposite path exists, take it for max XOR
            if (node.children[oppositeBit] != null) {
                maxXor |= (1 << i); // Set i-th bit in result
                node = node.children[oppositeBit];
            } else {
                // Forced to take same bit path
                node = node.children[bit];
            }
        }

        return maxXor;
    }
}

// Time: O(n * 32) = O(n) where n = array length
// Space: O(n * 32) = O(n) for Trie
// MUCH better than naive O(nÂ²) approach!
```

**Example:**
```
Input: [3, 10, 5, 25, 2, 8]
Binary representations:
3:  00011
10: 01010
5:  00101
25: 11001

For 3 (00011), find max XOR:
- Start from MSB, try to flip each bit
- Want opposite: 11100 
- Best match in Trie gives: 3 XOR 25 = 26 (11010)

Maximum XOR in array: 28 (5 XOR 25)
5:  00101
25: 11001
XOR: 11100 = 28
```

**Why Trie Works:**
- Greedy choice at each bit level
- Always try opposite bit for maximum XOR
- Trie structure enables efficient opposite-bit lookup
- Alternative: start from MSB, try to maximize at each level

---

## L6: Maximum XOR with an Element from Array
**Question:** Given array and queries [xi, mi], for each query find max XOR of xi with any element â‰¤ mi. If no element â‰¤ mi exists, return -1.  
**Intuition:** Extension of L5 with constraint! Can't use all numbers - only those â‰¤ mi. Solution: (1) Sort array and queries by limit, (2) For each query, insert eligible numbers (â‰¤ mi) into Trie, then find max XOR. This ensures Trie only contains valid numbers for current query!  
**Logic:** Sort queries by mi (keeping original index). Process queries in order, inserting numbers into Trie as they become eligible. For each query, Trie contains all nums[i] â‰¤ mi.  
**Java:**
```java
class TrieNode {
    TrieNode[] children;

    TrieNode() {
        children = new TrieNode[2];
    }
}

class Solution {
    TrieNode root;

    public int[] maximizeXor(int[] nums, int[][] queries) {
        int n = queries.length;
        int[] result = new int[n];

        // Sort array
        Arrays.sort(nums);

        // Create query array with original indices
        int[][] queriesWithIndex = new int[n][3];
        for (int i = 0; i < n; i++) {
            queriesWithIndex[i][0] = queries[i][0]; // xi
            queriesWithIndex[i][1] = queries[i][1]; // mi
            queriesWithIndex[i][2] = i;             // original index
        }

        // Sort queries by mi
        Arrays.sort(queriesWithIndex, (a, b) -> a[1] - b[1]);

        root = new TrieNode();
        int idx = 0;

        for (int[] query : queriesWithIndex) {
            int xi = query[0];
            int mi = query[1];
            int originalIdx = query[2];

            // Insert all nums <= mi into Trie
            while (idx < nums.length && nums[idx] <= mi) {
                insert(nums[idx]);
                idx++;
            }

            // If Trie empty (no valid numbers), result = -1
            if (idx == 0) {
                result[originalIdx] = -1;
            } else {
                result[originalIdx] = getMaxXor(xi);
            }
        }

        return result;
    }

    void insert(int num) {
        TrieNode node = root;
        for (int i = 31; i >= 0; i--) {
            int bit = (num >> i) & 1;
            if (node.children[bit] == null) {
                node.children[bit] = new TrieNode();
            }
            node = node.children[bit];
        }
    }

    int getMaxXor(int num) {
        TrieNode node = root;
        int maxXor = 0;

        for (int i = 31; i >= 0; i--) {
            int bit = (num >> i) & 1;
            int oppositeBit = 1 - bit;

            if (node.children[oppositeBit] != null) {
                maxXor |= (1 << i);
                node = node.children[oppositeBit];
            } else {
                node = node.children[bit];
            }
        }

        return maxXor;
    }
}

// Time: O(n log n + q log q + 32(n + q)) = O((n+q) log(n+q))
// Space: O(n * 32) = O(n)
// where n = array size, q = number of queries
```

**Why Sort?**
- Sorting array: ensures we process numbers in increasing order
- Sorting queries by mi: allows us to incrementally build Trie
- Once number inserted for query with mi, it's available for all later queries (with higher mi)
- Avoids rebuilding Trie for each query!

---

## L7: Maximum XOR of Two Numbers in Array (Alternative - Hash Set)
**Question:** Same as L5 but using Hash Set instead of Trie (different approach).  
**Intuition:** Greedy bit-by-bit from MSB to LSB. For each bit position, assume we can set it to 1 in answer. Build prefix of all numbers up to current bit. Check if two prefixes exist whose XOR gives desired max prefix. Use property: if a XOR b = c, then a XOR c = b. So check if (maxPrefix XOR number) exists in set!  
**Logic:** For each bit from MSB to LSB: (1) Try to set current bit to 1, (2) Build all prefixes up to current bit, (3) Check if (currentMax XOR prefix) exists in prefix set, (4) If yes, keep bit as 1; else set to 0.  
**Java:**
```java
class Solution {
    public int findMaximumXOR(int[] nums) {
        int max = 0;
        int mask = 0;

        // Try to build answer bit by bit from MSB
        for (int i = 31; i >= 0; i--) {
            mask = mask | (1 << i); // Include current bit in mask

            Set<Integer> prefixes = new HashSet<>();

            // Get all prefixes of numbers up to current bit
            for (int num : nums) {
                prefixes.add(num & mask);
            }

            // Try to set current bit to 1 in answer
            int candidate = max | (1 << i);

            // Check if we can achieve this candidate
            for (int prefix : prefixes) {
                // If a XOR b = c, then a XOR c = b
                // We want: prefix1 XOR prefix2 = candidate
                // So check if (prefix1 XOR candidate) exists
                if (prefixes.contains(prefix ^ candidate)) {
                    max = candidate;
                    break;
                }
            }
        }

        return max;
    }
}

// Time: O(32 * nÂ²) = O(nÂ²) worst case, but often faster
// Space: O(n) for hash set
// Comparison: Trie is cleaner and O(n) time guaranteed
```

**Trie vs Hash Set Approach:**

| Aspect | Trie | Hash Set |
|--------|------|----------|
| Time | O(32n) guaranteed | O(32nÂ²) worst, often faster |
| Space | O(32n) | O(n) |
| Complexity | More code | Less code |
| Intuition | Clearer | More clever |
| Interview | Preferred | Alternative |

Both work, but **Trie is recommended** for interviews!

---

# Advanced Trie Concepts

## Bit Manipulation Tricks for Trie Problems

### Extract i-th bit:
```java
int bit = (num >> i) & 1;
```

### Set i-th bit:
```java
result |= (1 << i);
```

### Clear i-th bit:
```java
result &= ~(1 << i);
```

### Toggle i-th bit:
```java
result ^= (1 << i);
```

### Get prefix up to i-th bit:
```java
int prefix = num & mask; // where mask = all 1s up to i-th bit
```

---

## Trie Node Variations

### Standard (lowercase letters):
```java
TrieNode[] children = new TrieNode[26];
```

### Binary Trie (for numbers):
```java
TrieNode[] children = new TrieNode[2]; // 0 and 1
```

### Full ASCII:
```java
TrieNode[] children = new TrieNode[128];
```

### Map-based (flexible):
```java
Map<Character, TrieNode> children = new HashMap<>();
```

---

## Common Trie Patterns

### Pattern 1: String Operations
- Insert, Search, StartsWith
- Auto-complete
- Spell checker
- **Use:** Standard Trie with `isEnd` flag

### Pattern 2: Counting
- Count words equal to
- Count words with prefix
- **Use:** Trie with counters (`endsWith`, `countPrefix`)

### Pattern 3: Prefix Validation
- All prefixes exist
- Longest complete string
- **Use:** Check `isEnd` at every step

### Pattern 4: XOR Maximization
- Max XOR of two numbers
- Max XOR with constraints
- **Use:** Binary Trie (bit-by-bit)

---

## Time Complexity Summary

| Operation | Time | Space |
|-----------|------|-------|
| Insert | O(L) | O(L) per word |
| Search | O(L) | - |
| StartsWith | O(L) | - |
| Build Trie | O(N*L) | O(N*L) |
| XOR Operations | O(N*32) | O(N*32) |

where L = word/number length, N = number of words/numbers

---

## Space Optimization Tips

### 1. Lazy Node Creation:
```java
if (node.children[index] == null) {
    node.children[index] = new TrieNode(); // Only create when needed
}
```

### 2. Use Map for Sparse Children:
```java
Map<Character, TrieNode> children = new HashMap<>();
// Better when alphabet size is large but usage is sparse
```

### 3. Compress Chains (Radix Tree):
```java
// Instead of: a -> p -> p -> l -> e
// Store: "apple" in single node
```

---

## Interview Tips

**Most Important Trie Problems:**
1. **Implement Trie I & II** - Foundation
2. **Longest Complete String** - Prefix checking pattern
3. **Maximum XOR** - Binary Trie pattern â­â­â­
4. **Distinct Substrings** - Suffix insertion pattern

**Common Mistakes:**
- Forgetting `isEnd` flag vs checking existence
- Array index calculation: `ch - 'a'` (only for lowercase)
- Bit manipulation: process from MSB to LSB (31 to 0)
- XOR properties: `a XOR a = 0`, `a XOR 0 = a`

**When to Use Trie:**
- âœ… Need to search by prefix
- âœ… Multiple string searches (build once, search many)
- âœ… Auto-complete, spell checker
- âœ… XOR maximization problems
- âŒ Simple single search (use HashMap instead)
- âŒ Need range queries (use Segment Tree)

**Trie vs Other Data Structures:**

| Requirement | Trie | HashMap | Binary Search |
|-------------|------|---------|---------------|
| Exact match | O(L) | O(1) avg | O(log N) |
| Prefix match | O(L) | O(N*L) | O(N*L) |
| Space | O(N*L*26) | O(N*L) | O(N*L) |
| Order matters | Yes | No | Yes |

**Pro Tips:**
- For XOR problems, always think Binary Trie!
- Count operations? Add counters to nodes!
- Prefix operations? Trie is your friend!
- Practice bit manipulation separately

---

## Bonus: Advanced Trie Applications

### 1. Auto-Complete System
```java
List<String> autoComplete(String prefix) {
    // Search for prefix, then DFS to collect all words
    TrieNode node = searchPrefix(prefix);
    if (node == null) return new ArrayList<>();

    List<String> results = new ArrayList<>();
    dfs(node, prefix, results);
    return results;
}
```

### 2. Word Search II (Trie + Backtracking)
```java
// Build Trie of dictionary
// DFS on board, prune using Trie
// Much faster than checking each word separately
```

### 3. Palindrome Pairs (Trie + Reverse)
```java
// Insert all words reversed
// For each word, search for complement that forms palindrome
```

### 4. IP Routing (Longest Prefix Match)
```java
// Store IP prefixes in Trie
// Find longest matching prefix for given IP
```

---

# Summary: Complete Trie Mastery

## Core Concepts
âœ… Trie structure with children array and flags  
âœ… Insert, Search, StartsWith operations  
âœ… Count operations with counters  
âœ… Binary Trie for XOR problems  
âœ… Bit manipulation fundamentals  

## Problem Patterns
âœ… Prefix validation (L3)  
âœ… Distinct substrings (L4)  
âœ… XOR maximization (L5, L6, L7)  
âœ… String operations (L1, L2)  

## Interview Readiness
âœ… Implement Trie from scratch  
âœ… Solve XOR problems with Binary Trie  
âœ… Handle edge cases (empty, duplicates)  
âœ… Optimize space when needed  