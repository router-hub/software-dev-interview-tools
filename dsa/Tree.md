
## Table of Contents
- **Part 1: Tree Fundamentals (L1-L3)** - Introduction & Representation
- **Part 2: Tree Traversals (L4-L11)** - All Traversal Techniques  
- **Part 3: Medium Problems (L12-L25)** - Core Tree Problems
- **Part 4: Hard Problems (L26-L33)** - Advanced Concepts
- **Part 5: Binary Search Tree (L34-L39)** - BST Operations

---

# Part 1: Tree Fundamentals

## L1: Introduction to Trees
**Question:** What is a tree? What are different types of trees?  
**Intuition:** Tree is a **hierarchical data structure** with nodes connected by edges. Unlike linear structures (array, linkedlist), trees are non-linear. Key terms: **Root** (topmost node), **Parent/Child** (connected nodes), **Leaf** (nodes with no children), **Height** (max edges from root to leaf), **Depth** (edges from root to node). Types: **Binary Tree** (max 2 children), **Complete Binary Tree** (all levels filled except possibly last, which is left-filled), **Full Binary Tree** (every node has 0 or 2 children), **Perfect Binary Tree** (all levels completely filled), **Balanced Binary Tree** (height difference between left and right subtrees ≤ 1).  
**Logic:** Understanding tree terminology is foundation for all tree problems!  
**Java:**
```java
// Tree Node Structure
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
        this.left = null;
        this.right = null;
    }
}
```

---

## L2 & L3: Binary Tree Representation
**Question:** How to represent binary tree in code?  
**Intuition:** Each node contains: (1) data value, (2) pointer to left child, (3) pointer to right child. This simple structure enables entire tree representation!  
**Logic:** Use class/struct with val, left, right pointers.  
**Java:**
```java
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
        this.left = null;
        this.right = null;
    }
}

// Creating a tree
TreeNode root = new TreeNode(1);
root.left = new TreeNode(2);
root.right = new TreeNode(3);
root.left.left = new TreeNode(4);
root.left.right = new TreeNode(5);
```

---

# Part 2: Tree Traversals (Foundation!)

## L4: Preorder Traversal (Root → Left → Right)
**Question:** Traverse tree in Root-Left-Right order.  
**Intuition:** Process root FIRST, then recursively traverse left subtree, then right subtree. Think "print before going deeper".  
**Logic:** (1) Process current node, (2) Recurse left, (3) Recurse right.  
**Java:**
```java
// Recursive
void preorder(TreeNode root, List<Integer> result) {
    if (root == null) return;

    result.add(root.val);        // Root
    preorder(root.left, result);  // Left
    preorder(root.right, result); // Right
}

// Iterative using Stack
List<Integer> preorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Stack<TreeNode> stack = new Stack<>();
    stack.push(root);

    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        result.add(node.val);

        if (node.right != null) stack.push(node.right); // Right first!
        if (node.left != null) stack.push(node.left);   // Then left
    }

    return result;
}
// Time: O(n), Space: O(h) where h = height
```

---

## L5: Inorder Traversal (Left → Root → Right)
**Question:** Traverse tree in Left-Root-Right order.  
**Intuition:** Process left subtree FIRST, then root, then right. For **BST, gives sorted order**! Most important traversal.  
**Logic:** (1) Recurse left, (2) Process current node, (3) Recurse right.  
**Java:**
```java
// Recursive
void inorder(TreeNode root, List<Integer> result) {
    if (root == null) return;

    inorder(root.left, result);  // Left
    result.add(root.val);         // Root
    inorder(root.right, result);  // Right
}

// Iterative using Stack
List<Integer> inorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    Stack<TreeNode> stack = new Stack<>();
    TreeNode curr = root;

    while (curr != null || !stack.isEmpty()) {
        while (curr != null) {
            stack.push(curr);
            curr = curr.left;
        }

        curr = stack.pop();
        result.add(curr.val);
        curr = curr.right;
    }

    return result;
}
// Time: O(n), Space: O(h)
```

---

## L6: Postorder Traversal (Left → Right → Root)
**Question:** Traverse tree in Left-Right-Root order.  
**Intuition:** Process left subtree, then right subtree, THEN root. Root processed LAST. Useful for tree deletion, calculating subtree properties.  
**Logic:** (1) Recurse left, (2) Recurse right, (3) Process current node.  
**Java:**
```java
// Recursive
void postorder(TreeNode root, List<Integer> result) {
    if (root == null) return;

    postorder(root.left, result);  // Left
    postorder(root.right, result); // Right
    result.add(root.val);          // Root
}

// Iterative using 2 Stacks
List<Integer> postorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Stack<TreeNode> s1 = new Stack<>();
    Stack<TreeNode> s2 = new Stack<>();
    s1.push(root);

    while (!s1.isEmpty()) {
        TreeNode node = s1.pop();
        s2.push(node);

        if (node.left != null) s1.push(node.left);
        if (node.right != null) s1.push(node.right);
    }

    while (!s2.isEmpty()) {
        result.add(s2.pop().val);
    }

    return result;
}
```

---

## L7: Level Order Traversal (BFS)
**Question:** Traverse tree level by level from left to right.  
**Intuition:** Use **Queue** for BFS! Process all nodes at current level before moving to next. Essential for many tree problems!  
**Logic:** Use queue, process level by level. Track level size to separate levels.  
**Java:**
```java
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> currentLevel = new ArrayList<>();

        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            currentLevel.add(node.val);

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        result.add(currentLevel);
    }

    return result;
}
// Time: O(n), Space: O(n) for queue
```

---

## L8: Iterative Traversals using 1 Stack
**Question:** Can we do all three traversals iteratively using single stack?  
**Intuition:** Use **stack with state tracking**! Track whether node visited 0, 1, or 2 times. 0→preorder, 1→inorder, 2→postorder position.  
**Logic:** Maintain (node, count) in stack. Increment count each visit.  
**Java:**
```java
class Pair {
    TreeNode node;
    int count;
    Pair(TreeNode node, int count) {
        this.node = node;
        this.count = count;
    }
}

void allTraversals(TreeNode root) {
    List<Integer> pre = new ArrayList<>();
    List<Integer> in = new ArrayList<>();
    List<Integer> post = new ArrayList<>();

    if (root == null) return;

    Stack<Pair> stack = new Stack<>();
    stack.push(new Pair(root, 0));

    while (!stack.isEmpty()) {
        Pair p = stack.pop();

        if (p.count == 0) {
            pre.add(p.node.val); // Preorder
            stack.push(new Pair(p.node, 1));
            if (p.node.left != null) stack.push(new Pair(p.node.left, 0));
        } else if (p.count == 1) {
            in.add(p.node.val); // Inorder
            stack.push(new Pair(p.node, 2));
            if (p.node.right != null) stack.push(new Pair(p.node.right, 0));
        } else {
            post.add(p.node.val); // Postorder
        }
    }
}
```

---

## L9-L11: Morris Traversal (O(1) Space!)
**Question:** Can we traverse without recursion/stack (O(1) space)?  
**Intuition:** **Threading technique**! Temporarily modify tree by creating threads (links) to inorder successor/predecessor. After using, restore original structure. Mind-blowing concept!  
**Logic:** For each node, find inorder predecessor. Create thread from predecessor to current. Use thread to return. Remove thread after use.  
* For each node, check if it has a left child.
* If it does not have a left child, visit it and move to the right child.
* If it has a left child, find the inorder predecessor (rightmost node in the left subtree).
* Make the current node as the right child of its inorder predecessor (temporary link).
* Move to the left child.
* When you encounter a temporary link again, it means the left subtree is fully visited:
* Remove the temporary link.
* Visit the current node.
* Move to the right child.
**Java:**
```java
// Morris Inorder Traversal
List<Integer> morrisInorder(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    TreeNode curr = root;

    while (curr != null) {
        if (curr.left == null) {
            result.add(curr.val);
            curr = curr.right;
        } else {
            TreeNode prev = curr.left;
            while (prev.right != null && prev.right != curr) {
                prev = prev.right;
            }

            if (prev.right == null) {
                prev.right = curr; // Create thread
                curr = curr.left;
            } else {
                prev.right = null; // Remove thread
                result.add(curr.val);
                curr = curr.right;
            }
        }
    }

    return result;
}
// Time: O(n), Space: O(1) ⭐⭐⭐
```

---

# Part 3: Medium Tree Problems

## L12: Maximum Depth / Height of Binary Tree
**Question:** Find maximum depth (height) of binary tree.  
**Intuition:** Recursively! Height = 1 + max(leftHeight, rightHeight). Base case: null node has height 0.  
**Logic:** Post-order thinking - calculate heights of subtrees first.  
**Java:**
```java
int maxDepth(TreeNode root) {
    if (root == null) return 0;

    int leftHeight = maxDepth(root.left);
    int rightHeight = maxDepth(root.right);

    return 1 + Math.max(leftHeight, rightHeight);
}
// Time: O(n), Space: O(h)
```

---

## L13: Check if Tree is Balanced
**Question:** Check if tree is height-balanced (|left height - right height| ≤ 1 for every node).  
**Intuition:** Check balance while calculating height! If subtree unbalanced, return -1. Efficient single-pass solution.  
**Logic:** Return height if balanced, -1 if not. Propagate -1 upward.  
**Java:**
```java
int checkBalance(TreeNode root) {
    if (root == null) return 0;

    int leftHeight = checkBalance(root.left);
    if (leftHeight == -1) return -1;

    int rightHeight = checkBalance(root.right);
    if (rightHeight == -1) return -1;

    if (Math.abs(leftHeight - rightHeight) > 1) return -1;

    return 1 + Math.max(leftHeight, rightHeight);
}

boolean isBalanced(TreeNode root) {
    return checkBalance(root) != -1;
}
// Time: O(n), Space: O(h)
```

---

## L14: Diameter of Binary Tree
**Question:** Find diameter - longest path between any two nodes (path may/may not pass through root).  
**Intuition:** For each node, diameter passing through it = leftHeight + rightHeight. Track maximum globally. Calculate height simultaneously!  
**Logic:** At each node, update maxDiameter = max(maxDiameter, leftHeight + rightHeight).  
**Java:**
```java
int maxDiameter = 0;

int calculateHeight(TreeNode root) {
    if (root == null) return 0;

    int leftHeight = calculateHeight(root.left);
    int rightHeight = calculateHeight(root.right);

    maxDiameter = Math.max(maxDiameter, leftHeight + rightHeight);

    return 1 + Math.max(leftHeight, rightHeight);
}

int diameterOfBinaryTree(TreeNode root) {
    maxDiameter = 0;
    calculateHeight(root);
    return maxDiameter;
}
// Time: O(n), Space: O(h)
```

---

## L15: Maximum Path Sum
**Question:** Find maximum path sum between any two nodes.  
**Intuition:** Similar to diameter but track SUM instead of length! For each node, max path through it = node.val + leftSum + rightSum (take only positive contributions). Track global maximum.  
**Logic:** At each node, path through it = val + max(0, leftSum) + max(0, rightSum).  
**Java:**
```java
int maxSum = Integer.MIN_VALUE;

int maxPathSumHelper(TreeNode root) {
    if (root == null) return 0;

    int leftSum = Math.max(0, maxPathSumHelper(root.left));   // Take only if positive
    int rightSum = Math.max(0, maxPathSumHelper(root.right)); // Take only if positive

    maxSum = Math.max(maxSum, root.val + leftSum + rightSum);

    return root.val + Math.max(leftSum, rightSum); // Return max gain
}

int maxPathSum(TreeNode root) {
    maxSum = Integer.MIN_VALUE;
    maxPathSumHelper(root);
    return maxSum;
}
// Time: O(n), Space: O(h)
```

---

## L16: Check if Two Trees are Identical
**Question:** Check if two trees have same structure and values.  
**Intuition:** Recursively check: (1) both null â†’ true, (2) one null â†’ false, (3) values match AND left identical AND right identical.  
**Logic:** Base cases + recursive calls on left and right.  
**Java:**
```java
boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;

    return p.val == q.val && 
           isSameTree(p.left, q.left) && 
           isSameTree(p.right, q.right);
}
// Time: O(n), Space: O(h)
```

---

## L17: Zigzag Level Order Traversal
**Question:** Level order but alternate levels go left-to-right and right-to-left.  
**Intuition:** Normal level order with flag to reverse alternate levels! Or use deque to add from alternate ends.  
**Logic:** Track level number, reverse list if odd level.  
**Java:**
```java
List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    boolean leftToRight = true;

    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        if (!leftToRight) {
            Collections.reverse(level);
        }

        result.add(level);
        leftToRight = !leftToRight;
    }

    return result;
}
```

---

## L18: Boundary Traversal
**Question:** Print boundary - left boundary (excluding leaf) + leaves + right boundary (excluding leaf, reverse order).  
**Intuition:** Three separate traversals! (1) Left boundary going down, (2) All leaves, (3) Right boundary going up.  
**Logic:** Combine three parts, avoid duplicates.  
**Java:**
```java
List<Integer> boundaryTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    if (!isLeaf(root)) result.add(root.val);

    addLeftBoundary(root.left, result);
    addLeaves(root, result);
    addRightBoundary(root.right, result);

    return result;
}

boolean isLeaf(TreeNode node) {
    return node != null && node.left == null && node.right == null;
}

void addLeftBoundary(TreeNode node, List<Integer> result) {
    while (node != null) {
        if (!isLeaf(node)) result.add(node.val);
        node = (node.left != null) ? node.left : node.right;
    }
}

void addLeaves(TreeNode node, List<Integer> result) {
    if (node == null) return;
    if (isLeaf(node)) {
        result.add(node.val);
        return;
    }
    addLeaves(node.left, result);
    addLeaves(node.right, result);
}

void addRightBoundary(TreeNode node, List<Integer> result) {
    List<Integer> temp = new ArrayList<>();
    while (node != null) {
        if (!isLeaf(node)) temp.add(node.val);
        node = (node.right != null) ? node.right : node.left;
    }
    for (int i = temp.size() - 1; i >= 0; i--) {
        result.add(temp.get(i));
    }
}
```

---

## L19: Vertical Order Traversal
**Question:** Print vertical order - nodes at same vertical line together, top to bottom, left to right.  
**Intuition:** Assign (vertical, level) coordinates to each node! Use level-order traversal. Store nodes in TreeMap<vertical, TreeMap<level, PriorityQueue<value>>>. Complex data structure but concept is simple!  
**Logic:** Root at vertical=0. Left child: vertical-1, Right child: vertical+1. Group by vertical, then by level.  
**Java:**
```java
List<List<Integer>> verticalTraversal(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    TreeMap<Integer, TreeMap<Integer, PriorityQueue<Integer>>> map = new TreeMap<>();
    Queue<Tuple> queue = new LinkedList<>();
    queue.offer(new Tuple(root, 0, 0));

    while (!queue.isEmpty()) {
        Tuple tuple = queue.poll();
        TreeNode node = tuple.node;
        int vertical = tuple.vertical;
        int level = tuple.level;

        map.putIfAbsent(vertical, new TreeMap<>());
        map.get(vertical).putIfAbsent(level, new PriorityQueue<>());
        map.get(vertical).get(level).offer(node.val);

        if (node.left != null) {
            queue.offer(new Tuple(node.left, vertical - 1, level + 1));
        }
        if (node.right != null) {
            queue.offer(new Tuple(node.right, vertical + 1, level + 1));
        }
    }

    for (TreeMap<Integer, PriorityQueue<Integer>> levels : map.values()) {
        List<Integer> column = new ArrayList<>();
        for (PriorityQueue<Integer> nodes : levels.values()) {
            while (!nodes.isEmpty()) {
                column.add(nodes.poll());
            }
        }
        result.add(column);
    }

    return result;
}

class Tuple {
    TreeNode node;
    int vertical, level;
    Tuple(TreeNode node, int vertical, int level) {
        this.node = node;
        this.vertical = vertical;
        this.level = level;
    }
}
```

---

## L20: Top View of Binary Tree
**Question:** Print nodes visible from top. For each vertical line, print first node encountered from top.  
**Intuition:** Similar to vertical order but take ONLY FIRST node at each vertical! Use level-order, track first node per vertical.  
**Logic:** Map vertical to first node's value.  
**Java:**
```java
List<Integer> topView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Map<Integer, Integer> map = new TreeMap<>();
    Queue<Pair> queue = new LinkedList<>();
    queue.offer(new Pair(root, 0));

    while (!queue.isEmpty()) {
        Pair p = queue.poll();
        TreeNode node = p.node;
        int vertical = p.vertical;

        if (!map.containsKey(vertical)) {
            map.put(vertical, node.val);
        }

        if (node.left != null) queue.offer(new Pair(node.left, vertical - 1));
        if (node.right != null) queue.offer(new Pair(node.right, vertical + 1));
    }

    result.addAll(map.values());
    return result;
}

class Pair {
    TreeNode node;
    int vertical;
    Pair(TreeNode node, int vertical) {
        this.node = node;
        this.vertical = vertical;
    }
}
```

---

## L21: Bottom View of Binary Tree
**Question:** Print nodes visible from bottom. For each vertical, print LAST node from top.  
**Intuition:** Opposite of top view! Overwrite map value for each vertical - last one wins!  
**Java:**
```java
List<Integer> bottomView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Map<Integer, Integer> map = new TreeMap<>();
    Queue<Pair> queue = new LinkedList<>();
    queue.offer(new Pair(root, 0));

    while (!queue.isEmpty()) {
        Pair p = queue.poll();
        TreeNode node = p.node;
        int vertical = p.vertical;

        map.put(vertical, node.val); // Overwrite!

        if (node.left != null) queue.offer(new Pair(node.left, vertical - 1));
        if (node.right != null) queue.offer(new Pair(node.right, vertical + 1));
    }

    result.addAll(map.values());
    return result;
}
```

---

## L22: Right/Left View of Binary Tree
**Question:** Print nodes visible from right side (or left side).  
**Intuition:** For right view: take RIGHTMOST (last) node at each level. For left view: take LEFTMOST (first) node at each level. Use level-order or clever recursion!  
**Logic:** Level-order: add last node of each level. Recursive: track level, add first node encountered at each level (going right first for right view).  
**Java:**
```java
// Right View - Level Order
List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();

            if (i == size - 1) { // Last node of level
                result.add(node.val);
            }

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }

    return result;
}

// Right View - Recursive (More elegant!)
void rightViewRecursive(TreeNode root, int level, List<Integer> result) {
    if (root == null) return;

    if (level == result.size()) {
        result.add(root.val);
    }

    rightViewRecursive(root.right, level + 1, result); // Right first!
    rightViewRecursive(root.left, level + 1, result);
}
```

---

## L23: Check for Symmetrical Binary Tree
**Question:** Check if tree is mirror image of itself (symmetric around center).  
**Intuition:** Check if left subtree is mirror of right subtree! Two trees are mirrors if: roots equal AND left's left = right's right AND left's right = right's left.  
**Logic:** Helper function to check if two trees are mirrors.  
**Java:**
```java
boolean isSymmetric(TreeNode root) {
    return root == null || isMirror(root.left, root.right);
}

boolean isMirror(TreeNode left, TreeNode right) {
    if (left == null && right == null) return true;
    if (left == null || right == null) return false;

    return left.val == right.val && 
           isMirror(left.left, right.right) && 
           isMirror(left.right, right.left);
}
// Time: O(n), Space: O(h)
```

---

## L24: Root to Node Path
**Question:** Find path from root to given node.  
**Intuition:** Use recursion with backtracking! Try going left and right. If node found in subtree, add current node to path and return true.  
**Logic:** Preorder traversal with path tracking. Backtrack if node not found.  
**Java:**
```java
boolean getPath(TreeNode root, int target, List<Integer> path) {
    if (root == null) return false;

    path.add(root.val);

    if (root.val == target) return true;

    if (getPath(root.left, target, path) || getPath(root.right, target, path)) {
        return true;
    }

    path.remove(path.size() - 1); // Backtrack
    return false;
}
```

---

## L25: Lowest Common Ancestor (LCA)
**Question:** Find LCA of two nodes - lowest node that has both as descendants.  
**Intuition:** Recursively search! If current node is one of targets, return it. If found in both subtrees, current is LCA. If found in only one subtree, return that subtree's result.  
**Logic:** Base case: null or found target. Recurse both sides. If both return non-null, current is LCA.  
**Java:**
```java
TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) {
        return root;
    }

    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);

    if (left != null && right != null) {
        return root; // Both found in different subtrees
    }

    return (left != null) ? left : right; // Return non-null one
}
// Time: O(n), Space: O(h)
```

---

# Part 4: Hard Tree Problems

## L26: Maximum Width of Binary Tree
**Question:** Find maximum width - maximum number of nodes in any level (including null nodes in between).  
**Intuition:** Use indexing! Root at index 1. Left child: 2*i, Right child: 2*i+1. Track min and max index at each level. Width = maxIndex - minIndex + 1. Handle overflow by normalizing indices!  
**Logic:** Level-order with index tracking.  
**Java:**
```java
int widthOfBinaryTree(TreeNode root) {
    if (root == null) return 0;

    Queue<Pair> queue = new LinkedList<>();
    queue.offer(new Pair(root, 0));
    int maxWidth = 0;

    while (!queue.isEmpty()) {
        int size = queue.size();
        int minIndex = queue.peek().index;
        int first = 0, last = 0;

        for (int i = 0; i < size; i++) {
            Pair p = queue.poll();
            TreeNode node = p.node;
            int index = p.index - minIndex; // Normalize to prevent overflow

            if (i == 0) first = index;
            if (i == size - 1) last = index;

            if (node.left != null) {
                queue.offer(new Pair(node.left, 2 * index));
            }
            if (node.right != null) {
                queue.offer(new Pair(node.right, 2 * index + 1));
            }
        }

        maxWidth = Math.max(maxWidth, last - first + 1);
    }

    return maxWidth;
}

class Pair {
    TreeNode node;
    int index;
    Pair(TreeNode node, int index) {
        this.node = node;
        this.index = index;
    }
}
```

---

## L27: Children Sum Property
**Question:** Check if for every node: node.val = sum of children's values.  
**Intuition:** Simple recursive check! For each node, check if val equals left.val + right.val.  
**Java:**
```java
boolean childrenSumProperty(TreeNode root) {
    if (root == null || (root.left == null && root.right == null)) {
        return true;
    }

    int sum = 0;
    if (root.left != null) sum += root.left.val;
    if (root.right != null) sum += root.right.val;

    return (root.val == sum) && 
           childrenSumProperty(root.left) && 
           childrenSumProperty(root.right);
}
```

---

## L28: All Nodes at Distance K
**Question:** Find all nodes at distance K from target node.  
**Intuition:** Tree becomes like undirected graph! Need to move in all directions (up to parent too). Solution: (1) Mark parents for all nodes, (2) BFS from target treating it as graph, (3) Track visited to avoid cycles.  
**Logic:** Build parent map, then BFS with distance tracking.  
**Java:**
```java
List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
    Map<TreeNode, TreeNode> parentMap = new HashMap<>();
    markParents(root, parentMap);

    Map<TreeNode, Boolean> visited = new HashMap<>();
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(target);
    visited.put(target, true);
    int currentDist = 0;

    while (!queue.isEmpty()) {
        if (currentDist == k) break;

        int size = queue.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();

            // Go to left child
            if (node.left != null && visited.get(node.left) == null) {
                queue.offer(node.left);
                visited.put(node.left, true);
            }

            // Go to right child
            if (node.right != null && visited.get(node.right) == null) {
                queue.offer(node.right);
                visited.put(node.right, true);
            }

            // Go to parent
            TreeNode parent = parentMap.get(node);
            if (parent != null && visited.get(parent) == null) {
                queue.offer(parent);
                visited.put(parent, true);
            }
        }
        currentDist++;
    }

    List<Integer> result = new ArrayList<>();
    while (!queue.isEmpty()) {
        result.add(queue.poll().val);
    }
    return result;
}

void markParents(TreeNode root, Map<TreeNode, TreeNode> parentMap) {
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();

        if (node.left != null) {
            parentMap.put(node.left, node);
            queue.offer(node.left);
        }
        if (node.right != null) {
            parentMap.put(node.right, node);
            queue.offer(node.right);
        }
    }
}
```

---

## L29: Minimum Time to Burn Binary Tree
**Question:** Given tree and target node, if fire starts at target and spreads to adjacent nodes (parent + children) in 1 second, find time to burn entire tree.  
**Intuition:** Same as distance K problem! Find maximum distance from target to any node. Use parent map + BFS. Time = maximum distance reached.  
**Java:**
```java
int minTime(TreeNode root, TreeNode target) {
    Map<TreeNode, TreeNode> parentMap = new HashMap<>();
    markParents(root, parentMap);

    Map<TreeNode, Boolean> visited = new HashMap<>();
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(target);
    visited.put(target, true);
    int time = 0;

    while (!queue.isEmpty()) {
        int size = queue.size();
        boolean burned = false;

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();

            if (node.left != null && visited.get(node.left) == null) {
                burned = true;
                queue.offer(node.left);
                visited.put(node.left, true);
            }

            if (node.right != null && visited.get(node.right) == null) {
                burned = true;
                queue.offer(node.right);
                visited.put(node.right, true);
            }

            TreeNode parent = parentMap.get(node);
            if (parent != null && visited.get(parent) == null) {
                burned = true;
                queue.offer(parent);
                visited.put(parent, true);
            }
        }

        if (burned) time++;
    }

    return time;
}
```

---

## L30: Count Complete Tree Nodes
**Question:** Count nodes in complete binary tree in less than O(n) time.  
**Intuition:** Complete tree property! If leftHeight == rightHeight, tree is perfect: nodes = 2^h - 1. Else, recursively count left + right + 1. Use height checking to prune!  
**Logic:** O(logÂ²n) solution by checking if subtrees are perfect.  
**Java:**
```java
int countNodes(TreeNode root) {
    if (root == null) return 0;

    int leftHeight = getLeftHeight(root);
    int rightHeight = getRightHeight(root);

    if (leftHeight == rightHeight) {
        return (1 << leftHeight) - 1; // 2^h - 1
    }

    return 1 + countNodes(root.left) + countNodes(root.right);
}

int getLeftHeight(TreeNode node) {
    int height = 0;
    while (node != null) {
        height++;
        node = node.left;
    }
    return height;
}

int getRightHeight(TreeNode node) {
    int height = 0;
    while (node != null) {
        height++;
        node = node.right;
    }
    return height;
}
// Time: O(logÂ²n), Space: O(log n)
```

---

## L31: Construct Binary Tree from Preorder & Inorder
**Question:** Given preorder and inorder arrays, construct unique binary tree.  
**Intuition:** Preorder's first element is root! Find root in inorder - left part is left subtree, right part is right subtree. Recursively build!  
**Logic:** Use map for O(1) inorder lookups. Track indices carefully.  
**Java:**
```java
int preIndex = 0;

TreeNode buildTree(int[] preorder, int[] inorder) {
    Map<Integer, Integer> inMap = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) {
        inMap.put(inorder[i], i);
    }
    return build(preorder, inMap, 0, inorder.length - 1);
}

TreeNode build(int[] preorder, Map<Integer, Integer> inMap, int inStart, int inEnd) {
    if (inStart > inEnd) return null;

    TreeNode root = new TreeNode(preorder[preIndex++]);
    int inIndex = inMap.get(root.val);

    root.left = build(preorder, inMap, inStart, inIndex - 1);
    root.right = build(preorder, inMap, inIndex + 1, inEnd);

    return root;
}
// Time: O(n), Space: O(n)
```

---

## L32: Construct Binary Tree from Postorder & Inorder
**Question:** Same but with postorder and inorder.  
**Intuition:** Postorder's LAST element is root! Process from right to left in postorder.  
**Java:**
```java
int postIndex;

TreeNode buildTreePost(int[] inorder, int[] postorder) {
    postIndex = postorder.length - 1;
    Map<Integer, Integer> inMap = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) {
        inMap.put(inorder[i], i);
    }
    return buildPost(postorder, inMap, 0, inorder.length - 1);
}

TreeNode buildPost(int[] postorder, Map<Integer, Integer> inMap, int inStart, int inEnd) {
    if (inStart > inEnd) return null;

    TreeNode root = new TreeNode(postorder[postIndex--]);
    int inIndex = inMap.get(root.val);

    root.right = buildPost(postorder, inMap, inIndex + 1, inEnd); // Right first!
    root.left = buildPost(postorder, inMap, inStart, inIndex - 1);

    return root;
}
```

---

## L33: Serialize and Deserialize Binary Tree
**Question:** Encode tree to string and decode back.  
**Intuition:** Use level-order traversal! Include null nodes as "#". Decode by rebuilding level by level.  
**Java:**
```java
// Serialize
String serialize(TreeNode root) {
    if (root == null) return "";

    StringBuilder sb = new StringBuilder();
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();

        if (node == null) {
            sb.append("# ");
        } else {
            sb.append(node.val).append(" ");
            queue.offer(node.left);
            queue.offer(node.right);
        }
    }

    return sb.toString();
}

// Deserialize
TreeNode deserialize(String data) {
    if (data.equals("")) return null;

    String[] values = data.split(" ");
    TreeNode root = new TreeNode(Integer.parseInt(values[0]));
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    for (int i = 1; i < values.length; i++) {
        TreeNode parent = queue.poll();

        if (!values[i].equals("#")) {
            TreeNode left = new TreeNode(Integer.parseInt(values[i]));
            parent.left = left;
            queue.offer(left);
        }

        if (++i < values.length && !values[i].equals("#")) {
            TreeNode right = new TreeNode(Integer.parseInt(values[i]));
            parent.right = right;
            queue.offer(right);
        }
    }

    return root;
}
```

---

# Part 5: Binary Search Tree (BST)

## L34: Introduction to BST
**Question:** What is BST? Properties?  
**Intuition:** BST is special binary tree where **left subtree < node < right subtree** for ALL nodes! This property enables O(log n) search, insert, delete. Inorder traversal gives **sorted sequence**!  
**Logic:** For every node: all left descendants < node.val < all right descendants.  

---

## L35: Search in BST
**Question:** Search for value in BST.  
**Intuition:** Use BST property! If target < node, go left. If target > node, go right. If equal, found! O(log n) average, O(n) worst (skewed).  
**Java:**
```java
TreeNode searchBST(TreeNode root, int val) {
    while (root != null && root.val != val) {
        root = (val < root.val) ? root.left : root.right;
    }
    return root;
}
// Time: O(h), Space: O(1)
```

---

## L36: Ceil in BST
**Question:** Find ceil of key - smallest value >= key.  
**Intuition:** Track potential ceil while traversing! If node.val >= key, it's potential ceil, go left for smaller. If node.val < key, go right for larger.  
**Java:**
```java
int findCeil(TreeNode root, int key) {
    int ceil = -1;

    while (root != null) {
        if (root.val == key) {
            return root.val;
        } else if (root.val > key) {
            ceil = root.val;
            root = root.left;
        } else {
            root = root.right;
        }
    }

    return ceil;
}
```

---

## L37: Floor in BST
**Question:** Find floor - largest value <= key.  
**Intuition:** Opposite of ceil! Track potential floor while traversing.  
**Java:**
```java
int findFloor(TreeNode root, int key) {
    int floor = -1;

    while (root != null) {
        if (root.val == key) {
            return root.val;
        } else if (root.val < key) {
            floor = root.val;
            root = root.right;
        } else {
            root = root.left;
        }
    }

    return floor;
}
```

---

## L38: Insert into BST
**Question:** Insert value maintaining BST property.  
**Intuition:** Find correct position using BST property, create new node!  
**Java:**
```java
TreeNode insertIntoBST(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);

    TreeNode curr = root;
    while (true) {
        if (val < curr.val) {
            if (curr.left == null) {
                curr.left = new TreeNode(val);
                break;
            } else {
                curr = curr.left;
            }
        } else {
            if (curr.right == null) {
                curr.right = new TreeNode(val);
                break;
            } else {
                curr = curr.right;
            }
        }
    }

    return root;
}
```

---

## L39: Delete from BST
**Question:** Delete node maintaining BST property.  
**Intuition:** Three cases: (1) Leaf - simply remove, (2) One child - replace with child, (3) Two children - replace with inorder successor (or predecessor), then delete successor.  
**Java:**
```java
TreeNode deleteNode(TreeNode root, int key) {
    if (root == null) return null;

    if (key < root.val) {
        root.left = deleteNode(root.left, key);
    } else if (key > root.val) {
        root.right = deleteNode(root.right, key);
    } else {
        // Node to delete found
        if (root.left == null) return root.right;
        if (root.right == null) return root.left;

        // Two children - find inorder successor
        TreeNode successor = findMin(root.right);
        root.val = successor.val;
        root.right = deleteNode(root.right, successor.val);
    }

    return root;
}

TreeNode findMin(TreeNode node) {
    while (node.left != null) {
        node = node.left;
    }
    return node;
}
```

---

## L40: Kth Smallest in BST
**Question:** Find Kth smallest element.  
**Intuition:** Inorder traversal gives sorted order! Count nodes during inorder, return Kth.  
**Java:**
```java
int kthSmallest(TreeNode root, int k) {
    Stack<TreeNode> stack = new Stack<>();
    TreeNode curr = root;
    int count = 0;

    while (curr != null || !stack.isEmpty()) {
        while (curr != null) {
            stack.push(curr);
            curr = curr.left;
        }

        curr = stack.pop();
        count++;

        if (count == k) return curr.val;

        curr = curr.right;
    }

    return -1;
}
```

---

## L41: Validate BST
**Question:** Check if valid BST.  
**Intuition:** Use range checking! For each node, valid range is (min, max). Left child: (min, node.val), Right child: (node.val, max).  
**Java:**
```java
boolean isValidBST(TreeNode root) {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;

    if (node.val <= min || node.val >= max) return false;

    return validate(node.left, min, node.val) && 
           validate(node.right, node.val, max);
}
```

---

## L42: LCA in BST
**Question:** Find LCA in BST.  
**Intuition:** Use BST property! If both nodes < current, go left. If both > current, go right. Else, current is LCA!  
**Java:**
```java
TreeNode lowestCommonAncestorBST(TreeNode root, TreeNode p, TreeNode q) {
    while (root != null) {
        if (p.val < root.val && q.val < root.val) {
            root = root.left;
        } else if (p.val > root.val && q.val > root.val) {
            root = root.right;
        } else {
            return root;
        }
    }
    return null;
}
// Time: O(h), Space: O(1)
```

---

## L43: Construct BST from Preorder
**Question:** Build BST from preorder traversal.  
**Intuition:** First element is root! Elements smaller than root go left, larger go right. Use upper bound to track valid range!  
**Java:**
```java
int index = 0;

TreeNode bstFromPreorder(int[] preorder) {
    return build(preorder, Integer.MAX_VALUE);
}

TreeNode build(int[] preorder, int bound) {
    if (index == preorder.length || preorder[index] > bound) {
        return null;
    }

    TreeNode root = new TreeNode(preorder[index++]);
    root.left = build(preorder, root.val);
    root.right = build(preorder, bound);

    return root;
}
// Time: O(n), Space: O(h)
```

---

## L44: Inorder Successor/Predecessor in BST
**Question:** Find inorder successor (next larger) and predecessor (next smaller).  
**Intuition:** Successor: If right child exists, leftmost in right subtree. Else, ancestor where we took left turn. Predecessor: mirror logic.  
**Java:**
```java
TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
    TreeNode successor = null;

    while (root != null) {
        if (p.val >= root.val) {
            root = root.right;
        } else {
            successor = root;
            root = root.left;
        }
    }

    return successor;
}

TreeNode inorderPredecessor(TreeNode root, TreeNode p) {
    TreeNode predecessor = null;

    while (root != null) {
        if (p.val <= root.val) {
            root = root.left;
        } else {
            predecessor = root;
            root = root.right;
        }
    }

    return predecessor;
}
```

---

## L45: BST Iterator
**Question:** Implement iterator with next() and hasNext() for BST inorder traversal.  
**Intuition:** Use stack to simulate inorder! Initialize by pushing all left nodes. next() pops, then pushes all left of right child.  
**Java:**
```java
class BSTIterator {
    Stack<TreeNode> stack = new Stack<>();

    public BSTIterator(TreeNode root) {
        pushAll(root);
    }

    public int next() {
        TreeNode node = stack.pop();
        pushAll(node.right);
        return node.val;
    }

    public boolean hasNext() {
        return !stack.isEmpty();
    }

    private void pushAll(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }
}
// Time: next() and hasNext() both O(1) average
```

---

## L46: Two Sum in BST
**Question:** Check if two numbers exist in BST that sum to target.  
**Intuition:** Use BST iterator! Create forward iterator (inorder) and reverse iterator (reverse inorder). Two-pointer technique!  
**Java:**
```java
boolean findTarget(TreeNode root, int k) {
    if (root == null) return false;

    BSTIterator left = new BSTIterator(root, false); // Forward
    BSTIterator right = new BSTIterator(root, true);  // Reverse

    int l = left.next();
    int r = right.next();

    while (l < r) {
        if (l + r == k) return true;
        if (l + r < k) l = left.next();
        else r = right.next();
    }

    return false;
}
// Time: O(n), Space: O(h)
```

---

## L47: Recover BST (Two Nodes Swapped)
**Question:** Two nodes of BST swapped by mistake. Recover without changing structure.  
**Intuition:** Inorder of BST is sorted! Find two places where inorder is violated. Those are swapped nodes. Swap their values!  
**Java:**
```java
TreeNode first = null, second = null, prev = null;

void recoverTree(TreeNode root) {
    inorder(root);
    // Swap values
    int temp = first.val;
    first.val = second.val;
    second.val = temp;
}

void inorder(TreeNode node) {
    if (node == null) return;

    inorder(node.left);

    if (prev != null && node.val < prev.val) {
        if (first == null) {
            first = prev;
        }
        second = node;
    }

    prev = node;
    inorder(node.right);
}
```

---

## L48: Largest BST in Binary Tree
**Question:** Find size of largest BST subtree in binary tree.  
**Intuition:** For each node, check if subtree is BST and track size. Return (isBST, size, min, max) info for each subtree. If both children are BST and current node forms BST with them, update max size.  
**Logic:** Post-order traversal returning BST info.  
**Java:**
```java
class NodeInfo {
    int size, min, max;
    boolean isBST;

    NodeInfo(int size, int min, int max, boolean isBST) {
        this.size = size;
        this.min = min;
        this.max = max;
        this.isBST = isBST;
    }
}

int maxBSTSize = 0;

int largestBST(TreeNode root) {
    maxBSTSize = 0;
    helper(root);
    return maxBSTSize;
}

NodeInfo helper(TreeNode node) {
    if (node == null) {
        return new NodeInfo(0, Integer.MAX_VALUE, Integer.MIN_VALUE, true);
    }

    NodeInfo left = helper(node.left);
    NodeInfo right = helper(node.right);

    if (left.isBST && right.isBST && node.val > left.max && node.val < right.min) {
        int size = 1 + left.size + right.size;
        maxBSTSize = Math.max(maxBSTSize, size);
        return new NodeInfo(size, Math.min(node.val, left.min), 
                           Math.max(node.val, right.max), true);
    }

    return new NodeInfo(Math.max(left.size, right.size), 0, 0, false);
}
```

---

# Summary: Complete Tree Mastery

## Key Patterns

### 1. **Traversals** (Foundation)
- Preorder: Root â†’ Left â†’ Right
- Inorder: Left â†’ Root â†’ Right (BST â†’ sorted!)
- Postorder: Left â†’ Right â†’ Root
- Level Order: BFS with queue
- Morris: O(1) space traversal

### 2. **Height/Depth Problems**
- Max Depth, Diameter, Balanced Tree
- Calculate recursively in post-order

### 3. **Path Problems**
- Root to Leaf, Max Path Sum, LCA
- Use recursion with backtracking

### 4. **View Problems**
- Top/Bottom/Left/Right View
- Use level-order or recursion with level tracking

### 5. **BST Operations**
- Search, Insert, Delete: O(h)
- Validate, Ceil, Floor, Kth Smallest
- Use BST property for optimization

### 6. **Construction Problems**
- Build from traversals
- Serialize/Deserialize
- Use recursion with careful indexing

### 7. **Hard Concepts**
- Width, Distance K, Burn Tree
- Use level-order with extra info (index, parent)

---

## Time Complexities

| Operation         | Time         | Space    |
| ----------------- | ------------ | -------- |
| Traversals        | O(n)         | O(h)     |
| Height/Diameter   | O(n)         | O(h)     |
| Level Order       | O(n)         | O(n)     |
| BST Search        | O(h)         | O(1)     |
| BST Insert/Delete | O(h)         | O(h)     |
| LCA               | O(n) or O(h) | O(h)     |
| Morris            | O(n)         | O(1) â­ |

---

## Interview Tips

**Most Important Problems:**
1. All Traversals (especially iterative)
2. Level Order Traversal
3. Maximum Depth & Diameter
4. LCA
5. Validate BST
6. Vertical Order
7. Serialize/Deserialize
8. Max Path Sum

**Common Mistakes:**
- Forgetting null checks
- Not handling single node cases
- Wrong base cases in recursion
- Missing edge cases in BST

**Pro Tips:**
- Most problems use recursion or level-order
- BST problems often have O(h) solutions
- For views, think about what's visible from that angle
- Parent tracking enables many hard problems
