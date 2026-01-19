## L1: Largest Element in an Array

**Question:** Find the largest element in an array.
**Intuition:** Keep track of maximum while traversing. Update max when you find bigger element.
**Logic:** Single pass through array, compare each element with current max.

**Java:**

```java
int findLargest(int[] arr) {
    int max = arr[0];
    for (int i = 1; i < arr.length; i++) {
        if (arr[i] > max) {
            max = arr[i];
        }
    }
    return max;
}
```

**Time Complexity:** O(n) — Must examine every element once.
**Space Complexity:** O(1) — No extra space used.

---

## L2: Second Largest Element in an Array

**Question:** Find second largest element without sorting.
**Intuition:** Track both largest and second largest in single pass. Update second largest when you update largest.
**Logic:** Maintain two variables, update them based on comparisons.

**Java:**

```java
int findSecondLargest(int[] arr) {
    int largest = arr[0], secondLargest = -1;

    for (int i = 1; i < arr.length; i++) {
        if (arr[i] > largest) {
            secondLargest = largest;
            largest = arr[i];
        } else if (arr[i] > secondLargest && arr[i] != largest) {
            secondLargest = arr[i];
        }
    }
    return secondLargest;
}
```

**Time Complexity:** O(n) — Each element checked once.
**Space Complexity:** O(1) — Only two variables used.

---

## L3: Check if Array is Sorted

**Question:** Check if array is sorted in non-decreasing order.
**Intuition:** If sorted, each element should be ≤ next element. Single violation means not sorted.
**Logic:** Compare adjacent elements.

**Java:**

```java
boolean isSorted(int[] arr) {
    for (int i = 0; i < arr.length - 1; i++) {
        if (arr[i] > arr[i + 1]) {
            return false;
        }
    }
    return true;
}
```

**Time Complexity:** O(n) — Must examine each adjacent pair.
**Space Complexity:** O(1) — Just a flag/loop variable.

---

## L4: Remove Duplicates from Sorted Array

**Question:** Remove duplicates in-place from sorted array, return new length.
**Intuition:** Use two pointers. Slow pointer tracks position for unique elements, fast pointer scans array.
**Logic:** When element changes, place it at slow pointer position.

**Java:**

```java
int removeDuplicates(int[] arr) {
    int i = 0;
    for (int j = 1; j < arr.length; j++) {
        if (arr[j] != arr[i]) {
            i++;
            arr[i] = arr[j];
        }
    }
    return i + 1;
}
```

**Time Complexity:** O(n) — Each element checked at most twice.
**Space Complexity:** O(1) — In-place, no extra array needed.

---

## L5: Left Rotate Array by One Place

**Question:** Rotate array left by one position.
**Intuition:** Save first element, shift all elements left by 1, place first element at end.
**Logic:** Temporary variable + shifting.

**Java:**

```java
void leftRotateByOne(int[] arr) {
    int temp = arr[0];
    for (int i = 1; i < arr.length; i++) {
        arr[i - 1] = arr[i];
    }
    arr[arr.length - 1] = temp;
}
```

**Time Complexity:** O(n) — Each element shifted once.
**Space Complexity:** O(1) — Uses only a temp variable.

---

## L6: Left Rotate Array by D Places

**Question:** Rotate array left by D positions.
**Intuition:** Reverse entire array, then reverse first n-d elements, then reverse last d elements. Or use temp array for first d elements.
**Logic:** Reversal algorithm or temporary array.

**Java:**

```java
// Optimal: Reversal algorithm
void leftRotate(int[] arr, int d) {
    int n = arr.length;
    d = d % n;
    reverse(arr, 0, d - 1);
    reverse(arr, d, n - 1);
    reverse(arr, 0, n - 1);
}

void reverse(int[] arr, int start, int end) {
    while (start < end) {
        int temp = arr[start];
        arr[start] = arr[end];
        arr[end] = temp;
        start++;
        end--;
    }
}
```

**Time Complexity:** O(n) — Each element swapped a constant number of times.
**Space Complexity:** O(1) — All done in-place.

---

## L7: Move Zeros to End

**Question:** Move all zeros to end while maintaining relative order of non-zeros.
**Intuition:** Two pointers - one for non-zero position, one for scanning. Place non-zeros first, zeros will automatically be at end.
**Logic:** Swap non-zeros to front using two pointers.

**Java:**

```java
void moveZeros(int[] arr) {
    int j = 0; // position for next non-zero

    // Move all non-zeros to front
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] != 0) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            j++;
        }
    }
}
```

**Time Complexity:** O(n) — Every element read once, some written twice.
**Space Complexity:** O(1) — Swapping in-place.

---

## L8: Linear Search

**Question:** Find element in array, return index or -1.
**Intuition:** Check each element sequentially until found.
**Logic:** Simple iteration.

**Java:**

```java
int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) {
            return i;
        }
    }
    return -1;
}
```

**Time Complexity:** O(n) — Worst case must scan all elements.
**Space Complexity:** O(1).

---

## L9: Union of Two Sorted Arrays

**Question:** Find union of two sorted arrays (unique elements in sorted order).
**Intuition:** Use two pointers, pick smaller element each time, skip duplicates.
**Logic:** Two pointer merging with duplicate handling.

**Java:**

```java
ArrayList<Integer> findUnion(int[] arr1, int[] arr2) {
    ArrayList<Integer> union = new ArrayList<>();
    int i = 0, j = 0;

    while (i < arr1.length && j < arr2.length) {
        if (arr1[i] <= arr2[j]) {
            if (union.isEmpty() || union.get(union.size() - 1) != arr1[i]) {
                union.add(arr1[i]);
            }
            i++;
        } else {
            if (union.isEmpty() || union.get(union.size() - 1) != arr2[j]) {
                union.add(arr2[j]);
            }
            j++;
        }
    }

    while (i < arr1.length) {
        if (union.get(union.size() - 1) != arr1[i]) {
            union.add(arr1[i]);
        }
        i++;
    }

    while (j < arr2.length) {
        if (union.get(union.size() - 1) != arr2[j]) {
            union.add(arr2[j]);
        }
        j++;
    }

    return union;
}
```

**Time Complexity:** O(m+n) — Each element of both arrays processed at most once.
**Space Complexity:** O(m+n) for output, but in practice, only unique elements (≤m+n).

---

## L10: Intersection of Two Sorted Arrays

**Question:** Find intersection (common elements).
**Intuition:** Two pointers - when elements match, add to result. Move pointer of smaller element.
**Logic:** Two pointer comparison.

**Java:**

```java
ArrayList<Integer> findIntersection(int[] arr1, int[] arr2) {
    ArrayList<Integer> intersection = new ArrayList<>();
    int i = 0, j = 0;

    while (i < arr1.length && j < arr2.length) {
        if (arr1[i] < arr2[j]) {
            i++;
        } else if (arr1[i] > arr2[j]) {
            j++;
        } else {
            intersection.add(arr1[i]);
            i++;
            j++;
        }
    }
    return intersection;
}
```

**Time Complexity:** O(m+n) — Each element in both arrays scanned at most once.
**Space Complexity:** O(min(m, n)) — Only stores common elements.

---

## L11: Missing Number in Array

**Question:** Array contains n-1 distinct numbers from 0 to n. Find missing number.
**Intuition:** Use sum formula or XOR. Sum of 0 to n minus sum of array = missing number.
**Logic:** Math approach: sum = n*(n+1)/2 - arraySum.

**Java:**

```java
int missingNumber(int[] arr, int n) {
    int expectedSum = n * (n + 1) / 2;
    int actualSum = 0;
    for (int num : arr) {
        actualSum += num;
    }
    return expectedSum - actualSum;
}

// XOR approach (avoids overflow)
int missingNumberXOR(int[] arr, int n) {
    int xor1 = 0, xor2 = 0;
    for (int i = 0; i < n - 1; i++) {
        xor1 ^= arr[i];
        xor2 ^= (i + 1);
    }
    xor2 ^= n;
    return xor1 ^ xor2;
}
```

**Time Complexity:** O(n) — One pass through the array.
**Space Complexity:** O(1) — Just sum/xor variables.

---

## L12: Maximum Consecutive Ones

**Question:** Find maximum number of consecutive 1s in binary array.
**Intuition:** Count consecutive 1s, reset on 0, track maximum.
**Logic:** Single pass with counter.

**Java:**

```java
int findMaxConsecutiveOnes(int[] arr) {
    int maxCount = 0, currentCount = 0;

    for (int num : arr) {
        if (num == 1) {
            currentCount++;
            maxCount = Math.max(maxCount, currentCount);
        } else {
            currentCount = 0;
        }
    }
    return maxCount;
}
```

**Time Complexity:** O(n) — Each element checked once.
**Space Complexity:** O(1).

---

## L13: Find Number that Appears Once

**Question:** Every element appears twice except one. Find that element.
**Intuition:** XOR all elements. Duplicates cancel out (a^a=0), leaving the unique element.
**Logic:** XOR property: a^0=a, a^a=0.

**Java:**

```java
int findSingle(int[] arr) {
    int xor = 0;
    for (int num : arr) {
        xor ^= num;
    }
    return xor;
}
```

**Time Complexity:** O(n) — One scan through array.
**Space Complexity:** O(1).

---

## L14: Longest Subarray with Sum K (Positives Only)

**Question:** Find length of longest subarray with sum equal to K (array has only positives).
**Intuition:** Use two pointers/sliding window. Expand window when sum < K, shrink when sum > K.
**Logic:** Two pointer approach works because all elements are positive (sum only increases when expanding).

**Java:**

```java
int longestSubarrayWithSumK(int[] arr, int k) {
    int left = 0, right = 0;
    int sum = 0, maxLen = 0;

    while (right < arr.length) {
        sum += arr[right];

        while (sum > k && left <= right) {
            sum -= arr[left];
            left++;
        }

        if (sum == k) {
            maxLen = Math.max(maxLen, right - left + 1);
        }
        right++;
    }
    return maxLen;
}
```

**Time Complexity:** O(n) — Each index visited at most twice: right expands, left contracts.
**Space Complexity:** O(1).

---

## L15: 2 Sum Problem

**Question:** Find if there exist two numbers that add up to target.
**Intuition:** Use HashMap to store complements. For each element, check if (target - element) exists in map.
**Logic:** Single pass with HashMap for O(n) time.

**Java:**

```java
boolean twoSum(int[] arr, int target) {
    HashSet<Integer> set = new HashSet<>();

    for (int num : arr) {
        if (set.contains(target - num)) {
            return true;
        }
        set.add(num);
    }
    return false;
}

// Return indices
int[] twoSumIndices(int[] arr, int target) {
    HashMap<Integer, Integer> map = new HashMap<>();

    for (int i = 0; i < arr.length; i++) {
        int complement = target - arr[i];
        if (map.containsKey(complement)) {
            return new int[]{map.get(complement), i};
        }
        map.put(arr[i], i);
    }
    return new int[]{-1, -1};
}
```

**Time Complexity:** O(n) — Each lookup and insert in hash map is O(1) avg; n elements.
**Space Complexity:** O(n) — For the hash set/map in worst case (all elements unique).

---

## L16: Sort Array of 0s, 1s, and 2s (Dutch National Flag)

**Question:** Sort array containing only 0s, 1s, and 2s.
**Intuition:** Three pointers - low (0s boundary), mid (current), high (2s boundary). Place 0s at start, 2s at end, 1s in middle.
**Logic:** Dutch National Flag algorithm with three pointers.

**Java:**

```java
void sort012(int[] arr) {
    int low = 0, mid = 0, high = arr.length - 1;

    while (mid <= high) {
        if (arr[mid] == 0) {
            swap(arr, low, mid);
            low++;
            mid++;
        } else if (arr[mid] == 1) {
            mid++;
        } else { // arr[mid] == 2
            swap(arr, mid, high);
            high--;
        }
    }
}

void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
}
```

**Time Complexity:** O(n) — Each element looked at most once.
**Space Complexity:** O(1) — Done in-place.

---

## L17: Majority Element (>n/2 times)

**Question:** Find element appearing more than n/2 times.
**Intuition:** Moore's Voting Algorithm - cancel out different elements. Majority element survives cancellation.
**Logic:** Maintain candidate and count. When count=0, pick new candidate.

**Java:**

```java
int majorityElement(int[] arr) {
    int candidate = arr[0], count = 0;

    // Find candidate
    for (int num : arr) {
        if (count == 0) {
            candidate = num;
        }
        count += (num == candidate) ? 1 : -1;
    }

    // Verify (if not guaranteed to exist)
    count = 0;
    for (int num : arr) {
        if (num == candidate) count++;
    }
    return count > arr.length / 2 ? candidate : -1;
}
```

**Time Complexity:** O(n) — Single scan for candidate, optional verify.
**Space Complexity:** O(1) — Constant candidates/counters.

---

## L18: Maximum Subarray Sum (Kadane's Algorithm)

**Question:** Find maximum sum of contiguous subarray.
**Intuition:** At each position, decide: extend current subarray or start fresh. Keep track of best sum seen.
**Logic:** Kadane's Algorithm - maintain current sum, reset to 0 if negative.

**Java:**

```java
int maxSubarraySum(int[] arr) {
    int maxSum = arr[0];
    int currentSum = 0;

    for (int num : arr) {
        currentSum += num;
        maxSum = Math.max(maxSum, currentSum);

        if (currentSum < 0) {
            currentSum = 0;
        }
    }
    return maxSum;
}
```

**Time Complexity:** O(n) — Each element considered once.
**Space Complexity:** O(1).

---

## L19: Stock Buy and Sell

**Question:** Buy and sell stock once for maximum profit.
**Intuition:** Track minimum price seen so far. For each price, calculate profit if sold today.
**Logic:** Single pass tracking minimum.

**Java:**

```java
int maxProfit(int[] prices) {
    int minPrice = prices[0];
    int maxProfit = 0;

    for (int i = 1; i < prices.length; i++) {
        maxProfit = Math.max(maxProfit, prices[i] - minPrice);
        minPrice = Math.min(minPrice, prices[i]);
    }
    return maxProfit;
}
```

**Time Complexity:** O(n) — Each price considered once.
**Space Complexity:** O(1).

**Example Dry Run:**
Suppose `prices = [7, 1, 5, 3, 6, 4]`


| Day | Price | minPrice | maxProfit | Profit (if sold now) |
| --- | ----- | -------- | --------- | -------------------- |
| 0   | 7     | 7        | 0         | 0                    |
| 1   | 1     | 1        | 0         | (1-7) = -6           |
| 2   | 5     | 1        | 4         | (5-1) = 4            |
| 3   | 3     | 1        | 4         | (3-1) = 2            |
| 4   | 6     | 1        | 5         | (6-1) = 5            |
| 5   | 4     | 1        | 5         | (4-1) = 3            |

*Best profit: 5 (buy at 1, sell at 6).*

---

## L20: Rearrange Array Elements by Sign

**Question:** Rearrange positive and negative numbers alternately.
**Intuition:** Use two pointers for positive and negative positions. Place elements at alternate indices.
**Logic:** Two pointers tracking even/odd positions.

**Java:**

```java
int[] rearrangeBySign(int[] arr) {
    int[] result = new int[arr.length];
    int posIdx = 0, negIdx = 1;

    for (int num : arr) {
        if (num > 0) {
            result[posIdx] = num;
            posIdx += 2;
        } else {
            result[negIdx] = num;
            negIdx += 2;
        }
    }
    return result;
}
```

**Time Complexity:** O(n) — Each element placed once.
**Space Complexity:** O(n) — New array constructed.

---

## L21: Next Permutation

**Question:** Find next lexicographically greater permutation.
**Intuition:** From right, find first decreasing element (break-point). Swap it with next greater element from right. Reverse everything after break-point.
**Logic:** Three steps: find break-point, swap, reverse.

**Java:**

```java
void nextPermutation(int[] arr) {
    int n = arr.length;
    int breakPoint = -1;

    // Find break-point
    for (int i = n - 2; i >= 0; i--) {
        if (arr[i] < arr[i + 1]) {
            breakPoint = i;
            break;
        }
    }

    // If no break-point, reverse entire array
    if (breakPoint == -1) {
        reverse(arr, 0, n - 1);
        return;
    }

    // Find next greater element to swap
    for (int i = n - 1; i > breakPoint; i--) {
        if (arr[i] > arr[breakPoint]) {
            swap(arr, i, breakPoint);
            break;
        }
    }

    // Reverse after break-point
    reverse(arr, breakPoint + 1, n - 1);
}
```

**Time Complexity:** O(n) — At most, 3 full passes (break-point search, swap search, reverse).
**Space Complexity:** O(1) — Sorting/reversal is done in-place.

**Example Dry Run & Diagram:**
Suppose `arr = [1, 3, 5, 4, 2]`

Steps:

1. **Find break-point:**
   Traverse from right: 5 > 4 > 2 (decreasing), but 3 < 5 at index 1. So, breakPoint = 1.

   ```
   [1, 3, 5, 4, 2]
      ^ breakPoint
   ```
2. **Find next greater element to swap with arr[1]:**
   Search right-to-left: first element > 3 is 4 at index 3.

   ```
   [1, 3, 5, 4, 2]
         ^    ^  
       break  swap with 4
   ```
3. **Swap arr[1] and arr[3]:**

   ```
   Before swap: [1, 3, 5, 4, 2]
   After swap:  [1, 4, 5, 3, 2]
   ```
4. **Reverse after break-point (indices 2 to 4):**
   Subarray to reverse: [5, 3, 2] → [2, 3, 5]
   Final result: [1, 4, 2, 3, 5]

   ```
   [1, 4, | 5, 3, 2] -> [1, 4, | 2, 3, 5]
          ^  reverse after break-point ^
   ```

**Illustrative Diagrams:**

```
Initial     : [1, 3, 5, 4, 2]
Break-point : [1, 3*, 5, 4, 2]
                 ^
Find swap   :      [1, 4*, 5, 3, 2]
                      ^ (swap with 3)
After swap  : [1, 4, 5, 3, 2]
Reverse tail: [1, 4, 2, 3, 5]
```

Another quick example:Input: [1, 2, 3]After steps:

- Break-point = 1 (arr[1]=2).
- Swap with arr[2]=3.
- Reverse arr[2:] (only one element).
  Output: [1, 3, 2]

---

## L22: Leaders in an Array

**Question:** Find all leaders. Element is leader if all elements to its right are smaller.
**Intuition:** Traverse from right to left. Track maximum seen so far. Current element is leader if ≥ max from right.
**Logic:** Right to left traversal.

**Java:**

```java
ArrayList<Integer> findLeaders(int[] arr) {
    ArrayList<Integer> leaders = new ArrayList<>();
    int n = arr.length;
    int maxFromRight = arr[n - 1];
    leaders.add(maxFromRight);

    for (int i = n - 2; i >= 0; i--) {
        if (arr[i] >= maxFromRight) {
            leaders.add(arr[i]);
            maxFromRight = arr[i];
        }
    }

    Collections.reverse(leaders);
    return leaders;
}
```

**Time Complexity:** O(n) — Single pass needed.
**Space Complexity:** O(1) for just returning count; O(k) for list of leaders (if returning list).

---

## L23: Longest Consecutive Sequence

**Question:** Find length of longest consecutive sequence.
**Intuition:** Use HashSet. For each number, check if it's start of sequence (num-1 not in set). Count consecutive numbers.
**Logic:** HashSet for O(1) lookups, smart sequence start detection.

**Java:**

```java
int longestConsecutive(int[] arr) {
    HashSet<Integer> set = new HashSet<>();
    for (int num : arr) {
        set.add(num);
    }

    int maxLength = 0;

    for (int num : set) {
        // Check if start of sequence
        if (!set.contains(num - 1)) {
            int currentNum = num;
            int currentLength = 1;

            while (set.contains(currentNum + 1)) {
                currentNum++;
                currentLength++;
            }

            maxLength = Math.max(maxLength, currentLength);
        }
    }
    return maxLength;
}
```

**Time Complexity:** O(n) — In practice, all insert/lookups/while are proportional to n.
**Space Complexity:** O(n) — For the hash set.

---

## L24: Set Matrix Zeroes

**Question:** If element is 0, set its entire row and column to 0.
**Intuition:** Use first row and first column as markers. Process matrix, mark first row/col, then set zeros.
**Logic:** In-place marking using first row/column.

**Java:**

```java
void setZeroes(int[][] matrix) {
    int m = matrix.length, n = matrix[0].length;
    boolean firstRowZero = false, firstColZero = false;

    // Check if first row/col has zero
    for (int j = 0; j < n; j++) {
        if (matrix[0][j] == 0) firstRowZero = true;
    }
    for (int i = 0; i < m; i++) {
        if (matrix[i][0] == 0) firstColZero = true;
    }

    // Use first row/col as markers
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            if (matrix[i][j] == 0) {
                matrix[i][0] = 0;
                matrix[0][j] = 0;
            }
        }
    }

    // Set zeros based on markers
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                matrix[i][j] = 0;
            }
        }
    }

    // Handle first row/col
    if (firstRowZero) {
        for (int j = 0; j < n; j++) matrix[0][j] = 0;
    }
    if (firstColZero) {
        for (int i = 0; i < m; i++) matrix[i][0] = 0;
    }
}
```

**Time Complexity:** O(mn) — Each cell read up to 3 times.
**Space Complexity:** O(1) — In-place via marker row/col; O(m+n) if using extra arrays.

**Step-by-step Example (Intuitive Dry Run):**
Suppose

```
Input matrix:
[
 [1, 2, 3],
 [4, 0, 6],
 [7, 8, 9]
]
```

**Step 1: Mark first row/col if they have any zero.**

- FirstRowZero = false (row 0 has no zero)
- FirstColZero = false (col 0 has no zero)

**Step 2: Mark which rows and columns need to be zeroed using first row/column as markers.**
Search from index (1,1): Only matrix[m-1][n-1]==0 (middle element).
So mark matrix[1][0]=0 (marks row 1 to be zeroed), matrix[0][1]=0 (marks col 1 to be zeroed).
Now matrix =

```
[
 [1, 0, 3],
 [0, 0, 6],
 [7, 8, 9]
]
```

*Yellow cells are markers; they signal the "zero" operation!*

**Step 3: Use markers to set others to zero.**For all (i,j) from (1,1):

- (1,1): already 0.
- (1,2): since matrix[1][0] == 0, set to 0.
- (2,1): since matrix[0][1] == 0, set to 0.

Matrix now:

```
[
 [1, 0, 3],
 [0, 0, 0],
 [7, 0, 9]
]
```

**Step 4: Handle first row and first column.**

- FirstRowZero was false, don't zero first row.
- FirstColZero was false, don't zero first col.

**Final Result:**

```
[
 [1, 0, 3],
 [0, 0, 0],
 [7, 0, 9]
]
```

**Visualization:**

```
Markers Stage:         After Zeroing:         Final:
[1, 0, 3]              [1, 0, 3]             [1, 0, 3]
[0, 0, 6]     --->     [0, 0, 0]      --->   [0, 0, 0]
[7, 8, 9]              [7, 0, 9]             [7, 0, 9]
(Markers in bold)
```

Wherever the first row or column has a 0 after marking, the entire corresponding row or column is zeroed—no need for extra space!

---

## L25: Rotate Matrix 90 Degrees

**Question:** Rotate matrix 90 degrees clockwise in-place.
**Intuition:** Transpose matrix, then reverse each row. This achieves 90-degree clockwise rotation.
**Logic:** Two steps: transpose + reverse rows.

**Java:**

```java
void rotate(int[][] matrix) {
    int n = matrix.length;

    // Transpose
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            int temp = matrix[i][j];
            matrix[i][j] = matrix[j][i];
            matrix[j][i] = temp;
        }
    }

    // Reverse each row
    for (int i = 0; i < n; i++) {
        reverse(matrix[i]);
    }
}

void reverse(int[] row) {
    int left = 0, right = row.length - 1;
    while (left < right) {
        int temp = row[left];
        row[left] = row[right];
        row[right] = temp;
        left++;
        right--;
    }
}
```

**Time Complexity:** O(n^2) — Every cell visited twice (transpose + row reverse).
**Space Complexity:** O(1) — Done in-place.

---

## L26: Print Matrix in Spiral Order

**Question:** Print matrix elements in spiral order.
**Intuition:** Use four boundaries (top, bottom, left, right). Print and shrink boundaries.
**Logic:** Four directions: right, down, left, up. Adjust boundaries after each direction.

**Java:**

```java
List<Integer> spiralOrder(int[][] matrix) {
    List<Integer> result = new ArrayList<>();
    if (matrix.length == 0) return result;

    int top = 0, bottom = matrix.length - 1;
    int left = 0, right = matrix[0].length - 1;

    while (top <= bottom && left <= right) {
        // Right
        for (int j = left; j <= right; j++) {
            result.add(matrix[top][j]);
        }
        top++;

        // Down
        for (int i = top; i <= bottom; i++) {
            result.add(matrix[i][right]);
        }
        right--;

        // Left (check if row exists)
        if (top <= bottom) {
            for (int j = right; j >= left; j--) {
                result.add(matrix[bottom][j]);
            }
            bottom--;
        }

        // Up (check if column exists)
        if (left <= right) {
            for (int i = bottom; i >= top; i--) {
                result.add(matrix[i][left]);
            }
            left++;
        }
    }
    return result;
}
```

**Time Complexity:** O(mn) — Every cell visited exactly once.
**Space Complexity:** O(1) (ignoring output) — Output list is O(m*n) for result.

---

## L27: Count Subarrays with Given Sum

**Question:** Count subarrays with sum equal to K.
**Intuition:** Use prefix sum + HashMap. If (prefixSum - K) exists in map, we found subarrays.
**Logic:** Track prefix sums in HashMap.

**Java:**

```java
int subarraySum(int[] arr, int k) {
    HashMap<Integer, Integer> map = new HashMap<>();
    map.put(0, 1); // base case

    int count = 0, prefixSum = 0;

    for (int num : arr) {
        prefixSum += num;

        if (map.containsKey(prefixSum - k)) {
            count += map.get(prefixSum - k);
        }

        map.put(prefixSum, map.getOrDefault(prefixSum, 0) + 1);
    }
    return count;
}
```

**Time Complexity:** O(n) — Each element processed in constant time.
**Space Complexity:** O(n) — For prefix sum hash map.

---

## L28: Pascal's Triangle

**Question:** Generate Pascal's triangle up to n rows. Or find specific row/element.
**Intuition:** Each element = sum of two elements above it. Use combinatorics: C(n, r) = n! / (r! (n − r)!).

**Combinatorics Refresher:**

- **Combinations vs. Permutations:**
  - *Permutation* (ordered): Number of ways to arrange r items from n is nPr = n!/(n−r)!
  - *Combination* (unordered): Number of ways to **choose** r from n is nCr = n!/(r!(n−r)!)
- **What is n! (n factorial)?**
  - n! = n × (n−1) × (n−2) × ... × 1. The count of all possible ways to arrange n distinct items in a row.
- **Why does nCr = n!/(r!(n−r)!)?**
  - n! gives permutations (order matters). But for choosing without order, every selection of r can be arranged among themselves in r! ways (so, overcounted). And the (n−r)! corrects for the remaining unused items.
- **Pascal's Triangle and nCr:**
  - Each entry at (row n, col r) is exactly nCr (count of subsets of size r from n).
  - Each entry is also the sum of the two above it: nCr = (n−1)C(r−1) + (n−1)Cr.
- **Binomial Expansion:**
  - (a + b)^n = Σ (from r=0 to n) [nCr × a^{n−r} × b^{r}]. The coefficients are nCr—exactly the entries of the triangle!

**Logic:** Build iteratively or use formula.

**Java:**

```java
// Generate n rows
List<List<Integer>> generate(int numRows) {
    List<List<Integer>> triangle = new ArrayList<>();

    for (int i = 0; i < numRows; i++) {
        List<Integer> row = new ArrayList<>();
        for (int j = 0; j <= i; j++) {
            if (j == 0 || j == i) {
                row.add(1);
            } else {
                row.add(triangle.get(i-1).get(j-1) + triangle.get(i-1).get(j));
            }
        }
        triangle.add(row);
    }
    return triangle;
}

/*
for row - r and column c
we need to find to (r-1) C (c - 1)
*/
// Get specific element (row r, col c)
int getElement(int r, int c) {
    long result = 1;
    for (int i = 0; i < c; i++) {
        result = result * (r - i) / (i + 1);
    }
    return (int) result;
}
```

**Time Complexity:** O(n^2) to build triangle (must fill every triangle element).
**Space Complexity:** O(n^2) for the result list. For the combinatorics method: O(1).

---

## L29: 3 Sum Problem

**Question:** Find all unique triplets that sum to zero.
**Intuition:** Sort array. Fix one element, use two pointers for remaining two. Skip duplicates.
**Logic:** Sort + two pointer for each fixed element.

**Java:**

```java
List<List<Integer>> threeSum(int[] arr) {
    List<List<Integer>> result = new ArrayList<>();
    Arrays.sort(arr);

    for (int i = 0; i < arr.length - 2; i++) {
        // Skip duplicates for first element
        if (i > 0 && arr[i] == arr[i - 1]) continue;

        int left = i + 1, right = arr.length - 1;

        while (left < right) {
            int sum = arr[i] + arr[left] + arr[right];

            if (sum == 0) {
                result.add(Arrays.asList(arr[i], arr[left], arr[right]));

                // Skip duplicates
                while (left < right && arr[left] == arr[left + 1]) left++;
                while (left < right && arr[right] == arr[right - 1]) right--;

                left++;
                right--;
            } else if (sum < 0) {
                left++;
            } else {
                right--;
            }
        }
    }
    return result;
}
```

**Time Complexity:** O(n^2) — Outer loop O(n), inner two-pointer scan O(n) per unique i.
**Space Complexity:** O(k) — For storing triplets, where k is number of solutions.

---

## L30: 4 Sum Problem

**Question:** Find all unique quadruplets that sum to target.
**Intuition:** Similar to 3Sum but with two fixed elements. Sort + two loops + two pointers.
**Logic:** Fix two elements, use two pointers for remaining two.

**Java:**

```java
List<List<Integer>> fourSum(int[] arr, int target) {
    List<List<Integer>> result = new ArrayList<>();
    Arrays.sort(arr);
    int n = arr.length;

    for (int i = 0; i < n - 3; i++) {
        if (i > 0 && arr[i] == arr[i - 1]) continue;

        for (int j = i + 1; j < n - 2; j++) {
            if (j > i + 1 && arr[j] == arr[j - 1]) continue;

            int left = j + 1, right = n - 1;

            while (left < right) {
                long sum = (long)arr[i] + arr[j] + arr[left] + arr[right];

                if (sum == target) {
                    result.add(Arrays.asList(arr[i], arr[j], arr[left], arr[right]));

                    while (left < right && arr[left] == arr[left + 1]) left++;
                    while (left < right && arr[right] == arr[right - 1]) right--;

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
```

**Time Complexity:** O(n^3) — Double loop O(n^2), inner two-pointer O(n) per pair.
**Space Complexity:** O(k) — k is number of unique quadruplets.

---

## L31: Subarray with XOR K

**Question:** Count subarrays with XOR equal to K.
**Intuition:** Similar to prefix sum approach. Use prefix XOR + HashMap.
**Logic:** If (prefixXOR ^ K) exists in map, we found subarrays.

**Java:**

```java
int subarraysWithXorK(int[] arr, int k) {
    HashMap<Integer, Integer> map = new HashMap<>();
    map.put(0, 1);

    int count = 0, xor = 0;

    for (int num : arr) {
        xor ^= num;

        int target = xor ^ k;
        if (map.containsKey(target)) {
            count += map.get(target);
        }

        map.put(xor, map.getOrDefault(xor, 0) + 1);
    }
    return count;
}
```

**Time Complexity:** O(n) — Prefix XOR for each index, every lookup/update O(1) avg.
**Space Complexity:** O(n) — For hash map of prefix XORs.

---

## L32: Merge Overlapping Intervals

**Question:** Merge all overlapping intervals.
**Intuition:** Sort by start time. Merge if current interval overlaps with last merged interval.
**Logic:** Sort + linear merge.

**Java:**

```java
int[][] merge(int[][] intervals) {
    if (intervals.length <= 1) return intervals;

    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

    List<int[]> merged = new ArrayList<>();
    int[] currentInterval = intervals[0];
    merged.add(currentInterval);

    for (int[] interval : intervals) {
        int currentEnd = currentInterval[1];
        int nextStart = interval[0];
        int nextEnd = interval[1];

        if (currentEnd >= nextStart) {
            // Overlapping - merge
            currentInterval[1] = Math.max(currentEnd, nextEnd);
        } else {
            // Non-overlapping - add new interval
            currentInterval = interval;
            merged.add(currentInterval);
        }
    }

    return merged.toArray(new int[merged.size()][]);
}
```

**Time Complexity:** O(n log n) — Sorting is dominant (n intervals).
**Space Complexity:** O(n) — For output list.

---

## L33: Merge Two Sorted Arrays Without Extra Space

**Question:** Merge two sorted arrays in-place.
**Intuition:** Use gap method (shell sort idea). Start with gap = (m+n)/2, compare elements gap distance apart, reduce gap.

Another logic:Use two pointers, one starting from the end of arr1 (right) and one from the start of arr2 (left).

- At each step, compare arr1[i] (largest of arr1) with arr2[j] (smallest of arr2).
- If arr1[i] > arr2[j], swap them (put smaller value in arr1, larger in arr2).
- Move i one step left and j one step right.
- Stop when arr1[i] <= arr2[j] (all elements on left are less than or equal to all elements on right).
- Finally, sort both arrays individually.
- Intuition: This gets elements into the correct array before sorting, making both sorted arrays contain only their rightful values.

**Java (Right-to-left/Left-to-right Place-and-Sort):**

```java
void mergePlaceToCorrectArrayAndSort(int[] arr1, int[] arr2) {
    int m = arr1.length, n = arr2.length;
    int i = m - 1;
    int j = 0;
    // Move elements to correct array
    while (i >= 0 && j < n && arr1[i] > arr2[j]) {
        // Swap the largest on left with smallest on right
        int temp = arr1[i];
        arr1[i] = arr2[j];
        arr2[j] = temp;
        i--;
        j++;
    }
    // Now sort both arrays
    Arrays.sort(arr1);
    Arrays.sort(arr2);
}
```

**Time Complexity:** O(min(m, n)) for the swaps + O(m log m + n log n) for the separate sorts.
**Space Complexity:** O(1) — All work is in-place; only swaps done.

**Logic:** Gap method for O(1) space.

**Java:**

```java
void merge(int[] arr1, int[] arr2) {
    int m = arr1.length, n = arr2.length;
    int gap = (m + n + 1) / 2;

    while (gap > 0) {
        int i = 0, j = gap;

        while (j < m + n) {
            // Both in arr1
            if (j < m) {
                if (arr1[i] > arr1[j]) {
                    swap(arr1, i, j);
                }
            }
            // i in arr1, j in arr2
            else if (i < m) {
                if (arr1[i] > arr2[j - m]) {
                    int temp = arr1[i];
                    arr1[i] = arr2[j - m];
                    arr2[j - m] = temp;
                }
            }
            // Both in arr2
            else {
                if (arr2[i - m] > arr2[j - m]) {
                    int temp = arr2[i - m];
                    arr2[i - m] = arr2[j - m];
                    arr2[j - m] = temp;
                }
            }
            i++;
            j++;
        }

        if (gap == 1) break;
        gap = (gap + 1) / 2;
    }
}
```

**Time Complexity:** O((m+n) log(m+n)) — Logarithmic number of gap reductions, each pass O(m+n)
**Space Complexity:** O(1) — In-place swap; no extra buffer.

---

**Comparison: Gap Method vs. Place-Sort Method**


| Aspect          | Gap Method (Shell-sort style)                                                                                                                | Two-pointer Place-&-Sort                                                                                                                 |
| --------------- | -------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| Steps           | Repeatedly compare elements`gap` apart in the two arrays and swap if needed. Reduce `gap` and repeat until gap = 1. Final arrays are sorted. | Swap largest from end of arr1 with smallest from start of arr2 until arr1[i] <= arr2[j]. Then**sort both arrays** individually.          |
| Time Complexity | O((m+n) * log(m+n))                                                                                                                          | O(min(m, n)) for swaps + O(m log m + n log n) for sorting                                                                                |
| Best for        | Space-optimized in-place merging, minimizes sorts; efficient for large datasets if sorting is costly                                         | Simpler to code and easy to think about; quick for small/mid arrays but sorting both arrays at the end can be expensive for large arrays |
| Space           | O(1)                                                                                                                                         | O(1)                                                                                                                                     |
| Swaps/Shifts    | Fewer, more spread out (due to gap)                                                                                                          | Can have many swaps and the sorting step dominates                                                                                       |
| Interview value | Elegant, more optimal                                                                                                                        | Very intuitive—great for whiteboard, clear logic                                                                                        |
| Stable          | No                                                                                                                                           | No                                                                                                                                       |

*Summary: Gap method is usually better for large inputs if you want to avoid sorting at the end, but is a little more complex to code. The place-&-sort approach is easy for interviews and small cases, but the final sort dominates for large arrays.*

---

## L34: Find Missing and Repeating Numbers

**Question:** One number repeats, one is missing. Find both.
**Intuition:** Use math or XOR. With math: use sum and sum of squares equations.
**Logic:** Two equations with two unknowns.

**Java:**

```java
int[] findMissingRepeating(int[] arr) {
    int n = arr.length;
    long SN = (long)n * (n + 1) / 2;
    long SN2 = (long)n * (n + 1) * (2 * n + 1) / 6;

    long S = 0, S2 = 0;
    for (int num : arr) {
        S += num;
        S2 += (long)num * num;
    }

    long val1 = S - SN; // x - y
    long val2 = S2 - SN2; // x^2 - y^2
    val2 = val2 / val1; // x + y

    long x = (val1 + val2) / 2; // repeating
    long y = val2 - x; // missing

    return new int[]{(int)x, (int)y};
}
```

**Time Complexity:** O(n) — Single scan for sum and sum^2.
**Space Complexity:** O(1).

---

## L35: Count Inversions

**Question:** Count pairs (i,j) where i<j and arr[i]>arr[j].
**Intuition:** Use merge sort. While merging, count inversions.
**Logic:** Modified merge sort.

**Java:**

```java
int countInversions(int[] arr) {
    return mergeSort(arr, 0, arr.length - 1);
}

int mergeSort(int[] arr, int left, int right) {
    int count = 0;
    if (left < right) {
        int mid = left + (right - left) / 2;
        count += mergeSort(arr, left, mid);
        count += mergeSort(arr, mid + 1, right);
        count += merge(arr, left, mid, right);
    }
    return count;
}

int merge(int[] arr, int left, int mid, int right) {
    int[] temp = new int[right - left + 1];
    int i = left, j = mid + 1, k = 0;
    int count = 0;

    while (i <= mid && j <= right) {
        if (arr[i] <= arr[j]) {
            temp[k++] = arr[i++];
        } else {
            temp[k++] = arr[j++];
            count += (mid - i + 1); // inversion count
        }
    }

    while (i <= mid) temp[k++] = arr[i++];
    while (j <= right) temp[k++] = arr[j++];

    for (i = left, k = 0; i <= right; i++, k++) {
        arr[i] = temp[k];
    }

    return count;
}
```

**Time Complexity:** O(n log n) — Standard merge sort with extra work during merge.
**Space Complexity:** O(n) — Temporary array used in merge.

---

## L36: Reverse Pairs

**Question:** Count pairs (i,j) where i<j and arr[i]>2*arr[j].
**Intuition:** Similar to count inversions but with modified condition. Use merge sort with separate counting logic.
**Logic:** Modified merge sort with 2* condition check.

**Java:**

```java
int reversePairs(int[] arr) {
    return mergeSort(arr, 0, arr.length - 1);
}

int mergeSort(int[] arr, int left, int right) {
    if (left >= right) return 0;

    int mid = left + (right - left) / 2;
    int count = mergeSort(arr, left, mid);
    count += mergeSort(arr, mid + 1, right);
    count += countPairs(arr, left, mid, right);
    merge(arr, left, mid, right);

    return count;
}

int countPairs(int[] arr, int left, int mid, int right) {
    int count = 0;
    int j = mid + 1;

    for (int i = left; i <= mid; i++) {
        while (j <= right && arr[i] > 2L * arr[j]) {
            j++;
        }
        count += (j - (mid + 1));
    }
    return count;
}
```

**Time Complexity:** O(n log n) — Just like normal merge sort.
**Space Complexity:** O(n) — For temporary array during merge.

---

## L37: Maximum Product Subarray

**Question:** Find contiguous subarray with largest product.
**Intuition:** Track both max and min products (negative * negative = positive). At each element, update both.
**Logic:** Dynamic tracking of max and min products.

**Java:**

```java
int maxProduct(int[] arr) {
    if (arr.length == 0) return 0;

    int maxProd = arr[0];
    int currentMax = arr[0];
    int currentMin = arr[0];

    for (int i = 1; i < arr.length; i++) {
        if (arr[i] < 0) {
            int temp = currentMax;
            currentMax = currentMin;
            currentMin = temp;
        }

        currentMax = Math.max(arr[i], currentMax * arr[i]);
        currentMin = Math.min(currentMin, currentMax * arr[i]);

        maxProd = Math.max(maxProd, currentMax);
    }
    return maxProd;
}
```

**Time Complexity:** O(n) — One scan, constant time per element.
**Space Complexity:** O(1) — Constant variables tracked.

---

## More Advanced InterviewBit Array Problems

---

### IB6: Minimum Swaps for Increasing Sequence

**Question:** Given a binary array, find the minimum number of swaps required to bring all 1s together.

**The Core Intuition - Breaking It Down**

- **Step 1: The Final State Must Be a Contiguous Block**

  - If you have k ones in the array, the *final arrangement* must look like:
    ```
    ....[1][1][1][1]....
    ```
  - Those k ones will occupy exactly k consecutive positions.
- **Step 2: We Don't Know WHERE That Block Will Be**

  - Any k consecutive positions could be the window—at the start, middle, or end.
  - For example, with `[0][1][0][1][0][1][0][1]` (4 ones), valid blocks could occupy positions 0–3, 2–5, or 4–7, etc.
- **Step 3: The Sliding Window Reveals the Answer**

  - Slide a window of size k through the array, at each step count the number of zeros in the window.
  - The *minimum* zeros in any window is the answer—these are the swaps needed to "bubble in" the 1s.

**Java:**

```java
int minSwapsToGroupOnes(int[] arr) {
    int n = arr.length, ones = 0;
    for (int num : arr) if (num == 1) ones++;
    if (ones == 0) return 0;
    int minSwaps = n, zerosInWindow = 0;
    // Find zeros in first window
    for (int i = 0; i < ones; i++) if (arr[i] == 0) zerosInWindow++;
    minSwaps = zerosInWindow;
    for (int i = ones; i < n; i++) {
        if (arr[i - ones] == 0) zerosInWindow--;
        if (arr[i] == 0) zerosInWindow++;
        minSwaps = Math.min(minSwaps, zerosInWindow);
    }
    return minSwaps;
}
```

**Time Complexity:** O(n)
**Space Complexity:** O(1)

---

### IB6b: Minimum Adjacent Swaps for Gathering All 1s

**Question:** Given a binary array, find the minimum number of *adjacent* swaps required to bring all 1s together.
You can only swap neighboring elements.

**Intuition:**

- With adjacent swaps only, think bubble sort: Each 1 must "bubble through" any zeros in its way.
- For each 1, count how many zeros it must cross on its way to the block of 1s.
- This is equivalent to counting inversions between 1s and 0s.

**Java:**

```java
public int minAdjacentSwaps(int[] arr) {
    // Calculate swaps to move all 1s to left
    int swapsLeft = 0;
    int zeroCount = 0;
  
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == 0) {
            zeroCount++;
        } else {
            swapsLeft += zeroCount;
        }
    }
  
    // Calculate swaps to move all 1s to right
    int swapsRight = 0;
    zeroCount = 0;
  
    for (int i = arr.length - 1; i >= 0; i--) {
        if (arr[i] == 0) {
            zeroCount++;
        } else {
            swapsRight += zeroCount;
        }
    }
  
    return Math.min(swapsLeft, swapsRight);
}
```

**Time Complexity:** O(n)
**Space Complexity:** O(1)

---

### IB7: Largest Number Arrangement

**Question:** Given a list of non-negative integers, arrange them such that they form the largest number.
**Intuition:** Sort numbers as strings based on which concatenation gives larger result.
**Java:**

```java
String largestNumber(int[] nums) {
    String[] arr = Arrays.stream(nums).mapToObj(String::valueOf).toArray(String[]::new);
    Arrays.sort(arr, (a, b) -> (b + a).compareTo(a + b));
    if (arr[0].equals("0")) return "0";
    StringBuilder sb = new StringBuilder();
    for (String num : arr) sb.append(num);
    return sb.toString();
}
```

**Time Complexity:** O(n log n) (for custom string sort)
**Space Complexity:** O(n) (for string array and result)

---

### IB8: Flip Array (Minimum no. to flip to get sum/2)

**Question:** Given positive integers, flip signs so the sum is as close as possible to 0 (minimize number of flips). Return minimum flips.
**Intuition:** Classic DP for partitioning into two sums; subset sum DP with min flips.
**Java (pseudo):**

```java
int minFlipsForZeroOrNear(int[] arr) {
    int total = 0;
    for (int a : arr) total += a;
    int n = arr.length;
    int S = total / 2;
    int[][] dp = new int[n + 1][S + 1];
    Arrays.fill(dp[0], Integer.MAX_VALUE / 2);
    dp[0][0] = 0;
    for (int i = 1; i <= n; i++) {
        for (int j = 0; j <= S; j++) {
            dp[i][j] = dp[i - 1][j];
            if (j >= arr[i-1] && dp[i - 1][j - arr[i-1]] + 1 < dp[i][j])
                dp[i][j] = dp[i - 1][j - arr[i-1]] + 1;
        }
    }
    for (int j = S; j >= 0; j--)
        if (dp[n][j] != Integer.MAX_VALUE / 2)
            return dp[n][j];
    return -1;
}
```

**Time Complexity:** O(n * S) (pseudo-polynomial, S ~ total/2)
**Space Complexity:** O(n * S)

---

### IB9: Spiral Order Matrix Retrieval II (General Rectangular)

**Question:** Given an m x n matrix, print elements in spiral order.
**Intuition:** Like previous spiral, but handles rectangular matrices.
**Java:**

```java
List<Integer> spiralOrder(int[][] matrix) {
    List<Integer> res = new ArrayList<>();
    int m = matrix.length, n = matrix[0].length;
    int top = 0, bottom = m - 1, left = 0, right = n - 1;
    while (top <= bottom && left <= right) {
        for (int i = left; i <= right; i++) res.add(matrix[top][i]);
        top++;
        for (int i = top; i <= bottom; i++) res.add(matrix[i][right]);
        right--;
        if (top <= bottom) {
            for (int i = right; i >= left; i--) res.add(matrix[bottom][i]);
            bottom--;
        }
        if (left <= right) {
            for (int i = bottom; i >= top; i--) res.add(matrix[i][left]);
            left++;
        }
    }
    return res;
}
```

**Time Complexity:** O(mn)
**Space Complexity:** O(mn) (for result list)

---

### IB10: Noble Integer

**Question:** Given an array, find if there exists an integer p such that the number of elements greater than p is exactly p. Return 1 if exists, else -1.
**Intuition:** Sort, for each unique a[i], check if n - i - 1 == a[i].
**Java:**

```java
int nobleInteger(int[] arr) {
    Arrays.sort(arr);
    int n = arr.length;
    for (int i = 0; i < n; i++) {
        if (i < n-1 && arr[i] == arr[i+1]) continue;
        if (arr[i] == n - i - 1) return 1;
    }
    return -1;
}
```

**Time Complexity:** O(n log n)
**Space Complexity:** O(1)

---

### IB11: Maximum Sum Path in Two Arrays

**Question:** Given two sorted arrays, find the maximum sum path to traverse from the beginning of either array to the end, switching arrays only at common elements.

**Intuition (Expanded):**

- You may start in either array, move forward adding up the numbers you see, and at any *common value* (a number appearing at the same position in both arrays), you can "switch" to the other array for free.
- At every common element, you must decide if you want to keep going in your current array or switch and continue in the other—but you want the total sum to be as large as possible.

**Step-by-Step Example:**
Suppose:

```
arr1 = [2, 3, 7, 10, 12, 15, 30, 34]
arr2 = [1, 5, 7, 8, 10, 15, 16, 19]
```

1. **Visualizing choices:**
   - Both arrays are sorted, and common elements are 7, 10, and 15.
2. **Walkthrough:**
   - Start at beginning. Keep running two sums: sum1 (arr1) and sum2 (arr2).
   - Move forward in both arrays with two pointers.
   - Before first common (7):
     - arr1: 2+3 = 5
     - arr2: 1+5 = 6
   - At first common (7):
     - add 7 to both: arr1: 5+7=12, arr2: 6+7=13
     - Choose max of sum1 and sum2 so far (`max(12, 13) = 13`). This is the best path till here. Reset both sums to this value.
   - Continue:
     - arr1: from 7, next is 10
     - arr2: from 7, next is 8 (add to sum2): 13+8=21
     - arr1's sum: still 13 (nothing new until 10)
   - At next common (10):
     - arr1: 13+10=23
     - arr2: 21+10=31
     - best path so far: max(23, 31)=31. Update both sums to 31.
   - Continue:
     - arr1: 12 (31+12=43), 15 (43+15=58)
     - arr2: 15 (31+15=46), 16 (46+16=62), 19 (62+19=81)
   - At next common (15):
     - arr1: (previous sum at 12) 43, at 15: 43+15=58
     - arr2: at 15: 31+15=46
     - best path so far: max(58, 46)=58 (but notice array pointers, after this, continue down both)
   - From here, finish with the remaining elements; add whichever sum is highest at the end.

**Why does this work?**

- At each common element, you can "switch tracks" for the highest score so far, so at every intersection you reset your running sum as the max up to that point.
- Between intersections, you simply add numbers as they appear.

**Summary:**

- Walk both arrays in lockstep; whenever you hit a common number, pick the path you accumulated the largest sum on, and reset both running totals to that value.
- The answer is the largest sum possible by cleverly switching tracks only at values that are common to both arrays.

**Java:**

```java
int maxPathSum(int[] arr1, int[] arr2) {
    int i = 0, j = 0, sum1 = 0, sum2 = 0, result = 0;
    while (i < arr1.length && j < arr2.length) {
        if (arr1[i] < arr2[j])
            sum1 += arr1[i++];
        else if (arr1[i] > arr2[j])
            sum2 += arr2[j++];
        else {
            result += Math.max(sum1, sum2) + arr1[i];
            sum1 = 0;
            sum2 = 0;
            i++;
            j++;
        }
    }
    while (i < arr1.length)
        sum1 += arr1[i++];
    while (j < arr2.length)
        sum2 += arr2[j++];
    result += Math.max(sum1, sum2);
    return result;
}
```

**Time Complexity:** O(m + n)
**Space Complexity:** O(1)

---

### IB12: Maximum Absolute Difference

**Question:** Find max value of |A[i]-A[j]| + |i-j| for given array A.
**Intuition:** Consider all forms, realize max = max(A[i]+i) - min(A[i]+i) and max(A[i]-i) - min(A[i]-i).
**Java:**

```java
int maxArr(int[] A) {
    int max1 = Integer.MIN_VALUE, min1 = Integer.MAX_VALUE;
    int max2 = Integer.MIN_VALUE, min2 = Integer.MAX_VALUE;
    for (int i = 0; i < A.length; i++) {
        max1 = Math.max(max1, A[i] + i);
        min1 = Math.min(min1, A[i] + i);
        max2 = Math.max(max2, A[i] - i);
        min2 = Math.min(min2, A[i] - i);
    }
    return Math.max(max1 - min1, max2 - min2);
}
```

**Time Complexity:** O(n)
**Space Complexity:** O(1)

---

### IB13: Minimum Lights to Activate

**Question:** Given a binary array representing positions, and integer B, each light at i can light B-1 range on both sides. Find the minimum number of lights to activate to light the whole array.

**Intuition (with example):**

- Imagine you have a street of n houses (array A), where A[i] = 1 if you can place a light at position i, or 0 if not.
- Each light lights up from (i - B + 1) to (i + B - 1).
- Your goal is to place the fewest lights so every house gets lit — and you can only light from a position if A[pos] == 1.
- **Greedy idea:** Always place the rightmost possible light to cover the current "unlit" position, because it will cover the farthest right from your current spot, possibly lighting more houses with fewer lights.

**Step-by-step Example:**
Suppose A = [0, 1, 0, 1, 1, 0, 0, 1, 0], B = 2

Let's walk through:

- Start at i = 0. Earliest lit by any light is position 0 (must find a light from i = 0 up to min(i+B-1, n-1) = 1).
- See A[1] = 1, so you can light up [0, 2]. Place light at 1.
- Next i = 1 + B = 3 (need to cover next "unlit"). Look at lights in range i–B+1 to i+B–1 = [2,4]. Find rightmost possible (A[4]=1). Place light at 4 (covers 3,4,5).
- Next i = 4 + B = 6. Look at lights in [5,7]. A[7]=1, place light at 7 (covers 6,7,8).
- Now i = 7 + B = 9, which is ≥ n, so stop.
- We've used **3 lights** at positions 1, 4, and 7.

If you can't find a light to cover a range, it's impossible to cover all positions (return -1).

**Why greedy "rightmost light" works:**
Placing the light as far right as possible from the current coverage point means each new light adds the most unique coverage, so you never use more lights than needed.

**Java:**

```java
int minLights(int[] A, int B) {
    int count = 0, i = 0, n = A.length;
    while (i < n) {
        int pos = Math.min(i + B - 1, n - 1);
        while (pos >= i - B + 1 && (pos < 0 || A[pos] == 0))
            pos--;
        if (pos < i - B + 1 || pos < 0)
            return -1;
        count++;
        i = pos + B;
    }
    return count;
}
```

**Time Complexity:** O(n)
**Space Complexity:** O(1)

---

### IB14: Find Repeating Subarray

**Question:** Find the length of the longest subarray with at least two matching elements.
**Intuition:** Use HashMap to store the earliest index of occurrences, keeping the maximum distance.
**Java:**

```java
int longestRepeatingSubarray(int[] arr) {
    Map<Integer, Integer> map = new HashMap<>();
    int maxLen = 0;
    for (int i = 0; i < arr.length; i++) {
        if (map.containsKey(arr[i])) {
            maxLen = Math.max(maxLen, i - map.get(arr[i]));
        } else {
            map.put(arr[i], i);
        }
    }
    return maxLen;
}
```

**Time Complexity:** O(n)
**Space Complexity:** O(n)

---

### IB15: Kth Smallest Element in a Sorted Matrix

**Question:** Given an N x N matrix where each row and each column is sorted in increasing order, find the kth smallest element in the matrix.
**Intuition:** Use a min-heap to extract elements in order, or use binary search on range of values and count elements ≤ mid.
**Java (min-heap approach):**

```java
int kthSmallest(int[][] matrix, int k) {
    int n = matrix.length;
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> matrix[a[0]][a[1]]));
    for (int i = 0; i < Math.min(n, k); i++) minHeap.add(new int[]{i, 0});
    int res = 0;
    for (int i = 0; i < k; i++) {
        int[] curr = minHeap.poll();
        res = matrix[curr[0]][curr[1]];
        if (curr[1] + 1 < n) {
            minHeap.add(new int[]{curr[0], curr[1] + 1});
        }
    }
    return res;
}
```

**Time Complexity:** O(k log n)
**Space Complexity:** O(n)
**Java (binary search approach):**

```java
int kthSmallest(int[][] matrix, int k) {
    int n = matrix.length;
    int low = matrix[0][0];
    int high = matrix[n-1][n-1];
    
    while (low < high) {
        int mid = low + (high - low) / 2;
        int count = countLessOrEqual(matrix, mid);
        
        if (count < k) {
            low = mid + 1;
        } else {
            high = mid;
        }
    }
    return low;
}

private int countLessOrEqual(int[][] matrix, int target) {
    int n = matrix.length;
    int row = n - 1;
    int col = 0;
    int count = 0;
    
    // Start from bottom-left corner
    while (row >= 0 && col < n) {
        if (matrix[row][col] <= target) {
            count += row + 1;  // All elements in this column up to row
            col++;
        } else {
            row--;
        }
    }
    return count;
}
```
**Time Complexity:** O(n log(Max-Min)) - The while loop runs log(Range) times. The countLessOrEqual runs in O(N) per iteration.
**Space Complexity:** O(1)
