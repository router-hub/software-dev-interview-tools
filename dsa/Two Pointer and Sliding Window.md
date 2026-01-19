
## L1: Introduction to Two Pointers & Sliding Window | Templates
**Question:** Understand the four patterns of two-pointer and sliding window problems.  
**Intuition:** Instead of checking all subarrays (O(nÂ²)), use two pointers to efficiently expand and shrink windows based on conditions.  
**Logic:** Four main patterns:
1. **Constant window size** - Fixed size k
2. **Longest/Maximum window** - Expand till valid, track max
3. **Shortest/Minimum window** - Expand till valid, then shrink
4. **Count subarrays** - Use "at most K" trick

**Templates:**
```java
// Pattern 1: Fixed Window Size
int fixedWindow(int[] arr, int k) {
    int windowSum = 0, maxSum = 0;

    // Build first window
    for (int i = 0; i < k; i++) {
        windowSum += arr[i];
    }
    maxSum = windowSum;

    // Slide window
    for (int i = k; i < arr.length; i++) {
        windowSum += arr[i] - arr[i - k];
        maxSum = Math.max(maxSum, windowSum);
    }
    return maxSum;
}

// Pattern 2: Longest/Maximum Window
int longestWindow(int[] arr, int condition) {
    int left = 0, maxLen = 0;

    for (int right = 0; right < arr.length; right++) {
        // Add arr[right] to window

        // Shrink while invalid
        while (windowIsInvalid()) {
            // Remove arr[left] from window
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}

// Pattern 3: Shortest/Minimum Window
int shortestWindow(int[] arr, int target) {
    int left = 0, minLen = Integer.MAX_VALUE;

    for (int right = 0; right < arr.length; right++) {
        // Add arr[right] to window

        // Shrink while valid
        while (windowIsValid()) {
            minLen = Math.min(minLen, right - left + 1);
            // Remove arr[left] from window
            left++;
        }
    }
    return minLen == Integer.MAX_VALUE ? 0 : minLen;
}

// Pattern 4: Count Subarrays
// Trick: countExact(K) = countAtMost(K) - countAtMost(K-1)
int countSubarrays(int[] arr, int k) {
    return countAtMost(arr, k) - countAtMost(arr, k - 1);
}

int countAtMost(int[] arr, int k) {
    int left = 0, count = 0;

    for (int right = 0; right < arr.length; right++) {
        // Add arr[right] to window

        // Shrink while invalid
        while (windowIsInvalid()) {
            // Remove arr[left] from window
            left++;
        }

        count += (right - left + 1); // All subarrays ending at right
    }
    return count;
}
```

---

## L2: Maximum Points from Cards
**Question:** Pick k cards from either end to maximize sum.  
**Intuition:** Instead of trying all combinations, pick all k from left first, then swap one by one with right side.  
**Logic:** Calculate sum of first k cards. Then replace leftmost with rightmost one by one.

**Java:**
```java
int maxScore(int[] cardPoints, int k) {
    int n = cardPoints.length;
    int leftSum = 0, rightSum = 0;

    // Take all k cards from left
    for (int i = 0; i < k; i++) {
        leftSum += cardPoints[i];
    }
    int maxSum = leftSum;

    // Replace left cards with right cards one by one
    for (int i = 0; i < k; i++) {
        leftSum -= cardPoints[k - 1 - i];
        rightSum += cardPoints[n - 1 - i];
        maxSum = Math.max(maxSum, leftSum + rightSum);
    }

    return maxSum;
}
// Time: O(k), Space: O(1)
```

---

## L3: Longest Substring Without Repeating Characters
**Question:** Find length of longest substring without repeating characters.  
**Intuition:** Use sliding window with HashSet. Expand right, shrink left when duplicate found.  
**Logic:** Track last occurrence of each character using HashMap for O(n) solution.

**Java:**
```java
// Approach 1: Using HashSet
int lengthOfLongestSubstring(String s) {
    HashSet<Character> set = new HashSet<>();
    int left = 0, maxLen = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);

        // Shrink window if duplicate
        while (set.contains(c)) {
            set.remove(s.charAt(left));
            left++;
        }

        set.add(c);
        maxLen = Math.max(maxLen, right - left + 1);
    }

    return maxLen;
}

// Approach 2: Using HashMap (Optimal)
int lengthOfLongestSubstringOptimal(String s) {
    HashMap<Character, Integer> map = new HashMap<>();
    int left = 0, maxLen = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);

        // If character seen before and in current window
        if (map.containsKey(c) && map.get(c) >= left) {
            left = map.get(c) + 1;
        }

        map.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }

    return maxLen;
}
// Time: O(n), Space: O(min(n, alphabet size))
```

---

## L4: Max Consecutive Ones III
**Question:** Find max consecutive 1s if you can flip at most k zeros.  
**Intuition:** Sliding window - count zeros. When zeros > k, shrink from left until zeros â‰¤ k.  
**Logic:** Expand right, track zero count. Shrink left when invalid.

**Java:**
```java
int longestOnes(int[] nums, int k) {
    int left = 0, maxLen = 0;
    int zeros = 0;

    for (int right = 0; right < nums.length; right++) {
        if (nums[right] == 0) {
            zeros++;
        }

        // Shrink window if zeros exceed k
        while (zeros > k) {
            if (nums[left] == 0) {
                zeros--;
            }
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1);
    }

    return maxLen;
}
// Time: O(n), Space: O(1)
```

---

## L5: Fruits Into Baskets
**Question:** Pick fruits from trees. Can only have 2 types of fruits in baskets. Find max fruits.  
**Intuition:** This is "longest subarray with at most 2 distinct elements". Use HashMap to track fruit counts.  
**Logic:** Sliding window with HashMap tracking distinct fruits.

**Java:**
```java
int totalFruit(int[] fruits) {
    HashMap<Integer, Integer> map = new HashMap<>();
    int left = 0, maxFruits = 0;

    for (int right = 0; right < fruits.length; right++) {
        map.put(fruits[right], map.getOrDefault(fruits[right], 0) + 1);

        // Shrink if more than 2 types
        while (map.size() > 2) {
            int leftFruit = fruits[left];
            map.put(leftFruit, map.get(leftFruit) - 1);
            if (map.get(leftFruit) == 0) {
                map.remove(leftFruit);
            }
            left++;
        }

        maxFruits = Math.max(maxFruits, right - left + 1);
    }

    return maxFruits;
}
// Time: O(n), Space: O(1) - at most 3 elements in map
```

---

## L6: Longest Substring with At Most K Distinct Characters
**Question:** Find longest substring with at most K distinct characters.  
**Intuition:** Generalization of fruits problem. Use HashMap to track character frequencies.  
**Logic:** Sliding window, shrink when distinct > K.

**Java:**
```java
int lengthOfLongestSubstringKDistinct(String s, int k) {
    if (k == 0) return 0;

    HashMap<Character, Integer> map = new HashMap<>();
    int left = 0, maxLen = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        map.put(c, map.getOrDefault(c, 0) + 1);

        // Shrink if distinct > k
        while (map.size() > k) {
            char leftChar = s.charAt(left);
            map.put(leftChar, map.get(leftChar) - 1);
            if (map.get(leftChar) == 0) {
                map.remove(leftChar);
            }
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1);
    }

    return maxLen;
}
// Time: O(n), Space: O(k)
```

---

## L7: Number of Substrings with All Three Characters
**Question:** Count substrings containing at least one a, b, and c.  
**Intuition:** At each position, if window contains all three, all subarrays extending to the right are valid.  
**Logic:** Track last occurrence of a, b, c. Count valid subarrays.

**Java:**
```java
int numberOfSubstrings(String s) {
    int[] lastSeen = {-1, -1, -1}; // for 'a', 'b', 'c'
    int count = 0;

    for (int i = 0; i < s.length(); i++) {
        lastSeen[s.charAt(i) - 'a'] = i;

        // Minimum of last seen positions
        int minIndex = Math.min(lastSeen[0], Math.min(lastSeen[1], lastSeen[2]));

        // All substrings starting from 0 to minIndex and ending at i are valid
        count += minIndex + 1;
    }

    return count;
}
// Time: O(n), Space: O(1)
```

---

## L8: Longest Repeating Character Replacement
**Question:** Replace at most k characters to get longest substring with same character.  
**Intuition:** In valid window: length - maxFrequency â‰¤ k (can replace remaining chars).  
**Logic:** Track max frequency in window. Shrink if replacements needed > k.

**Java:**
```java
int characterReplacement(String s, int k) {
    int[] count = new int[26];
    int left = 0, maxLen = 0;
    int maxFreq = 0;

    for (int right = 0; right < s.length(); right++) {
        count[s.charAt(right) - 'A']++;
        maxFreq = Math.max(maxFreq, count[s.charAt(right) - 'A']);

        // If replacements needed > k, shrink
        int windowLen = right - left + 1;
        if (windowLen - maxFreq > k) {
            count[s.charAt(left) - 'A']--;
            left++;
        }

        maxLen = Math.max(maxLen, right - left + 1);
    }

    return maxLen;
}
// Time: O(n), Space: O(26) = O(1)
```

---

## L9: Binary Subarrays with Sum
**Question:** Count subarrays with sum equal to goal (binary array).  
**Intuition:** Use trick: countExact(goal) = countAtMost(goal) - countAtMost(goal - 1).  
**Logic:** Implement countAtMost helper function.

**Java:**
```java
int numSubarraysWithSum(int[] nums, int goal) {
    return countAtMost(nums, goal) - countAtMost(nums, goal - 1);
}

int countAtMost(int[] nums, int goal) {
    if (goal < 0) return 0;

    int left = 0, sum = 0, count = 0;

    for (int right = 0; right < nums.length; right++) {
        sum += nums[right];

        // Shrink while sum > goal
        while (sum > goal) {
            sum -= nums[left];
            left++;
        }

        // All subarrays ending at right with sum â‰¤ goal
        count += (right - left + 1);
    }

    return count;
}
// Time: O(n), Space: O(1)
```

---

## L10: Count Number of Nice Subarrays
**Question:** Count subarrays with exactly k odd numbers.  
**Intuition:** Convert to binary array (odd = 1, even = 0). Then same as binary subarrays with sum.  
**Logic:** Use countAtMost trick.

**Java:**
```java
int numberOfSubarrays(int[] nums, int k) {
    return countAtMost(nums, k) - countAtMost(nums, k - 1);
}

int countAtMost(int[] nums, int k) {
    int left = 0, oddCount = 0, count = 0;

    for (int right = 0; right < nums.length; right++) {
        if (nums[right] % 2 == 1) {
            oddCount++;
        }

        // Shrink while oddCount > k
        while (oddCount > k) {
            if (nums[left] % 2 == 1) {
                oddCount--;
            }
            left++;
        }

        count += (right - left + 1);
    }

    return count;
}
// Time: O(n), Space: O(1)
```

---

## L11: Subarrays with K Different Integers
**Question:** Count subarrays with exactly K distinct integers.  
**Intuition:** Use trick: countExact(K) = countAtMost(K) - countAtMost(K - 1).  
**Logic:** Implement countAtMost with HashMap tracking distinct elements.

**Java:**
```java
int subarraysWithKDistinct(int[] nums, int k) {
    return countAtMost(nums, k) - countAtMost(nums, k - 1);
}

int countAtMost(int[] nums, int k) {
    HashMap<Integer, Integer> map = new HashMap<>();
    int left = 0, count = 0;

    for (int right = 0; right < nums.length; right++) {
        map.put(nums[right], map.getOrDefault(nums[right], 0) + 1);

        // Shrink while distinct > k
        while (map.size() > k) {
            int leftNum = nums[left];
            map.put(leftNum, map.get(leftNum) - 1);
            if (map.get(leftNum) == 0) {
                map.remove(leftNum);
            }
            left++;
        }

        count += (right - left + 1);
    }

    return count;
}
// Time: O(n), Space: O(k)
```

---

## L12: Minimum Window Substring
**Question:** Find minimum window in S that contains all characters of T.  
**Intuition:** Expand to find valid window (contains all chars). Then shrink to minimize.  
**Logic:** Use HashMap to track required vs current character counts.

**Java:**
```java
String minWindow(String s, String t) {
    if (s.length() < t.length()) return "";

    HashMap<Character, Integer> required = new HashMap<>();
    for (char c : t.toCharArray()) {
        required.put(c, required.getOrDefault(c, 0) + 1);
    }

    int left = 0, minLen = Integer.MAX_VALUE;
    int start = 0; // start of minimum window
    int matched = 0; // count of characters matched
    HashMap<Character, Integer> window = new HashMap<>();

    for (int right = 0; right < s.length(); right++) {
        char rightChar = s.charAt(right);
        window.put(rightChar, window.getOrDefault(rightChar, 0) + 1);

        // Check if this character matches requirement
        if (required.containsKey(rightChar) && 
            window.get(rightChar).intValue() == required.get(rightChar).intValue()) {
            matched++;
        }

        // Try to shrink window
        while (matched == required.size()) {
            // Update minimum window
            if (right - left + 1 < minLen) {
                minLen = right - left + 1;
                start = left;
            }

            char leftChar = s.charAt(left);
            window.put(leftChar, window.get(leftChar) - 1);

            if (required.containsKey(leftChar) && 
                window.get(leftChar) < required.get(leftChar)) {
                matched--;
            }

            left++;
        }
    }

    return minLen == Integer.MAX_VALUE ? "" : s.substring(start, start + minLen);
}
// Time: O(m + n), Space: O(m + n)
```

---

## L13: Minimum Window Subsequence
**Question:** Find minimum window in S containing T as subsequence.  
**Intuition:** Two-pointer. Expand to find T as subsequence. Then contract from left.  
**Logic:** Expand right to match T. Contract left while valid. Repeat.

**Java:**
```java
String minWindowSubsequence(String s, String t) {
    int sLen = s.length(), tLen = t.length();
    int start = -1, minLen = Integer.MAX_VALUE;
    int i = 0, j = 0;

    while (i < sLen) {
        // Match characters of T
        if (s.charAt(i) == t.charAt(j)) {
            j++;

            // Found complete match
            if (j == tLen) {
                int end = i;
                j--;

                // Contract from left
                while (j >= 0) {
                    if (s.charAt(i) == t.charAt(j)) {
                        j--;
                    }
                    i--;
                }

                i++; // adjust
                j++; // adjust

                // Update minimum window
                if (end - i + 1 < minLen) {
                    minLen = end - i + 1;
                    start = i;
                }

                j = 0; // reset for next window
            }
        }
        i++;
    }

    return start == -1 ? "" : s.substring(start, start + minLen);
}
// Time: O(m * n), Space: O(1)
```

---

## L14: Container With Most Water
**Question:** Find two lines that form container with maximum water.  
**Intuition:** Two pointers at both ends. Move pointer with smaller height (can't do better with current smaller height).  
**Logic:** Calculate area, move pointer with smaller height inward.

**Java:**
```java
int maxArea(int[] height) {
    int left = 0, right = height.length - 1;
    int maxWater = 0;

    while (left < right) {
        int width = right - left;
        int h = Math.min(height[left], height[right]);
        int area = width * h;
        maxWater = Math.max(maxWater, area);

        // Move pointer with smaller height
        if (height[left] < height[right]) {
            left++;
        } else {
            right--;
        }
    }

    return maxWater;
}
// Time: O(n), Space: O(1)
```

---

## L15: Three Sum
**Question:** Find all unique triplets that sum to zero.  
**Intuition:** Sort array. Fix one element, use two pointers for remaining two.  
**Logic:** For each element, use two-pointer to find pairs that sum to -element.

**Java:**
```java
List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();

    for (int i = 0; i < nums.length - 2; i++) {
        // Skip duplicates for first element
        if (i > 0 && nums[i] == nums[i - 1]) continue;

        int left = i + 1, right = nums.length - 1;
        int target = -nums[i];

        while (left < right) {
            int sum = nums[left] + nums[right];

            if (sum == target) {
                result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                // Skip duplicates
                while (left < right && nums[left] == nums[left + 1]) left++;
                while (left < right && nums[right] == nums[right - 1]) right--;

                left++;
                right--;
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
    }

    return result;
}
// Time: O(nÂ²), Space: O(1) excluding output
```

---

## L16: Four Sum
**Question:** Find all unique quadruplets that sum to target.  
**Intuition:** Similar to 3Sum but with two fixed elements.  
**Logic:** Fix two elements, use two pointers for remaining two.

**Java:**
```java
List<List<Integer>> fourSum(int[] nums, int target) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    int n = nums.length;

    for (int i = 0; i < n - 3; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue;

        for (int j = i + 1; j < n - 2; j++) {
            if (j > i + 1 && nums[j] == nums[j - 1]) continue;

            int left = j + 1, right = n - 1;

            while (left < right) {
                long sum = (long)nums[i] + nums[j] + nums[left] + nums[right];

                if (sum == target) {
                    result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));

                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;

                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }
    }

    return result;
}
// Time: O(nÂ³), Space: O(1)
```

---

## Bonus: Two Pointer Patterns Summary

### Pattern 1: Opposite Direction
- Start from both ends, move towards center
- **Examples:** Two Sum (sorted), Container with water, Valid palindrome
- **When:** Array is sorted or needs comparison from both ends

### Pattern 2: Same Direction
- Both pointers move in same direction (slow/fast)
- **Examples:** Remove duplicates, Move zeros, Partition array
- **When:** In-place modification, partitioning

### Pattern 3: Sliding Window (Variable Size)
- Expand right, shrink left based on condition
- **Examples:** Longest substring, Max consecutive ones
- **When:** Subarray/substring optimization with condition

### Pattern 4: Sliding Window (Fixed Size)
- Maintain constant window size
- **Examples:** Max sum of k elements, Max average
- **When:** Fixed size window required

### Pattern 5: Counting with "At Most" Trick
- Count exact = count at most K - count at most (K-1)
- **Examples:** Binary subarrays sum, Nice subarrays, K distinct
- **When:** Counting subarrays with exact condition

---