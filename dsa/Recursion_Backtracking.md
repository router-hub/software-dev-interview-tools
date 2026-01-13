## Re-1: Introduction to Recursion | Recursion Tree | Stack Space

**Question:** What is recursion? How does it work internally?
**Intuition:** A function that calls itself to solve smaller instances of same problem. Base case stops recursion. Stack stores function calls.
**Logic:** Break problem into smaller subproblems â†’ Solve recursively â†’ Combine results.

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
//       f(3)
//      /
//    f(2)
//   /
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
// Time: O(n), Space: O(n) stack space
```

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
    System.out.print(i + " ");     // Print during backtrack
}

// Call: printNTo1Backtrack(1, 5);
// Output: 5 4 3 2 1
```

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

// Call: sumParameterized(5, 0); â†’ 15
// Call: sumFunctional(5); â†’ 15
// Time: O(n), Space: O(n)
```

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

// Time: O(n), Space: O(n)
```

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

// Call: isPalindrome("madam", 0, 4); â†’ true
```

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

// Time: O(2^n) - exponential (bad!)
// Space: O(n) - recursion depth

// Optimized with memoization:
int fibMemo(int n, int[] dp) {
    if (n <= 1) return n;
    if (dp[n] != -1) return dp[n];

    return dp[n] = fibMemo(n - 1, dp) + fibMemo(n - 2, dp);
}
// Time: O(n), Space: O(n)
```

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

// Time: O(2^n * n), Space: O(n) depth
```

**Key Pattern:** Pick/Not-Pick recursion tree

```
                    f(0, [])
                   /                    Pick 3          Not Pick
             f(1, [3])      f(1, [])
            /      \         /             Pick 1   Not Pick  Pick 1  Not Pick
       f(2,[3,1]) f(2,[3]) f(2,[1]) f(2,[])
```

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

---

## Re-12: Print Only One Subsequence with Sum K

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

// Time: O(2^n), Space: O(n)
```

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

// Example: arr = [2,3,6,7], target = 7
// Output: [[2,2,3], [7]]
// Time: O(2^t) where t = target/min
```

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

// Must sort array first: Arrays.sort(arr);
// Example: arr = [1,1,2,5,6,7,10], target = 8
// Output: [[1,1,6], [1,2,5], [1,7], [2,6]]
```

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

// Sort result at the end
// Time: O(2^n), Space: O(2^n)
```

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

// Time: O(n!), Space: O(nÂ²)
```

---

## Re-21: Sudoku Solver

**Question:** Solve 9Ã—9 Sudoku puzzle using backtracking.
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

        // Check 3Ã—3 box
        if (board[3 * (row / 3) + i / 3][3 * (col / 3) + i % 3] == c) return false;
    }
    return true;
}

// Time: O(9^(nÂ²)), Space: O(1)
```

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

// Time: O(4^(nÂ²)), Space: O(nÂ²)
```

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
// Time: O(n * 2^n), Space: O(n)
```

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
        if (i > index && arr[i] == arr[i-1]) continue; // Skip duplicates

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
| Sudoku           | O(9^(nÂ²))    | Sudoku Solver    |
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
            makeChoice(choice);     // Choose
            backtrack(newState);    // Explore
            undoChoice(choice);     // Unchoose (Backtrack)
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
