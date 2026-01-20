
## Table of Contents

**Part 1: Trie Fundamentals**
- [L1: Implement Trie (Prefix Tree)](#l1-implement-trie-prefix-tree)
- [L2: Implement Trie II (with Count Operations)](#l2-implement-trie-ii-with-count-operations)

**Part 2: Trie Problems**
- [L3: Longest Word with All Prefixes (Complete String)](#l3-longest-word-with-all-prefixes-complete-string)
- [L4: Number of Distinct Substrings](#l4-number-of-distinct-substrings)
- [L5: Maximum XOR of Two Numbers in Array](#l5-maximum-xor-of-two-numbers-in-array)
- [L6: Maximum XOR with an Element from Array](#l6-maximum-xor-with-an-element-from-array)
- [L7: Maximum XOR (Alternative - Hash Set)](#l7-maximum-xor-of-two-numbers-in-array-alternative---hash-set)

**Part 3: Advanced Trie Applications**
- [L8: Auto-Complete System](#l8-auto-complete-system)
- [L9: Word Search II (Trie + Backtracking)](#l9-word-search-ii-trie--backtracking)
- [L10: Palindrome Pairs](#l10-palindrome-pairs)
- [L11: Design Search Autocomplete System](#l11-design-search-autocomplete-system)

**Part 4: Reference Material**
- [L12: Bit Manipulation Tricks](#l12-bit-manipulation-tricks-for-trie-problems)
- [Trie Node Variations](#trie-node-variations)
- [Common Trie Patterns](#common-trie-patterns)
- [Time Complexity Summary](#time-complexity-summary)
- [Interview Tips](#interview-tips)

**Part 5: Interview Preparation**
- [Comprehensive Time & Space Complexity Guide](#comprehensive-time--space-complexity-guide-for-interviews)

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

**Time & Space Complexity Discussion:**

**Time Complexity: O(L) for all operations**
- **Insert**: O(L) - Visit each character once, create nodes as needed
- **Search**: O(L) - Traverse L nodes to verify word exists and check `isEnd`
- **StartsWith**: O(L) - Same traversal but ignore `isEnd` flag
- L = length of word/prefix

**Why O(L) and not O(1)?**
- Unlike HashMap's O(1) average lookup, Trie must traverse character by character
- Each character access is O(1), but we do it L times → O(L)
- Trade-off: slightly slower than HashMap for exact match, but enables prefix operations!

**Space Complexity: O(ALPHABET_SIZE × N × L) worst case**
- ALPHABET_SIZE = 26 for lowercase English (each node has array[26])
- N = number of words inserted
- L = average word length
- **Worst case**: No shared prefixes ("a", "b", "c") → O(26 × N × L)
- **Best case**: High prefix sharing ("cat", "cats", "cattle") → O(total unique characters)
- **Real-world**: Natural language has ~40-60% prefix overlap → Much better than worst case!

**Interview Insights:**
- "Each node has 26 pointers, but we use lazy initialization - only create when needed"
- "Space grows with unique paths, not total words - that's why Trie excels for dictionaries"
- "For sparse character sets, HashMap<Character, TrieNode> reduces space from O(26) to O(actual children) per node"

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
 int endsWith; // Count of words ending at this node
 int countPrefix; // Count of words passing through this node

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

**Time & Space Complexity Discussion:**

**Time Complexity: O(L) for all operations** (same as Trie I!)
- **Insert**: O(L) - Traverse + increment counters at each node
- **CountWordsEqualTo**: O(L) - Traverse and return `endsWith` counter
- **CountWordsStartingWith**: O(L) - Traverse and return `countPrefix` counter
- **Erase**: O(L) - Traverse and decrement counters
- Counter operations (++/--) are O(1), so overall still O(L)

**Space Complexity: O(ALPHABET_SIZE × N × L) + O(N) for counters**
- Same Trie structure as before: O(26 × N × L)
- Additional space: 2 integers per node (`endsWith`, `countPrefix`)
- In Java: 2 × 4 bytes = 8 bytes per node
- Negligible compared to 26 pointers (26 × 8 = 208 bytes on 64-bit JVM)
- **Effective space**: Same as Trie I with minor constant overhead

**Why Counters Don't Change Complexity:**
- Incrementing/decrementing integer: O(1)
- No additional data structures needed
- Just storing two more fields per node (constant space per node)

**Interview Insights:**
- "Trie II handles duplicates elegantly without changing time complexity"
- "Two counters serve different purposes: `endsWith` for exact matches, `countPrefix` for prefix queries"
- "This is superior to storing count only at leaf nodes - we get O(L) prefix counting instead of O(L + M) where M = subtree size"

**Key Differences from Trie I:**
- **boolean → int counters** for handling duplicates
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
- "n" œ“ (itself is complete)
- "ni" œ“ (n exists, ni exists)
- "nin" œ“ (n exists, ni exists, nin exists)
- "ninja" œ“ (n exists, ni exists, nin exists, ninj exists, ninja exists)
Output: "ninja" (longest complete string)
```

---

## L4: Number of Distinct Substrings
**Question:** Given string, count number of distinct substrings (including empty string).
**Intuition:** Naive approach: generate all substrings O(n²), store in set O(n²), answer = set size. **Trie optimization**: Insert all suffixes! Why? When inserting suffix starting at position i, we're essentially adding all substrings starting at i. Each new node created = new distinct substring! Count nodes created during insertion.
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

// Time: O(n²) - inserting n suffixes, each of length O(n)
// Space: O(n²) for Trie in worst case
// Better than Set approach: O(n²) time, O(n²) space
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

**Time & Space Complexity Discussion:**

**Time Complexity: O(N²)**
- Insert N suffixes: suffix[0] length N, suffix[1] length N-1, ..., suffix[N-1] length 1
- Total characters inserted: N + (N-1) + (N-2) + ... + 1 = N(N+1)/2 = O(N²)
- Each character insertion is O(1), so total is O(N²)

**Space Complexity: O(N²) worst case, O(N) best case**
- **Worst case**: All substrings unique (e.g., "abcdefg") → O(N²) nodes
- **Best case**: Highly repetitive string (e.g., "aaaaaaa") → O(N) nodes
- Each new node represents a unique substring

**Why Suffix Insertion Works:**
- Suffix starting at position i contains all substrings starting at position i
- Example: suffix "abc" gives substrings "a", "ab", "abc"
- By inserting all suffixes, we cover ALL possible substrings!
- Each NEW node created = NEW distinct substring discovered

**Comparison with HashSet Approach:**

| Approach    | Time  | Space | Implementation                                   |
| ----------- | ----- | ----- | ------------------------------------------------ |
| **HashSet** | O(N²) | O(N²) | Generate all substrings, add to set, return size |
| **Trie**    | O(N²) | O(N²) | Insert suffixes, count new nodes created         |

**Interview Insights:**
- "Both approaches have same complexity, but Trie is more elegant"
- "Trie counts during insertion - no need to store all substrings explicitly"
- "For highly repetitive strings, Trie saves significant space through sharing"
- "Alternative: Suffix Array with LCP (Longest Common Prefix) gives O(N log N) but harder to implement"

**Key Insight:** Each NEW TrieNode created represents a NEW distinct substring!

---

## L5: Maximum XOR of Two Numbers in Array
**Question:** Given array of integers, find maximum XOR of any two numbers.
**Intuition:** Naive O(n²) checks all pairs. **Trie on bits** gives O(n*32)! Key insight: to maximize XOR, for each bit, we want **opposite bit** (0 XOR 1 = 1, same bits = 0). Store all numbers in Trie using their **binary representation**. For each number, traverse Trie trying to go opposite bit at each level - this maximizes XOR!
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
// MUCH better than naive O(n²) approach!
```

**Time & Space Complexity Discussion:**

**Time Complexity: O(N × 32) = O(N)**
- **Build Trie**: O(N × 32) - Insert N numbers, each requires 32 bit operations
- **Query each number**: O(N × 32) - Find max XOR for each number
- **Total**: O(N × 32) + O(N × 32) = O(N × 32) = O(N)
- The factor of 32 is constant (integer bit width), so O(N) linear time!

**Space Complexity: O(N × 32) = O(N)**
- Binary Trie with 2 children per node (0 and 1 branches)
- Maximum depth: 32 levels (one per bit)
- **Worst case**: All numbers have different bit patterns → O(N × 32) nodes
- **Best case**: Numbers share bit prefixes → fewer nodes through sharing
- Still O(N) space, constant factor of 32

**Why Binary Trie is Optimal for XOR:**
1. **Greedy bit-by-bit**: Try to flip each bit from MSB to LSB
2. **XOR property**: 0 XOR 1 = 1 (different bits give 1), 0 XOR 0 = 0 (same bits give 0)
3. **Maximize XOR**: Always try opposite bit → greedy choice gives global optimum!
4. **Trie enables O(1) lookup** per bit level for opposite bit

**Comparison with Brute Force:**

| Approach        | Time  | Explanation                              |
| --------------- | ----- | ---------------------------------------- |
| **Brute Force** | O(N²) | Check all pairs: (N choose 2) = N(N-1)/2 |
| **Binary Trie** | O(N)  | Linear pass with O(32) per number        |

**Speedup**: For N=100,000: Brute force ~10 billion ops vs Trie ~3.2 million ops = **3000x faster!**

**Interview Insights:**
- "We process from MSB (bit 31) to LSB (bit 0) because higher bits contribute more to XOR value"
- "At each level, we greedily choose opposite bit to maximize that bit position"
- "This greedy approach works because XOR doesn't have carry-over like addition"
- "Binary Trie is the standard solution for XOR problems - recognize the pattern!"

**Example:**
```
Input: [3, 10, 5, 25, 2, 8]
Binary representations:
3: 00011
10: 01010
5: 00101
25: 11001

For 3 (00011), find max XOR:
- Start from MSB, try to flip each bit
- Want opposite: 11100
- Best match in Trie gives: 3 XOR 25 = 26 (11010)

Maximum XOR in array: 28 (5 XOR 25)
5: 00101
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
**Question:** Given array and queries [xi, mi], for each query find max XOR of xi with any element ‰¤ mi. If no element ‰¤ mi exists, return -1.
**Intuition:** Extension of L5 with constraint! Can't use all numbers - only those ‰¤ mi. Solution: (1) Sort array and queries by limit, (2) For each query, insert eligible numbers (‰¤ mi) into Trie, then find max XOR. This ensures Trie only contains valid numbers for current query!
**Logic:** Sort queries by mi (keeping original index). Process queries in order, inserting numbers into Trie as they become eligible. For each query, Trie contains all nums[i] ‰¤ mi.
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
            queriesWithIndex[i][2] = i; // original index
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

**Time & Space Complexity Discussion:**

**Time Complexity: O(N log N + Q log Q + (N + Q) × 32)**
- **Sort array**: O(N log N)
- **Sort queries by mi**: O(Q log Q)
- **Process queries**: O((N + Q) × 32)
  - Insert numbers incrementally as they become eligible: amortized O(N × 32) total
  - Query XOR for each query: O(Q × 32)
- **Total**: O((N + Q) log(N + Q) + (N + Q) × 32) = **O((N + Q) log(N + Q))**
- Where N = array size, Q = number of queries

**Space Complexity: O(N × 32 + Q) = O(N + Q)**
- Binary Trie: O(N × 32) = O(N)
- Query array with indices: O(Q)
- Result array: O(Q)

**Why Sorting is Crucial:**

**Without sorting (naive approach):**
```
For each query (xi, mi):
    Build new Trie with nums ≤ mi  // O(N × 32)
    Find max XOR                    // O(32)
Total: O(Q × N × 32) = Too slow!
```

**With sorting (optimized):**
```
Sort array: [1, 3, 5, 8, 10]
Sort queries by mi: [(x1,5), (x2,8), (x3,10)]

Process incrementally:
- Query mi=5: Insert [1,3,5], query
- Query mi=8: Insert [8], query (already have 1,3,5)
- Query mi=10: Insert [10], query (already have 1,3,5,8)

Each number inserted once → O(N × 32) total!
```

**Interview Insights:**
- "Sorting enables incremental Trie building - each number inserted exactly once"
- "Once a number is inserted for mi=5, it's available for all queries with mi≥5"
- "This transforms O(Q × N) to O(Q + N) - massive optimization!"
- "Need to track original query indices because sorting reorders them"
- "Similar pattern appears in many problems: sort + sweep/process incrementally"

**Key Optimization**: Incremental Trie building saves O(Q × N) work!

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

// Time: O(32 * n²) = O(n²) worst case, but often faster
// Space: O(n) for hash set
// Comparison: Trie is cleaner and O(n) time guaranteed
```

# Part 3: Advanced Trie Applications

## L8: Auto-Complete System
**Question:** Implement an autocomplete system that returns top 3 most frequent words with given prefix. Support input() method that adds characters and returns suggestions.
**Intuition:** Classic Trie + Frequency tracking! Store each sentence with its frequency (hot degree). For autocomplete, navigate to prefix node, then DFS/BFS collect all words under that subtree. Sort by frequency (descending) and lexicographically if tied. Return top 3.
**Logic:** (1) TrieNode stores: children, isEnd, and list of (sentence, frequency) pairs at end nodes, (2) Input accumulates current prefix, searches Trie, collects completions, sorts and returns top 3, (3) On '#' character, save accumulated sentence with frequency.
**Java:**
```java
class TrieNode {
    TrieNode[] children;
    Map<String, Integer> sentences; // sentence -> frequency

    TrieNode() {
        children = new TrieNode[27]; // a-z + space
        sentences = new HashMap<>();
    }
}

class AutocompleteSystem {
    TrieNode root;
    StringBuilder currentInput;

    public AutocompleteSystem(String[] sentences, int[] times) {
        root = new TrieNode();
        currentInput = new StringBuilder();

        // Build initial Trie with sentences
        for (int i = 0; i < sentences.length; i++) {
            insert(sentences[i], times[i]);
        }
    }

    public List<String> input(char c) {
        if (c == '#') {
            // Save current sentence
            insert(currentInput.toString(), 1);
            currentInput = new StringBuilder();
            return new ArrayList<>();
        }

        currentInput.append(c);
        return search(currentInput.toString());
    }

    void insert(String sentence, int times) {
        TrieNode node = root;

        for (char ch : sentence.toCharArray()) {
            int index = getIndex(ch);
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
            
            // Store sentence at every node (for prefix search)
            node.sentences.put(sentence, 
                node.sentences.getOrDefault(sentence, 0) + times);
        }
    }

    List<String> search(String prefix) {
        TrieNode node = root;

        // Navigate to prefix
        for (char ch : prefix.toCharArray()) {
            int index = getIndex(ch);
            if (node.children[index] == null) {
                return new ArrayList<>();
            }
            node = node.children[index];
        }

        // Get all sentences with this prefix
        List<String> results = new ArrayList<>(node.sentences.keySet());

        // Sort by frequency (descending), then lexicographically
        Collections.sort(results, (a, b) -> {
            int freqA = node.sentences.get(a);
            int freqB = node.sentences.get(b);
            if (freqA != freqB) {
                return freqB - freqA; // Higher frequency first
            }
            return a.compareTo(b); // Lexicographical order
        });

        // Return top 3
        return results.subList(0, Math.min(3, results.size()));
    }

    int getIndex(char ch) {
        return ch == ' ' ? 26 : ch - 'a';
    }
}

// Time: Insert O(L), Input O(L + M log M) where M = matching sentences
// Space: O(N * L * M) where N = sentences, M = avg matches per prefix
```
```
Time Complexity:
- Insert: O(L × M) where M = number of sentences sharing this prefix
  - Store sentence at each node along path
- Search: O(L + M log M)
  - Navigate to prefix: O(L)
  - Sort M matching sentences: O(M log M)
  - Select top 3: O(1)

Space Complexity: O(N × L × M)
- Each node stores map of sentences passing through
- Trade-off: more space for faster queries

Alternative (minimal storage):
- Store only at leaf nodes: O(N × L) space
- But search requires DFS: O(L + total nodes in subtree)

Interview Discussion:
- Discuss trade-off between space and query speed
- Real systems: use caching, precomputation
- Optimize top-K with min-heap if M is large
```
**Key Points:**
- Store sentences at every node along the path (not just at end)
- This enables quick retrieval of all completions for any prefix
- Sorting is done at query time based on frequency + lexicographical order
- Top-K problem: could optimize with heap if K is small

---

## L9: Word Search II (Trie + Backtracking)
**Question:** Given m×n board and list of words, find all words that exist in the board. Words can be formed by sequentially adjacent cells (no reuse).
**Intuition:** Naive: For each word, run DFS on board = O(words * m * n * 4^L). **Trie optimization**: Build Trie of all words once! Then single DFS on board, at each cell check if current path exists in Trie. If path is not a valid prefix, prune immediately! This eliminates checking invalid paths early.
**Logic:** (1) Build Trie with all dictionary words, (2) DFS from each cell, maintain current TrieNode, (3) At each cell, check if current character exists in Trie children, (4) If yes, continue DFS; if no, prune (backtrack), (5) When isEnd=true, found complete word!
**Java:**
```java
class TrieNode {
    TrieNode[] children;
    String word; // Store complete word at end node

    TrieNode() {
        children = new TrieNode[26];
        word = null;
    }
}

class Solution {
    public List<String> findWords(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();
        TrieNode root = buildTrie(words);

        int m = board.length;
        int n = board[0].length;

        // Start DFS from each cell
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dfs(board, i, j, root, result);
            }
        }

        return result;
    }

    TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();

        for (String word : words) {
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                int index = ch - 'a';
                if (node.children[index] == null) {
                    node.children[index] = new TrieNode();
                }
                node = node.children[index];
            }
            node.word = word; // Store word at leaf
        }

        return root;
    }

    void dfs(char[][] board, int i, int j, TrieNode node, List<String> result) {
        int m = board.length, n = board[0].length;

        // Boundary check
        if (i < 0 || i >= m || j < 0 || j >= n) return;

        char ch = board[i][j];

        // Already visited or no path in Trie
        if (ch == '#' || node.children[ch - 'a'] == null) return;

        node = node.children[ch - 'a'];

        // Found a word!
        if (node.word != null) {
            result.add(node.word);
            node.word = null; // Avoid duplicates
        }

        // Mark as visited
        board[i][j] = '#';

        // Explore 4 directions
        dfs(board, i + 1, j, node, result);
        dfs(board, i - 1, j, node, result);
        dfs(board, i, j + 1, node, result);
        dfs(board, i, j - 1, node, result);

        // Backtrack
        board[i][j] = ch;
    }
}

// Time: O(W*L) build Trie + O(m*n*4^L) DFS worst case
// But Trie pruning makes it MUCH faster in practice!
// Space: O(W*L) for Trie + O(L) recursion depth
// where W = number of words, L = max word length
```
```
Time Complexity:
Without Trie: O(W × M × N × 4^L)
- W words
- M×N board
- 4^L DFS for each word (4 directions, L length)

With Trie: O(M × N × 4^L)
- Build Trie: O(W × L)
- Single DFS: O(M × N × 4^L)
- Massive savings: check all W words in single traversal!

Space Complexity: O(W × L)
- Trie storage
- Recursion depth: O(L)

Interview Highlight:
- "Trie enables pruning: if 'ab' not in Trie, skip all 'ab*' paths"
- "Shared prefix optimization: 'cat', 'car', 'card' checked together"
- This is THE classic Trie+Backtracking problem
```

**Why Trie is Critical:**
- **Pruning**: If current path "ab" not in Trie, no need to explore "abc", "abd", etc.
- **Shared prefixes**: Words like "cat", "car", "card" share prefix "ca"
- **Single traversal**: One DFS checks all words simultaneously
- Without Trie: would need separate DFS for each word!

---

## L10: Palindrome Pairs
**Question:** Given list of unique words, find all pairs of indices (i, j) such that concatenation of words[i] + words[j] is a palindrome.
**Intuition:** Naive O(n² * L) checks all pairs. **Trie approach**: For each word, split it into left+right parts. If left is palindrome, check if reverse(right) exists in Trie. If right is palindrome, check if reverse(left) exists. Build Trie with reversed words to enable quick lookup!
**Logic:** (1) Build Trie with all words reversed, storing word index at end, (2) For each word, check all possible splits, (3) If one part forms palindrome, check if reverse of other part exists in Trie.
**Java:**
```java
class TrieNode {
    TrieNode[] children;
    int wordIndex; // Index of word ending here (-1 if none)
    List<Integer> palindromeIndexes; // Words where remaining suffix is palindrome

    TrieNode() {
        children = new TrieNode[26];
        wordIndex = -1;
        palindromeIndexes = new ArrayList<>();
    }
}

class Solution {
    public List<List<Integer>> palindromePairs(String[] words) {
        List<List<Integer>> result = new ArrayList<>();
        TrieNode root = new TrieNode();

        // Build Trie with reversed words
        for (int i = 0; i < words.length; i++) {
            insert(root, words[i], i);
        }

        // For each word, search for palindrome pairs
        for (int i = 0; i < words.length; i++) {
            search(root, words, i, result);
        }

        return result;
    }

    void insert(TrieNode root, String word, int index) {
        TrieNode node = root;

        // Insert word in REVERSE
        for (int i = word.length() - 1; i >= 0; i--) {
            int j = word.charAt(i) - 'a';

            if (node.children[j] == null) {
                node.children[j] = new TrieNode();
            }

            // If remaining prefix is palindrome, store this word's index
            if (isPalindrome(word, 0, i)) {
                node.palindromeIndexes.add(index);
            }

            node = node.children[j];
        }

        node.wordIndex = index;
        node.palindromeIndexes.add(index); // Empty suffix is palindrome
    }

    void search(TrieNode root, String[] words, int index, List<List<Integer>> result) {
        String word = words[index];
        TrieNode node = root;

        // Case 1: Current word matches and remaining is palindrome
        for (int i = 0; i < word.length(); i++) {
            // Found complete word in Trie
            if (node.wordIndex != -1 && node.wordIndex != index) {
                // Check if remaining part of current word is palindrome
                if (isPalindrome(word, i, word.length() - 1)) {
                    result.add(Arrays.asList(index, node.wordIndex));
                }
            }

            int j = word.charAt(i) - 'a';
            if (node.children[j] == null) return;
            node = node.children[j];
        }

        // Case 2: Traversed entire word, check palindrome suffixes in Trie
        for (int idx : node.palindromeIndexes) {
            if (idx != index) {
                result.add(Arrays.asList(index, idx));
            }
        }
    }

    boolean isPalindrome(String word, int left, int right) {
        while (left < right) {
            if (word.charAt(left++) != word.charAt(right--)) {
                return false;
            }
        }
        return true;
    }
}

// Time: O(N * L²) where N = number of words, L = max length
// Space: O(N * L) for Trie
// Much better than naive O(N² * L)
```
```
Time Complexity: O(N × L²)
- Build Trie: O(N × L)
  - For each word: O(L) insertion + O(L) palindrome checks at each position
  - Total: O(N × L²)
- Search: O(N × L²)
  - For each word: O(L) traversal, check palindrome at each position: O(L²)

Space Complexity: O(N × L)
- Trie with reversed words

Without Trie: O(N² × L)
- Check all pairs: O(N²)
- Each palindrome check: O(L)

Interview Note:
- Trie reduces from O(N²) to O(N × L) lookups
- Palindrome checks add L factor: O(N × L²)
- Still better than brute force when L << N
```


**Key Insight:**
- **Reverse storage**: Allows matching suffix of current word with prefix in Trie
- **Palindrome tracking**: Store indices where remaining part is palindrome
- **Two cases**: (1) Match entire word + palindrome suffix, (2) Partial match + stored palindrome

---

## L11: Design Search Autocomplete System
**Question:** Design system that returns top 3 historical hot sentences based on input prefix. Each sentence has hotness rank.
**Intuition:** Enhanced version of L8! Maintain Trie where each node tracks all sentences passing through it with their counts. On each character input, navigate Trie and return top 3 sentences. On '#', save current input and update frequency.
**Logic:** Similar to L8 but optimized: store sentence-frequency map at each node during insertion. This makes search O(L + M log M) instead of DFS.
**Java:**
```java
class TrieNode {
    Map<Character, TrieNode> children;
    Map<String, Integer> sentenceCount; // All sentences through this node

    TrieNode() {
        children = new HashMap<>();
        sentenceCount = new HashMap<>();
    }
}

class AutocompleteSystem {
    TrieNode root;
    StringBuilder current;
    Map<String, Integer> sentenceFreq;

    public AutocompleteSystem(String[] sentences, int[] times) {
        root = new TrieNode();
        current = new StringBuilder();
        sentenceFreq = new HashMap<>();

        // Initialize with historical data
        for (int i = 0; i < sentences.length; i++) {
            sentenceFreq.put(sentences[i], times[i]);
            addSentence(sentences[i]);
        }
    }

    public List<String> input(char c) {
        if (c == '#') {
            String sentence = current.toString();
            sentenceFreq.put(sentence, sentenceFreq.getOrDefault(sentence, 0) + 1);
            addSentence(sentence);
            current = new StringBuilder();
            return new ArrayList<>();
        }

        current.append(c);
        TrieNode node = root;

        // Navigate to current prefix
        for (char ch : current.toString().toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return new ArrayList<>();
            }
            node = node.children.get(ch);
        }

        // Get top 3 sentences
        PriorityQueue<Pair> pq = new PriorityQueue<>((a, b) -> {
            if (a.count != b.count) {
                return b.count - a.count; // Higher count first
            }
            return a.sentence.compareTo(b.sentence); // Lexicographical
        });

        for (Map.Entry<String, Integer> entry : node.sentenceCount.entrySet()) {
            pq.offer(new Pair(entry.getKey(), entry.getValue()));
        }

        List<String> result = new ArrayList<>();
        for (int i = 0; i < 3 && !pq.isEmpty(); i++) {
            result.add(pq.poll().sentence);
        }

        return result;
    }

    void addSentence(String sentence) {
        TrieNode node = root;
        int count = sentenceFreq.get(sentence);

        for (char ch : sentence.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode());
            node = node.children.get(ch);
            node.sentenceCount.put(sentence, count);
        }
    }

    class Pair {
        String sentence;
        int count;

        Pair(String s, int c) {
            sentence = s;
            count = c;
        }
    }
}

// Time: Input O(L + M log M) where M = matching sentences
// Space: O(N * L * M) for storing sentence maps at each node
```

**Optimization Trade-off:**
- Store more data (sentence maps at each node) → faster queries
- Alternative: minimal storage + DFS at query time → slower queries
- Choice depends on: query frequency vs memory constraints

---
## L12. Bit Manipulation Tricks for Trie Problems

* **Extract i-th bit:**
```java
int bit = (num >> i) & 1;
```

* **Set i-th bit:**
```java
result |= (1 << i);
```

* **Clear i-th bit:**
```java
result &= ~(1 << i);
```

* **Toggle i-th bit:**
```java
result ^= (1 << i);
```

* **Get prefix up to i-th bit:**
```java
int prefix = num & mask; // where mask = all 1s up to i-th bit
```

**Trie vs Hash Set Approach:**

| Aspect     | Trie              | Hash Set                    |
| ---------- | ----------------- | --------------------------- |
| Time       | O(32n) guaranteed | O(32n²) worst, often faster |
| Space      | O(32n)            | O(n)                        |
| Complexity | More code         | Less code                   |
| Intuition  | Clearer           | More clever                 |
| Interview  | Preferred         | Alternative                 |

Both work, but **Trie is recommended** for interviews!

---
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

| Operation      | Time    | Space         |
| -------------- | ------- | ------------- |
| Insert         | O(L)    | O(L) per word |
| Search         | O(L)    | -             |
| StartsWith     | O(L)    | -             |
| Build Trie     | O(N*L)  | O(N*L)        |
| XOR Operations | O(N*32) | O(N*32)       |

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
3. **Maximum XOR** - Binary Trie pattern 
4. **Distinct Substrings** - Suffix insertion pattern

**Common Mistakes:**
- Forgetting `isEnd` flag vs checking existence
- Array index calculation: `ch - 'a'` (only for lowercase)
- Bit manipulation: process from MSB to LSB (31 to 0)
- XOR properties: `a XOR a = 0`, `a XOR 0 = a`

**When to Use Trie:**
- œ… Need to search by prefix
- œ… Multiple string searches (build once, search many)
- œ… Auto-complete, spell checker
- œ… XOR maximization problems
- Œ Simple single search (use HashMap instead)
- Œ Need range queries (use Segment Tree)

**Trie vs Other Data Structures:**

| Requirement   | Trie      | HashMap  | Binary Search |
| ------------- | --------- | -------- | ------------- |
| Exact match   | O(L)      | O(1) avg | O(log N)      |
| Prefix match  | O(L)      | O(N*L)   | O(N*L)        |
| Space         | O(N*L*26) | O(N*L)   | O(N*L)        |
| Order matters | Yes       | No       | Yes           |

**Pro Tips:**
- For XOR problems, always think Binary Trie!
- Count operations? Add counters to nodes!
- Prefix operations? Trie is your friend!
- Practice bit manipulation separately

---

# Part 5: Interview Preparation

## Comprehensive Time & Space Complexity Guide for Interviews

### 1. Basic Trie Operations Complexity

| Operation      | Time Complexity | Space Complexity | Explanation                                                           |
| -------------- | --------------- | ---------------- | --------------------------------------------------------------------- |
| **Insert**     | O(L)            | O(L) per word    | L = word length. Visit each character once, create new node if needed |
| **Search**     | O(L)            | O(1)             | L = word length. Traverse existing nodes, no extra space              |
| **StartsWith** | O(L)            | O(1)             | L = prefix length. Same as search but ignore isEnd flag               |
| **Delete**     | O(L)            | O(1)             | L = word length. Traverse and mark/remove nodes                       |

**Detailed Space Analysis for Insert:**
- Best case: O(1) - word shares entire path with existing words
- Average case: O(L) - some characters shared, some new
- Worst case: O(L) - completely new word, all new nodes
- **Total Trie space**: O(ALPHABET_SIZE × N × L) worst case
  - N = number of words
  - L = average word length
  - ALPHABET_SIZE = 26 for lowercase English
- **Optimized space** (with shared prefixes): O(Total characters in all unique paths)

---

### 2. Complete Trie Construction Complexity

**Building Trie from N words:**
- **Time**: O(N × L)
  - N words, each requiring O(L) insertion
- **Space**: O(N × L × ALPHABET_SIZE) worst case
  - If no shared prefixes (e.g., random strings)
- **Space**: O(Total unique characters) best case
  - High prefix sharing (e.g., "cat", "cats", "caterpillar")

**Example Calculation:**
```
Words: ["cat", "cats", "dog", "dogs"]
Worst case space (array-based): 4 words × 4 avg length × 26 children = 416 nodes
Actual space (with sharing): ~10-12 nodes
- c-a-t (3 nodes)
- c-a-t-s (1 additional node)
- d-o-g (3 nodes)  
- d-o-g-s (1 additional node)
- Plus root = 9 nodes
```

---

### Complexity Analysis

#### **Technique 1: Array vs HashMap Children**

**Array-based (TrieNode[] children = new TrieNode[26])**
```
Pros:
- O(1) access time
- Cache-friendly (contiguous memory)
- Simple implementation

Cons:
- O(26) space per node, even if only 1 child used
- Wasteful for sparse data

Best for:
- Dense character distribution
- Known small alphabet (a-z)
- Performance-critical applications
```

**HashMap-based (Map<Character, TrieNode> children)**
```
Pros:
- O(actual children) space
- Flexible for any character set (Unicode, special chars)
- Scales to large alphabets

Cons:
- O(1) average access, but higher constant factor
- Hash overhead per entry
- More memory per entry (reference + hash)

Best for:
- Sparse data (e.g., autocomplete with mixed case, numbers)
- Unicode support needed
- Memory-constrained systems
```

**Space Comparison:**
```
Scenario: 1000 words, average 5 chars, low character diversity

Array-based:
- Nodes created: ~5000 (with sharing)
- Space per node: 26 × 8 bytes (reference) = 208 bytes
- Total: ~5000 × 208 = 1 MB

HashMap-based:
- Nodes created: ~5000
- Average children per node: 2-3
- Space per node: ~3 × (8 + 16) = 72 bytes (ref + HashMap overhead)
- Total: ~5000 × 72 = 360 KB

Savings: ~65% with HashMap for sparse data!
```

#### **Technique 2: Radix Tree (Compressed Trie)**
```
Idea: Compress chains of single-child nodes

Before (Trie):
  c -> a -> t  [3 nodes]

After (Radix):
  "cat"  [1 node]

Space savings: O(N × L) → O(N)
- Number of nodes = number of branch points + leaves
- Much fewer than one node per character

Trade-off:
- More complex insertion/search logic
- Need to handle string splitting on branches

Real-world use:
- Git (commit tree)
- Linux kernel routing tables
- IPv6 address lookup
```

#### **Technique 3: Lazy Node Creation**
```java
// Always use this pattern - only create nodes when needed
if (node.children[index] == null) {
    node.children[index] = new TrieNode();
}

// NEVER pre-allocate:
// for (int i = 0; i < 26; i++) {
//     node.children[i] = new TrieNode(); // Wasteful!
// }
```

---

### 5. Time Complexity Edge Cases & Amortization

#### **Amortized Analysis for Multiple Operations**
```
Question: "What if I insert N words then do M searches?"

Answer:
- Build phase: O(N × L)
- Query phase: O(M × L)
- Total: O((N + M) × L)
- Amortized per operation: O(L)

Key insight: Trie build cost is amortized over many searches
- 1 insert + 1 search: Trie ≈ HashMap
- 1 insert + 1000 searches: Trie >> HashMap (for prefix queries)
```

#### **Worst-Case vs Average-Case**
```
Worst Case (no prefix sharing):
Words: ["a", "b", "c", "d", ...]
Space: O(N × L × 26) = O(26N) for single-char words
Each word creates unique path

Best Case (high sharing):
Words: ["a", "aa", "aaa", "aaaa", ...]
Space: O(L) = O(sum of word lengths)
All words share same path

Average Case (natural language):
English words share ~40-60% of prefixes
Space: O(0.5 × N × L × 26)
```

---

### 6. Comparison with Alternative Data Structures

| Operation             | Trie      | HashMap  | BST            | Suffix Array     |
| --------------------- | --------- | -------- | -------------- | ---------------- |
| **Insert**            | O(L)      | O(L) avg | O(L log N)     | O(N log N) build |
| **Search exact**      | O(L)      | O(1) avg | O(L log N)     | O(L log N)       |
| **Search prefix**     | O(L)      | O(N×L)   | O(L log N + K) | O(L log N)       |
| **Autocomplete**      | O(L + M)  | O(N×L)   | O(L log N + M) | O(L log N + M)   |
| **Space**             | O(N×L×26) | O(N×L)   | O(N×L)         | O(N×L)           |
| **Ordered traversal** | Yes       | No       | Yes            | Yes              |

Where:
- L = word/pattern length
- N = number of words
- M = number of results
- K = size of subtree

**When to Use Each:**

**Use Trie when:**
- ✅ Prefix-based queries (autocomplete, search suggestions)
- ✅ Multiple prefix searches (amortized benefit)
- ✅ Need lexicographic ordering
- ✅ XOR maximization (binary Trie)
- ✅ IP routing, phone directories

**Use HashMap when:**
- ✅ Only exact match lookups
- ✅ Memory constrained
- ✅ Few prefix queries
- ✅ Need O(1) average lookup

**Use BST when:**
- ✅ Need range queries
- ✅ Dynamic ordering requirements
- ✅ Less memory than Trie, faster prefix than HashMap

**Use Suffix Array/Tree when:**
- ✅ Pattern matching in single text
- ✅ Find all occurrences of substring
- ✅ Longest common substring problems

---

### 7. Interview-Specific Complexity Talking Points

#### **How to Discuss Complexity in Interview:**

**Step 1: State the basics clearly**
```
"For basic Trie operations:
- Insert, search, and prefix queries are all O(L) time
- Where L is the length of the word or prefix
- Space complexity depends on number of words and alphabet size"
```

**Step 2: Build up to total complexity**
```
"For the complete solution:
- Building the Trie from N words: O(N × L) time
- Processing queries: O(Q × L) for Q queries
- Total time complexity: O((N + Q) × L)
- Space complexity: O(N × L × ALPHABET_SIZE) worst case,
  but typically much less due to prefix sharing"
```

**Step 3: Compare with alternatives**
```
"Compared to brute force:
- Without Trie: would need O(N × L) per search
- With M queries: O(M × N × L) total
- Trie reduces this to O((M + N) × L)
- That's M times faster when M is large!"
```

**Step 4: Discuss optimizations**
```
"For further optimization:
- Use HashMap children for sparse data → reduces space
- Implement node pooling → reduces allocation overhead
- Add caching for frequent queries → O(1) repeated searches
- Compress single-child chains → Radix tree, reduces nodes"
```

---

### 8. Common Complexity Mistakes to Avoid

#### **❌ Mistake 1: Forgetting alphabet size**
```
Wrong: "Space is O(N × L)"
Right: "Space is O(N × L × ALPHABET_SIZE) worst case"
       "But O(total unique characters) with sharing"
```

#### **❌ Mistake 2: Confusing per-operation vs total**
```
Wrong: "Trie is O(1)"
Right: "Each operation is O(L), where L is word length"
       "Building N-word Trie is O(N × L)"
```

#### **❌ Mistake 3: Ignoring the constant factor**
```
Wrong: "Binary Trie XOR is O(1)"
Right: "O(32) for 32-bit integers, which is O(1) constant
       but important to mention the '32' factor"
```

#### **❌ Mistake 4: Not considering amortization**
```
Wrong: "Trie is slower than HashMap"
Right: "Single lookup: HashMap O(1) vs Trie O(L)
       But for prefix queries: Trie O(L) vs HashMap O(N×L)
       Trie wins when doing multiple prefix searches"
```

---

### 9. Quick Reference for Interview

**Standard Trie:**
- ⏱️ Time: O(L) all operations
- 💾 Space: O(N × L × 26) worst, O(shared prefixes) typical

**Binary Trie (XOR problems):**
- ⏱️ Time: O(32) = O(1) per operation
- 💾 Space: O(N × 32) = O(N)

**Trie with Counts:**
- ⏱️ Time: O(L) all operations (same as standard)
- 💾 Space: O(N × L × 26) + O(integers for counts)

**Autocomplete System:**
- ⏱️ Insert: O(L × M) where M = sentences with this prefix
- ⏱️ Search: O(L + M log M) for sorting top matches
- 💾 Space: O(N × L × M) with sentence storage at nodes

**Word Search II:**
- ⏱️ Time: O(M × N × 4^L) single DFS vs O(W × M × N × 4^L) without Trie
- 💾 Space: O(W × L) for Trie + O(L) recursion

---

### 10. Practice Questions for Complexity Understanding

**Q1:** "Why is Trie better than HashMap for autocomplete?"
**A:** HashMap can find exact match in O(1), but for prefix "ca" to find "cat", "car", "card", it must check all N words: O(N×L). Trie navigates to prefix in O(L), then collects matches in O(M) where M = number of matches. For 1M words with 100 matching "ca*", Trie: O(L+M)=O(2+100), HashMap: O(N×L)=O(1M×2).

**Q2:** "What's the space complexity if I insert the same word 1000 times?"
**A:** In Trie without counts: O(L) - same path used. With counter: O(L) structure + O(1) integer counter. HashMap: O(L) for word storage. Both: O(L), but Trie might be larger due to node overhead.

**Q3:** "Why use binary Trie instead of sorting for XOR maximum?"
**A:** Sorting is O(N log N), can't help find XOR maximum directly. Greedy bit-by-bit approach with Trie: O(N × 32) = O(N). For XOR, need to try opposite bit at each position - Trie enables this in O(32) per lookup.

**Q4:** "When would you NOT use a Trie?"
**A:** 
- Only exact match needed: HashMap better (O(1) vs O(L))
- Very long strings: space overhead too high
- Single query: build cost O(N×L) not amortized
- Need range queries: BST or specialized structure better

---

# Summary: Complete Trie Mastery
