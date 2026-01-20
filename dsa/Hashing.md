
## Table of Contents
- **Part 1: Hashing Fundamentals** - HashMap, HashSet, How They Work
- **Part 2: Basic Patterns** - Frequency, Lookup, Duplicates
- **Part 3: Subarray Patterns** - Prefix Sum + Hashing
- **Part 4: Advanced Patterns** - Sliding Window, Anagrams, More

---

# Part 1: Hashing Fundamentals

## Introduction to Hashing
**Question:** What is Hashing? Why do we need it?
**Intuition:** Imagine a library with millions of books. Finding a book by checking each shelf takes forever (O(n))! But with a catalog system that tells you EXACTLY which shelf (hash function -> index), you find it instantly (O(1))! That's **hashing** - converting data into an index/address for super-fast access.

**Real-World Use Cases:**
- **Password Storage** - Hash passwords, never store plain text
- **Caching** - Store frequently accessed data (LRU cache)
- **Spell Checkers** - Quick dictionary lookup
- **Databases** - Index tables for fast queries
- **Compilers** - Symbol tables (variable names -> memory)
- **Blockchain** - Hash of previous block
- **Load Balancing** - Consistent hashing for servers
- **Deduplication** - Find duplicate files/data
- **Social Networks** - Friend lists, followers
- **URL Shorteners** - Map short URL to long URL

**Core Concept:**

```
Hash Function: Key -> Hash Code -> Index
"apple" -> hashCode() -> 93029210 -> 93029210 % arraySize -> index
```

---

## HashMap vs HashSet vs Hashtable

### **HashMap**
- **Stores:** Key-Value pairs
- **Allows:** One null key, multiple null values
- **Order:** No guaranteed order (use LinkedHashMap for insertion order)
- **Thread-Safe:** NO (use ConcurrentHashMap)
- **Performance:** O(1) average for get/put

**Java:**

```java
Map<String, Integer> map = new HashMap<>();

// Basic operations
map.put("apple", 5); // Add/Update - O(1)
map.get("apple"); // Get - O(1) -> returns 5
map.containsKey("apple"); // Check key - O(1) -> true
map.containsValue(5); // Check value - O(n) š ï¸
map.remove("apple"); // Remove - O(1)
map.size(); // Size - O(1)
map.isEmpty(); // Check empty - O(1)

// Get with default
map.getOrDefault("banana", 0); // Returns 0 if not exists

// Update pattern
map.put("apple", map.getOrDefault("apple", 0) + 1); // Increment count
```

### **HashSet**
- **Stores:** Unique elements only (no duplicates)
- **Allows:** One null element
- **Order:** No guaranteed order
- **Use:** When you only care about presence, not count

**Java:**

```java
Set<String> set = new HashSet<>();

set.add("apple"); // Add - O(1)
set.contains("apple"); // Check - O(1) -> true
set.remove("apple"); // Remove - O(1)
set.size(); // Size - O(1)
```

### **Hashtable (Legacy)**
- Old class, **synchronized** (thread-safe but slow)
- No null keys or values
- **Don't use** - Use ConcurrentHashMap instead!

---

## How HashMap Works Internally

**Under the Hood:**
1. HashMap is **array of buckets** (linked lists/trees)
2. **Hash Function** converts key to hash code
3. **Index** = hashCode % arraySize
4. **Collision** handling: separate chaining (linked list) or open addressing

**Structure:**

```
HashMap Internal:
Index 0: [key1->val1] -> [key7->val7] (collision chain)
Index 1: null
Index 2: [key2->val2]
Index 3: [key3->val3] -> [key9->val9] (collision chain)
...
```

**Load Factor & Capacity:**
- **Capacity**: Size of array (default 16)
- **Load Factor**: Threshold for resizing (default 0.75)
- When size > capacity * loadFactor -> **rehashing** (doubles size)

**Important Methods:**

```java
// Override in custom objects used as keys
@Override
public int hashCode() {
 // Must be consistent: equals() objects -> same hashCode
}

@Override
public boolean equals(Object obj) {
 // Define equality
}
```

**Why Override hashCode and equals?**

```java
class Person {
 String name;
 int age;

 // MUST override both for HashMap key!
 @Override
 public int hashCode() {
 return Objects.hash(name, age);
 }

 @Override
 public boolean equals(Object obj) {
 if (this == obj) return true;
 if (obj == null || getClass() != obj.getClass()) return false;
 Person p = (Person) obj;
 return age == p.age && Objects.equals(name, p.name);
 }
}

// Now can use as HashMap key
Map<Person, String> map = new HashMap<>();
```

---

## HashMap Iteration Patterns

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.put("b", 2);
map.put("c", 3);

// Pattern 1: Iterate over entries (BEST for key-value together)
for (Map.Entry<String, Integer> entry : map.entrySet()) {
 System.out.println(entry.getKey() + " -> " + entry.getValue());
}

// Pattern 2: Iterate over keys only
for (String key : map.keySet()) {
 System.out.println(key + " -> " + map.get(key));
}

// Pattern 3: Iterate over values only
for (Integer value : map.values()) {
 System.out.println(value);
}

// Pattern 4: Using forEach (Java 8+)
map.forEach((key, value) -> {
 System.out.println(key + " -> " + value);
});

// Pattern 5: Using streams (Java 8+)
map.entrySet().stream()
 .filter(e -> e.getValue() > 1)
 .forEach(e -> System.out.println(e.getKey()));
```

---

# Part 2: Basic Hashing Patterns

## Pattern 1: Frequency Counter

### Problem 1: First Unique Character
**Question:** Find first non-repeating character in string.
**Intuition:** **Two-pass approach**: (1) Count frequencies with HashMap, (2) Scan string, return first with freq = 1.
**Java:**

```java
int firstUniqChar(String s) {
    Map<Character, Integer> freq = new HashMap<>();

    // Pass 1: Count frequencies
    for (char c : s.toCharArray()) {
        freq.put(c, freq.getOrDefault(c, 0) + 1);
    }

    // Pass 2: Find first unique
    for (int i = 0; i < s.length(); i++) {
        if (freq.get(s.charAt(i)) == 1) {
            return i;
        }
    }

    return -1;
}
// Time: O(n), Space: O(1) since at most 26 letters
```

---

### Problem 2: Valid Anagram
**Question:** Check if two strings are anagrams.
**Intuition:** Anagrams have **same character frequencies**! Count frequencies of both, compare.
**Java:**

```java
boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;

    Map<Character, Integer> count = new HashMap<>();

    // Add frequencies from s
    for (char c : s.toCharArray()) {
        count.put(c, count.getOrDefault(c, 0) + 1);
    }

    // Subtract frequencies from t
    for (char c : t.toCharArray()) {
        if (!count.containsKey(c)) return false;
        count.put(c, count.get(c) - 1);
        if (count.get(c) < 0) return false;
    }

    return true;
}
// Time: O(n), Space: O(1)

// Alternative: Array for lowercase letters
boolean isAnagramArray(String s, String t) {
    if (s.length() != t.length()) return false;

    int[] count = new int[26];

    for (int i = 0; i < s.length(); i++) {
        count[s.charAt(i) - 'a']++;
        count[t.charAt(i) - 'a']--;
    }

    for (int c : count) {
        if (c != 0) return false;
    }

    return true;
}
```

---

### Problem 3: Group Anagrams
**Question:** Group strings that are anagrams together.
**Intuition:** Anagrams have same sorted version! Use **sorted string as key** in HashMap. All anagrams map to same key!
**Java:**

```java
List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> map = new HashMap<>();

    for (String str : strs) {
        char[] chars = str.toCharArray();
        Arrays.sort(chars);
        String key = new String(chars);

        map.putIfAbsent(key, new ArrayList<>());
        map.get(key).add(str);
    }

    return new ArrayList<>(map.values());
}
// Time: O(n * k log k) where k = average string length
// Space: O(n * k)

// Alternative: Character count as key (faster!)
List<List<String>> groupAnagramsOptimized(String[] strs) {
    Map<String, List<String>> map = new HashMap<>();

    for (String str : strs) {
        int[] count = new int[26];
        for (char c : str.toCharArray()) {
            count[c - 'a']++;
        }

        // Build key from counts
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            if (count[i] > 0) {
                key.append((char) ('a' + i)).append(count[i]);
            }
        }

        map.putIfAbsent(key.toString(), new ArrayList<>());
        map.get(key.toString()).add(str);
    }

    return new ArrayList<>(map.values());
}
// Time: O(n * k), Space: O(n * k)
```

---

## Pattern 2: Lookup/Check Existence

### Problem 4: Two Sum 
**Question:** Find two indices where nums[i] + nums[j] = target.
**Intuition:** **One-pass with HashMap!** For each num, check if (target - num) exists in map. If yes, found! If no, add num to map.
**Java:**

```java
int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>(); // value -> index

    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];

        if (map.containsKey(complement)) {
            return new int[]{map.get(complement), i};
        }

        map.put(nums[i], i);
    }

    return new int[]{-1, -1};
}
// Time: O(n), Space: O(n)
// Much better than nested loop: O(n²)!
```

---

### Problem 5: Contains Duplicate
**Question:** Check if array has any duplicates.
**Intuition:** Use **HashSet** to track seen elements. If already in set, duplicate!
**Java:**

```java
boolean containsDuplicate(int[] nums) {
    Set<Integer> seen = new HashSet<>();

    for (int num : nums) {
        if (seen.contains(num)) {
            return true; // Duplicate found!
        }
        seen.add(num);
    }

    return false;
}
// Time: O(n), Space: O(n)
```

---

### Problem 6: Contains Duplicate II (Within K Distance)
**Question:** Check if duplicate exists within k indices apart.
**Intuition:** Use HashMap to store **value -> last index**. When seeing duplicate, check distance!
**Java:**

```java
boolean containsNearbyDuplicate(int[] nums, int k) {
    Map<Integer, Integer> map = new HashMap<>(); // value -> last index

    for (int i = 0; i < nums.length; i++) {
        if (map.containsKey(nums[i])) {
            if (i - map.get(nums[i]) <= k) {
                return true;
            }
        }
        map.put(nums[i], i);
    }

    return false;
}
// Time: O(n), Space: O(n)
```

---

### Problem 7: Isomorphic Strings
**Question:** Check if two strings are isomorphic (character mapping exists).
**Intuition:** Need **bidirectional mapping**: s -> t AND t -> s. Use two HashMaps!
**Java:**

```java
boolean isIsomorphic(String s, String t) {
    if (s.length() != t.length()) return false;

    Map<Character, Character> mapST = new HashMap<>();
    Map<Character, Character> mapTS = new HashMap<>();

    for (int i = 0; i < s.length(); i++) {
        char c1 = s.charAt(i);
        char c2 = t.charAt(i);

        // Check s -> t mapping
        if (mapST.containsKey(c1)) {
            if (mapST.get(c1) != c2) return false;
        } else {
            mapST.put(c1, c2);
        }

        // Check t -> s mapping
        if (mapTS.containsKey(c2)) {
            if (mapTS.get(c2) != c1) return false;
        } else {
            mapTS.put(c2, c1);
        }
    }

    return true;
}

// Example:
// "egg", "add" -> true (e->a, g->d)
// "foo", "bar" -> false (o maps to both a and r)
```

---

### Problem 8: Word Pattern
**Question:** Check if pattern matches string (like isomorphic but with words).
**Intuition:** Same as isomorphic! Map pattern char †” word bidirectionally.
**Java:**

```java
boolean wordPattern(String pattern, String s) {
    String[] words = s.split(" ");
    if (pattern.length() != words.length) return false;

    Map<Character, String> charToWord = new HashMap<>();
    Map<String, Character> wordToChar = new HashMap<>();

    for (int i = 0; i < pattern.length(); i++) {
        char c = pattern.charAt(i);
        String word = words[i];

        if (charToWord.containsKey(c)) {
            if (!charToWord.get(c).equals(word)) return false;
        } else {
            charToWord.put(c, word);
        }

        if (wordToChar.containsKey(word)) {
            if (wordToChar.get(word) != c) return false;
        } else {
            wordToChar.put(word, c);
        }
    }

    return true;
}
```

---

# Part 3: Subarray Patterns (Prefix Sum + Hashing)

## Pattern 3: Subarray Sum Problems

### Problem 9: Subarray Sum Equals K 
**Question:** Count subarrays with sum = k.
**Intuition:** **Prefix Sum + HashMap magic!** If prefixSum[j] - prefixSum[i] = k, then subarray [i+1...j] has sum k. Rearrange: prefixSum[i] = prefixSum[j] - k. So for each j, check if (currentSum - k) exists in map!
**Java:**

```java
int subarraySum(int[] nums, int k) {
    Map<Integer, Integer> prefixSumCount = new HashMap<>();
    prefixSumCount.put(0, 1); // Base case: empty prefix

    int count = 0;
    int currentSum = 0;

    for (int num : nums) {
        currentSum += num;

        // Check if (currentSum - k) exists
        if (prefixSumCount.containsKey(currentSum - k)) {
            count += prefixSumCount.get(currentSum - k);
        }

        // Add current sum to map
        prefixSumCount.put(currentSum, prefixSumCount.getOrDefault(currentSum, 0) + 1);
    }

    return count;
}
// Time: O(n), Space: O(n)
```

**Example:**

```
nums = [1, 2, 3], k = 3
Step by step:
i=0, num=1: sum=1, map={0:1, 1:1}, count=0
i=1, num=2: sum=3, map={0:1, 1:1, 3:1}, check (3-3=0) exists -> count=1
i=2, num=3: sum=6, map={0:1, 1:1, 3:1, 6:1}, check (6-3=3) exists -> count=2

Answer: 2 subarrays -> [3], [1,2]
```

---

### Problem 10: Longest Subarray with Sum K
**Question:** Find length of longest subarray with sum = k.
**Intuition:** Similar to count! Map stores **prefix sum -> first index**. For each position, if (currentSum - k) exists, calculate length!
**Java:**

```java
int maxSubArrayLen(int[] nums, int k) {
    Map<Integer, Integer> prefixSumIndex = new HashMap<>();
    prefixSumIndex.put(0, -1); // Base case

    int maxLen = 0;
    int currentSum = 0;

    for (int i = 0; i < nums.length; i++) {
        currentSum += num;

        if (prefixSumIndex.containsKey(currentSum - k)) {
            maxLen = Math.max(maxLen, i - prefixSumIndex.get(currentSum - k));
        }

        // Only add if not exists (want earliest index)
        if (!prefixSumIndex.containsKey(currentSum)) {
            prefixSumIndex.put(currentSum, i);
        }
    }

    return maxLen;
}
```

---

### Problem 11: Longest Subarray with 0 Sum
**Question:** Find longest subarray with sum = 0.
**Intuition:** Special case of sum = k where k = 0! If same prefix sum appears twice, subarray between them has sum 0!
**Java:**

```java
int maxLen(int arr[]) {
    Map<Integer, Integer> prefixSumIndex = new HashMap<>();
    prefixSumIndex.put(0, -1);

    int maxLen = 0;
    int currentSum = 0;

    for (int i = 0; i < arr.length; i++) {
        currentSum += arr[i];

        if (prefixSumIndex.containsKey(currentSum)) {
            maxLen = Math.max(maxLen, i - prefixSumIndex.get(currentSum));
        } else {
            prefixSumIndex.put(currentSum, i);
        }
    }

    return maxLen;
}
```

---

### Problem 12: Count Subarrays with Equal 0s and 1s
**Question:** Binary array, count subarrays with equal 0s and 1s.
**Intuition:** **Transform problem!** Replace 0 with -1. Now problem becomes: count subarrays with sum = 0! Use Problem 9's approach.
**Java:**

```java
int countSubarrays(int[] arr) {
    Map<Integer, Integer> prefixSumCount = new HashMap<>();
    prefixSumCount.put(0, 1);

    int count = 0;
    int currentSum = 0;

    for (int num : arr) {
        currentSum += (num == 1) ? 1 : -1; // Transform 0 -> -1

        count += prefixSumCount.getOrDefault(currentSum, 0);
        prefixSumCount.put(currentSum, prefixSumCount.getOrDefault(currentSum, 0) + 1);
    }

    return count;
}
```

---

### Problem 13: Count Subarrays with XOR K
**Question:** Count subarrays with XOR = k.
**Intuition:** **XOR prefix sum!** Property: prefixXOR[i] XOR prefixXOR[j] = XOR of subarray [i+1...j]. So check if (currentXOR XOR k) exists!
**Java:**

```java
int subarrayXOR(int[] arr, int k) {
    Map<Integer, Integer> xorCount = new HashMap<>();
    xorCount.put(0, 1);

    int count = 0;
    int currentXOR = 0;

    for (int num : arr) {
        currentXOR ^= num;

        // Check if (currentXOR ^ k) exists
        int target = currentXOR ^ k;
        count += xorCount.getOrDefault(target, 0);

        xorCount.put(currentXOR, xorCount.getOrDefault(currentXOR, 0) + 1);
    }

    return count;
}
```

---

## Pattern 4: Contiguous Sequence

### Problem 14: Longest Consecutive Sequence 
**Question:** Find length of longest consecutive sequence in unsorted array.
**Intuition:** Use **HashSet** for O(1) lookup! For each number, check if it's **start of sequence** (num-1 doesn't exist). If yes, count sequence length!
**Java:**

```java
int longestConsecutive(int[] nums) {
    Set<Integer> numSet = new HashSet<>();
    for (int num : nums) {
        numSet.add(num);
    }

    int longestStreak = 0;

    for (int num : numSet) {
        // Check if start of sequence
        if (!numSet.contains(num - 1)) {
            int currentNum = num;
            int currentStreak = 1;

            // Count consecutive numbers
            while (numSet.contains(currentNum + 1)) {
                currentNum++;
                currentStreak++;
            }

            longestStreak = Math.max(longestStreak, currentStreak);
        }
    }

    return longestStreak;
}
// Time: O(n), Space: O(n)
// Brilliant! Each element visited at most twice.
```

**Example:**

```
nums = [100, 4, 200, 1, 3, 2]
Set = {100, 4, 200, 1, 3, 2}

Check 1: is start (0 not in set)
 -> 1, 2, 3, 4 -> length = 4 œ“

Check 2: not start (1 in set) -> skip
Check 3: not start (2 in set) -> skip
Check 4: not start (3 in set) -> skip

Check 100: is start (99 not in set)
 -> just 100 -> length = 1

Check 200: is start (199 not in set)
 -> just 200 -> length = 1

Answer: 4
```

---

# Part 4: Advanced Patterns

## Pattern 5: Sliding Window + Hashing

### Problem 15: Longest Substring Without Repeating Characters
**Question:** Find length of longest substring without repeating characters.
**Intuition:** **Sliding window + HashMap!** Map stores **char -> last index**. When duplicate found, move left pointer to right of last occurrence!
**Java:**

```java
int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> lastIndex = new HashMap<>();
    int maxLen = 0;
    int left = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);

        // If duplicate, move left pointer
        if (lastIndex.containsKey(c)) {
            left = Math.max(left, lastIndex.get(c) + 1);
        }

        lastIndex.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }

    return maxLen;
}
// Time: O(n), Space: O(min(n, alphabet size))
```

---

### Problem 16: Longest Substring with K Distinct Characters
**Question:** Find longest substring with at most K distinct characters.
**Intuition:** **Sliding window!** Expand right, when distinct > K, shrink left. HashMap tracks **char -> count**.
**Java:**

```java
int lengthOfLongestSubstringKDistinct(String s, int k) {
    if (k == 0) return 0;

    Map<Character, Integer> charCount = new HashMap<>();
    int maxLen = 0;
    int left = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        charCount.put(c, charCount.getOrDefault(c, 0) + 1);

        // Shrink window if too many distinct
        while (charCount.size() > k) {
            char leftChar = s.charAt(left);
            charCount.put(leftChar, charCount.get(leftChar) - 1);
            if (charCount.get(leftChar) == 0) {
                charCount.remove(leftChar);
            }
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1);
    }

    return maxLen;
}
```

---

### Problem 17: Minimum Window Substring 
**Question:** Find minimum window in s containing all characters of t.
**Intuition:** **Two pointers + two HashMaps!** One for target counts, one for window counts. Expand right to include chars, shrink left when valid window found!
**Java:**

```java
String minWindow(String s, String t) {
    if (s.length() < t.length()) return "";

    Map<Character, Integer> targetCount = new HashMap<>();
    for (char c : t.toCharArray()) {
        targetCount.put(c, targetCount.getOrDefault(c, 0) + 1);
    }

    Map<Character, Integer> windowCount = new HashMap<>();
    int required = targetCount.size();
    int formed = 0;

    int left = 0;
    int minLen = Integer.MAX_VALUE;
    int minStart = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        windowCount.put(c, windowCount.getOrDefault(c, 0) + 1);

        // Check if frequency matches
        if (targetCount.containsKey(c) &&
                windowCount.get(c).intValue() == targetCount.get(c).intValue()) {
            formed++;
        }

        // Shrink window
        while (left <= right && formed == required) {
            if (right - left + 1 < minLen) {
                minLen = right - left + 1;
                minStart = left;
            }

            char leftChar = s.charAt(left);
            windowCount.put(leftChar, windowCount.get(leftChar) - 1);

            if (targetCount.containsKey(leftChar) &&
                    windowCount.get(leftChar) < targetCount.get(leftChar)) {
                formed--;
            }

            left++;
        }
    }

    return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
}
// Time: O(s + t), Space: O(s + t)
```

---

## Pattern 6: Advanced Problems

### Problem 18: LRU Cache 
**Question:** Implement LRU (Least Recently Used) Cache with O(1) get/put.
**Intuition:** **HashMap + Doubly Linked List!** HashMap for O(1) lookup, DLL for O(1) add/remove and tracking order!
**Java:**

```java
class LRUCache {
 class Node {
 int key, value;
 Node prev, next;

 Node(int key, int value) {
 this.key = key;
 this.value = value;
 }
 }

 Map<Integer, Node> cache;
 Node head, tail;
 int capacity;

 public LRUCache(int capacity) {
 this.capacity = capacity;
 cache = new HashMap<>();

 // Dummy head and tail
 head = new Node(0, 0);
 tail = new Node(0, 0);
 head.next = tail;
 tail.prev = head;
 }

 public int get(int key) {
 if (!cache.containsKey(key)) return -1;

 Node node = cache.get(key);
 remove(node);
 addToFront(node);

 return node.value;
 }

 public void put(int key, int value) {
 if (cache.containsKey(key)) {
 remove(cache.get(key));
 }

 Node node = new Node(key, value);
 cache.put(key, node);
 addToFront(node);

 if (cache.size() > capacity) {
 Node lru = tail.prev;
 remove(lru);
 cache.remove(lru.key);
 }
 }

 void remove(Node node) {
 node.prev.next = node.next;
 node.next.prev = node.prev;
 }

 void addToFront(Node node) {
 node.next = head.next;
 node.prev = head;
 head.next.prev = node;
 head.next = node;
 }
}
// get: O(1), put: O(1)
```

---

### Problem 19: 4Sum II
**Question:** Given 4 arrays, find count of tuples where nums1[i] + nums2[j] + nums3[k] + nums4[l] = 0.
**Intuition:** **Split into two groups!** Map stores (nums1[i] + nums2[j]) -> count. Then check if -(nums3[k] + nums4[l]) exists!
**Java:**

```java
int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
    Map<Integer, Integer> sumCount = new HashMap<>();

    // Store all sums from nums1 and nums2
    for (int a : nums1) {
        for (int b : nums2) {
            int sum = a + b;
            sumCount.put(sum, sumCount.getOrDefault(sum, 0) + 1);
        }
    }

    int count = 0;

    // Check complements from nums3 and nums4
    for (int c : nums3) {
        for (int d : nums4) {
            int target = -(c + d);
            count += sumCount.getOrDefault(target, 0);
        }
    }

    return count;
}
// Time: O(n²), Space: O(n²)
```

---

### Problem 20: Random Pick with Blacklist
**Question:** Given N and blacklist, return random number in [0, N) excluding blacklist in O(1).
**Intuition:** **Map blacklist to valid range!** Remap blacklisted numbers < N-len(blacklist) to valid numbers >= N-len(blacklist).
**Java:**

```java
class Solution {
    Map<Integer, Integer> map;
    Random random;
    int M;

    public Solution(int n, int[] blacklist) {
        map = new HashMap<>();
        random = new Random();
        M = n - blacklist.length;

        Set<Integer> blackSet = new HashSet<>();
        for (int b : blacklist) blackSet.add(b);

        int last = n - 1;
        for (int b : blacklist) {
            if (b < M) {
                while (blackSet.contains(last)) last--;
                map.put(b, last);
                last--;
            }
        }
    }

    public int pick() {
        int rand = random.nextInt(M);
        return map.getOrDefault(rand, rand);
    }
}
```

---

# Summary: Hashing Patterns

## Pattern Recognition Guide

### ðŸŽ¯ **Pattern 1: Frequency/Count**
**Use HashMap<Element, Count>**
- Character/word frequencies
- First unique/repeating
- Anagrams
- Keywords: "count", "frequency", "unique", "duplicate"

### ðŸŽ¯ **Pattern 2: Lookup/Check**
**Use HashMap or HashSet**
- Two Sum family
- Contains duplicate
- Isomorphic mapping
- Keywords: "find", "check", "exists", "pair"

### ðŸŽ¯ **Pattern 3: Prefix Sum + Hash**
**Use HashMap<PrefixSum, Count/Index>**
- Subarray sum = K
- Longest subarray
- Equal 0s and 1s
- Keywords: "subarray", "sum", "equal", "continuous"

### ðŸŽ¯ **Pattern 4: Sliding Window + Hash**
**Use HashMap for window state**
- Longest substring
- K distinct characters
- Minimum window
- Keywords: "substring", "window", "consecutive"

### ðŸŽ¯ **Pattern 5: Advanced**
**Custom data structures**
- LRU Cache (HashMap + DLL)
- Design problems
- Optimization with hashing

---

## Time Complexity Cheat Sheet

| Operation | HashMap | HashSet | Array |
|-----------|---------|---------|-------|
| Insert | O(1) avg | O(1) avg | O(1) end, O(n) mid |
| Delete | O(1) avg | O(1) avg | O(n) |
| Search | O(1) avg | O(1) avg | O(n) linear, O(log n) binary |
| Iterate | O(n) | O(n) | O(n) |
| Space | O(n) | O(n) | O(n) |

**Worst case:** O(n) for all HashMap/HashSet operations (hash collisions)

---

## Common Mistakes

Œ **Using containsValue() on HashMap**

```java
map.containsValue(5); // O(n) - Slow!
```

œ... Better: Maintain reverse map if needed

Œ **Modifying HashMap while iterating**

```java
for (String key : map.keySet()) {
 map.remove(key); // ConcurrentModificationException!
}
```

œ... Use Iterator with remove() or collect keys first

Œ **Not handling null carefully**

```java
map.get(key).toString(); // NullPointerException if key doesn't exist!
```

œ... Use getOrDefault() or check containsKey()

Œ **Forgetting to override hashCode/equals**

```java
class Person { String name; }
Map<Person, String> map = new HashMap<>(); // Won't work correctly!
```

œ... Override both hashCode() and equals()

---

## Interview Tips

### HashMap Decision Tree:

```
Need to count/track frequency? -> HashMap<Element, Count>
Need to check existence? -> HashSet
Need order preserved? -> LinkedHashMap/LinkedHashSet
Need sorted order? -> TreeMap/TreeSet
Need to map both ways? -> Two HashMaps
Need to track index? -> HashMap<Element, Index>
```

### Performance Tips:
1. **Initial Capacity**: `new HashMap<>(expectedSize)` avoids resizing
2. **Load Factor**: Default 0.75 is good for most cases
3. **Null Handling**: HashMap allows one null key, HashSet one null element
4. **Thread Safety**: Use `ConcurrentHashMap` for concurrent access

---

## Real Interview Questions by Company

**Google:**
- Two Sum, 4Sum variants
- Group Anagrams
- Longest Consecutive Sequence

**Amazon:**
- Subarray Sum Equals K
- LRU Cache
- Minimum Window Substring

**Facebook:**
- Valid Anagram
- Isomorphic Strings
- Word Pattern

**Microsoft:**
- Contains Duplicate II
- Longest Substring Without Repeating
- First Unique Character
