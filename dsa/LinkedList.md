
## L1: Introduction to LinkedList | Node Structure
**Question:** What is a LinkedList? How is it different from arrays?
**Intuition:** Arrays have fixed size and contiguous memory. LinkedList nodes are scattered in memory, connected by pointers. Dynamic size, efficient insertions/deletions.
**Logic:** Each node contains data and reference to next node.

**Node Structure:**
```java
class Node {
 int data;
 Node next;

 Node(int val) {
 data = val;
 next = null;
 }

 Node(int val, Node next) {
 this.data = val;
 this.next = next;
 }
}

// Basic Operations
void traverse(Node head) {
 Node temp = head;
 while (temp != null) {
 System.out.print(temp.data + " ");
 temp = temp.next;
 }
}

int length(Node head) {
 int count = 0;
 Node temp = head;
 while (temp != null) {
 count++;
 temp = temp.next;
 }
 return count;
}

boolean search(Node head, int val) {
 Node temp = head;
 while (temp != null) {
 if (temp.data == val) return true;
 temp = temp.next;
 }
 return false;
}
```

---

## L2: Doubly LinkedList | Introduction
**Question:** Implement doubly linked list with forward and backward traversal.
**Intuition:** Two pointers per node allow bidirectional traversal. Easier deletions but more memory.
**Logic:** Node has data, prev, and next pointers.

**Java:**
```java
class DLLNode {
 int data;
 DLLNode prev;
 DLLNode next;

 DLLNode(int val) {
 data = val;
 prev = null;
 next = null;
 }
}

// Convert array to DLL
DLLNode arrayToDLL(int[] arr) {
 if (arr.length == 0) return null;

 DLLNode head = new DLLNode(arr[0]);
 DLLNode mover = head;

 for (int i = 1; i < arr.length; i++) {
 DLLNode temp = new DLLNode(arr[i]);
 mover.next = temp;
 temp.prev = mover;
 mover = temp;
 }

 return head;
}

// Insert at head
DLLNode insertAtHead(DLLNode head, int val) {
 DLLNode newHead = new DLLNode(val);
 newHead.next = head;
 if (head != null) {
 head.prev = newHead;
 }
 return newHead;
}

// Delete head
DLLNode deleteHead(DLLNode head) {
 if (head == null || head.next == null) return null;

 DLLNode newHead = head.next;
 newHead.prev = null;
 head.next = null;
 return newHead;
}
```

---

## L3: Reverse a LinkedList
**Question:** Reverse the linked list.
**Intuition:** Change direction of all next pointers. Use three pointers to avoid losing references.
**Logic:** Iterative with prev, curr, next OR recursive approach.

**Java:**
```java
// Iterative
Node reverseIterative(Node head) {
 Node prev = null;
 Node curr = head;

 while (curr != null) {
 Node next = curr.next;
 curr.next = prev;
 prev = curr;
 curr = next;
 }

 return prev;
}

// Recursive
Node reverseRecursive(Node head) {
 if (head == null || head.next == null) {
 return head;
 }

 Node newHead = reverseRecursive(head.next);
 Node front = head.next;
 front.next = head;
 head.next = null;

 return newHead;
}
// Time: O(n), Space: O(1) iterative, O(n) recursive
```

---

## L4: Detect Loop in LinkedList
**Question:** Check if linked list has a cycle using Floyd's algorithm.

### Interview Explanation: How We Come to This Solution

**Step 1: Understanding the Problem**
- We need to detect if there's a cycle in the linked list
- If we keep traversing normally, we'll get stuck in infinite loop if cycle exists
- Need an efficient way to detect this

**Step 2: Brute Force Approach (What NOT to do)**
```java
// Approach 1: Using HashSet - O(n) time, O(n) space
boolean hasCycle(Node head) {
 Set<Node> visited = new HashSet<>();
 Node curr = head;

 while (curr != null) {
 if (visited.contains(curr)) {
 return true; // Found cycle!
 }
 visited.add(curr);
 curr = curr.next;
 }
 return false;
}
```
**Problem:** Uses O(n) extra space. Interviewer will ask: "Can you do better?"

**Step 3: The Insight - Floyd's Cycle Detection (Tortoise and Hare)**

**Key Intuition:**
- Imagine two runners on a circular track
- One runs at 1x speed (slow/tortoise), other at 2x speed (fast/hare)
- If track is circular, the faster runner will eventually lap and meet the slower one
- If track is straight (no cycle), fast runner reaches the end

**Visual Example:**
```
No Cycle: 1 -> 2 -> 3 -> 4 -> NULL
 S
 F (fast reaches NULL, no cycle)

Cycle: 1 -> 2 -> 3 -> 4
 ^ |
 |_________| (fast will eventually catch slow)
```

**Why Does This Work?**
1. **No cycle:** Fast pointer reaches NULL → return false
2. **Has cycle:** Once both pointers enter the cycle:
 - Distance between them decreases by 1 in each iteration
 - Eventually distance becomes 0 → they meet → return true

**Mathematical Proof:**
- Let's say slow is at position `S` and fast is at position `F` inside cycle
- After one iteration: slow at `S+1`, fast at `F+2`
- Gap reduction: `(F - S) - 1` each time
- When gap becomes 0, they meet → cycle detected!

**Step 4: Optimized Solution**

**Java:**
```java
boolean hasCycle(Node head) {
 if (head == null || head.next == null) return false;

 Node slow = head;
 Node fast = head;

 while (fast != null && fast.next != null) {
 slow = slow.next; // Move 1 step
 fast = fast.next.next; // Move 2 steps

 if (slow == fast) {
 return true; // Cycle detected!
 }
 }

 return false; // Fast reached end, no cycle
}
// Time: O(n), Space: O(1)
```

**Key Interview Points:**
1. ✅ Optimal: O(1) space vs O(n) in HashSet approach
2. ✅ Single pass: O(n) time complexity
3. ✅ Why `fast != null && fast.next != null`?
 - `fast != null` → ensures fast.next exists
 - `fast.next != null` → ensures fast.next.next exists
 - Prevents NullPointerException
4. ✅ Why both start at head? Simplicity; could also start fast at head.next

---

## L5: Find Starting Point of Loop
**Question:** Find the node where cycle begins.

### Interview Explanation: Mathematical Proof of Floyd's Algorithm

**Step 1: First, Detect the Cycle (same as L4)**
```java
Node slow = head, fast = head;
while (fast != null && fast.next != null) {
 slow = slow.next;
 fast = fast.next.next;
 if (slow == fast) break; // Cycle detected, they met!
}
if (fast == null || fast.next == null) return null; // No cycle
```

**Step 2: Why Reset One Pointer to Head?**

Let's define:
- `L` = Distance from head to cycle start
- `C` = Cycle length
- `K` = Distance from cycle start to meeting point

**Visual Representation:**
```
head -> ... (L nodes) ... -> cycle_start -> ... (K nodes) ... -> meeting_point
 ^ |
 |_______________ (C nodes) ____________|
```

**When They Meet:**
- **Slow** traveled: `L + K` (entered cycle, moved K steps inside)
- **Fast** traveled: `L + K + nC` (traveled same path + n full cycles)
 - n = number of complete cycles fast made before meeting

**Since fast travels 2x speed of slow:**
```
Distance(fast) = 2 × Distance(slow)
L + K + nC = 2(L + K)
L + K + nC = 2L + 2K
nC = L + K
L = nC - K
```

**Critical Insight:**
```
L = nC - K
L = (n-1)C + (C - K)
```

This means:
- Distance from **head to cycle start** = `L`
- Distance from **meeting point to cycle start** = `C - K` (moving forward in cycle)
- They differ by `(n-1)` complete cycles!

**Step 3: The Magic Move**

Reset slow to head, keep fast at meeting point:
- Move both at **same speed** (1 step each)
- Slow travels `L` to reach cycle start
- Fast travels `C - K` to reach cycle start (+ maybe some complete cycles)
- They meet exactly at the **cycle start**! 🎯

**Step 4: Complete Solution**

**Java:**
```java
Node detectCycle(Node head) {
 if (head == null || head.next == null) return null;

 Node slow = head;
 Node fast = head;

 // Phase 1: Detect cycle using Floyd's algorithm
 while (fast != null && fast.next != null) {
 slow = slow.next;
 fast = fast.next.next;

 if (slow == fast) {
 // Cycle detected!

 // Phase 2: Find cycle start
 slow = head; // Reset slow to head

 // Move both at same speed
 while (slow != fast) {
 slow = slow.next;
 fast = fast.next; // Now moves 1 step (not 2!)
 }

 return slow; // This is the cycle start!
 }
 }

 return null; // No cycle found
}
// Time: O(n), Space: O(1)
```

**Example Walkthrough:**
```
List: 1 -> 2 -> 3 -> 4 -> 5 -> 6
 ^ |
 |______________| (cycle starts at node 3)

L = 2 (nodes 1, 2)
C = 4 (nodes 3, 4, 5, 6)

Phase 1 - Detect cycle:
 Slow: 1 -> 2 -> 3 -> 4 -> 5 (moved 4 steps)
 Fast: 1 -> 3 -> 5 -> 4 (moved 8 steps, they meet at node 5)
 K = 2 (from node 3 to node 5)

Phase 2 - Find start:
 Slow = head (node 1)
 Fast = meeting point (node 5)

 Step 1: Slow = 2, Fast = 6
 Step 2: Slow = 3, Fast = 3 ← Both at cycle start!
```

**Key Interview Points:**
1. ✅ **Two phases:** Detect cycle, then find start
2. ✅ **Why it works:** Mathematical proof using distances
3. ✅ **Speed change:** Fast moves 2x in phase 1, but 1x in phase 2
4. ✅ **Optimal:** O(n) time, O(1) space
5. ✅ **Edge cases:** No cycle, cycle at head, single node

**Common Interview Follow-up:**
- Q: "What if we want cycle length?"
- A: After finding start, keep one pointer fixed and count steps until it returns

---

## L6: Find Length of Loop
**Question:** Count number of nodes in the cycle.
**Intuition:** After detecting cycle, fix one pointer at meeting point, count steps until it returns.
**Logic:** Keep moving pointer until it completes the loop.

**Java:**
```java
int lengthOfLoop(Node head) {
 Node slow = head;
 Node fast = head;

 while (fast != null && fast.next != null) {
 slow = slow.next;
 fast = fast.next.next;

 if (slow == fast) {
 int count = 1;
 Node temp = slow;
 while (temp.next != slow) {
 count++;
 temp = temp.next;
 }
 return count;
 }
 }

 return 0;
}
```

---

## L7: Check if Palindrome
**Question:** Check if linked list values form a palindrome.
**Intuition:** Find middle, reverse second half, compare with first half.
**Logic:** Slow-fast to find middle, reverse from middle, compare.

**Java:**
```java
boolean isPalindrome(Node head) {
 if (head == null || head.next == null) return true;

 // Find middle
 Node slow = head;
 Node fast = head;
 while (fast.next != null && fast.next.next != null) {
 slow = slow.next;
 fast = fast.next.next;
 }

 // Reverse second half
 Node newHead = reverse(slow.next);

 // Compare
 Node first = head;
 Node second = newHead;
 while (second != null) {
 if (first.data != second.data) {
 return false;
 }
 first = first.next;
 second = second.next;
 }

 return true;
}
```

---

## L8: Find Middle of LinkedList
**Question:** Find middle node (if two middles, return second).
**Intuition:** Slow-fast pointer. When fast reaches end, slow is at middle.
**Logic:** Slow moves 1, fast moves 2.

**Java:**
```java
Node middleNode(Node head) {
 Node slow = head;
 Node fast = head;

 while (fast != null && fast.next != null) {
 slow = slow.next;
 fast = fast.next.next;
 }

 return slow;
}
// Time: O(n), Space: O(1)
```

---

## L9: Delete Middle Node
**Question:** Delete the middle node of linked list.
**Intuition:** Find middle using slow-fast, track previous, delete slow.
**Logic:** Slow-fast with prev pointer.

**Java:**
```java
Node deleteMiddle(Node head) {
 if (head == null || head.next == null) return null;

 Node slow = head;
 Node fast = head;
 Node prev = null;

 while (fast != null && fast.next != null) {
 prev = slow;
 slow = slow.next;
 fast = fast.next.next;
 }

 if (prev != null) {
 prev.next = slow.next;
 }

 return head;
}
```

---

## L10: Remove Nth Node from End
**Question:** Remove nth node from end in one pass.
**Intuition:** Two pointers with n gap. When first reaches end, second is at (n-1)th from end.
**Logic:** Move first n steps, then move both together.

**Java:**
```java
Node removeNthFromEnd(Node head, int n) {
 Node dummy = new Node(0, head);
 Node first = dummy;
 Node second = dummy;

 // Move first n+1 steps
 for (int i = 0; i <= n; i++) {
 first = first.next;
 }

 // Move both
 while (first != null) {
 first = first.next;
 second = second.next;
 }

 // Delete nth node
 second.next = second.next.next;

 return dummy.next;
}
// Time: O(n), Space: O(1)
```

---

## L11: Delete All Occurrences of a Key
**Question:** Delete all nodes with given value.
**Intuition:** Traverse and skip nodes with matching value.
**Logic:** Use dummy node to handle edge cases, adjust pointers.

**Java:**
```java
Node deleteAllOccurrences(Node head, int key) {
 Node dummy = new Node(0, head);
 Node prev = dummy;
 Node curr = head;

 while (curr != null) {
 if (curr.data == key) {
 prev.next = curr.next;
 } else {
 prev = curr;
 }
 curr = curr.next;
 }

 return dummy.next;
}
```

---

## L12: Merge Two Sorted Lists
**Question:** Merge two sorted linked lists into one sorted list.
**Intuition:** Compare heads, attach smaller one, move that pointer forward.
**Logic:** Two-pointer merging with dummy node.

**Java:**
```java
Node mergeTwoLists(Node l1, Node l2) {
 Node dummy = new Node(0);
 Node curr = dummy;

 while (l1 != null && l2 != null) {
 if (l1.data <= l2.data) {
 curr.next = l1;
 l1 = l1.next;
 } else {
 curr.next = l2;
 l2 = l2.next;
 }
 curr = curr.next;
 }

 if (l1 != null) curr.next = l1;
 if (l2 != null) curr.next = l2;

 return dummy.next;
}
// Time: O(n + m), Space: O(1)
```

---

## L13: Add 1 to a Number Represented as LinkedList
**Question:** Add 1 to number represented as linked list (head is most significant).
**Intuition:** Reverse, add 1 with carry, reverse back OR use recursion.
**Logic:** Reverse approach is simpler.

**Java:**
```java
Node addOne(Node head) {
 // Reverse
 head = reverse(head);

 // Add 1
 Node curr = head;
 int carry = 1;

 while (curr != null && carry > 0) {
 int sum = curr.data + carry;
 curr.data = sum % 10;
 carry = sum / 10;

 if (curr.next == null && carry > 0) {
 curr.next = new Node(carry);
 carry = 0;
 }
 curr = curr.next;
 }

 // Reverse back
 head = reverse(head);
 return head;
}
```

---

## L14: Add Two Numbers
**Question:** Add two numbers represented as linked lists (reverse order).
**Intuition:** Add digit by digit with carry, create new nodes.
**Logic:** Traverse both lists, compute sum with carry.

**Java:**
```java
Node addTwoNumbers(Node l1, Node l2) {
 Node dummy = new Node(0);
 Node curr = dummy;
 int carry = 0;

 while (l1 != null || l2 != null || carry != 0) {
 int sum = carry;

 if (l1 != null) {
 sum += l1.data;
 l1 = l1.next;
 }

 if (l2 != null) {
 sum += l2.data;
 l2 = l2.next;
 }

 carry = sum / 10;
 curr.next = new Node(sum % 10);
 curr = curr.next;
 }

 return dummy.next;
}
// Time: O(max(n, m)), Space: O(max(n, m))
```

---

## L15: Intersection of Two LinkedLists
**Question:** Find node where two lists intersect.

### Interview Explanation: How We Come to This Solution

**Step 1: Understanding the Problem**
- Two linked lists may share some common nodes at the end
- Once they intersect, they share all remaining nodes
- We need to find the first common node
- Lists can have different lengths before intersection

**Visual Example:**
```
List A: 1 -> 2 -> 3 \
 7 -> 8 -> 9 -> NULL (intersection at node 7)
List B: 4 -> 5 -> 6 /

List C: 1 -> 2 -> 3 -> NULL (no intersection)
List D: 4 -> 5 -> NULL
```

**Step 2: Brute Force Approaches**

**Approach 1: Using HashSet - O(n+m) time, O(n) space**
```java
Node getIntersectionNode(Node headA, Node headB) {
 Set<Node> visited = new HashSet<>();

 // Store all nodes of list A
 Node curr = headA;
 while (curr != null) {
 visited.add(curr);
 curr = curr.next;
 }

 // Check which node of list B is in set
 curr = headB;
 while (curr != null) {
 if (visited.contains(curr)) {
 return curr; // First intersection!
 }
 curr = curr.next;
 }

 return null;
}
```
**Problem:** Uses O(n) extra space. Can we do better?

**Approach 2: Calculate Lengths - O(n+m) time, O(1) space**
```java
Node getIntersectionNode(Node headA, Node headB) {
 int lenA = getLength(headA);
 int lenB = getLength(headB);

 // Move longer list's pointer ahead by difference
 while (lenA > lenB) {
 headA = headA.next;
 lenA--;
 }
 while (lenB > lenA) {
 headB = headB.next;
 lenB--;
 }

 // Now both are equidistant from intersection
 while (headA != headB) {
 headA = headA.next;
 headB = headB.next;
 }

 return headA;
}
```
**Works but:** Requires 3 passes (2 for length, 1 for finding intersection)

**Step 3: The Elegant Solution - Pointer Redirection**

**Key Insight:**
- If we make pointers "swap" lists when they reach the end, they travel equal distance!
- Both will either meet at intersection or both become null (no intersection)

**Why Does This Work? Mathematical Proof:**

Let's define:
- `A` = Length of unique part of list A (before intersection)
- `B` = Length of unique part of list B (before intersection)
- `C` = Length of common part (intersection to end)

**Visual:**
```
List A: [A nodes] -> [C common nodes]
List B: [B nodes] -> [C common nodes]
```

**Path traveled by each pointer:**
- **Pointer a:** A + C + B nodes (goes through list A, then list B)
- **Pointer b:** B + C + A nodes (goes through list B, then list A)

**Both travel:** `A + B + C` nodes! 🎯

**If intersection exists:**
- After `A + B` steps, both pointers are at the intersection start
- They meet at the intersection node!

**If no intersection (C = 0):**
- After `A + B` steps, both become null
- They "meet" at null! Return null.

**Step 4: Optimized Solution**

**Java:**
```java
Node getIntersectionNode(Node headA, Node headB) {
 if (headA == null || headB == null) return null;

 Node a = headA;
 Node b = headB;

 // Keep traversing until they meet
 while (a != b) {
 // When a reaches end, redirect to headB
 // When b reaches end, redirect to headA
 a = (a == null) ? headB : a.next;
 b = (b == null) ? headA : b.next;
 }

 return a; // Either intersection node or null
}
// Time: O(n + m), Space: O(1)
```

**Detailed Walkthrough Example:**
```
List A: 1 -> 2 -> 7 -> 8 -> 9 -> NULL (length 5)
List B: 3 -> 4 -> 5 -> 7 -> 8 -> 9 -> NULL (length 7)
Intersection at node 7

Step-by-step:
┌──────┬─────┬─────┬─────────────────────────────────────┐
│ Step │ a │ b │ Comment │
├──────┼─────┼─────┼─────────────────────────────────────┤
│ 0 │ 1 │ 3 │ Start │
│ 1 │ 2 │ 4 │ │
│ 2 │ 7 │ 5 │ │
│ 3 │ 8 │ 7 │ │
│ 4 │ 9 │ 8 │ │
│ 5 │NULL │ 9 │ │
│ 6 │ 3 │NULL │ a redirected to headB │
│ 7 │ 4 │ 1 │ b redirected to headA │
│ 8 │ 5 │ 2 │ │
│ 9 │ 7 │ 7 │ Both meet at intersection! Return 7 │
└──────┴─────┴─────┴─────────────────────────────────────┘

Distance traveled:
- a: 5 (list A) + 3 (list B until node 7) = 8 nodes
- b: 7 (list B) + 1 (list A until node 7) = 8 nodes ✅ Equal!
```

**No Intersection Example:**
```
List A: 1 -> 2 -> 3 -> NULL
List B: 4 -> 5 -> NULL

Step-by-step:
- a: 1 -> 2 -> 3 -> NULL -> 4 -> 5 -> NULL
- b: 4 -> 5 -> NULL -> 1 -> 2 -> 3 -> NULL

Both become NULL at the same time → return NULL
```

**Key Interview Points:**
1. ✅ **Elegant**: No need to calculate lengths explicitly
2. ✅ **Optimal**: O(n+m) time, O(1) space - can't do better!
3. ✅ **Single pass**: Traverses each list at most twice
4. ✅ **Mathematical beauty**: Equal distance traveled by both pointers
5. ✅ **Handles all cases**:
 - Different lengths ✓
 - No intersection ✓
 - Same length ✓
 - One or both null ✓

**Common Interview Questions:**
- Q: "What if lists have cycles?"
 - A: This algorithm assumes no cycles. With cycles, use cycle detection first.

- Q: "Can we use this for arrays?"
 - A: No, this works because of pointer redirection. Arrays need different approach.

- Q: "What's the maximum iterations?"
 - A: At most `length(A) + length(B)` iterations.

**Alternative Verbal Explanation:**
"Imagine two people walking at the same speed. When they reach the end of their path, they switch paths. Since both walk the same total distance (their path + other's path), they'll meet at the intersection point - or both finish at the same time if no intersection exists."

---

## L16: Sort LinkedList (Merge Sort)
**Question:** Sort linked list using O(n log n) algorithm.
**Intuition:** Merge sort - divide into halves, sort recursively, merge.
**Logic:** Find middle, split, recurse, merge sorted halves.

**Java:**
```java
Node sortList(Node head) {
 if (head == null || head.next == null) return head;

 // Find middle
 Node mid = findMiddle(head);
 Node left = head;
 Node right = mid.next;
 mid.next = null;

 // Sort halves
 left = sortList(left);
 right = sortList(right);

 // Merge
 return merge(left, right);
}

Node findMiddle(Node head) {
 Node slow = head;
 Node fast = head.next;

 while (fast != null && fast.next != null) {
 slow = slow.next;
 fast = fast.next.next;
 }

 return slow;
}
// Time: O(n log n), Space: O(log n)
```

---

## L17: Sort 0s, 1s, and 2s in LinkedList
**Question:** Sort linked list containing only 0s, 1s, and 2s.
**Intuition:** Count each number, rebuild list OR use three dummy nodes.
**Logic:** Three separate lists for 0s, 1s, 2s, then connect.

**Java:**
```java
Node sortList(Node head) {
 if (head == null || head.next == null) return head;

 Node zero = new Node(0);
 Node one = new Node(0);
 Node two = new Node(0);

 Node z = zero, o = one, t = two;
 Node curr = head;

 while (curr != null) {
 if (curr.data == 0) {
 z.next = curr;
 z = z.next;
 } else if (curr.data == 1) {
 o.next = curr;
 o = o.next;
 } else {
 t.next = curr;
 t = t.next;
 }
 curr = curr.next;
 }

 // Connect lists
 z.next = (one.next != null) ? one.next : two.next;
 o.next = two.next;
 t.next = null;

 return zero.next;
}
```

---

## L18: Find Pairs with Given Sum in DLL
**Question:** Find all pairs in sorted DLL with sum equal to target.
**Intuition:** Two pointers from both ends. Move based on sum comparison.
**Logic:** Left and right pointers moving towards each other.

**Java:**
```java
List<int[]> findPairs(DLLNode head, int target) {
 List<int[]> result = new ArrayList<>();

 if (head == null) return result;

 // Find tail
 DLLNode left = head;
 DLLNode right = head;
 while (right.next != null) {
 right = right.next;
 }

 // Two pointer
 while (left.data < right.data) {
 int sum = left.data + right.data;

 if (sum == target) {
 result.add(new int[]{left.data, right.data});
 left = left.next;
 right = right.prev;
 } else if (sum < target) {
 left = left.next;
 } else {
 right = right.prev;
 }
 }

 return result;
}
```

---

## L19: Remove Duplicates from Sorted LinkedList
**Question:** Remove duplicate nodes from sorted linked list.
**Intuition:** Since sorted, duplicates are adjacent. Skip duplicate nodes.
**Logic:** Compare current with next, skip if equal.

**Java:**
```java
Node deleteDuplicates(Node head) {
 if (head == null) return null;

 Node curr = head;

 while (curr != null && curr.next != null) {
 if (curr.data == curr.next.data) {
 curr.next = curr.next.next;
 } else {
 curr = curr.next;
 }
 }

 return head;
}
// Time: O(n), Space: O(1)
```

---

## L20: Remove Duplicates from Sorted DLL
**Question:** Remove duplicates from sorted doubly linked list.
**Intuition:** Similar to singly LL but also update prev pointers.
**Logic:** Skip duplicates and adjust both next and prev.

**Java:**
```java
DLLNode removeDuplicates(DLLNode head) {
 if (head == null) return null;

 DLLNode curr = head;

 while (curr != null && curr.next != null) {
 if (curr.data == curr.next.data) {
 DLLNode duplicate = curr.next;
 curr.next = duplicate.next;
 if (duplicate.next != null) {
 duplicate.next.prev = curr;
 }
 } else {
 curr = curr.next;
 }
 }

 return head;
}
```

---

## L21: Reverse LinkedList in Groups of K
**Question:** Reverse linked list in groups of size K.
**Intuition:** Find Kth node, reverse that segment, connect segments, repeat.
**Logic:** Break list into K-sized groups, reverse each, reconnect.

**Java:**
```java
Node reverseKGroup(Node head, int k) {
 if (head == null || k == 1) return head;

 Node dummy = new Node(0, head);
 Node prevGroup = dummy;

 while (true) {
 Node kthNode = getKthNode(prevGroup, k);

 if (kthNode == null) break;

 Node nextGroup = kthNode.next;

 // Reverse current group
 Node prev = nextGroup;
 Node curr = prevGroup.next;

 while (curr != nextGroup) {
 Node next = curr.next;
 curr.next = prev;
 prev = curr;
 curr = next;
 }

 Node temp = prevGroup.next;
 prevGroup.next = kthNode;
 prevGroup = temp;
 }

 return dummy.next;
}

Node getKthNode(Node start, int k) {
 Node curr = start;
 while (curr != null && k > 0) {
 curr = curr.next;
 k--;
 }
 return curr;
}
// Time: O(n), Space: O(1)
```

---

## L22: Rotate LinkedList
**Question:** Rotate list to the right by k places.
**Intuition:** Make circular, find new tail, break circle.
**Logic:** Connect tail to head, move to (len - k % len - 1)th node, break.

**Java:**
```java
Node rotateRight(Node head, int k) {
 if (head == null || head.next == null || k == 0) return head;

 // Find length and tail
 Node tail = head;
 int len = 1;
 while (tail.next != null) {
 tail = tail.next;
 len++;
 }

 // Make circular
 tail.next = head;

 // Find new tail
 k = k % len;
 int stepsToNewTail = len - k;
 Node newTail = head;
 for (int i = 1; i < stepsToNewTail; i++) {
 newTail = newTail.next;
 }

 // Break circle
 Node newHead = newTail.next;
 newTail.next = null;

 return newHead;
}
// Time: O(n), Space: O(1)
```

---

## L23: Flatten a Linked List
**Question:** Flatten linked list where each node has next and bottom pointer (vertical lists).

### Interview Explanation: How We Come to This Solution

**Step 1: Understanding the Problem**
- Each node has TWO pointers: `next` (horizontal) and `bottom` (vertical)
- Nodes connected via `bottom` form sorted vertical lists
- We need to flatten into a single sorted list using only `bottom` pointers
- Similar to merging multiple sorted lists

**Visual Example:**
```
Input (multi-level structure):
 5 -> 10 -> 19 -> 28
 | | | |
 7 20 22 35
 | | |
 8 50 40
 | |
 30 45

Each vertical chain is sorted!

Output (flattened using bottom pointers):
5 -> 7 -> 8 -> 10 -> 19 -> 20 -> 22 -> 28 -> 30 -> 35 -> 40 -> 45 -> 50 -> NULL
```

**Step 2: Key Observations**
1. Each vertical list (connected by `bottom`) is already sorted
2. The `next` pointer connects the heads of different vertical lists
3. Result should use only `bottom` pointers (no `next` pointers)
4. This is essentially **merging K sorted lists** (where K = number of horizontal nodes)

**Step 3: Approach - Merge Sort Style**

**Key Insight:**
- Recursively flatten from right to left
- Merge current vertical list with the already-flattened right portion
- Similar to merge sort: divide, conquer, combine

**Why Recursion?**
- Break down: "Flatten everything to my right first"
- Then: "Merge me with that flattened result"
- Natural divide-and-conquer pattern

**Algorithm Steps:**
1. **Base case:** If no nodes or only one vertical list, return as is
2. **Recursive case:**
 - Recursively flatten everything to the right (via `next`)
 - Merge current vertical list with flattened right portion
 - Return the merged result

**Step 4: Implementation Breakdown**

**Java:**
```java
class Node {
 int data;
 Node next; // Horizontal pointer
 Node bottom; // Vertical pointer

 Node(int val) { data = val; }
}

Node flatten(Node head) {
 // Base case: empty or single vertical list
 if (head == null || head.next == null) {
 return head;
 }

 // Step 1: Recursively flatten everything to the right
 Node mergedRest = flatten(head.next);

 // Step 2: Disconnect current node's next pointer
 // (we only want bottom pointers in result)
 head.next = null;

 // Step 3: Merge current vertical list with flattened rest
 return mergeTwoLists(head, mergedRest);
}

// Standard merge of two sorted lists using bottom pointers
Node mergeTwoLists(Node l1, Node l2) {
 Node dummy = new Node(0);
 Node curr = dummy;

 // Merge while both lists have nodes
 while (l1 != null && l2 != null) {
 if (l1.data <= l2.data) {
 curr.bottom = l1;
 l1 = l1.bottom;
 } else {
 curr.bottom = l2;
 l2 = l2.bottom;
 }
 curr = curr.bottom;
 }

 // Attach remaining nodes
 if (l1 != null) curr.bottom = l1;
 if (l2 != null) curr.bottom = l2;

 return dummy.bottom;
}
// Time: O(N * M) where N = number of horizontal nodes, M = average vertical list length
// Space: O(N) for recursion stack
```

**Detailed Walkthrough Example:**
```
Input:
 5 -> 10 -> 19
 | | |
 7 20 22
 |
 8

Step-by-step execution:

1. flatten(5) called
 |
 ├─ Recursively flatten(10)
 | |
 | ├─ Recursively flatten(19)
 | | Base case: return 19→22→NULL
 | |
 | └─ Merge(10→20→NULL, 19→22→NULL)
 | Result: 10→19→20→22→NULL
 |
 └─ Merge(5→7→8→NULL, 10→19→20→22→NULL)
 Result: 5→7→8→10→19→20→22→NULL

Final result: 5 -> 7 -> 8 -> 10 -> 19 -> 20 -> 22 -> NULL
```

**Visual Recursion Tree:**
```
 flatten(5)
 |
 Merge(5→7→8, flattened rest)
 |
 flatten(10)
 |
 Merge(10→20, flattened rest)
 |
 flatten(19)
 |
 Base: 19→22
```

**Merge Example (5→7→8 with 10→19→20→22):**
```
Step l1 l2 Result
────────────────────────
 0 5 10 5→
 1 7 10 5→7→
 2 8 10 5→7→8→
 3 - 10 5→7→8→10→
 4 - 19 5→7→8→10→19→
 5 - 20 5→7→8→10→19→20→
 6 - 22 5→7→8→10→19→20→22→NULL
```

**Key Interview Points:**
1. ✅ **Two-phase approach:** Recursion + Merge
2. ✅ **Why right-to-left?** Ensures we merge smaller results progressively
3. ✅ **Time Complexity:** O(Total nodes) since each node is visited during merge
4. ✅ **Space:** O(N) recursion stack where N = horizontal nodes
5. ✅ **Edge cases:**
 - Empty list → return null
 - Single vertical list → return as is
 - All nodes in one vertical list → base case handles it

**Common Interview Questions:**

**Q: "Can we do this iteratively?"**
A: Yes! Use a stack or priority queue, but recursion is more elegant.

**Q: "Can we use a min-heap?"**
A: Yes, like "Merge K Sorted Lists" - add all heads to heap, extract min, add next bottom node. O(Total nodes × log K) time.

**Q: "Why not flatten left-to-right?"**
A: Works too! But right-to-left matches merge sort's natural flow.

**Q: "What if vertical lists aren't sorted?"**
A: Would need to sort each vertical list first, or collect all nodes and sort entirely.

**Alternative Approach - Priority Queue:**
```java
Node flattenWithHeap(Node head) {
 if (head == null) return null;

 PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.data - b.data);

 // Add all horizontal heads
 Node temp = head;
 while (temp != null) {
 pq.offer(temp);
 temp = temp.next;
 }

 Node dummy = new Node(0);
 Node curr = dummy;

 while (!pq.isEmpty()) {
 Node min = pq.poll();
 curr.bottom = min;
 curr = curr.bottom;

 if (min.bottom != null) {
 pq.offer(min.bottom);
 }
 }

 curr.bottom = null;
 return dummy.bottom;
}
// Time: O(Total nodes × log N), Space: O(N)
```

**Comparison:**
- **Recursive merge:** O(N×M) time, O(N) space, elegant code
- **Priority queue:** O(Total×log N) time, O(N) space, easier to understand

Choose recursive merge for interviews - shows strong recursion and merge skills!

---

## L24: Clone LinkedList with Random Pointer
**Question:** Deep copy linked list where each node has next and random pointer.
**Intuition:** Three steps - insert copy nodes in between, connect random pointers, separate lists.
**Logic:** Interleave original and copy, leverage this for random pointers.

**Java:**
```java
class Node {
 int data;
 Node next;
 Node random;

 Node(int val) { data = val; }
}

Node copyRandomList(Node head) {
 if (head == null) return null;

 // Step 1: Insert copy nodes
 Node curr = head;
 while (curr != null) {
 Node copy = new Node(curr.data);
 copy.next = curr.next;
 curr.next = copy;
 curr = copy.next;
 }

 // Step 2: Connect random pointers
 curr = head;
 while (curr != null) {
 if (curr.random != null) {
 curr.next.random = curr.random.next;
 }
 curr = curr.next.next;
 }

 // Step 3: Separate lists
 Node dummy = new Node(0);
 Node copy = dummy;
 curr = head;

 while (curr != null) {
 Node next = curr.next.next;

 // Extract copy
 copy.next = curr.next;
 copy = copy.next;

 // Restore original
 curr.next = next;
 curr = next;
 }

 return dummy.next;
}
// Time: O(n), Space: O(1) excluding output
```

---

## L25: Segregate Odd and Even Nodes
**Question:** Group all odd-indexed nodes together followed by even-indexed.
**Intuition:** Two separate lists for odd and even positions, connect at end.
**Logic:** Odd and even pointers alternating.

**Java:**
```java
Node oddEvenList(Node head) {
 if (head == null || head.next == null) return head;

 Node odd = head;
 Node even = head.next;
 Node evenHead = even;

 while (even != null && even.next != null) {
 odd.next = even.next;
 odd = odd.next;
 even.next = odd.next;
 even = even.next;
 }

 odd.next = evenHead;
 return head;
}
// Time: O(n), Space: O(1)
```

---

## L26: Delete Node when Node Pointer Given
**Question:** Delete a node when you only have pointer to that node (not head).
**Intuition:** Copy next node's value to current, delete next node.
**Logic:** This is the only way without access to previous node.

**Java:**
```java
void deleteNode(Node node) {
 if (node == null || node.next == null) return;

 // Copy next node's value
 node.data = node.next.data;

 // Delete next node
 node.next = node.next.next;
}
// Time: O(1), Space: O(1)
```

---

## L27: Merge K Sorted Lists
**Question:** Merge K sorted linked lists into one sorted list.
**Intuition:** Use min heap to efficiently find smallest element among K lists.
**Logic:** Priority queue with K elements at most.

**Java:**
```java
Node mergeKLists(Node[] lists) {
 if (lists == null || lists.length == 0) return null;

 PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.data - b.data);

 // Add first node of each list
 for (Node list : lists) {
 if (list != null) {
 pq.offer(list);
 }
 }

 Node dummy = new Node(0);
 Node curr = dummy;

 while (!pq.isEmpty()) {
 Node smallest = pq.poll();
 curr.next = smallest;
 curr = curr.next;

 if (smallest.next != null) {
 pq.offer(smallest.next);
 }
 }

 return dummy.next;
}
// Time: O(N log K) where N = total nodes, K = number of lists
// Space: O(K)
```

---

## L28: Multiply Two LinkedLists
**Question:** Multiply two numbers represented as linked lists.
**Intuition:** Convert both lists to numbers (handling overflow), multiply.
**Logic:** Traverse and build numbers with modulo to prevent overflow.

**Java:**
```java
long multiplyTwoLists(Node l1, Node l2) {
 long MOD = 1000000007;

 long num1 = 0, num2 = 0;

 while (l1 != null) {
 num1 = (num1 * 10 + l1.data) % MOD;
 l1 = l1.next;
 }

 while (l2 != null) {
 num2 = (num2 * 10 + l2.data) % MOD;
 l2 = l2.next;
 }

 return (num1 * num2) % MOD;
}
```

---

## Important LinkedList Patterns

### Pattern 1: Slow-Fast Pointer (Floyd's Algorithm)
**Use Cases:** Find middle, detect cycle, palindrome check
**Key Insight:** When fast reaches end, slow is at middle or meeting point

```java
Node slow = head, fast = head;
while (fast != null && fast.next != null) {
 slow = slow.next;
 fast = fast.next.next;
}
```

### Pattern 2: Dummy Node
**Use Cases:** Merge lists, complex deletions, list construction
**Key Insight:** Simplifies edge cases (empty list, head deletion)

```java
Node dummy = new Node(0);
dummy.next = head;
// Work with dummy.next
return dummy.next;
```

### Pattern 3: Reverse LinkedList
**Use Cases:** Reverse entire list, reverse in groups, palindrome
**Key Insight:** Three pointers (prev, curr, next)

```java
Node prev = null, curr = head;
while (curr != null) {
 Node next = curr.next;
 curr.next = prev;
 prev = curr;
 curr = next;
}
return prev;
```

### Pattern 4: Two Pointer with Gap
**Use Cases:** Remove nth from end, intersection point
**Key Insight:** Create gap of n, then move together

```java
// Move first n steps
for (int i = 0; i < n; i++) {
 first = first.next;
}
// Move both together
while (first != null) {
 first = first.next;
 second = second.next;
}
```

### Pattern 5: Recursion
**Use Cases:** Reverse, merge, flatten
**Key Insight:** Base case + process rest + combine

```java
Node recursive(Node head) {
 if (head == null || head.next == null) return head;

 Node result = recursive(head.next);
 // Process current node
 return result;
}
```

### Pattern 6: InterLeaving
**Use Cases:** Clone with random pointer, reorder list
**Key Insight:** Insert nodes in between, process, then separate

```java
// Insert copies in between
// Original: 1->2->3
// After: 1->1'->2->2'->3->3'
```

---

## Time Complexity Cheat Sheet

| Operation | Time | Space |
| ------------------- | ---------- | -------- |
| Traversal | O(n) | O(1) |
| Search | O(n) | O(1) |
| Insert at Head | O(1) | O(1) |
| Insert at Tail | O(n) | O(1) |
| Delete at Head | O(1) | O(1) |
| Delete at Tail | O(n) | O(1) |
| Reverse (Iterative) | O(n) | O(1) |
| Reverse (Recursive) | O(n) | O(n) |
| Detect Cycle | O(n) | O(1) |
| Find Middle | O(n) | O(1) |
| Merge Two Lists | O(n+m) | O(1) |
| Sort (Merge Sort) | O(n log n) | O(log n) |
| Clone with Random | O(n) | O(1) |

---