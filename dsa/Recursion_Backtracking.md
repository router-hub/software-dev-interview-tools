# Time Complexity & Space Complexity - Interview Guide

## 🎯 How to Discuss Complexity in Interviews

When solving recursion/backtracking problems, **ALWAYS** discuss complexity in this order:

### 1. **Identify the Recursion Tree Structure**
   - How many recursive calls per function call?
   - What's the height/depth of the recursion tree?
   - How does the problem size reduce at each level?

### 2. **Calculate Time Complexity**
   - **Formula:** `O(branches^depth × work_per_node)`
   - Count total nodes in recursion tree
   - Multiply by work done at each node

### 3. **Calculate Space Complexity**
   - **Recursion stack space:** O(maximum depth)
   - **Auxiliary space:** Any extra data structures (arrays, lists, etc.)
   - **Total:** O(recursion depth + auxiliary space)

---

## 📊 Common Time Complexity Patterns

| Pattern                 | Branches       | Depth    | Time Complexity    | Example                  |
| ----------------------- | -------------- | -------- | ------------------ | ------------------------ |
| **Linear Recursion**    | 1              | n        | O(n)               | Print 1 to N, Factorial  |
| **Binary Recursion**    | 2              | n        | O(2^n)             | Fibonacci (naive)        |
| **Pick/Not-Pick**       | 2              | n        | O(2^n)             | All subsequences         |
| **Loop with Recursion** | varies         | n        | O(2^n) to O(m^n)   | Combination Sum II       |
| **Permutations (swap)** | n, n-1, n-2... | n        | O(n! × n)          | All permutations         |
| **Grid DFS (4-dir)**    | up to 4        | m×n      | O(4^(m×n))         | Word search, Rat in maze |
| **N-Queens**            | varies         | n        | O(n!)              | N-Queens placement       |
| **Sudoku**              | up to 9        | 81 cells | O(9^(empty cells)) | Sudoku solver            |

---

## 🧮 How to Calculate Recursion Tree Size

### **Method 1: Count Nodes in Tree**
For recursion with branching factor `b` and depth `d`:
- **Total nodes** = b^0 + b^1 + b^2 + ... + b^d = **(b^(d+1) - 1) / (b - 1)**
- For large d: **≈ O(b^d)**

**Example:** Fibonacci(n)
- Branches: 2 (fib(n-1) + fib(n-2))
- Depth: n
- Nodes: 2^0 + 2^1 + ... + 2^n ≈ **O(2^n)**

### **Method 2: Analyze Recursive Relation**
Use Master Theorem or recurrence relations:
- **T(n) = aT(n/b) + f(n)**
- Example: Merge Sort → T(n) = 2T(n/2) + O(n) → **O(n log n)**

### **Method 3: Draw Small Examples**
For small inputs (n=3, n=4), draw the complete recursion tree and count nodes.

---

## 💡 Interview Tips - What to Say

### **When Analyzing Time Complexity:**

✅ **SAY THIS:**
- *"The recursion forms a tree where each node makes 2 recursive calls (binary tree)"*
- *"The depth of recursion is n, so we have 2^n nodes in the tree"*
- *"At each node, we do O(k) work for copying the list, so total is O(2^n × k)"*
- *"The time complexity is exponential due to the branching factor"*

❌ **DON'T SAY:**
- *"It's just O(n)"* (without explaining recursion tree)
- *"I think it's exponential maybe?"* (be precise)

### **When Analyzing Space Complexity:**

✅ **SAY THIS:**
- *"The recursion depth is n, so stack space is O(n)"*
- *"We use an auxiliary list that grows up to size n, so O(n) extra space"*
- *"Total space: O(n) for stack + O(n) for the list = O(n)"*
- *"We don't count the output space in space complexity, only auxiliary space"*

❌ **DON'T SAY:**
- *"Space is also 2^n because of all the recursive calls"* (stack only holds current path)

---

## 🔥 Common Interview Questions on Complexity

### **Q1: Why is Fibonacci O(2^n) and not O(n)?**
**A:** "Because each call makes 2 more calls, forming a binary tree. The tree has height n and ~2^n total nodes. Each node does O(1) work, so total is O(2^n)."

### **Q2: How can we optimize Fibonacci from O(2^n) to O(n)?**
**A:** "Use memoization to store computed values in a dp array. Each subproblem is computed only once, giving us n subproblems × O(1) work = O(n)."

### **Q3: Why is N-Queens O(n!) and not O(n^n)?**
**A:** "In the first row, we try n columns. In the second row, we have at most n-1 safe positions (one column is blocked). This continues as n × (n-1) × (n-2) × ... = n!. Pruning with isSafe() reduces branches, but worst case is still O(n!)."

### **Q4: What's the difference between O(2^n) and O(n!)?**
**A:** "O(2^n) grows as 2, 4, 8, 16, 32... (exponential). O(n!) grows as 1, 2, 6, 24, 120... (factorial). Factorial grows MUCH faster. For n=10: 2^10 = 1024, but 10! = 3,628,800."

### **Q5: In backtracking, why is space O(n) when we generate 2^n outputs?**
**A:** "We don't count the output space. The recursion stack depth is n (we go n levels deep). The auxiliary space for the current path is also n. So space = O(n). The 2^n outputs are the result, not auxiliary space."

### **Q6: How do you calculate complexity for grid DFS?**
**A:** "Each cell has 4 directional choices (up, down, left, right). In worst case, we explore all paths. For an m×n grid, depth can be m×n, and each node branches 4 ways, giving O(4^(m×n)). Visited marking prunes this significantly in practice."

---

## 🎓 Step-by-Step Template for Interview

**When interviewer asks: "What's the time and space complexity?"**

1. **Draw the recursion tree** (or describe it verbally)
2. **Count branches per node:** "Each call makes ___ recursive calls"
3. **Find the depth:** "The recursion goes ___ levels deep"
4. **Calculate total nodes:** "So we have approximately ___ nodes"
5. **Work per node:** "At each node, we do ___ work"
6. **Time complexity:** "Total time = nodes × work = ___"
7. **Space complexity:** "Stack depth is ___, auxiliary space is ___, total O(___)"

---

## Re-1: Introduction to Recursion | Recursion Tree | Stack Space

**Question:** What is recursion? How does it work internally?
**Intuition:** A function that calls itself to solve smaller instances of same problem. Base case stops recursion. Stack stores function calls.
**Logic:** Break problem into smaller subproblems → Solve recursively → Combine results.

**Recursion Components:**

```java
// Three essential parts:
// 1. Base Case (termination condition)
// 2. Recursive Call (calling itself)
// 3. Processing (before or after recursive call)

void recursiveFunction(int n) {
    // 1. Base Case - stops recursion
    if (n == 0) {
        return;
    }

    // 2. Processing before recursive call (optional)
    System.out.print(n + " ");

    // 3. Recursive Call
    recursiveFunction(n - 1);

    // 4. Processing after recursive call - Backtracking (optional)
}

// Recursion Tree for f(3):
// f(3)
// /
// f(2)
// /
// f(1)
// /
// f(0) - Base case

// Stack Space: O(n) - height of recursion tree
// Each call takes O(1) space, n calls total
```

---

## Re-2: Print Name N Times

**Question:** Print your name N times using recursion.
**Intuition:** Call function recursively N times. Base case when count reaches N.
**Logic:** Decrement counter in each call.

**Java:**

```java
void printName(int i, int n) {
    if (i > n) return; // Base case

    System.out.println("Striver");
    printName(i + 1, n);
}

// Call: printName(1, 5);
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n)**
- **Recursion tree:** Linear chain (1 recursive call per function)
- **Depth:** n (from i=1 to i=n)
- **Total calls:** n function calls
- **Work per call:** O(1) (just printing)
- **Total:** n × O(1) = O(n)

**Space Complexity: O(n)**
- **Recursion stack:** O(n) - maximum n frames on call stack
- **Auxiliary space:** O(1) - no extra data structures
- **Total:** O(n)

**Interview Discussion:**
- *"Each function call adds one frame to the stack. At maximum depth (when i=n), we have n frames on the stack before unwinding begins. This is why space is O(n) despite not using any data structures."*

---

## Re-3: Print 1 to N using Recursion

**Question:** Print numbers from 1 to N.
**Intuition:** Print current, then recurse for i+1.
**Logic:** Forward recursion.

**Java:**

```java
void print1ToN(int i, int n) {
    if (i > n) return;

    System.out.print(i + " ");
    print1ToN(i + 1, n);
}

// Call: print1ToN(1, 5);
// Output: 1 2 3 4 5
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n)**
- **Approach:** Linear recursion
- **Total calls:** n
- **Work per call:** O(1)

**Space Complexity: O(n)**
- **Stack depth:** n levels

---

## Re-4: Print N to 1 using Recursion

**Question:** Print numbers from N to 1.
**Intuition:** Two ways - print then recurse OR recurse then print (backtracking).
**Logic:** Forward recursion OR backtracking.

**Java:**

```java
// Method 1: Forward Recursion
void printNTo1(int i, int n) {
    if (i < 1) return;

    System.out.print(i + " ");
    printNTo1(i - 1, n);
}

// Method 2: Backtracking (Print after recursive call)
void printNTo1Backtrack(int i, int n) {
    if (i > n) return;

    printNTo1Backtrack(i + 1, n); // Recurse first
    System.out.print(i + " "); // Print during backtrack
}

// Call: printNTo1Backtrack(1, 5);
// Output: 5 4 3 2 1
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n)** - Both methods
- n recursive calls, O(1) work each

**Space Complexity: O(n)** - Both methods
- **Stack depth:** n (goes from 1 to n, then unwinds)

**Interview Note:**
- *"Method 2 (backtracking) prints during the unwinding phase. The stack still reaches depth n before any printing happens, so space complexity remains O(n)."*

---

## Re-5: Sum of First N Numbers

**Question:** Calculate sum of first N natural numbers using recursion.
**Intuition:** Two approaches - parameterized (pass sum) or functional (return sum).
**Logic:** sum(n) = n + sum(n-1).

**Java:**

```java
// Approach 1: Parameterized Recursion
void sumParameterized(int i, int sum) {
    if (i < 1) {
        System.out.println(sum);
        return;
    }

    sumParameterized(i - 1, sum + i);
}

// Approach 2: Functional Recursion (Better)
int sumFunctional(int n) {
    if (n == 0) return 0;

    return n + sumFunctional(n - 1);
}

// Call: sumParameterized(5, 0); → 15
// Call: sumFunctional(5); → 15
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n)** - Both approaches
- **Recursive calls:** n calls (from n down to 0)
- **Work per call:** O(1) (addition)
- **Total:** n × O(1) = O(n)

**Space Complexity: O(n)** - Both approaches
- **Recursion stack:** O(n) depth
- **No auxiliary space**

**Interview Discussion:**
- *"While the parameterized approach passes the sum as a parameter and functional approach returns it, both still require O(n) stack frames. The iterative approach would be O(1) space: `sum = n*(n+1)/2`."*

---

## Re-6: Factorial of N

**Question:** Calculate N! using recursion.
**Intuition:** n! = n * (n-1)!
**Logic:** Functional recursion returning product.

**Java:**

```java
int factorial(int n) {
    if (n == 0 || n == 1) return 1;

    return n * factorial(n - 1);
}

// factorial(5) = 5 * 4 * 3 * 2 * 1 = 120
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n)**
- **Recursive calls:** n calls (n, n-1, n-2, ..., 1)
- **Work per call:** O(1) (one multiplication)
- **Total:** O(n)

**Space Complexity: O(n)**
- **Stack depth:** n levels deep

**Interview Note:**
- *"This is a tail-recursive function that could be optimized by a compiler with tail-call optimization to O(1) space, but Java doesn't support this. We can also solve iteratively in O(1) space."*

---

## Re-7: Reverse an Array using Recursion

**Question:** Reverse array using recursion with two pointers.
**Intuition:** Swap elements at left and right pointers, recurse for middle portion.
**Logic:** Two pointers moving towards center.

**Java:**

```java
// Using two pointers
void reverseArray(int[] arr, int left, int right) {
    if (left >= right) return;

    // Swap
    int temp = arr[left];
    arr[left] = arr[right];
    arr[right] = temp;

    reverseArray(arr, left + 1, right - 1);
}

// Using single pointer
void reverseArray(int[] arr, int i) {
    int n = arr.length;
    if (i >= n / 2) return;

    int temp = arr[i];
    arr[i] = arr[n - i - 1];
    arr[n - i - 1] = temp;

    reverseArray(arr, i + 1);
}

```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n)**
- **Recursive calls:** n/2 swaps needed
- **Work per swap:** O(1)
- **Total:** O(n/2) = O(n)

**Space Complexity: O(n)**
- **Stack depth:** O(n/2) = O(n) - recursion goes halfway through array
- **Auxiliary space:** O(1) - only temp variable

**Interview Discussion:**
- *"We only recurse until i reaches n/2, so we make n/2 recursive calls. However, this is still O(n) in Big-O notation. The space complexity is O(n/2) for the stack, which simplifies to O(n)."*
- *"Iterative two-pointer approach would achieve O(1) space."*

---

## Re-8: Check if String is Palindrome

**Question:** Check palindrome using recursion.
**Intuition:** Compare first and last chars, recurse for middle.
**Logic:** Two pointers approach.

**Java:**

```java
boolean isPalindrome(String s, int left, int right) {
    if (left >= right) return true;

    if (s.charAt(left) != s.charAt(right)) return false;

    return isPalindrome(s, left + 1, right - 1);
}

// Call: isPalindrome("madam", 0, 4); → true
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n)**
- **Recursive calls:** n/2 comparisons
- **Work per call:** O(1) (character comparison)
- **Total:** O(n)

**Space Complexity: O(n)**
- **Stack depth:** O(n/2) = O(n)

**Interview Note:**
- *"Similar to array reversal, we recurse halfway through the string. Each recursive call compares two characters."*

---

## Re-9: Fibonacci Number

**Question:** Find nth Fibonacci number using recursion.
**Intuition:** fib(n) = fib(n-1) + fib(n-2). Base cases: fib(0)=0, fib(1)=1.
**Logic:** Multiple recursion (calls itself twice).

**Java:**

```java
int fibonacci(int n) {
    if (n <= 1) return n;

    return fibonacci(n - 1) + fibonacci(n - 2);
}

```

### ⏱️ Time & Space Complexity Analysis

**Naive Recursion:**

**Time Complexity: O(2^n)** ❌ Very Bad!
- **Recursion tree:** Binary tree (2 branches per node)
- **Depth:** n
- **Total nodes:** 2^0 + 2^1 + ... + 2^n ≈ **2^(n+1) - 1 ≈ O(2^n)**
- **Work per node:** O(1)
- **Total:** O(2^n)

**Why it's exponential:**
```
         fib(5)
        /      \
     fib(4)    fib(3)
     /    \    /    \
  fib(3) fib(2) fib(2) fib(1)
   ...     ...    ...     ...
```
- fib(3) is calculated **multiple times** - no memoization!

**Space Complexity: O(n)**
- **Stack depth:** Maximum n (following the left branch: fib(n) → fib(n-1) → ... → fib(0))
- **Not O(2^n)!** - Stack only holds the current path, not all nodes

**Optimized with Memoization:**

```java
int fibMemo(int n, int[] dp) {
    if (n <= 1) return n;
    if (dp[n] != -1) return dp[n];

    return dp[n] = fibMemo(n - 1, dp) + fibMemo(n - 2, dp);
}
```

**Time Complexity: O(n)** ✅
- Each subproblem (fib(0) to fib(n)) computed **only once**
- n subproblems × O(1) work = O(n)

**Space Complexity: O(n)**
- **Stack:** O(n)
- **DP array:** O(n)
- **Total:** O(n) + O(n) = O(n)

**Interview Discussion:**
- *"This is the classic example of why recursion can be inefficient. The naive approach recalculates the same values exponentially many times. With memoization, we store results in a dp array, avoiding redundant calculations."*
- *"For n=30, naive takes ~2^30 = 1 billion operations, while memoized takes just 30 operations!"*

---

## Re-10: Print All Subsequences

**Question:** Print all subsequences of array [3, 1, 2].
**Intuition:** For each element, two choices - pick or not pick. Use recursion tree.
**Logic:** Pick/Not-pick pattern. This is the foundation of backtracking!

**Java:**

```java
void printSubsequences(int[] arr, int index, List<Integer> current) {
    // Base case
    if (index == arr.length) {
        System.out.println(current);
        return;
    }

    // Pick current element
    current.add(arr[index]);
    printSubsequences(arr, index + 1, current);

    // Not pick - Backtrack (remove last added)
    current.remove(current.size() - 1);
    printSubsequences(arr, index + 1, current);
}

// Call: printSubsequences(arr, 0, new ArrayList<>());
// Output: [3,1,2], [3,1], [3,2], [3], [1,2], [1], [2], []
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(2^n × n)**
- **Recursion tree:** Perfect binary tree - each node makes 2 recursive calls (pick/not-pick)
- **Depth:** n (one decision per element)
- **Total nodes:** 2^n (each subset is a leaf node)
- **Work per node:** O(n) for printing the list (in worst case, list has n elements)
- **Total:** 2^n × O(n) = **O(n × 2^n)**

**Space Complexity: O(n)**
- **Recursion stack:** O(n) - maximum depth is n
- **Auxiliary space (current list):** O(n) - at most n elements
- **Total:** O(n)
- **Note:** We don't count the 2^n output subsequences in space complexity

**Recursion Tree Visualization:**
```
                   f(0, [])
            Pick 3 /        \ Not Pick
           f(1,[3])          f(1,[])
         /        \         /        \
    f(2,[3,1])  f(2,[3]) f(2,[1])  f(2,[])
      / \        / \       / \       / \
    ...  ...   ...  ...  ... ...   ... ...
```
- **Levels:** 3 (for n=3)
- **Nodes at each level:** 1, 2, 4, 8 (total = 2^0 + 2^1 + 2^2 + 2^3 = 15 ≈ 2^(n+1) - 1)

**Interview Discussion:**
- *"This is the classic pick/not-pick pattern. Each element has 2 choices, so for n elements, we get 2^n total subsequences. The time is O(2^n × n) because printing a list can take O(n) in the worst case."*
- *"Space is NOT O(2^n) because the recursion stack only stores the current path, not all paths. At any point, we have at most n recursive calls on the stack."*

---

## Re-11: Subsequences with Sum K

**Question:** Print only subsequences whose sum equals K.
**Intuition:** Add condition - print only when sum == K at base case.
**Logic:** Same pick/not-pick, but track sum.

**Java:**

```java
void printSubsequencesWithSumK(int[] arr, int index, List<Integer> current, int sum, int k) {
    if (index == arr.length) {
        if (sum == k) {
            System.out.println(current);
        }
        return;
    }

    // Pick
    current.add(arr[index]);
    printSubsequencesWithSumK(arr, index + 1, current, sum + arr[index], k);

    // Not pick - Backtrack
    current.remove(current.size() - 1);
    printSubsequencesWithSumK(arr, index + 1, current, sum, k);
}
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(2^n × n)**
- Same structure as Re-10, but with pruning (early termination when sum > k)
- Worst case: still explores all 2^n subsequences
- **Work per node:** O(n) for list operations

**Space Complexity: O(n)**
- Stack depth: O(n)
- Current list: O(n)

**Interview Note:**
- *"The additional sum parameter and condition check don't change the complexity class. We still explore up to 2^n subsequences in worst case. However, if we detect sum has exceeded target, we can add early pruning to skip that branch entirely."*

---

**Question:** Print only one subsequence (not all) whose sum is K.
**Intuition:** Return boolean. When found, return true and stop further recursion.
**Logic:** Return true when condition satisfied.

**Java:**

```java
boolean printOneSubsequence(int[] arr, int index, List<Integer> current, int sum, int k) {
    if (index == arr.length) {
        if (sum == k) {
            System.out.println(current);
            return true; // Found one, stop
        }
        return false;
    }

    // Pick
    current.add(arr[index]);
    if (printOneSubsequence(arr, index + 1, current, sum + arr[index], k)) {
        return true; // Stop further recursion
    }

    // Not pick
    current.remove(current.size() - 1);
    if (printOneSubsequence(arr, index + 1, current, sum, k)) {
        return true;
    }

    return false;
}
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(2^n)**
- **Best case:** O(1) if first path found has sum = k
- **Worst case:** O(2^n) if answer is the last subsequence or doesn't exist
- **Average case:** Better than Re-11 due to early termination

**Space Complexity: O(n)**
- Stack depth: O(n)
- Current list: O(n)

**Interview Discussion:**
- *"The boolean return value allows us to short-circuit as soon as we find one valid subsequence. The moment we find a valid answer, we return true, which propagates up the recursion stack and stops further exploration. This is significantly faster in practice than printing all subsequences."*

---

## Re-13: Count Subsequences with Sum K

**Question:** Count number of subsequences with sum K (don't print).
**Intuition:** Return count from left subtree + count from right subtree.
**Logic:** Add results of both recursive calls.

**Java:**

```java
int countSubsequences(int[] arr, int index, int sum, int k) {
    if (index == arr.length) {
        return (sum == k) ? 1 : 0;
    }

    // Pick
    int left = countSubsequences(arr, index + 1, sum + arr[index], k);

    // Not pick
    int right = countSubsequences(arr, index + 1, sum, k);

    return left + right;
}
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(2^n)**
- **Total subsequences:** 2^n
- **Work per node:** O(1) (just incrementing count, not printing)
- **Total:** 2^n × O(1) = **O(2^n)**

**Space Complexity: O(n)**
- Stack depth: O(n)
- **No auxiliary list** - we're just counting, not storing

**Interview Note:**
- *"This is more efficient than printing all subsequences because we're not actually building or printing the lists. We just count them. So the work per node is O(1) instead of O(n), giving us O(2^n) instead of O(n × 2^n)."*

---

## Re-14: Combination Sum I

**Question:** Find all unique combinations that sum to target. Elements can be reused.
**Intuition:** For each element, either pick it (and can pick again) or skip it.
**Logic:** Pick same index again OR move to next.

**Java:**

```java
void combinationSum(int[] arr, int index, int target, List<Integer> current, List<List<Integer>> result) {
    if (index == arr.length) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
        }
        return;
    }

    // Pick same element again (if target allows)
    if (arr[index] <= target) {
        current.add(arr[index]);
        combinationSum(arr, index, target - arr[index], current, result);
        current.remove(current.size() - 1);
    }

    // Not pick - move to next
    combinationSum(arr, index + 1, target, current, result);
}
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(2^t) where t = target/min_element**
- **Why not O(2^n)?** Because we can pick the same element multiple times!
- **Recursion depth:** At most t (e.g., if min element is 1, we can pick it 'target' times)
- **Branches per node:** Up to 2 (pick same element again OR skip to next)
- **Example:** arr = [2,3,5], target = 8
  - We can pick 2 up to 4 times (8/2 = 4), so depth can be > n

**Alternative analysis:**
- For each position in recursion tree, we try picking the current element 0, 1, 2, ... times until target is exceeded
- **Worst case:** O(target/min) levels deep
- **Time:** **O(2^(target/min))**

**Space Complexity: O(target/min)**
- **Stack depth:** O(target/min) in worst case
- **Auxiliary list:** O(target/min)

**Interview Discussion:**
- *"Unlike normal subsequence problems which are O(2^n), this problem allows unlimited reuse of elements. The complexity depends on the target and the smallest element. If min element is very small (like 1), the recursion tree can be very deep, much deeper than n."*
- *"For arr=[2,3,6,7], target=7, maximum depth would be 7/2=3 (picking 2 three times gives 6, can't add more 2s)."*

---

## Re-15: Combination Sum II

**Question:** Find combinations that sum to target. Each element used only once. No duplicate combinations.
**Intuition:** Sort array. Skip duplicates at same level of recursion.
**Logic:** Pick current once, move to next. Skip duplicates.

**Java:**

```java
void combinationSum2(int[] arr, int index, int target, List<Integer> current, List<List<Integer>> result) {
    if (target == 0) {
        result.add(new ArrayList<>(current));
        return;
    }

    for (int i = index; i < arr.length; i++) {
        // Skip duplicates at same level
        if (i > index && arr[i] == arr[i - 1]) continue;

        if (arr[i] > target) break;

        current.add(arr[i]);
        combinationSum2(arr, i + 1, target - arr[i], current, result);
        current.remove(current.size() - 1);
    }
}

}
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(2^n × n)**
- **Recursion tree:** Similar to subsequence, but with duplicate handling
- **Total combinations:** At most 2^n (reduced by duplicate skipping)
- **Work per node:** O(n) for list operations
- **Sorting:** O(n log n) preprocessing (done once)
- **Total:** O(n log n) + O(2^n × n) = **O(2^n × n)**

**Space Complexity: O(n)**
- Stack depth: O(n)
- Current list: O(n)

**Interview Discussion:**
- *"We must sort the array first to handle duplicates efficiently. The line `if (i > index && arr[i] == arr[i-1]) continue;` ensures we skip duplicate elements at the same recursion level, preventing duplicate combinations."*
- *"The for-loop approach is different from pick/not-pick. Here, at each level, we try picking different elements starting from index, rather than making a binary pick/not-pick decision."*

---

## Re-16: Subset Sum I

**Question:** Find all possible subset sums.
**Intuition:** Pick/not-pick for each element. Store sum.
**Logic:** Add current element to sum or don't.

**Java:**

```java
void subsetSum(int[] arr, int index, int sum, List<Integer> result) {
    if (index == arr.length) {
        result.add(sum);
        return;
    }

    // Pick
    subsetSum(arr, index + 1, sum + arr[index], result);

    // Not pick
    subsetSum(arr, index + 1, sum, result);
}
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(2^n × n)**
- **Total subsets:** 2^n
- **Storing each sum:** O(1)
- **Sorting result:** O(2^n log 2^n) = O(n × 2^n)
- **Total:** **O(2^n × n)** (dominated by generation + sorting)

**Space Complexity: O(2^n)**
- **Result list:** Stores 2^n sums
- **Recursion stack:** O(n)
- **Total:** O(2^n) + O(n) = **O(2^n)**

**Interview Note:**
- *"This problem doesn't build the actual subsets, just their sums. So the auxiliary space includes the result list with 2^n sums. The recursion stack is still only O(n) deep."*

---

## Re-17: Subset Sum II (All Unique Subsets)

**Question:** Find all unique subsets (no duplicates).
**Intuition:** Sort array. At each level, try picking each unique element.
**Logic:** Skip duplicates at same recursion level.

**Java:**

```java
void subsetsWithDup(int[] arr, int index, List<Integer> current, List<List<Integer>> result) {
    result.add(new ArrayList<>(current));

    for (int i = index; i < arr.length; i++) {
        // Skip duplicates at same level
        if (i > index && arr[i] == arr[i - 1]) continue;

        current.add(arr[i]);
        subsetsWithDup(arr, i + 1, current, result);
        current.remove(current.size() - 1);
    }
}

// Must sort first: Arrays.sort(arr);
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(2^n × n)**
- **Total unique subsets:** At most 2^n
- **Creating copy of each subset:** O(n) on average
- **Sorting:** O(n log n) preprocessing
- **Total:** **O(2^n × n)**

**Space Complexity: O(n)**
- **Recursion stack:** O(n)
- **Current list:** O(n)
- **Note:** Result list is output, not counted in auxiliary space

**Interview Discussion:**
- *"This uses the 'for-loop recursion' pattern. By sorting the array first and using `if (i > index && arr[i] == arr[i-1]) continue`, we skip duplicate values at the same recursion level. This naturally prevents duplicate subsets without needing a HashSet."*
- *"The key insight: if we've already explored starting with arr[i], we don't need to explore again with arr[i] if it has the same value as arr[i-1] at the same recursion depth."*

---

## Re-18: Permutations I (Using Extra Space)

**Question:** Generate all permutations of array.
**Intuition:** For each position, try all unused elements. Use boolean array to track.
**Logic:** Mark element as used, recurse, unmark (backtrack).

**Java:**

```java
void permute(int[] arr, List<Integer> current, boolean[] used, List<List<Integer>> result) {
    if (current.size() == arr.length) {
        result.add(new ArrayList<>(current));
        return;
    }

    for (int i = 0; i < arr.length; i++) {
        if (used[i]) continue;

        current.add(arr[i]);
        used[i] = true;

        permute(arr, current, used, result);

        // Backtrack
        current.remove(current.size() - 1);
        used[i] = false;
    }
}

// Time: O(n! * n), Space: O(n)
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n! × n)**
- **Permutation count:** n! (factorial)
- **Why n!?** First position has n choices, second has n-1, third has n-2, ..., last has 1
  - Total = n × (n-1) × (n-2) × ... × 1 = **n!**
- **Work per permutation:** O(n) to copy the current list to result
- **Total:** n! × O(n) = **O(n! × n)**

**Space Complexity: O(n)**
- **Recursion stack:** O(n) depth
- **Auxiliary space:**
  - `current` list: O(n)
  - `used` boolean array: O(n)
  - **Total auxiliary:** O(n)
- **Note:** Output with n! permutations is not counted

**Recursion Tree Visualization (for n=3):**
```
Level 0:               []
                  /    |    \
              [1]     [2]    [3]
Level 1:     / \     / \    / \
         [1,2][1,3][2,1][2,3][3,1][3,2]
Level 2: Each leads to 1 complete permutation
```
- **Branches reduce at each level:** n, n-1, n-2, ..., 1
- **Total leaf nodes (permutations):** n!

**Interview Discussion:**
- *"Permutation problems have factorial time complexity. Each level of recursion has fewer choices because we can't reuse elements. The `used[]` array tracks which elements have been picked in the current path."*
- *"For n=10, we get 10! = 3.6 million permutations. This grows extremely fast!"*

---

## Re-19: Permutations II (Swap Method - No Extra Space)

**Question:** Generate permutations by swapping elements.
**Intuition:** Fix element at each position by swapping, recurse for remaining.
**Logic:** Swap, recurse, swap back (backtrack).

**Java:**

```java
void permuteSwap(int[] arr, int index, List<List<Integer>> result) {
    if (index == arr.length) {
        List<Integer> current = new ArrayList<>();
        for (int num : arr) current.add(num);
        result.add(current);
        return;
    }

    for (int i = index; i < arr.length; i++) {
        swap(arr, i, index);
        permuteSwap(arr, index + 1, result);
        swap(arr, i, index); // Backtrack
    }
}

void swap(int[] arr, int i, int j) {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
}

// Time: O(n! * n), Space: O(1) excluding recursion
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n! × n)**
- **Permutation count:** n! 
- **Work per permutation:** O(n) to create and add to result list
- **Total:** **O(n! × n)**

**Space Complexity: O(n)** - More space-efficient!
- **Recursion stack:** O(n)
- **Auxiliary space:** O(1) - No `used[]` array or `current` list!
- **Array is modified in-place** through swapping
- **Total:** O(n) from recursion only

**Comparison with Re-18:**
- **Re-18 (Extra Space approach):**
  - Uses `boolean[] used` (O(n)) + `List<Integer> current` (O(n))
  - Cleaner code, easier to understand
- **Re-19 (Swap approach):**
  - Modifies array in-place
  - O(1) auxiliary space (better)
  - Slightly trickier logic

**Interview Discussion:**
- *"The swap method is more space-efficient. We fix elements one by one by swapping. After recursion, we swap back (backtrack) to restore the array state."*
- *"Both approaches have the same time complexity O(n! × n), but the swap method saves space by not using auxiliary data structures."*

---

## Re-20: N-Queens Problem

**Question:** Place N queens on N*N board such that no two queens attack each other.
**Intuition:** Place queen row by row. Check if safe before placing. Backtrack if not possible.
**Logic:** Try each column in current row. Recurse for next row.

**Java:**

```java
void solveNQueens(int n, int row, char[][] board, List<List<String>> result) {
    if (row == n) {
        result.add(construct(board));
        return;
    }

    for (int col = 0; col < n; col++) {
        if (isSafe(board, row, col, n)) {
            board[row][col] = 'Q';
            solveNQueens(n, row + 1, board, result);
            board[row][col] = '.'; // Backtrack
        }
    }
}

boolean isSafe(char[][] board, int row, int col, int n) {
    // Check upper column
    for (int i = 0; i < row; i++) {
        if (board[i][col] == 'Q') return false;
    }

    // Check upper left diagonal
    for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
        if (board[i][j] == 'Q') return false;
    }

    // Check upper right diagonal
    for (int i = row - 1, j = col + 1; i >= 0 && j < n; i--, j++) {
        if (board[i][j] == 'Q') return false;
    }

    return true;
}

// Time: O(n!), Space: O(n²)
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n!)**
- **Upper bound:** O(n!) because we try n positions in first row, at most n-1 valid in second row, etc.
- **Actual complexity:** Better than O(n!) due to pruning with `isSafe()` check
- **isSafe() cost:** O(n) per call (checks column + 2 diagonals)
- **Total:** Approximately **O(n!)** (with significant constant factor reduction from pruning)

**Space Complexity: O(n²)**
- **Board:** O(n²) to store n×n board
- **Recursion stack:** O(n) - maximum n rows
- **Total:** O(n²) + O(n) = **O(n²)**

**Why not O(n^n)?**
- Initially, it seems like each of n rows has n choices → n^n
- **BUT**, `isSafe()` heavily prunes invalid placements
- After placing queens in earlier rows, many positions become invalid
- **Actual branches per row decrease significantly** → closer to factorial

**Interview Discussion:**
- *"N-Queens is a classic backtracking problem. We place queens row by row, checking if each placement is safe (no attacks from previously placed queens). The isSafe() function checks the column and both diagonals."*
- *"For N=8 (standard chessboard), there are only 92 solutions out of 8^8 = 16 million possible placements. This shows how effective the pruning is!"*
- *"We only check upper cells (rows 0 to row-1) because we haven't placed queens in lower rows yet."*

---

## Re-21: Sudoku Solver

**Question:** Solve 9×9 Sudoku puzzle using backtracking.
**Intuition:** Try digits 1-9 in empty cells. Backtrack if invalid.
**Logic:** Fill cell, recurse, if solution found return true.

**Java:**

```java
boolean solveSudoku(char[][] board) {
    for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9; j++) {
            if (board[i][j] == '.') {
                for (char c = '1'; c <= '9'; c++) {
                    if (isValid(board, i, j, c)) {
                        board[i][j] = c;

                        if (solveSudoku(board)) {
                            return true;
                        }

                        board[i][j] = '.'; // Backtrack
                    }
                }
                return false; // No valid digit found
            }
        }
    }
    return true; // All cells filled
}

boolean isValid(char[][] board, int row, int col, char c) {
    for (int i = 0; i < 9; i++) {
        // Check row
        if (board[row][i] == c) return false;

        // Check column
        if (board[i][col] == c) return false;

        // Check 3×3 box
        if (board[3 * (row / 3) + i / 3][3 * (col / 3) + i % 3] == c) return false;
    }
    return true;
}

// Time: O(9^(n²)), Space: O(1)
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(9^m) where m = number of empty cells**
- **For each empty cell:** Try 9 digits (1-9)
- **Empty cells (m):** At most 81 (for standard 9×9 Sudoku)
- **Worst case:** 9^81 ≈ **O(9^(n²))** where n=9
- **Practical:** Much better due to constraint propagation and pruning
- **isValid() cost:** O(9) = O(1) for 9×9 board

**Space Complexity: O(1)** - Impressive!
- **Board modification:** In-place (given as input)
- **Recursion stack:** O(81) = O(1) for constant-sized 9×9 board
- **Auxiliary space:** O(1) - no extra data structures
- **Total:** **O(1)**

**Why such high time complexity?**
- Each empty cell branches up to 9 ways
- For m empty cells: 9 × 9 × 9 × ... (m times) = 9^m
- **However**, early pruning with constraint checking reduces this massively in practice

**Interview Discussion:**
- *"Sudoku is harder than N-Queens because each cell has 9 possible values vs N-Queens where we just decide which column per row. The time complexity is exponential in the number of empty cells."*
- *"The isValid() function checks three constraints: row, column, and 3×3 box. The box check uses the formula `board[3*(row/3) + i/3][3*(col/3) + i%3]` which maps i (0-8) to the 9 cells in the current box."*
- *"Space is O(1) because we modify the board in-place and don't use auxiliary data structures. The recursion depth is bounded by the number of cells (at most 81)."*

---

## Re-22: M-Coloring Problem

**Question:** Color graph with M colors such that no two adjacent nodes have same color.
**Intuition:** Try each color for current node. Check if safe. Recurse for next node.
**Logic:** Backtracking with color array.

**Java:**

```java
boolean graphColoring(List<List<Integer>> graph, int m, int node, int[] color) {
    int n = graph.size();

    if (node == n) {
        return true; // All nodes colored
    }

    for (int c = 1; c <= m; c++) {
        if (isSafeToColor(graph, node, c, color)) {
            color[node] = c;

            if (graphColoring(graph, m, node + 1, color)) {
                return true;
            }

            color[node] = 0; // Backtrack
        }
    }

    return false;
}

boolean isSafeToColor(List<List<Integer>> graph, int node, int c, int[] color) {
    for (int neighbor : graph.get(node)) {
        if (color[neighbor] == c) return false;
    }
    return true;
}

// Time: O(m^n), Space: O(n)
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(m^n)**
- **m:** Number of colors available
- **n:** Number of nodes/vertices in graph
- **Branches per node:** At most m choices (trying each color)
- **Depth:** n (one decision per node)
- **Total:** m^n in worst case
- **Pruning:** `isSafeToColor()` reduces branches significantly

**Space Complexity: O(n)**
- **color[] array:** O(n)
- **Recursion stack:** O(n) depth
- **Total:** O(n)

**Interview Discussion:**
- *"Graph coloring is NP-complete. For each vertex, we try all m colors and check if it's safe (no adjacent vertex has the same color). If we can color all n vertices, we return true."*
- *"Time complexity is O(m^n) because each of n vertices has up to m color choices. This is better than N-Queens (O(n!)) when m < n, but worse when m > n."*
- *"Example: m=3 colors, n=10 vertices → 3^10 = 59,049 possibilities vs 10! = 3.6 million for N-Queens."*

---

## Re-23: Rat in a Maze

**Question:** Find all paths for rat to reach from (0,0) to (n-1,n-1) in maze.
**Intuition:** Try all 4 directions (Down, Left, Right, Up). Mark visited. Backtrack.
**Logic:** DFS with path string. Lexicographical order: DLRU.

**Java:**

```java
void findPaths(int[][] maze, int i, int j, String path, boolean[][] visited, List<String> result) {
    int n = maze.length;

    // Base case
    if (i == n - 1 && j == n - 1) {
        result.add(path);
        return;
    }

    // Mark visited
    visited[i][j] = true;

    // Down
    if (i + 1 < n && maze[i + 1][j] == 1 && !visited[i + 1][j]) {
        findPaths(maze, i + 1, j, path + "D", visited, result);
    }

    // Left
    if (j - 1 >= 0 && maze[i][j - 1] == 1 && !visited[i][j - 1]) {
        findPaths(maze, i, j - 1, path + "L", visited, result);
    }

    // Right
    if (j + 1 < n && maze[i][j + 1] == 1 && !visited[i][j + 1]) {
        findPaths(maze, i, j + 1, path + "R", visited, result);
    }

    // Up
    if (i - 1 >= 0 && maze[i - 1][j] == 1 && !visited[i - 1][j]) {
        findPaths(maze, i - 1, j, path + "U", visited, result);
    }

    // Backtrack
    visited[i][j] = false;
}

// Time: O(4^(n²)), Space: O(n²)
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(4^(n²))** - Worst case
- **Branching factor:** Up to 4 directions (D, L, R, U) at each cell
- **Depth:** At most n×n (visiting all cells)
- **Total paths:** 4^(n²) in absolute worst case
- **Practical:** Much better due to:
  - `visited[][]` prevents revisiting
  - Blocked cells (maze[i][j] == 0) reduce branches
  - Dead ends terminate early

**Space Complexity: O(n²)**
- **visited[][] array:** O(n²)
- **Recursion stack:** O(n²) in worst case (path visits all cells)
- **Path string:** O(n²) length in worst case
- **Total:** O(n²)

**Interview Discussion:**
- *"Rat in a Maze is a grid DFS problem. From each cell, we can move in 4 directions. The visited[][] array prevents cycles. We backtrack by unmarking visited[i][j] = false after exploring each direction."*
- *"Time complexity is theoretically O(4^(n²)) but in practice much better. For a 4×4 maze, worst case would be 4^16 = 4 billion paths, but visited marking ensures each cell is explored at most once per path."*
- *"The lexicographical order (DLRU) is maintained by trying directions in that order: Down, Left, Right, Up."*

---

## Re-24: Word Search

**Question:** Check if word exists in 2D board. Can move up/down/left/right.
**Intuition:** DFS from each cell. Try to match word character by character. Backtrack on mismatch.
**Logic:** Mark visited, try 4 directions, unmark (backtrack).

**Java:**

```java
boolean exist(char[][] board, String word) {
    int m = board.length, n = board[0].length;

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (dfs(board, word, i, j, 0)) {
                return true;
            }
        }
    }

    return false;
}

boolean dfs(char[][] board, String word, int i, int j, int index) {
    if (index == word.length()) return true;

    if (i < 0 || i >= board.length || j < 0 || j >= board[0].length ||
            board[i][j] != word.charAt(index)) {
        return false;
    }

    char temp = board[i][j];
    board[i][j] = '#'; // Mark visited

    boolean found = dfs(board, word, i + 1, j, index + 1) ||
            dfs(board, word, i - 1, j, index + 1) ||
            dfs(board, word, i, j + 1, index + 1) ||
            dfs(board, word, i, j - 1, index + 1);

    board[i][j] = temp; // Backtrack

    return found;
}
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(m × n × 4^k)** where k = word.length()
- **Starting cells:** m × n (we try DFS from every cell)
- **Each DFS:** Branching factor 4 (up/down/left/right), depth k (word length)
- **Total:** **O(m × n × 4^k)**

**Why 4^k?**
- At each character position, we can try up to 4 directions
- For a word of length k, we explore up to 4^k paths from each starting cell
- **Practical:** Much better due to early termination on mismatches

**Space Complexity: O(k)**
- **Recursion stack:** O(k) - depth equals word length
- **Board modification:** In-place (marking with '#'), no extra space
- **Total:** O(k)

**Interview Discussion:**
- *"Word Search uses DFS with backtracking. We try starting from each cell, attempting to match the word character by character. We mark cells with '#' to avoid revisiting in the current path, then restore the original character when backtracking."*
- *"The complexity depends on word length (k), not board size for depth. We try all m×n starting positions, and from each, we explore up to 4^k paths."*
- *"The in-place marking technique (using '#') saves space compared to maintaining a separate visited[][] array. This brings space down to O(k) for just the recursion stack."*
- *"For a 4×4 board and word length 7: we try 16 starting cells, each can branch 4^7 = 16,384 ways in worst case."*

---

## Re-25: Palindrome Partitioning

**Question:** Partition string into substrings where each substring is palindrome.
**Intuition:** Try partitioning at each position. Check if substring is palindrome. Recurse for remaining.
**Logic:** Backtracking with partition list.

**Java:**

```java
void partition(String s, int index, List<String> current, List<List<String>> result) {
    if (index == s.length()) {
        result.add(new ArrayList<>(current));
        return;
    }

    for (int i = index; i < s.length(); i++) {
        if (isPalindrome(s, index, i)) {
            current.add(s.substring(index, i + 1));
            partition(s, i + 1, current, result);
            current.remove(current.size() - 1); // Backtrack
        }
    }
}

boolean isPalindrome(String s, int left, int right) {
    while (left < right) {
        if (s.charAt(left++) != s.charAt(right--)) {
            return false;
        }
    }
    return true;
}

// Example: s = "aab"
// Output: [["a","a","b"], ["aa","b"]]
```

### ⏱️ Time & Space Complexity Analysis

**Time Complexity: O(n × 2^n)**
- **Partitioning possibilities:** 2^(n-1) ways to partition string of length n
  - **Why?** Between each adjacent pair of characters, we decide: partition or not
  - For n characters, there are n-1 gaps → 2^(n-1) ≈ 2^n ways
- **Palindrome check:** O(n) for each partition
- **Total:** **O(n × 2^n)**

**Alternative analysis:**
- At each position i, we try all possible partitions from i to end: O(n) choices
- Recursion depth: O(n) levels
- **Recursion tree nodes:** Can have O(2^n) nodes
- **Work per node:** O(n) for palindrome check and substring operations
- **Total:** **O(n × 2^n)**

**Space Complexity: O(n)**
- **Recursion stack:** O(n) depth (maximum n recursive calls)
- **Current partition list:** O(n) strings at most
- **Total:** O(n)

**Optimization possibility:**
- We can precompute all palindrome substrings using DP in O(n²) time and O(n²) space
- This would reduce isPalindrome from O(n) to O(1)
- **Optimized time:** O(n²) preprocessing + O(2^n) generation = **O(2^n)** (asymptotically better)

**Interview Discussion:**
- *"Palindrome Partitioning explores all possible ways to partition the string. At each position, we try making a cut if the substring up to that point is a palindrome. This forms a decision tree with approximately 2^n partitioning possibilities."*
- *"The isPalindrome() check is O(n) in worst case, done for each partition attempt. We could precompute a boolean DP table isPal[i][j] in O(n²) time to reduce palindrome checks to O(1)."*
- *"For string 'aab' (n=3), we have 2^(3-1) = 4 theoretical partitions: ['a','a','b'], ['a','ab'], ['aa','b'], ['aab']. We only add those where ALL parts are palindromes, which gives us 2 valid partitions."*

---

## Recursion & Backtracking Patterns

### Pattern 1: Single Recursion (Linear)

**Examples:** Print 1 to N, Factorial, Sum of N
**Template:**

```java
void linear(int n) {
    if (n == 0) return;
    // Process
    linear(n - 1);
}
```

### Pattern 2: Multiple Recursion

**Examples:** Fibonacci, Tower of Hanoi
**Template:**

```java
int multiple(int n) {
    if (n <= 1) return n;
    return multiple(n - 1) + multiple(n - 2);
}
```

### Pattern 3: Pick/Not-Pick (Subsequence)

**Examples:** All subsequences, Subset sum
**Template:**

```java
void pickNotPick(int[] arr, int index, List<Integer> current) {
    if (index == arr.length) {
        // Process current
        return;
    }

    // Pick
    current.add(arr[index]);
    pickNotPick(arr, index + 1, current);

    // Not pick - Backtrack
    current.remove(current.size() - 1);
    pickNotPick(arr, index + 1, current);
}
```

### Pattern 4: Loop Recursion (Combination)

**Examples:** Combination Sum II, Subsets II
**Template:**

```java
void loopRecursion(int[] arr, int index, List<Integer> current, List<List<Integer>> result) {
    result.add(new ArrayList<>(current));

    for (int i = index; i < arr.length; i++) {
        if (i > index && arr[i] == arr[i - 1]) continue; // Skip duplicates

        current.add(arr[i]);
        loopRecursion(arr, i + 1, current, result);
        current.remove(current.size() - 1); // Backtrack
    }
}
```

### Pattern 5: Permutation

**Examples:** All permutations, N-Queens
**Template:**

```java
void permute(int[] arr, int index) {
    if (index == arr.length) {
        // Process
        return;
    }

    for (int i = index; i < arr.length; i++) {
        swap(arr, i, index);
        permute(arr, index + 1);
        swap(arr, i, index); // Backtrack
    }
}
```

### Pattern 6: Grid Traversal (4 Directions)

**Examples:** Rat in maze, Word search
**Template:**

```java
boolean dfs(int[][] grid, int i, int j, boolean[][] visited) {
    if (baseCondition) return true;

    visited[i][j] = true;

    // Try 4 directions
    int[] dx = {1, 0, -1, 0};
    int[] dy = {0, 1, 0, -1};

    for (int k = 0; k < 4; k++) {
        int ni = i + dx[k];
        int nj = j + dy[k];

        if (isValid(ni, nj) && !visited[ni][nj]) {
            if (dfs(grid, ni, nj, visited)) return true;
        }
    }

    visited[i][j] = false; // Backtrack
    return false;
}
```

---

## Time Complexity Analysis


| Pattern          | Time Complexity | Example          |
| ---------------- | --------------- | ---------------- |
| Linear Recursion | O(n)            | Print 1 to N     |
| Binary Recursion | O(2^n)          | Fibonacci        |
| Subsequences     | O(2^n)          | All subsets      |
| Permutations     | O(n! * n)       | All permutations |
| Combinations     | O(2^n * k)      | Combination Sum  |
| N-Queens         | O(n!)           | N-Queens         |
| Sudoku           | O(9^(n²))       | Sudoku Solver    |
| Graph Coloring   | O(m^n)          | M-Coloring       |

---

## Backtracking Key Points

1. **What is Backtracking?**

 - Try all possibilities
 - If solution doesn't work, undo (backtrack) and try another
2. **Backtracking Template:**

```java
void backtrack(state, choices) {
    if (isComplete(state)) {
        addSolution(state);
        return;
    }

    for (choice in choices) {
        if (isValid(choice)) {
            makeChoice(choice); // Choose
            backtrack(newState); // Explore
            undoChoice(choice); // Unchoose (Backtrack)
        }
    }
}
```

3. **Common Backtracking Problems:**

 - Combination/Permutation problems
 - Sudoku/N-Queens
 - Graph coloring
 - Maze problems
 - Subset problems
4. **When to Use Backtracking:**

 - Need to find ALL solutions
 - Decision tree exploration
 - Constraint satisfaction problems

---

## Common Mistakes to Avoid

1. **Forgetting Base Case:**

 - Always have termination condition
 - Causes stack overflow
2. **Not Backtracking Properly:**

 - Undo changes after recursive call
 - Remove from list, unmark visited, etc.
3. **Reference vs Value:**

```java
// WRONG: Adds reference (all lists same)
result.add(current);

// RIGHT: Adds copy
result.add(new ArrayList<>(current));
```

4. **Stack Overflow:**

 - Deep recursion (n > 10000)
 - Use iterative approach or tail recursion
5. **Time Limit Exceeded:**

 - Exponential time complexity
 - Use pruning/memoization

---
