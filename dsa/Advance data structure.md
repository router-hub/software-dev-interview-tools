
## Table of Contents
- **Part 1: Disjoint Set Union (DSU/Union-Find)** - Graph Connectivity
- **Part 2: Segment Tree** - Range Queries & Updates
- **Part 3: Fenwick Tree (Binary Indexed Tree)** - Efficient Range Operations
- **Part 4: Additional Interview Structures** - Sparse Table, Mo's Algorithm, etc.
 
---

# Part 1: Disjoint Set Union (DSU / Union-Find)

## Introduction to DSU
**Question:** How to efficiently track and merge disjoint sets?  
**Intuition:** Imagine social network: people belong to different friend groups (disjoint sets). When two people become friends, their entire groups merge! DSU efficiently handles: (1) **Find** - which group does person belong to?, (2) **Union** - merge two groups. Without optimization: O(n) per operation. With **Path Compression** + **Union by Rank**: almost O(1) - specifically O(Î±(n)) where Î± is inverse Ackermann function (practically constant)!  
**Real-World Use Cases:**
- **Network Connectivity** - Are two computers connected?
- **Kruskal's MST Algorithm** - Detect cycles while adding edges
- **Image Processing** - Connected component labeling
- **Social Networks** - Friend circles, community detection
- **Percolation Theory** - Water flow through porous material
- **Incremental Connectivity** - Roads connecting cities

**Logic:** Each set has a **representative** (parent). Initially, each element is its own parent. Find: traverse up to root. Union: attach smaller tree under larger tree's root.  
**Java:**
```java
class DisjointSet {
    int[] parent;
    int[] rank;  // or size

    // Initialize: each element is its own parent
    public DisjointSet(int n) {
        parent = new int[n];
        rank = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;  // Self parent
            rank[i] = 0;
        }
    }

    // Find with Path Compression â­
    // All nodes point directly to root after find!
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression!
        }
        return parent[x];
    }

    // Union by Rank â­
    // Attach smaller tree under larger tree
    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) return; // Already in same set

        // Union by rank: attach smaller rank tree under larger
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
    }

    // Check if x and y are in same set
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}

// Time: O(Î±(n)) â‰ˆ O(1) per operation with optimizations
// Space: O(n)
```

**Alternative: Union by Size**
```java
class DisjointSet {
    int[] parent;
    int[] size;

    public DisjointSet(int n) {
        parent = new int[n];
        size = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1; // Each set initially has size 1
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) return;

        // Union by size: attach smaller set under larger
        if (size[rootX] < size[rootY]) {
            parent[rootX] = rootY;
            size[rootY] += size[rootX];
        } else {
            parent[rootY] = rootX;
            size[rootX] += size[rootY];
        }
    }

    public int getSize(int x) {
        return size[find(x)];
    }
}
```

---

## DSU Problem 1: Number of Provinces (Friend Circles)
**Question:** Given n people and their friendships (adjacency matrix), find number of friend circles.  
**Intuition:** Each friend circle is a disjoint set! If i and j are friends, union them. Count number of unique roots at end = number of provinces.  
**Java:**
```java
int findCircleNum(int[][] isConnected) {
    int n = isConnected.length;
    DisjointSet ds = new DisjointSet(n);

    // Union all friends
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (isConnected[i][j] == 1) {
                ds.union(i, j);
            }
        }
    }

    // Count unique roots
    int provinces = 0;
    for (int i = 0; i < n; i++) {
        if (ds.find(i) == i) { // i is root
            provinces++;
        }
    }

    return provinces;
}
// Time: O(nÂ² * Î±(n)), Space: O(n)
```

---

## DSU Problem 2: Number of Operations to Make Network Connected
**Question:** Given n computers and connections, find min operations to connect all (can disconnect and reconnect edges).  
**Intuition:** Need (n-1) edges to connect n computers. Count: (1) number of components, (2) extra edges. If extra edges â‰¥ components - 1, possible!  
**Java:**
```java
int makeConnected(int n, int[][] connections) {
    if (connections.length < n - 1) return -1; // Not enough edges

    DisjointSet ds = new DisjointSet(n);
    int extraEdges = 0;

    for (int[] conn : connections) {
        if (ds.find(conn[0]) == ds.find(conn[1])) {
            extraEdges++; // Redundant edge
        } else {
            ds.union(conn[0], conn[1]);
        }
    }

    // Count components
    int components = 0;
    for (int i = 0; i < n; i++) {
        if (ds.find(i) == i) components++;
    }

    int operationsNeeded = components - 1;
    return (extraEdges >= operationsNeeded) ? operationsNeeded : -1;
}
```

---

## DSU Problem 3: Accounts Merge
**Question:** Given accounts with emails, merge accounts belonging to same person.  
**Intuition:** If two accounts share an email, they belong to same person. Use DSU where each account is a node. Union accounts with common emails!  
**Java:**
```java
List<List<String>> accountsMerge(List<List<String>> accounts) {
    int n = accounts.size();
    DisjointSet ds = new DisjointSet(n);
    Map<String, Integer> emailToAccount = new HashMap<>();

    // Map emails to account indices and union
    for (int i = 0; i < n; i++) {
        for (int j = 1; j < accounts.get(i).size(); j++) {
            String email = accounts.get(i).get(j);

            if (emailToAccount.containsKey(email)) {
                ds.union(i, emailToAccount.get(email));
            } else {
                emailToAccount.put(email, i);
            }
        }
    }

    // Group emails by root account
    Map<Integer, List<String>> merged = new HashMap<>();
    for (String email : emailToAccount.keySet()) {
        int account = emailToAccount.get(email);
        int root = ds.find(account);
        merged.putIfAbsent(root, new ArrayList<>());
        merged.get(root).add(email);
    }

    // Build result
    List<List<String>> result = new ArrayList<>();
    for (int root : merged.keySet()) {
        List<String> emails = merged.get(root);
        Collections.sort(emails);
        List<String> account = new ArrayList<>();
        account.add(accounts.get(root).get(0)); // Name
        account.addAll(emails);
        result.add(account);
    }

    return result;
}
```

---

## DSU Problem 4: Kruskal's MST Algorithm
**Question:** Find Minimum Spanning Tree using Kruskal's algorithm.  
**Intuition:** Greedy! Sort edges by weight, add smallest edge if it doesn't form cycle. DSU detects cycles: if both nodes in same set, adding edge creates cycle!  
**Java:**
```java
class Edge implements Comparable<Edge> {
    int src, dest, weight;

    public int compareTo(Edge other) {
        return this.weight - other.weight;
    }
}

int kruskalMST(int n, List<Edge> edges) {
    Collections.sort(edges); // Sort by weight
    DisjointSet ds = new DisjointSet(n);

    int mstWeight = 0;
    int edgesAdded = 0;

    for (Edge edge : edges) {
        int rootSrc = ds.find(edge.src);
        int rootDest = ds.find(edge.dest);

        if (rootSrc != rootDest) { // No cycle
            ds.union(edge.src, edge.dest);
            mstWeight += edge.weight;
            edgesAdded++;

            if (edgesAdded == n - 1) break; // MST complete
        }
    }

    return mstWeight;
}
// Time: O(E log E) for sorting + O(E * Î±(V))
```

---

## DSU Problem 5: Number of Islands II (Dynamic Connectivity)
**Question:** Initially mÃ—n grid is all water. Given list of positions to add land, return count of islands after each addition.  
**Intuition:** Dynamic DSU! Each land cell is a node. When adding land, union with adjacent land cells (4-directional). Count components after each operation!  
**Java:**
```java
List<Integer> numIslands2(int m, int n, int[][] positions) {
    List<Integer> result = new ArrayList<>();
    DisjointSet ds = new DisjointSet(m * n);
    boolean[] isLand = new boolean[m * n];
    int count = 0;

    int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};

    for (int[] pos : positions) {
        int r = pos[0], c = pos[1];
        int idx = r * n + c;

        if (isLand[idx]) {
            result.add(count);
            continue;
        }

        isLand[idx] = true;
        count++; // New island

        // Check 4 neighbors
        for (int[] dir : dirs) {
            int nr = r + dir[0], nc = c + dir[1];
            int nidx = nr * n + nc;

            if (nr >= 0 && nr < m && nc >= 0 && nc < n && isLand[nidx]) {
                int root1 = ds.find(idx);
                int root2 = ds.find(nidx);

                if (root1 != root2) {
                    ds.union(idx, nidx);
                    count--; // Merged two islands
                }
            }
        }

        result.add(count);
    }

    return result;
}
```

---

# Part 2: Segment Tree

## Introduction to Segment Tree
**Question:** How to efficiently perform range queries (sum, min, max) with updates?  
**Intuition:** Segment Tree is a **tree for arrays**! Each node represents a segment [l, r]. Leaf nodes are individual elements. Internal nodes store aggregate (sum/min/max) of children. **Binary tree structure** where each level represents progressively larger ranges. Enables **O(log n) queries and updates** instead of O(n)!  
**Real-World Use Cases:**
- **Stock Market Analysis** - Range min/max in time period, updates on price changes
- **Game Leaderboards** - Top scores in range, score updates
- **Hotel Booking** - Available rooms in date range
- **Network Traffic** - Bandwidth usage in time windows
- **Computational Geometry** - Range queries in 2D space
- **Database Indexing** - Range aggregations

**Structure:**
```
Array: [1, 3, 5, 7, 9, 11]
Segment Tree (sum):
                  36 [0,5]
                /           \
            9 [0,2]          27 [3,5]
           /    \           /     \
        4[0,1]  5[2,2]   16[3,4]  11[5,5]
        /   \           /    \
     1[0,0] 3[1,1]   7[3,3]  9[4,4]
```

**Java Implementation:**
```java
class SegmentTree {
    int[] tree;
    int n;

    public SegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4 * n]; // Safe size: 4*n
        build(arr, 0, 0, n - 1);
    }

    // Build tree from array
    void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start]; // Leaf node
            return;
        }

        int mid = (start + end) / 2;
        int leftChild = 2 * node + 1;
        int rightChild = 2 * node + 2;

        build(arr, leftChild, start, mid);
        build(arr, rightChild, mid + 1, end);

        tree[node] = tree[leftChild] + tree[rightChild]; // Aggregate
    }

    // Range Sum Query [l, r]
    int query(int l, int r) {
        return queryHelper(0, 0, n - 1, l, r);
    }

    int queryHelper(int node, int start, int end, int l, int r) {
        // No overlap
        if (r < start || end < l) {
            return 0;
        }

        // Complete overlap
        if (l <= start && end <= r) {
            return tree[node];
        }

        // Partial overlap
        int mid = (start + end) / 2;
        int leftSum = queryHelper(2 * node + 1, start, mid, l, r);
        int rightSum = queryHelper(2 * node + 2, mid + 1, end, l, r);

        return leftSum + rightSum;
    }

    // Point Update: arr[idx] = val
    void update(int idx, int val) {
        updateHelper(0, 0, n - 1, idx, val);
    }

    void updateHelper(int node, int start, int end, int idx, int val) {
        if (start == end) {
            tree[node] = val; // Update leaf
            return;
        }

        int mid = (start + end) / 2;

        if (idx <= mid) {
            updateHelper(2 * node + 1, start, mid, idx, val);
        } else {
            updateHelper(2 * node + 2, mid + 1, end, idx, val);
        }

        // Update current node
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
    }
}

// Time: Build O(n), Query O(log n), Update O(log n)
// Space: O(4n) = O(n)
```

**Example Usage:**
```java
int[] arr = {1, 3, 5, 7, 9, 11};
SegmentTree st = new SegmentTree(arr);

System.out.println(st.query(1, 3)); // Sum of arr[1..3] = 3+5+7 = 15

st.update(1, 10); // arr[1] = 10

System.out.println(st.query(1, 3)); // Sum now = 10+5+7 = 22
```

---

## Segment Tree Variations

### 1. Range Min/Max Query
```java
class SegmentTreeMin {
    int[] tree;
    int n;

    void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start];
            return;
        }

        int mid = (start + end) / 2;
        build(arr, 2*node+1, start, mid);
        build(arr, 2*node+2, mid+1, end);

        tree[node] = Math.min(tree[2*node+1], tree[2*node+2]); // MIN instead of SUM
    }

    int queryMin(int node, int start, int end, int l, int r) {
        if (r < start || end < l) return Integer.MAX_VALUE;
        if (l <= start && end <= r) return tree[node];

        int mid = (start + end) / 2;
        int leftMin = queryMin(2*node+1, start, mid, l, r);
        int rightMin = queryMin(2*node+2, mid+1, end, l, r);

        return Math.min(leftMin, rightMin);
    }
}
```

### 2. Lazy Propagation (Range Updates)
**Intuition:** Instead of updating each element individually (slow for range updates), mark entire range as "needs update" and propagate lazily!  
```java
class SegmentTreeLazy {
    int[] tree, lazy;
    int n;

    // Range Update: add val to all elements in [l, r]
    void updateRange(int node, int start, int end, int l, int r, int val) {
        // Apply pending updates
        if (lazy[node] != 0) {
            tree[node] += (end - start + 1) * lazy[node];

            if (start != end) {
                lazy[2*node+1] += lazy[node];
                lazy[2*node+2] += lazy[node];
            }

            lazy[node] = 0;
        }

        // No overlap
        if (start > end || start > r || end < l) return;

        // Complete overlap
        if (start >= l && end <= r) {
            tree[node] += (end - start + 1) * val;

            if (start != end) {
                lazy[2*node+1] += val;
                lazy[2*node+2] += val;
            }

            return;
        }

        // Partial overlap
        int mid = (start + end) / 2;
        updateRange(2*node+1, start, mid, l, r, val);
        updateRange(2*node+2, mid+1, end, l, r, val);

        tree[node] = tree[2*node+1] + tree[2*node+2];
    }

    int queryRange(int node, int start, int end, int l, int r) {
        if (start > end || start > r || end < l) return 0;

        // Apply pending updates
        if (lazy[node] != 0) {
            tree[node] += (end - start + 1) * lazy[node];

            if (start != end) {
                lazy[2*node+1] += lazy[node];
                lazy[2*node+2] += lazy[node];
            }

            lazy[node] = 0;
        }

        if (start >= l && end <= r) return tree[node];

        int mid = (start + end) / 2;
        return queryRange(2*node+1, start, mid, l, r) +
               queryRange(2*node+2, mid+1, end, l, r);
    }
}
// Range Update: O(log n) instead of O(n)!
```

---

## Segment Tree Problem 1: Range Sum Query - Mutable
**Question:** Implement class supporting range sum queries and point updates.  
**Java:**
```java
class NumArray {
    SegmentTree st;

    public NumArray(int[] nums) {
        st = new SegmentTree(nums);
    }

    public void update(int index, int val) {
        st.update(index, val);
    }

    public int sumRange(int left, int right) {
        return st.query(left, right);
    }
}
// Beats brute force: O(n) update + O(1) query â†’ O(log n) + O(log n)
```

---

## Segment Tree Problem 2: Count of Smaller Numbers After Self
**Question:** For each element in array, count elements to its right that are smaller.  
**Intuition:** Process from right to left. For each element, query count of smaller elements seen so far, then insert element!  
**Java:**
```java
List<Integer> countSmaller(int[] nums) {
    int n = nums.length;
    Integer[] result = new Integer[n];

    // Coordinate compression
    int[] sorted = nums.clone();
    Arrays.sort(sorted);
    Map<Integer, Integer> ranks = new HashMap<>();
    int rank = 1;
    for (int num : sorted) {
        if (!ranks.containsKey(num)) {
            ranks.put(num, rank++);
        }
    }

    SegmentTree st = new SegmentTree(rank);

    // Process from right to left
    for (int i = n - 1; i >= 0; i--) {
        int r = ranks.get(nums[i]);
        result[i] = st.query(1, r - 1); // Count smaller
        st.update(r, st.query(r, r) + 1); // Insert
    }

    return Arrays.asList(result);
}
```

---

# Part 3: Fenwick Tree (Binary Indexed Tree)

## Introduction to Fenwick Tree
**Question:** Simpler alternative to Segment Tree for **prefix operations** (prefix sum, prefix XOR).  
**Intuition:** **Tree hidden in array**! Uses binary representation magic. Each index stores sum of a specific range based on its binary form. **Key insight**: index i is responsible for range determined by **last set bit**! For index 6 (110â‚‚), responsible for 2 elements (last set bit at position 1 = 2Â¹ = 2). Simpler code than Segment Tree, less memory, but **less flexible** (only prefix operations).  
**Real-World Use Cases:**
- **Stock Portfolio** - Total value up to date i
- **Event Counting** - Number of events up to time t
- **Cumulative Statistics** - Running totals, rankings
- **2D Range Queries** - Combined with 2D BIT
- **Inversion Count** - Count inversions in array

**When Fenwick vs Segment:**
- Fenwick: Simpler, faster, less memory, **prefix only**
- Segment: More flexible, **any range**, can do min/max/gcd

**Java Implementation:**
```java
class FenwickTree {
    int[] tree;
    int n;

    public FenwickTree(int n) {
        this.n = n;
        tree = new int[n + 1]; // 1-indexed
    }

    // Update: add val at index i
    void update(int i, int val) {
        i++; // Convert to 1-indexed

        while (i <= n) {
            tree[i] += val;
            i += i & (-i); // Add last set bit
        }
    }

    // Query: prefix sum from index 0 to i
    int query(int i) {
        i++; // Convert to 1-indexed
        int sum = 0;

        while (i > 0) {
            sum += tree[i];
            i -= i & (-i); // Remove last set bit
        }

        return sum;
    }

    // Range sum [l, r]
    int rangeQuery(int l, int r) {
        return query(r) - (l > 0 ? query(l - 1) : 0);
    }
}

// Time: Update O(log n), Query O(log n)
// Space: O(n)
// Simpler and faster than Segment Tree for prefix operations!
```

**Last Set Bit Magic:**
```java
int lastSetBit(int i) {
    return i & (-i);
}

// Examples:
// 6 (110â‚‚) â†’ 2 (010â‚‚)
// 12 (1100â‚‚) â†’ 4 (0100â‚‚)
// 7 (111â‚‚) â†’ 1 (001â‚‚)
```

**Example:**
```java
FenwickTree ft = new FenwickTree(6);

// Build from array [1, 3, 5, 7, 9, 11]
ft.update(0, 1);
ft.update(1, 3);
ft.update(2, 5);
ft.update(3, 7);
ft.update(4, 9);
ft.update(5, 11);

System.out.println(ft.query(3)); // Prefix sum [0..3] = 1+3+5+7 = 16
System.out.println(ft.rangeQuery(1, 3)); // Sum [1..3] = 3+5+7 = 15

ft.update(1, 7); // Add 7 to index 1 (3 â†’ 10)
System.out.println(ft.query(3)); // Now = 1+10+5+7 = 23
```

---

## Fenwick Tree Problem: Count of Range Sum
**Question:** Given array and range [lower, upper], count how many range sums fall in this range.  
**Intuition:** Use prefix sums + coordinate compression + Fenwick Tree. For each prefix sum, count how many previous prefix sums are in valid range!  
**Java:**
```java
int countRangeSum(int[] nums, int lower, int upper) {
    int n = nums.length;
    long[] prefixSum = new long[n + 1];

    for (int i = 0; i < n; i++) {
        prefixSum[i + 1] = prefixSum[i] + nums[i];
    }

    // Coordinate compression
    Set<Long> allValues = new TreeSet<>();
    for (long sum : prefixSum) {
        allValues.add(sum);
        allValues.add(sum - lower);
        allValues.add(sum - upper);
    }

    Map<Long, Integer> ranks = new HashMap<>();
    int rank = 0;
    for (long val : allValues) {
        ranks.put(val, rank++);
    }

    FenwickTree ft = new FenwickTree(ranks.size());
    int count = 0;

    for (long sum : prefixSum) {
        int left = ranks.get(sum - upper);
        int right = ranks.get(sum - lower);
        count += ft.rangeQuery(left, right);
        ft.update(ranks.get(sum), 1);
    }

    return count;
}
```

---

# Part 4: Additional Interview Structures

## 1. Sparse Table (Range Minimum Query - Immutable)
**Question:** For **static array** (no updates), answer range min/max queries in O(1)!  
**Intuition:** Precompute answers for all ranges of length 2^k. For query [l, r], combine two overlapping ranges! Only works for **idempotent operations** (min, max, gcd) where overlapping is okay.  
**Java:**
```java
class SparseTable {
    int[][] st;
    int[] log;
    int n;

    public SparseTable(int[] arr) {
        n = arr.length;
        int maxLog = (int)(Math.log(n) / Math.log(2)) + 1;
        st = new int[n][maxLog];
        log = new int[n + 1];

        // Precompute logs
        log[1] = 0;
        for (int i = 2; i <= n; i++) {
            log[i] = log[i / 2] + 1;
        }

        // Base case: ranges of length 1
        for (int i = 0; i < n; i++) {
            st[i][0] = arr[i];
        }

        // Build table
        for (int j = 1; (1 << j) <= n; j++) {
            for (int i = 0; i + (1 << j) <= n; i++) {
                st[i][j] = Math.min(st[i][j-1], st[i + (1 << (j-1))][j-1]);
            }
        }
    }

    // O(1) Range Minimum Query!
    int query(int l, int r) {
        int len = r - l + 1;
        int k = log[len];

        return Math.min(st[l][k], st[r - (1 << k) + 1][k]);
    }
}

// Time: Build O(n log n), Query O(1) â­â­â­
// Space: O(n log n)
// Perfect for static RMQ!
```

---

## 2. Monotonic Stack/Deque
**Question:** Find next greater/smaller element efficiently.  
**Intuition:** Maintain **decreasing/increasing stack**. When new element breaks monotonicity, pop and process!  
**Use Cases:**
- Next Greater Element
- Stock Span Problem
- Largest Rectangle in Histogram
- Sliding Window Maximum

**Java:**
```java
// Next Greater Element to Right
int[] nextGreaterElement(int[] arr) {
    int n = arr.length;
    int[] result = new int[n];
    Stack<Integer> stack = new Stack<>();

    for (int i = n - 1; i >= 0; i--) {
        while (!stack.isEmpty() && stack.peek() <= arr[i]) {
            stack.pop();
        }

        result[i] = stack.isEmpty() ? -1 : stack.peek();
        stack.push(arr[i]);
    }

    return result;
}

// Sliding Window Maximum using Deque
int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new ArrayDeque<>();

    for (int i = 0; i < n; i++) {
        // Remove out of window
        if (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }

        // Remove smaller elements (maintain decreasing)
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }

        deque.offerLast(i);

        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }

    return result;
}
```

---

## 3. Square Root Decomposition
**Question:** Alternative to Segment Tree with simpler implementation.  
**Intuition:** Divide array into âˆšn blocks. Store aggregate for each block. Query: full blocks + partial ends. Update: modify element + update its block.  
**Java:**
```java
class SqrtDecomposition {
    int[] arr;
    int[] blocks;
    int blockSize;
    int n;

    public SqrtDecomposition(int[] arr) {
        this.arr = arr;
        n = arr.length;
        blockSize = (int)Math.sqrt(n) + 1;
        blocks = new int[(n + blockSize - 1) / blockSize];

        for (int i = 0; i < n; i++) {
            blocks[i / blockSize] += arr[i];
        }
    }

    void update(int idx, int val) {
        int blockIdx = idx / blockSize;
        blocks[blockIdx] += val - arr[idx];
        arr[idx] = val;
    }

    int query(int l, int r) {
        int sum = 0;
        int leftBlock = l / blockSize;
        int rightBlock = r / blockSize;

        if (leftBlock == rightBlock) {
            for (int i = l; i <= r; i++) {
                sum += arr[i];
            }
        } else {
            // Left partial
            for (int i = l; i < (leftBlock + 1) * blockSize; i++) {
                sum += arr[i];
            }

            // Full blocks
            for (int i = leftBlock + 1; i < rightBlock; i++) {
                sum += blocks[i];
            }

            // Right partial
            for (int i = rightBlock * blockSize; i <= r; i++) {
                sum += arr[i];
            }
        }

        return sum;
    }
}
// Time: Update O(1), Query O(âˆšn)
// Space: O(âˆšn)
// Simpler than Segment Tree but slower queries
```

---

## 4. Mo's Algorithm (Offline Range Queries)
**Question:** Answer multiple range queries efficiently when **order doesn't matter**.  
**Intuition:** Sort queries cleverly! Process in order that minimizes pointer movements. For queries in same âˆšn block, sort by right pointer.  
**Java:**
```java
class Query {
    int l, r, idx;

    Query(int l, int r, int idx) {
        this.l = l;
        this.r = r;
        this.idx = idx;
    }
}

int[] mosAlgorithm(int[] arr, int[][] queries) {
    int n = arr.length;
    int q = queries.length;
    int blockSize = (int)Math.sqrt(n) + 1;

    Query[] queriesList = new Query[q];
    for (int i = 0; i < q; i++) {
        queriesList[i] = new Query(queries[i][0], queries[i][1], i);
    }

    // Mo's ordering
    Arrays.sort(queriesList, (a, b) -> {
        int blockA = a.l / blockSize;
        int blockB = b.l / blockSize;
        if (blockA != blockB) return blockA - blockB;
        return a.r - b.r;
    });

    int[] result = new int[q];
    int currentL = 0, currentR = -1;
    int currentAnswer = 0;

    for (Query query : queriesList) {
        // Expand/Contract range
        while (currentR < query.r) {
            currentR++;
            // Add arr[currentR] to answer
        }
        while (currentR > query.r) {
            // Remove arr[currentR] from answer
            currentR--;
        }
        while (currentL < query.l) {
            // Remove arr[currentL] from answer
            currentL++;
        }
        while (currentL > query.l) {
            currentL--;
            // Add arr[currentL] to answer
        }

        result[query.idx] = currentAnswer;
    }

    return result;
}
// Time: O((N + Q)âˆšN)
// Perfect for frequency, distinct elements queries!
```

---

# Comparison Table

## When to Use Which Data Structure?

| Problem Type | Best Choice | Time | Reason |
|--------------|-------------|------|--------|
| **Union/Find, Connectivity** | DSU | O(Î±(n)) | Nearly constant operations |
| **Range Sum, Point Update** | Fenwick/Segment | O(log n) | Fenwick simpler |
| **Range Min/Max, Updates** | Segment Tree | O(log n) | Fenwick can't do min/max |
| **Range Update, Range Query** | Segment Lazy | O(log n) | Efficient batch updates |
| **Static RMQ (no updates)** | Sparse Table | O(1) query | Unbeatable for static |
| **Next Greater/Smaller** | Monotonic Stack | O(n) | Linear scan |
| **Sliding Window Max** | Monotonic Deque | O(n) | Maintains order |
| **Multiple Range Queries** | Mo's Algorithm | O((N+Q)âˆšN) | Offline, any range operation |
| **Simple Range Ops** | Sqrt Decomposition | O(âˆšn) | Easy to code |

---

## Interview Pattern Recognition

### Pattern 1: Connectivity/Grouping
**Use DSU if:**
- "Are X and Y connected?"
- "Merge two groups"
- "Count connected components"
- "Detect cycles in undirected graph"

### Pattern 2: Range Queries + Updates
**Use Segment Tree if:**
- "Sum/Min/Max in range [L, R]"
- "Update value at index i"
- "Need flexibility (not just prefix)"

### Pattern 3: Prefix Operations
**Use Fenwick Tree if:**
- "Prefix sum up to index i"
- "Range sum using prefix difference"
- "Simpler implementation needed"

### Pattern 4: Static Range Min/Max
**Use Sparse Table if:**
- "No updates, just queries"
- "Need O(1) query time"
- "Can afford O(n log n) preprocessing"

### Pattern 5: Next/Previous Element
**Use Monotonic Stack if:**
- "Next greater element"
- "Nearest smaller to left/right"
- "Stock span, histogram problems"

---

## Common Mistakes & Tips

### DSU Mistakes:
âŒ Forgetting path compression  
âŒ Not using union by rank/size  
âœ… Always find() before comparing roots

### Segment Tree Mistakes:
âŒ Wrong array size (use 4*n to be safe)  
âŒ Forgetting to update parent after child update  
âŒ Off-by-one in range queries  
âœ… Handle no-overlap/complete-overlap carefully

### Fenwick Tree Mistakes:
âŒ Forgetting 1-indexed conversion  
âŒ Using for non-prefix operations  
âŒ Wrong last set bit calculation  
âœ… Remember: `i & (-i)` for last set bit

---

## Advanced Tips

### 1. Coordinate Compression
When values are large but count is small:
```java
// Compress values to ranks
Set<Integer> sorted = new TreeSet<>(Arrays.asList(arr));
Map<Integer, Integer> ranks = new HashMap<>();
int rank = 0;
for (int val : sorted) {
    ranks.put(val, rank++);
}
// Use ranks in Segment/Fenwick Tree
```

### 2. Lazy Propagation
For range updates on Segment Tree:
- Mark parent as "needs update"
- Propagate only when visiting
- Saves O(n) â†’ O(log n) per range update

### 3. 2D Fenwick/Segment
For 2D range queries:
```java
// 2D Fenwick Tree
void update(int x, int y, int val) {
    for (int i = x; i <= n; i += i & (-i)) {
        for (int j = y; j <= m; j += j & (-j)) {
            tree[i][j] += val;
        }
    }
}
```

---
