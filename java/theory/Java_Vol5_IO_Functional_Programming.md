# Java Volume 5: I/O, Serialization & Functional Programming

> **Focus**: File I/O, NIO, Serialization, Lambda Expressions, Streams API, Method References

---

## 1. File I/O (java.io)

### Reading Files

**BufferedReader (Text Files)**
```java
// Try-with-resources (auto-closes)
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// Read all lines at once
List<String> lines = Files.readAllLines(Paths.get("file.txt"));
```

**FileInputStream (Binary Files)**
```java
try (FileInputStream fis = new FileInputStream("image.jpg");
     FileOutputStream fos = new FileOutputStream("copy.jpg")) {
    
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = fis.read(buffer)) != -1) {
        fos.write(buffer, 0, bytesRead);
    }
}
```

### Writing Files

```java
// BufferedWriter
try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
    bw.write("Hello World");
    bw.newLine();
    bw.write("Second Line");
}

// PrintWriter
try (PrintWriter pw = new PrintWriter("output.txt")) {
    pw.println("Line 1");
    pw.printf("Number: %d%n", 42);
}

// Files.write (Java 7+)
List<String> lines = Arrays.asList("Line 1", "Line 2");
Files.write(Paths.get("output.txt"), lines);
```

---

## 2. NIO (New I/O) - java.nio

### Path and Files

```java
import java.nio.file.*;

// Create Path
Path path = Paths.get("folder", "file.txt");
Path absolute = path.toAbsolutePath();

// File operations
boolean exists = Files.exists(path);
boolean isDirectory = Files.isDirectory(path);
long size = Files.size(path);

// Create directories
Files.createDirectory(Paths.get("newFolder"));
Files.createDirectories(Paths.get("path/to/nested/folders"));

// Copy/Move/Delete
Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
Files.delete(path);

// Read/Write
String content = Files.readString(Paths.get("file.txt"));  // Java 11+
Files.writeString(Paths.get("output.txt"), "content");
```

### Directory Listing

```java
// List files in directory
try (Stream<Path> paths = Files.list(Paths.get("folder"))) {
    paths.filter(Files::isRegularFile)
         .forEach(System.out::println);
}

// Walk directory tree
try (Stream<Path> paths = Files.walk(Paths.get("folder"))) {
    paths.filter(p -> p.toString().endsWith(".java"))
         .forEach(System.out::println);
}
```

---

## 3. Serialization

### Serializable Interface

```java
import java.io.*;

class User implements Serializable {
    private static final long serialVersionUID = 1L;  // Version control
    
    private String name;
    private int age;
    private transient String password;  // Not serialized
    
    public User(String name, int age, String password) {
        this.name = name;
        this.age = age;
        this.password = password;
    }
}

// Serialize
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("user.ser"))) {
    User user = new User("Alice", 30, "secret");
    oos.writeObject(user);
}

// Deserialize
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("user.ser"))) {
    User user = (User) ois.readObject();
    System.out.println(user.getName());  // "Alice"
    System.out.println(user.getPassword());  // null (transient)
}
```

**Key Points:**
- `serialVersionUID`: Version compatibility
- `transient`: Skip field serialization
- Static fields not serialized
- Parent must be Serializable for inheritance

### Custom Serialization

```java
class User implements Serializable {
    private String name;
    private transient String sensitiveData;
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        // Custom serialization for sensitiveData
        String encrypted = encrypt(sensitiveData);
        oos.writeObject(encrypted);
    }
    
    private void readObject(ObjectInputStream ois) 
            throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        // Custom deserialization
        String encrypted = (String) ois.readObject();
        sensitiveData = decrypt(encrypted);
    }
}
```

---

## 4. Lambda Expressions

### Syntax

```java
// Traditional anonymous class
Runnable r1 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Running");
    }
};

// Lambda
Runnable r2 = () -> System.out.println("Running");

// With parameters
Comparator<String> cmp1 = (s1, s2) -> s1.length() - s2.length();

// Multi-line
Consumer<String> printer = s -> {
    String upper = s.toUpperCase();
    System.out.println(upper);
};
```

### Functional Interfaces

```java
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}

// Usage
Calculator add = (a, b) -> a + b;
Calculator multiply = (a, b) -> a * b;

System.out.println(add.calculate(5, 3));       // 8
System.out.println(multiply.calculate(5, 3));  // 15
```

**Built-in Functional Interfaces:**

Java provides several built-in functional interfaces in `java.util.function` package:

| Interface           | Method Signature         | Use Case                                  |
| ------------------- | ------------------------ | ----------------------------------------- |
| `Predicate<T>`      | `boolean test(T t)`      | Filtering or checking a condition         |
| `Function<T, R>`    | `R apply(T t)`           | Transforming input into different output  |
| `UnaryOperator<T>`  | `T apply(T t)`           | Transforming input into same type         |
| `Consumer<T>`       | `void accept(T t)`       | Taking action without returning anything  |
| `Supplier<T>`       | `T get()`                | Providing value without taking input      |
| `BiFunction<T,U,R>` | `R apply(T t, U u)`      | Two inputs, transform to different output |
| `BinaryOperator<T>` | `T apply(T t, T t)`      | Two inputs of same type, return same type |
| `BiConsumer<T,U>`   | `void accept(T t, U u)`  | Two inputs, perform action                |
| `BiPredicate<T,U>`  | `boolean test(T t, U u)` | Two inputs, test condition                |

### Detailed Examples

```java
// 1. Predicate<T> - Testing/Filtering
Predicate<String> isEmpty = s -> s.isEmpty();
Predicate<Integer> isEven = n -> n % 2 == 0;
Predicate<String> startsWithA = s -> s.startsWith("A");

// Combining predicates
Predicate<String> longAndStartsWithA = startsWithA.and(s -> s.length() > 5);
Predicate<Integer> evenOrNegative = isEven.or(n -> n < 0);
Predicate<String> notEmpty = isEmpty.negate();

// Usage in streams
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
List<Integer> evens = numbers.stream()
    .filter(isEven)
    .collect(Collectors.toList());  // [2, 4, 6]

// 2. Function<T, R> - Transformation to different type
Function<String, Integer> length = String::length;
Function<Integer, String> toString = Object::toString;
Function<String, String> toUpper = String::toUpperCase;

// Chaining functions
Function<String, Integer> lengthOfUpper = toUpper.andThen(length);
Function<String, Integer> composedLength = length.compose(toUpper);

// Usage
int len = length.apply("Hello");  // 5
List<Integer> lengths = Arrays.asList("a", "bb", "ccc").stream()
    .map(length)
    .collect(Collectors.toList());  // [1, 2, 3]

// 3. UnaryOperator<T> - Transformation to same type
UnaryOperator<Integer> square = n -> n * n;
UnaryOperator<String> trim = String::trim;
UnaryOperator<Double> increment = d -> d + 1.0;

// Usage
int squared = square.apply(5);  // 25
List<Integer> squares = numbers.stream()
    .map(square)
    .collect(Collectors.toList());  // [1, 4, 9, 16, 25, 36]

// 4. Consumer<T> - Processing without return
Consumer<String> printer = System.out::println;
Consumer<String> logger = s -> System.out.println("LOG: " + s);
Consumer<Integer> doubler = n -> System.out.println(n * 2);

// Chaining consumers
Consumer<String> printAndLog = printer.andThen(logger);

// Usage
Arrays.asList("A", "B", "C").forEach(printer);
numbers.forEach(doubler);  // Prints: 2, 4, 6, 8, 10, 12

// 5. Supplier<T> - Generating values
Supplier<Double> random = Math::random;
Supplier<String> uuid = () -> UUID.randomUUID().toString();
Supplier<LocalDateTime> now = LocalDateTime::now;
Supplier<List<String>> listFactory = ArrayList::new;

// Usage
Double randomValue = random.get();
String id = uuid.get();
List<String> randomList = Stream.generate(random)
    .limit(5)
    .map(Object::toString)
    .collect(Collectors.toList());

// 6. BiFunction<T, U, R> - Two inputs, different output
BiFunction<String, String, Integer> totalLength = (s1, s2) -> s1.length() + s2.length();
BiFunction<Integer, Integer, String> sumAsString = (a, b) -> String.valueOf(a + b);
BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);

// Usage
int combined = totalLength.apply("Hello", "World");  // 10
String result = repeat.apply("Hi", 3);  // "HiHiHi"

// 7. BinaryOperator<T> - Two inputs of same type, same type output
BinaryOperator<Integer> add = (a, b) -> a + b;
BinaryOperator<String> concat = (a, b) -> a + b;
BinaryOperator<Integer> max = Integer::max;
BinaryOperator<Integer> min = Integer::min;

// Usage in reduce
int sum = numbers.stream()
    .reduce(0, add);  // 21
Optional<Integer> maximum = numbers.stream()
    .reduce(max);  // Optional[6]

// 8. BiConsumer<T, U> - Two inputs, perform action
BiConsumer<String, Integer> printWithIndex = (s, i) -> 
    System.out.println(i + ": " + s);
BiConsumer<String, String> mapPutter = (k, v) -> 
    System.out.println(k + " = " + v);

// Usage
Map<String, String> map = new HashMap<>();
map.put("key1", "value1");
map.put("key2", "value2");
map.forEach(mapPutter);  // key1 = value1, key2 = value2

// 9. BiPredicate<T, U> - Two inputs, test condition
BiPredicate<String, Integer> lengthEquals = (s, len) -> s.length() == len;
BiPredicate<Integer, Integer> greaterThan = (a, b) -> a > b;

// Usage
boolean result1 = lengthEquals.test("Hello", 5);  // true
boolean result2 = greaterThan.test(10, 5);  // true
```

### Specialized Functional Interfaces for Primitives

To avoid boxing/unboxing overhead, Java provides primitive-specialized versions:

```java
// For int
IntPredicate isPositive = n -> n > 0;
IntFunction<String> intToString = String::valueOf;
IntConsumer printInt = System.out::println;
IntSupplier randomInt = () -> new Random().nextInt();
IntUnaryOperator doubleIt = n -> n * 2;
IntBinaryOperator multiply = (a, b) -> a * b;

// For long
LongPredicate isEven = n -> n % 2 == 0;
LongFunction<String> longToString = String::valueOf;
LongSupplier timestamp = System::currentTimeMillis;

// For double
DoublePredicate isPositive = d -> d > 0.0;
DoubleFunction<String> doubleToString = String::valueOf;
DoubleSupplier random = Math::random;

// Usage - no boxing!
IntStream.range(1, 10)
    .filter(isPositive)
    .map(doubleIt)
    .forEach(printInt);
```

**Interview Tip**: Know when to use which functional interface and understand the primitive specializations for performance-critical code.


---

## 5. Streams API

### Creating Streams

```java
// From collection
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream1 = list.stream();

// From array
String[] array = {"a", "b", "c"};
Stream<String> stream2 = Arrays.stream(array);

// Empty stream
Stream<String> empty = Stream.empty();

// Range
IntStream numbers = IntStream.range(1, 10);  // 1 to 9

// Infinite stream
Stream<Double> randoms = Stream.generate(Math::random);
Stream<Integer> naturals = Stream.iterate(1, n -> n + 1);
```

### Intermediate Operations

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

// filter
names.stream()
     .filter(name -> name.length() > 4)
     .forEach(System.out::println);  // Alice, Charlie, David

// map
names.stream()
     .map(String::toUpperCase)
     .forEach(System.out::println);  // ALICE, BOB, CHARLIE, DAVID

// flatMap
List<List<Integer>> nested = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4)
);
nested.stream()
      .flatMap(Collection::stream)
      .forEach(System.out::println);  // 1, 2, 3, 4

// distinct
Stream.of(1, 2, 2, 3, 3, 3)
      .distinct()
      .forEach(System.out::println);  // 1, 2, 3

// sorted
names.stream()
     .sorted()
     .forEach(System.out::println);  // Alice, Bob, Charlie, David

// limit & skip
names.stream()
     .skip(1)
     .limit(2)
     .forEach(System.out::println);  // Bob, Charlie

// peek (debugging)
names.stream()
     .peek(s -> System.out.println("Processing: " + s))
     .map(String::toUpperCase)
     .peek(s -> System.out.println("Mapped: " + s))
     .collect(Collectors.toList());
```

### Terminal Operations

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// forEach
numbers.forEach(System.out::println);

// collect
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// reduce
int sum = numbers.stream()
    .reduce(0, Integer::sum);  // 15

Optional<Integer> max = numbers.stream()
    .reduce(Integer::max);

// count
long count = numbers.stream()
    .filter(n -> n > 2)
    .count();  // 3

// anyMatch, allMatch, noneMatch
boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);  // true
boolean allPositive = numbers.stream().allMatch(n -> n > 0);  // true
boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);  // true

// findFirst, findAny
Optional<Integer> first = numbers.stream().findFirst();  // Optional[1]
Optional<Integer> any = numbers.stream().findAny();

// min, max
Optional<Integer> min = numbers.stream().min(Integer::compareTo);
Optional<Integer> max = numbers.stream().max(Integer::compareTo);
```

### Collectors

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// toList
List<String> list = names.stream().collect(Collectors.toList());

// toSet
Set<String> set = names.stream().collect(Collectors.toSet());

// toMap
Map<String, Integer> map = names.stream()
    .collect(Collectors.toMap(
        name -> name,           // key
        String::length          // value
    ));

// joining
String joined = names.stream()
    .collect(Collectors.joining(", "));  // "Alice, Bob, Charlie"

// groupingBy
Map<Integer, List<String>> byLength = names.stream()
    .collect(Collectors.groupingBy(String::length));
// {3=[Bob], 5=[Alice], 7=[Charlie]}

// partitioningBy
Map<Boolean, List<Integer>> partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));
// {false=[1,3,5], true=[2,4]}

// counting
Long count = names.stream()
    .collect(Collectors.counting());

// summarizingInt
IntSummaryStatistics stats = numbers.stream()
    .collect(Collectors.summarizingInt(Integer::intValue));
System.out.println(stats.getAverage());  // 3.0
System.out.println(stats.getMax());      // 5
```

---

## 6. Method References

### Types of Method References

```java
// 1. Static method reference
Function<String, Integer> parseInt = Integer::parseInt;
int num = parseInt.apply("123");  // 123

// 2. Instance method on particular object
String prefix = "Hello ";
Function<String, String> concat = prefix::concat;
String result = concat.apply("World");  // "Hello World"

// 3. Instance method on arbitrary object
Function<String, String> toUpper = String::toUpperCase;
String upper = toUpper.apply("hello");  // "HELLO"

List<String> names = Arrays.asList("alice", "bob");
names.stream()
     .map(String::toUpperCase)
     .forEach(System.out::println);

// 4. Constructor reference
Supplier<List<String>> listFactory = ArrayList::new;
List<String> list = listFactory.get();

Function<Integer, List<String>> sizedListFactory = ArrayList::new;
List<String> sizedList = sizedListFactory.apply(10);
```

---

## 7. Optional

### Creating Optional

```java
// Of value (throws if null)
Optional<String> opt1 = Optional.of("Hello");

// Nullable
Optional<String> opt2 = Optional.ofNullable(getValue());

// Empty
Optional<String> opt3 = Optional.empty();
```

### Using Optional

```java
Optional<String> optional = findUser();

// Bad - defeats purpose
if (optional.isPresent()) {
    String value = optional.get();
}

// Good - functional style
optional.ifPresent(value -> System.out.println(value));

String result = optional.orElse("default");
String result = optional.orElseGet(() -> computeDefault());
String result = optional.orElseThrow(() -> new Exception("Not found"));

// Chaining
String upperCase = optional
    .map(String::toUpperCase)
    .filter(s -> s.length() > 5)
    .orElse("SHORT");

// flatMap for nested Optionals
Optional<Optional<String>> nested = Optional.of(Optional.of("value"));
Optional<String> flat = nested.flatMap(o -> o);
```

---

## 8. Advanced Stream Operations

### Parallel Streams

```java
List<Integer> numbers = IntStream.rangeClosed(1, 1000000)
    .boxed()
    .collect(Collectors.toList());

// Sequential
long sum1 = numbers.stream()
    .mapToLong(Integer::longValue)
    .sum();

// Parallel (uses ForkJoinPool)
long sum2 = numbers.parallelStream()
    .mapToLong(Integer::longValue)
    .sum();
```

**When to use parallel:**
- Large dataset (> 10,000 elements)
- CPU-intensive operations
- Independent operations (no shared state)

### Custom Collectors

```java
// Immutable list collector
Collector<String, ?, List<String>> toImmutableList = Collector.of(
    ArrayList::new,              // supplier
    List::add,                   // accumulator
    (left, right) -> {           // combiner
        left.addAll(right);
        return left;
    },
    Collections::unmodifiableList  // finisher
);

List<String> immutable = Stream.of("a", "b", "c")
    .collect(toImmutableList);
```

---

## 9. Common Patterns

### Filter-Map-Collect

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

List<Integer> nameLengths = names.stream()
    .filter(name -> name.startsWith("A"))
    .map(String::length)
    .collect(Collectors.toList());
```

### GroupBy with Transformation

```java
Map<Character, List<String>> byFirstLetter = names.stream()
    .collect(Collectors.groupingBy(
        name -> name.charAt(0),
        Collectors.mapping(String::toUpperCase, Collectors.toList())
    ));
```

### Flattening Nested Structures

```java
class Department {
    List<Employee> employees;
}

List<Department> departments = getDepartments();

List<Employee> allEmployees = departments.stream()
    .flatMap(dept -> dept.employees.stream())
    .collect(Collectors.toList());
```

---

## Interview Tips

### Common Questions
1. **Difference between map() and flatMap()?**
   - map: 1-to-1 transformation
   - flatMap: 1-to-many + flattening

2. **Intermediate vs Terminal operations?**
   - Intermediate: return Stream (lazy)
   - Terminal: return result (trigger processing)

3. **When to use parallel streams?**
   - Large data, CPU-intensive, independent tasks

4. **How to handle IOException in lambda?**
   - Wrap in try-catch or create helper method

### Best Practices
- ✅ Use method references when possible
- ✅ Prefer functional style (no side effects)
- ✅ Always close streams (try-with-resources)
- ✅ Use Optional for return types, not parameters
- ✅ Avoid parallel streams for small collections
- ❌ Don't modify collection while streaming
- ❌ Don't reuse streams
