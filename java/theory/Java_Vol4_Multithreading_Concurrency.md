# Java Volume 4: Multithreading & Concurrency

> **Focus**: Threads, Synchronization, Concurrent Collections, Executor Framework, Thread Safety

---

## 1. Thread Fundamentals

### Creating Threads

**Method 1: Extending Thread**
```java
class MyThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + i);
        }
    }
}

// Usage
MyThread t1 = new MyThread();
t1.start();  // Starts new thread (calls run() method)
// t1.run();  // ❌ Wrong: Executes in current thread, not new thread
```

**Method 2: Implementing Runnable** (Preferred)
```java
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}

// Usage
Thread t1 = new Thread(new MyTask());
t1.start();

// Lambda (Java 8+)
Thread t2 = new Thread(() -> {
    System.out.println("Lambda thread");
});
t2.start();
```

**Why Runnable over Thread?**
- Separation of concerns (task vs execution)
- Can extend another class
- Can be reused with Executor framework

### Thread Lifecycle

```
NEW → RUNNABLE → RUNNING → TERMINATED
       ↕           ↓
    BLOCKED ← WAITING/TIMED_WAITING
```

**States:**
```java
Thread t = new Thread(() -> {
    try {
        Thread.sleep(1000);  // TIMED_WAITING
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

System.out.println(t.getState());  // NEW
t.start();
System.out.println(t.getState());  // RUNNABLE
```

---

## 2. Synchronization

### Race Condition Problem

```java
class Counter {
    private int count = 0;
    
    public void increment() {
        count++;  // NOT atomic: read → modify → write
    }
    
    public int getCount() {
        return count;
    }
}

// Problem: Two threads increment simultaneously
// Expected: 2000, Actual: ~1800 (lost updates)
Counter counter = new Counter();
Thread t1 = new Thread(() -> {
    for (int i = 0; i < 1000; i++) counter.increment();
});
Thread t2 = new Thread(() -> {
    for (int i = 0; i < 1000; i++) counter.increment();
});
```

### Synchronized Keyword

**Method-level synchronization:**
```java
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}
```

**Block-level synchronization:**
```java
class Counter {
    private int count = 0;
    private final Object lock = new Object();
    
    public void increment() {
        synchronized (lock) {
            count++;
        }
    }
}
```

**Static synchronization:**
```java
class Counter {
    private static int count = 0;
    
    public static synchronized void increment() {
        count++;  // Locks on Counter.class object
    }
}
```

### Deadlock Example & Prevention

```java
// ❌ Deadlock scenario
class Account {
    private double balance;
    
    public synchronized void transfer(Account to, double amount) {
        this.balance -= amount;
        synchronized (to) {  // Nested lock → potential deadlock
            to.balance += amount;
        }
    }
}

// Thread 1: account1.transfer(account2, 100)
// Thread 2: account2.transfer(account1, 50)
// → DEADLOCK!

// ✅ Solution: Lock ordering
public void transfer(Account to, double amount) {
    Account first = this.hashCode() < to.hashCode() ? this : to;
    Account second = this.hashCode() < to.hashCode() ? to : this;
    
    synchronized (first) {
        synchronized (second) {
            this.balance -= amount;
            to.balance += amount;
        }
    }
}
```

---

## 3. Wait, Notify, NotifyAll

### Producer-Consumer Problem

```java
class SharedQueue {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;
    
    public SharedQueue(int capacity) {
        this.capacity = capacity;
    }
    
    public synchronized void produce(int item) throws InterruptedException {
        // Wait while queue is full
        while (queue.size() == capacity) {
            System.out.println("Queue full. Producer waiting...");
            wait();  // Releases lock and waits
        }
        
        queue.offer(item);
        System.out.println("Produced: " + item);
        notifyAll();  // Notify waiting consumers
    }
    
    public synchronized int consume() throws InterruptedException {
        // Wait while queue is empty
        while (queue.isEmpty()) {
            System.out.println("Queue empty. Consumer waiting...");
            wait();
        }
        
        int item = queue.poll();
        System.out.println("Consumed: " + item);
        notifyAll();  // Notify waiting producers
        return item;
    }
}

// Usage
SharedQueue queue = new SharedQueue(5);

// Producer thread
new Thread(() -> {
    for (int i = 0; i < 10; i++) {
        try {
            queue.produce(i);
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}).start();

// Consumer thread
new Thread(() -> {
    for (int i = 0; i < 10; i++) {
        try {
            queue.consume();
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}).start();
```

**Key Points:**
- `wait()`: Releases lock, waits for `notify()`/`notifyAll()`
- `notify()`: Wakes one waiting thread
- `notifyAll()`: Wakes all waiting threads (preferred to avoid missed signals)
- Always use in `while` loop, not `if` (handles spurious wakeups)

---

## 4. Locks & Conditions

### ReentrantLock

```java
class Counter {
    private int count = 0;
    private final Lock lock = new ReentrantLock();
    
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();  // Always in finally
        }
    }
    
    // Try lock with timeout
    public boolean tryIncrement() {
        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    count++;
                    return true;
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
```

### ReadWriteLock

```java
class Cache {
    private final Map<String, String> data = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    public String get(String key) {
        rwLock.readLock().lock();
        try {
            return data.get(key);  // Multiple readers allowed
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public void put(String key, String value) {
        rwLock.writeLock().lock();
        try {
            data.put(key, value);  // Exclusive write access
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
```

**Advantages over synchronized:**
- Interruptible lock acquisition: `lockInterruptibly()`
- Try lock: `tryLock()`
- Fair locking: `new ReentrantLock(true)`
- Multiple condition variables
- Lock/unlock in different methods

---

## 5. Atomic Classes

```java
import java.util.concurrent.atomic.*;

// AtomicInteger
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();  // Atomic count++
counter.getAndIncrement();  // Atomic return count, then increment
counter.addAndGet(5);       // Atomic count += 5
counter.compareAndSet(10, 20);  // CAS: if value==10, set to 20

// AtomicLong
AtomicLong longCounter = new AtomicLong(0);

// AtomicBoolean
AtomicBoolean flag = new AtomicBoolean(false);
if (flag.compareAndSet(false, true)) {
    // Only one thread succeeds
}

// AtomicReference
class User {
    String name;
    int age;
}
AtomicReference<User> userRef = new AtomicReference<>(new User());
userRef.updateAndGet(user -> {
    user.age++;
    return user;
});
```

**Interview Tip**: Atomic classes use **CAS (Compare-And-Swap)** operations, which are lock-free and faster than synchronized.

---

## 6. Concurrent Collections

### ConcurrentHashMap

```java
Map<String, Integer> map = new ConcurrentHashMap<>();

// Thread-safe operations
map.put("key", 1);
map.get("key");
map.remove("key");

// Atomic operations
map.putIfAbsent("key", 1);
map.computeIfAbsent("key", k -> expensiveComputation());
map.merge("key", 1, Integer::sum);  // Atomic increment

// Iteration (weakly consistent)
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    // May not reflect concurrent modifications
}
```

**vs Collections.synchronizedMap():**
- ConcurrentHashMap: Segment-level locking (better concurrency)
- SynchronizedMap: Whole-map locking (slower)

### CopyOnWriteArrayList

```java
List<String> list = new CopyOnWriteArrayList<>();

// Write operations copy entire array
list.add("item");  // Expensive
list.remove("item");

// Read operations are fast (no locking)
for (String item : list) {
    // Safe from ConcurrentModificationException
}
```

**Use case**: Many reads, few writes (event listeners, observers)

### BlockingQueue

```java
BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

// Producer
new Thread(() -> {
    try {
        for (int i = 0; i < 20; i++) {
            queue.put(i);  // Blocks if full
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();

// Consumer
new Thread(() -> {
    try {
        while (true) {
            Integer item = queue.take();  // Blocks if empty
            System.out.println("Consumed: " + item);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();
```

**Implementations:**
- `ArrayBlockingQueue`: Bounded, array-based
- `LinkedBlockingQueue`: Optionally bounded, linked-node
- `PriorityBlockingQueue`: Unbounded, priority heap
- `SynchronousQueue`: Zero capacity (direct handoff)

---

## 7. Executor Framework

### Thread Pool Benefits
- Reuses threads (avoids creation overhead)
- Limits concurrent threads (prevents resource exhaustion)
- Easier task management

### Types of Executors

```java
// Fixed thread pool
ExecutorService executor = Executors.newFixedThreadPool(5);

// Cached thread pool (creates threads as needed)
ExecutorService executor = Executors.newCachedThreadPool();

// Single thread executor
ExecutorService executor = Executors.newSingleThreadExecutor();

// Scheduled executor
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
```

### Usage

```java
ExecutorService executor = Executors.newFixedThreadPool(3);

// Submit Runnable
executor.submit(() -> {
    System.out.println("Task executed");
});

// Submit Callable (returns result)
Future<Integer> future = executor.submit(() -> {
    Thread.sleep(1000);
    return 42;
});

try {
    Integer result = future.get();  // Blocks until done
    System.out.println("Result: " + result);
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}

// Shutdown
executor.shutdown();  // No new tasks, wait for existing
// executor.shutdownNow();  // Interrupt running tasks
executor.awaitTermination(5, TimeUnit.SECONDS);
```

### Custom ThreadPoolExecutor

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    5,                      // corePoolSize
    10,                     // maximumPoolSize
    60, TimeUnit.SECONDS,   // keepAliveTime
    new LinkedBlockingQueue<>(100),  // workQueue
    new ThreadPoolExecutor.CallerRunsPolicy()  // rejectionPolicy
);
```

**Rejection Policies:**
- `AbortPolicy`: Throw exception (default)
- `CallerRunsPolicy`: Run in caller's thread
- `DiscardPolicy`: Silently discard
- `DiscardOldestPolicy`: Discard oldest task

---

## 8. CompletableFuture (Java 8+)

### Asynchronous Programming

```java
// Async task
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    // Runs in ForkJoinPool.commonPool()
    return "Hello";
});

// Chaining
future.thenApply(s -> s + " World")
      .thenAccept(System.out::println)
      .join();  // Wait for completion

// Combining
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "A");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "B");

CompletableFuture<String> combined = future1.thenCombine(future2, (a, b) -> a + b);
System.out.println(combined.get());  // "AB"

// Exception handling
CompletableFuture.supplyAsync(() -> {
    if (Math.random() > 0.5) throw new RuntimeException("Error");
    return "Success";
})
.exceptionally(ex -> "Fallback")
.thenAccept(System.out::println);
```

---

## 9. Common Interview Problems

### Implement Thread-Safe Singleton

```java
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

### Implement Semaphore from Scratch

```java
class CustomSemaphore {
    private int permits;
    
    public CustomSemaphore(int permits) {
        this.permits = permits;
    }
    
    public synchronized void acquire() throws InterruptedException {
        while (permits == 0) {
            wait();
        }
        permits--;
    }
    
    public synchronized void release() {
        permits++;
        notifyAll();
    }
}
```

### Print 1-100 with 3 Threads in Order

```java
class NumberPrinter {
    private int current = 1;
    private final int max = 100;
    
    public synchronized void print(int threadId) {
        while (current <= max) {
            while (current % 3 != threadId) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (current <= max) {
                System.out.println(Thread.currentThread().getName() + ": " + current);
                current++;
                notifyAll();
            }
        }
    }
}

// Usage
NumberPrinter printer = new NumberPrinter();
new Thread(() -> printer.print(1), "Thread-1").start();
new Thread(() -> printer.print(2), "Thread-2").start();
new Thread(() -> printer.print(0), "Thread-3").start();
```

---

## Interview Checklist

### Must-Know Concepts
- [ ] Thread creation (Runnable vs Thread)
- [ ] Thread lifecycle and states
- [ ] Synchronization (method vs block)
- [ ] Deadlock causes and prevention
- [ ] wait/notify/notifyAll usage
- [ ] Lock interfaces (ReentrantLock, ReadWriteLock)
- [ ] Atomic classes (CAS operations)
- [ ] ConcurrentHashMap internals
- [ ] BlockingQueue implementations
- [ ] Executor framework
- [ ] Thread pool sizing
- [ ] CompletableFuture basics

### Common Interview Questions
1. **Difference between Thread and Runnable?**
2. **What is synchronized keyword?**
3. **Explain deadlock with example**
4. **wait() vs sleep()?**
5. **ConcurrentHashMap vs Hashtable?**
6. **How to create thread pool?**
7. **What is volatile keyword?**
8. **Implement producer-consumer**

### Red Flags to Avoid
- ❌ Not unlocking in finally block
- ❌ Using `==` for thread comparison
- ❌ Calling `run()` instead of `start()`
- ❌ Not handling InterruptedException
- ❌ Using `notify()` instead of `notifyAll()`
- ❌ Forgetting `volatile` in double-checked locking
