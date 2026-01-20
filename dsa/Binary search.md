## BS-1: Binary Search Introduction | Template

**Question:** Search for element X in sorted array. Return index or -1.
**Intuition:** In sorted array, we can eliminate half the search space by comparing with middle element. If target > mid, search right; if target < mid, search left.
**Logic:** Divide and conquer - repeatedly halve search space.

**Java:**

```java
// Iterative Binary Search (Preferred)
int binarySearch(int[] arr, int target) {
 int low = 0, high = arr.length - 1;

 while (low <= high) {
 int mid = low + (high - low) / 2; // Avoid overflow

 if (arr[mid] == target) {
 return mid;
 } else if (arr[mid] < target) {
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }

 return -1;
}

// Recursive Binary Search
int binarySearchRecursive(int[] arr, int low, int high, int target) {
 if (low > high) return -1;

 int mid = low + (high - low) / 2;

 if (arr[mid] == target) return mid;
 else if (arr[mid] < target) return binarySearchRecursive(arr, mid + 1, high, target);
 else return binarySearchRecursive(arr, low, mid - 1, target);
}

// Time: O(log n), Space: O(1) iterative, O(log n) recursive
```

**Important Notes:**

```java
// Overflow Issue: DON'T use (low + high) / 2
// If low = 2147483640, high = 2147483647
// low + high = overflow!
// USE: mid = low + (high - low) / 2

// When to use low <= high vs low < high?
// low <= high: When you want to check all elements including when low == high
// low < high: When you want to stop before last element (rare in basic BS)
```

---

## BS-2-5: Lower Bound/Upper Bound/Ceil/Floor

**Lower & Upper Bound: "Where to INSERT"**
Lower Bound = Where to INSERT target to keep it as LEFT as possible
Upper Bound = Where to INSERT target to keep it as RIGHT as possible

**Floor & Ceil: "What EXISTS nearby"**
Think of array as steps on a staircase
Floor = Step BELOW you (largest ≤ target)
Ceil = Step ABOVE you (smallest ≥ target)

**THE STAIRCASE VISUALIZATION**

```
Array: [1, 2, 3, 5, 7, 9]

Number line: 1 2 3 4 5 6 7 8 9
Array steps: ● ● ● ● ● ●
 ↑ ↑ ↑
 Floor YOU Ceil
 (5) (6) (7)
```

You're at position 6 (not in array):

- Floor(6) = 5 ← Largest step ≤ 6
- Ceil(6) = 7 ← Smallest step ≥ 6

**DEFINITIONS TABLE**


| Array: [1, 2, 3, 5, 7, 9] | | | |
| ------------------------- | ------------------------------- | ------------------ | ----------- |
| **Concept** | **Definition** | **Condition** | **Returns** |
| Lower Bound | First position arr[i] >= target | arr[mid] >= target | Index |
| Upper Bound | First position arr[i] > target | arr[mid] > target | Index |
| Floor | Largest value <= target | arr[mid] <= target | Value/Index |
| Ceil | Smallest value >= target | arr[mid] >= target | Value/Index |

**COMPLETE EXAMPLES**

**Target = 6 (Doesn't Exist)**

```
Array: [1, 2, 3, 5, 7, 9]
Index: 0 1 2 3 4 5

Lower Bound: 4 (index where 7 is, first >= 6)
Upper Bound: 4 (index where 7 is, first > 6)
Floor: 5 (value 5, largest <= 6)
Ceil: 7 (value 7, smallest >= 6)
```

**Target = 3 (Exists)**

```
Array: [1, 2, 3, 5, 7, 9]
Index: 0 1 2 3 4 5

Lower Bound: 2 (first occurrence of 3)
Upper Bound: 3 (position after 3)
Floor: 3 (standing on step 3)
Ceil: 3 (standing on step 3)
```

**Target = 0 (Smaller than All)**

```
Array: [1, 2, 3, 5, 7, 9]

Lower Bound: 0 (would insert at start)
Upper Bound: 0 (would insert at start)
Floor: -1 (no step below)
Ceil: 1 (first step above)
```

**Target = 10 (Larger than All)**

```
Array: [1, 2, 3, 5, 7, 9]

Lower Bound: 6 (arr.length, insert at end)
Upper Bound: 6 (arr.length, insert at end)
Floor: 9 (last step below)
Ceil: -1 (no step above)
```

**KEY RELATIONSHIPS**
When Target **EXISTS**:
Lower Bound = Ceil = First Occurrence
Floor = Last Occurrence
Upper Bound = Last Occurrence + 1

When Target **DOESN'T EXIST**:
Lower Bound = Upper Bound (same insertion point)
Ceil = arr[lowerBound]
Floor = arr[upperBound - 1] (if valid)

**CODE TEMPLATES**

**Lower Bound (First >= target)**

```java
int lowerBound(int[] arr, int target) {
 int lo = 0, hi = arr.length;

 while (lo < hi) {
 int mid = lo + (hi - lo) / 2;
 if (arr[mid] >= target) {
 hi = mid; // Found candidate, look left
 } else {
 lo = mid + 1; // Too small, go right
 }
 }
 return lo;
}
```

**Upper Bound (First > target)**

```java
int upperBound(int[] arr, int target) {
 int lo = 0, hi = arr.length;

 while (lo < hi) {
 int mid = lo + (hi - lo) / 2;
 if (arr[mid] > target) {
 hi = mid; // Found candidate, look left
 } else {
 lo = mid + 1; // Too small or equal, go right
 }
 }
 return lo;
}
```

The ONLY difference: >= vs >

**Floor (Largest <= target)**

```java
int floor(int[] arr, int target) {
 int lo = 0, hi = arr.length - 1;
 int result = -1; // No floor by default

 while (lo <= hi) {
 int mid = lo + (hi - lo) / 2;

 if (arr[mid] <= target) {
 result = arr[mid]; // Store candidate
 lo = mid + 1; // Look for larger floor
 } else {
 hi = mid - 1; // Too large, go left
 }
 }
 return result;
}
```

**Ceil (Smallest >= target)**

```java
int ceil(int[] arr, int target) {
 int lo = 0, hi = arr.length - 1;
 int result = -1; // No ceil by default

 while (lo <= hi) {
 int mid = lo + (hi - lo) / 2;

 if (arr[mid] >= target) {
 result = arr[mid]; // Store candidate
 hi = mid - 1; // Look for smaller ceil
 } else {
 lo = mid + 1; // Too small, go right
 }
 }
 return result;
}
```

**MEMORY TRICKS**
"L" for Lower/Left
Lower bound = as Left as possible

Uses >= (includes Less-or-equal boundary)

"U" for Upper/Ultimate
Upper bound = as far right as possible

Uses > (strictly greater, Ultra-right)

Floor = FLOOR Below
Stand on floor below you

Largest value ≤ target

Floor = Find largest Fewer-or-equal

Ceil = CEILING Above
Look at ceiling above you

Smallest value ≥ target

Ceil = Closest Ceiling (above)

## BS-6: First and Last Occurrence

**Question:** Find first and last position of target in sorted array.
**Intuition:** Two binary searches - one for first (lower bound), one for last.
**Logic:** First: find lower bound. Last: find upper bound - 1.

```java
class Solution {
 public int[] searchRange(int[] nums, int target) {
 // if exists -
 // first occurance is lower bound
 // last occurance is upper bound - 1

 // if doesnot -
 // lower bound either move it to nums.length index or the target will
 // not be there at that location

 int l = 0, r = nums.length - 1;
 int lb = r + 1;
 while(l <= r){
 int mid = l + (r - l)/2;
 if(nums[mid] >= target){
 lb = mid;
 r = mid - 1;
 }
 else l = mid + 1;
 }
 if(lb == nums.length || nums[lb] != target) {
 int[] res = {-1, -1};
 return res;
 }
 l = 0; r = nums.length - 1;
 int ub = r + 1;
 while(l <= r){
 int mid = l + (r - l)/2;
 if(nums[mid] > target){
 ub = mid ;
 r = mid - 1;
 }
 else l = mid + 1;
 }
 if(lb != ub) ub--;
 int[] res = {lb, ub};
 return res;
 }
}
```

**Java:**

```java
int[] searchRange(int[] arr, int target) {
 int first = findFirst(arr, target);

 if (first == -1) return new int[]{-1, -1};

 int last = findLast(arr, target);
 return new int[]{first, last};
}

int findFirst(int[] arr, int target) {
 int low = 0, high = arr.length - 1;
 int first = -1;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 if (arr[mid] == target) {
 first = mid;
 high = mid - 1; // Continue searching left
 } else if (arr[mid] < target) {
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }

 return first;
}

int findLast(int[] arr, int target) {
 int low = 0, high = arr.length - 1;
 int last = -1;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 if (arr[mid] == target) {
 last = mid;
 low = mid + 1; // Continue searching right
 } else if (arr[mid] < target) {
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }

 return last;
}
// Time: O(log n), Space: O(1)
```

---

## BS-7: Count Occurrences

**Question:** Count number of times target appears in sorted array.
**Intuition:** Find first and last occurrence, count = last - first + 1.
**Logic:** Use first and last occurrence logic.

**Java:**

```java
int countOccurrences(int[] arr, int target) {
 int first = findFirst(arr, target);
 if (first == -1) return 0;

 int last = findLast(arr, target);
 return last - first + 1;
}
```

---

## BS-8: Search in Rotated Sorted Array I

**Question:** Search target in rotated sorted array (no duplicates).

> **"At least ONE half of the array will ALWAYS be properly sorted"**

**Example 1:**

<pre><strong>Input:</strong> nums = [4,5,6,7,0,1,2], target = 0
<strong>Output:</strong> 4
</pre>

**Algorithm Steps**

**Step 1: Find Which Half is Sorted**

<pre class="not-prose w-full rounded font-mono text-sm font-extralight"><div class="codeWrapper text-light selection:text-super selection:bg-super/10 my-md relative flex flex-col rounded font-mono text-sm font-normal bg-subtler"><div class="translate-y-xs -translate-x-xs bottom-xl mb-xl flex h-0 items-start justify-end md:sticky md:top-[100px]"><div class="overflow-hidden rounded-full border-subtlest ring-subtlest divide-subtlest bg-base"><div class="border-subtlest ring-subtlest divide-subtlest bg-subtler"></div></div></div><div class="-mt-xl"><div><div data-testid="code-language-indicator" class="text-quiet bg-subtle py-xs px-sm inline-block rounded-br rounded-tl-[3px] font-thin">java</div></div><div><span><code><span class="token token">if</span><span> </span><span class="token token punctuation">(</span><span>nums</span><span class="token token punctuation">[</span><span>l</span><span class="token token punctuation">]</span><span> </span><span class="token token operator"><=</span><span> nums</span><span class="token token punctuation">[</span><span>mid</span><span class="token token punctuation">]</span><span class="token token punctuation">)</span><span> </span><span class="token token punctuation">{</span><span>
</span><span> </span><span class="token token">// Left half is sorted</span><span>
</span><span></span><span class="token token punctuation">}</span><span> </span><span class="token token">else</span><span> </span><span class="token token punctuation">{</span><span>
</span><span> </span><span class="token token">// Right half is sorted</span><span>
</span><span></span><span class="token token punctuation">}</span><span>
</span></code></span></div></div></div></pre>

**Why?** If `nums[l] <= nums[mid]`, the left side is in ascending order.

**Step 2: Check if Target is in Sorted Half**

Once you know which half is sorted, check if target lies within its boundaries:

**If left half is sorted:**

<pre class="not-prose w-full rounded font-mono text-sm font-extralight"><div class="codeWrapper text-light selection:text-super selection:bg-super/10 my-md relative flex flex-col rounded font-mono text-sm font-normal bg-subtler"><div class="translate-y-xs -translate-x-xs bottom-xl mb-xl flex h-0 items-start justify-end md:sticky md:top-[100px]"><div class="overflow-hidden rounded-full border-subtlest ring-subtlest divide-subtlest bg-base"><div class="border-subtlest ring-subtlest divide-subtlest bg-subtler"></div></div></div><div class="-mt-xl"><div><div data-testid="code-language-indicator" class="text-quiet bg-subtle py-xs px-sm inline-block rounded-br rounded-tl-[3px] font-thin">java</div></div><div><span><code><span class="token token">if</span><span> </span><span class="token token punctuation">(</span><span>nums</span><span class="token token punctuation">[</span><span>l</span><span class="token token punctuation">]</span><span> </span><span class="token token operator"><=</span><span> target </span><span class="token token operator">&&</span><span> target </span><span class="token token operator"><</span><span> nums</span><span class="token token punctuation">[</span><span>mid</span><span class="token token punctuation">]</span><span class="token token punctuation">)</span><span> </span><span class="token token punctuation">{</span><span>
</span><span> </span><span class="token token">// Target is in left half</span><span>
</span><span> r </span><span class="token token operator">=</span><span> mid </span><span class="token token operator">-</span><span> </span><span class="token token">1</span><span class="token token punctuation">;</span><span>
</span><span></span><span class="token token punctuation">}</span><span> </span><span class="token token">else</span><span> </span><span class="token token punctuation">{</span><span>
</span><span> </span><span class="token token">// Target must be in right half</span><span>
</span><span> l </span><span class="token token operator">=</span><span> mid </span><span class="token token operator">+</span><span> </span><span class="token token">1</span><span class="token token punctuation">;</span><span>
</span><span></span><span class="token token punctuation">}</span><span>
</span></code></span></div></div></div></pre>

**If right half is sorted:**

<pre class="not-prose w-full rounded font-mono text-sm font-extralight"><div class="codeWrapper text-light selection:text-super selection:bg-super/10 my-md relative flex flex-col rounded font-mono text-sm font-normal bg-subtler"><div class="translate-y-xs -translate-x-xs bottom-xl mb-xl flex h-0 items-start justify-end md:sticky md:top-[100px]"><div class="overflow-hidden rounded-full border-subtlest ring-subtlest divide-subtlest bg-base"><div class="border-subtlest ring-subtlest divide-subtlest bg-subtler"></div></div></div><div class="-mt-xl"><div><div data-testid="code-language-indicator" class="text-quiet bg-subtle py-xs px-sm inline-block rounded-br rounded-tl-[3px] font-thin">java</div></div><div><span><code><span class="token token">if</span><span> </span><span class="token token punctuation">(</span><span>nums</span><span class="token token punctuation">[</span><span>mid</span><span class="token token punctuation">]</span><span> </span><span class="token token operator"><</span><span> target </span><span class="token token operator">&&</span><span> target </span><span class="token token operator"><=</span><span> nums</span><span class="token token punctuation">[</span><span>r</span><span class="token token punctuation">]</span><span class="token token punctuation">)</span><span> </span><span class="token token punctuation">{</span><span>
</span><span> </span><span class="token token">// Target is in right half</span><span>
</span><span> l </span><span class="token token operator">=</span><span> mid </span><span class="token token operator">+</span><span> </span><span class="token token">1</span><span class="token token punctuation">;</span><span>
</span><span></span><span class="token token punctuation">}</span><span> </span><span class="token token">else</span><span> </span><span class="token token punctuation">{</span><span>
</span><span> </span><span class="token token">// Target must be in left half</span><span>
</span><span> r </span><span class="token token operator">=</span><span> mid </span><span class="token token operator">-</span><span> </span><span class="token token">1</span><span class="token token punctuation">;</span><span>
</span><span></span><span class="token token punctuation">}</span></code></span></div></div></div></pre>

**Java:**

```java
int search(int[] arr, int target) {
 int low = 0, high = arr.length - 1;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 if (arr[mid] == target) return mid;

 // Check which half is sorted
 if (arr[low] <= arr[mid]) {
 // Left half is sorted
 if (arr[low] <= target && target < arr[mid]) {
 high = mid - 1;
 } else {
 low = mid + 1;
 }
 } else {
 // Right half is sorted
 if (arr[mid] < target && target <= arr[high]) {
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }
 }

 return -1;
}
// Time: O(log n), Space: O(1)
```

---

## BS-9: Search in Rotated Sorted Array II (with Duplicates)

**Question:** Search in rotated sorted array that may contain duplicates.

**Intuition:** Same as previous but handle arr[low] == arr[mid] == arr[high] case by shrinking.
**Logic:** Skip duplicates, then apply same logic.

**The Problem with Duplicates**

**The Breaking Case**

<pre class="not-prose w-full rounded font-mono text-sm font-extralight"><div class="codeWrapper text-light selection:text-super selection:bg-super/10 my-md relative flex flex-col rounded font-mono text-sm font-normal bg-subtler"><div class="translate-y-xs -translate-x-xs bottom-xl mb-xl flex h-0 items-start justify-end md:sticky md:top-[100px]"><div class="overflow-hidden rounded-full border-subtlest ring-subtlest divide-subtlest bg-base"><div class="border-subtlest ring-subtlest divide-subtlest bg-subtler"></div></div></div><div class="-mt-xl"><div><div data-testid="code-language-indicator" class="text-quiet bg-subtle py-xs px-sm inline-block rounded-br rounded-tl-[3px] font-thin">text</div></div><div><span><code><span><span>Array: [2, 2, 2, 2, 2, 0, 1, 2]
</span></span><span> l mid r
</span><span> ↓ ↓ ↓
</span><span> nums[l]=2 nums[mid]=2 nums[r]=2
</span><span></span></code></span></div></div></div></pre>

**Question:** Which half is sorted?

<pre class="not-prose w-full rounded font-mono text-sm font-extralight"><div class="codeWrapper text-light selection:text-super selection:bg-super/10 my-md relative flex flex-col rounded font-mono text-sm font-normal bg-subtler"><div class="translate-y-xs -translate-x-xs bottom-xl mb-xl flex h-0 items-start justify-end md:sticky md:top-[100px]"><div class="overflow-hidden rounded-full border-subtlest ring-subtlest divide-subtlest bg-base"><div class="border-subtlest ring-subtlest divide-subtlest bg-subtler"></div></div></div><div class="-mt-xl"><div><div data-testid="code-language-indicator" class="text-quiet bg-subtle py-xs px-sm inline-block rounded-br rounded-tl-[3px] font-thin">java</div></div><div><span><code><span class="token token">if</span><span> </span><span class="token token punctuation">(</span><span>nums</span><span class="token token punctuation">[</span><span>l</span><span class="token token punctuation">]</span><span> </span><span class="token token operator"><=</span><span> nums</span><span class="token token punctuation">[</span><span>mid</span><span class="token token punctuation">]</span><span class="token token punctuation">)</span><span> </span><span class="token token punctuation">{</span><span>
</span><span> </span><span class="token token">// Left half sorted? Can't tell!</span><span>
</span><span></span><span class="token token punctuation">}</span><span>
</span></code></span></div></div></div></pre>

**Problem:** `nums[l] == nums[mid] == nums[r]` - we **cannot determine** which half contains the rotation point !

This is impossible to resolve with binary search logic alone.

---

**The Solution: Shrink Search Space**

When `nums[l] == nums[mid] == nums[r]`, **we cannot eliminate half the array**. Instead:

<pre class="not-prose w-full rounded font-mono text-sm font-extralight"><div class="codeWrapper text-light selection:text-super selection:bg-super/10 my-md relative flex flex-col rounded font-mono text-sm font-normal bg-subtler"><div class="translate-y-xs -translate-x-xs bottom-xl mb-xl flex h-0 items-start justify-end md:sticky md:top-[100px]"><div class="overflow-hidden rounded-full border-subtlest ring-subtlest divide-subtlest bg-base"><div class="border-subtlest ring-subtlest divide-subtlest bg-subtler"></div></div></div><div class="-mt-xl"><div><div data-testid="code-language-indicator" class="text-quiet bg-subtle py-xs px-sm inline-block rounded-br rounded-tl-[3px] font-thin">java</div></div><div><span><code><span class="token token">if</span><span> </span><span class="token token punctuation">(</span><span>nums</span><span class="token token punctuation">[</span><span>l</span><span class="token token punctuation">]</span><span> </span><span class="token token operator">==</span><span> nums</span><span class="token token punctuation">[</span><span>mid</span><span class="token token punctuation">]</span><span> </span><span class="token token operator">&&</span><span> nums</span><span class="token token punctuation">[</span><span>mid</span><span class="token token punctuation">]</span><span> </span><span class="token token operator">==</span><span> nums</span><span class="token token punctuation">[</span><span>r</span><span class="token token punctuation">]</span><span class="token token punctuation">)</span><span> </span><span class="token token punctuation">{</span><span>
</span><span> l</span><span class="token token operator">++</span><span class="token token punctuation">;</span><span> </span><span class="token token">// Shrink from left</span><span>
</span><span> r</span><span class="token token operator">--</span><span class="token token punctuation">;</span><span> </span><span class="token token">// Shrink from right</span><span>
</span><span> </span><span class="token token">continue</span><span class="token token punctuation">;</span><span>
</span><span></span><span class="token token punctuation">}</span><span>
</span></code></span></div></div></div></pre>

**Why this works:** By removing one element from each end, we gradually eliminate duplicates until we can determine which half is sorted.

**Java:**

```java
boolean search(int[] arr, int target) {
 int low = 0, high = arr.length - 1;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 if (arr[mid] == target) return true;

 // Handle duplicates
 if (arr[low] == arr[mid] && arr[mid] == arr[high]) {
 low++;
 high--;
 continue;
 }

 // Identify sorted half
 if (arr[low] <= arr[mid]) {
 if (arr[low] <= target && target < arr[mid]) {
 high = mid - 1;
 } else {
 low = mid + 1;
 }
 } else {
 if (arr[mid] < target && target <= arr[high]) {
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }
 }

 return false;
}
// Time: O(log n) average, O(n) worst case, Space: O(1)
```

---

## BS-10: Find Minimum in Rotated Sorted Array

**Question:** Find minimum element in rotated sorted array (no duplicates).

```
Input: nums = [3,4,5,1,2]
Output: 1
Explanation: The original array was [1,2,3,4,5] rotated 3 times.
```

**Intuition:** Minimum is where rotation happens. Unsorted side contains minimum.
**Logic:** Always move towards unsorted part.

**Java:**

```java
int findMin(int[] arr) {
 int low = 0, high = arr.length - 1;
 int min = Integer.MAX_VALUE;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 // If already sorted
 if (arr[low] <= arr[high]) {
 min = Math.min(min, arr[low]);
 break;
 }

 // Update minimum
 if (arr[low] <= arr[mid]) {
 // Left is sorted
 min = Math.min(min, arr[low]);
 low = mid + 1; // Search in unsorted right
 } else {
 // Right is sorted
 min = Math.min(min, arr[mid]);
 high = mid - 1; // Search in unsorted left
 }
 }

 return min;
}
// Time: O(log n), Space: O(1)
```

---

## BS-11: Find How Many Times Array is Rotated

**Question:** Find number of rotations in rotated sorted array.
**Intuition:** Number of rotations = index of minimum element.
**Logic:** Find index of minimum element.

**Java:**

```java
int findKRotation(int[] arr) {
 int low = 0, high = arr.length - 1;
 int min = Integer.MAX_VALUE;
 int index = -1;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 // If already sorted
 if (arr[low] <= arr[high]) {
 if (arr[low] < min) {
 min = arr[low];
 index = low;
 }
 break;
 }

 if (arr[low] <= arr[mid]) {
 if (arr[low] < min) {
 min = arr[low];
 index = low;
 }
 low = mid + 1;
 } else {
 if (arr[mid] < min) {
 min = arr[mid];
 index = mid;
 }
 high = mid - 1;
 }
 }

 return index;
}
```

---

## BS-12: Single Element in Sorted Array

**Question:** Every element appears twice except one. Find that single element.
**Intuition:** Before single element: (even, odd) pairs. After: (odd, even) pairs.
**Logic:** Check if mid is on left or right of single element.

**Java:**

```java
int singleNonDuplicate(int[] arr) {
 int n = arr.length;

 // Edge cases
 if (n == 1) return arr[0];
 if (arr[0] != arr[1]) return arr[0];
 if (arr[n-1] != arr[n-2]) return arr[n-1];

 int low = 1, high = n - 2;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 // Check if mid is the single element
 if (arr[mid] != arr[mid-1] && arr[mid] != arr[mid+1]) {
 return arr[mid];
 }

 // Check which half we are in
 // Left half: (even, odd) pairs
 // Right half: (odd, even) pairs
 if ((mid % 2 == 0 && arr[mid] == arr[mid+1]) ||
 (mid % 2 == 1 && arr[mid] == arr[mid-1])) {
 // We are in left half, move right
 low = mid + 1;
 } else {
 // We are in right half, move left
 high = mid - 1;
 }
 }

 return -1;
}
// Time: O(log n), Space: O(1)
```

---

## BS-13: Find Peak Element

**Question:** Peak element is greater than its neighbors. Find any peak.
**Intuition:** Move towards increasing slope. There's always a peak in that direction.
**Logic:** If arr[mid] < arr[mid+1], peak is on right. Else, on left or at mid.

**Java:**

```java
int findPeakElement(int[] arr) {
 int n = arr.length;

 // Edge cases
 if (n == 1) return 0;
 if (arr[0] > arr[1]) return 0;
 if (arr[n-1] > arr[n-2]) return n - 1;

 int low = 1, high = n - 2;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 // Check if mid is peak
 if (arr[mid] > arr[mid-1] && arr[mid] > arr[mid+1]) {
 return mid;
 }

 // Move towards increasing slope
 if (arr[mid] < arr[mid+1]) {
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }

 return -1;
}
// Time: O(log n), Space: O(1)
```

---

## BS-14: Square Root of a Number

**Question:** Find integer square root of n (floor value).
**Intuition:** Answer lies between 1 and n. Binary search on answer space.
**Logic:** Check if mid * mid <= n.

**Java:**

```java
int floorSqrt(int n) {
 if (n == 0 || n == 1) return n;

 int low = 1, high = n;
 int ans = 1;

 while (low <= high) {
 int mid = low + (high - low) / 2;
 long square = (long)mid * mid;

 if (square <= n) {
 ans = mid;
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }

 return ans;
}
// Time: O(log n), Space: O(1)
```

---

## BS-15: Nth Root of a Number

**Question:** Find integer nth root of m. Return -1 if doesn't exist.
**Intuition:** Binary search on answer space [1, m].
**Logic:** Calculate mid^n and compare with m.

**Java:**

```java
int NthRoot(int n, int m) {
 int low = 1, high = m;

 while (low <= high) {
 int mid = low + (high - low) / 2;
 long power = calculatePower(mid, n);

 if (power == m) {
 return mid;
 } else if (power < m) {
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }

 return -1;
}

long calculatePower(int base, int exp) {
 long result = 1;
 for (int i = 0; i < exp; i++) {
 result *= base;
 if (result > 1e18) return (long)1e18; // Prevent overflow
 }
 return result;
}
// Time: O(log m * n), Space: O(1)
```

---

## BS-16: Koko Eating Bananas

**Question:** Find minimum eating speed K such that Koko can finish all bananas in H hours.
**Intuition:** Binary search on eating speed. Minimum = 1, Maximum = max(piles).
**Logic:** For each speed, calculate hours needed. Find minimum valid speed.

**Example 1:**

<pre><strong>Input:</strong> piles = [3,6,7,11], h = 8
<strong>Output:</strong> 4
</pre>

**Example 2:**

<pre><strong>Input:</strong> piles = [30,11,23,4,20], h = 5
<strong>Output:</strong> 30
</pre>

**Example 3:**

<pre><strong>Input:</strong> piles = [30,11,23,4,20], h = 6
<strong>Output:</strong> 23</pre>

**Java:**

```java
int minEatingSpeed(int[] piles, int h) {
 int low = 1;
 int high = Arrays.stream(piles).max().getAsInt();
 int ans = high;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 long totalHours = calculateHours(piles, mid);

 if (totalHours <= h) {
 ans = mid;
 high = mid - 1; // Try smaller speed
 } else {
 low = mid + 1;
 }
 }

 return ans;
}

long calculateHours(int[] piles, int speed) {
 long hours = 0;
 for (int pile : piles) {
 hours += (pile + speed - 1) / speed; // Ceiling division
 }
 return hours;
}
// Time: O(n * log(max)), Space: O(1)
```

---

## BS-17: Minimum Days to Make M Bouquets

**Question:** Make m bouquets, each needs k adjacent flowers. Find minimum days.
**Intuition:** Binary search on days. Check if can make m bouquets on given day.
**Logic:** For each day, count possible bouquets.

**Example 1:**

<pre><strong>Input:</strong> bloomDay = [1,10,3,10,2], m = 3, k = 1
<strong>Output:</strong> 3
<strong>Explanation:</strong> Let us see what happened in the first three days. x means flower bloomed and _ means flower did not bloom in the garden.
We need 3 bouquets each should contain 1 flower.
After day 1: [x, _, _, _, _] // we can only make one bouquet.
After day 2: [x, _, _, _, x] // we can only make two bouquets.
After day 3: [x, _, x, _, x] // we can make 3 bouquets. The answer is 3.
</pre>

**Example 2:**

<pre><strong>Input:</strong> bloomDay = [1,10,3,10,2], m = 3, k = 2
<strong>Output:</strong> -1
<strong>Explanation:</strong> We need 3 bouquets each has 2 flowers, that means we need 6 flowers. We only have 5 flowers so it is impossible to get the needed bouquets and we return -1.</pre>

**Java:**

```java
int minDays(int[] bloomDay, int m, int k) {
 long required = (long)m * k;
 if (required > bloomDay.length) return -1;

 int low = Arrays.stream(bloomDay).min().getAsInt();
 int high = Arrays.stream(bloomDay).max().getAsInt();
 int ans = -1;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 if (canMakeBouquets(bloomDay, mid, m, k)) {
 ans = mid;
 high = mid - 1;
 } else {
 low = mid + 1;
 }
 }

 return ans;
}

boolean canMakeBouquets(int[] bloomDay, int day, int m, int k) {
 int bouquets = 0;
 int flowers = 0;

 for (int bloom : bloomDay) {
 if (bloom <= day) {
 flowers++;
 if (flowers == k) {
 bouquets++;
 flowers = 0;
 }
 } else {
 flowers = 0;
 }
 }

 return bouquets >= m;
}
// Time: O(n * log(max)), Space: O(1)
```

---

## BS-18: Find Smallest Divisor

**Question:** Find smallest divisor such that sum of division results <= threshold.Each result of the division is rounded to the nearest integer greater than or equal to that element. (For example: `7/3 = 3` and `10/2 = 5`).

```
Input: nums = [1,2,5,9], threshold = 6
Output: 5
Explanation: We can get a sum to 17 (1+2+5+9) if the divisor is 1.
If the divisor is 4 we can get a sum of 7 (1+1+2+3) and if the divisor is 5 the sum will be 5 (1+1+1+2).
```

**Intuition:** Binary search on divisor. Range: [1, max(arr)].
**Logic:** For each divisor, calculate sum of ceiling divisions.

**Java:**

```java
int smallestDivisor(int[] nums, int threshold) {
 int low = 1;
 int high = Arrays.stream(nums).max().getAsInt();
 int ans = high;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 int sum = calculateSum(nums, mid);

 if (sum <= threshold) {
 ans = mid;
 high = mid - 1;
 } else {
 low = mid + 1;
 }
 }

 return ans;
}

int calculateSum(int[] nums, int divisor) {
 int sum = 0;
 for (int num : nums) {
 sum += (num + divisor - 1) / divisor;
 }
 return sum;
}
```

---

## BS-19: Capacity to Ship Packages within D Days

**Question:** Find minimum ship capacity to ship all packages in D days.

<pre><strong>Input:</strong> weights = [1,2,3,4,5,6,7,8,9,10], days = 5
<strong>Output:</strong> 15
<strong>Explanation:</strong> A ship capacity of 15 is the minimum to ship all the packages in 5 days like this:
1st day: 1, 2, 3, 4, 5
2nd day: 6, 7
3rd day: 8
4th day: 9
5th day: 10

Note that the cargo must be shipped in the order given, so using a ship of capacity 14 and splitting the packages into parts like (2, 3, 4, 5), (1, 6, 7), (8), (9), (10) is not allowed.
</pre>

**Intuition:** Binary search on capacity. Min = max(weights), Max = sum(weights).
**Logic:** For each capacity, check if can ship in D days.

**Java:**

```java
int shipWithinDays(int[] weights, int days) {
 int low = Arrays.stream(weights).max().getAsInt();
 int high = Arrays.stream(weights).sum();
 int ans = high;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 if (canShip(weights, mid, days)) {
 ans = mid;
 high = mid - 1;
 } else {
 low = mid + 1;
 }
 }

 return ans;
}

boolean canShip(int[] weights, int capacity, int days) {
 int daysNeeded = 1;
 int currentLoad = 0;

 for (int weight : weights) {
 if (currentLoad + weight > capacity) {
 daysNeeded++;
 currentLoad = weight;
 } else {
 currentLoad += weight;
 }
 }

 return daysNeeded <= days;
}
// Time: O(n * log(sum)), Space: O(1)
```

---

## BS-20: Kth Missing Positive Number

**Question:** Find kth missing positive number from sorted array.

**Example 1:**

<pre><strong>Input:</strong> arr = [2,3,4,7,11], k = 5
<strong>Output:</strong> 9
<strong>Explanation: </strong>The missing positive integers are [1,5,6,8,9,10,12,13,...]. The 5<sup>th</sup> missing positive integer is 9.</pre>

**Example 2:**

<pre><strong>Input:</strong> arr = [1,2,3,4], k = 2
<strong>Output:</strong> 6
<strong>Explanation: </strong>The missing positive integers are [5,6,7,...]. The 2<sup>nd</sup> missing positive integer is 6.</pre>

**Intuition:** At index i, missing count = arr[i] - (i + 1). Binary search to find position.
**Logic:** Find index where missing count >= k, calculate answer.

**Java:**

```java
int findKthPositive(int[] arr, int k) {
 int low = 0, high = arr.length - 1;

 while (low <= high) {
 int mid = low + (high - low) / 2;
 int missing = arr[mid] - (mid + 1);

 if (missing < k) {
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }

 // Answer: arr[high] + (k - missing at high)
 // Or simplified: high + k + 1
 return low + k;
}
// Time: O(log n), Space: O(1)
```

---

## BS-21: Aggressive Cows (Minimize Maximum Distance)

**Question:** Place C cows in N stalls to maximize minimum distance between cows.
**Intuition:** Binary search on answer (minimum distance). Check if possible to place cows with that distance.
**Logic:** Greedy check - place cow if distance from last >= mid.

**Java:**

```java
int aggressiveCows(int[] stalls, int cows) {
 Arrays.sort(stalls);

 int low = 1;
 int high = stalls[stalls.length - 1] - stalls[0];
 int ans = 0;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 if (canPlaceCows(stalls, cows, mid)) {
 ans = mid;
 low = mid + 1; // Try larger distance
 } else {
 high = mid - 1;
 }
 }

 return ans;
}

boolean canPlaceCows(int[] stalls, int cows, int minDist) {
 int count = 1;
 int lastPos = stalls[0];

 for (int i = 1; i < stalls.length; i++) {
 if (stalls[i] - lastPos >= minDist) {
 count++;
 lastPos = stalls[i];
 }
 }

 return count >= cows;
}
// Time: O(n log n + n * log(max-min)), Space: O(1)
```

---

## BS-22: Book Allocation / Painter's Partition / Split Array

**Question:** Allocate books to students to minimize maximum pages allocated to any student.
**Intuition:** Binary search on answer (max pages). Check if allocation possible with that limit.
**Logic:** Greedy allocation - give books to student until limit.

**Java:**

```java
int findPages(int[] arr, int m) {
 if (m > arr.length) return -1;

 int low = Arrays.stream(arr).max().getAsInt();
 int high = Arrays.stream(arr).sum();
 int ans = -1;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 if (canAllocate(arr, m, mid)) {
 ans = mid;
 high = mid - 1;
 } else {
 low = mid + 1;
 }
 }

 return ans;
}

boolean canAllocate(int[] arr, int students, int maxPages) {
 int studentsNeeded = 1;
 int currentPages = 0;

 for (int pages : arr) {
 if (currentPages + pages > maxPages) {
 studentsNeeded++;
 currentPages = pages;
 } else {
 currentPages += pages;
 }
 }

 return studentsNeeded <= students;
}
// Time: O(n * log(sum)), Space: O(1)
```

---

## BS-23: Median of Two Sorted Arrays

**Question:** Find median of two sorted arrays in O(log(min(m,n))).
**Intuition:** Binary search on smaller array. Partition both arrays to form two halves.
**Logic:** Ensure left half elements <= right half elements.

**Java:**

```java
double findMedianSortedArrays(int[] nums1, int[] nums2) {
 if (nums1.length > nums2.length) {
 return findMedianSortedArrays(nums2, nums1);
 }

 int m = nums1.length, n = nums2.length;
 int low = 0, high = m;

 while (low <= high) {
 int cut1 = (low + high) / 2;
 int cut2 = (m + n + 1) / 2 - cut1;

 int left1 = (cut1 == 0) ? Integer.MIN_VALUE : nums1[cut1 - 1];
 int left2 = (cut2 == 0) ? Integer.MIN_VALUE : nums2[cut2 - 1];
 int right1 = (cut1 == m) ? Integer.MAX_VALUE : nums1[cut1];
 int right2 = (cut2 == n) ? Integer.MAX_VALUE : nums2[cut2];

 if (left1 <= right2 && left2 <= right1) {
 if ((m + n) % 2 == 0) {
 return (Math.max(left1, left2) + Math.min(right1, right2)) / 2.0;
 } else {
 return Math.max(left1, left2);
 }
 } else if (left1 > right2) {
 high = cut1 - 1;
 } else {
 low = cut1 + 1;
 }
 }

 return 0.0;
}
// Time: O(log(min(m,n))), Space: O(1)
```

---

## BS-24: Kth Element of Two Sorted Arrays

**Question:** Find kth element in union of two sorted arrays.
**Intuition:** Similar to median. Binary search on smaller array.
**Logic:** Partition arrays such that left part has k elements.

**Java:**

```java
int kthElement(int[] arr1, int[] arr2, int k) {
 if (arr1.length > arr2.length) {
 return kthElement(arr2, arr1, k);
 }

 int m = arr1.length, n = arr2.length;
 int low = Math.max(0, k - n), high = Math.min(k, m);

 while (low <= high) {
 int cut1 = (low + high) / 2;
 int cut2 = k - cut1;

 int left1 = (cut1 == 0) ? Integer.MIN_VALUE : arr1[cut1 - 1];
 int left2 = (cut2 == 0) ? Integer.MIN_VALUE : arr2[cut2 - 1];
 int right1 = (cut1 == m) ? Integer.MAX_VALUE : arr1[cut1];
 int right2 = (cut2 == n) ? Integer.MAX_VALUE : arr2[cut2];

 if (left1 <= right2 && left2 <= right1) {
 return Math.max(left1, left2);
 } else if (left1 > right2) {
 high = cut1 - 1;
 } else {
 low = cut1 + 1;
 }
 }

 return -1;
}
```

---

## BS-25: Row with Maximum 1s

**Question:** Find row with maximum number of 1s in binary matrix (0s followed by 1s in each row).
**Intuition:** Binary search in each row to find first 1, count 1s.
**Logic:** Use lower bound to find first 1 in each row.

**Java:**

```java
int rowWithMax1s(int[][] arr) {
 int maxCount = 0;
 int maxRow = -1;

 for (int i = 0; i < arr.length; i++) {
 int firstOne = lowerBound(arr[i], 1);
 int count = arr[i].length - firstOne;

 if (count > maxCount) {
 maxCount = count;
 maxRow = i;
 }
 }

 return maxRow;
}

int lowerBound(int[] arr, int target) {
 int low = 0, high = arr.length - 1;
 int ans = arr.length;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 if (arr[mid] >= target) {
 ans = mid;
 high = mid - 1;
 } else {
 low = mid + 1;
 }
 }

 return ans;
}
// Time: O(m * log n), Space: O(1)
```

---

## BS-26: Search in 2D Matrix I

**Question:** Search target in matrix where each row is sorted and first element of row > last of previous row.
**Intuition:** Treat 2D matrix as 1D sorted array. Use binary search with index mapping.
**Logic:** mid index in 1D = (row, col) in 2D where row = mid/n, col = mid%n.

**Java:**

```java
boolean searchMatrix(int[][] matrix, int target) {
 int m = matrix.length, n = matrix[0].length;
 int low = 0, high = m * n - 1;

 while (low <= high) {
 int mid = low + (high - low) / 2;
 int row = mid / n;
 int col = mid % n;

 if (matrix[row][col] == target) {
 return true;
 } else if (matrix[row][col] < target) {
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }

 return false;
}
// Time: O(log(m*n)), Space: O(1)
```

---

## BS-27: Search in 2D Matrix II

**Question:** Search in matrix where rows and columns are sorted.
**Intuition:** Start from top-right or bottom-left. Move based on comparison.
**Logic:** Top-right: if target < current, move left. If target > current, move down.

**Java:**

```java
boolean searchMatrix(int[][] matrix, int target) {
 int m = matrix.length, n = matrix[0].length;
 int row = 0, col = n - 1;

 while (row < m && col >= 0) {
 if (matrix[row][col] == target) {
 return true;
 } else if (matrix[row][col] < target) {
 row++;
 } else {
 col--;
 }
 }

 return false;
}
// Time: O(m + n), Space: O(1)
```

---

## BS-28: Find Peak Element in 2D Matrix

**Question:** Find peak element in 2D matrix (greater than all neighbors).You may assume that the entire matrix is surrounded by an **outer perimeter** with the value `-1` in each cell.

**Example 1:**

![](https://assets.leetcode.com/uploads/2021/06/08/1.png)


<pre><strong>Input:</strong> mat = [[1,4],[3,2]]
<strong>Output:</strong> [0,1]
<strong>Explanation:</strong> Both 3 and 4 are peak elements so [1,0] and [0,1] are both acceptable answers.</pre>

**Intuition:** Binary search on columns. In each mid column, find max element. Check if it's peak.
**Logic:** If not peak, move towards greater neighbor.

Core Intuition

**Peak is Guaranteed to Exist**: If you're at any element and it's not a peak, moving in the direction of the larger neighbor will eventually lead you to a peak. This is because edge elements treat missing neighbors as -∞, ensuring at least one peak exists.

Binary Search on Columns Approach

The most common strategy applies binary search on **columns** while scanning **rows**:

1. **Pick middle column** (`mid = (low + high) / 2`)
2. **Find max element in that column** across all rows → this gives you `maxRow`
3. **Compare with left and right neighbors**:
 * If `mat[maxRow][mid]` ≥ both neighbors → **Peak found!**
 * If left neighbor is bigger → **Peak must be in left half** (move `high = mid - 1`)
 * If right neighbor is bigger → **Peak must be in right half** (move `low = mid + 1`)

Why This Works (Visual Analogy)

Think of the matrix as a **mountain range viewed from above**:

* Finding the max in a column is like **finding the tallest point along a vertical slice**
* If this point is smaller than its left neighbor, there's a **higher peak to the left** (like following uphill)
* By repeatedly moving toward higher values, you're **guaranteed to reach a summit**

**Java:**

```java
int[] findPeakGrid(int[][] mat) {
 int m = mat.length, n = mat[0].length;
 int low = 0, high = n - 1;

 while (low <= high) {
 int mid = low + (high - low) / 2;
 int maxRow = findMaxInColumn(mat, mid);

 int left = (mid > 0) ? mat[maxRow][mid - 1] : -1;
 int right = (mid < n - 1) ? mat[maxRow][mid + 1] : -1;

 if (mat[maxRow][mid] > left && mat[maxRow][mid] > right) {
 return new int[]{maxRow, mid};
 } else if (mat[maxRow][mid] < left) {
 high = mid - 1;
 } else {
 low = mid + 1;
 }
 }

 return new int[]{-1, -1};
}

int findMaxInColumn(int[][] mat, int col) {
 int maxRow = 0;
 for (int i = 1; i < mat.length; i++) {
 if (mat[i][col] > mat[maxRow][col]) {
 maxRow = i;
 }
 }
 return maxRow;
}
// Time: O(m * log n), Space: O(1)
```

---

## BS-29: Median in Row-wise Sorted Matrix

**Question:** Find median in matrix where each row is sorted.

```
Input: mat[][] = [[1, 3, 5],
 [2, 6, 9],
 [3, 6, 9]]
Output: 5
Explanation: Sorting matrix elements gives us [1, 2, 3, 3, 5, 6, 6, 9, 9]. Hence, 5 is median.
```


**Intuition:** Binary search on answer. Count elements <= mid. Median has (m*n)/2 elements smaller.
**Logic:** Use upper bound on each row to count elements <= mid.

**Java:**

```java
int findMedian(int[][] matrix) {
 int m = matrix.length, n = matrix[0].length;

 int low = Integer.MAX_VALUE, high = Integer.MIN_VALUE;
 for (int i = 0; i < m; i++) {
 low = Math.min(low, matrix[i][0]);
 high = Math.max(high, matrix[i][n - 1]);
 }

 int required = (m * n) / 2;

 while (low <= high) {
 int mid = low + (high - low) / 2;
 int count = countSmallerOrEqual(matrix, mid);

 if (count <= required) {
 low = mid + 1;
 } else {
 high = mid - 1;
 }
 }

 return low;
}

int countSmallerOrEqual(int[][] matrix, int target) {
 int count = 0;
 for (int[] row : matrix) {
 count += upperBound(row, target);
 }
 return count;
}

int upperBound(int[] arr, int target) {
 int low = 0, high = arr.length - 1;
 int ans = arr.length;

 while (low <= high) {
 int mid = low + (high - low) / 2;

 if (arr[mid] > target) {
 ans = mid;
 high = mid - 1;
 } else {
 low = mid + 1;
 }
 }

 return ans;
}
// Time: O(m * log n * log(max-min)), Space: O(1)
```

---

## BS-30: Binary Search on Answers with Decimal

**Question:** Find square root with decimal precision (up to d decimal places).
**Intuition:** Binary search but with decimal steps. Or use formula approach.
**Logic:** Binary search on [0, n] with precision.

**Java:**

```java
double sqrtWithPrecision(int n, int d) {
 double low = 0, high = n;
 double epsilon = Math.pow(10, -d);

 while (high - low > epsilon) {
 double mid = low + (high - low) / 2;

 if (mid * mid < n) {
 low = mid;
 } else {
 high = mid;
 }
 }

 return low;
}
// Time: O(log n * d), Space: O(1)
```

---

## Binary Search Patterns Summary

### Pattern 1: Simple Binary Search

**Template:**

```java
while (low <= high) {
 int mid = low + (high - low) / 2;
 if (arr[mid] == target) return mid;
 else if (arr[mid] < target) low = mid + 1;
 else high = mid - 1;
}
```

### Pattern 2: Lower Bound / Upper Bound

**Template:**

```java
int ans = n; // Default value
while (low <= high) {
 int mid = low + (high - low) / 2;
 if (condition) {
 ans = mid;
 high = mid - 1; // Search for better answer
 } else {
 low = mid + 1;
 }
}
return ans;
```

### Pattern 3: Binary Search on Answer Space

**Use Cases:** Koko bananas, aggressive cows, book allocation
**Template:**

```java
int low = minPossible, high = maxPossible;
int ans = -1;
while (low <= high) {
 int mid = low + (high - low) / 2;
 if (isPossible(mid)) {
 ans = mid;
 high = mid - 1; // or low = mid + 1 based on min/max
 } else {
 low = mid + 1; // or high = mid - 1
 }
}
```

### Pattern 4: Rotated Array Search

**Key:** Identify sorted half, check if target lies in that range

### Pattern 5: Peak Finding

**Key:** Move towards increasing slope

### Pattern 6: 2D Matrix Search

- **Type 1:** Treat as 1D array (row = mid/n, col = mid%n)
- **Type 2:** Start top-right or bottom-left, move based on comparison

---

## Common Binary Search Mistakes to Avoid

1. **Overflow:** Use `mid = low + (high - low) / 2` NOT `(low + high) / 2`
2. **Infinite Loop:**

 - Check boundary conditions carefully
 - Ensure `low` and `high` update properly
3. **low <= high vs low < high:**

 - Use `low <= high` in most cases
 - Use `low < high` only for specific edge case handling
4. **When to use low = mid vs low = mid + 1:**

 - If `mid` can be answer: `low = mid` (use `low < high`)
 - If `mid` cannot be answer: `low = mid + 1` (use `low <= high`)
5. **Answer variable:**

 - Initialize properly (often `n`, `-1`, or extreme values)
 - Update only when condition is satisfied

---

## Time Complexity Table


| Problem Type | Time Complexity | Space |
| ------------------ | ----------------------- | ----- |
| Simple BS | O(log n) | O(1) |
| Lower/Upper Bound | O(log n) | O(1) |
| Rotated Array | O(log n) | O(1) |
| Peak Element | O(log n) | O(1) |
| BS on Answer | O(n * log(range)) | O(1) |
| 2D Matrix (Type 1) | O(log(m*n)) | O(1) |
| 2D Matrix (Type 2) | O(m + n) | O(1) |
| Median of 2 Arrays | O(log(min(m,n))) | O(1) |
| Aggressive Cows | O(n log n + n*log(max)) | O(1) |

---
