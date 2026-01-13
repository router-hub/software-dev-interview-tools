# Java Volume 3: Generics, Collections & Modern Java Features

> **Focus**: Generics, Arrays, Comparison, Records, Pattern Matching, and Java 14-21 Features

---

## 1. Generics - Type Safety at Compile Time

### Why Generics?

**Without Generics** (Pre-Java 5):
```java
List list = new ArrayList();
list.add("String");
list.add(123);  // Runtime disaster waiting to happen

String s = (String) list.get(1);  // ClassCastException at runtime!
```

**With Generics**:
```java
List<String> list = new ArrayList<>();
list.add("String");
// list.add(123);  // Compile error - caught early!

String s = list.get(0);  // No casting needed
```

### Generic Classes

```java
public class Box<T> {
    private T content;
    
    public void set(T content) {
        this.content = content;
    }
    
    public T get() {
        return content;
    }
}

// Usage
Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String value = stringBox.get();  // No cast needed

Box<Integer> intBox = new Box<>();
intBox.set(42);
```

### Generic Methods

```java
public class Utils {
    // Generic method
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.println(element);
        }
    }
    
    // Bounded type parameter
    public static <T extends Number> double sum(T[] numbers) {
        double total = 0;
        for (T num : numbers) {
            total += num.doubleValue();
        }
        return total;
    }
}

// Usage
Integer[] nums = {1, 2, 3};
Utils.printArray(nums);
double total = Utils.sum(nums);
```

### Wildcards

```java
// Upper bounded wildcard (? extends Type)
public void processShapes(List<? extends Shape> shapes) {
    for (Shape shape : shapes) {
        shape.draw();  // Can read as Shape
        // shapes.add(new Circle());  // ERROR: Cannot add
    }
}

// Lower bounded wildcard (? super Type)
public void addCircles(List<? super Circle> list) {
    list.add(new Circle());  // Can add Circle or subclasses
    // Circle c = list.get(0);  // ERROR: Don't know exact type
}

// Unbounded wildcard (?)
public void printList(List<?> list) {
    for (Object obj : list) {  // Can only read as Object
        System.out.println(obj);
    }
}
```

### Type Erasure (Interview Critical)

```java
// At compile time
List<String> strings = new ArrayList<>();
List<Integer> integers = new ArrayList<>();

// At runtime (after type erasure)
// Both become: List objects
// Cannot do: if (strings instanceof List<String>)  // ERROR
// Can do: if (strings instanceof List)  // OK
```

---

## 2. Arrays

### Array Basics

```java
// Declaration + Creation
int[] numbers = new int[5];  // [0, 0, 0, 0, 0]
String[] names = new String[3];  // [null, null, null]

// Declaration + Initialization
int[] primes = {2, 3, 5, 7, 11};
String[] days = {"Mon", "Tue", "Wed"};

// Anonymous array
processArray(new int[]{1, 2, 3, 4, 5});
```

### Multidimensional Arrays

```java
// 2D array
int[][] matrix = new int[3][4];  // 3 rows, 4 columns

// Jagged array (different row sizes)
int[][] jagged = {
    {1, 2},
    {3, 4, 5},
    {6}
};

// 3D array
int[][][] cube = new int[3][3][3];
```

### Arrays Class Utilities

```java
import java.util.Arrays;

int[] numbers = {5, 2, 8, 1, 9};

// Sorting
Arrays.sort(numbers);  // [1, 2, 5, 8, 9]

// Binary search (array MUST be sorted)
int index = Arrays.binarySearch(numbers, 5);  // 2

// Filling
Arrays.fill(numbers, 0);  // [0, 0, 0, 0, 0]

// Copying
int[] copy = Arrays.copyOf(numbers, 10);  // Extended to length 10
int[] range = Arrays.copyOfRange(numbers, 1, 4);  // [index 1 to 3]

// Comparison
int[] a = {1, 2, 3};
int[] b = {1, 2, 3};
System.out.println(a == b);           // false (different objects)
System.out.println(Arrays.equals(a, b));  // true (same content)

// String representation
System.out.println(Arrays.toString(numbers));
```

### Array vs ArrayList

| Feature     | Array                 | ArrayList            |
| ----------- | --------------------- | -------------------- |
| Size        | Fixed                 | Dynamic              |
| Type        | Primitives or Objects | Only Objects         |
| Performance | Faster                | Slower (autoboxing)  |
| Methods     | Few                   | Many utility methods |
| Syntax      | `arr[0]`              | `list.get(0)`        |

---

## 3. Comparison Interfaces

### Comparable - Natural Ordering

```java
public class Student implements Comparable<Student> {
    private String name;
    private int age;
    
    @Override
    public int compareTo(Student other) {
        // Natural ordering: by name
        return this.name.compareTo(other.name);
        
        // Alternative: by age
        // return Integer.compare(this.age, other.age);
    }
}

// Usage
List<Student> students = new ArrayList<>();
students.add(new Student("Bob", 20));
students.add(new Student("Alice", 22));

Collections.sort(students);  // Uses compareTo()
students.sort(null);         // Also uses compareTo()
```

### Comparator - Custom Ordering

```java
// Multiple comparison strategies
public class Student {
    private String name;
    private int age;
    private double gpa;
    
    // Getters...
}

// Comparator 1: By Age
Comparator<Student> byAge = new Comparator<Student>() {
    @Override
    public int compare(Student s1, Student s2) {
        return Integer.compare(s1.getAge(), s2.getAge());
    }
};

// Comparator 2: By GPA (Lambda)
Comparator<Student> byGpa = (s1, s2) -> Double.compare(s1.getGpa(), s2.getGpa());

// Comparator 3: Method Reference
Comparator<Student> byName = Comparator.comparing(Student::getName);

// Chaining comparators
Comparator<Student> multi = Comparator
    .comparing(Student::getGpa)
    .thenComparing(Student::getAge)
    .thenComparing(Student::getName);

// Usage
Collections.sort(students, byAge);
students.sort(byGpa);
```

### Comparable vs Comparator

| Aspect    | Comparable               | Comparator                           |
| --------- | ------------------------ | ------------------------------------ |
| Location  | Inside class             | Separate class/lambda                |
| Method    | `compareTo(T)`           | `compare(T, T)`                      |
| Instances | Single (natural order)   | Multiple (custom orders)             |
| Sorting   | `Collections.sort(list)` | `Collections.sort(list, comparator)` |

---

## 4. Records (Java 14+)

### Traditional POJO vs Record

**Old Way** (50+ lines):
```java
public class Point {
    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return "Point{x=" + x + ", y=" + y + "}";
    }
}
```

**New Way** (1 line):
```java
public record Point(int x, int y) {}
```

**Automatically provides:**
- Constructor: `new Point(10, 20)`
- Getters: `point.x()`, `point.y()` (note: no "get" prefix)
- `equals()`, `hashCode()`, `toString()`
- All fields are `final` (immutable)

### Custom Record Features

```java
public record Student(String name, int age, double gpa) {
    // Compact constructor (validation)
    public Student {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        if (gpa < 0 || gpa > 4.0) {
            throw new IllegalArgumentException("Invalid GPA");
        }
    }
    
    // Additional methods
    public boolean isHonorStudent() {
        return gpa >= 3.5;
    }
    
    // Static factory method
    public static Student createDefault() {
        return new Student("Unknown", 18, 0.0);
    }
}
```

---

## 5. Pattern Matching

### Pattern Matching for instanceof (Java 16+)

**Old Way**:
```java
if (obj instanceof String) {
    String str = (String) obj;  // Explicit cast
    System.out.println(str.length());
}
```

**New Way**:
```java
if (obj instanceof String str) {  // Pattern variable
    System.out.println(str.length());  // Ready to use
}

// With logical operators
if (obj instanceof String str && str.length() > 5) {
    System.out.println("Long string: " + str);
}
```

### Pattern Matching for switch (Java 21+)

```java
public String describe(Object obj) {
    return switch (obj) {
        case null -> "It's null";
        case String s -> "String of length " + s.length();
        case Integer i -> "Integer: " + i;
        case Long l -> "Long: " + l;
        case Double d -> "Double: " + d;
        case int[] arr -> "Int array of length " + arr.length;
        default -> "Unknown type";
    };
}
```

### Guarded Patterns

```java
public String categorize(Object obj) {
    return switch (obj) {
        case String s when s.length() < 5 -> "Short string";
        case String s when s.length() >= 5 -> "Long string";
        case Integer i when i < 0 -> "Negative";
        case Integer i when i >= 0 -> "Positive";
        default -> "Other";
    };
}
```

### Record Patterns (Java 21+)

```java
record Point(int x, int y) {}

public void process(Object obj) {
    if (obj instanceof Point(int x, int y)) {
        System.out.println("X: " + x + ", Y: " + y);  // Deconstructed!
    }
}

// In switch
public String describePoint(Object obj) {
    return switch (obj) {
        case Point(int x, int y) when x == 0 && y == 0 -> "Origin";
        case Point(int x, int y) when x == 0 -> "On Y-axis";
        case Point(int x, int y) when y == 0 -> "On X-axis";
        case Point(int x, int y) -> "Point at (" + x + ", " + y + ")";
        default -> "Not a point";
    };
}
```

---

## 6. Switch Expressions (Java 14+)

### Traditional Switch vs Expression

**Old Switch**:
```java
String day = "MONDAY";
String type;
switch (day) {
    case "MONDAY":
    case "TUESDAY":
    case "WEDNESDAY":
    case "THURSDAY":
    case "FRIDAY":
        type = "Weekday";
        break;
    case "SATURDAY":
    case "SUNDAY":
        type = "Weekend";
        break;
    default:
        type = "Unknown";
}
```

**Switch Expression**:
```java
String type = switch (day) {
    case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "Weekday";
    case "SATURDAY", "SUNDAY" -> "Weekend";
    default -> "Unknown";
};
```

### Using yield for Multi-line Cases

```java
int numDays = switch (month) {
    case "FEBRUARY" -> {
        boolean leapYear = /* check */;
        yield leapYear ? 29 : 28;
    }
    case "APRIL", "JUNE", "SEPTEMBER", "NOVEMBER" -> 30;
    default -> 31;
};
```

---

## 7. Loops and Iteration

### for Loop Variations

```java
// Traditional for
for (int i = 0; i < 10; i++) {
    System.out.println(i);
}

// Enhanced for (foreach)
int[] numbers = {1, 2, 3, 4, 5};
for (int num : numbers) {
    System.out.println(num);
}

// Multiple variables
for (int i = 0, j = 10; i < j; i++, j--) {
    System.out.println(i + " " + j);
}
```

### while and do-while

```java
// while - may not execute at all
int i = 0;
while (i < 5) {
    System.out.println(i);
    i++;
}

// do-while - executes at least once
int j = 0;
do {
    System.out.println(j);
    j++;
} while (j < 5);
```

### Loop Control

```java
// break - exits loop
for (int i = 0; i < 10; i++) {
    if (i == 5) break;  // Stop at 5
    System.out.println(i);
}

// continue - skips iteration
for (int i = 0; i < 10; i++) {
    if (i % 2 == 0) continue;  // Skip even numbers
    System.out.println(i);  // Prints odd only
}

// Labeled break (nested loops)
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) break outer;  // Break out of both loops
        System.out.println(i + "," + j);
    }
}
```

---

## 8. Modern Java Best Practices

### Var Usage

```java
// GOOD: Clear type from context
var list = new ArrayList<String>();
var message = "Hello";
var count = 42;

// BAD: Obscure types
var data = getData();  // What type is this?
var result = process();  // Unclear
```

### Factory Methods

```java
// Immutable collections (Java 9+)
List<String> list = List.of("A", "B", "C");  // Immutable
Set<Integer> set = Set.of(1, 2, 3);          // Immutable
Map<String, Integer> map = Map.of("A", 1, "B", 2);  // Immutable

// Cannot modify
// list.add("D");  // UnsupportedOperationException
```

### Try-with-Resources (Java 7+)

```java
// Old way - manual cleanup
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("file.txt"));
    String line = br.readLine();
} finally {
    if (br != null) br.close();
}

// New way - automatic cleanup
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    String line = br.readLine();
}  // Automatically closed
```

---

## Interview Cheat Sheet

### Common Mistakes

1. ❌ **Raw types**: `List list = new ArrayList();`
   ✅ **Use generics**: `List<String> list = new ArrayList<>();`

2. ❌ **Array comparison**: `arr1 == arr2`
   ✅ **Content comparison**: `Arrays.equals(arr1, arr2)`

3. ❌ **Modifying while iterating**:
   ```java
   for (String s : list) {
       list.remove(s);  // ConcurrentModificationException
   }
   ```
   ✅ **Use Iterator**:
   ```java
   Iterator<String> it = list.iterator();
   while (it.hasNext()) {
       String s = it.next();
       it.remove();
   }
   ```

4. ❌ **Sorting without Comparable/Comparator**
   ✅ **Implement one of them**

5. ❌ **Using == for wrapper comparison**:
   ```java
   Integer a = 128;
   Integer b = 128;
   System.out.println(a == b);  // false (outside cache range)
   ```
   ✅ **Use equals()**: `a.equals(b)`

### Key Takeaways

1. **Generics** provide compile-time type safety
2. **Arrays** are fixed-size but fast
3. **Comparable** for natural order, **Comparator** for custom
4. **Records** eliminate boilerplate for data carriers
5. **Pattern matching** reduces casting and improves readability
6. **Switch expressions** are cleaner and safer than statements
7. **Modern Java** (14-21) has powerful features - use them!
