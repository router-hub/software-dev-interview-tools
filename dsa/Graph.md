
## G-1: Introduction to Graph | Types | Different Conventions Used
**Question:** What are graphs? Explain different types and conventions.  
**Intuition:** Graphs model relationships: cities connected by roads, people connected by friendships, web pages linked by URLs.  
**Logic:** Graphs = vertices (nodes) + edges (connections).  
**Types:** Directed (one-way), Undirected (two-way), Weighted (costs), Unweighted (no costs).

---

## G-2: Graph Representation in C++ | Two Ways to Represent
**Question:** How to represent a graph?  
**Intuition:** We need to know "who is connected to whom". Matrix = table lookup (fast for dense), List = save space (good for sparse).  
**Logic:** Adjacency Matrix (2D array) vs Adjacency List (array of lists).

**Java:**
```java
// Adjacency Matrix
int[][] matrix = new int[n][n];
matrix[u][v] = 1;

// Adjacency List
ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
adj.get(u).add(v);
```

---

## G-3: Graph Representation in Java | Two Ways to Represent
**Question:** How to implement graph in Java?  
**Intuition:** Java ArrayList makes adjacency list clean. For undirected, think "if A connects to B, then B connects to A".  
**Logic:** Use ArrayList of ArrayList. Add both directions for undirected graphs.

**Java:**
```java
ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

// Undirected edge
adj.get(u).add(v);
adj.get(v).add(u);

// For weighted: use int[] {neighbor, weight}
```

---

## G-4: What are Connected Components?
**Question:** Count connected components in undirected graph.  
**Intuition:** Separate friend circles. Each DFS call from unvisited node discovers one complete circle.  
**Logic:** DFS/BFS from each unvisited node. Count = number of DFS calls needed.

**Java:**
```java
void dfs(int u, boolean[] vis, ArrayList<ArrayList<Integer>> adj) {
    vis[u] = true;
    for (int v : adj.get(u))
        if (!vis[v]) dfs(v, vis, adj);
}

int components = 0;
boolean[] vis = new boolean[n];
for (int i = 0; i < n; i++)
    if (!vis[i]) {
        dfs(i, vis, adj);
        components++;
    }
```

---

## G-5: Breadth-First Search (BFS) | C++ and Java
**Question:** How does BFS work?  
**Intuition:** Like ripples in water - explore all neighbors at distance 1, then distance 2, then distance 3... layer by layer.  
**Logic:** Queue processes nodes level-wise.

**Java:**
```java
Queue<Integer> q = new LinkedList<>();
boolean[] vis = new boolean[n];
q.add(start);
vis[start] = true;

while (!q.isEmpty()) {
    int u = q.poll();
    for (int v : adj.get(u)) {
        if (!vis[v]) {
            vis[v] = true;
            q.add(v);
        }
    }
}
```

---

## G-6: Depth-First Search (DFS) | C++ and Java
**Question:** How does DFS work?  
**Intuition:** Like exploring a maze - go as deep as you can along one path, then backtrack when stuck.  
**Logic:** Recursion explores deeply before returning.

**Java:**
```java
void dfs(int u, boolean[] vis, ArrayList<ArrayList<Integer>> adj) {
    vis[u] = true;
    for (int v : adj.get(u))
        if (!vis[v]) dfs(v, vis, adj);
}
```

---

## G-7: Number of Provinces | C++ | Java
**Question:** Count provinces (connected city groups).  
**Intuition:** Same as connected components - each isolated group is one province.  
**Logic:** DFS from each unvisited city, count calls.

**Java:**
```java
void dfs(int u, boolean[] vis, int[][] isConnected) {
    vis[u] = true;
    for (int v = 0; v < isConnected.length; v++)
        if (isConnected[u][v] == 1 && !vis[v])
            dfs(v, vis, isConnected);
}

int provinces = 0;
for (int i = 0; i < n; i++)
    if (!vis[i]) {
        dfs(i, vis, isConnected);
        provinces++;
    }
```

---

## G-8: Number of Islands | Online Queries | DSU
**Question:** Handle dynamic island additions.  
**Intuition:** DSU lets us merge components efficiently. Each new land connects to neighbors, reducing island count.  
**Logic:** Union-Find for dynamic connectivity.

**Java:**
```java
class DSU {
    int[] par, rank;
    DSU(int n) {
        par = new int[n]; rank = new int[n];
        for (int i = 0; i < n; i++) par[i] = i;
    }
    int find(int x) {
        if (par[x] != x) par[x] = find(par[x]);
        return par[x];
    }
    boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false;
        if (rank[px] < rank[py]) par[px] = py;
        else if (rank[px] > rank[py]) par[py] = px;
        else { par[py] = px; rank[px]++; }
        return true;
    }
}
```

---

## G-9: Flood Fill Algorithm | C++ | Java
**Question:** Fill region with new color.  
**Intuition:** Paint bucket tool - fill starting pixel and all connected pixels of same color.  
**Logic:** DFS changes color and spreads to 4-directional same-color neighbors.

**Java:**
```java
void dfs(int[][] image, int sr, int sc, int newColor, int oldColor) {
    if (sr < 0 || sc < 0 || sr >= image.length || sc >= image[0].length) return;
    if (image[sr][sc] != oldColor) return;

    image[sr][sc] = newColor;
    dfs(image, sr+1, sc, newColor, oldColor);
    dfs(image, sr-1, sc, newColor, oldColor);
    dfs(image, sr, sc+1, newColor, oldColor);
    dfs(image, sr, sc-1, newColor, oldColor);
}
```

---

## G-10: Rotten Oranges | C++ | Java
**Question:** Minimum time to rot all oranges.  
**Intuition:** All rotten oranges simultaneously infect neighbors. Think of pandemic spreading - multiple sources at once, not one by one.  
**Logic:** Multi-source BFS. Each level = 1 minute.

**Java:**
```java
Queue<int[]> q = new LinkedList<>();
int fresh = 0;

// Add all rotten oranges
for (int i = 0; i < m; i++)
    for (int j = 0; j < n; j++) {
        if (grid[i][j] == 2) q.add(new int[]{i, j});
        else if (grid[i][j] == 1) fresh++;
    }

int time = 0;
int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}};

while (!q.isEmpty() && fresh > 0) {
    int size = q.size();
    for (int i = 0; i < size; i++) {
        int[] cur = q.poll();
        for (int[] d : dirs) {
            int nx = cur[0] + d[0], ny = cur[1] + d[1];
            if (nx >= 0 && ny >= 0 && nx < m && ny < n && grid[nx][ny] == 1) {
                grid[nx][ny] = 2;
                fresh--;
                q.add(new int[]{nx, ny});
            }
        }
    }
    time++;
}
return fresh == 0 ? time : -1;
```

---

## G-11: Detect Cycle in Undirected Graph | BFS
**Question:** Detect cycle using BFS.  
**Intuition:** If we reach an already visited node that's not our immediate parent, we found a back edge = cycle.  
**Logic:** BFS with parent tracking.

**Java:**
```java
Queue<int[]> q = new LinkedList<>();
q.add(new int[]{start, -1}); // {node, parent}
vis[start] = true;

while (!q.isEmpty()) {
    int[] pair = q.poll();
    int node = pair[0], parent = pair[1];
    for (int v : adj.get(node)) {
        if (!vis[v]) {
            vis[v] = true;
            q.add(new int[]{v, node});
        } else if (v != parent) {
            return true; // cycle found
        }
    }
}
```

---

## G-12: Detect Cycle in Undirected Graph | DFS
**Question:** Detect cycle using DFS.  
**Intuition:** Same idea - visited neighbor (except parent) means we came back via different path = cycle.  
**Logic:** DFS with parent check.

**Java:**
```java
boolean dfs(int u, int parent, boolean[] vis, ArrayList<ArrayList<Integer>> adj) {
    vis[u] = true;
    for (int v : adj.get(u)) {
        if (!vis[v]) {
            if (dfs(v, u, vis, adj)) return true;
        } else if (v != parent) {
            return true;
        }
    }
    return false;
}
```

---

## G-13: Distance of nearest 1 | 0/1 Matrix
**Question:** Find distance to nearest 1 for each cell.  
**Intuition:** Instead of checking from each 0, start from all 1s simultaneously - they spread their distances outward.  
**Logic:** Multi-source BFS from all 1s.

**Java:**
```java
Queue<int[]> q = new LinkedList<>();
int[][] dist = new int[m][n];
boolean[][] vis = new boolean[m][n];

// Start from all 1s
for (int i = 0; i < m; i++)
    for (int j = 0; j < n; j++)
        if (mat[i][j] == 1) {
            q.add(new int[]{i, j, 0});
            vis[i][j] = true;
        }

int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}};
while (!q.isEmpty()) {
    int[] cur = q.poll();
    dist[cur[0]][cur[1]] = cur[2];
    for (int[] d : dirs) {
        int nx = cur[0] + d[0], ny = cur[1] + d[1];
        if (nx >= 0 && ny >= 0 && nx < m && ny < n && !vis[nx][ny]) {
            vis[nx][ny] = true;
            q.add(new int[]{nx, ny, cur[2] + 1});
        }
    }
}
```

---

## G-14: Surrounded Regions | Replace O's with X's
**Question:** Convert surrounded O's to X's.  
**Intuition:** O's on border can't be surrounded. Mark them + their connected O's as safe, convert rest.  
**Logic:** DFS/BFS from border O's, mark safe, flip others.

**Java:**
```java
void dfs(char[][] board, int i, int j) {
    if (i < 0 || j < 0 || i >= board.length || j >= board[0].length || board[i][j] != 'O') return;
    board[i][j] = 'S'; // mark safe
    dfs(board, i+1, j); dfs(board, i-1, j);
    dfs(board, i, j+1); dfs(board, i, j-1);
}

// Mark border-connected O's
for (int i = 0; i < m; i++) {
    if (board[i][0] == 'O') dfs(board, i, 0);
    if (board[i][n-1] == 'O') dfs(board, i, n-1);
}
for (int j = 0; j < n; j++) {
    if (board[0][j] == 'O') dfs(board, 0, j);
    if (board[m-1][j] == 'O') dfs(board, m-1, j);
}

// Convert O to X, S back to O
for (int i = 0; i < m; i++)
    for (int j = 0; j < n; j++) {
        if (board[i][j] == 'O') board[i][j] = 'X';
        if (board[i][j] == 'S') board[i][j] = 'O';
    }
```

---

## G-15: Number of Enclaves
**Question:** Count land cells that can't reach boundary.  
**Intuition:** Opposite of surrounded regions - mark boundary-reachable lands, count unmarked.  
**Logic:** DFS from boundary lands, count remaining.

**Java:**
```java
// Similar to G-14, DFS from all boundary 1s
// Count remaining 1s
```

---

## G-16: Number of Distinct Islands
**Question:** Count unique island shapes.  
**Intuition:** Two islands are same if one can be translated to match the other. Record shape as relative positions.  
**Logic:** DFS records positions relative to start, store in set.

**Java:**
```java
void dfs(int i, int j, int baseI, int baseJ, String path, char[][] grid, Set<String> shape) {
    if (i < 0 || j < 0 || i >= grid.length || j >= grid[0].length || grid[i][j] != '1') return;
    grid[i][j] = '0';
    shape.add((i-baseI) + "," + (j-baseJ));
    dfs(i+1, j, baseI, baseJ, path+"D", grid, shape);
    // ... all 4 directions
}

Set<String> uniqueIslands = new HashSet<>();
for (int i = 0; i < m; i++)
    for (int j = 0; j < n; j++)
        if (grid[i][j] == '1') {
            Set<String> shape = new HashSet<>();
            dfs(i, j, i, j, "", grid, shape);
            uniqueIslands.add(shape.toString());
        }
```

---

## G-17: Bipartite Graph | BFS
**Question:** Can graph be 2-colored with no adjacent same color?  
**Intuition:** Try coloring with 2 colors. If neighbors have same color, impossible = not bipartite.  
**Logic:** BFS coloring, adjacent nodes get opposite colors.

**Java:**
```java
int[] color = new int[n];
Arrays.fill(color, -1);
Queue<Integer> q = new LinkedList<>();
q.add(start); color[start] = 0;

while (!q.isEmpty()) {
    int u = q.poll();
    for (int v : adj.get(u)) {
        if (color[v] == -1) {
            color[v] = 1 - color[u];
            q.add(v);
        } else if (color[v] == color[u]) {
            return false;
        }
    }
}
return true;
```

---

## G-18: Bipartite Graph | DFS
**Question:** Check bipartite using DFS.  
**Intuition:** Same coloring logic, use DFS instead of BFS.  
**Logic:** DFS with 2-coloring.

**Java:**
```java
boolean dfs(int u, int col, int[] color, ArrayList<ArrayList<Integer>> adj) {
    color[u] = col;
    for (int v : adj.get(u)) {
        if (color[v] == -1) {
            if (!dfs(v, 1 - col, color, adj)) return false;
        } else if (color[v] == col) {
            return false;
        }
    }
    return true;
}
```

---

## G-19: Detect Cycle in Directed Graph | DFS
**Question:** Detect cycle in directed graph.  
**Intuition:** Keep track of current DFS path (recursion stack). If we revisit a node in same path, cycle exists.  
**Logic:** Use visited + recursion stack arrays.

**Java:**
```java
boolean dfs(int u, boolean[] vis, boolean[] recStack, ArrayList<ArrayList<Integer>> adj) {
    vis[u] = true;
    recStack[u] = true;

    for (int v : adj.get(u)) {
        if (!vis[v] && dfs(v, vis, recStack, adj)) return true;
        else if (recStack[v]) return true;
    }

    recStack[u] = false;
    return false;
}
```

---

## G-20: Find Eventual Safe States | BFS
**Question:** Find nodes from which all paths lead to terminal nodes.  
**Intuition:** Reverse the graph! Terminal nodes have no outgoing edges. Work backwards to find what leads only to safe nodes.  
**Logic:** Reverse graph + topological sort approach.

**Java:**
```java
// Reverse all edges
// Use Kahn's algorithm (topo sort with in-degree)
// Safe nodes are those processed successfully
```

---

## G-21: Topological Sort | DFS
**Question:** Linear ordering where u before v for every edge uâ†’v.  
**Intuition:** Finish processing all descendants before adding current node. Reverse gives topo order.  
**Logic:** DFS postorder, push to stack.

**Java:**
```java
void dfs(int u, boolean[] vis, Stack<Integer> stack, ArrayList<ArrayList<Integer>> adj) {
    vis[u] = true;
    for (int v : adj.get(u))
        if (!vis[v]) dfs(v, vis, stack, adj);
    stack.push(u);
}

Stack<Integer> stack = new Stack<>();
for (int i = 0; i < n; i++)
    if (!vis[i]) dfs(i, vis, stack, adj);
// Stack contains topo order
```

---

## G-22: Topological Sort | Kahn's Algorithm | BFS
**Question:** Topo sort using BFS.  
**Intuition:** Process nodes with no dependencies (in-degree 0) first. Remove them, repeat.  
**Logic:** Track in-degrees, process zero in-degree nodes.

**Java:**
```java
int[] inDeg = new int[n];
for (List<Integer> nbrs : adj)
    for (int v : nbrs) inDeg[v]++;

Queue<Integer> q = new LinkedList<>();
for (int i = 0; i < n; i++)
    if (inDeg[i] == 0) q.add(i);

ArrayList<Integer> order = new ArrayList<>();
while (!q.isEmpty()) {
    int u = q.poll();
    order.add(u);
    for (int v : adj.get(u))
        if (--inDeg[v] == 0) q.add(v);
}
```

---

## G-23: Detect Cycle | Directed Graph | Topo Sort
**Question:** Use topo sort to detect cycle.  
**Intuition:** If topo sort can't process all nodes (some have non-zero in-degree), cycle exists.  
**Logic:** Kahn's algorithm; if output size < n, cycle exists.

**Java:**
```java
// Run Kahn's algorithm
// if (order.size() != n) return true; // cycle exists
```

---

## G-24: Course Schedule I and II | Prerequisites
**Question:** Can finish all courses with prerequisites?  
**Intuition:** Prerequisites = directed edges. Cycle means circular dependency = impossible.  
**Logic:** Build graph, detect cycle using topo sort.

**Java:**
```java
// Build adjacency list from prerequisites
// Apply Kahn's algorithm
// If order.size() == numCourses, possible
```

---

## G-25: Find Eventual Safe States | DFS
**Question:** Find safe nodes using DFS.  
**Intuition:** Node is safe if no path leads to cycle. Use DFS to mark safe/unsafe nodes.  
**Logic:** DFS with safe array tracking.

**Java:**
```java
boolean dfs(int u, int[] safe, ArrayList<ArrayList<Integer>> adj) {
    if (safe[u] != 0) return safe[u] == 2;
    safe[u] = 1; // visiting
    for (int v : adj.get(u))
        if (!dfs(v, safe, adj)) {
            safe[u] = 3; // unsafe
            return false;
        }
    safe[u] = 2; // safe
    return true;
}
```

---

## G-26: Alien Dictionary | Topological Sort
**Question:** Derive character order from sorted alien words.  
**Intuition:** Compare adjacent words to find which char comes before which. Build graph, topo sort gives order.  
**Logic:** Extract char precedence, build graph, topo sort.

**Java:**
```java
// Compare words[i] and words[i+1]
// Find first differing char: ch1 -> ch2
// Build graph, apply topo sort
```

---

## G-27: Shortest Path in DAG | Topo Sort
**Question:** Shortest paths in DAG.  
**Intuition:** Process nodes in topo order, relax edges. No need for Dijkstra since DAG = no cycles.  
**Logic:** Topo sort + edge relaxation.

**Java:**
```java
// Get topo order
// dist[src] = 0, rest = INF
// For each node u in topo order:
//     for each edge (u,v,w):
//         dist[v] = min(dist[v], dist[u] + w)
```

---

## G-28: Shortest Path in Unweighted Graph | BFS
**Question:** Shortest path in unweighted graph.  
**Intuition:** BFS naturally finds shortest path since all edges cost 1.  
**Logic:** BFS from source, track distance.

**Java:**
```java
int[] dist = new int[n];
Arrays.fill(dist, -1);
Queue<Integer> q = new LinkedList<>();
q.add(src); dist[src] = 0;

while (!q.isEmpty()) {
    int u = q.poll();
    for (int v : adj.get(u))
        if (dist[v] == -1) {
            dist[v] = dist[u] + 1;
            q.add(v);
        }
}
```

---

## G-29: Word Ladder I
**Question:** Minimum transformations from start to end word.  
**Intuition:** Each valid transformation is one step. BFS gives shortest path.  
**Logic:** BFS trying all possible one-letter changes.

**Java:**
```java
Queue<String> q = new LinkedList<>();
Set<String> visited = new HashSet<>();
q.add(beginWord);
int level = 1;

while (!q.isEmpty()) {
    int size = q.size();
    for (int i = 0; i < size; i++) {
        String word = q.poll();
        if (word.equals(endWord)) return level;

        for (int j = 0; j < word.length(); j++) {
            char[] arr = word.toCharArray();
            for (char c = 'a'; c <= 'z'; c++) {
                arr[j] = c;
                String newWord = new String(arr);
                if (wordSet.contains(newWord) && !visited.contains(newWord)) {
                    visited.add(newWord);
                    q.add(newWord);
                }
            }
        }
    }
    level++;
}
```

---

## G-30: Word Ladder II
**Question:** Find all shortest transformation sequences.  
**Intuition:** BFS to find shortest distance + build parent map. Backtrack from end to get all paths.  
**Logic:** BFS builds level structure, backtracking generates paths.

**Java:**
```java
// BFS to build parent relationships
// Backtrack from endWord to beginWord collecting all paths
```

---

## G-31: Shortest Path in Binary Maze
**Question:** Shortest path in 0/1 matrix.  
**Intuition:** BFS on grid where 0=walkable, 1=blocked.  
**Logic:** BFS with 4/8-directional movement.

**Java:**
```java
// BFS from start to end
// Check bounds and grid[nx][ny] == 0 before adding to queue
```

---


## G-32: Dijkstra's Algorithm | Priority Queue
**Question:** Shortest paths in weighted graph (positive weights).  
**Intuition:** Greedy approach - always expand the closest unvisited node. Works because weights are positive (no surprises later).  
**Logic:** Min-heap/Priority Queue to get minimum distance node efficiently.

**Java:**
```java
PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
int[] dist = new int[n];
Arrays.fill(dist, Integer.MAX_VALUE);
dist[src] = 0;
pq.add(new int[]{src, 0});

while (!pq.isEmpty()) {
    int[] cur = pq.poll();
    int u = cur[0], d = cur[1];
    if (d > dist[u]) continue;

    for (int[] edge : adj.get(u)) { // {neighbor, weight}
        int v = edge[0], w = edge[1];
        if (dist[v] > dist[u] + w) {
            dist[v] = dist[u] + w;
            pq.add(new int[]{v, dist[v]});
        }
    }
}
```

---

## G-33: Dijkstra's Algorithm | Using Set
**Question:** Implement Dijkstra using Set.  
**Intuition:** Set allows removal of outdated entries, slightly more efficient than PQ with duplicates.  
**Logic:** Similar to PQ but can erase old distances.

---

## G-34: Dijkstra - Why PQ not Q?
**Question:** Why Priority Queue instead of normal Queue?  
**Intuition:** Normal queue processes FIFO, might relax longer paths first. PQ ensures shortest unexplored path is always processed.  
**Logic:** Greedy choice requires minimum selection.

---

## G-35: Print Shortest Path | Dijkstra
**Question:** Print the actual path, not just distance.  
**Intuition:** Track parent of each node during relaxation. Backtrack from destination to source.  
**Logic:** Maintain parent array alongside distance array.

**Java:**
```java
int[] parent = new int[n];
Arrays.fill(parent, -1);

// During relaxation:
if (dist[v] > dist[u] + w) {
    dist[v] = dist[u] + w;
    parent[v] = u;
    pq.add(new int[]{v, dist[v]});
}

// Reconstruct path
ArrayList<Integer> path = new ArrayList<>();
int node = dest;
while (node != -1) {
    path.add(node);
    node = parent[node];
}
Collections.reverse(path);
```

---

## G-36: Shortest Distance in Binary Maze
**Question:** Shortest path in 0/1 matrix.  
**Intuition:** Grid graph where cells are nodes. BFS for unweighted, Dijkstra if different costs.  
**Logic:** Apply BFS with bound checks.

**Java:**
```java
Queue<int[]> q = new LinkedList<>();
int[][] dist = new int[m][n];
for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);

q.add(new int[]{startX, startY});
dist[startX][startY] = 0;

int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}};
while (!q.isEmpty()) {
    int[] cur = q.poll();
    int x = cur[0], y = cur[1];

    for (int[] d : dirs) {
        int nx = x + d[0], ny = y + d[1];
        if (nx >= 0 && ny >= 0 && nx < m && ny < n && grid[nx][ny] == 0) {
            if (dist[nx][ny] > dist[x][y] + 1) {
                dist[nx][ny] = dist[x][y] + 1;
                q.add(new int[]{nx, ny});
            }
        }
    }
}
```

---

## G-37: Path With Minimum Effort
**Question:** Minimize maximum absolute difference along path.  
**Intuition:** Not about total cost, but maximum single step. Modified Dijkstra where "distance" = max effort encountered so far.  
**Logic:** Dijkstra with effort as priority instead of distance sum.

**Java:**
```java
PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
int[][] effort = new int[m][n];
for (int[] row : effort) Arrays.fill(row, Integer.MAX_VALUE);

pq.add(new int[]{0, 0, 0}); // {x, y, maxEffort}
effort[0][0] = 0;

while (!pq.isEmpty()) {
    int[] cur = pq.poll();
    int x = cur[0], y = cur[1], maxEff = cur[2];

    if (x == m-1 && y == n-1) return maxEff;
    if (maxEff > effort[x][y]) continue;

    for (int[] d : dirs) {
        int nx = x + d[0], ny = y + d[1];
        if (nx >= 0 && ny >= 0 && nx < m && ny < n) {
            int newEffort = Math.max(maxEff, Math.abs(heights[nx][ny] - heights[x][y]));
            if (newEffort < effort[nx][ny]) {
                effort[nx][ny] = newEffort;
                pq.add(new int[]{nx, ny, newEffort});
            }
        }
    }
}
```

---

## G-38: Cheapest Flights Within K Stops
**Question:** Cheapest flight with at most K stops.  
**Intuition:** Can't use pure Dijkstra because we need to track stops. Use modified BFS/Dijkstra with stop limit.  
**Logic:** Track (node, cost, stops). Process if stops â‰¤ K.

**Java:**
```java
Queue<int[]> q = new LinkedList<>(); // {node, cost, stops}
int[] minCost = new int[n];
Arrays.fill(minCost, Integer.MAX_VALUE);

q.add(new int[]{src, 0, 0});

while (!q.isEmpty()) {
    int[] cur = q.poll();
    int node = cur[0], cost = cur[1], stops = cur[2];

    if (stops > k) continue;

    for (int[] edge : adj.get(node)) {
        int next = edge[0], price = edge[1];
        int newCost = cost + price;
        if (newCost < minCost[next]) {
            minCost[next] = newCost;
            q.add(new int[]{next, newCost, stops + 1});
        }
    }
}
return minCost[dst] == Integer.MAX_VALUE ? -1 : minCost[dst];
```

---

## G-39: Minimum Multiplications to Reach End
**Question:** Minimum steps multiplying by given numbers (mod 10000).  
**Intuition:** Numbers are nodes, multiplications are edges. BFS in number space.  
**Logic:** BFS where each number generates new numbers via multiplication.

**Java:**
```java
Queue<int[]> q = new LinkedList<>(); // {number, steps}
int[] dist = new int[10000];
Arrays.fill(dist, -1);

q.add(new int[]{start, 0});
dist[start] = 0;

while (!q.isEmpty()) {
    int[] cur = q.poll();
    int num = cur[0], steps = cur[1];

    if (num == end) return steps;

    for (int mult : arr) {
        int newNum = (num * mult) % 10000;
        if (dist[newNum] == -1) {
            dist[newNum] = steps + 1;
            q.add(new int[]{newNum, steps + 1});
        }
    }
}
```

---

## G-40: Number of Ways to Arrive at Destination
**Question:** Count shortest paths to destination.  
**Intuition:** Modified Dijkstra - when we find same distance via different path, add the path counts.  
**Logic:** Track distance + count of paths achieving that distance.

**Java:**
```java
long[] dist = new long[n];
long[] ways = new long[n];
Arrays.fill(dist, Long.MAX_VALUE);
dist[0] = 0; ways[0] = 1;

PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[1]));
pq.add(new long[]{0, 0});

while (!pq.isEmpty()) {
    long[] cur = pq.poll();
    int u = (int)cur[0];
    long d = cur[1];

    if (d > dist[u]) continue;

    for (int[] edge : adj.get(u)) {
        int v = edge[0];
        long w = edge[1];

        if (dist[v] > dist[u] + w) {
            dist[v] = dist[u] + w;
            ways[v] = ways[u];
            pq.add(new long[]{v, dist[v]});
        } else if (dist[v] == dist[u] + w) {
            ways[v] = (ways[v] + ways[u]) % MOD;
        }
    }
}
```

---

## G-41: Bellman-Ford Algorithm
**Question:** Shortest paths with negative edges; detect negative cycles.  
**Intuition:** Relax all edges n-1 times. If any edge can still be relaxed after that, negative cycle exists (infinite reduction possible).  
**Logic:** Iterate n-1 times over all edges, relax. Check nth iteration for cycle.

**Java:**
```java
int[] dist = new int[n];
Arrays.fill(dist, Integer.MAX_VALUE);
dist[src] = 0;

// Relax n-1 times
for (int i = 1; i <= n - 1; i++) {
    for (int[] edge : edges) {
        int u = edge[0], v = edge[1], w = edge[2];
        if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
            dist[v] = dist[u] + w;
        }
    }
}

// Check for negative cycle
for (int[] edge : edges) {
    int u = edge[0], v = edge[1], w = edge[2];
    if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
        return true; // negative cycle exists
    }
}
```

---

## G-42: Floyd-Warshall Algorithm
**Question:** All-pairs shortest paths.  
**Intuition:** Try every node as intermediate. If going via k is shorter than direct, update. DP over all triplets (i, j, k).  
**Logic:** Triple nested loop - for each pair (i,j), try all k as intermediate.

**Java:**
```java
int[][] dist = new int[n][n];
// Initialize with adjacency matrix

for (int k = 0; k < n; k++)
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
            if (dist[i][k] != INF && dist[k][j] != INF)
                dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
```

---

## G-43: City With Smallest Number of Neighbors at Threshold
**Question:** Find city with fewest reachable cities within distance threshold.  
**Intuition:** Need all-pairs distances â†’ Floyd-Warshall. Then count reachable cities for each.  
**Logic:** Apply Floyd-Warshall, count distances â‰¤ threshold for each city.

**Java:**
```java
// Run Floyd-Warshall
int minReachable = n, result = 0;
for (int i = 0; i < n; i++) {
    int count = 0;
    for (int j = 0; j < n; j++)
        if (i != j && dist[i][j] <= distanceThreshold)
            count++;
    if (count <= minReachable) {
        minReachable = count;
        result = i;
    }
}
```

---

## G-44: Minimum Spanning Tree | Theory
**Question:** What is MST?  
**Intuition:** Connect all nodes with minimum total edge weight using exactly n-1 edges (tree property).  
**Logic:** Subset of edges forming tree with minimum weight sum.

---

## G-45: Prim's Algorithm | MST
**Question:** Find MST using Prim's.  
**Intuition:** Start from any node, greedily add the cheapest edge connecting tree to new node. Repeat until all nodes included.  
**Logic:** Priority queue for minimum edge weight selection.

**Java:**
```java
PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
boolean[] inMST = new boolean[n];
pq.add(new int[]{0, 0}); // {node, weight}
int mstCost = 0;

while (!pq.isEmpty()) {
    int[] cur = pq.poll();
    int u = cur[0], w = cur[1];
    if (inMST[u]) continue;

    inMST[u] = true;
    mstCost += w;

    for (int[] edge : adj.get(u)) {
        int v = edge[0], wt = edge[1];
        if (!inMST[v]) pq.add(new int[]{v, wt});
    }
}
```

---

## G-46: Disjoint Set | Union by Rank | Path Compression
**Question:** Implement efficient Union-Find.  
**Intuition:** Track components with near-constant time operations. Path compression flattens trees, union by rank keeps trees balanced.  
**Logic:** Parent array + rank array + path compression in find.

**Java:**
```java
class DSU {
    int[] par, rank;

    DSU(int n) {
        par = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) par[i] = i;
    }

    int find(int x) {
        if (par[x] != x) par[x] = find(par[x]); // path compression
        return par[x];
    }

    void union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return;
        // Union by rank
        if (rank[px] < rank[py]) par[px] = py;
        else if (rank[px] > rank[py]) par[py] = px;
        else { par[py] = px; rank[px]++; }
    }
}
```

---

## G-47: Kruskal's Algorithm | MST
**Question:** Find MST using Kruskal's.  
**Intuition:** Sort all edges by weight. Add edges greedily if they don't create cycle (use DSU to check).  
**Logic:** Sort edges + DSU for cycle detection.

**Java:**
```java
Collections.sort(edges, Comparator.comparingInt(a -> a[2]));
DSU dsu = new DSU(n);
int mstCost = 0;
int edgesUsed = 0;

for (int[] edge : edges) {
    int u = edge[0], v = edge[1], w = edge[2];
    if (dsu.find(u) != dsu.find(v)) {
        dsu.union(u, v);
        mstCost += w;
        edgesUsed++;
        if (edgesUsed == n - 1) break;
    }
}
```

---

## G-48: Number of Provinces | DSU
**Question:** Count provinces using DSU.  
**Intuition:** Union connected cities. Count unique parents = number of components.  
**Logic:** DSU operations + count unique roots.

---

## G-49: Network Connected | DSU
**Question:** Minimum operations to connect network.  
**Intuition:** Need n-1 edges for n computers. Count extra edges and components, check if enough edges to connect.  
**Logic:** Count components using DSU, check if extraEdges â‰¥ components-1.

---

## G-50: Accounts Merge | DSU
**Question:** Merge accounts by common emails.  
**Intuition:** Emails are edges connecting accounts. DSU groups accounts by common emails.  
**Logic:** Map emails to account indices, union accounts with common emails.

---

## G-51: Number of Islands II | DSU
**Question:** Handle online island additions.  
**Intuition:** Start with all water. Add lands one by one, union with adjacent lands, track component count.  
**Logic:** DSU with dynamic additions.

---

## G-52: Making Large Island | DSU
**Question:** Largest island after changing one 0 to 1.  
**Intuition:** Find all island sizes using DSU. For each 0, check neighbor island sizes, sum them up.  
**Logic:** DSU for islands + try flipping each 0.

---

## G-53: Most Stones Removed
**Question:** Max stones removable (same row/col).  
**Intuition:** Stones in same row/col form one component. Can remove all but one from each component.  
**Logic:** Use DSU, answer = totalStones - numberOfComponents.

---

## G-54: Strongly Connected Components | Kosaraju
**Question:** Find all SCCs.  
**Intuition:** Nodes in SCC can all reach each other. First pass finds finish order, second pass on reversed graph identifies SCCs.  
**Logic:** DFS twice - once for ordering, once on reversed graph.

**Java:**
```java
// Step 1: DFS to get finish order
void dfs1(int u, boolean[] vis, Stack<Integer> stack, ArrayList<ArrayList<Integer>> adj) {
    vis[u] = true;
    for (int v : adj.get(u))
        if (!vis[v]) dfs1(v, vis, stack, adj);
    stack.push(u);
}

// Step 2: DFS on reversed graph
void dfs2(int u, boolean[] vis, ArrayList<ArrayList<Integer>> revAdj) {
    vis[u] = true;
    for (int v : revAdj.get(u))
        if (!vis[v]) dfs2(v, vis, revAdj);
}

// Main: Fill stack with finish order, reverse graph, DFS in stack order
```

---

## G-55: Bridges in Graph | Tarjan's Algorithm
**Question:** Find all bridges (cut-edges).  
**Intuition:** Bridge removal disconnects graph. Use DFS with discovery time and lowest reachable ancestor time. If child can't reach back to ancestors, edge is bridge.  
**Logic:** DFS with tin[] (discovery time) and low[] (lowest reachable) arrays.

**Java:**
```java
int timer = 0;

void dfs(int u, int parent, int[] tin, int[] low, boolean[] vis, ArrayList<ArrayList<Integer>> adj, List<List<Integer>> bridges) {
    vis[u] = true;
    tin[u] = low[u] = timer++;

    for (int v : adj.get(u)) {
        if (v == parent) continue;
        if (!vis[v]) {
            dfs(v, u, tin, low, vis, adj, bridges);
            low[u] = Math.min(low[u], low[v]);

            // Bridge condition
            if (low[v] > tin[u]) {
                bridges.add(Arrays.asList(u, v));
            }
        } else {
            low[u] = Math.min(low[u], tin[v]);
        }
    }
}
```

---

## G-56: Articulation Points
**Question:** Find all cut-vertices.  
**Intuition:** Articulation point removal increases components. Check if any child subtree can't reach back without going through current node.  
**Logic:** DFS with tin[] and low[]. Check condition: low[child] â‰¥ tin[u].

**Java:**
```java
void dfs(int u, int parent, int[] tin, int[] low, boolean[] vis, boolean[] artPoint, ArrayList<ArrayList<Integer>> adj) {
    vis[u] = true;
    tin[u] = low[u] = timer++;
    int children = 0;

    for (int v : adj.get(u)) {
        if (v == parent) continue;
        if (!vis[v]) {
            children++;
            dfs(v, u, tin, low, vis, artPoint, adj);
            low[u] = Math.min(low[u], low[v]);

            // Articulation point condition
            if (parent != -1 && low[v] >= tin[u]) {
                artPoint[u] = true;
            }
        } else {
            low[u] = Math.min(low[u], tin[v]);
        }
    }

    // Root with multiple children
    if (parent == -1 && children > 1) {
        artPoint[u] = true;
    }
}
```

---