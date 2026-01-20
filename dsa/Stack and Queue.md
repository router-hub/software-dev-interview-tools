## L1: Implement Stack using Array

**Question:** Implement stack with push, pop, top, size operations using array.
**Intuition:** Stack is LIFO (Last In First Out). Use array with top pointer tracking last element position.
**Logic:** Maintain top index, increment for push, decrement for pop.

**Java:**

```java
class Stack {
 int[] arr;
 int top;
 int capacity;

 Stack(int size) {
 arr = new int[size];
 capacity = size;
 top = -1;
 }

 void push(int x) {
 if (top == capacity - 1) {
 System.out.println("Stack Overflow");
 return;
 }
 arr[++top] = x;
 }

 int pop() {
 if (top == -1) {
 System.out.println("Stack Underflow");
 return -1;
 }
 return arr[top--];
 }

 int top() {
 if (top == -1) return -1;
 return arr[top];
 }

 int size() {
 return top + 1;
 }

 boolean isEmpty() {
 return top == -1;
 }
}
// Time: O(1) for all operations, Space: O(capacity)
```

---

## L2: Implement Queue using Array

**Question:** Implement queue with enqueue, dequeue, front, size operations.
**Intuition:** Queue is FIFO (First In First Out). Use array with front and rear pointers.
**Logic:** Use circular array to avoid shifting elements.

**Java:**

```java
class Queue {
 int[] arr;
 int front, rear, size, capacity;

 Queue(int capacity) {
 this.capacity = capacity;
 arr = new int[capacity];
 front = 0;
 rear = -1;
 size = 0;
 }

 void enqueue(int x) {
 if (size == capacity) {
 System.out.println("Queue is full");
 return;
 }
 rear = (rear + 1) % capacity;
 arr[rear] = x;
 size++;
 }

 int dequeue() {
 if (size == 0) {
 System.out.println("Queue is empty");
 return -1;
 }
 int val = arr[front];
 front = (front + 1) % capacity;
 size--;
 return val;
 }

 int front() {
 if (size == 0) return -1;
 return arr[front];
 }

 int size() {
 return size;
 }
}
// Time: O(1) for all operations
```

---

## L3: Implement Stack using Linked List

**Question:** Implement stack using linked list (dynamic size).
**Intuition:** Each push creates new node at head. Pop removes head. No size limit.
**Logic:** Maintain top pointer, add/remove at head for O(1) operations.

**Java:**

```java
class Node {
 int data;
 Node next;
 Node(int val) {
 data = val;
 next = null;
 }
}

class Stack {
 Node top;
 int size;

 Stack() {
 top = null;
 size = 0;
 }

 void push(int x) {
 Node newNode = new Node(x);
 newNode.next = top;
 top = newNode;
 size++;
 }

 int pop() {
 if (top == null) {
 System.out.println("Stack is empty");
 return -1;
 }
 int val = top.data;
 top = top.next;
 size--;
 return val;
 }

 int top() {
 if (top == null) return -1;
 return top.data;
 }

 int size() {
 return size;
 }
}
```

---

## L4: Implement Queue using Linked List

**Question:** Implement queue using linked list.
**Intuition:** Maintain front and rear pointers. Enqueue at rear, dequeue from front.
**Logic:** Add at tail, remove from head for FIFO behavior.

**Java:**

```java
class Node {
 int data;
 Node next;
 Node(int val) { data = val; next = null; }
}

class Queue {
 Node front, rear;
 int size;

 Queue() {
 front = rear = null;
 size = 0;
 }

 void enqueue(int x) {
 Node newNode = new Node(x);
 if (rear == null) {
 front = rear = newNode;
 } else {
 rear.next = newNode;
 rear = newNode;
 }
 size++;
 }

 int dequeue() {
 if (front == null) return -1;
 int val = front.data;
 front = front.next;
 if (front == null) rear = null;
 size--;
 return val;
 }

 int front() {
 return front == null ? -1 : front.data;
 }
}
```

---

## L5: Implement Stack using Queue

**Question:** Implement stack using single queue.
**Intuition:** When pushing element, rotate queue so new element comes to front. This makes pop/top O(1).
**Logic:** After push, rotate queue (size-1) times to bring new element to front.

**Java:**

```java
class Stack {
 Queue<Integer> q = new LinkedList<>();

 void push(int x) {
 q.add(x);
 int size = q.size();
 // Rotate queue to bring x to front
 for (int i = 0; i < size - 1; i++) {
 q.add(q.poll());
 }
 }

 int pop() {
 if (q.isEmpty()) return -1;
 return q.poll();
 }

 int top() {
 if (q.isEmpty()) return -1;
 return q.peek();
 }

 int size() {
 return q.size();
 }
}
// Time: push O(n), pop/top O(1)
```

---

## L6: Implement Queue using Stack

**Question:** Implement queue using two stacks.
**Intuition:** Use two stacks - input and output. Push to input. When popping, transfer to output if empty.
**Logic:** Amortized O(1) - each element moved once from input to output.

**Java:**

```java
class Queue {
 Stack<Integer> input = new Stack<>();
 Stack<Integer> output = new Stack<>();

 void push(int x) {
 input.push(x);
 }

 int pop() {
 if (output.isEmpty()) {
 while (!input.isEmpty()) {
 output.push(input.pop());
 }
 }
 return output.isEmpty() ? -1 : output.pop();
 }

 int front() {
 if (output.isEmpty()) {
 while (!input.isEmpty()) {
 output.push(input.pop());
 }
 }
 return output.isEmpty() ? -1 : output.peek();
 }

 int size() {
 return input.size() + output.size();
 }
}
// Time: Amortized O(1) for all operations
```

---

## L7: Check for Balanced Parentheses

**Question:** Check if string has balanced parentheses (), {}, [].
**Intuition:** Use stack. Push opening brackets. For closing, check if top matches.
**Logic:** Stack to match pairs. Empty stack at end means balanced.

**Java:**

```java
boolean isValid(String s) {
 Stack<Character> stack = new Stack<>();

 for (char c : s.toCharArray()) {
 if (c == '(' || c == '{' || c == '[') {
 stack.push(c);
 } else {
 if (stack.isEmpty()) return false;
 char top = stack.pop();
 if ((c == ')' && top != '(') ||
 (c == '}' && top != '{') ||
 (c == ']' && top != '[')) {
 return false;
 }
 }
 }
 return stack.isEmpty();
}
// Time: O(n), Space: O(n)
```

---

## L8: Implement Min Stack

**Question:** Design stack supporting push, pop, top, and getMin in O(1).
**Intuition:** Store pairs (value, min_so_far) OR use formula to encode min without extra space.
**Logic:** Maintain minimum along with each element.

**Java:**

```java
// Approach 1: Using Pairs
class MinStack {
 Stack<int[]> stack = new Stack<>(); // {value, min}

 void push(int val) {
 if (stack.isEmpty()) {
 stack.push(new int[]{val, val});
 } else {
 int currentMin = Math.min(val, stack.peek()[1]);
 stack.push(new int[]{val, currentMin});
 }
 }

 void pop() {
 if (!stack.isEmpty()) stack.pop();
 }

 int top() {
 return stack.isEmpty() ? -1 : stack.peek()[0];
 }

 int getMin() {
 return stack.isEmpty() ? -1 : stack.peek()[1];
 }
}

// Approach 2: O(1) Space (using modified value)
class MinStackOptimal {
 Stack<Long> stack = new Stack<>();
 long min;

 void push(int val) {
 if (stack.isEmpty()) {
 stack.push((long)val);
 min = val;
 } else {
 if (val < min) {
 // Push modified value
 stack.push(2L * val - min);
 min = val;
 } else {
 stack.push((long)val);
 }
 }
 }

 void pop() {
 if (stack.isEmpty()) return;
 long top = stack.pop();
 if (top < min) {
 // Restore previous min
 min = 2 * min - top;
 }
 }

 int top() {
 if (stack.isEmpty()) return -1;
 long top = stack.peek();
 return (int)(top < min ? min : top);
 }


 int getMin() {
 return (int)min;
 }
}
```

## 8B: Implement Max Stack Variation

**Question:** Design stack supporting push, pop, top, and getMax in O(1) time (variation of MinStack).
**Intuition:** Mirror MinStack but for maximum. Encode previous max when new max is pushed, using formula to store implicitly. Encoded sentinels are > current max for detection.
**Logic:** Track `maxVal`. Push encoded `2 * val - maxVal` if val > maxVal. Restore on pop if popped > maxVal. Top returns maxVal if peeking encoded value.

**Java:**

```java
class MaxStack {
 Stack<Long> st;
 long maxVal; // Tracks current maximum

 public MaxStack() {
 st = new Stack<>();
 }

 public void push(int val) {
 if (st.size() == 0) {
 maxVal = val;
 st.push((long) val);
 } else {
 if (val > maxVal) {
 // New maximum! Encode previous max:
 // Push 2*val - maxVal (> new maxVal) as sentinel
 // Recovery: old_max = 2*new_max - encoded
 st.push(2L * val - maxVal);
 maxVal = val;
 } else {
 st.push((long) val); // Normal push
 }
 }
 }

 public void pop() {
 if (st.size() > 0) {
 long top = st.pop();
 if (top > maxVal) {
 // Encoded: Restore previous max
 maxVal = 2 * maxVal - top;
 }
 }
 }

 public int top() {
 if (st.size() > 0) {
 long top = st.peek();
 // If encoded (top > maxVal), return current maxVal
 return (int) (top > maxVal ? maxVal : top);
 }
 return -1;
 }

 public int getMax() {
 if (st.size() == 0) return -1;
 return (int) maxVal;
 }
}
// Time: O(1) all ops, Space: O(n) stack + O(1) extra
```

**Intuition Notes:**

- **Symmetry**: Invert MinStack inequalities (`<` to `>`, `min` to `maxVal`). Encoding flips old max around new for reversibility.
- **Example**: push(3), push(1), push(5): stack=[3,1,7] (7=2*5-3), max=5; top()=5 (7>5); pop() restores max=3.
- **Edges**: Negatives, nested max, empty. Long for overflow. Useful for max-tracking scenarios like priority queues.

---

## L9: Prefix, Infix, Postfix Conversions

**Background:**
Mathematical expressions can be written in different notations:

Infix: Operator between operands (human-readable, e.g., A + B). Requires precedence rules (e.g., * before +) and parentheses for grouping.

Prefix (Polish Notation): Operator before operands (e.g., + A B). No parentheses needed; evaluated right-to-left or using stack.

Postfix (Reverse Polish Notation): Operator after operands (e.g., A B +). No precedence/parentheses; stack-based evaluation is straightforward.

**Why Convert?** Conversions simplify evaluation (postfix/prefix avoid operator precedence parsing). Used in compilers, calculators, and expression trees. Questions typically ask to:

Convert infix to postfix/prefix using stack for precedence.

Convert between prefix/postfix using stack-based algorithms (NOT simple reversal!).

Evaluate prefix/postfix expressions using stack (push operands, apply operators).

**Examples:**

Infix: (A + B) * C → Postfix: AB+C*, Prefix: *+ABC

Postfix: AB+C* → Prefix: *+ABC (requires stack, not reversal)

Evaluation (Postfix: 23+4*): Push 2,3; pop/add=5; push 5,4; pop/mul=20.

**Question:** Implement conversions: infix to prefix/postfix, prefix to postfix, postfix to prefix, and evaluate prefix/postfix.
**Intuition:** Stack handles precedence for infix conversions (Shunting-yard). Prefix/postfix conversions require stack processing, NOT simple string reversal. Evaluation: Stack for operands/operators.
**Logic:** For infix → postfix: Scan left-right, push operators by precedence, pop higher/equal. For prefix ↔ postfix: Use stack to rebuild expression. Evaluation: Scan postfix left-right (pop two for op); for prefix, scan right-left.

**Java:**

```java
// Helper: Check if operator is right-associative
boolean isRightAssociative(char op) {
 return op == '^'; // Exponentiation is right-associative; others left
}

// Helper: Precedence function
int precedence(char c) {
 if (c == '^') return 3;
 if (c == '*' || c == '/') return 2;
 if (c == '+' || c == '-') return 1;
 return 0;
}

// Helper: Check if character is operator
boolean isOperator(char c) {
 return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
}

// 1. Infix to Postfix (Shunting-yard algorithm)
String infixToPostfix(String s) {
 StringBuilder result = new StringBuilder();
 Stack<Character> stack = new Stack<>();

 for (char c : s.toCharArray()) {
 if (Character.isLetterOrDigit(c)) {
 result.append(c); // Operand → directly to output
 } else if (c == '(') {
 stack.push(c); // Opening bracket → push to stack
 } else if (c == ')') {
 // Closing bracket → pop until matching '('
 while (!stack.isEmpty() && stack.peek() != '(') {
 result.append(stack.pop());
 }
 stack.pop(); // Remove '('
 } else { // Operator
 // Pop operators with higher precedence or equal precedence (if left-associative)
 while (!stack.isEmpty() && stack.peek() != '(' &&
 (precedence(stack.peek()) > precedence(c) ||
 (precedence(stack.peek()) == precedence(c) && !isRightAssociative(c)))) {
 result.append(stack.pop());
 }
 stack.push(c); // Push current operator
 }
 }

 // Pop remaining operators
 while (!stack.isEmpty()) {
 result.append(stack.pop());
 }
 return result.toString();
}

/*
Dry Run: Infix "(A+B)*C" → Postfix "AB+C*"

Step 1: '(' → Push '(' → Stack: ['('], Result: ""
Step 2: 'A' → Operand → Result: "A"
Step 3: '+' → Operator, stack top '(' → Push '+' → Stack: ['(', '+'], Result: "A"
Step 4: 'B' → Operand → Result: "AB"
Step 5: ')' → Pop until '(': pop '+' → Result: "AB+", pop '(' → Stack: [], Result: "AB+"
Step 6: '*' → Operator, empty stack → Push '*' → Stack: ['*'], Result: "AB+"
Step 7: 'C' → Operand → Result: "AB+C"
Step 8: End → Pop '*' → Result: "AB+C*"

Key insight: Higher precedence operators stay on stack longer, ensuring correct evaluation order.
*/

// 2. Infix to Prefix
String infixToPrefix(String s) {
 // Step 1: Reverse the infix expression
 String reversed = new StringBuilder(s).reverse().toString();

 // Step 2: Swap '(' and ')'
 char[] chars = reversed.toCharArray();
 for (int i = 0; i < chars.length; i++) {
 if (chars[i] == '(') chars[i] = ')';
 else if (chars[i] == ')') chars[i] = '(';
 }
 reversed = new String(chars);

 // Step 3: Get postfix of reversed expression
 String postfix = infixToPostfix(reversed);

 // Step 4: Reverse the postfix to get prefix
 return new StringBuilder(postfix).reverse().toString();
}

/*
Example: "(A+B)*C" → "*+ABC"
Step 1: Reverse → "C*)B+A("
Step 2: Swap parens → "C*(B+A)"
Step 3: Postfix → "CBA+*"
Step 4: Reverse → "*+ABC" ✓
*/

// 3. Prefix to Postfix (CORRECTED - Stack-based)
String prefixToPostfix(String prefix) {
 Stack<String> stack = new Stack<>();

 // Scan from RIGHT to LEFT
 for (int i = prefix.length() - 1; i >= 0; i--) {
 char c = prefix.charAt(i);

 if (Character.isLetterOrDigit(c)) {
 // Operand → push to stack
 stack.push(String.valueOf(c));
 } else if (isOperator(c)) {
 // Operator → pop two operands
 String op1 = stack.pop();
 String op2 = stack.pop();

 // Create postfix: op1 + op2 + operator
 String postfix = op1 + op2 + c;
 stack.push(postfix);
 }
 }

 return stack.pop();
}

/*
Dry Run: Prefix "*+ABC" → Postfix "AB+C*"

Scan RIGHT to LEFT: C → B → A → + → *

Step 1: 'C' → Push "C" → Stack: ["C"]
Step 2: 'B' → Push "B" → Stack: ["C", "B"]
Step 3: 'A' → Push "A" → Stack: ["C", "B", "A"]
Step 4: '+' → Pop "A", "B" → Create "AB+" → Push → Stack: ["C", "AB+"]
Step 5: '*' → Pop "AB+", "C" → Create "AB+C*" → Push → Stack: ["AB+C*"]
Result: "AB+C*" ✓

Why right-to-left? In prefix, operator comes BEFORE operands.
When scanning backwards, we encounter operands first, store them,
then when we hit operator, we build the postfix sub-expression.
*/

// 4. Postfix to Prefix (CORRECTED - Stack-based)
String postfixToPrefix(String postfix) {
 Stack<String> stack = new Stack<>();

 // Scan from LEFT to RIGHT
 for (char c : postfix.toCharArray()) {
 if (Character.isLetterOrDigit(c)) {
 // Operand → push to stack
 stack.push(String.valueOf(c));
 } else if (isOperator(c)) {
 // Operator → pop two operands
 String op2 = stack.pop(); // Second operand
 String op1 = stack.pop(); // First operand

 // Create prefix: operator + op1 + op2
 String prefix = c + op1 + op2;
 stack.push(prefix);
 }
 }

 return stack.pop();
}

/*
Dry Run: Postfix "AB+C*" → Prefix "*+ABC"

Scan LEFT to RIGHT: A → B → + → C → *

Step 1: 'A' → Push "A" → Stack: ["A"]
Step 2: 'B' → Push "B" → Stack: ["A", "B"]
Step 3: '+' → Pop "B", "A" → Create "+AB" → Push → Stack: ["+AB"]
Step 4: 'C' → Push "C" → Stack: ["+AB", "C"]
Step 5: '*' → Pop "C", "+AB" → Create "*+ABC" → Push → Stack: ["*+ABC"]
Result: "*+ABC" ✓

Key difference from prefix→postfix: Here we scan left-to-right (natural for postfix),
and build prefix by putting operator BEFORE operands.
*/

// 5. Evaluate Postfix Expression
int evaluatePostfix(String s) {
 Stack<Integer> stack = new Stack<>();

 for (char c : s.toCharArray()) {
 if (Character.isDigit(c)) {
 stack.push(c - '0'); // Convert char to int
 } else if (isOperator(c)) {
 int val2 = stack.pop(); // Second operand (right)
 int val1 = stack.pop(); // First operand (left)

 int result = 0;
 switch (c) {
 case '+': result = val1 + val2; break;
 case '-': result = val1 - val2; break;
 case '*': result = val1 * val2; break;
 case '/': result = val1 / val2; break;
 case '^': result = (int) Math.pow(val1, val2); break;
 }
 stack.push(result);
 }
 }

 return stack.pop();
}

/*
Example: "23+4*" → 20
Step 1: '2' → Push 2 → Stack: [2]
Step 2: '3' → Push 3 → Stack: [2, 3]
Step 3: '+' → Pop 3, 2 → 2+3=5 → Push 5 → Stack: [5]
Step 4: '4' → Push 4 → Stack: [5, 4]
Step 5: '*' → Pop 4, 5 → 5*4=20 → Push 20 → Stack: [20]
Result: 20
*/

// 6. Evaluate Prefix Expression
int evaluatePrefix(String prefix) {
 Stack<Integer> stack = new Stack<>();

 // Scan from RIGHT to LEFT
 for (int i = prefix.length() - 1; i >= 0; i--) {
 char c = prefix.charAt(i);

 if (Character.isDigit(c)) {
 stack.push(c - '0');
 } else if (isOperator(c)) {
 // For prefix: first pop is left operand, second is right
 int val1 = stack.pop();
 int val2 = stack.pop();

 int result = 0;
 switch (c) {
 case '+': result = val1 + val2; break;
 case '-': result = val1 - val2; break;
 case '*': result = val1 * val2; break;
 case '/': result = val1 / val2; break;
 case '^': result = (int) Math.pow(val1, val2); break;
 }
 stack.push(result);
 }
 }

 return stack.pop();
}

/*
Example: "*+234" → 20
Scan RIGHT to LEFT: 4 → 3 → 2 → + → *

Step 1: '4' → Push 4 → Stack: [4]
Step 2: '3' → Push 3 → Stack: [4, 3]
Step 3: '2' → Push 2 → Stack: [4, 3, 2]
Step 4: '+' → Pop 2, 3 → 2+3=5 → Push 5 → Stack: [4, 5]
Step 5: '*' → Pop 5, 4 → 5*4=20 → Push 20 → Stack: [20]
Result: 20
*/

// Time Complexity: O(n) for all operations
// Space Complexity: O(n) for stack storage
```

---

## L10: Next Greater Element (NGE)

**Question:** For each element, find next greater element to its right.
**Intuition:** Use monotonic decreasing stack. Traverse right to left. Stack stores elements in decreasing order.
**Logic:** Pop smaller elements, they found their NGE. Push current element.

**Example:**
Input: arr = [4,5,2,25,1]
Output: [5,25,25,-1,-1]
Explanation: For 4, next greater is 5 (at i=1); for 5, 25 (i=3); for 2, 25 (i=3); for 25, no greater (-1); for 1, no greater (-1).

**Java:**

```java
int[] nextGreaterElement(int[] arr) {
 int n = arr.length;
 int[] nge = new int[n];
 Stack<Integer> stack = new Stack<>();

 // Traverse from right to left
 for (int i = n - 1; i >= 0; i--) {
 // Pop smaller elements
 while (!stack.isEmpty() && stack.peek() <= arr[i]) {
 stack.pop();
 }

 // Top of stack is NGE
 nge[i] = stack.isEmpty() ? -1 : stack.peek();

 // Push current element
 stack.push(arr[i]);
 }

 return nge;
}
// Time: O(n), Space: O(n)
// Each element pushed and popped at most once
```

---

## L11: Next Greater Element II (Circular Array)

**Question:** Find NGE in circular array (last can find NGE in beginning).
**Intuition:** Traverse array twice (simulate circular). Use same monotonic stack approach.
**Logic:** Use index % n to handle circular nature.

**Example:**
Input: arr = [4,5,2,25,1] (circular)
Output: [5,25,25,-1,4]
Explanation: For 4, next greater is 5; for 5, 25; for 2, 25; for 25, For 25, no greater in any cycle,for 1 it is in first cycle, so NGE is 4 (wrap).

**Java:**

```java
int[] nextGreaterElements(int[] arr) {
 int n = arr.length;
 int[] nge = new int[n];
 Stack<Integer> stack = new Stack<>();

 // Traverse twice for circular
 for (int i = 2 * n - 1; i >= 0; i--) {
 int idx = i % n;

 while (!stack.isEmpty() && stack.peek() <= arr[idx]) {
 stack.pop();
 }

 if (i < n) {
 nge[idx] = stack.isEmpty() ? -1 : stack.peek();
 }

 stack.push(arr[idx]);
 }

 return nge;
}
```

---

## L12: Previous Smaller Element

**Question:** For each element, find previous smaller element.
**Intuition:** Similar to NGE but traverse left to right, maintain increasing stack.
**Logic:** Monotonic increasing stack, traverse left to right.

**Java:**

```java
int[] previousSmallerElement(int[] arr) {
 int n = arr.length;
 int[] pse = new int[n];
 Stack<Integer> stack = new Stack<>();

 for (int i = 0; i < n; i++) {
 while (!stack.isEmpty() && stack.peek() >= arr[i]) {
 stack.pop();
 }

 pse[i] = stack.isEmpty() ? -1 : stack.peek();
 stack.push(arr[i]);
 }

 return pse;
}
```

---

## L13: Trapping Rain Water

**Question:** Calculate trapped rainwater between bars.
**Intuition:** For each bar, the water it can trap is determined by the minimum of the maximum height to its left and right, minus its own height. If this value is positive, it contributes that much water. Sum over all bars.

**Example:**
Input: height = [0,1,0,2,1,0,1,3,2,1,2,1]
Output: 6
Explanation: Water trapped at indices 2 (1), 4 (1), 5 (2), 6 (1), 8 (1), 9 (1) = 6 units total.

**Approach 1: Brute Force**
**Intuition:** For each index i, find the max height to the left (0 to i-1) and to the right (i+1 to n-1). Then water[i] = max(0, min(leftMax, rightMax) - height[i]). Sum all water[i].
**Logic:** Nested loops to compute maxLeft and maxRight for each i.
**Time Complexity:** O(n^2), **Space Complexity:** O(1)

**Java:**

```java
int trap(int[] height) {
 int n = height.length;
 int water = 0;
 for (int i = 0; i < n; i++) {
 int leftMax = 0;
 for (int j = 0; j < i; j++) {
 leftMax = Math.max(leftMax, height[j]);
 }
 int rightMax = 0;
 for (int j = i + 1; j < n; j++) {
 rightMax = Math.max(rightMax, height[j]);
 }
 water += Math.max(0, Math.min(leftMax, rightMax) - height[i]);
 }
 return water;
}
```

**Approach 2: Dynamic Programming**
**Intuition:** Precompute leftMax and rightMax arrays in O(n) time to avoid recomputation.
**Logic:** leftMax[i] = max(leftMax[i-1], height[i]); rightMax[i] = max(rightMax[i+1], height[i]). Then compute water as in brute force.
**Time Complexity:** O(n), **Space Complexity:** O(n)

**Java:**

```java
int trap(int[] height) {
 int n = height.length;
 if (n == 0) return 0;
 int[] leftMax = new int[n];
 int[] rightMax = new int[n];

 leftMax[0] = height[0];
 for (int i = 1; i < n; i++) {
 leftMax[i] = Math.max(leftMax[i-1], height[i]);
 }

 rightMax[n-1] = height[n-1];
 for (int i = n-2; i >= 0; i--) {
 rightMax[i] = Math.max(rightMax[i+1], height[i]);
 }

 int water = 0;
 for (int i = 0; i < n; i++) {
 water += Math.max(0, Math.min(leftMax[i], rightMax[i]) - height[i]);
 }
 return water;
}
```

**Approach 3: Two Pointers (Optimal)**
**Intuition:** No need for arrays. Use two pointers starting from ends, always move the one with smaller height, updating the max for that side and adding water if possible.
**Logic:** The smaller side limits the water, so process the side with current smaller height.
**Time Complexity:** O(n), **Space Complexity:** O(1)

**Java:**

```java
int trap(int[] height) {
 int n = height.length;
 int left = 0, right = n - 1;
 int leftMax = 0, rightMax = 0;
 int water = 0;

 while (left < right) {
 if (height[left] < height[right]) {
 if (height[left] >= leftMax) {
 leftMax = height[left];
 } else {
 water += leftMax - height[left];
 }
 left++;
 } else {
 if (height[right] >= rightMax) {
 rightMax = height[right];
 } else {
 water += rightMax - height[right];
 }
 right--;
 }
 }

 return water;
}
```

**Approach 4: Stack (Another Optimal)**
**Intuition:** Use a monotonic increasing stack to track indices of bars. When a smaller bar is found, pop taller bars and calculate water trapped between them using the current bar as right boundary.
**Logic:** Stack stores indices in increasing height order. For each bar, while stack not empty and current < stack.top, pop and compute water for popped bar using min(current height, previous in stack) - popped height, width = current idx - prev idx -1.
**Time Complexity:** O(n), **Space Complexity:** O(n)

**Java:**

```java
int trap(int[] height) {
 int n = height.length;
 Stack<Integer> stack = new Stack<>();
 int water = 0;

 for (int i = 0; i < n; i++) {
 while (!stack.isEmpty() && height[stack.peek()] < height[i]) {
 int top = stack.pop();
 if (stack.isEmpty()) break;
 int distance = i - stack.peek() - 1;
 int boundedHeight = Math.min(height[stack.peek()], height[i]);
 water += distance * (boundedHeight - height[top]);
 }
 stack.push(i);
 }

 return water;
}
```

---

## L14: Sum of Subarray Minimums

**Question:** Find sum of minimums of all subarrays.
**Intuition:** For each element, find how many subarrays it's the minimum. Use PSE and NSE.
**Logic:** Contribution of arr[i] = arr[i] * (i - PSE[i]) * (NSE[i] - i).

**Example:**
Input: arr = [3,1,2,4]
Output: 17
Explanation:
Subarrays are [3], [1], [2], [4], [3,1], [1,2], [2,4], [3,1,2], [1,2,4], [3,1,2,4].
Minimums are 3, 1, 2, 4, 1, 1, 2, 1, 1, 1.
Sum is 17.
**Java:**

```java
int sumSubarrayMins(int[] arr) {
 int n = arr.length;
 int MOD = 1_000_000_007;

 // Find previous smaller and next smaller elements
 int[] prevSmaller = new int[n];
 int[] nextSmaller = new int[n];

 Stack<Integer> stack = new Stack<>();

 // Previous smaller
 for (int i = 0; i < n; i++) {
 while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) {
 stack.pop();
 }
 prevSmaller[i] = stack.isEmpty() ? -1 : stack.peek();
 stack.push(i);
 }

 stack.clear();

 // Next smaller
 for (int i = n - 1; i >= 0; i--) {
 while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) {
 stack.pop();
 }
 nextSmaller[i] = stack.isEmpty() ? n : stack.peek();
 stack.push(i);
 }

 long sum = 0;
 for (int i = 0; i < n; i++) {
 long left = i - prevSmaller[i];
 long right = nextSmaller[i] - i;
 sum = (sum + (long)arr[i] * left * right) % MOD;
 }

 return (int)sum;
}
```

---

## L15: Asteroid Collision

**Question:** Asteroids moving right (+ve) and left (-ve). Find state after collisions.
**Intuition:** Use stack. Right-moving stay. Left-moving collide with right-moving on stack.
**Logic:** Push right-moving. For left-moving, pop smaller right-moving asteroids.
**Example 1:**

Input: asteroids = [5,10,-5]
Output: [5,10]
Explanation: The 10 and -5 collide resulting in 10. The 5 and 10 never collide.

**Example 2:**
Input: asteroids = [8,-8]
Output: []
Explanation: The 8 and -8 collide exploding each other.
**Java:**

```java
int[] asteroidCollision(int[] asteroids) {
 Stack<Integer> stack = new Stack<>();

 for (int asteroid : asteroids) {
 boolean alive = true;

 while (!stack.isEmpty() && asteroid < 0 && stack.peek() > 0) {
 // Collision occurs
 if (stack.peek() < -asteroid) {
 stack.pop(); // Right-moving destroyed
 continue;
 } else if (stack.peek() == -asteroid) {
 stack.pop(); // Both destroyed
 }
 alive = false;
 break;
 }

 if (alive) {
 stack.push(asteroid);
 }
 }

 int[] result = new int[stack.size()];
 for (int i = result.length - 1; i >= 0; i--) {
 result[i] = stack.pop();
 }
 return result;
}
```

---

## L16: Sum of Subarray Ranges

**Question:** Sum of (max - min) for all subarrays.
**Intuition:** Sum of max - Sum of min. Use contribution technique with monotonic stack.
**Logic:** Similar to subarray minimums, but for both max and min.

**Example 1:**

<pre><strong>Input:</strong> nums = [1,2,3]
<strong>Output:</strong> 4
<strong>Explanation:</strong> The 6 subarrays of nums are the following:
[1], range = largest - smallest = 1 - 1 = 0
[2], range = 2 - 2 = 0
[3], range = 3 - 3 = 0
[1,2], range = 2 - 1 = 1
[2,3], range = 3 - 2 = 1
[1,2,3], range = 3 - 1 = 2
So the sum of all ranges is 0 + 0 + 0 + 1 + 1 + 2 = 4.</pre>

**Java:**

```java
long subArrayRanges(int[] arr) {
 return sumSubarrayMaxs(arr) - sumSubarrayMins(arr);
}

long sumSubarrayMaxs(int[] arr) {
 // Similar to sumSubarrayMins but find previous/next greater
 // Contribution: arr[i] * (i - PGE[i]) * (NGE[i] - i)
}

long sumSubarrayMins(int[] arr) {
 // As implemented earlier
}
```

---

## L17: Remove K Digits

**Question:** Remove K digits to form smallest number.
**Intuition:** Use monotonic increasing stack. Remove larger digits greedily from left.
**Logic:** Build smallest number by keeping digits in increasing order.

**Example:**
Input: num = "1432219", k = 3
Output: "1219"
Explanation: Remove the three digits 4, 3, and 2 to form the new number 1219 which is the smallest.

**Approach 1: DP Thinking (Natural but Inefficient)**

**Initial Intuition:**
For each digit, I have 2 choices: KEEP or REMOVE.

**DP Recurrence:** dp(index, removals_left, current_number) = min of:

- Keep digit: dp(index+1, k, current_number + digit)
- Remove digit: dp(index+1, k-1, current_number)

**Why It Seems Correct:**

- Explores all possibilities
- Guarantees finding minimum
- Classic "choice at each step" pattern

**Why It Fails:**
❌ **Time Complexity:** O(2^n) - exponential explosion
❌ **State Space:** Must track the actual number string being built → huge memory
❌ **Redundant Work:** Recalculates same subproblems repeatedly

**Verdict:** Correct logic but impractical for large inputs.

**Approach 2: Greedy + Stack (Optimal Solution)**

**The Greedy Insight:**

**Key Observation:**
If digit A appears before digit B, and A > B, we should ALWAYS remove A to minimize the number.

**Why?**

- Position matters: Earlier digits have higher significance (1000s vs 10s)
- Example: "43" → removing '4' gives "3" (better than removing '3' to get "4")

**The Greedy Choice:**
When you see a smaller digit, go BACK and remove all larger digits you recently kept.

**Why Greedy Works Here:**

**1. Greedy Choice Property ✓**

- Removing a larger digit before a smaller digit is ALWAYS optimal
- No future digit can make us regret this choice
- Example: If "43" appears, removing '4' is irreversibly correct

**2. Optimal Substructure ✓**

- After removing k digits optimally from first m digits, remaining problem is same type
- Optimal solution = greedy choices at each step

**3. Exchange Argument Proof:**

- Assume optimal solution keeps '4' when '43' exists
- Swap: Remove '4' instead → number becomes smaller
- Contradiction: Our assumed optimal solution is not optimal, hence the greedy choice is correct.

**Java:**

```java
String removeKdigits(String num, int k) {
 Stack<Character> stack = new Stack<>();

 for (char digit : num.toCharArray()) {
 while (!stack.isEmpty() && k > 0 && stack.peek() > digit) {
 stack.pop();
 k--;
 }
 stack.push(digit);
 }

 // Remove remaining k digits from end
 while (k > 0 && !stack.isEmpty()) {
 stack.pop();
 k--;
 }

 // Build result, skip leading zeros
 StringBuilder result = new StringBuilder();
 boolean leadingZero = true;
 for (char c : stack) {
 if (c == '0' && leadingZero) continue;
 leadingZero = false;
 result.append(c);
 }

 return result.length() == 0 ? "0" : result.toString();
}
```

---

## L18: Largest Rectangle in Histogram

**Question:** Find largest rectangle area in histogram.
**Intuition:** For each bar, find previous and next smaller bars. Width = NSE - PSE - 1.
**Logic:** Use monotonic stack to find PSE and NSE.

**Example 1:**

![](https://assets.leetcode.com/uploads/2021/01/04/histogram.jpg)

<pre><strong>Input:</strong> heights = [2,1,5,6,2,3]
<strong>Output:</strong> 10
<strong>Explanation:</strong> The above is a histogram where width of each bar is 1.
The largest rectangle is shown in the red area, which has an area = 10 units.</pre>

**Java:**

```java
public int largestRectangleArea(int[] heights) {
 int n = heights.length;

 // Find boundaries for each bar
 int[] leftSmaller = new int[n]; // Previous smaller element index
 int[] rightSmaller = new int[n]; // Next smaller element index

 Stack<Integer> st = new Stack<>();

 // Find left boundaries (previous smaller)
 for (int i = 0; i < n; i++) {
 while (!st.isEmpty() && heights[st.peek()] >= heights[i]) {
 st.pop();
 }
 leftSmaller[i] = st.isEmpty() ? -1 : st.peek();
 st.push(i);
 }

 st.clear();

 // Find right boundaries (next smaller)
 for (int i = n - 1; i >= 0; i--) {
 while (!st.isEmpty() && heights[st.peek()] >= heights[i]) {
 st.pop();
 }
 rightSmaller[i] = st.isEmpty() ? n : st.peek();
 st.push(i);
 }

 // Calculate maximum area
 int maxArea = 0;
 for (int i = 0; i < n; i++) {
 int width = rightSmaller[i] - leftSmaller[i] - 1;
 int area = heights[i] * width;
 maxArea = Math.max(maxArea, area);
 }

 return maxArea;
}

```

---

## L19: Maximal Rectangle

**Question:** Find largest rectangle of 1s in binary matrix.
**Intuition:** Treat each row as base of histogram. Heights accumulate consecutive 1s.
**Logic:** For each row, apply largest rectangle in histogram.

**Natural Intuition:**
"For each cell with '1', try to build rectangles starting from it"

**Why This Seems Right:**

- Explore all possible rectangles
- Standard DP "explore all choices" pattern
- Guaranteed to find maximum

**Why It's Inefficient:**
❌ **Time:** O(m × n × m × n) - must check all rectangle combinations
❌ **Space:** Complex state tracking
❌ **Too many subproblems:** Overlapping rectangles checked repeatedly

**Verdict:** Correct but impractical.

**THE BREAKTHROUGH: 2D → 1D Transformation**
**Key Insight:**
"Treat each row as the BASE of a histogram, where bar heights = consecutive 1s extending UPWARD"

This reduces the problem to: "Find largest rectangle in histogram" (which you already know!)

**THE "HISTOGRAM PER ROW" CONCEPT**
**What "Row as Base" Means:**
Standing at row i, look UPWARD and count consecutive 1s from each column

**Building Heights Array:**
For each cell at (row, col):

```java
if (matrix[row][col] == '1'):
 heights[col]++ // Extend bar UP (more consecutive 1s)
else:
 heights[col] = 0 // Hit a 0, RESET to ground level
```

**Meaning of heights[col]:**
"Number of consecutive 1s from current row going UP (including current row)"

**VISUAL WALKTHROUGH**
**Example Matrix:**

```
 0 1 2 3 4
R0: 1 0 1 0 0
R1: 1 0 1 1 1
R2: 1 1 1 1 1
R3: 1 0 0 1 0
```

**Row 0: Heights =**
Looking up from R0:
█ _ █ _ _ ← Histogram
Max area = 1

**Row 1: Heights =**
Looking up from R1 (includes R0+R1):
█ _ █ _ _ ← 2 consecutive 1s at col 0
█ _ █ █ █ ← 1 consecutive 1 at cols 3-4
Max area = 3 (cols 2-4, height 1)

**Row 2: Heights =**
Looking up from R2 (includes R0+R1+R2):
█ _ █ _ _ ← 3 consecutive at cols 0,2
█ █ █ █ █ ← 2 consecutive at cols 1,3,4
█ █ █ █ █
Max area = 6 (cols 2-4, height 2)

This represents the rectangle:

```
R1: [_, _, 1, 1, 1] ← 2 rows
R2: [_, _, 1, 1, 1] ← × 3 cols = 6
```

**Row 3: Heights =**
█ _ _ █ _ ← Col 0 has 4 consecutive
█ _ _ █ _ Col 3 has 3 consecutive
█ _ _ █ _ (R3 breaks cols 1,2,4)
█ _ _ _ _
Max area = 4 (col 0 only)

**Global maximum = 6 ✓**

**Example 1:**

![](https://assets.leetcode.com/uploads/2020/09/14/maximal.jpg)

<pre><strong>Input:</strong> matrix = [["1","0","1","0","0"],["1","0","1","1","1"],["1","1","1","1","1"],["1","0","0","1","0"]]
<strong>Output:</strong> 6
<strong>Explanation:</strong> The maximal rectangle is shown in the above picture.</pre>

**Java:**

```java
int maximalRectangle(char[][] matrix) {
 if (matrix.length == 0) return 0;

 int m = matrix.length, n = matrix[0].length;
 int[] heights = new int[n];
 int maxArea = 0;

 for (int i = 0; i < m; i++) {
 // Update heights
 for (int j = 0; j < n; j++) {
 if (matrix[i][j] == '1') {
 heights[j]++;
 } else {
 heights[j] = 0;
 }
 }

 // Find max rectangle for this row
 maxArea = Math.max(maxArea, largestRectangleArea(heights));
 }

 return maxArea;
}
```

---

## L20: Sliding Window Maximum

**Question:** Find maximum in each sliding window of size k.
**Intuition:** Use deque to maintain elements in decreasing order. Front has maximum.
**Logic:** Remove elements outside window and smaller elements.


**Start with Priority Queue:**

> "My first thought is a max heap to track the maximum. But removing elements from the window is O(k), making it O(nk) total. Let me think of something better..."

**Transition to Deque:**

> "I notice that if a smaller element appears before a larger one, it can never be the maximum while the larger one is in the window. This suggests maintaining only 'useful' candidates in decreasing order - a monotonic deque."

**Example 1:**

<pre><strong>Input:</strong> nums = [1,3,-1,-3,5,3,6,7], k = 3
<strong>Output:</strong> [3,3,5,5,6,7]
<strong>Explanation:</strong>
Window position Max
--------------- -----
[1 3 -1] -3 5 3 6 7 <strong>3</strong>
 1 [3 -1 -3] 5 3 6 7 <strong>3</strong>
 1 3 [-1 -3 5] 3 6 7 <strong> 5</strong>
 1 3 -1 [-3 5 3] 6 7 <strong>5</strong>
 1 3 -1 -3 [5 3 6] 7 <strong>6</strong>
 1 3 -1 -3 5 [3 6 7] <strong>7</strong>
</pre>

**Example 2:**

<pre><strong>Input:</strong> nums = [1], k = 1
<strong>Output:</strong> [1]</pre>

**Java:**

```java
int[] maxSlidingWindow(int[] arr, int k) {
 int n = arr.length;
 int[] result = new int[n - k + 1];
 Deque<Integer> deque = new ArrayDeque<>(); // stores indices

 for (int i = 0; i < n; i++) {
 // Remove elements outside window
 while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
 deque.pollFirst();
 }

 // Remove smaller elements (maintain decreasing order)
 while (!deque.isEmpty() && arr[deque.peekLast()] < arr[i]) {
 deque.pollLast();
 }

 deque.addLast(i);

 // Add to result when window is complete
 if (i >= k - 1) {
 result[i - k + 1] = arr[deque.peekFirst()];
 }
 }

 return result;
}
// Time: O(n), Space: O(k)
```

---

## L21: Stock Span Problem

**Question:** Find span (consecutive days with price <= today) for each day.
**Intuition:** For each price, find previous greater or equal price index.
**Logic:** Monotonic stack, store indices.

```
Input: arr[] = [100, 80, 90, 120]
Output: [1, 1, 2, 4]
Explanation: Traversing the given input span 100 is greater than equal to 100 and there are no more days behind it so the span is 1, 80 is greater than equal to 80 and smaller than 100 so the span is 1, 90 is greater than equal to 90 and 80 so the span is 2, 120 is greater than 90, 80 and 100 so the span is 4. So the output will be [1, 1, 2, 4].
```

**Java:**

```java
int[] stockSpan(int[] prices) {
 int n = prices.length;
 int[] span = new int[n];
 Stack<Integer> stack = new Stack<>(); // store indices

 for (int i = 0; i < n; i++) {
 // Pop smaller or equal prices
 while (!stack.isEmpty() && prices[stack.peek()] <= prices[i]) {
 stack.pop();
 }

 span[i] = stack.isEmpty() ? (i + 1) : (i - stack.peek());
 stack.push(i);
 }

 return span;
}
```

---

## L22: The Celebrity Problem

**Question:** Find celebrity (known by all, knows none). Or return -1.
**Intuition:** Use stack or two-pointer. Eliminate non-celebrities.
**Logic:** Compare pairs - if A knows B, A can't be celebrity. If A doesn't know B, B can't be celebrity.

A square matrix **mat[][] **of size n\*n is used to represent people at the party such that if an element of row **i** and column **j** is **set to 1** it means **ith person knows jth person**. You need to return the **index** of the **celebrity** in the party, if the celebrity does not exist, return **-1**.

**Note:** Follow **0-based** indexing.

**Examples:**

```
Input: mat[][] = [[1, 1, 0],
 [0, 1, 0],
 [0, 1, 1]]
Output: 1
Explanation: 0th and 2nd person both know 1st person and 1st person does not know anyone. Therefore, 1 is the celebrity person.
```


**Java:**

```java
int findCelebrity(int n) {
 Stack<Integer> stack = new Stack<>();

 // Push all candidates
 for (int i = 0; i < n; i++) {
 stack.push(i);
 }

 // Eliminate non-celebrities
 while (stack.size() > 1) {
 int a = stack.pop();
 int b = stack.pop();

 if (knows(a, b)) {
 stack.push(b); // a knows b, so a is not celebrity
 } else {
 stack.push(a); // a doesn't know b, so b is not celebrity
 }
 }

 int candidate = stack.pop();

 // Verify candidate
 for (int i = 0; i < n; i++) {
 if (i != candidate && (knows(candidate, i) || !knows(i, candidate))) {
 return -1;
 }
 }

 return candidate;
}
// Time: O(n), Space: O(n)
```

---

## L23: LRU Cache

**Question:** Implement LRU (Least Recently Used) cache with O(1) get and put.
**Intuition:** Use HashMap + Doubly Linked List. HashMap for O(1) access, DLL for O(1) insertion/deletion.
**Logic:** Most recent at head, least recent at tail. Move to head on access.

Implement the `LRUCache` class:

* `LRUCache(int capacity)` Initialize the LRU cache with **positive** size `capacity`.
* `int get(int key)` Return the value of the `key` if the key exists, otherwise return `-1`.
* `void put(int key, int value)` Update the value of the `key` if the `key` exists. Otherwise, add the `key-value` pair to the cache. If the number of keys exceeds the `capacity` from this operation, **evict** the least recently used key.

The functions `get` and `put` must each run in `O(1)` average time complexity.

**Java:**

```java
class Node {
 int key, value;
 Node prev, next;
 Node(int k, int v) { key = k; value = v; }
}

class LRUCache {
 Map<Integer, Node> map = new HashMap<>();
 Node head = new Node(0, 0);
 Node tail = new Node(0, 0);
 int capacity;

 LRUCache(int capacity) {
 this.capacity = capacity;
 head.next = tail;
 tail.prev = head;
 }

 int get(int key) {
 if (!map.containsKey(key)) return -1;

 Node node = map.get(key);
 remove(node);
 insert(node);
 return node.value;
 }

 void put(int key, int value) {
 if (map.containsKey(key)) {
 remove(map.get(key));
 }

 if (map.size() == capacity) {
 remove(tail.prev);
 }

 insert(new Node(key, value));
 }

 void insert(Node node) {
 map.put(node.key, node);
 node.next = head.next;
 node.prev = head;
 head.next.prev = node;
 head.next = node;
 }

 void remove(Node node) {
 map.remove(node.key);
 node.prev.next = node.next;
 node.next.prev = node.prev;
 }
}
// Time: O(1) for get and put
```

---

## L24: LFU Cache

**Question:** Implement LFU (Least Frequently Used) cache.
**Intuition:** Track frequency of each key. Evict least frequent. Use HashMap + frequency map + DLL.
**Logic:** Maintain frequency buckets with DLL for LRU order within same frequency.

**Java:**

```java
class LFUCache {
 Map<Integer, Node> cache;
 Map<Integer, DLList> freqMap;
 int capacity, minFreq;

 class Node {
 int key, val, freq;
 Node prev, next;
 Node(int k, int v) {
 key = k; val = v; freq = 1;
 }
 }

 class DLList {
 Node head, tail;
 int size;

 DLList() {
 head = new Node(0, 0);
 tail = new Node(0, 0);
 head.next = tail;
 tail.prev = head;
 }

 void add(Node node) {
 node.next = head.next;
 node.prev = head;
 head.next.prev = node;
 head.next = node;
 size++;
 }

 void remove(Node node) {
 node.prev.next = node.next;
 node.next.prev = node.prev;
 size--;
 }

 Node removeLast() {
 if (size > 0) {
 Node last = tail.prev;
 remove(last);
 return last;
 }
 return null;
 }
 }

 LFUCache(int capacity) {
 this.capacity = capacity;
 cache = new HashMap<>();
 freqMap = new HashMap<>();
 minFreq = 0;
 }

 int get(int key) {
 if (!cache.containsKey(key)) return -1;

 Node node = cache.get(key);
 updateFreq(node);
 return node.val;
 }

 void put(int key, int value) {
 if (capacity == 0) return;

 if (cache.containsKey(key)) {
 Node node = cache.get(key);
 node.val = value;
 updateFreq(node);
 } else {
 if (cache.size() >= capacity) {
 DLList minList = freqMap.get(minFreq);
 Node toRemove = minList.removeLast();
 cache.remove(toRemove.key);
 }

 Node newNode = new Node(key, value);
 cache.put(key, newNode);
 freqMap.computeIfAbsent(1, k -> new DLList()).add(newNode);
 minFreq = 1;
 }
 }

 void updateFreq(Node node) {
 int freq = node.freq;
 DLList list = freqMap.get(freq);
 list.remove(node);

 if (freq == minFreq && list.size == 0) {
 minFreq++;
 }

 node.freq++;
 freqMap.computeIfAbsent(node.freq, k -> new DLList()).add(node);
 }
}
```

---
