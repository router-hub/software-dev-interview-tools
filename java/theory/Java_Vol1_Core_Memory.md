# Java Volume 1: Core Structure & Memory Systems

> **Focus**: JVM Architecture, Data Types, Operators, Control Flow, Memory Management, and Core OOP

---

## 1. Java Platform Architecture

### The Java Ecosystem
**Java is Platform Independent** - "Write Once, Run Anywhere"

#### Components Breakdown
- **JDK (Java Development Kit)**: Complete dev environment
  - Compiler (`javac`), Debugger, Documentation generator (`javadoc`)
  - Includes JRE + development tools
- **JRE (Java Runtime Environment)**: Runtime-only package
  - JVM + Standard Libraries
  - **Cannot compile** - only execute
- **JVM (Java Virtual Machine)**: The execution engine
  - Platform-specific (Windows JVM ≠ Linux JVM)
  - Executes platform-independent bytecode

### Compilation and Execution Flow

```
Developer writes:           HelloWorld.java (Source Code)
                                    ↓
Compiler processes:         javac HelloWorld.java
                                    ↓
Creates:                    HelloWorld.class (Bytecode - Platform Independent)
                                    ↓
JVM executes:               java HelloWorld
                                    ↓
Produces:                   Native Machine Code (Platform Specific)
```

**Key Point**: Bytecode (`.class`) is the secret - same bytecode runs on any JVM.

### Compilation Commands
```bash
# Compile with classpath
javac -d classes -cp lib/* src/com/example/Main.java

# Execute with classpath
java -cp classes:lib/* com.example.Main

# Single-file execution (Java 11+)
java HelloWorld.java  # Compiles and runs in one step
```

---

## 2. Data Types and Variables

### Primitive Types - The Building Blocks

| Type        | Size   | Range             | Default  | Literal Example       |
| ----------- | ------ | ----------------- | -------- | --------------------- |
| **byte**    | 8-bit  | -128 to 127       | 0        | `byte b = 10;`        |
| **short**   | 16-bit | -32,768 to 32,767 | 0        | `short s = 1000;`     |
| **int**     | 32-bit | -2³¹ to 2³¹-1     | 0        | `int i = 100000;`     |
| **long**    | 64-bit | -2⁶³ to 2⁶³-1     | 0L       | `long l = 100000L;`   |
| **float**   | 32-bit | ±3.4E+38          | 0.0f     | `float f = 3.14f;`    |
| **double**  | 64-bit | ±1.7E+308         | 0.0d     | `double d = 3.14159;` |
| **char**    | 16-bit | 0 to 65,535       | '\u0000' | `char c = 'A';`       |
| **boolean** | 1-bit  | -                 | false    | `boolean b = true;`   |

### Number Representation

```java
// Binary (0b prefix)
int binary = 0b1101;        // 13

// Octal (0 prefix)
int octal = 077;            // 63

// Hexadecimal (0x prefix)
int hex = 0x2F;             // 47

// Underscores for readability (Java 7+)
int million = 1_000_000;
long creditCard = 1234_5678_9012_3456L;
```

### Type Casting

```java
// Implicit (Widening) - Safe, automatic
int i = 100;
long l = i;           // int → long (no cast needed)
double d = i;         // int → double

// Explicit (Narrowing) - May lose data, requires cast
double price = 99.99;
int approx = (int) price;  // 99 (decimals lost)

// Overflow example
byte b = (byte) 130;   // -126 (wraps around)
```

**Important**: Arithmetic on types smaller than `int` produces `int`:
```java
byte a = 10, b = 20;
// byte c = a + b;     // ERROR: a+b is int
byte c = (byte)(a + b); // OK
```

### Variable Type Inference (`var`)
Since Java 10, compiler can infer types from initializers:

```java
var name = "Java";              // String
var count = 100;                // int
var price = 99.99;              // double
var list = new ArrayList<String>(); // ArrayList<String>

// Limitations
// var x;                       // ERROR: needs initializer
// var y = null;                // ERROR: cannot infer from null
```

---

## 3. Operators and Expressions

### Operator Precedence (High to Low)

| Category       | Operators                              |
| -------------- | -------------------------------------- |
| Postfix        | `expr++`, `expr--`                     |
| Unary          | `++expr`, `--expr`, `+`, `-`, `!`, `~` |
| Multiplicative | `*`, `/`, `%`                          |
| Additive       | `+`, `-`                               |
| Shift          | `<<`, `>>`, `>>>`                      |
| Relational     | `<`, `>`, `<=`, `>=`, `instanceof`     |
| Equality       | `==`, `!=`                             |
| Bitwise AND    | `&`                                    |
| Bitwise XOR    | `^`                                    |
| Bitwise OR     | `                                      | ` |
| Logical AND    | `&&`                                   |
| Logical OR     | `                                      |   | ` |
| Ternary        | `? :`                                  |
| Assignment     | `=`, `+=`, `-=`, etc.                  |

### Increment/Decrement Operators

```java
int x = 5;
int a = x++;  // a = 5, x = 6 (post-increment: use then increment)
int b = ++x;  // b = 7, x = 7 (pre-increment: increment then use)
```

### Short-Circuit Evaluation

```java
// && (AND) - stops if left is false
if (obj != null && obj.isValid()) {  // Safe: won't call isValid() if obj is null
    // Process
}

// || (OR) - stops if left is true
if (cache.contains(key) || database.contains(key)) {
    // Skips DB check if cache hit
}
```

### Bitwise Operators (Interview Favorite)

```java
int a = 5;   // 0101
int b = 3;   // 0011

// AND: 1 only if both are 1
int and = a & b;   // 0001 = 1

// OR: 1 if at least one is 1
int or = a | b;    // 0111 = 7

// XOR: 1 if different
int xor = a ^ b;   // 0110 = 6

// Left Shift: multiply by 2^n
int left = a << 1; // 1010 = 10 (5 * 2)

// Right Shift: divide by 2^n
int right = a >> 1; // 0010 = 2 (5 / 2)
```

---

## 4. Control Flow

### if-else Statements

```java
if (score >= 90) {
    grade = 'A';
} else if (score >= 80) {
    grade = 'B';
} else if (score >= 70) {
    grade = 'C';
} else {
    grade = 'F';
}

// Ternary operator (concise for simple cases)
String result = (score >= 60) ? "Pass" : "Fail";
```

### Switch Statements (Traditional)

```java
switch (dayOfWeek) {
    case "Monday":
    case "Tuesday":
    case "Wednesday":
        System.out.println("Weekday");
        break;  // Important: prevents fall-through
    case "Saturday":
    case "Sunday":
        System.out.println("Weekend");
        break;
    default:
        System.out.println("Invalid day");
}
```

### Switch Expressions (Java 14+)

```java
// Returns a value, no break needed
String dayType = switch (dayOfWeek) {
    case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> "Weekday";
    case "Saturday", "Sunday" -> "Weekend";
    default -> throw new IllegalArgumentException("Invalid day");
};

// Multi-line with yield
int numLetters = switch (day) {
    case "Monday" -> {
        System.out.println("Start of week");
        yield 6;
    }
    case "Friday" -> {
        System.out.println("End of week");
        yield 6;
    }
    default -> 0;
};
```

---

## 5. Memory Management (Critical for Interviews)

### Stack vs Heap

```
┌─────────────────────────────┐         ┌─────────────────────────────┐
│         STACK               │         │           HEAP              │
│  (Thread-specific)          │         │      (Application-wide)     │
├─────────────────────────────┤         ├─────────────────────────────┤
│                             │         │                             │
│  Method Stack Frames:       │         │  Objects:                   │
│  ┌───────────────────┐      │         │  ┌───────────────────┐     │
│  │ main()            │      │         │  │ Product           │     │
│  │ int x = 10    ──────┐   │         │  │ name: "Tea"       │     │
│  │ Product p ─────┼────┼───┼─────────┼─→│ price: 1.99       │     │
│  └───────────────────┘ │   │         │  └───────────────────┘     │
│                        │   │         │                             │
│  ┌───────────────────┐ │   │         │  ┌───────────────────┐     │
│  │ process()         │ │   │         │  │ String            │     │
│  │ double tax = 0.07 │←┘   │         │  │ value: "Tea"      │     │
│  └───────────────────┘     │         │  └───────────────────┘     │
│                             │         │                             │
│  Cleaned when method ends   │         │  Cleaned by GC              │
└─────────────────────────────┘         └─────────────────────────────┘
```

**What Goes Where:**
- **Stack**: Local variables, method parameters, primitives, object **references**
- **Heap**: Actual **objects**, instance variables, arrays

### Pass-by-Value Explained

**Java ALWAYS passes by value** - but the "value" of an object variable is its reference address!

```java
// Example 1: Primitives
void modifyPrimitive(int x) {
    x = 100;  // Only changes local copy
}

int num = 5;
modifyPrimitive(num);
System.out.println(num);  // Still 5

// Example 2: Objects
void modifyObject(Product p) {
    p.setPrice(99.99);  // Modifies the actual object ✓
}

void reassignObject(Product p) {
    p = new Product();  // Only changes local reference copy ✗
    p.setPrice(50.0);   // Affects only the new local object
}

Product product = new Product();
product.setPrice(10.0);

modifyObject(product);
System.out.println(product.getPrice());  // 99.99 ✓

reassignObject(product);
System.out.println(product.getPrice());  // Still 99.99 (not 50)
```

**Key Interview Point**: You can modify object contents but cannot reassign the original reference from inside a method.

### Garbage Collection

**Eligibility Rules:**
1. Object has no references pointing to it
2. All references are `null`
3. References go out of scope

```java
void createObjects() {
    Product p1 = new Product("A");
    Product p2 = new Product("B");
    
    p1 = null;  // "A" object eligible for GC
    p2 = p1;    // "B" object also eligible (p2 now null)
    
    Product p3 = new Product("C");
    // p3 goes out of scope when method returns → "C" eligible
}
```

**Triggering GC**: `System.gc()` is only a suggestion, JVM decides when to actually run it.

**Types of GC** (Advanced):
- **Serial GC**: Single thread, simple
- **Parallel GC**: Multiple threads, throughput-focused
- **G1 GC**: Balanced, default since Java 9
- **ZGC**: Ultra-low pause times (Java 15+)

---

## 6. Core OOP Concepts

### Classes and Objects

```java
public class Product {
    // Instance variables (state)
    private String name;
    private double price;
    private static long nextId = 1;  // Class variable (shared)
    
    // Constructor
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    // Instance method
    public double calculateTax() {
        return price * 0.1;
    }
    
    // Static method (belongs to class)
    public static long generateId() {
        return nextId++;
    }
}

// Creating objects
Product tea = new Product("Tea", 10.0);
Product coffee = new Product("Coffee", 15.0);
```

### Constructor Chaining

```java
public class Product {
    private String name;
    private double price;
    private int quantity;
    
    // Constructor 1
    public Product() {
        this("Unknown", 0.0, 0);  // Calls constructor 3
    }
    
    // Constructor 2
    public Product(String name, double price) {
        this(name, price, 0);     // Calls constructor 3
    }
    
    // Constructor 3 (Master constructor)
    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
```

**Rule**: `this()` must be the **first statement** in a constructor.

### Access Modifiers

| Modifier      | Class | Package | Subclass (same pkg) | Subclass (diff pkg) | World |
| ------------- | ----- | ------- | ------------------- | ------------------- | ----- |
| **private**   | ✓     | ✗       | ✗                   | ✗                   | ✗     |
| **default**   | ✓     | ✓       | ✓                   | ✗                   | ✗     |
| **protected** | ✓     | ✓       | ✓                   | ✓                   | ✗     |
| **public**    | ✓     | ✓       | ✓                   | ✓                   | ✓     |

### Encapsulation Best Practices

```java
public class BankAccount {
    private double balance;  // Private: cannot access directly
    
    // Controlled access with validation
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    public double getBalance() {
        return balance;  // Read-only access
    }
}
```

---

## 7. Static Context

**Static** members belong to the **class**, not instances.

```java
public class Counter {
    private static int count = 0;  // Shared by all instances
    private int id;                // Unique per instance
    
    public Counter() {
        id = ++count;  // Static accessible from instance
    }
    
    public static int getCount() {
        // return id;  // ERROR: Cannot access instance variable
        return count;  // OK: Static accessing static
    }
    
    public int getId() {
        return id;     // OK: Instance accessing instance
    }
}

// Usage
Counter c1 = new Counter();  // count = 1, c1.id = 1
Counter c2 = new Counter();  // count = 2, c2.id = 2
System.out.println(Counter.getCount());  // 2 (class method)
```

### Static Initialization Block

```java
public class Config {
    private static Properties props;
    
    // Runs once when class is loaded
    static {
        props = new Properties();
        props.load(...);
        System.out.println("Config loaded");
    }
}
```

---

## Interview Red Flags to Avoid

1. ❌ **Confusing `==` with `equals()`** for Strings
2. ❌ **Not understanding pass-by-value** (thinking Java passes objects)
3. ❌ **Creating unnecessary objects** in loops
4. ❌ **Not knowing when GC runs** (it's non-deterministic)
5. ❌ **Mixing static and instance context** incorrectly
6. ❌ **Forgetting `break` in switch** (causing fall-through)
7. ❌ **Using `float` for money** (use `BigDecimal` instead)
8. ❌ **Not initializing local variables** (compiler error)
