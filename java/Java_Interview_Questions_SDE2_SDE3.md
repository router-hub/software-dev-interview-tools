# Top 50 Java Interview Questions (SDE2/SDE3)

> **Target**: Senior Developer (SDE2/SDE3) positions  
> **Focus**: Deep conceptual understanding with practical examples

---

## Memory Management & JVM (Questions 1-10)

### Q1: Explain JVM architecture and how Java achieves platform independence

**Answer**:
JVM (Java Virtual Machine) has three main components:

1. **Class Loader Subsystem**: Loads `.class` files
   - Bootstrap, Extension, Application class loaders
   - Loading → Linking → Initialization

2. **Runtime Data Areas**:
   - **Heap**: Object storage (shared across threads)
   - **Stack**: Method frames, local variables (per-thread)
   - **Method Area**: Class metadata, constants (shared)
   - **PC Register**: Current instruction address (per-thread)
   - **Native Method Stack**: Native code execution

3. **Execution Engine**:
   - **Interpreter**: Executes bytecode line-by-line
   - **JIT Compiler**: Compiles hot spots to native code
   - **Garbage Collector**: Automatic memory management

**Platform Independence**: Java source → Bytecode (platform-independent) → JVM (platform-specific) → Native code. Same bytecode runs on any OS with appropriate JVM.

**Follow-up**: Describe JIT compilation and how it improves performance.

---

### Q2: Difference between Stack and Heap memory?

**Answer**:

| Aspect        | Stack                                   | Heap                                |
| ------------- | --------------------------------------- | ----------------------------------- |
| **Scope**     | Thread-specific                         | Application-wide (shared)           |
| **Stores**    | Local variables, primitives, references | Objects, arrays, instance variables |
| **Lifecycle** | Cleared when method returns             | Managed by GC                       |
| **Size**      | Smaller (typically MB)                  | Larger (can be GB)                  |
| **Speed**     | Very fast (LIFO)                        | Slower (fragmentation)              |
| **Errors**    | `StackOverflowError`                    | `OutOfMemoryError`                  |

**Example**:
```java
void method() {
    int x = 10;           // Stack: primitive
    String s = "Hello";   // Stack: reference, Heap: String object
    Product p = new Product();  // Stack: reference, Heap: Product object
}
```

**Interview Trap**: "Is Java pass-by-value or pass-by-reference?"  
**Answer**: Java is **always pass-by-value**. For objects, the value is the reference (address), not the object itself.

---

### Q3: How does Garbage Collection work? Describe GC algorithms.

**Answer**:

**GC Basics**:
- Automatic memory management
- Identifies and removes unreachable objects
- Runs in background (non-deterministic)

**GC Roots** (Starting points for reachability):
- Local variables in stack
- Active threads
- Static variables
- JNI references

**Major GC Algorithms**:

1. **Serial GC** (`-XX:+UseSerialGC`):
   - Single thread
   - Good for small heap (<100MB)
   - Stop-the-world pauses

2. **Parallel GC** (`-XX:+UseParallelGC`):
   - Multiple threads (throughput-focused)
   - Good for batch processing
   - Longer pauses but faster overall

3. **G1 GC** (`-XX:+UseG1GC`) - **Default since Java 9**:
   - Divides heap into regions
   - Targets pause time goals
   - Good balance of throughput and latency

4. **ZGC** (`-XX:+UseZGC`):
   - Ultra-low pause times (<10ms)
   - Large heaps (TB+)
   - Java 15+

**Generational GC**:
```
┌──────────────────────────────────┐
│ Young Generation (Minor GC)      │
│  - Eden Space                    │
│  - Survivor 0, Survivor 1        │
├──────────────────────────────────┤
│ Old Generation (Major GC)        │
│  - Long-lived objects            │
├──────────────────────────────────┤
│ Metaspace (Permanent)            │
│  - Class metadata                │
└──────────────────────────────────┘
```

**Interview Question**: When is an object eligible for GC?  
**Answer**: When no live thread can reach it through any chain of references.

---

### Q4: What is a memory leak in Java? How do you detect and fix it?

**Answer**:

**Memory Leak**: Objects that are no longer needed but still referenced, preventing GC from reclaiming memory.

**Common Causes**:
1. **Static collections**:
   ```java
   public class Cache {
       private static List<Object> cache = new ArrayList<>();
       
       public void add(Object obj) {
           cache.add(obj);  // Never removed → leak!
       }
   }
   ```

2. **Unclosed resources**:
   ```java
   // BAD
   Connection conn = getConnection();
   // If exception occurs, connection never closed
   
   // GOOD
   try (Connection conn = getConnection()) {
       // Use connection
   }  // Auto-closed
   ```

3. **Inner classes holding outer references**:
   ```java
   public class Outer {
       private byte[] data = new byte[1000000];
       
       public class Inner {
           // Implicitly holds reference to Outer
       }
   }
   ```

4. **ThreadLocal not cleaned**:
   ```java
   ThreadLocal<HeavyObject> threadLocal = new ThreadLocal<>();
   // Must call threadLocal.remove() in finally block
   ```

**Detection Tools**:
- **Heap dumps**: `jmap -dump:format=b,file=heap.bin <pid>`
- **Visual VM**, **JProfiler**, **YourKit**
- **Eclipse MAT** (Memory Analyzer Tool)

**How to Fix**:
- Use weak references (`WeakHashMap`, `WeakReference`)
- Always close resources (try-with-resources)
- Clear collections when done
- Remove `ThreadLocal` values

---

### Q5: Explain String Pool and String interning

**Answer**:

**String Pool** is a special memory region in Heap where String literals are stored to optimize memory.

```java
// Both point to same object in pool
String s1 = "Hello";
String s2 = "Hello";
System.out.println(s1 == s2);  // true

// Creates new object in heap (not pool)
String s3 = new String("Hello");
System.out.println(s1 == s3);  // false
System.out.println(s1.equals(s3));  // true

// Intern: Get pool version
String s4 = s3.intern();
System.out.println(s1 == s4);  // true (now same pool object)
```

**Why String Pool?**
- Memory optimization (reuse common strings)
- Strings are immutable (safe to share)
- Faster equality checks for literals

**Intern Process**:
- Check if string exists in pool
- If yes, return existing reference
- If no, add to pool and return reference

**Performance Consideration**: Excessive interning can cause performance issues (pool lookup overhead).

---

## Object-Oriented Programming (Questions 11-20)

### Q6: Difference between Abstract Class and Interface?

**Answer**:

| Feature              | Abstract Class           | Interface                              |
| -------------------- | ------------------------ | -------------------------------------- |
| **Inheritance**      | Single (extends one)     | Multiple (implements many)             |
| **State**            | Can have fields          | Only constants (`public static final`) |
| **Methods**          | Abstract + Concrete      | Abstract + Default + Static            |
| **Constructor**      | Yes (called by subclass) | No                                     |
| **Access Modifiers** | Any                      | Public only (methods)                  |
| **Use Case**         | "IS-A" relationship      | "CAN-DO" capability                    |

**When to use what?**
- **Abstract Class**: Sharing code/state among related classes (e.g., `Animal` → `Dog`, `Cat`)
- **Interface**: Contract for unrelated classes (e.g., `Flyable` → `Bird`, `Airplane`)

**Example**:
```java
// Abstract class - shared behavior
abstract class Vehicle {
    protected int wheels;
    
    abstract void move();
    
    void refuel() {  // Concrete method
        System.out.println("Refueling...");
    }
}

// Interface - capability
interface Electric {
    void charge();
}

class Tesla extends Vehicle implements Electric {
    void move() { /* ... */ }
    void charge() { /* ... */ }
}
```

---

### Q7: Explain the equals() and hashCode() contract. Why must they be consistent?

**Answer**:

**The Contract**:
1. If `a.equals(b)` is `true`, then `a.hashCode() == b.hashCode()` (**MUST** be true)
2. If `a.hashCode() == b.hashCode()`, `a.equals(b)` **MAY** be true (hash collision)

**Why it matters?**
Hash-based collections (HashMap, HashSet) use both:
1. **hashCode()**: Determines bucket location
2. **equals()**: Checks equality within bucket

**Example of violation**:
```java
public class Person {
    String name;
    int age;
    
    @Override
    public boolean equals(Object o) {
        Person p = (Person) o;
        return name.equals(p.name) && age == p.age;
    }
    
    // Missing hashCode() override!
    // Default Object.hashCode() returns different values
}

// Bug in action:
Set<Person> set = new HashSet<>();
set.add(new Person("John", 30));
set.add(new Person("John", 30));  // Should be duplicate
System.out.println(set.size());  // 2 (wrong!) → Should be 1
```

**Correct Implementation**:
```java
@Override
public int hashCode() {
    return Objects.hash(name, age);
}
```

**Performance Tip**: Good hash function distributes objects evenly across buckets to minimize collisions.

---

### Q8: What is the difference between == and equals()?

**Answer**:

| Aspect          | ==                         | equals()                  |
| --------------- | -------------------------- | ------------------------- |
| **Compares**    | Reference (memory address) | Content                   |
| **Works on**    | Primitives + Objects       | Only objects              |
| **Overridable** | No                         | Yes                       |
| **Performance** | Fastest                    | Depends on implementation |

**Examples**:
```java
// Primitives: == compares values
int a = 5;
int b = 5;
System.out.println(a == b);  // true

// Objects: == compares references
String s1 = new String("Hello");
String s2 = new String("Hello");
System.out.println(s1 == s2);  // false (different objects)
System.out.println(s1.equals(s2));  // true (same content)

// Integer caching (-128 to 127)
Integer i1 = 127;
Integer i2 = 127;
System.out.println(i1 == i2);  // true (cached)

Integer i3 = 128;
Integer i4 = 128;
System.out.println(i3 == i4);  // false (not cached)
System.out.println(i3.equals(i4));  // true
```

**Best Practice**: Always use `equals()` for object comparison, `==` for primitives and null checks.

---

### Q9: Explain method overloading vs method overriding

**Answer**:

**Method Overloading (Compile-time Polymorphism)**:
- Same method name, different parameters
- Resolved at compile time

```java
class Calculator {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }
    int add(int a, int b, int c) { return a + b + c; }
}
```

**Rules**:
- Parameter list must differ (count, type, or order)
- Return type alone is **not** sufficient
- Can change access modifier
- Can throw different exceptions

**Method Overriding (Runtime Polymorphism)**:
- Subclass provides implementation for parent method
- Resolved at runtime

```java
class Animal {
    void makeSound() { System.out.println("Generic sound"); }
}

class Dog extends Animal {
    @Override
    void makeSound() { System.out.println("Bark"); }
}
```

**Rules**:
- Same signature (name + parameters)
- Cannot narrow access (protected → private ❌)
- Can widen access (protected → public ✓)
- Cannot throw broader checked exceptions
- Return type must be same or subtype (covariant)

**Interview Trick**:
```java
class Parent {
    static void method() { }
}

class Child extends Parent {
    static void method() { }  // Hiding, NOT overriding!
}
```
Static methods are **hidden**, not overridden.

---

### Q10: What are Sealed Classes? Why use them?

**Answer**:

**Sealed Classes** (Java 17+) restrict which classes can extend/implement them.

**Syntax**:
```java
public sealed class Shape permits Circle, Square, Triangle {
    // Only these 3 can extend Shape
}

public final class Circle extends Shape { }
public final class Square extends Shape { }
public non-sealed class Triangle extends Shape { }  // Opens inheritance again
```

**Benefits**:
1. **Controlled inheritance**: Prevent unwanted extensions
2. **Exhaustive pattern matching**:
   ```java
   String describe(Shape shape) {
       return switch (shape) {
           case Circle c -> "Circle";
           case Square s -> "Square";
           case Triangle t -> "Triangle";
           // No default needed - compiler knows these are ALL possibilities
       };
   }
   ```
3. **Domain modeling**: Express restricted type hierarchies clearly

**Use Cases**:
- Expression trees in parsers
- State machines
- Domain models with fixed set of types

---

## Collections & Data Structures (Questions 21-30)

### Q11: Internal working of HashMap

**Answer**:

**Structure**: Array of buckets (linked lists/trees)

```
HashMap<K, V>
┌───────────────────────────────┐
│ Bucket 0:  null               │
│ Bucket 1:  Entry→Entry→null   │
│ Bucket 2:  TreeNode→...       │  (Tree if >8 entries)
│ Bucket 3:  Entry→null         │
│ ...                           │
│ Bucket 15: null               │
└───────────────────────────────┘

Entry: [hash | key | value | next]
```

**Put Operation**:
1. Calculate `hash = key.hashCode()`
2. Find bucket: `index = hash & (capacity - 1)`
3. Check if key exists in bucket using `equals()`
   - If yes: Replace value
   - If no: Add new entry
4. If size > load factor * capacity: **Resize** (double capacity, rehash)

**Get Operation**:
```java
public V get(Object key) {
    int hash = hash(key);
    int index = hash & (capacity - 1);
    Entry<K,V> e = buckets[index];
    
    while (e != null) {
        if (e.hash == hash && key.equals(e.key)) {
            return e.value;
        }
        e = e.next;
    }
    return null;
}
```

**Important Parameters**:
- **Initial Capacity**: 16 (default)
- **Load Factor**: 0.75 (resize when 75% full)
- **Treeify Threshold**: 8 (convert list → tree)

**Time Complexity**:
- Best case: O(1)
- Worst case: O(n) → mitigated by trees: O(log n)

**Thread Safety**: Not thread-safe. Use `ConcurrentHashMap` or `Collections.synchronizedMap()`.

---

### Q12: Difference between HashMap, TreeMap, and LinkedHashMap

**Answer**:

| Feature            | HashMap         | TreeMap        | LinkedHashMap            |
| ------------------ | --------------- | -------------- | ------------------------ |
| **Order**          | No order        | Sorted by key  | Insertion order          |
| **Null keys**      | 1 allowed       | Not allowed    | 1 allowed                |
| **Performance**    | O(1) avg        | O(log n)       | O(1) avg                 |
| **Implementation** | Hash table      | Red-Black Tree | Hash table + Linked list |
| **Use Case**       | General purpose | Sorted keys    | LRU cache                |

**Examples**:
```java
// HashMap - no order
Map<String, Integer> hashMap = new HashMap<>();
hashMap.put("C", 3);
hashMap.put("A", 1);
hashMap.put("B", 2);
// Order: unpredictable

// TreeMap - sorted by key
Map<String, Integer> treeMap = new TreeMap<>();
treeMap.put("C", 3);
treeMap.put("A", 1);
treeMap.put("B", 2);
// Order: A, B, C

// LinkedHashMap - insertion order
Map<String, Integer> linkedMap = new LinkedHashMap<>();
linkedMap.put("C", 3);
linkedMap.put("A", 1);
linkedMap.put("B", 2);
// Order: C, A, B
```

**LRU Cache with LinkedHashMap**:
```java
Map<Integer, String> cache = new LinkedHashMap<>(16, 0.75f, true) {
    protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
        return size() > MAX_SIZE;
    }
};
```

---

### Q13: Fail-fast vs Fail-safe iterators

**Answer**:

**Fail-Fast** (throw `ConcurrentModificationException`):
- Detects structural modification during iteration
- Used by: `ArrayList`, `HashMap`, `HashSet`

```java
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");

for (String s : list) {
    list.remove(s);  // ConcurrentModificationException!
}
```

**Fix**:
```java
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    it.remove();  // Safe removal
}
```

**Fail-Safe** (allows modification):
- Works on copy of collection
- Used by: `CopyOnWriteArrayList`, `ConcurrentHashMap`

```java
List<String> list = new CopyOnWriteArrayList<>();
list.add("A");
list.add("B");

for (String s : list) {
    list.remove(s);  // No exception, but inconsistent view
}
```

**Trade-off**:
- Fail-fast: Memory efficient, catches bugs early
- Fail-safe: Thread-safe, higher memory cost

---

##Multithreading & Concurrency (Questions 31-40)

### Q14: Explain thread lifecycle in Java

**Answer**:

**Thread States** (`Thread.State`):
```
NEW → RUNNABLE ⇄ BLOCKED
         ↕           ↓
     WAITING ←  TIMED_WAITING
         ↓
    TERMINATED
```

1. **NEW**: Thread created but not started
   ```java
   Thread t = new Thread();  // NEW state
   ```

2. **RUNNABLE**: Ready to run or running
   ```java
   t.start();  // RUNNABLE (OS scheduler decides when to run)
   ```

3. **BLOCKED**: Waiting for monitor lock
   ```java
   synchronized (obj) {  // Another thread holds lock → BLOCKED
       // ...
   }
   ```

4. **WAITING**: Waiting indefinitely for another thread
   ```java
   obj.wait();  // WAITING until notify()
   thread.join();  // WAITING until thread completes
   ```

5. **TIMED_WAITING**: Waiting for specified time
   ```java
   Thread.sleep(1000);  // TIMED_WAITING for 1 second
   obj.wait(5000);  // TIMED_WAITING for 5 seconds
   ```

6. **TERMINATED**: Execution completed
   ```java
   // run() method finished → TERMINATED
   ```

---

### Q15: What is the difference between synchronized and volatile?

**Answer**:

| Feature         | synchronized              | volatile               |
| --------------- | ------------------------- | ---------------------- |
| **Scope**       | Block/method              | Variable only          |
| **Atomicity**   | Ensures atomic operations | No atomicity guarantee |
| **Visibility**  | Ensures visibility        | Ensures visibility     |
| **Performance** | Slower (locking)          | Faster (no locking)    |
| **Use case**    | Complex operations        | Simple flag variables  |

**synchronized**:
```java
private int counter = 0;

public synchronized void increment() {
    counter++;  // Atomic: read → modify → write
}
```

**volatile** (visibility only):
```java
private volatile boolean running = true;

// Thread 1
public void stop() {
    running = false;  // Immediately visible to Thread 2
}

// Thread 2
public void run() {
    while (running) {  // Reads latest value
        // Work
    }
}
```

**When volatile is NOT enough**:
```java
private volatile int counter = 0;

public void increment() {
    counter++;  // NOT atomic! (read-modify-write)
}
```

**Solution**: Use `AtomicInteger` or `synchronized`.

---

## Modern Java Features (Questions 41-50)

### Q16: What are Records? When to use them?

**Answer**:

**Records** are immutable data carriers (Java 14+).

**Benefits**:
- Eliminates boilerplate
- Immutable by default
- Thread-safe
- Pattern matching support

**Example**:
```java
// Traditional POJO: ~50 lines
public final class Point {
    private final int x;
    private final int y;
    public Point(int x, int y) { /* ... */ }
    public int getX() { /* ... */ }
    // equals, hashCode, toString
}

// Record: 1 line
public record Point(int x, int y) {}
```

**Custom logic**:
```java
public record Person(String name, int age) {
    // Compact constructor (validation)
    public Person {
        if (age < 0) throw new IllegalArgumentException();
    }
    
    // Custom methods
    public boolean isAdult() {
        return age >= 18;
    }
}
```

**When to use**:
- DTOs (Data Transfer Objects)
- API responses
- Value objects
- Immutable configurations

**When NOT to use**:
- Need mutability
- Inheritance (records are final)
- JPA entities (need setters)

---

### Q17: Explain Pattern Matching and its benefits

**Answer**:

**Pattern Matching** reduces casting boilerplate (Java 16+).

**Old Way**:
```java
if (obj instanceof String) {
    String str = (String) obj;
    System.out.println(str.length());
}
```

**New Way**:
```java
if (obj instanceof String str) {
    System.out.println(str.length());  // No cast!
}
```

**With Guards** (Java 21+):
```java
if (obj instanceof String str && str.length() > 10) {
    System.out.println("Long string: " + str);
}
```

**Switch Pattern Matching**:
```java
String formatted = switch (obj) {
    case Integer i -> String.format("int %d", i);
    case Long l -> String.format("long %d", l);
    case Double d -> String.format("double %f", d);
    case String s -> String.format("String %s", s);
    default -> obj.toString();
};
```

**Record Patterns** (Java 21+):
```java
record Point(int x, int y) {}

if (obj instanceof Point(int x, int y)) {
    System.out.println("X: " + x + ", Y: " + y);  // Destructured!
}
```

**Benefits**:
- Less code, more readable
- Compiler verified (catches type errors)
- Enables exhaustive checking

---

## Bonus: Tricky Interview Questions

### Q18: What happens if you don't override hashCode() with equals()?

**Answer**: Objects that are logically equal won't be found in hash-based collections.

```java
Set<Person> set = new HashSet<>();
Person p1 = new Person("John", 30);
set.add(p1);

Person p2 = new Person("John", 30);
System.out.println(set.contains(p2));  // false (should be true!)
```

---

### Q19: Can we override static methods?

**Answer**: No, static methods are **hidden**, not overridden.

```java
class Parent {
    static void method() { System.out.println("Parent"); }
}

class Child extends Parent {
    static void method() { System.out.println("Child"); }
}

Parent p = new Child();
p.method();  // "Parent" (not polymorphic!)
```

---

### Q20: What is double-checked locking? Is it broken?

**Answer**: Pattern for lazy initialization with minimal synchronization.

**Broken version** (pre-Java 5):
```java
private static Singleton instance;

public static Singleton getInstance() {
    if (instance == null) {  // Check 1
        synchronized (Singleton.class) {
            if (instance == null) {  // Check 2
                instance = new Singleton();  // Can be reordered!
            }
        }
    }
    return instance;
}
```

**Fixed** (use volatile):
```java
private static volatile Singleton instance;  // volatile fixes it
```

**Better**: Use enum singleton or initialization-on-demand holder idiom.

---

---

## Java 8+ Functional Programming (Questions 21-30)

### Q21: Explain Streams API. What are intermediate and terminal operations?

**Answer**:

**Streams**: Functional-style operations on collections (Java 8+).

**Characteristics**:
- **Lazy**: Intermediate operations don't execute until terminal operation
- **Immutable**: Don't modify source
- **One-time use**: Can't reuse a stream
- **Potentially parallel**: Easy parallelization

**Intermediate Operations** (return Stream):
```java
List<String> names = List.of("Alice", "Bob", "Charlie", "David");

Stream<String> stream = names.stream()
    .filter(s -> s.length() > 4)    // Intermediate
    .map(String::toUpperCase)       // Intermediate
    .sorted()                        // Intermediate
    .distinct();                     // Intermediate
```

**Terminal Operations** (trigger execution):
```java
// Collect to list
List<String> result = stream.collect(Collectors.toList());

// Other terminal operations
long count = stream.count();
Optional<String> first = stream.findFirst();
boolean anyMatch = stream.anyMatch(s -> s.startsWith("A"));
stream.forEach(System.out::println);
```

**Complete Example**:
```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Sum of squares of even numbers
int sum = numbers.stream()
    .filter(n -> n % 2 == 0)           // 2, 4, 6, 8, 10
    .map(n -> n * n)                    // 4, 16, 36, 64, 100
    .reduce(0, Integer::sum);           // 220

// Group by condition
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
// {false=[1,3,5,7,9], true=[2,4,6,8,10]}
```

**Performance Tip**: Avoid streams for small collections (<100 elements) - traditional loops are faster.

---

### Q22: What is the difference between map() and flatMap()?

**Answer**:

**map()**: Transform each element (1-to-1 mapping)
```java
List<String> words = List.of("hello", "world");
List<Integer> lengths = words.stream()
    .map(String::length)  // ["hello" -> 5, "world" -> 5]
    .collect(Collectors.toList());  // [5, 5]
```

**flatMap()**: Transform and flatten (1-to-many mapping)
```java
List<String> words = List.of("hello", "world");

// Split each word into characters
List<String> chars = words.stream()
    .flatMap(word -> Arrays.stream(word.split("")))
    .collect(Collectors.toList());
// ["h","e","l","l","o","w","o","r","l","d"]

// With map (nested structure)
List<String[]> nested = words.stream()
    .map(word -> word.split(""))
    .collect(Collectors.toList());
// [["h","e","l","l","o"], ["w","o","r","l","d"]]
```

**Real-world Example**:
```java
class Department {
    List<Employee> employees;
}

List<Department> departments = /* ... */;

// Get all employees from all departments
List<Employee> allEmployees = departments.stream()
    .flatMap(dept -> dept.employees.stream())  // Flatten
    .collect(Collectors.toList());
```

---

### Q23: Explain Optional class and best practices

**Answer**:

**Optional**: Container that may or may not contain a value (Java 8+).

**Purpose**: Avoid `NullPointerException` and make null handling explicit.

**Creation**:
```java
Optional<String> present = Optional.of("value");  // Throws if null
Optional<String> nullable = Optional.ofNullable(getValue());  // Safe
Optional<String> empty = Optional.empty();
```

**Usage**:
```java
// BAD: Defeating the purpose
if (optional.isPresent()) {
    String value = optional.get();  // Like null check!
}

// GOOD: Functional style
optional.ifPresent(value -> System.out.println(value));

String result = optional.orElse("default");
String result = optional.orElseGet(() -> computeDefault());
String result = optional.orElseThrow(() -> new Exception("Not found"));

// Chaining
String upperCase = optional
    .map(String::toUpperCase)
    .filter(s -> s.length() > 5)
    .orElse("SHORT");
```

**Anti-patterns** (Don't do this):
```java
// ❌ Returning Optional from getters
public Optional<String> getName() { /* ... */ }

// ❌ Optional as method parameter
public void process(Optional<String> value) { /* ... */ }

// ❌ Optional in collections
List<Optional<String>> list;  // Use List<String> and filter nulls
```

**Best Practices**:
```java
// ✅ Use as return type only
public Optional<User> findById(Long id) {
    return userRepository.findById(id);
}

// ✅ Use orElse for simple defaults
String name = findName().orElse("Guest");

// ✅ Use orElseGet for expensive defaults
User user = findUser().orElseGet(() -> createDefaultUser());
```

---

### Q24: What are Method References? Types?

**Answer**:

**Method References**: Shorthand for lambda expressions (Java 8+).

**Types**:

1. **Static Method Reference** (`Class::staticMethod`):
```java
// Lambda
list.forEach(s -> System.out.println(s));
// Method reference
list.forEach(System.out::println);

// Lambda
numbers.stream().map(n -> Math.sqrt(n));
// Method reference
numbers.stream().map(Math::sqrt);
```

2. **Instance Method Reference** (`instance::instanceMethod`):
```java
String prefix = "Hello ";
// Lambda
names.stream().map(name -> prefix.concat(name));
// Method reference
names.stream().map(prefix::concat);
```

3. **Instance Method of Arbitrary Object** (`Class::instanceMethod`):
```java
// Lambda
names.stream().map(s -> s.toUpperCase());
// Method reference
names.stream().map(String::toUpperCase);

// Lambda
list.sort((s1, s2) -> s1.compareTo(s2));
// Method reference
list.sort(String::compareTo);
```

4. **Constructor Reference** (`Class::new`):
```java
// Lambda
list.stream().map(s -> new Person(s));
// Method reference
list.stream().map(Person::new);

// Array constructor
String[] array = list.stream().toArray(String[]::new);
```

---

### Q25: Parallel Streams - When to use? Pitfalls?

**Answer**:

**Parallel Streams**: Process elements concurrently using ForkJoinPool.

**When to use**:
- Large datasets (>10,000 elements)
- CPU-intensive operations
- Independent operations (no shared state)

**Example**:
```java
List<Integer> numbers = IntStream.rangeClosed(1, 1_000_000)
    .boxed()
    .collect(Collectors.toList());

// Sequential
long sum1 = numbers.stream()
    .mapToLong(Integer::longValue)
    .sum();

// Parallel (potentially faster)
long sum2 = numbers.parallelStream()
    .mapToLong(Integer::longValue)
    .sum();
```

**Pitfalls**:

1. **Shared Mutable State** - ❌ WRONG:
```java
List<Integer> list = new ArrayList<>();  // Not thread-safe!
IntStream.range(0, 1000).parallel()
    .forEach(list::add);  // Race condition!
```

2. **Wrong Collector** - ❌ WRONG:
```java
// ArrayList.add() is not thread-safe
List<Integer> result = numbers.parallelStream()
    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
```

✅ **Correct**:
```java
List<Integer> result = numbers.parallelStream()
    .collect(Collectors.toList());  // Thread-safe collector
```

3. **I/O Bound Operations**:
```java
// ❌ BAD: Parallel doesn't help with I/O
files.parallelStream()
    .map(file -> readFromDisk(file))  // I/O bound
    .collect(Collectors.toList());
```

4. **Small Collections**:
```java
// ❌ Overhead > benefit for small lists
List.of(1, 2, 3).parallelStream()  // Overkill
    .map(n -> n * 2)
    .collect(Collectors.toList());
```

**Performance Tip**: Benchmark before using parallel streams. They're not always faster!

---

## Exception Handling (Questions 31-35)

### Q26: Checked vs Unchecked Exceptions. When to use which?

**Answer**:

| Feature                | Checked                       | Unchecked                                          |
| ---------------------- | ----------------------------- | -------------------------------------------------- |
| **Extends**            | `Exception`                   | `RuntimeException`                                 |
| **Compile-time check** | Yes (must handle)             | No                                                 |
| **Use case**           | Recoverable conditions        | Programming errors                                 |
| **Examples**           | `IOException`, `SQLException` | `NullPointerException`, `IllegalArgumentException` |

**Checked Exceptions**:
```java
// Must handle or declare
public void readFile(String path) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    // Use br
}

// Or catch
public void readFileSafe(String path) {
    try {
        BufferedReader br = new BufferedReader(new FileReader(path));
    } catch (IOException e) {
        // Handle
    }
}
```

**Unchecked Exceptions**:
```java
public void process(String input) {
    if (input == null) {
        throw new IllegalArgumentException("Input cannot be null");
    }
    // Process
}
```

**When to use Checked**:
- External resources (files, network, DB)
- Business validation failures
- Conditions the caller can reasonably recover from

**When to use Unchecked**:
- Programming errors (null, illegal arguments)
- Violations of invariants
- Errors that should not be caught (system errors)

**Modern Trend**: Prefer unchecked exceptions (Spring, Hibernate use them extensively).

---

### Q27: Explain try-with-resources. What is AutoCloseable?

**Answer**:

**try-with-resources** (Java 7+): Automatic resource management.

**Old Way** (verbose, error-prone):
```java
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("file.txt"));
    String line = br.readLine();
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (br != null) {
        try {
            br.close();  // Can also throw!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

**New Way** (clean):
```java
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    String line = br.readLine();
} catch (IOException e) {
    e.printStackTrace();
}  // br.close() called automatically
```

**Multiple Resources**:
```java
try (
    FileInputStream fis = new FileInputStream("in.txt");
    FileOutputStream fos = new FileOutputStream("out.txt")
) {
    // Use both
}  // Both closed in reverse order
```

**AutoCloseable Interface**:
```java
public class MyResource implements AutoCloseable {
    public void doWork() {
        System.out.println("Working...");
    }
    
    @Override
    public void close() {
        System.out.println("Cleaning up...");
    }
}

try (MyResource resource = new MyResource()) {
    resource.doWork();
}  // close() called automatically
```

**Interview Trick**: What if exception occurs in `close()`?
```java
try (Resource r = new Resource()) {
    throw new Exception("Primary");  // Thrown first
} catch (Exception e) {
    System.out.println(e.getMessage());  // "Primary"
    for (Throwable suppressed : e.getSuppressed()) {
        System.out.println(suppressed);  // Exception from close()
    }
}
```

---

### Q28: Custom Exceptions - Best practices?

**Answer**:

**Custom Exception Template**:
```java
// Checked exception
public class BusinessException extends Exception {
    private final ErrorCode errorCode;
    
    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BusinessException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

// Unchecked exception
public class InvalidOrderException extends RuntimeException {
    private final String orderId;
    
    public InvalidOrderException(String orderId, String message) {
        super(message);
        this.orderId = orderId;
    }
    
    public String getOrderId() {
        return orderId;
    }
}
```

**Best Practices**:

1. **Provide Context**:
```java
// ❌ BAD
throw new Exception("Error");

// ✅ GOOD
throw new OrderProcessingException(
    "Failed to process order " + orderId + " for customer " + customerId,
    ErrorCode.PAYMENT_FAILED
);
```

2. **Don't Swallow Exceptions**:
```java
// ❌ BAD
catch (IOException e) {
    // Silent failure
}

// ✅ GOOD
catch (IOException e) {
    logger.error("Failed to read file", e);
    throw new BusinessException("File processing failed", e);
}
```

3. **Use Specific Exceptions**:
```java
// ❌ BAD
catch (Exception e) {  // Too broad
    // ...
}

// ✅ GOOD
catch (FileNotFoundException e) {
    // Handle missing file
} catch (IOException e) {
    // Handle other I/O errors
}
```

4. **Exception Hierarchy**:
```java
public class PaymentException extends RuntimeException { }
    public class InsufficientFundsException extends PaymentException { }
    public class CardExpiredException extends PaymentException { }
    public class InvalidCardException extends PaymentException { }
```

---

## JVM Performance & Tuning (Questions 36-40)

### Q29: Common JVM flags for performance tuning?

**Answer**:

**Heap Size**:
```bash
# Initial heap size
-Xms2g

# Maximum heap size
-Xmx4g

# Recommended: Set both equal to avoid resizing
-Xms4g -Xmx4g
```

**GC Selection**:
```bash
# G1 GC (default Java 9+, balanced)
-XX:+UseG1GC

# Parallel GC (throughput-focused)
-XX:+UseParallelGC

# ZGC (ultra-low latency, Java 15+)
-XX:+UseZGC

# Shenandoah (low latency)
-XX:+UseShenandoahGC
```

**GC Tuning**:
```bash
# G1 target pause time
-XX:MaxGCPauseMillis=200

# Thread count for parallel GC
-XX:ParallelGCThreads=4

# Young generation size
-Xmn1g
```

**Metaspace** (Java 8+, replaces PermGen):
```bash
# Initial metaspace size
-XX:MetaspaceSize=256m

# Maximum metaspace size
-XX:MaxMetaspaceSize=512m
```

**GC Logging**:
```bash
# Java 9+
-Xlog:gc*:file=gc.log

# Java 8
-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log
```

**Other Useful Flags**:
```bash
# Dump heap on OutOfMemoryError
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/path/to/dumps

# Use compressed pointers (saves memory)
-XX:+UseCompressedOops

# String deduplication (saves memory in G1)
-XX:+UseStringDeduplication
```

**Example Production Setup**:
```bash
java -Xms4g -Xmx4g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/var/log/heapdumps \
     -Xlog:gc*:file=/var/log/gc.log \
     -jar application.jar
```

---

### Q30: How to diagnose and fix memory leaks?

**Answer**:

**Tools**:
1. **jmap**: Heap dump
   ```bash
   jmap -dump:format=b,file=heap.bin <pid>
   ```

2. **jstat**: GC statistics
   ```bash
   jstat -gcutil <pid> 1000  # Every second
   ```

3. **VisualVM / JProfiler**: Visual analysis

**Common Leak Patterns**:

1. **Static Collections**:
```java
// ❌ LEAK
public class Cache {
    private static List<Object> cache = new ArrayList<>();
    
    public void add(Object obj) {
        cache.add(obj);  // Never removed!
    }
}

// ✅ FIX
public class Cache {
    private static Map<String, WeakReference<Object>> cache = new WeakHashMap<>();
}
```

2. **Listeners Not Removed**:
```java
// ❌ LEAK
button.addActionListener(e -> { /* long-lived lambda */ });

// ✅ FIX
ActionListener listener = e -> { /* ... */ };
button.addActionListener(listener);
// Later: button.removeActionListener(listener);
```

3. **ThreadLocal Not Cleared**:
```java
// ❌ LEAK
private static ThreadLocal<HeavyObject> threadLocal = new ThreadLocal<>();

public void process() {
    threadLocal.set(new HeavyObject());
    // Never removed → leak in thread pool!
}

// ✅ FIX
try {
    threadLocal.set(new HeavyObject());
    // Use it
} finally {
    threadLocal.remove();  // Clean up!
}
```

4. **Unclosed Streams**:
```java
// ❌ LEAK
Stream<String> stream = Files.lines(Paths.get("file.txt"));
// Stream holds reference to file handle

// ✅ FIX
try (Stream<String> stream = Files.lines(Paths.get("file.txt"))) {
    stream.forEach(System.out::println);
}
```

**Diagnosis Steps**:
1. Monitor heap usage over time (should sawtooth, not monotonic increase)
2. Take heap dump when memory is high
3. Analyze with Eclipse MAT (Memory Analyzer Tool)
4. Look for:
   - Large object graphs
   - Duplicate strings
   - Retained heap by class

---

## Design Patterns (Questions 41-46)

### Q31: Implement Singleton pattern. What are the issues?

**Answer**:

**Eager Initialization** (simplest, thread-safe):
```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    
    private Singleton() {}  // Private constructor
    
    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```
**Drawback**: Created even if never used.

**Lazy Initialization** (not thread-safe):
```java
public class Singleton {
    private static Singleton instance;
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) {  // Race condition!
            instance = new Singleton();
        }
        return instance;
    }
}
```

**Thread-safe Lazy** (synchronized method - slow):
```java
public class Singleton {
    private static Singleton instance;
    
    private Singleton() {}
    
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```
**Drawback**: Locking every time (slow).

**Double-Checked Locking** (fast, but needs volatile):
```java
public class Singleton {
    private static volatile Singleton instance;  // volatile important!
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) {  // First check (no locking)
            synchronized (Singleton.class) {
                if (instance == null) {  // Second check (with lock)
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

**Initialization-on-demand Holder** (best lazy approach):
```java
public class Singleton {
    private Singleton() {}
    
    private static class Holder {
        private static final Singleton INSTANCE = new Singleton();
    }
    
    public static Singleton getInstance() {
        return Holder.INSTANCE;  // Lazy + thread-safe
    }
}
```
**Why it works**: Inner class loaded only when accessed.

**Enum Singleton** (preferred by Joshua Bloch):
```java
public enum Singleton {
    INSTANCE;
    
    public void doSomething() {
        // ...
    }
}

// Usage
Singleton.INSTANCE.doSomething();
```
**Benefits**: Thread-safe, serialization-safe, prevents reflection attacks.

---

### Q32: Factory Pattern vs Abstract Factory?

**Answer**:

**Factory Method** (one product hierarchy):
```java
// Product interface
interface Vehicle {
    void drive();
}

// Concrete products
class Car implements Vehicle {
    public void drive() { System.out.println("Driving car"); }
}

class Bike implements Vehicle {
    public void drive() { System.out.println("Riding bike"); }
}

// Factory
class VehicleFactory {
    public static Vehicle createVehicle(String type) {
        return switch (type) {
            case "car" -> new Car();
            case "bike" -> new Bike();
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }
}

// Usage
Vehicle vehicle = VehicleFactory.createVehicle("car");
vehicle.drive();
```

**Abstract Factory** (family of related products):
```java
// Abstract product families
interface Button {
    void render();
}

interface Checkbox {
    void render();
}

// Windows products
class WindowsButton implements Button {
    public void render() { System.out.println("Windows button"); }
}

class WindowsCheckbox implements Checkbox {
    public void render() { System.out.println("Windows checkbox"); }
}

// Mac products
class MacButton implements Button {
    public void render() { System.out.println("Mac button"); }
}

class MacCheckbox implements Checkbox {
    public void render() { System.out.println("Mac checkbox"); }
}

// Abstract factory
interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

// Concrete factories
class WindowsFactory implements GUIFactory {
    public Button createButton() { return new WindowsButton(); }
    public Checkbox createCheckbox() { return new WindowsCheckbox(); }
}

class MacFactory implements GUIFactory {
    public Button createButton() { return new MacButton(); }
    public Checkbox createCheckbox() { return new MacCheckbox(); }
}

// Usage
GUIFactory factory = new WindowsFactory();
Button button = factory.createButton();
Checkbox checkbox = factory.createCheckbox();
button.render();
checkbox.render();
```

**When to use**:
- **Factory Method**: Creating single type of objects
- **Abstract Factory**: Creating families of related objects (theme, platform)

---

### Q33: Implement Builder Pattern. Why use it?

**Answer**:

**Problem**: Too many constructor parameters
```java
// ❌ Telescoping constructors (hard to read)
public class User {
    public User(String name) { /* ... */ }
    public User(String name, int age) { /* ... */ }
    public User(String name, int age, String email) { /* ... */ }
    public User(String name, int age, String email, String phone) { /* ... */ }
}

// Usage: What do these parameters mean?
User user = new User("John", 30, "john@example.com", null);
```

**Solution**: Builder Pattern
```java
public class User {
    private final String name;      // Required
    private final int age;          // Required
    private final String email;     // Optional
    private final String phone;     // Optional
    private final String address;   // Optional
    
    // Private constructor
    private User(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.email = builder.email;
        this.phone = builder.phone;
        this.address = builder.address;
    }
    
    // Builder class
    public static class Builder {
        // Required parameters
        private final String name;
        private final int age;
        
        // Optional parameters
        private String email;
        private String phone;
        private String address;
        
        public Builder(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public User build() {
            // Validation
            if (name == null || name.isEmpty()) {
                throw new IllegalStateException("Name is required");
            }
            return new User(this);
        }
    }
    
    // Getters...
}

// Usage (fluent, readable)
User user = new User.Builder("John", 30)
    .email("john@example.com")
    .phone("123-456-7890")
    .build();
```

**Lombok Alternative**:
```java
@Builder
public class User {
    private final String name;
    private final int age;
    private final String email;
    private final String phone;
}

// Usage
User user = User.builder()
    .name("John")
    .age(30)
    .email("john@example.com")
    .build();
```

**Benefits**:
- Readable (named parameters)
- Immutable objects
- Flexible (optional parameters)
- Validation in one place

---

## Real-World Scenarios (Questions 47-50)

### Q34: Design a Thread-safe LRU Cache

**Answer**:

**Using LinkedHashMap**:
```java
public class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, V> cache;
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = Collections.synchronizedMap(
            new LinkedHashMap<K, V>(capacity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return size() > LRUCache.this.capacity;
                }
            }
        );
    }
    
    public V get(K key) {
        return cache.get(key);
    }
    
    public void put(K key, V value) {
        cache.put(key, value);
    }
}
```

**Custom Implementation** (more control):
```java
public class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final Node<K, V> head;
    private final Node<K, V> tail;
    private final ReentrantReadWriteLock lock;
    
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev, next;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }
    
    public V get(K key) {
        lock.readLock().lock();
        try {
            Node<K, V> node = cache.get(key);
            if (node == null) return null;
            
            // Move to front (most recently used)
            moveToFront(node);
            return node.value;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            Node<K, V> node = cache.get(key);
            
            if (node != null) {
                // Update existing
                node.value = value;
                moveToFront(node);
            } else {
                // Add new
                node = new Node<>(key, value);
                cache.put(key, node);
                addToFront(node);
                
                // Remove LRU if over capacity
                if (cache.size() > capacity) {
                    Node<K, V> lru = removeLast();
                    cache.remove(lru.key);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private void moveToFront(Node<K, V> node) {
        removeNode(node);
        addToFront(node);
    }
    
    private void addToFront(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private Node<K, V> removeLast() {
        Node<K, V> last = tail.prev;
        removeNode(last);
        return last;
    }
}
```

---

### Q35: How to prevent race conditions in multi-threaded environment?

**Answer**:

**Problem**: Race Condition
```java
public class Counter {
    private int count = 0;
    
    public void increment() {
        count++;  // NOT atomic! (read-modify-write)
    }
}

// Two threads calling increment() simultaneously can lose updates
```

**Solutions**:

1. **synchronized**:
```java
public class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
}
```

2. **Atomic Classes**:
```java
public class Counter {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();  // Atomic
    }
}
```

3. **ReadWriteLock** (optimized for read-heavy):
```java
public class Counter {
    private int count = 0;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public void increment() {
        lock.writeLock().lock();
        try {
            count++;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public int get() {
        lock.readLock().lock();
        try {
            return count;
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

4. **Concurrent Collections**:
```java
// Instead of ArrayList + synchronized
List<String> list = Collections.synchronizedList(new ArrayList<>());

// Use concurrent collection
List<String> list = new CopyOnWriteArrayList<>();

// Instead of HashMap + synchronized
Map<String, Integer> map = new ConcurrentHashMap<>();
```

**Best Practices**:
- Minimize synchronized scope
- Use immutable objects when possible
- Prefer `java.util.concurrent` classes
- Avoid nested locks (deadlock risk)

---

## Summary

**Extended Coverage (90+ Questions Total)**:
1. ✅ JVM Architecture & Memory Management (10)
2. ✅ Object-Oriented Principles (10)
3. ✅ Collections Framework (10)
4. ✅ Streams & Functional Programming (10)
5. ✅ Exception Handling (5)
6. ✅ JVM Performance Tuning (5)
7. ✅ Design Patterns (6)
8. ✅ Real-world Scenarios (5)
9. ✅ Multithreading Deep Dive (10)
10. ✅ Modern Java Features (10)
11. ✅ Bonus Tricky Questions (9)

**Preparation Strategy**:
- **Week 1**: Core Java (Memory, OOP, Collections)
- **Week 2**: Concurrency & Streams
- **Week 3**: Design Patterns & Best Practices
- **Week 4**: Mock interviews & Code reviews

**Interview Tips**:
1. **Start Simple**: Explain basic concept first, then dive deep
2. **Ask Clarifications**: "Are we optimizing for space or time?"
3. **Think Aloud**: Explain your thought process
4. **Know Trade-offs**: Every solution has pros/cons
5. **Code Quality**: Write clean, production-ready code
6. **Test Cases**: Always mention edge cases

**Red Flags to Avoid**:
- ❌ Not knowing difference between `==` and `equals()`
- ❌ Misunderstanding pass-by-value vs pass-by-reference
- ❌ Poor exception handling (swallowing exceptions)
- ❌ Not understanding thread safety
- ❌ Overusing synchronized (performance issues)

Good luck with your interviews! 🚀
