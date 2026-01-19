
## Table of Contents
- **Part 1: 1D DP (DP 1-7)** - Foundations
- **Part 2: 2D/3D DP on Grids (DP 8-13)** - Grid Problems  
- **Part 3: DP on Subsequences (DP 14-24)** - Pick/Not-Pick Pattern
- **Part 4: DP on Strings (DP 25-34)** - LCS Family
- **Part 5: DP on Stocks (DP 35-40)** - Buy-Sell Problems
- **Part 6: DP on LIS (DP 41-47)** - Longest Increasing Subsequence
- **Part 7: MCM DP / Partition DP (DP 48-54)** - Matrix Chain Multiplication

---

# Part 1: 1D DP (Foundations)

## DP-1: Introduction to Dynamic Programming
**Question:** What is Dynamic Programming? When should we use DP?  
**Intuition:** Dynamic Programming is an optimization technique that stores solutions to subproblems to avoid recomputation. Two key conditions: (1) **Optimal Substructure** - optimal solution contains optimal solutions of subproblems, (2) **Overlapping Subproblems** - same subproblems solved multiple times. Think "recursion with memory". DP trades space for time!  
**Logic:** Two main approaches: **Memoization** (Top-Down - recursion with cache) and **Tabulation** (Bottom-Up - iterative with table). Memoization is easier to code, Tabulation is more efficient.  
**Java:**
```java
// Memoization Template
int solveMemo(int n, int[] dp) {
    if (baseCase) return baseValue;
    if (dp[n] != -1) return dp[n];

    // Compute and store
    return dp[n] = recursiveCall(...);
}

// Tabulation Template
int solveTab(int n) {
    int[] dp = new int[n+1];
    dp[0] = baseValue;

    for (int i = 1; i <= n; i++) {
        dp[i] = compute(dp[i-1], ...);
    }
    return dp[n];
}
```

---

## DP-2: Climbing Stairs / Fibonacci Number
**Question:** Calculate nth Fibonacci number. f(n) = f(n-1) + f(n-2), with f(0) = 0, f(1) = 1.  
**Intuition:** Naive recursion has exponential time O(2^n) because same values computed repeatedly (massive overlapping subproblems). By storing computed values, we reduce to O(n). This is THE fundamental DP example showing how DP works!  
**Logic:** Use memoization to cache results, or tabulation to build from base. Can optimize space to O(1) since only last two values needed.  
**Java:**
```java
// Memoization - O(n) time, O(n) space
int fibMemo(int n, int[] dp) {
    if (n <= 1) return n;
    if (dp[n] != -1) return dp[n];
    return dp[n] = fibMemo(n-1, dp) + fibMemo(n-2, dp);
}

// Tabulation - O(n) time, O(n) space
int fibTab(int n) {
    if (n <= 1) return n;
    int[] dp = new int[n+1];
    dp[0] = 0; dp[1] = 1;
    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i-1] + dp[i-2];
    }
    return dp[n];
}

// Space Optimized - O(n) time, O(1) space â­
int fibOptimized(int n) {
    if (n <= 1) return n;
    int prev2 = 0, prev1 = 1;
    for (int i = 2; i <= n; i++) {
        int curr = prev1 + prev2;
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
```

---

## DP-3: Frog Jump (Min Cost)
**Question:** Frog at stone i can jump to i+1 or i+2. Energy cost = |height[i] - height[j]|. Find minimum cost to reach last stone.  
**Intuition:** At each stone, frog has 2 choices - jump 1 step or 2 steps. We want minimum cost path. Classic 1D DP where state depends on previous states. Without DP, recursive solution recomputes same stones many times through different paths (overlapping subproblems).  
**Logic:** dp[i] = minimum cost to reach stone i = min(dp[i-1] + cost1, dp[i-2] + cost2). Base case: dp[0] = 0 (already at first stone).  
**Java:**
```java
// Memoization
int frogJumpMemo(int[] height, int n, int[] dp) {
    if (n == 0) return 0;
    if (dp[n] != -1) return dp[n];

    int jumpOne = frogJumpMemo(height, n-1, dp) + Math.abs(height[n] - height[n-1]);
    int jumpTwo = Integer.MAX_VALUE;
    if (n > 1) {
        jumpTwo = frogJumpMemo(height, n-2, dp) + Math.abs(height[n] - height[n-2]);
    }

    return dp[n] = Math.min(jumpOne, jumpTwo);
}

// Space Optimized â­
int frogJump(int[] height) {
    int n = height.length;
    int prev2 = 0, prev1 = 0;

    for (int i = 1; i < n; i++) {
        int jumpOne = prev1 + Math.abs(height[i] - height[i-1]);
        int jumpTwo = Integer.MAX_VALUE;
        if (i > 1) {
            jumpTwo = prev2 + Math.abs(height[i] - height[i-2]);
        }
        int curr = Math.min(jumpOne, jumpTwo);
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
// Time: O(n), Space: O(1)
```

---

## DP-4: Frog Jump with K Distance
**Question:** Frog can jump from stone i to any stone from i+1 to i+k. Find minimum cost.  
**Intuition:** Generalization of previous problem. Now try all possible jumps (1 to k) instead of just 2. For each stone, check all reachable previous stones within k distance and pick minimum cost option.  
**Logic:** dp[i] = min(dp[i-j] + |height[i] - height[i-j]|) for all j from 1 to min(k, i).  
**Java:**
```java
int frogJumpK(int[] height, int k) {
    int n = height.length;
    int[] dp = new int[n];
    dp[0] = 0;

    for (int i = 1; i < n; i++) {
        int minCost = Integer.MAX_VALUE;

        for (int j = 1; j <= k && i - j >= 0; j++) {
            int cost = dp[i-j] + Math.abs(height[i] - height[i-j]);
            minCost = Math.min(minCost, cost);
        }

        dp[i] = minCost;
    }

    return dp[n-1];
}
// Time: O(n*k), Space: O(n)
```

---

## DP-5: Maximum Sum of Non-Adjacent Elements (House Robber)
**Question:** Find maximum sum of subsequence where no two elements are adjacent.  
**Intuition:** At each house, robber has 2 choices: (1) **Rob it** - can't rob previous, so add to dp[i-2], (2) **Skip it** - carry forward dp[i-1]. Pick maximum of both. This is the fundamental **pick/not-pick pattern** that appears in countless DP problems! Imagine robbing houses on a street - can't rob adjacent houses or alarm goes off.  
**Logic:** dp[i] = max(arr[i] + dp[i-2], dp[i-1]). Base cases: dp[0] = arr[0], dp[1] = max(arr[0], arr[1]).  
**Java:**
```java
// Tabulation
int maxSumNonAdjacent(int[] arr) {
    int n = arr.length;
    if (n == 1) return arr[0];

    int[] dp = new int[n];
    dp[0] = arr[0];
    dp[1] = Math.max(arr[0], arr[1]);

    for (int i = 2; i < n; i++) {
        int pick = arr[i] + dp[i-2];
        int notPick = dp[i-1];
        dp[i] = Math.max(pick, notPick);
    }

    return dp[n-1];
}

// Space Optimized â­
int maxSumOptimized(int[] arr) {
    int n = arr.length;
    if (n == 1) return arr[0];

    int prev2 = arr[0];
    int prev1 = Math.max(arr[0], arr[1]);

    for (int i = 2; i < n; i++) {
        int pick = arr[i] + prev2;
        int notPick = prev1;
        int curr = Math.max(pick, notPick);
        prev2 = prev1;
        prev1 = curr;
    }

    return prev1;
}
// Time: O(n), Space: O(1)
```

---

## DP-6: House Robber II (Circular Array)
**Question:** Same as House Robber but houses arranged in circle, so first and last houses are adjacent.  
**Intuition:** Brilliant constraint! Since circular, we can't rob both first and last house. Key observation: solve **two separate cases** - (1) consider houses 0 to n-2 (exclude last), (2) consider houses 1 to n-1 (exclude first). Take maximum. This converts circular problem into two linear subproblems!  
**Logic:** Apply House Robber I on two subarrays: [0...n-2] and [1...n-1], return max.  
**Java:**
```java
int rob(int[] nums) {
    int n = nums.length;
    if (n == 1) return nums[0];
    if (n == 2) return Math.max(nums[0], nums[1]);

    int case1 = robLinear(nums, 0, n-2);
    int case2 = robLinear(nums, 1, n-1);

    return Math.max(case1, case2);
}

int robLinear(int[] nums, int start, int end) {
    int prev2 = 0, prev1 = 0;

    for (int i = start; i <= end; i++) {
        int curr = Math.max(nums[i] + prev2, prev1);
        prev2 = prev1;
        prev1 = curr;
    }

    return prev1;
}
// Time: O(n), Space: O(1)
```

---

## DP-7: Ninja's Training (2D DP Introduction)
**Question:** N days, 3 tasks per day with points. Can't do same task on consecutive days. Maximize total points.  
**Intuition:** This introduces **2D DP**! State now has TWO dimensions: (day, lastTask). For each day and each possible last task performed, we try all OTHER tasks and pick maximum. The constraint (no consecutive same tasks) is elegantly handled by tracking what was done last.  
**Logic:** dp[day][last] = max points achievable on 'day' when last task was 'last'. For each task != last: dp[day][last] = points[day][task] + dp[day-1][task].  
**Java:**
```java
// Memoization
int ninjaTraining(int[][] points, int day, int last, int[][] dp) {
    if (day == 0) {
        int max = 0;
        for (int task = 0; task < 3; task++) {
            if (task != last) {
                max = Math.max(max, points[0][task]);
            }
        }
        return max;
    }

    if (dp[day][last] != -1) return dp[day][last];

    int max = 0;
    for (int task = 0; task < 3; task++) {
        if (task != last) {
            int point = points[day][task] + ninjaTraining(points, day-1, task, dp);
            max = Math.max(max, point);
        }
    }

    return dp[day][last] = max;
}

// Space Optimized â­
int ninjaTrainingOptimized(int[][] points) {
    int n = points.length;
    int[] prev = new int[4]; // 4 states: task 0, 1, 2, or 3 (no restriction)

    // Base case - day 0
    prev[0] = Math.max(points[0][1], points[0][2]);
    prev[1] = Math.max(points[0][0], points[0][2]);
    prev[2] = Math.max(points[0][0], points[0][1]);
    prev[3] = Math.max(points[0][0], Math.max(points[0][1], points[0][2]));

    for (int day = 1; day < n; day++) {
        int[] curr = new int[4];

        for (int last = 0; last < 4; last++) {
            for (int task = 0; task < 3; task++) {
                if (task != last) {
                    curr[last] = Math.max(curr[last], points[day][task] + prev[task]);
                }
            }
        }

        prev = curr;
    }

    return prev[3]; // No restriction on last day
}
// Time: O(n*4*3) = O(n), Space: O(1)
```

---

# Part 2: 2D/3D DP on Grids

## DP-8: Unique Paths (Grid Navigation)
**Question:** Robot at (0,0) wants to reach (m-1,n-1) in mÃ—n grid. Can only move right or down. Count number of unique paths.  
**Intuition:** Classic grid DP! To reach any cell (i,j), robot MUST come from either (i-1,j) [from top] or (i,j-1) [from left]. Total paths to (i,j) = sum of paths from both directions. This builds solution from top-left to bottom-right systematically.  
**Logic:** dp[i][j] = dp[i-1][j] + dp[i][j-1]. Base cases: dp[0][j] = 1 (only one way along top row), dp[i][0] = 1 (only one way along left column).  
**Java:**
```java
// Tabulation
int uniquePaths(int m, int n) {
    int[][] dp = new int[m][n];

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (i == 0 && j == 0) {
                dp[i][j] = 1;
            } else {
                int up = (i > 0) ? dp[i-1][j] : 0;
                int left = (j > 0) ? dp[i][j-1] : 0;
                dp[i][j] = up + left;
            }
        }
    }

    return dp[m-1][n-1];
}

// Space Optimized â­
int uniquePathsOptimized(int m, int n) {
    int[] prev = new int[n];

    for (int i = 0; i < m; i++) {
        int[] curr = new int[n];
        for (int j = 0; j < n; j++) {
            if (i == 0 && j == 0) {
                curr[j] = 1;
            } else {
                int up = (i > 0) ? prev[j] : 0;
                int left = (j > 0) ? curr[j-1] : 0;
                curr[j] = up + left;
            }
        }
        prev = curr;
    }

    return prev[n-1];
}
// Time: O(m*n), Space: O(n)
```

---

## DP-9: Unique Paths II (With Obstacles)
**Question:** Same as Unique Paths but some cells have obstacles (marked as 1). Robot can't pass through obstacles.  
**Intuition:** Natural extension! If cell has obstacle, paths through it = 0 (blocked). Otherwise apply same formula. This shows how **constraints modify DP transitions** without changing overall structure. Obstacle handling is key!  
**Logic:** If grid[i][j] == 1, dp[i][j] = 0. Else dp[i][j] = dp[i-1][j] + dp[i][j-1].  
**Java:**
```java
int uniquePathsWithObstacles(int[][] grid) {
    int m = grid.length, n = grid[0].length;

    if (grid[0][0] == 1 || grid[m-1][n-1] == 1) return 0;

    int[] prev = new int[n];

    for (int i = 0; i < m; i++) {
        int[] curr = new int[n];
        for (int j = 0; j < n; j++) {
            if (grid[i][j] == 1) {
                curr[j] = 0; // Obstacle!
            } else if (i == 0 && j == 0) {
                curr[j] = 1;
            } else {
                int up = (i > 0) ? prev[j] : 0;
                int left = (j > 0) ? curr[j-1] : 0;
                curr[j] = up + left;
            }
        }
        prev = curr;
    }

    return prev[n-1];
}
// Time: O(m*n), Space: O(n)
```

---

## DP-10: Minimum Path Sum
**Question:** Grid with non-negative numbers. Find path from top-left to bottom-right with minimum sum. Can only move right or down.  
**Intuition:** Instead of counting paths, we **optimize path value**! At each cell, minimum sum to reach it = current cell value + minimum of (sum from top, sum from left). This demonstrates how grid DP handles optimization problems.  
**Logic:** dp[i][j] = grid[i][j] + min(dp[i-1][j], dp[i][j-1]). Base case: dp[0][0] = grid[0][0].  
**Java:**
```java
int minPathSum(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    int[] prev = new int[n];

    for (int i = 0; i < m; i++) {
        int[] curr = new int[n];
        for (int j = 0; j < n; j++) {
            if (i == 0 && j == 0) {
                curr[j] = grid[i][j];
            } else {
                int up = (i > 0) ? prev[j] : Integer.MAX_VALUE;
                int left = (j > 0) ? curr[j-1] : Integer.MAX_VALUE;
                curr[j] = grid[i][j] + Math.min(up, left);
            }
        }
        prev = curr;
    }

    return prev[n-1];
}
// Time: O(m*n), Space: O(n)
```

---

## DP-11: Triangle (Minimum Path Sum)
**Question:** Given triangle array, find minimum path sum from top to bottom. Can move to adjacent numbers on next row: from (i,j) can go to (i+1,j) or (i+1,j+1).  
**Intuition:** Different grid structure but same DP principle! Key insight: **start from bottom and work upward**! This avoids index boundary complexity. From position (i,j), two choices for next row. Working bottom-up makes code cleaner.  
**Logic:** dp[i][j] = triangle[i][j] + min(dp[i+1][j], dp[i+1][j+1]). Base case: last row values are themselves.  
**Java:**
```java
int minimumTotal(List<List<Integer>> triangle) {
    int n = triangle.size();
    int[] next = new int[n];

    // Base case - last row
    for (int j = 0; j < n; j++) {
        next[j] = triangle.get(n-1).get(j);
    }

    // Move upward from second-last row
    for (int i = n-2; i >= 0; i--) {
        int[] curr = new int[n];
        for (int j = 0; j <= i; j++) {
            int down = next[j];
            int diagonal = next[j+1];
            curr[j] = triangle.get(i).get(j) + Math.min(down, diagonal);
        }
        next = curr;
    }

    return next[0];
}
// Time: O(nÂ²), Space: O(n)
```

---

## DP-12: Maximum/Minimum Falling Path Sum
**Question:** Find minimum sum path from first row to last row of matrix. Can move to 3 adjacent cells in next row (down, down-left, down-right).  
**Intuition:** Extension of grid DP with **3 transitions** instead of 2! At each cell, can come from 3 cells above. Check all 3, take minimum. This shows how increasing transitions affects DP formulation. Must handle boundary conditions carefully!  
**Logic:** dp[i][j] = matrix[i][j] + min(dp[i+1][j-1], dp[i+1][j], dp[i+1][j+1]). Handle edges where j-1 or j+1 out of bounds.  
**Java:**
```java
int minFallingPathSum(int[][] matrix) {
    int n = matrix.length;
    int[] prev = matrix[n-1].clone();

    for (int i = n-2; i >= 0; i--) {
        int[] curr = new int[n];
        for (int j = 0; j < n; j++) {
            int down = prev[j];
            int leftDiag = (j > 0) ? prev[j-1] : Integer.MAX_VALUE;
            int rightDiag = (j < n-1) ? prev[j+1] : Integer.MAX_VALUE;

            curr[j] = matrix[i][j] + Math.min(down, Math.min(leftDiag, rightDiag));
        }
        prev = curr;
    }

    // Answer is minimum in first row after processing
    int min = prev[0];
    for (int j = 1; j < n; j++) {
        min = Math.min(min, prev[j]);
    }

    return min;
}
// Time: O(nÂ²), Space: O(n)
```

---

## DP-13: Chocolate Pickup / Cherry Pickup (3D DP)
**Question:** Two robots start from top corners of grid (0,0) and (0,m-1), both move down collecting chocolates. If at same cell, count chocolate once. Both must reach bottom. Maximize total chocolates collected.  
**Intuition:** This is **3D DP**! State has 3 dimensions: (row, col1, col2) where col1 = first robot's column, col2 = second robot's column. Both robots move simultaneously row by row. Each robot has 3 movement choices (stay, left-diagonal, right-diagonal), giving 3Ã—3 = 9 total combinations per state. Key trick: when j1 == j2, add chocolate value only once!  
**Logic:** dp[i][j1][j2] = chocolates[i][j1] + chocolates[i][j2] (if j1 != j2, else add once) + max of all 9 movement combinations from next row.  
**Java:**
```java
int cherryPickup(int[][] grid) {
    int n = grid.length, m = grid[0].length;
    int[][] prev = new int[m][m];

    // Base case - last row
    for (int j1 = 0; j1 < m; j1++) {
        for (int j2 = 0; j2 < m; j2++) {
            if (j1 == j2) {
                prev[j1][j2] = grid[n-1][j1];
            } else {
                prev[j1][j2] = grid[n-1][j1] + grid[n-1][j2];
            }
        }
    }

    for (int i = n-2; i >= 0; i--) {
        int[][] curr = new int[m][m];

        for (int j1 = 0; j1 < m; j1++) {
            for (int j2 = 0; j2 < m; j2++) {
                int max = Integer.MIN_VALUE;

                // Try all 9 combinations (3 moves Ã— 3 moves)
                for (int dj1 = -1; dj1 <= 1; dj1++) {
                    for (int dj2 = -1; dj2 <= 1; dj2++) {
                        int value = 0;

                        if (j1 == j2) {
                            value = grid[i][j1];
                        } else {
                            value = grid[i][j1] + grid[i][j2];
                        }

                        if (j1+dj1 >= 0 && j1+dj1 < m && j2+dj2 >= 0 && j2+dj2 < m) {
                            value += prev[j1+dj1][j2+dj2];
                        } else {
                            value = Integer.MIN_VALUE;
                        }

                        max = Math.max(max, value);
                    }
                }

                curr[j1][j2] = max;
            }
        }

        prev = curr;
    }

    return prev[0][m-1]; // Both robots start from top corners
}
// Time: O(n*mÂ²*9), Space: O(mÂ²)
```

---

# Part 3: DP on Subsequences (Pick/Not-Pick Pattern)

## DP-14: Subset Sum Equals K
**Question:** Given array and target K, check if subset exists with sum = K.  
**Intuition:** Classic **pick/not-pick pattern** - foundation for many subset problems! For each element, two choices: (1) Include it - check if remaining sum achievable, (2) Exclude it - check if sum achievable without it. This explores all possible subsets systematically.  
**Logic:** dp[i][sum] = true if sum achievable using first i elements. dp[i][sum] = dp[i-1][sum] (not take) || dp[i-1][sum-arr[i]] (take).  
**Java:**
```java
boolean subsetSum(int[] arr, int k) {
    int n = arr.length;
    boolean[] prev = new boolean[k+1];

    prev[0] = true; // sum 0 always achievable (empty subset)
    if (arr[0] <= k) prev[arr[0]] = true;

    for (int i = 1; i < n; i++) {
        boolean[] curr = new boolean[k+1];
        curr[0] = true;

        for (int target = 1; target <= k; target++) {
            boolean notTake = prev[target];
            boolean take = false;
            if (target >= arr[i]) {
                take = prev[target - arr[i]];
            }
            curr[target] = take || notTake;
        }
        prev = curr;
    }

    return prev[k];
}
// Time: O(n*k), Space: O(k)
```

---

## DP-15: Partition Equal Subset Sum
**Question:** Check if array can be partitioned into two subsets with equal sum.  
**Intuition:** Brilliant reduction! If total sum is odd, impossible (can't divide odd into two equal integers). If even, problem reduces to: "Does subset with sum = total/2 exist?" Uses DP-14!  
**Logic:** Calculate total. If odd, return false. Else check subset sum with target = total/2.  
**Java:**
```java
boolean canPartition(int[] arr) {
    int total = 0;
    for (int num : arr) total += num;

    if (total % 2 != 0) return false;

    return subsetSum(arr, total / 2);
}
```

---

## DP-16: Partition with Minimum Difference
**Question:** Partition array into two subsets such that absolute difference of their sums is minimized.  
**Intuition:** Mathematical insight! If S1 is one subset sum, S2 = total - S1. We want min|S1 - S2| = |2*S1 - total|. Find all possible S1 values using subset sum DP, then find S1 closest to total/2.  
**Logic:** Use subset sum to find all achievable sums. For each valid S1, calculate |S1 - S2|, return minimum.  
**Java:**
```java
int minimumDifference(int[] arr) {
    int total = 0;
    for (int num : arr) total += num;

    boolean[] dp = new boolean[total+1];
    dp[0] = true;

    for (int num : arr) {
        for (int sum = total; sum >= num; sum--) {
            dp[sum] = dp[sum] || dp[sum - num];
        }
    }

    int minDiff = Integer.MAX_VALUE;
    for (int s1 = 0; s1 <= total/2; s1++) {
        if (dp[s1]) {
            int s2 = total - s1;
            minDiff = Math.min(minDiff, Math.abs(s1 - s2));
        }
    }

    return minDiff;
}
```

---

## DP-17: Count Subsets with Sum K
**Question:** Count number of subsets with sum equal to K.  
**Intuition:** Variation where we COUNT instead of checking existence. Use integer DP instead of boolean to accumulate counts. For each element, total count = count(including it) + count(excluding it).  
**Logic:** dp[i][sum] = count of subsets. dp[i][sum] = dp[i-1][sum] + dp[i-1][sum-arr[i]].  
**Java:**
```java
int countSubsets(int[] arr, int k) {
    int n = arr.length;
    int[] prev = new int[k+1];

    prev[0] = 1; // one way: empty subset
    if (arr[0] <= k) prev[arr[0]] += 1;

    for (int i = 1; i < n; i++) {
        int[] curr = new int[k+1];
        curr[0] = 1;

        for (int sum = 0; sum <= k; sum++) {
            int notTake = prev[sum];
            int take = 0;
            if (sum >= arr[i]) {
                take = prev[sum - arr[i]];
            }
            curr[sum] = notTake + take;
        }
        prev = curr;
    }

    return prev[k];
}
```

---

## DP-18: Count Partitions with Given Difference
**Question:** Count ways to partition into two subsets S1, S2 such that S1 - S2 = D.  
**Intuition:** Algebra magic! Given S1 + S2 = total and S1 - S2 = D. Solving: S1 = (total + D)/2. Problem reduces to counting subsets with sum = (total + D)/2!  
**Logic:** Calculate target = (total + D)/2. Use DP-17.  
**Java:**
```java
int countPartitions(int[] arr, int d) {
    int total = 0;
    for (int num : arr) total += num;

    if ((total + d) % 2 != 0 || total + d < 0) return 0;

    int target = (total + d) / 2;
    return countSubsets(arr, target);
}
```

---

## DP-19: 0/1 Knapsack â­â­â­
**Question:** Given weights and values of items, knapsack capacity W. Maximize value without exceeding capacity. Each item used at most once.  
**Intuition:** THE most important DP problem! Foundation of countless variations. For each item: (1) Take it - add value, reduce capacity by weight, (2) Skip it - keep capacity. Can't take if weight exceeds capacity. This pick/not-pick with constraint is everywhere!  
**Logic:** dp[i][w] = max value using first i items with capacity w. dp[i][w] = max(dp[i-1][w], val[i] + dp[i-1][w-wt[i]]).  
**Java:**
```java
// Space Optimized 1D - MUST iterate capacity REVERSE!
int knapsack(int[] wt, int[] val, int W) {
    int n = wt.length;
    int[] dp = new int[W+1];

    for (int i = 0; i < n; i++) {
        for (int w = W; w >= wt[i]; w--) { // REVERSE!
            dp[w] = Math.max(dp[w], val[i] + dp[w - wt[i]]);
        }
    }

    return dp[W];
}
// Time: O(n*W), Space: O(W) â­
// WHY REVERSE? To ensure we use values from PREVIOUS iteration only!
// Forward would use updated values from SAME iteration (wrong for 0/1)
```

---

## DP-20: Minimum Coins (Coin Change I)
**Question:** Given coin denominations and amount, find minimum coins to make amount. Coins can be reused unlimited times.  
**Intuition:** Unbounded knapsack variant! For each coin, can use it multiple times. For each amount, try using each coin, take minimum.  
**Logic:** dp[amount] = 1 + min(dp[amount - coin]) for all coins.  
**Java:**
```java
int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount+1];
    Arrays.fill(dp, Integer.MAX_VALUE);
    dp[0] = 0;

    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (i >= coin && dp[i - coin] != Integer.MAX_VALUE) {
                dp[i] = Math.min(dp[i], 1 + dp[i - coin]);
            }
        }
    }

    return dp[amount] == Integer.MAX_VALUE ? -1 : dp[amount];
}
```

---

## DP-21: Target Sum
**Question:** Assign + or - to each element to make sum = target. Count ways.  
**Intuition:** Mind-blowing transformation! Elements with + form set P, with - form N. P - N = target and P + N = total. Solving: P = (total + target)/2. Converts to counting subsets with sum = (total + target)/2! Pattern recognition is key!  
**Logic:** Use DP-17 with K = (total + target)/2.  
**Java:**
```java
int findTargetSumWays(int[] arr, int target) {
    int total = 0;
    for (int num : arr) total += num;

    if ((total + target) % 2 != 0 || Math.abs(target) > total) return 0;

    int sum = (total + target) / 2;
    return countSubsets(arr, sum);
}
```

---

## DP-22: Coin Change II (Count Ways)
**Question:** Count ways to make amount using coins. Coins reusable.  
**Intuition:** Unlike min coins, here we count combinations. Important: process coins in order to avoid duplicate counting (like {1,2} and {2,1} as different).  
**Logic:** For each coin, update all amounts: dp[amount] += dp[amount - coin].  
**Java:**
```java
int change(int amount, int[] coins) {
    int[] dp = new int[amount+1];
    dp[0] = 1;

    for (int coin : coins) {
        for (int i = coin; i <= amount; i++) {
            dp[i] += dp[i - coin];
        }
    }

    return dp[amount];
}
```

---

## DP-23: Unbounded Knapsack
**Question:** Same as 0/1 Knapsack but items reusable unlimited times.  
**Intuition:** Key difference: after taking item, can take again! When taking item i, recurse on dp[i][w-wt[i]] NOT dp[i-1][w-wt[i]]. In 1D, iterate capacity FORWARD not backward!  
**Logic:** dp[w] = max(dp[w], val[i] + dp[w-wt[i]]).  
**Java:**
```java
int unboundedKnapsack(int[] wt, int[] val, int W) {
    int[] dp = new int[W+1];

    for (int i = 0; i < wt.length; i++) {
        for (int w = wt[i]; w <= W; w++) { // FORWARD!
            dp[w] = Math.max(dp[w], val[i] + dp[w - wt[i]]);
        }
    }

    return dp[W];
}
// Compare 0/1: REVERSE vs Unbounded: FORWARD iteration!
```

---

## DP-24: Rod Cutting
**Question:** Rod of length n, prices for each length 1 to n. Cut rod to maximize profit.  
**Intuition:** This IS unbounded knapsack! Each length is an item with weight=length, value=price. Can cut same length multiple times.  
**Logic:** dp[len] = max(price[i] + dp[len-i]) for all cuts i.  
**Java:**
```java
int rodCutting(int[] price, int n) {
    int[] dp = new int[n+1];

    for (int len = 1; len <= n; len++) {
        for (int cut = 1; cut <= len && cut <= price.length; cut++) {
            dp[len] = Math.max(dp[len], price[cut-1] + dp[len - cut]);
        }
    }

    return dp[n];
}
```

---

# Part 4: DP on Strings (LCS Family)

## DP-25: Longest Common Subsequence (LCS) â­â­â­
**Question:** Find length of longest subsequence common to both strings.  
**Intuition:** Foundation of ALL string DP! For each char pair: if match, include both and extend LCS of remaining. If don't match, try excluding from either string, take max. Builds solution character by character.  
**Logic:** If s1[i]==s2[j]: dp[i][j] = 1 + dp[i-1][j-1]. Else: dp[i][j] = max(dp[i-1][j], dp[i][j-1]).  
**Java:**
```java
int longestCommonSubsequence(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[] prev = new int[n+1];

    for (int i = 1; i <= m; i++) {
        int[] curr = new int[n+1];
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i-1) == s2.charAt(j-1)) {
                curr[j] = 1 + prev[j-1];
            } else {
                curr[j] = Math.max(prev[j], curr[j-1]);
            }
        }
        prev = curr;
    }

    return prev[n];
}
```

---

## DP-26: Print LCS
**Question:** Print actual LCS string, not just length.  
**Intuition:** Build DP table, then backtrack from [m][n] to [0][0]. When chars match, include and move diagonal. Else move toward larger value.  
**Logic:** Backtrack through DP table based on decisions.  
**Java:**
```java
String printLCS(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m+1][n+1];

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i-1) == s2.charAt(j-1)) {
                dp[i][j] = 1 + dp[i-1][j-1];
            } else {
                dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
            }
        }
    }

    StringBuilder lcs = new StringBuilder();
    int i = m, j = n;
    while (i > 0 && j > 0) {
        if (s1.charAt(i-1) == s2.charAt(j-1)) {
            lcs.append(s1.charAt(i-1));
            i--; j--;
        } else if (dp[i-1][j] > dp[i][j-1]) {
            i--;
        } else {
            j--;
        }
    }

    return lcs.reverse().toString();
}
```

---

## DP-27: Longest Common Substring
**Question:** Find length of longest common substring (contiguous!).  
**Intuition:** Unlike subsequence, substring MUST be contiguous! If chars match, extend length. If don't match, reset to 0 (can't skip). Track maximum seen.  
**Logic:** If match: dp[i][j] = 1 + dp[i-1][j-1]. Else: dp[i][j] = 0.  
**Java:**
```java
int longestCommonSubstring(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[] prev = new int[n+1];
    int maxLen = 0;

    for (int i = 1; i <= m; i++) {
        int[] curr = new int[n+1];
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i-1) == s2.charAt(j-1)) {
                curr[j] = 1 + prev[j-1];
                maxLen = Math.max(maxLen, curr[j]);
            }
        }
        prev = curr;
    }

    return maxLen;
}
```

---

## DP-28: Longest Palindromic Subsequence
**Question:** Find length of longest palindromic subsequence.  
**Intuition:** Genius observation! LPS(s) = LCS(s, reverse(s))! Palindrome reads same forward/backward, so chars must appear in same order in both.  
**Logic:** Reverse string, find LCS.  
**Java:**
```java
int longestPalindromeSubseq(String s) {
    String rev = new StringBuilder(s).reverse().toString();
    return longestCommonSubsequence(s, rev);
}
```

---

## DP-29: Min Insertions for Palindrome
**Question:** Minimum insertions to make string palindrome.  
**Intuition:** Chars not in LPS need duplicates inserted. Insert = n - LPS_length.  
**Java:**
```java
int minInsertions(String s) {
    return s.length() - longestPalindromeSubseq(s);
}
```

---

## DP-30: Min Deletions for Palindrome  
**Intuition:** Same as insertions! Delete = n - LPS_length.  
**Java:**
```java
int minDeletions(String s) {
    return s.length() - longestPalindromeSubseq(s);
}
```

---

## DP-31: Shortest Common Supersequence
**Question:** Shortest string containing both s1 and s2 as subsequences.  
**Intuition:** Length = len1 + len2 - LCS (count LCS once). Build by merging along LCS path.  
**Java:**
```java
String shortestCommonSupersequence(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m+1][n+1];

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i-1) == s2.charAt(j-1)) {
                dp[i][j] = 1 + dp[i-1][j-1];
            } else {
                dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
            }
        }
    }

    StringBuilder result = new StringBuilder();
    int i = m, j = n;
    while (i > 0 && j > 0) {
        if (s1.charAt(i-1) == s2.charAt(j-1)) {
            result.append(s1.charAt(i-1));
            i--; j--;
        } else if (dp[i-1][j] > dp[i][j-1]) {
            result.append(s1.charAt(i-1));
            i--;
        } else {
            result.append(s2.charAt(j-1));
            j--;
        }
    }
    while (i > 0) { result.append(s1.charAt(i-1)); i--; }
    while (j > 0) { result.append(s2.charAt(j-1)); j--; }

    return result.reverse().toString();
}
```

---

## DP-32: Distinct Subsequences
**Question:** Count distinct subsequences of s that equal t.  
**Intuition:** If chars match, count = ways using + not using. If don't match, only not using.  
**Logic:** If match: dp[i][j] = dp[i-1][j-1] + dp[i-1][j]. Else: dp[i][j] = dp[i-1][j].  
**Java:**
```java
int numDistinct(String s, String t) {
    int m = s.length(), n = t.length();
    int[] prev = new int[n+1];
    prev[0] = 1;

    for (int i = 1; i <= m; i++) {
        int[] curr = new int[n+1];
        curr[0] = 1;
        for (int j = 1; j <= n; j++) {
            curr[j] = prev[j];
            if (s.charAt(i-1) == t.charAt(j-1)) {
                curr[j] += prev[j-1];
            }
        }
        prev = curr;
    }

    return prev[n];
}
```

---

## DP-33: Edit Distance â­â­â­
**Question:** Convert s1 to s2 using insert/delete/replace. Find minimum operations.  
**Intuition:** Asked in EVERY company! If chars match, no operation. If not, try all 3, take min. Replace often most efficient.  
**Logic:** If match: dp[i][j] = dp[i-1][j-1]. Else: 1 + min(insert, delete, replace).  
**Java:**
```java
int minDistance(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[] prev = new int[n+1];

    for (int j = 0; j <= n; j++) prev[j] = j;

    for (int i = 1; i <= m; i++) {
        int[] curr = new int[n+1];
        curr[0] = i;
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i-1) == s2.charAt(j-1)) {
                curr[j] = prev[j-1];
            } else {
                curr[j] = 1 + Math.min(curr[j-1], Math.min(prev[j], prev[j-1]));
            }
        }
        prev = curr;
    }

    return prev[n];
}
```

---

## DP-34: Wildcard Matching
**Question:** Match string with pattern: '*' matches any sequence, '?' matches single char.  
**Intuition:** '?' simple - matches one. '*' tricky - matches 0 or more. Try both: skip * or use *.  
**Logic:** If match or '?': dp[i][j] = dp[i-1][j-1]. If '*': dp[i][j] = dp[i][j-1] || dp[i-1][j].  
**Java:**
```java
boolean isMatch(String s, String p) {
    int m = s.length(), n = p.length();
    boolean[] prev = new boolean[n+1];
    prev[0] = true;

    for (int j = 1; j <= n; j++) {
        if (p.charAt(j-1) == '*') prev[j] = prev[j-1];
    }

    for (int i = 1; i <= m; i++) {
        boolean[] curr = new boolean[n+1];
        for (int j = 1; j <= n; j++) {
            if (s.charAt(i-1) == p.charAt(j-1) || p.charAt(j-1) == '?') {
                curr[j] = prev[j-1];
            } else if (p.charAt(j-1) == '*') {
                curr[j] = curr[j-1] || prev[j];
            }
        }
        prev = curr;
    }

    return prev[n];
}
```

---

# Part 5: DP on Stocks

## DP-35: Best Time to Buy and Sell Stock
**Question:** Buy once, sell once. Maximize profit.  
**Intuition:** Greedy works! Track min price, calculate profit if sold today.  
**Java:**
```java
int maxProfit(int[] prices) {
    int minPrice = Integer.MAX_VALUE, maxProfit = 0;
    for (int price : prices) {
        minPrice = Math.min(minPrice, price);
        maxProfit = Math.max(maxProfit, price - minPrice);
    }
    return maxProfit;
}
```

---

## DP-36: Buy and Sell Stock II (Unlimited Transactions)
**Question:** Can buy and sell unlimited times.  
**Intuition:** Add all positive differences! Capture all uptrends.  
**Java:**
```java
int maxProfit(int[] prices) {
    int profit = 0;
    for (int i = 1; i < prices.length; i++) {
        if (prices[i] > prices[i-1]) {
            profit += prices[i] - prices[i-1];
        }
    }
    return profit;
}
```

---

## DP-37: Buy and Sell Stock III (At Most 2 Transactions)
**Question:** At most 2 transactions.  
**Intuition:** State machine! Track 4 states: buy1, sell1, buy2, sell2.  
**Java:**
```java
int maxProfit(int[] prices) {
    int buy1 = -prices[0], sell1 = 0;
    int buy2 = -prices[0], sell2 = 0;

    for (int i = 1; i < prices.length; i++) {
        buy1 = Math.max(buy1, -prices[i]);
        sell1 = Math.max(sell1, buy1 + prices[i]);
        buy2 = Math.max(buy2, sell1 - prices[i]);
        sell2 = Math.max(sell2, buy2 + prices[i]);
    }

    return sell2;
}
```

---

## DP-38: Buy and Sell Stock IV (At Most K Transactions)
**Question:** At most K transactions.  
**Intuition:** Generalize to K transactions. If K >= n/2, becomes unlimited.  
**Java:**
```java
int maxProfit(int k, int[] prices) {
    if (k >= prices.length / 2) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i-1]) {
                profit += prices[i] - prices[i-1];
            }
        }
        return profit;
    }

    int[] buy = new int[k+1];
    int[] sell = new int[k+1];
    Arrays.fill(buy, -prices[0]);

    for (int i = 1; i < prices.length; i++) {
        for (int j = k; j >= 1; j--) {
            sell[j] = Math.max(sell[j], buy[j] + prices[i]);
            buy[j] = Math.max(buy[j], sell[j-1] - prices[i]);
        }
    }

    return sell[k];
}
```

---

## DP-39: Stock with Cooldown
**Question:** After sell, must wait 1 day before buying.  
**Intuition:** 3 states: holding, sold (cooldown), rest.  
**Java:**
```java
int maxProfit(int[] prices) {
    int sold = 0, hold = -prices[0], rest = 0;

    for (int i = 1; i < prices.length; i++) {
        int prevSold = sold;
        sold = hold + prices[i];
        hold = Math.max(hold, rest - prices[i]);
        rest = Math.max(rest, prevSold);
    }

    return Math.max(sold, rest);
}
```

---

## DP-40: Stock with Transaction Fee
**Question:** Pay fee for each transaction.  
**Intuition:** Subtract fee when selling.  
**Java:**
```java
int maxProfit(int[] prices, int fee) {
    int cash = 0, hold = -prices[0];

    for (int i = 1; i < prices.length; i++) {
        cash = Math.max(cash, hold + prices[i] - fee);
        hold = Math.max(hold, cash - prices[i]);
    }

    return cash;
}
```

---

# Part 6: DP on LIS (Longest Increasing Subsequence)

## DP-41: Longest Increasing Subsequence â­â­â­
**Question:** Find length of longest strictly increasing subsequence.  
**Intuition:** Two approaches! O(nÂ²) DP or O(n log n) binary search. Second is interview gold!  
**Logic:** O(nÂ²): For each i, check all j < i where arr[j] < arr[i]. O(n log n): Maintain tails array with binary search.  
**Java:**
```java
// O(nÂ²) DP
int lengthOfLIS(int[] nums) {
    int n = nums.length;
    int[] dp = new int[n];
    Arrays.fill(dp, 1);
    int maxLen = 1;

    for (int i = 1; i < n; i++) {
        for (int j = 0; j < i; j++) {
            if (nums[i] > nums[j]) {
                dp[i] = Math.max(dp[i], dp[j] + 1);
            }
        }
        maxLen = Math.max(maxLen, dp[i]);
    }
    return maxLen;
}

// O(n log n) Binary Search â­â­â­
int lengthOfLISOptimal(int[] nums) {
    List<Integer> tails = new ArrayList<>();

    for (int num : nums) {
        int pos = Collections.binarySearch(tails, num);
        if (pos < 0) pos = -(pos + 1);

        if (pos == tails.size()) {
            tails.add(num);
        } else {
            tails.set(pos, num);
        }
    }
    return tails.size();
}
```

---

## DP-42: Printing LIS
**Question:** Print actual LIS.  
**Intuition:** Track parent during DP, backtrack to build LIS.  
**Java:**
```java
List<Integer> printLIS(int[] nums) {
    int n = nums.length;
    int[] dp = new int[n], parent = new int[n];
    Arrays.fill(dp, 1);
    Arrays.fill(parent, -1);

    int maxLen = 1, maxIndex = 0;
    for (int i = 1; i < n; i++) {
        for (int j = 0; j < i; j++) {
            if (nums[i] > nums[j] && dp[j] + 1 > dp[i]) {
                dp[i] = dp[j] + 1;
                parent[i] = j;
            }
        }
        if (dp[i] > maxLen) {
            maxLen = dp[i];
            maxIndex = i;
        }
    }

    List<Integer> lis = new ArrayList<>();
    int curr = maxIndex;
    while (curr != -1) {
        lis.add(nums[curr]);
        curr = parent[curr];
    }
    Collections.reverse(lis);
    return lis;
}
```

---

## DP-43: Largest Divisible Subset
**Question:** Find largest subset where every pair (a,b) satisfies a%b==0 or b%a==0.  
**Intuition:** Sort first! Then LIS with divisibility condition.  
**Java:**
```java
List<Integer> largestDivisibleSubset(int[] nums) {
    Arrays.sort(nums);
    int n = nums.length;
    int[] dp = new int[n], parent = new int[n];
    Arrays.fill(dp, 1);
    Arrays.fill(parent, -1);

    int maxLen = 1, maxIndex = 0;
    for (int i = 1; i < n; i++) {
        for (int j = 0; j < i; j++) {
            if (nums[i] % nums[j] == 0 && dp[j] + 1 > dp[i]) {
                dp[i] = dp[j] + 1;
                parent[i] = j;
            }
        }
        if (dp[i] > maxLen) {
            maxLen = dp[i];
            maxIndex = i;
        }
    }

    List<Integer> result = new ArrayList<>();
    int curr = maxIndex;
    while (curr != -1) {
        result.add(nums[curr]);
        curr = parent[curr];
    }
    Collections.reverse(result);
    return result;
}
```

---

## DP-44: Longest String Chain
**Question:** Chain where each word formed by adding one letter to previous.  
**Intuition:** Sort by length! For each word, try removing each char, check if predecessor exists.  
**Java:**
```java
int longestStrChain(String[] words) {
    Arrays.sort(words, (a, b) -> a.length() - b.length());
    Map<String, Integer> dp = new HashMap<>();
    int maxLen = 1;

    for (String word : words) {
        int currentLen = 1;
        for (int i = 0; i < word.length(); i++) {
            String predecessor = word.substring(0, i) + word.substring(i + 1);
            currentLen = Math.max(currentLen, dp.getOrDefault(predecessor, 0) + 1);
        }
        dp.put(word, currentLen);
        maxLen = Math.max(maxLen, currentLen);
    }

    return maxLen;
}
```

---

## DP-45: Longest Bitonic Subsequence
**Question:** Bitonic = strictly increasing then strictly decreasing.  
**Intuition:** LIS from left + LIS from right (LDS). Combine at each peak.  
**Java:**
```java
int longestBitonicSubsequence(int[] nums) {
    int n = nums.length;
    int[] lis = new int[n], lds = new int[n];
    Arrays.fill(lis, 1);
    Arrays.fill(lds, 1);

    for (int i = 1; i < n; i++) {
        for (int j = 0; j < i; j++) {
            if (nums[i] > nums[j]) {
                lis[i] = Math.max(lis[i], lis[j] + 1);
            }
        }
    }

    for (int i = n-2; i >= 0; i--) {
        for (int j = n-1; j > i; j--) {
            if (nums[i] > nums[j]) {
                lds[i] = Math.max(lds[i], lds[j] + 1);
            }
        }
    }

    int maxLen = 0;
    for (int i = 0; i < n; i++) {
        if (lis[i] > 1 && lds[i] > 1) {
            maxLen = Math.max(maxLen, lis[i] + lds[i] - 1);
        }
    }
    return maxLen;
}
```

---

## DP-46: Number of LIS
**Question:** Count number of LIS.  
**Intuition:** Track count along with length!  
**Java:**
```java
int findNumberOfLIS(int[] nums) {
    int n = nums.length;
    int[] dp = new int[n], count = new int[n];
    Arrays.fill(dp, 1);
    Arrays.fill(count, 1);
    int maxLen = 1;

    for (int i = 1; i < n; i++) {
        for (int j = 0; j < i; j++) {
            if (nums[i] > nums[j]) {
                if (dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    count[i] = count[j];
                } else if (dp[j] + 1 == dp[i]) {
                    count[i] += count[j];
                }
            }
        }
        maxLen = Math.max(maxLen, dp[i]);
    }

    int result = 0;
    for (int i = 0; i < n; i++) {
        if (dp[i] == maxLen) result += count[i];
    }
    return result;
}
```

---

## DP-47: Matrix Chain Multiplication â­â­â­
**Question:** Find minimum operations to multiply matrix chain.  
**Intuition:** Classic interval DP! For range [i,j], try every split k. This partition pattern appears everywhere (MCM pattern)!  
**Logic:** dp[i][j] = min(dp[i][k] + dp[k+1][j] + arr[i]*arr[k+1]*arr[j+1]).  
**Java:**
```java
int matrixChainMultiplication(int[] arr) {
    int n = arr.length - 1;
    int[][] dp = new int[n][n];

    for (int len = 2; len <= n; len++) {
        for (int i = 0; i < n - len + 1; i++) {
            int j = i + len - 1;
            dp[i][j] = Integer.MAX_VALUE;

            for (int k = i; k < j; k++) {
                int cost = dp[i][k] + dp[k+1][j] + arr[i] * arr[k+1] * arr[j+1];
                dp[i][j] = Math.min(dp[i][j], cost);
            }
        }
    }
    return dp[0][n-1];
}
```

---

# Part 7: MCM DP / Partition DP

## DP-48: Minimum Cost to Cut Stick
**Question:** Cut stick at positions. Cost = stick length. Minimize total.  
**Intuition:** MCM variant! Sort cuts, add boundaries 0 and n.  
**Java:**
```java
int minCost(int n, int[] cuts) {
    int c = cuts.length;
    int[] arr = new int[c + 2];
    arr[0] = 0; arr[c + 1] = n;
    for (int i = 0; i < c; i++) arr[i + 1] = cuts[i];
    Arrays.sort(arr);

    int[][] dp = new int[c + 2][c + 2];

    for (int len = 2; len <= c + 1; len++) {
        for (int i = 0; i + len <= c + 1; i++) {
            int j = i + len;
            dp[i][j] = Integer.MAX_VALUE;

            for (int k = i + 1; k < j; k++) {
                dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k][j] + arr[j] - arr[i]);
            }
        }
    }

    return dp[0][c + 1];
}
```

---

## DP-49: Burst Balloons â­â­â­
**Question:** Burst balloons. Coins = nums[left] * nums[i] * nums[right].  
**Intuition:** Mind-bending! Think which balloon bursts LAST! Add 1 to boundaries.  
**Java:**
```java
int maxCoins(int[] nums) {
    int n = nums.length;
    int[] arr = new int[n + 2];
    arr[0] = 1; arr[n + 1] = 1;
    for (int i = 0; i < n; i++) arr[i + 1] = nums[i];

    int[][] dp = new int[n + 2][n + 2];

    for (int len = 1; len <= n; len++) {
        for (int i = 1; i + len <= n + 1; i++) {
            int j = i + len - 1;

            for (int k = i; k <= j; k++) {
                int coins = arr[i-1] * arr[k] * arr[j+1];
                coins += dp[i][k-1] + dp[k+1][j];
                dp[i][j] = Math.max(dp[i][j], coins);
            }
        }
    }

    return dp[1][n];
}
```

---

## DP-50: Palindrome Partitioning II
**Question:** Partition into palindromes. Minimize cuts.  
**Intuition:** Precompute palindromes. For each i, find min cuts.  
**Java:**
```java
int minCut(String s) {
    int n = s.length();
    boolean[][] isPalin = new boolean[n][n];

    for (int len = 1; len <= n; len++) {
        for (int i = 0; i <= n - len; i++) {
            int j = i + len - 1;
            if (s.charAt(i) == s.charAt(j)) {
                isPalin[i][j] = (len <= 2) || isPalin[i+1][j-1];
            }
        }
    }

    int[] dp = new int[n];
    for (int i = 0; i < n; i++) {
        if (isPalin[0][i]) {
            dp[i] = 0;
        } else {
            dp[i] = Integer.MAX_VALUE;
            for (int j = 0; j < i; j++) {
                if (isPalin[j+1][i]) {
                    dp[i] = Math.min(dp[i], dp[j] + 1);
                }
            }
        }
    }

    return dp[n-1];
}
```

---

## DP-51: Partition Array for Maximum Sum
**Question:** Partition into subarrays â‰¤k. Replace all with max. Maximize sum.  
**Intuition:** Try all partitions ending at each position.  
**Java:**
```java
int maxSumAfterPartitioning(int[] arr, int k) {
    int n = arr.length;
    int[] dp = new int[n + 1];

    for (int i = 1; i <= n; i++) {
        int max = 0;
        for (int len = 1; len <= k && i - len >= 0; len++) {
            max = Math.max(max, arr[i - len]);
            dp[i] = Math.max(dp[i], dp[i - len] + max * len);
        }
    }

    return dp[n];
}
```

---

## DP-52: Largest Rectangle in Histogram
**Question:** Find area of largest rectangle in histogram.  
**Intuition:** For each bar, find left and right boundaries using stack.  
**Java:**
```java
int largestRectangleArea(int[] heights) {
    int n = heights.length;
    int[] left = new int[n], right = new int[n];
    Stack<Integer> stack = new Stack<>();

    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
            stack.pop();
        }
        left[i] = stack.isEmpty() ? 0 : stack.peek() + 1;
        stack.push(i);
    }

    stack.clear();
    for (int i = n - 1; i >= 0; i--) {
        while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
            stack.pop();
        }
        right[i] = stack.isEmpty() ? n - 1 : stack.peek() - 1;
        stack.push(i);
    }

    int maxArea = 0;
    for (int i = 0; i < n; i++) {
        maxArea = Math.max(maxArea, heights[i] * (right[i] - left[i] + 1));
    }
    return maxArea;
}
```

---

## DP-53: Maximal Rectangle
**Question:** Binary matrix. Find largest rectangle of 1s.  
**Intuition:** For each row, calculate histogram heights, apply DP-52!  
**Java:**
```java
int maximalRectangle(char[][] matrix) {
    if (matrix.length == 0) return 0;

    int m = matrix.length, n = matrix[0].length;
    int[] heights = new int[n];
    int maxArea = 0;

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            heights[j] = (matrix[i][j] == '1') ? heights[j] + 1 : 0;
        }
        maxArea = Math.max(maxArea, largestRectangleArea(heights));
    }

    return maxArea;
}
```

---

## DP-54: Count Square Submatrices with All Ones
**Question:** Count number of square submatrices with all 1s.  
**Intuition:** dp[i][j] = size of largest square ending at (i,j). Sum all dp values!  
**Java:**
```java
int countSquares(int[][] matrix) {
    int m = matrix.length, n = matrix[0].length;
    int[][] dp = new int[m][n];
    int count = 0;

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (matrix[i][j] == 1) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 1;
                } else {
                    dp[i][j] = 1 + Math.min(dp[i-1][j], Math.min(dp[i][j-1], dp[i-1][j-1]));
                }
                count += dp[i][j];
            }
        }
    }

    return count;
}
```

---

# Summary: Complete DP Mastery

## 7 Major DP Patterns

### 1. 1D DP
- Examples: Fibonacci, House Robber, Frog Jump
- Use prev1, prev2 for O(1) space

### 2. 2D Grid DP  
- Examples: Unique Paths, Min Path Sum
- Use 1D array for O(n) space

### 3. Pick/Not-Pick (Subsequences)
- Examples: Subset Sum, Knapsack, LCS
- Foundation of countless problems

### 4. Unbounded DP
- Examples: Coin Change, Rod Cutting
- Key: FORWARD iteration (can reuse)

### 5. String DP
- Examples: LCS, Edit Distance
- 2D table, match/mismatch decisions

### 6. LIS Family
- O(nÂ²) DP or O(n log n) Binary Search
- Many variations possible

### 7. MCM / Partition DP
- Examples: Matrix Chain, Burst Balloons
- Try all partition points

---

## Time Complexity Reference

| Pattern | Time | Space |
|---------|------|-------|
| 1D DP | O(n) | O(1) |
| Grid DP | O(m*n) | O(n) |
| Subset Sum | O(n*sum) | O(sum) |
| Knapsack | O(n*W) | O(W) |
| LCS | O(m*n) | O(n) |
| LIS (BS) | O(n log n) | O(n) |
| MCM | O(nÂ³) | O(nÂ²) |

---

## Interview Tips

1. **Start with recursion** - builds intuition
2. **Draw recursion tree** - identify overlapping
3. **Add memoization** - cache results
4. **Convert to tabulation** - bottom-up
5. **Optimize space** - impress interviewer!

**Most Important:**
- 0/1 Knapsack - foundation of 20+ problems
- LCS - foundation of 15+ string problems
- LIS - asked everywhere
- Edit Distance - FAANG favorite
- MCM - tests advanced thinking
