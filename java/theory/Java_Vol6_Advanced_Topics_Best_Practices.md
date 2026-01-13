# Java Volume 6: Advanced Topics & Best Practices

> **Focus**: Reflection, Annotations, Exception Handling, Java Modules, Best Practices

---

## 1. Reflection API

### Class Inspection

```java
Class<?> clazz = String.class;
// Or: Class.forName("java.lang.String")
// Or: obj.getClass()

// Class information
String name = clazz.getName();           // "java.lang.String"
String simpleName = clazz.getSimpleName();  // "String"
int modifiers = clazz.getModifiers();
boolean isInterface = clazz.isInterface();

// Superclass and interfaces
Class<?> superClass = clazz.getSuperclass();
Class<?>[] interfaces = clazz.getInterfaces();
```

### Field Access

```java
class Person {
    private String name;
    public int age;
}

Class<?> clazz = Person.class;

// Get all fields
Field[] fields = clazz.getDeclaredFields();

// Get specific field
Field nameField = clazz.getDeclaredField("name");
nameField.setAccessible(true);  // Access private field

// Get/Set value
Person person = new Person();
nameField.set(person, "Alice");
String name = (String) nameField.get(person);
```

### Method Invocation

```java
class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    private int multiply(int a, int b) {
        return a * b;
    }
}

Class<?> clazz = Calculator.class;
Calculator calc = new Calculator();

// Get method
Method addMethod = clazz.getMethod("add", int.class, int.class);

// Invoke
Object result = addMethod.invoke(calc, 5, 3);
System.out.println(result);  // 8

// Private method
Method multiplyMethod = clazz.getDeclaredMethod("multiply", int.class, int.class);
multiplyMethod.setAccessible(true);
Object result2 = multiplyMethod.invoke(calc, 5, 3);
System.out.println(result2);  // 15
```

### Constructor Invocation

```java
Class<?> clazz = Person.class;

// Get constructor
Constructor<?> constructor = clazz.getConstructor(String.class, int.class);

// Create instance
Person person = (Person) constructor.newInstance("Alice", 30);
```

### Use Cases
- **Frameworks**: Spring, Hibernate use reflection for dependency injection
- **Testing**: JUnit uses reflection to find and run test methods
- **Serialization/Deserialization**: JSON libraries (Jackson, Gson)
- **ORM**: Database mapping

**Drawbacks:**
- Performance overhead
- Security restrictions
- Type safety lost at compile time

---

## 2. Annotations

### Built-in Annotations

```java
// @Override - Compile-time check
class Parent {
    public void display() {}
}

class Child extends Parent {
    @Override
    public void display() {  // Verifies override
        System.out.println("Child");
    }
}

// @Deprecated - Mark as obsolete
@Deprecated
public void oldMethod() {
    // Use newMethod() instead
}

// @SuppressWarnings - Suppress compiler warnings
@SuppressWarnings("unchecked")
public void method() {
    List list = new ArrayList();  // Raw type warning suppressed
}

// @FunctionalInterface - Enforce single abstract method
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
    // Only one abstract method allowed
}
```

### Custom Annotations

```java
// Define annotation
@Retention(RetentionPolicy.RUNTIME)  // Available at runtime
@Target(ElementType.METHOD)          // Can be applied to methods
public @interface Test {
    String value() default "";
    int timeout() default 0;
}

// Use annotation
class MyTest {
    @Test(value = "Addition test", timeout = 1000)
    public void testAdd() {
        // Test code
    }
}

// Process annotation using reflection
Class<?> clazz = MyTest.class;
for (Method method : clazz.getDeclaredMethods()) {
    if (method.isAnnotationPresent(Test.class)) {
        Test test = method.getAnnotation(Test.class);
        System.out.println("Test: " + test.value());
        System.out.println("Timeout: " + test.timeout());
        method.invoke(clazz.newInstance());
    }
}
```

### Meta-Annotations

```java
@Retention(RetentionPolicy.RUNTIME)  // When available
// SOURCE: Discarded by compiler
// CLASS: In .class file, not at runtime
// RUNTIME: Available via reflection

@Target(ElementType.METHOD)  // Where can be applied
// TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, etc.

@Inherited  // Inherited by subclasses

@Documented  // Included in Javadoc
```

### Real-World Example - Validation

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotNull {
    String message() default "Field cannot be null";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Min {
    int value();
}

class User {
    @NotNull
    private String name;
    
    @Min(18)
    private int age;
}

// Validator
class Validator {
    public static void validate(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(NotNull.class)) {
                if (field.get(obj) == null) {
                    NotNull annotation = field.getAnnotation(NotNull.class);
                    throw new Exception(annotation.message());
                }
            }
            
            if (field.isAnnotationPresent(Min.class)) {
                int value = (int) field.get(obj);
                Min annotation = field.getAnnotation(Min.class);
                if (value < annotation.value()) {
                    throw new Exception("Age must be at least " + annotation.value());
                }
            }
        }
    }
}
```

---

## 3. Exception Handling Best Practices

### Exception Hierarchy

```
Throwable
├── Error (system errors, don't catch)
│   ├── OutOfMemoryError
│   └── StackOverflowError
└── Exception
    ├── RuntimeException (unchecked)
    │   ├── NullPointerException
    │   ├── IllegalArgumentException
    │   └── IndexOutOfBoundsException
    └── IOException (checked)
        ├── FileNotFoundException
        └── SQLException
```

### Custom Exceptions

```java
// Application-specific exception hierarchy
public class ApplicationException extends Exception {
    private final String errorCode;
    
    public ApplicationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
}

public class DatabaseException extends ApplicationException {
    public DatabaseException(String message, Throwable cause) {
        super(message, "DB_ERROR");
        initCause(cause);
    }
}
```

### Try-with-Resources

```java
// Multiple resources
try (BufferedReader br = new BufferedReader(new FileReader("in.txt"));
     BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"))) {
    
    String line;
    while ((line = br.readLine()) != null) {
        bw.write(line);
        bw.newLine();
    }
} catch (IOException e) {
    e.printStackTrace();
}
// Both streams auto-closed in reverse order

// Custom AutoCloseable
class DatabaseConnection implements AutoCloseable {
    public void executeQuery(String sql) {
        System.out.println("Executing: " + sql);
    }
    
    @Override
    public void close() {
        System.out.println("Closing connection");
    }
}

try (DatabaseConnection conn = new DatabaseConnection()) {
    conn.executeQuery("SELECT * FROM users");
}  // Auto-closed
```

### Best Practices

```java
// ✅ GOOD: Specific exceptions
try {
    processFile();
} catch (FileNotFoundException e) {
    log.error("File not found", e);
    // Handle missing file
} catch (IOException e) {
    log.error("I/O error", e);
    // Handle I/O error
}

// ❌ BAD: Catch everything
try {
    processFile();
} catch (Exception e) {  // Too broad!
    e.printStackTrace();
}

// ✅ GOOD: Preserve stack trace
catch (IOException e) {
    throw new ApplicationException("Processing failed", e);
}

// ❌ BAD: Lose original exception
catch (IOException e) {
    throw new ApplicationException("Processing failed");
}

// ✅ GOOD: Clean up in finally
FileInputStream fis = null;
try {
    fis = new FileInputStream("file.txt");
    // Use fis
} catch (IOException e) {
    // Handle
} finally {
    if (fis != null) {
        try {
            fis.close();
        } catch (IOException e) {
            // Log close failure
        }
    }
}
```

---

## 4. Java Modules (Java 9+)

### Module Basics

```java
// module-info.java
module com.example.myapp {
    // Export package (make it public)
    exports com.example.myapp.api;
    
    // Require dependency
    requires java.sql;
    requires com.example.library;
    
    // Open for reflection (required for frameworks)
    opens com.example.myapp.entity to org.hibernate.orm;
    
    // Provide service
    provides com.example.myapp.api.Service 
        with com.example.myapp.impl.ServiceImpl;
    
    // Use service
    uses com.example.myapp.api.Service;
}
```

### Benefits
- **Strong encapsulation**: Hide implementation details
- **Explicit dependencies**: Clear dependency graph
- **Improved performance**: JVM can optimize
- **Better security**: Can't access internal APIs

---

## 5. Best Practices & Design Principles

### SOLID Principles

**1. Single Responsibility Principle (SRP)**
```java
// ❌ BAD: Multiple responsibilities
class User {
    private String name;
    
    public void save() {
        // Database logic
    }
    
    public void sendEmail() {
        // Email logic
    }
}

// ✅ GOOD: Separate concerns
class User {
    private String name;
    // Only user data
}

class UserRepository {
    public void save(User user) {
        // Database logic
    }
}

class EmailService {
    public void sendEmail(User user) {
        // Email logic
    }
}
```

**2. Open/Closed Principle (OCP)**
```java
// ✅ Open for extension, closed for modification
interface PaymentProcessor {
    void process(double amount);
}

class CreditCardProcessor implements PaymentProcessor {
    public void process(double amount) {
        // Credit card logic
    }
}

class PayPalProcessor implements PaymentProcessor {
    public void process(double amount) {
        // PayPal logic
    }
}

// Add new payment method without modifying existing code
class CryptoProcessor implements PaymentProcessor {
    public void process(double amount) {
        // Crypto logic
    }
}
```

**3. Liskov Substitution Principle (LSP)**
```java
// Subclass should be substitutable for parent
class Rectangle {
    protected int width;
    protected int height;
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

// ❌ Violates LSP
class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // Forces square shape
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height;
    }
}

// Problem:
Rectangle rect = new Square();
rect.setWidth(5);
rect.setHeight(4);
// Expected: 20, Actual: 16 (violates LSP)
```

**4. Interface Segregation Principle (ISP)**
```java
// ❌ BAD: Fat interface
interface Worker {
    void work();
    void eat();
    void sleep();
}

// ✅ GOOD: Segregated interfaces
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

class Human implements Workable, Eatable {
    public void work() { }
    public void eat() { }
}

class Robot implements Workable {
    public void work() { }
    // Doesn't need eat()
}
```

**5. Dependency Inversion Principle (DIP)**
```java
// ❌ BAD: High-level depends on low-level
class EmailService {
    // Tightly coupled to SMTP
}

class NotificationService {
    private EmailService emailService = new EmailService();
}

// ✅ GOOD: Both depend on abstraction
interface MessageSender {
    void send(String message);
}

class EmailSender implements MessageSender {
    public void send(String message) {
        // SMTP logic
    }
}

class SmsSender implements MessageSender {
    public void send(String message) {
        // SMS logic
    }
}

class NotificationService {
    private MessageSender sender;
    
    public NotificationService(MessageSender sender) {
        this.sender = sender;  // Dependency injection
    }
}
```

### Clean Code Practices

```java
// ✅ Meaningful names
int elapsedTimeInDays;
Employee employee;

// ❌ Cryptic names
int d;
Employee emp;

// ✅ Small functions (do one thing)
public void processOrder(Order order) {
    validateOrder(order);
    calculateTotal(order);
    saveOrder(order);
    sendConfirmation(order);
}

// ✅ Avoid magic numbers
private static final int MAX_RETRY_ATTEMPTS = 3;
private static final double TAX_RATE = 0.07;

// ✅ Fail fast
public void setAge(int age) {
    if (age < 0 || age > 150) {
        throw new IllegalArgumentException("Invalid age: " + age);
    }
    this.age = age;
}

// ✅ Use Optional for return types
public Optional<User> findById(Long id) {
    return Optional.ofNullable(userMap.get(id));
}

// ❌ Don't use Optional for parameters
public void process(Optional<String> value) {  // BAD
    // ...
}

// ✅ Prefer immutability
public final class Point {
    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // Only getters, no setters
}
```

---

## 6. Performance Optimization

### String Concatenation

```java
// ❌ BAD: String concatenation in loop
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;  // Creates 1000 String objects
}

// ✅ GOOD: StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String result = sb.toString();
```

### Collection Sizing

```java
// ✅ Specify initial capacity if known
List<String> list = new ArrayList<>(1000);
Map<String, Integer> map = new HashMap<>(16, 0.75f);
```

### Lazy Initialization

```java
class ExpensiveObject {
    private static ExpensiveObject instance;
    
    public static ExpensiveObject getInstance() {
        if (instance == null) {
            instance = new ExpensiveObject();
        }
        return instance;
    }
}
```

---

## 7. Testing Best Practices

### Unit Testing with JUnit 5

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    void testAddition() {
        assertEquals(5, calculator.add(2, 3));
    }
    
    @Test
    void testDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> {
            calculator.divide(10, 0);
        });
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testPositiveNumbers(int number) {
        assertTrue(number > 0);
    }
}
```

---

## Interview Preparation Checklist

### Core Concepts
- [ ] Reflection API usage and limitations
- [ ] Custom annotation creation
- [ ] Exception handling hierarchy
- [ ] Try-with-resources
- [ ] Java modules basics
- [ ] SOLID principles
- [ ] Design patterns (Singleton, Factory, Builder, Strategy, Observer)

### Advanced Topics
- [ ] Generics type erasure
- [ ] Lambda expressions and method references
- [ ] Stream API operations
- [ ] CompletableFuture
- [ ] Thread safety patterns

### Best Practices
- [ ] Code readability (naming, formatting)
- [ ] Error handling strategies
- [ ] Performance optimization
- [ ] Testing approaches
- [ ] Build tools (Maven, Gradle)

---

## Summary

**Complete Java Coverage:**
- **Vol 1**: Core & Memory (JVM, Primitives, OOP)
- **Vol 2**: Advanced Design (Inheritance, APIs, Interfaces)
- **Vol 3**: Collections & Modern (Generics, Streams, Records)
- **Vol 4**: Multithreading & Concurrency
- **Vol 5**: I/O & Functional Programming
- **Vol 6**: Advanced Topics & Best Practices

You now have comprehensive coverage of Java for interviews! 🎯
