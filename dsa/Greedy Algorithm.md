
## L1: Assign Cookies
**Question:** Assign cookies to children to maximize number of content children. Each child has greed factor g[i], each cookie has size s[j].
**Intuition:** Greedy approach - satisfy children with smallest greed using smallest sufficient cookies. Don't waste large cookies on children with small greed.
**Logic:** Sort both arrays. Use two pointers - assign smallest cookie that satisfies current child.

**Java:**

```java
int findContentChildren(int[] g, int[] s) {
    Arrays.sort(g); // greed factors
    Arrays.sort(s); // cookie sizes

    int child = 0, cookie = 0;

    while (child < g.length && cookie < s.length) {
        if (s[cookie] >= g[child]) {
            child++; // child is satisfied
        }
        cookie++; // try next cookie
    }

    return child;
}
// Time: O(nlogn + mlogm), Space: O(1)
```

---

## L2: Lemonade Change
**Question:** Each customer pays with $5, $10, or $20. Lemonade costs $5. Return true if you can provide correct change to every customer.
**Intuition:** Greedy - prioritize using larger bills for change. Keep count of $5 and $10 bills.
**Logic:** For $10, give one $5. For $20, prefer giving one $10 + one $5 over three $5s.

**Java:**

```java
boolean lemonadeChange(int[] bills) {
    int five = 0, ten = 0;

    for (int bill : bills) {
        if (bill == 5) {
            five++;
        } else if (bill == 10) {
            if (five == 0) return false;
            five--;
            ten++;
        } else { // bill == 20
            // Prefer giving 1 ten + 1 five over 3 fives
            if (ten > 0 && five > 0) {
                ten--;
                five--;
            } else if (five >= 3) {
                five -= 3;
            } else {
                return false;
            }
        }
    }

    return true;
}
// Time: O(n), Space: O(1)
```

---

## L3: Shortest Job First (SJF)
**Question:** Find average waiting time when jobs are scheduled using Shortest Job First.
**Intuition:** Greedy - execute shortest jobs first to minimize total waiting time.
**Logic:** Sort by burst time. Calculate cumulative waiting time.

**Java:**

```java
double shortestJobFirst(int[] bt) {
    Arrays.sort(bt);

    int totalWaitTime = 0;
    int currentTime = 0;

    for (int i = 0; i < bt.length; i++) {
        totalWaitTime += currentTime;
        currentTime += bt[i];
    }

    return (double) totalWaitTime / bt.length;
}
// Time: O(nlogn), Space: O(1)
```

---

## L4: Jump Game I
**Question:** Check if you can reach the last index. Each element represents max jump length from that position.
**Intuition:** Track maximum reachable index at each step. If current index > max reachable, return false.
**Logic:** Maintain maxReach, update it as you traverse.

**Java:**

```java
boolean canJump(int[] nums) {
    int maxReach = 0;

    for (int i = 0; i < nums.length; i++) {
        if (i > maxReach) return false;
        maxReach = Math.max(maxReach, i + nums[i]);

        if (maxReach >= nums.length - 1) return true;
    }

    return true;
}
// Time: O(n), Space: O(1)
```

---

## L5: Jump Game II
**Question:** Find minimum number of jumps to reach last index.
**Intuition:** BFS-like approach. Track farthest reach in current jump. When current range exhausted, increment jumps.
**Logic:** Use two pointers - current range end and farthest reach.

**Java:**

```java
int jump(int[] nums) {
    if (nums.length <= 1) return 0;

    int jumps = 0;
    int currentEnd = 0;
    int farthest = 0;

    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);

        // If reached end of current jump range
        if (i == currentEnd) {
            jumps++;
            currentEnd = farthest;

            // Early termination
            if (currentEnd >= nums.length - 1) break;
        }
    }

    return jumps;
}
// Time: O(n), Space: O(1)
```

---

## L6: Job Sequencing Problem
**Question:** Schedule jobs to maximize profit. Each job has deadline and profit. Each job takes 1 unit time.
**Intuition:** Greedy - sort by profit (descending). Schedule each job as late as possible before deadline.
**Logic:** Sort by profit. For each job, find latest available slot before deadline.

**Java:**

```java
class Job {
 int id, deadline, profit;
 Job(int i, int d, int p) {
 id = i; deadline = d; profit = p;
 }
}

int[] jobScheduling(Job[] jobs, int n) {
    // Sort by profit (descending)
    Arrays.sort(jobs, (a, b) -> b.profit - a.profit);

    // Find max deadline
    int maxDeadline = 0;
    for (Job job : jobs) {
        maxDeadline = Math.max(maxDeadline, job.deadline);
    }

    // Slot array to track which day is occupied
    int[] slot = new int[maxDeadline + 1];
    Arrays.fill(slot, -1);

    int countJobs = 0, maxProfit = 0;

    for (Job job : jobs) {
        // Find latest available slot before deadline
        for (int j = job.deadline; j > 0; j--) {
            if (slot[j] == -1) {
                slot[j] = job.id;
                countJobs++;
                maxProfit += job.profit;
                break;
            }
        }
    }

    return new int[]{countJobs, maxProfit};
}
// Time: O(n log n + n * maxDeadline), Space: O(maxDeadline)

// Optimized with Disjoint Set Union (DSU)
int[] jobSchedulingOptimized(Job[] jobs, int n) {
    Arrays.sort(jobs, (a, b) -> b.profit - a.profit);

    int maxDeadline = 0;
    for (Job job : jobs) {
        maxDeadline = Math.max(maxDeadline, job.deadline);
    }

    int[] parent = new int[maxDeadline + 1];
    for (int i = 0; i <= maxDeadline; i++) parent[i] = i;

    int countJobs = 0, maxProfit = 0;

    for (Job job : jobs) {
        int availableSlot = find(parent, job.deadline);

        if (availableSlot > 0) {
            parent[availableSlot] = find(parent, availableSlot - 1);
            countJobs++;
            maxProfit += job.profit;
        }
    }

    return new int[]{countJobs, maxProfit};
}

int find(int[] parent, int x) {
    if (parent[x] != x) {
        parent[x] = find(parent, parent[x]);
    }
    return parent[x];
}
// Time: O(n log n + n * alpha(n)), Space: O(maxDeadline)
```

---

## L7: N Meetings in One Room
**Question:** Find maximum number of meetings that can be performed in one meeting room.
**Intuition:** Greedy - always choose meeting that ends earliest. This leaves maximum time for remaining meetings.
**Logic:** Sort by end time. Pick meeting if start time >= last end time.

**Java:**

```java
class Meeting {
 int start, end, pos;
 Meeting(int s, int e, int p) {
 start = s; end = e; pos = p;
 }
}

int maxMeetings(int[] start, int[] end, int n) {
    Meeting[] meetings = new Meeting[n];
    for (int i = 0; i < n; i++) {
        meetings[i] = new Meeting(start[i], end[i], i + 1);
    }

    // Sort by end time
    Arrays.sort(meetings, (a, b) -> a.end - b.end);

    int count = 1;
    int lastEndTime = meetings[0].end;

    for (int i = 1; i < n; i++) {
        if (meetings[i].start > lastEndTime) {
            count++;
            lastEndTime = meetings[i].end;
        }
    }

    return count;
}
// Time: O(n log n), Space: O(n)
```

---

## L8: Minimum Number of Platforms
**Question:** Find minimum number of platforms required for railway station.
**Intuition:** Treat arrivals and departures as events. When train arrives, need platform. When departs, platform freed.
**Logic:** Sort arrivals and departures separately. Use two pointers to track current requirement.

**Java:**

```java
int findPlatform(int[] arr, int[] dep, int n) {
    Arrays.sort(arr);
    Arrays.sort(dep);

    int platformsNeeded = 1;
    int maxPlatforms = 1;
    int i = 1, j = 0;

    while (i < n && j < n) {
        if (arr[i] <= dep[j]) {
            platformsNeeded++;
            i++;
        } else {
            platformsNeeded--;
            j++;
        }
        maxPlatforms = Math.max(maxPlatforms, platformsNeeded);
    }

    return maxPlatforms;
}
// Time: O(n log n), Space: O(1)
```

---

## L9: Fractional Knapsack
**Question:** Maximize value in knapsack. Can take fractions of items.
**Intuition:** Greedy - pick items with highest value-to-weight ratio first.
**Logic:** Sort by value/weight ratio (descending). Take items greedily.

**Java:**

```java
class Item {
 int value, weight;
 Item(int v, int w) { value = v; weight = w; }
}

double fractionalKnapsack(int W, Item[] items, int n) {
    // Sort by value/weight ratio (descending)
    Arrays.sort(items, (a, b) ->
            Double.compare((double) b.value / b.weight, (double) a.value / a.weight));

    double totalValue = 0;
    int currentWeight = 0;

    for (Item item : items) {
        if (currentWeight + item.weight <= W) {
            // Take entire item
            currentWeight += item.weight;
            totalValue += item.value;
        } else {
            // Take fraction
            int remaining = W - currentWeight;
            totalValue += item.value * ((double) remaining / item.weight);
            break;
        }
    }

    return totalValue;
}
// Time: O(n log n), Space: O(1)
```

---

## L10: Minimum Coins (Greedy - works for specific coin systems)
**Question:** Find minimum coins needed to make amount.
**Intuition:** For canonical coin systems (like 1,5,10,25), greedy works. Always pick largest coin possible.
**Logic:** Sort coins descending. Pick maximum of each coin.

**Java:**

```java
int minCoins(int[] coins, int amount) {
    Arrays.sort(coins);

    int count = 0;

    // Traverse from largest to smallest
    for (int i = coins.length - 1; i >= 0; i--) {
        while (amount >= coins[i]) {
            amount -= coins[i];
            count++;
        }
    }

    return amount == 0 ? count : -1;
}
// NOTE: Greedy only works for certain coin systems
// For general case, use DP
```

---

## L11: Candy Distribution
**Question:** Give candies to children. Each child must get at least 1. Children with higher rating get more than neighbors.
**Intuition:** Two passes - left to right ensures right neighbor constraint, right to left ensures left neighbor.
**Logic:** Initialize all with 1. Left pass for right neighbors. Right pass for left neighbors.

**Java:**

```java
int candy(int[] ratings) {
    int n = ratings.length;
    int[] candies = new int[n];
    Arrays.fill(candies, 1);

    // Left to right: if rating[i] > rating[i-1], give more
    for (int i = 1; i < n; i++) {
        if (ratings[i] > ratings[i - 1]) {
            candies[i] = candies[i - 1] + 1;
        }
    }

    // Right to left: if rating[i] > rating[i+1], ensure more
    for (int i = n - 2; i >= 0; i--) {
        if (ratings[i] > ratings[i + 1]) {
            candies[i] = Math.max(candies[i], candies[i + 1] + 1);
        }
    }

    int total = 0;
    for (int candy : candies) {
        total += candy;
    }

    return total;
}
// Time: O(n), Space: O(n)

// Space optimized using slope approach
int candyOptimized(int[] ratings) {
    int n = ratings.length;
    if (n <= 1) return n;

    int candies = 1;
    int up = 0, down = 0, peak = 0;

    for (int i = 1; i < n; i++) {
        if (ratings[i] > ratings[i - 1]) {
            up++;
            down = 0;
            peak = up;
            candies += up + 1;
        } else if (ratings[i] == ratings[i - 1]) {
            up = 0;
            down = 0;
            peak = 0;
            candies += 1;
        } else {
            up = 0;
            down++;
            candies += down + (down > peak ? 1 : 0);
        }
    }

    return candies;
}
```

---

## L12: Insert Intervals
**Question:** Insert new interval into sorted non-overlapping intervals. Merge if necessary.
**Intuition:** Three parts - intervals before new, merge overlapping with new, intervals after.
**Logic:** Add all before, merge overlapping, add all after.

**Java:**

```java
int[][] insert(int[][] intervals, int[] newInterval) {
    List<int[]> result = new ArrayList<>();
    int i = 0;
    int n = intervals.length;

    // Add all intervals before newInterval
    while (i < n && intervals[i][1] < newInterval[0]) {
        result.add(intervals[i]);
        i++;
    }

    // Merge overlapping intervals
    while (i < n && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
        newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
        i++;
    }
    result.add(newInterval);

    // Add remaining intervals
    while (i < n) {
        result.add(intervals[i]);
        i++;
    }

    return result.toArray(new int[result.size()][]);
}
// Time: O(n), Space: O(n)
```

---

## L13: Non-overlapping Intervals
**Question:** Find minimum intervals to remove to make rest non-overlapping.
**Intuition:** Similar to meeting rooms. Keep intervals that end earliest. Remove others.
**Logic:** Sort by end time. Count non-overlapping intervals. Answer = total - count.

**Java:**

```java
int eraseOverlapIntervals(int[][] intervals) {
    if (intervals.length == 0) return 0;

    // Sort by end time
    Arrays.sort(intervals, (a, b) -> a[1] - b[1]);

    int count = 1; // count of non-overlapping intervals
    int lastEnd = intervals[0][1];

    for (int i = 1; i < intervals.length; i++) {
        if (intervals[i][0] >= lastEnd) {
            count++;
            lastEnd = intervals[i][1];
        }
    }

    return intervals.length - count;
}
// Time: O(n log n), Space: O(1)
```

---

## L14: Merge Intervals
**Question:** Merge all overlapping intervals.
**Intuition:** Sort by start time. Merge current with last if overlapping.
**Logic:** Sort, then iterate and merge.

**Java:**

```java
int[][] merge(int[][] intervals) {
    if (intervals.length <= 1) return intervals;

    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

    List<int[]> merged = new ArrayList<>();
    int[] currentInterval = intervals[0];
    merged.add(currentInterval);

    for (int[] interval : intervals) {
        if (interval[0] <= currentInterval[1]) {
            // Overlapping - merge
            currentInterval[1] = Math.max(currentInterval[1], interval[1]);
        } else {
            // Non-overlapping - add new interval
            currentInterval = interval;
            merged.add(currentInterval);
        }
    }

    return merged.toArray(new int[merged.size()][]);
}
// Time: O(n log n), Space: O(n)
```

---

## L15: Page Faults (LRU)
**Question:** Count page faults with LRU replacement.
**Intuition:** Keep track of recently used pages. Replace least recently used when capacity full.
**Logic:** Use queue/deque to maintain LRU order.

**Java:**

```java
int pageFaults(int[] pages, int capacity) {
    LinkedHashSet<Integer> set = new LinkedHashSet<>();
    int faults = 0;

    for (int page : pages) {
        if (!set.contains(page)) {
            faults++;

            if (set.size() == capacity) {
                // Remove least recently used (first element)
                int lru = set.iterator().next();
                set.remove(lru);
            }

            set.add(page);
        } else {
            // Update order - remove and re-add
            set.remove(page);
            set.add(page);
        }
    }

    return faults;
}
```

---

## L16: Valid Parenthesis String
**Question:** Check if string with *, (, ) is valid. * can be (, ), or empty.
**Intuition:** Track range of possible open brackets count. Min and max possible opens.
**Logic:** Maintain minOpen and maxOpen. Update based on character.

**Java:**

```java
boolean checkValidString(String s) {
    int minOpen = 0, maxOpen = 0;

    for (char c : s.toCharArray()) {
        if (c == '(') {
            minOpen++;
            maxOpen++;
        } else if (c == ')') {
            minOpen--;
            maxOpen--;
        } else { // c == '*'
            minOpen--; // treat as ')'
            maxOpen++; // treat as '('
        }

        if (maxOpen < 0) return false;
        if (minOpen < 0) minOpen = 0; // reset negative
    }

    return minOpen == 0;
}
// Time: O(n), Space: O(1)
```

---

## L17: Boats to Save People
**Question:** Each boat carries at most 2 people and has weight limit. Find minimum boats needed.
**Intuition:** Greedy two-pointer. Pair heaviest with lightest if possible.
**Logic:** Sort weights. Use two pointers from both ends.

**Java:**

```java
int numRescueBoats(int[] people, int limit) {
    Arrays.sort(people);

    int left = 0, right = people.length - 1;
    int boats = 0;

    while (left <= right) {
        if (people[left] + people[right] <= limit) {
            left++; // pair lightest with heaviest
        }
        right--; // heaviest always goes
        boats++;
    }

    return boats;
}
// Time: O(n log n), Space: O(1)
```

---

## L18: Gas Station (Circular Tour)
**Question:** Find starting gas station to complete circular tour.
**Intuition:** If total gas >= total cost, solution exists. Track deficit, start from where current journey fails.
**Logic:** One pass tracking current tank and deficit.

**Java:**

```java
int canCompleteCircuit(int[] gas, int[] cost) {
    int totalGas = 0, totalCost = 0;
    int currentGas = 0, startStation = 0;

    for (int i = 0; i < gas.length; i++) {
        totalGas += gas[i];
        totalCost += cost[i];

        currentGas += gas[i] - cost[i];

        if (currentGas < 0) {
            // Can't start from current startStation
            startStation = i + 1;
            currentGas = 0;
        }
    }

    return totalGas >= totalCost ? startStation : -1;
}
// Time: O(n), Space: O(1)
```

---

## L19: Minimum Cost to Cut a Stick
**Question:** Cut stick at given positions with minimum cost. Cost = length of stick being cut.
**Intuition:** Greedy doesn't work optimally here - this is actually a DP problem. But can use greedy heuristic.
**Logic:** For pure greedy (suboptimal), sort cuts and process.

**Java:**

```java
// Note: This problem actually requires DP for optimal solution
// Greedy approach is suboptimal
int minCostGreedy(int n, int[] cuts) {
    Arrays.sort(cuts);

    int cost = 0;
    int left = 0;

    for (int cut : cuts) {
        int length = n - left;
        cost += length;
        left = cut;
    }

    return cost;
}
// For optimal solution, use Interval DP
```

---

## L20: Task Scheduler
**Question:** Schedule tasks with cooldown period. Same task needs n intervals between executions.
**Intuition:** Most frequent task determines minimum intervals. Fill gaps with other tasks or idle.
**Logic:** Count frequencies. Calculate slots needed for most frequent task.

**Java:**

```java
int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    int maxFreq = 0;

    for (char task : tasks) {
        freq[task - 'A']++;
        maxFreq = Math.max(maxFreq, freq[task - 'A']);
    }

    // Count tasks with max frequency
    int maxCount = 0;
    for (int f : freq) {
        if (f == maxFreq) maxCount++;
    }

    // Calculate minimum intervals
    int partCount = maxFreq - 1;
    int partLength = n - (maxCount - 1);
    int emptySlots = partCount * partLength;
    int availableTasks = tasks.length - maxFreq * maxCount;
    int idles = Math.max(0, emptySlots - availableTasks);

    return tasks.length + idles;
}
// Time: O(n), Space: O(1)
```

---

## L21: Partition Labels
**Question:** Partition string so that each letter appears in at most one part. Maximize parts.
**Intuition:** Greedy - track last occurrence of each character. Extend current partition until reaching last occurrence of all characters in partition.
**Logic:** Store last index of each char. Extend partition end as needed.

**Java:**

```java
List<Integer> partitionLabels(String s) {
    int[] lastIndex = new int[26];

    // Store last occurrence of each character
    for (int i = 0; i < s.length(); i++) {
        lastIndex[s.charAt(i) - 'a'] = i;
    }

    List<Integer> result = new ArrayList<>();
    int start = 0, end = 0;

    for (int i = 0; i < s.length(); i++) {
        end = Math.max(end, lastIndex[s.charAt(i) - 'a']);

        if (i == end) {
            // Found a partition
            result.add(end - start + 1);
            start = i + 1;
        }
    }

    return result;
}
// Time: O(n), Space: O(1)
```

---

## L22: Minimum Cost to Hire K Workers
**Question:** Hire K workers with minimum cost given quality and wage expectations.
**Intuition:** Fix wage-to-quality ratio. Use max heap to maintain K workers with smallest qualities.
**Logic:** Sort by ratio. Use heap to track K smallest qualities.

**Java:**

```java
double mincostToHireWorkers(int[] quality, int[] wage, int K) {
    int n = quality.length;
    Worker[] workers = new Worker[n];

    for (int i = 0; i < n; i++) {
        workers[i] = new Worker(quality[i], wage[i]);
    }

    // Sort by wage/quality ratio
    Arrays.sort(workers, (a, b) ->
            Double.compare(a.ratio(), b.ratio()));

    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    int qualitySum = 0;
    double minCost = Double.MAX_VALUE;

    for (Worker worker : workers) {
        maxHeap.offer(worker.quality);
        qualitySum += worker.quality;

        if (maxHeap.size() > K) {
            qualitySum -= maxHeap.poll();
        }

        if (maxHeap.size() == K) {
            minCost = Math.min(minCost, qualitySum * worker.ratio());
        }
    }

    return minCost;
}

class Worker {
    int quality, wage;

    Worker(int q, int w) {
        quality = q;
        wage = w;
    }

    double ratio() {
        return (double) wage / quality;
    }
}
// Time: O(n log n + n log k), Space: O(n + k)
```

---