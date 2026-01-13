
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
**Intuition:** Slow pointer moves 1 step, fast moves 2 steps. If they meet, there's a cycle.  
**Logic:** Two pointers at different speeds. Meeting point indicates cycle.

**Java:**
```java
boolean hasCycle(Node head) {
    if (head == null || head.next == null) return false;

    Node slow = head;
    Node fast = head;

    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;

        if (slow == fast) {
            return true;
        }
    }

    return false;
}
// Time: O(n), Space: O(1)
```

---

## L5: Find Starting Point of Loop
**Question:** Find the node where cycle begins.  
**Intuition:** After detecting cycle, move one pointer to head. Move both at same speed - they meet at cycle start.  
**Logic:** Mathematical proof: distance from head to start = distance from meeting point to start within cycle.

**Java:**
```java
Node detectCycle(Node head) {
    if (head == null || head.next == null) return null;

    Node slow = head;
    Node fast = head;

    // Detect cycle
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;

        if (slow == fast) {
            // Find start
            slow = head;
            while (slow != fast) {
                slow = slow.next;
                fast = fast.next;
            }
            return slow;
        }
    }

    return null;
}
// Time: O(n), Space: O(1)
```

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
**Intuition:** Two pointers. When one reaches end, redirect to other list's head. They meet at intersection.  
**Logic:** Both travel same total distance.

**Java:**
```java
Node getIntersectionNode(Node headA, Node headB) {
    if (headA == null || headB == null) return null;

    Node a = headA;
    Node b = headB;

    while (a != b) {
        a = (a == null) ? headB : a.next;
        b = (b == null) ? headA : b.next;
    }

    return a;
}
// Time: O(n + m), Space: O(1)
```

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
**Intuition:** Merge lists vertically one by one like merge sort.  
**Logic:** Recursively flatten and merge sorted lists.

**Java:**
```java
class Node {
    int data;
    Node next;
    Node bottom;

    Node(int val) { data = val; }
}

Node flatten(Node head) {
    if (head == null || head.next == null) {
        return head;
    }

    // Flatten rest
    Node mergedRest = flatten(head.next);

    // Merge current with flattened rest
    head.next = null;
    return mergeTwoLists(head, mergedRest);
}

Node mergeTwoLists(Node l1, Node l2) {
    Node dummy = new Node(0);
    Node curr = dummy;

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

    if (l1 != null) curr.bottom = l1;
    if (l2 != null) curr.bottom = l2;

    return dummy.bottom;
}
// Time: O(N * M) where N = horizontal, M = average vertical length
```

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
|-----------|------|-------|
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