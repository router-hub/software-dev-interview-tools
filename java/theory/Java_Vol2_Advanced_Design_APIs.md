# Java Volume 2: Advanced Class Design & Essential APIs

> **Focus**: Inheritance, Polymorphism, Object Class, String/Date APIs, Enums, Abstract Classes, and Interfaces

---

## 1. Inheritance and Polymorphism

### Class Inheritance

```java
// Parent class (Superclass)
public class Product {
    protected String name;
    protected double price;
    
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    public double getPrice() {
        return price;
    }
    
    public double calculateDiscount() {
        return price * 0.9;  // 10% discount
    }
}

// Child class (Subclass)
public class Food extends Product {
    private LocalDate bestBefore;
    
    public Food(String name, double price, LocalDate bestBefore) {
        super(name, price);  // Call parent constructor
        this.bestBefore = bestBefore;
    }
    
    // Override parent method
    @Override
    public double calculateDiscount() {
        // Food gets 20% discount if expiring soon
        if (ChronoUnit.DAYS.between(LocalDate.now(), bestBefore) < 3) {
            return price * 0.5;
        }
        return price * 0.8;
    }
    
    // New method specific to Food
    public boolean isExpired() {
        return LocalDate.now().isAfter(bestBefore);
    }
}
```

### Polymorphism in Action

```java
// Polymorphic reference
Product p1 = new Product("Generic", 100);
Product p2 = new Food("Apple", 50, LocalDate.now().plusDays(2));

// Runtime polymorphism - correct method is called
System.out.println(p1.calculateDiscount());  // 90.0 (Product's version)
System.out.println(p2.calculateDiscount());  // 25.0 (Food's version - expiring soon)

// Array of polymorphic references
Product[] products = {
    new Product("Laptop", 1000),
    new Food("Milk", 5, LocalDate.now().plusDays(1)),
    new Food("Rice", 20, LocalDate.now().plusDays(30))
};

for (Product product : products) {
    System.out.println(product.calculateDiscount());  // Calls correct version
}
```

### Type Casting and instanceof

```java
public void processProduct(Product p) {
    // Always use instanceof before casting
    if (p instanceof Food) {
        Food food = (Food) p;
        if (food.isExpired()) {
            System.out.println("Expired!");
        }
    }
    
    // Pattern matching (Java 14+) - cleaner
    if (p instanceof Food food && food.isExpired()) {
        System.out.println("Expired!");
    }
}
```

**Casting Rules:**
- **Upcast** (Child → Parent): Always safe, implicit
  ```java
  Food food = new Food(...);
  Product p = food;  // No cast needed
  ```
- **Downcast** (Parent → Child): Requires explicit cast, may fail
  ```java
  Product p = new Food(...);
  Food food = (Food) p;  // Cast required
  ```

---

## 2. The Object Class - Foundation of Everything

Every class implicitly extends `Object`. Three methods are **critical** for correctness:

### toString() - String Representation

```java
public class Product {
    private String name;
    private double price;
    
    // Without override
    // Outputs: Product@1a2b3c4d
    
    // With override
    @Override
    public String toString() {
        return String.format("Product{name='%s', price=%.2f}", name, price);
    }
}

// Usage
Product p = new Product("Tea", 10.5);
System.out.println(p);  // Automatically calls toString()
```

### equals() - Logical Equality

**Default behavior**: Compares **memory addresses** (same as `==`)

```java
public class Product {
    private int id;
    private String name;
    private double price;
    
    @Override
    public boolean equals(Object obj) {
        // 1. Reference check (performance optimization)
        if (this == obj) return true;
        
        // 2. Null check
        if (obj == null) return false;
        
        // 3. Type check
        if (getClass() != obj.getClass()) return false;
        
        // 4. Field comparison
        Product other = (Product) obj;
        return id == other.id &&
               Objects.equals(name, other.name) &&
               Double.compare(price, other.price) == 0;
    }
}
```

**equals() Contract** (Must satisfy):
1. **Reflexive**: `x.equals(x)` is always true
2. **Symmetric**: If `x.equals(y)`, then `y.equals(x)`
3. **Transitive**: If `x.equals(y)` and `y.equals(z)`, then `x.equals(z)`
4. **Consistent**: Multiple calls return same result
5. **Non-null**: `x.equals(null)` is always false

### hashCode() - Hash Table Support

**Critical Rule**: If two objects are equal (`equals()`), they **must** have the same `hashCode()`.

```java
public class Product {
    private int id;
    private String name;
    private double price;
    
    @Override
    public boolean equals(Object obj) {
        // ... (shown above)
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }
}
```

**Why it matters:**
```java
Set<Product> products = new HashSet<>();
Product p1 = new Product(1, "Tea", 10.0);
Product p2 = new Product(1, "Tea", 10.0);

// Without proper hashCode/equals: both added (wrong!)
// With proper hashCode/equals: only one added (correct!)
products.add(p1);
products.add(p2);
System.out.println(products.size());  // Should be 1
```

---

## 3. String Processing

### String Immutability

```java
String s1 = "Hello";
String s2 = s1.concat(" World");  // Creates NEW string

System.out.println(s1);  // Still "Hello" (unchanged)
System.out.println(s2);  // "Hello World" (new object)
```

### String Pool (Interview Favorite)

```java
// String literals → Pool
String s1 = "Java";
String s2 = "Java";
System.out.println(s1 == s2);  // true (same pool object)

// new String() → Heap
String s3 = new String("Java");
System.out.println(s1 == s3);  // false (different objects)
System.out.println(s1.equals(s3));  // true (same content)

// Interning
String s4 = s3.intern();  // Get pool version
System.out.println(s1 == s4);  // true (now same pool object)
```

### StringBuilder - Mutable Alternative

```java
// BAD: Creates many String objects
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;  // Creates new String each iteration
}

// GOOD: Modifies same StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);  // Modifies in-place
}
String result = sb.toString();
```

### Text Blocks (Java 15+)

```java
// Old way - messy
String json = "{\n" +
              "  \"name\": \"Tea\",\n" +
              "  \"price\": 10.5\n" +
              "}";

// New way - clean
String json = """
    {
      "name": "Tea",
      "price": 10.5
    }
    """;
```

### Common String Methods

```java
String s = "Hello World";

// Searching
s.indexOf("World");        // 6
s.lastIndexOf("o");        // 7
s.contains("llo");         // true
s.startsWith("Hello");     // true
s.endsWith("World");       // true

// Extraction
s.substring(0, 5);         // "Hello"
s.charAt(0);               // 'H'
s.split(" ");              // ["Hello", "World"]

// Transformation
s.toUpperCase();           // "HELLO WORLD"
s.toLowerCase();           // "hello world"
s.replace("World", "Java");// "Hello Java"
s.trim();                  // Removes leading/trailing spaces
s.strip();                 // Unicode-aware trim (Java 11+)
```

---

## 4. Date and Time API (`java.time`)

**Avoid `java.util.Date`** - it's mutable and confusing!

### Core Classes

```java
// Date only
LocalDate date = LocalDate.now();                    // 2024-01-14
LocalDate specific = LocalDate.of(2024, 12, 25);    // Christmas

// Time only
LocalTime time = LocalTime.now();                    // 14:30:00
LocalTime noon = LocalTime.of(12, 0);               // 12:00

// Date + Time
LocalDateTime dateTime = LocalDateTime.now();
LocalDateTime meeting = LocalDateTime.of(2024, 1, 15, 14, 30);

// With timezone
ZonedDateTime zonedNow = ZonedDateTime.now();
ZonedDateTime tokyo = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));

// Machine time (UTC timestamp)
Instant now = Instant.now();
```

### Immutability and Operations

```java
LocalDate today = LocalDate.now();
LocalDate tomorrow = today.plusDays(1);     // New object
LocalDate nextMonth = today.plusMonths(1);  // New object
LocalDate lastWeek = today.minusWeeks(1);   // New object

System.out.println(today);      // Unchanged
System.out.println(tomorrow);   // One day ahead
```

### Duration and Period

```java
// Duration - Time-based (hours, minutes, seconds)
LocalTime start = LocalTime.of(9, 0);
LocalTime end = LocalTime.of(17, 30);
Duration workDay = Duration.between(start, end);  // 8 hours 30 minutes

// Period - Date-based (years, months, days)
LocalDate birth = LocalDate.of(1990, 1, 1);
LocalDate now = LocalDate.now();
Period age = Period.between(birth, now);
System.out.println(age.getYears() + " years old");
```

---

## 5. Enumerations - More Than Constants

### Simple Enum

```java
public enum Status {
    PENDING, ACTIVE, COMPLETED, CANCELLED
}

// Usage
Status status = Status.ACTIVE;

switch (status) {
    case PENDING -> System.out.println("Waiting");
    case ACTIVE -> System.out.println("In Progress");
    case COMPLETED -> System.out.println("Done");
    case CANCELLED -> System.out.println("Aborted");
}
```

### Complex Enum with Fields and Methods

```java
public enum PaymentMethod {
    CREDIT_CARD("CC", 0.03),
    DEBIT_CARD("DC", 0.01),
    PAYPAL("PP", 0.05),
    CRYPTO("CR", 0.02);
    
    private final String code;
    private final double fee;  // Transaction fee percentage
    
    // Constructor MUST be private
    private PaymentMethod(String code, double fee) {
        this.code = code;
        this.fee = fee;
    }
    
    public double calculateFee(double amount) {
        return amount * fee;
    }
    
    public String getCode() {
        return code;
    }
    
    // Static method to find by code
    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod pm : values()) {
            if (pm.code.equals(code)) {
                return pm;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}

// Usage
PaymentMethod pm = PaymentMethod.CREDIT_CARD;
double fee = pm.calculateFee(100);  // 3.0
```

---

## 6. Advanced Inheritance Concepts

### Abstract Classes

```java
public abstract class Shape {
    protected String color;
    
    // Concrete method
    public void setColor(String color) {
        this.color = color;
    }
    
    // Abstract method - subclasses MUST implement
    public abstract double calculateArea();
    
    // Abstract classes CAN have constructors
    public Shape(String color) {
        this.color = color;
    }
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}

// Cannot instantiate abstract class
// Shape s = new Shape("red");  // ERROR

// Can use polymorphic reference
Shape circle = new Circle("red", 5.0);
System.out.println(circle.calculateArea());
```

### Final Keyword

```java
// Final class - cannot be extended
public final class String { }

// Final method - cannot be overridden
public class Parent {
    public final void criticalMethod() {
        // Important logic that must not be changed
    }
}

// Final variable - constant
public static final double PI = 3.14159;
```

### Sealed Classes (Java 17+)

```java
// Sealed parent - restricts who can extend
public sealed class Payment permits CreditCard, PayPal, BankTransfer {
    // Common payment logic
}

// Permitted subclasses MUST be: final, sealed, or non-sealed
public final class CreditCard extends Payment {
    // Cannot be further extended
}

public sealed class PayPal extends Payment permits PayPalBusiness {
    // Can be extended, but only by PayPalBusiness
}

public non-sealed class BankTransfer extends Payment {
    // Open for any extension (back to normal inheritance)
}

// Unauthorized extension
// public class Bitcoin extends Payment { }  // ERROR
```

**Benefits**:
- Controlled inheritance hierarchy
- Pattern matching exhaustiveness
- Clear API design

---

## 7. Interfaces - Contracts and Flexibility

### Basic Interface

```java
public interface Drawable {
    void draw();  // implicitly public abstract
    
    // Constants
    int MAX_SIZE = 1000;  // implicitly public static final
}

public class Circle implements Drawable {
    @Override
    public void draw() {
        System.out.println("Drawing circle");
    }
}
```

### Default Methods (Java 8+)

```java
public interface Logger {
    // Abstract method
    void log(String message);
    
    // Default method - provides implementation
    default void logWithTimestamp(String message) {
        System.out.println(LocalDateTime.now() + ": " + message);
    }
}

public class FileLogger implements Logger {
    @Override
    public void log(String message) {
        // Write to file
    }
    
    // Can use default method or override it
}
```

### Static Methods in Interfaces

```java
public interface MathUtils {
    static int add(int a, int b) {
        return a + b;
    }
    
    static int multiply(int a, int b) {
        return a * b;
    }
}

// Call via interface name
int sum = MathUtils.add(5, 3);
```

### Multiple Interface Implementation

```java
public interface Flyable {
    void fly();
}

public interface Swimmable {
    void swim();
}

// Class can implement multiple interfaces
public class Duck implements Flyable, Swimmable {
    @Override
    public void fly() {
        System.out.println("Duck flying");
    }
    
    @Override
    public void swim() {
        System.out.println("Duck swimming");
    }
}
```

### Functional Interfaces

```java
@FunctionalInterface
public interface Processor<T> {
    T process(T input);  // Single abstract method
    
    // Can have default and static methods
    default T processWithLog(T input) {
        System.out.println("Processing: " + input);
        return process(input);
    }
}

// Used with lambda expressions
Processor<String> upperCase = s -> s.toUpperCase();
String result = upperCase.process("hello");  // "HELLO"
```

---

## 8. Method Overriding Rules

### @Override Annotation

```java
public class Parent {
    public void display() {
        System.out.println("Parent");
    }
}

public class Child extends Parent {
    @Override  // Compiler verifies this actually overrides
    public void display() {
        System.out.println("Child");
    }
    
    // @Override
    // public void displya() { }  // Typo caught by compiler!
}
```

### Overriding Constraints

```java
public class Parent {
    protected void method1() { }
    public String method2() { return "parent"; }
    public void method3() throws IOException { }
}

public class Child extends Parent {
    // 1. Cannot narrow access (protected → private)
    // private void method1() { }  // ERROR
    
    public void method1() { }     // OK (widening)
    
    // 2. Cannot change return type (unless covariant)
    // public int method2() { }   // ERROR
    
    public String method2() { return "child"; }  // OK
    
    // 3. Cannot throw broader checked exceptions
    // public void method3() throws Exception { }  // ERROR
    
    public void method3() { }  // OK (narrowing exceptions)
}
```

### Using super

```java
public class Child extends Parent {
    @Override
    public void display() {
        super.display();  // Call parent version
        System.out.println("Child addition");
    }
}
```

---

## Interview Tips

**Common Questions:**
1. **Difference between `==` and `equals()`?**
   - `==` compares references (memory address)
   - `equals()` compares content (if properly overridden)

2. **Why override `hashCode()` with `equals()`?**
   - HashMap/HashSet rely on both for correctness
   - Violating contract causes bugs in collections

3. **String pool vs Heap?**
   - Literals go to pool (optimization)
   - `new String()` forces heap allocation

4. **Abstract class vs Interface?**
   - Abstract: Single inheritance, can have state
   - Interface: Multiple implementation, contract only

5. **Why are Strings immutable?**
   - Security (can't modify after creation)
   - Thread safety
   - String pool optimization
   - HashCode caching
