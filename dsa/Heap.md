
## Table of Contents
- **Part 1: Heap Fundamentals** - Min Heap, Max Heap, Heapify
- **Part 2: K-Pattern Problems** - Kth Largest/Smallest variations
- **Part 3: Frequency-Based Problems** - Top K Frequent elements
- **Part 4: Advanced Heap Patterns** - Two Heaps, Merge K, Median

---

# Part 1: Heap Fundamentals

## Introduction to Heap & Priority Queue
**Question:** What is a Heap? What is Priority Queue?
**Intuition:** **Heap** is a **complete binary tree** that satisfies the heap property! **Max Heap**: parent >= children (root = maximum). **Min Heap**: parent <= children (root = minimum). **Priority Queue** is an **abstract data type** (ADT) where elements have priorities - higher priority served first. **Heap is the most common implementation** of Priority Queue!

**Real-World Use Cases:**
- **Operating Systems** - CPU scheduling, task prioritization
- **Graph Algorithms** - Dijkstra's, Prim's MST
- **Event-Driven Simulation** - Process events by timestamp
- **Load Balancing** - Assign tasks to least loaded servers
- **Stock Market** - Track highest/lowest prices in real-time
- **Autocomplete** - Show most relevant suggestions first
- **Data Compression** - Huffman coding
- **Median Finding** - Running median from data stream

**Key Properties:**
1. **Complete Binary Tree** - All levels filled except possibly last (filled left to right)
2. **Heap Property** - Parent-child relationship maintained
3. **Array Representation** - Efficient storage without pointers!
4. **Parent-Child Formulas** (0-indexed):
 - Parent of i: `(i-1)/2`
 - Left child of i: `2*i + 1`
 - Right child of i: `2*i + 2`

**Why Heap Over BST for Priority Queue?**
- Heap: O(1) peek, O(log n) insert/delete
- BST: O(log n) for all (but O(n) if unbalanced)
- Heap uses **less memory** (array vs pointers)
- Heap has **better cache locality**

**Java PriorityQueue:**

```java
// Min Heap (default)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();

// Max Heap (reverse comparator)
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
// Or
PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

// Custom comparator
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
 if (a[0] != b[0]) return a[0] - b[0]; // Compare first element
 return a[1] - b[1]; // Tie-breaker: compare second
});

// Common operations
pq.offer(element); // Insert - O(log n)
pq.peek(); // Get min/max - O(1)
pq.poll(); // Remove min/max - O(log n)
pq.size(); // Size - O(1)
pq.isEmpty(); // Check empty - O(1)
pq.contains(element); // Check existence - O(n)
```

**Heap Implementation (for understanding):**

```java
class MinHeap {
    int[] heap;
    int size;
    int capacity;

    MinHeap(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        heap = new int[capacity];
    }

    // Get parent index
    int parent(int i) {
        return (i - 1) / 2;
    }

    // Get left child
    int left(int i) {
        return 2 * i + 1;
    }

    // Get right child
    int right(int i) {
        return 2 * i + 2;
    }

    // Insert element
    void insert(int val) {
        if (size == capacity) {
            System.out.println("Heap overflow");
            return;
        }

        // Insert at end
        int i = size;
        heap[i] = val;
        size++;

        // Heapify up (bubble up)
        while (i != 0 && heap[parent(i)] > heap[i]) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    // Extract minimum
    int extractMin() {
        if (size == 0) return Integer.MAX_VALUE;
        if (size == 1) {
            size--;
            return heap[0];
        }

        int root = heap[0];
        heap[0] = heap[size - 1];
        size--;

        minHeapify(0); // Heapify down

        return root;
    }

    // Heapify down
    void minHeapify(int i) {
        int l = left(i);
        int r = right(i);
        int smallest = i;

        if (l < size && heap[l] < heap[smallest]) {
            smallest = l;
        }

        if (r < size && heap[r] < heap[smallest]) {
            smallest = r;
        }

        if (smallest != i) {
            swap(i, smallest);
            minHeapify(smallest);
        }
    }

    void swap(int i, int j) {
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    int getMin() {
        return heap[0];
    }
}
```

---

## Heap Pattern Identification
**How to identify Heap problems in interviews?**

ðŸŽ¯ **Keywords to look for:**
- "K largest/smallest"
- "Top K"
- "Kth largest/smallest"
- "Closest K"
- "Most frequent K"
- "Median"
- "Running median"
- "Merge K sorted"
- "Find minimum/maximum repeatedly"

ðŸŽ¯ **When NOT to use Heap:**
- Need full sorted order -> Use sorting
- Need fast search -> Use HashMap/TreeMap
- Range queries -> Use Segment Tree

---

# Part 2: K-Pattern Problems

## Problem 1: Kth Smallest Element
**Question:** Find Kth smallest element in unsorted array.
**Intuition:** **Core Heap Pattern!** Use **Max Heap of size K**. Why max heap? Keep K smallest elements. If new element smaller than heap top (largest among K smallest), replace! At end, heap top = Kth smallest.
**Logic:** Maintain max heap of size K. For each element: if heap size < K, add. Else if element < heap.peek(), remove top and add element.
**Java:**

```java
int kthSmallest(int[] arr, int k) {
    // Max heap of size k
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

    for (int num : arr) {
        maxHeap.offer(num);

        if (maxHeap.size() > k) {
            maxHeap.poll(); // Remove largest
        }
    }

    return maxHeap.peek(); // Top = kth smallest
}

// Time: O(n log k), Space: O(k)
// Better than sorting: O(n log n)
```

**Example:**

```
Array: [7, 10, 4, 3, 20, 15], k = 3
Step by step with max heap of size 3:
- Add 7: [7]
- Add 10: [10, 7]
- Add 4: [10, 7, 4]
- Add 3: [10, 7, 4] -> 3 < 10 -> [7, 4, 3]
- Add 20: [7, 4, 3] -> 20 > 7 -> skip
- Add 15: [7, 4, 3] -> 15 > 7 -> skip
Answer: 7 (3rd smallest)
```

---

## Problem 2: Kth Largest Element
**Question:** Find Kth largest element.
**Intuition:** **Opposite of Kth smallest!** Use **Min Heap of size K**. Keep K largest elements. If new element larger than heap top (smallest among K largest), replace!
**Java:**

```java
int kthLargest(int[] arr, int k) {
    // Min heap of size k
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();

    for (int num : arr) {
        minHeap.offer(num);

        if (minHeap.size() > k) {
            minHeap.poll(); // Remove smallest
        }
    }

    return minHeap.peek(); // Top = kth largest
}
```

---

## Problem 3: K Largest Elements (Return All)
**Question:** Return K largest elements (in any order).
**Intuition:** Same as Kth largest but return entire heap!
**Java:**

```java
List<Integer> kLargest(int[] arr, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();

    for (int num : arr) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();
        }
    }

    return new ArrayList<>(minHeap);
}
```

---

## Problem 4: K Smallest Elements (Return All)
**Question:** Return K smallest elements.
**Java:**

```java
List<Integer> kSmallest(int[] arr, int k) {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

    for (int num : arr) {
        maxHeap.offer(num);
        if (maxHeap.size() > k) {
            maxHeap.poll();
        }
    }

    return new ArrayList<>(maxHeap);
}
```

---

## Problem 5: K Closest Numbers to X
**Question:** Given sorted array and target X, find K closest numbers.
**Intuition:** "Closest" means minimum absolute difference! Use **Max Heap** storing pairs (difference, number). Keep K closest = smallest differences.
**Java:**

```java
List<Integer> kClosest(int[] arr, int k, int x) {
    // Max heap based on distance from x
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> b[0] - a[0] // Compare by distance
    );

    for (int num : arr) {
        int dist = Math.abs(num - x);
        maxHeap.offer(new int[]{dist, num});

        if (maxHeap.size() > k) {
            maxHeap.poll();
        }
    }

    List<Integer> result = new ArrayList<>();
    while (!maxHeap.isEmpty()) {
        result.add(maxHeap.poll()[1]);
    }

    return result;
}
```

**Example:**

```
arr = [5, 6, 7, 8, 9], k = 3, x = 7
Distances: [2, 1, 0, 1, 2]
Max heap of size 3 (store 3 smallest distances):
After processing: distances [0, 1, 1] -> numbers [7, 6, 8]
```

---

## Problem 6: K Closest Points to Origin
**Question:** Find K closest points to origin (0, 0).
**Intuition:** Distance = ˆš(x² + y²). Can skip sqrt since relative ordering same! Use **Max Heap** on squared distances.
**Java:**

```java
int[][] kClosest(int[][] points, int k) {
    // Max heap based on distance squared
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> (b[0] * b[0] + b[1] * b[1]) - (a[0] * a[0] + a[1] * a[1])
    );

    for (int[] point : points) {
        maxHeap.offer(point);

        if (maxHeap.size() > k) {
            maxHeap.poll();
        }
    }

    int[][] result = new int[k][2];
    int i = 0;
    while (!maxHeap.isEmpty()) {
        result[i++] = maxHeap.poll();
    }

    return result;
}
// Time: O(n log k), Space: O(k)
```

---

## Problem 7: Sort K-Sorted (Nearly Sorted) Array
**Question:** Array where each element is at most K positions away from target position. Sort it efficiently.
**Intuition:** Element at index i can only be in range [i-k, i+k]! Use **Min Heap of size K+1**. First K+1 elements -> heap min must be at position 0! Continue sliding window.
**Java:**

```java
int[] sortKSorted(int[] arr, int k) {
    int n = arr.length;
    int[] result = new int[n];
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();

    int index = 0;

    // Add first k+1 elements to heap
    for (int i = 0; i <= k && i < n; i++) {
        minHeap.offer(arr[i]);
    }

    // Extract min and add next element
    for (int i = k + 1; i < n; i++) {
        result[index++] = minHeap.poll();
        minHeap.offer(arr[i]);
    }

    // Empty remaining heap
    while (!minHeap.isEmpty()) {
        result[index++] = minHeap.poll();
    }

    return result;
}
// Time: O(n log k), Space: O(k)
// Much better than O(n log n) sorting!
```

**Example:**

```
arr = [6, 5, 3, 2, 8, 10, 9], k = 3
Each element at most 3 positions away from sorted position.
Sorted: [2, 3, 5, 6, 8, 9, 10]

Heap of size k+1 = 4:
[6, 5, 3, 2] -> min = 2 -> output 2, add 8
[6, 5, 3, 8] -> min = 3 -> output 3, add 10
... continues
```

---

# Part 3: Frequency-Based Problems

## Problem 8: Top K Frequent Elements
**Question:** Given array, return K most frequent elements.
**Intuition:** **Two-step approach**: (1) Count frequencies using HashMap, (2) Use **Min Heap of size K** on frequencies! Min heap because we keep K largest frequencies.
**Java:**

```java
int[] topKFrequent(int[] nums, int k) {
    // Step 1: Count frequencies
    Map<Integer, Integer> freqMap = new HashMap<>();
    for (int num : nums) {
        freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
    }

    // Step 2: Min heap of size k based on frequency
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(
            (a, b) -> freqMap.get(a) - freqMap.get(b)
    );

    for (int num : freqMap.keySet()) {
        minHeap.offer(num);

        if (minHeap.size() > k) {
            minHeap.poll(); // Remove least frequent
        }
    }

    int[] result = new int[k];
    int i = 0;
    while (!minHeap.isEmpty()) {
        result[i++] = minHeap.poll();
    }

    return result;
}
// Time: O(n + m log k) where m = unique elements
// Space: O(m + k)
```

---

## Problem 9: Sort Array by Frequency
**Question:** Sort array by frequency (most frequent first). If tie, smaller number first.
**Intuition:** Build frequency map, then use **Max Heap** with custom comparator!
**Java:**

```java
int[] sortByFrequency(int[] arr) {
    Map<Integer, Integer> freqMap = new HashMap<>();
    for (int num : arr) {
        freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
    }

    // Max heap: higher frequency first, tie -> smaller number first
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> {
        int freqCompare = freqMap.get(b) - freqMap.get(a);
        if (freqCompare != 0) return freqCompare;
        return a - b; // Tie breaker
    });

    maxHeap.addAll(freqMap.keySet());

    int[] result = new int[arr.length];
    int index = 0;

    while (!maxHeap.isEmpty()) {
        int num = maxHeap.poll();
        int freq = freqMap.get(num);
        for (int i = 0; i < freq; i++) {
            result[index++] = num;
        }
    }

    return result;
}
```

---

## Problem 10: Frequency Sort (String)
**Question:** Sort characters in string by frequency.
**Intuition:** Same pattern as array frequency sort!
**Java:**

```java
String frequencySort(String s) {
    Map<Character, Integer> freqMap = new HashMap<>();
    for (char c : s.toCharArray()) {
        freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
    }

    PriorityQueue<Character> maxHeap = new PriorityQueue<>(
            (a, b) -> freqMap.get(b) - freqMap.get(a)
    );

    maxHeap.addAll(freqMap.keySet());

    StringBuilder result = new StringBuilder();
    while (!maxHeap.isEmpty()) {
        char c = maxHeap.poll();
        int freq = freqMap.get(c);
        for (int i = 0; i < freq; i++) {
            result.append(c);
        }
    }

    return result.toString();
}
```

---

## Problem 11: K Most Frequent Words
**Question:** Return K most frequent words. If tie, lexicographically smaller comes first.
**Intuition:** Frequency + lexicographic ordering! Careful with comparator!
**Java:**

```java
List<String> topKFrequent(String[] words, int k) {
    Map<String, Integer> freqMap = new HashMap<>();
    for (String word : words) {
        freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
    }

    // Min heap: lower frequency first, tie -> lexicographically larger first
    // (opposite of final order for min heap of size k)
    PriorityQueue<String> minHeap = new PriorityQueue<>((a, b) -> {
        int freqCompare = freqMap.get(a) - freqMap.get(b);
        if (freqCompare != 0) return freqCompare;
        return b.compareTo(a); // Reverse lexicographic
    });

    for (String word : freqMap.keySet()) {
        minHeap.offer(word);
        if (minHeap.size() > k) {
            minHeap.poll();
        }
    }

    List<String> result = new ArrayList<>();
    while (!minHeap.isEmpty()) {
        result.add(0, minHeap.poll()); // Add to front
    }

    return result;
}
```

---

# Part 4: Advanced Heap Patterns

## Pattern 1: Two Heaps (Median Finding)

### Problem 12: Find Median from Data Stream 
**Question:** Design data structure that supports addNum(num) and findMedian() operations.
**Intuition:** **Two heaps trick!** **Max heap** for smaller half, **Min heap** for larger half. Keep heaps balanced (size difference <= 1). Median = heap tops!

**Why Two Heaps?**
- Max heap stores smaller half -> top = largest of small half
- Min heap stores larger half -> top = smallest of large half
- Median is between these two tops!

**Balanced Heaps:**
- If total even: median = average of tops
- If total odd: median = top of larger heap

**Java:**

```java
class MedianFinder {
    PriorityQueue<Integer> maxHeap; // Smaller half
    PriorityQueue<Integer> minHeap; // Larger half

    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        // Always add to max heap first
        maxHeap.offer(num);

        // Balance: move largest from max heap to min heap
        minHeap.offer(maxHeap.poll());

        // Balance sizes: max heap should have same or 1 more
        if (maxHeap.size() < minHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }
}

// addNum: O(log n), findMedian: O(1)
// Space: O(n)
```

**Example:**

```
Add 1: maxHeap=[1], minHeap=[]
 Median = 1

Add 2: maxHeap=[1], minHeap=[2]
 Median = (1+2)/2 = 1.5

Add 3: maxHeap=[2,1], minHeap=[3]
 Median = 2

Add 4: maxHeap=[2,1], minHeap=[3,4]
 Median = (2+3)/2 = 2.5
```

---

### Problem 13: Sliding Window Median 
**Question:** Find median in sliding window of size k.
**Intuition:** Extend two heaps pattern! But need to handle removal (lazy deletion using HashMap to track removed elements).
**Java:**

```java
double[] medianSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    double[] result = new double[n - k + 1];

    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    Map<Integer, Integer> delayed = new HashMap<>(); // Lazy deletion

    // Initialize first window
    for (int i = 0; i < k; i++) {
        maxHeap.offer(nums[i]);
    }
    for (int i = 0; i < k / 2; i++) {
        minHeap.offer(maxHeap.poll());
    }

    int balance = 0; // maxHeap.size - minHeap.size

    for (int i = k; i <= n; i++) {
        // Calculate median
        result[i - k] = (k % 2 == 1) ?
                maxHeap.peek() :
                ((long) maxHeap.peek() + minHeap.peek()) / 2.0;

        if (i == n) break;

        // Remove outgoing element (lazy)
        int out = nums[i - k];
        delayed.put(out, delayed.getOrDefault(out, 0) + 1);
        balance += (out <= maxHeap.peek()) ? -1 : 1;

        // Add incoming element
        int in = nums[i];
        if (!maxHeap.isEmpty() && in <= maxHeap.peek()) {
            maxHeap.offer(in);
            balance++;
        } else {
            minHeap.offer(in);
            balance--;
        }

        // Rebalance
        if (balance < 0) {
            maxHeap.offer(minHeap.poll());
            balance++;
        } else if (balance > 1) {
            minHeap.offer(maxHeap.poll());
            balance--;
        }

        // Clean tops
        while (!maxHeap.isEmpty() && delayed.getOrDefault(maxHeap.peek(), 0) > 0) {
            delayed.put(maxHeap.peek(), delayed.get(maxHeap.peek()) - 1);
            maxHeap.poll();
        }
        while (!minHeap.isEmpty() && delayed.getOrDefault(minHeap.peek(), 0) > 0) {
            delayed.put(minHeap.peek(), delayed.get(minHeap.peek()) - 1);
            minHeap.poll();
        }
    }

    return result;
}
```

---

## Pattern 2: Merge K Sorted

### Problem 14: Merge K Sorted Lists 
**Question:** Merge K sorted linked lists into one sorted list.
**Intuition:** Use **Min Heap** to track smallest element among K lists! Always pick minimum of K current heads. Each list contributes O(log K) per element.
**Java:**

```java
class ListNode {
 int val;
 ListNode next;
 ListNode(int val) { this.val = val; }
}

ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> minHeap = new PriorityQueue<>(
            (a, b) -> a.val - b.val
    );

    // Add first node of each list
    for (ListNode head : lists) {
        if (head != null) {
            minHeap.offer(head);
        }
    }

    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;

    while (!minHeap.isEmpty()) {
        ListNode node = minHeap.poll();
        curr.next = node;
        curr = curr.next;

        if (node.next != null) {
            minHeap.offer(node.next);
        }
    }

    return dummy.next;
}
// Time: O(N log K) where N = total nodes
// Space: O(K)
```

---

### Problem 15: Merge K Sorted Arrays
**Question:** Merge K sorted arrays into one.
**Intuition:** Same as K lists but track (value, arrayIndex, elementIndex) in heap!
**Java:**

```java
List<Integer> mergeKArrays(int[][] arrays) {
    List<Integer> result = new ArrayList<>();

    // Min heap: (value, arrayIdx, elementIdx)
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(
            (a, b) -> a[0] - b[0]
    );

    // Add first element of each array
    for (int i = 0; i < arrays.length; i++) {
        if (arrays[i].length > 0) {
            minHeap.offer(new int[]{arrays[i][0], i, 0});
        }
    }

    while (!minHeap.isEmpty()) {
        int[] curr = minHeap.poll();
        int val = curr[0];
        int arrIdx = curr[1];
        int elemIdx = curr[2];

        result.add(val);

        // Add next element from same array
        if (elemIdx + 1 < arrays[arrIdx].length) {
            minHeap.offer(new int[]{
                    arrays[arrIdx][elemIdx + 1],
                    arrIdx,
                    elemIdx + 1
            });
        }
    }

    return result;
}
```

---

## Pattern 3: Interval Problems

### Problem 16: Meeting Rooms II (Minimum Rooms Required)
**Question:** Given meeting intervals, find minimum rooms needed.
**Intuition:** Use **Min Heap** to track end times of ongoing meetings! Sort meetings by start time. For each meeting, check if earliest ending meeting finished (heap top). If yes, reuse room. Else, need new room.
**Java:**

```java
int minMeetingRooms(int[][] intervals) {
    if (intervals.length == 0) return 0;

    // Sort by start time
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

    // Min heap of end times
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    minHeap.offer(intervals[0][1]);

    for (int i = 1; i < intervals.length; i++) {
        // If earliest ending meeting finished, reuse room
        if (intervals[i][0] >= minHeap.peek()) {
            minHeap.poll();
        }

        // Add current meeting's end time
        minHeap.offer(intervals[i][1]);
    }

    return minHeap.size(); // Size = rooms needed
}
// Time: O(n log n), Space: O(n)
```

**Example:**

```
Meetings: [[0,30], [5,10], [15,20]]
Sorted: [[0,30], [5,10], [15,20]]

heap = []
- [0,30]: heap = [30], rooms = 1
- [5,10]: 5 < 30 -> need new room, heap = [10, 30], rooms = 2
- [15,20]: 15 >= 10 -> reuse, heap = [20, 30], rooms = 2

Answer: 2 rooms
```

---

### Problem 17: Employee Free Time
**Question:** Given schedules of employees (list of intervals), find common free time.
**Intuition:** Merge all intervals, find gaps! Use heap to process intervals from all employees in sorted order.
**Java:**

```java
class Interval {
 int start, end;
 Interval(int start, int end) {
 this.start = start;
 this.end = end;
 }
}

List<Interval> employeeFreeTime(List<List<Interval>> schedule) {
    List<Interval> result = new ArrayList<>();

    // Min heap: (start, end, employeeIdx, intervalIdx)
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(
            (a, b) -> a[0] - b[0]
    );

    // Add first interval of each employee
    for (int i = 0; i < schedule.size(); i++) {
        if (!schedule.get(i).isEmpty()) {
            Interval interval = schedule.get(i).get(0);
            minHeap.offer(new int[]{interval.start, interval.end, i, 0});
        }
    }

    int prevEnd = minHeap.peek()[1];

    while (!minHeap.isEmpty()) {
        int[] curr = minHeap.poll();
        int start = curr[0];
        int end = curr[1];
        int empIdx = curr[2];
        int intIdx = curr[3];

        // Gap found!
        if (prevEnd < start) {
            result.add(new Interval(prevEnd, start));
        }

        prevEnd = Math.max(prevEnd, end);

        // Add next interval of same employee
        if (intIdx + 1 < schedule.get(empIdx).size()) {
            Interval next = schedule.get(empIdx).get(intIdx + 1);
            minHeap.offer(new int[]{next.start, next.end, empIdx, intIdx + 1});
        }
    }

    return result;
}
```

---

## Pattern 4: Advanced Problems

### Problem 18: Kth Largest Element in Stream
**Question:** Design class that supports add() and getKthLargest().
**Intuition:** Maintain **Min Heap of size K** with K largest elements. Top = Kth largest!
**Java:**

```java
class KthLargest {
    PriorityQueue<Integer> minHeap;
    int k;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        minHeap = new PriorityQueue<>();

        for (int num : nums) {
            add(num);
        }
    }

    public int add(int val) {
        minHeap.offer(val);

        if (minHeap.size() > k) {
            minHeap.poll();
        }

        return minHeap.peek();
    }
}
```

---

### Problem 19: Reorganize String (No Adjacent Same)
**Question:** Rearrange string so no two adjacent chars are same. Return "" if impossible.
**Intuition:** **Greedy with Max Heap!** Always pick most frequent char (not same as previous). If can't pick different, impossible!
**Java:**

```java
String reorganizeString(String s) {
    Map<Character, Integer> freqMap = new HashMap<>();
    for (char c : s.toCharArray()) {
        freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
    }

    // Max heap by frequency
    PriorityQueue<Character> maxHeap = new PriorityQueue<>(
            (a, b) -> freqMap.get(b) - freqMap.get(a)
    );
    maxHeap.addAll(freqMap.keySet());

    StringBuilder result = new StringBuilder();
    Character prev = null;

    while (!maxHeap.isEmpty()) {
        char curr = maxHeap.poll();
        result.append(curr);

        if (prev != null) {
            maxHeap.offer(prev); // Add back previous
        }

        freqMap.put(curr, freqMap.get(curr) - 1);
        prev = (freqMap.get(curr) > 0) ? curr : null;
    }

    return (result.length() == s.length()) ? result.toString() : "";
}
```

---

### Problem 20: Task Scheduler
**Question:** Given tasks and cooldown period n, find minimum time to complete all tasks.
**Intuition:** **Greedy scheduling!** Always execute most frequent task. Use heap + cooling tracking!
**Java:**

```java
int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char task : tasks) {
        freq[task - 'A']++;
    }

    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int f : freq) {
        if (f > 0) maxHeap.offer(f);
    }

    int time = 0;

    while (!maxHeap.isEmpty()) {
        List<Integer> temp = new ArrayList<>();

        for (int i = 0; i <= n; i++) {
            if (!maxHeap.isEmpty()) {
                int f = maxHeap.poll();
                if (f > 1) temp.add(f - 1);
            }
            time++;

            if (maxHeap.isEmpty() && temp.isEmpty()) break;
        }

        maxHeap.addAll(temp);
    }

    return time;
}
```

---

# Summary: Heap Patterns

## Pattern Recognition Guide

### ðŸŽ¯ **Pattern 1: K-Problems**
**Use Min/Max Heap of size K**
- Kth Largest -> Min Heap
- Kth Smallest -> Max Heap
- Top K -> Opposite heap
- Keywords: "K largest", "K smallest", "Top K"

### ðŸŽ¯ **Pattern 2: Frequency Problems**
**HashMap + Heap**
1. Count frequencies
2. Heap on frequencies
- Top K frequent
- Sort by frequency
- Keywords: "frequent", "count", "occurrences"

### ðŸŽ¯ **Pattern 3: Two Heaps**
**Max Heap + Min Heap**
- Median problems
- Balance smaller/larger halves
- Keywords: "median", "balance", "running"

### ðŸŽ¯ **Pattern 4: Merge K**
**Min Heap for K-way merge**
- Merge K sorted arrays/lists
- Track which array/list
- Keywords: "merge", "K sorted", "combine"

### ðŸŽ¯ **Pattern 5: Greedy Scheduling**
**Heap for optimal choice**
- Reorganize string
- Task scheduling
- Meeting rooms
- Keywords: "arrange", "schedule", "optimal"

---

## Comparison: Heap vs Other Structures

| Requirement | Heap | Sorted Array | BST |
|-------------|------|-------------|-----|
| **Find min/max** | O(1) | O(1) | O(log n) |
| **Insert** | O(log n) | O(n) | O(log n) avg |
| **Delete min/max** | O(log n) | O(n) | O(log n) avg |
| **Find kth** | O(k log n) | O(1) | O(k) |
| **Space** | O(n) | O(n) | O(n) |
| **Build** | O(n) | O(n log n) | O(n log n) |

**When Heap Wins:**
- œ... Repeated min/max extractions
- œ... Priority-based processing
- œ... K-problems with K << n
- œ... Streaming data

---

## Common Mistakes

Œ **Wrong Heap Type:**
- Kth largest -> Use MIN heap!
- Kth smallest -> Use MAX heap!

Œ **Comparator Errors:**

```java
// WRONG for max heap
new PriorityQueue<>((a, b) -> a - b);

// CORRECT for max heap
new PriorityQueue<>((a, b) -> b - a);
// Or
new PriorityQueue<>(Collections.reverseOrder());
```

Œ **Size Management:**
- Forgetting to check `heap.size() > k`
- Not maintaining heap size

Œ **Tie Breaking:**
- Forgetting secondary sort criteria
- Example: frequency same -> lexicographic order

---

## Time Complexities

| Operation | Time |
|-----------|------|
| Insert | O(log n) |
| Delete min/max | O(log n) |
| Peek | O(1) |
| Build heap | O(n)  |
| Heapify | O(log n) |
| Search | O(n) |

---

## Pro Tips

1. **K problems:** Always maintain heap of size K!
2. **Frequency:** HashMap first, then Heap
3. **Median:** Two heaps trick (smaller + larger)
4. **Merge K:** Heap with tracking indices
5. **Custom objects:** Define comparator carefully