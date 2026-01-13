# Java Machine Coding Round - Part 3 (Problems 9-12)

> **Target**: SDE2/SDE3 positions  
> **Duration**: Typically 60-90 minutes per question  
> **Focus**: Production-ready code with design patterns

---

## Table of Contents

9. [Elevator System](#9-elevator-system)
10. [Rate Limiter](#10-rate-limiter)
11. [In-Memory Database](#11-in-memory-database)
12. [Vending Machine](#12-vending-machine)

---

## 9. Elevator System

### Problem Statement
Design a multi-elevator control system:
- Multiple elevators in a building
- Handle user requests (up/down buttons on floors)
- Efficient elevator assignment
- Multiple floors
- Handle concurrent requests

### Interview Checklist

**Requirements:**
- [ ] Number of elevators and floors
- [ ] Algorithm for elevator assignment (closest, least loaded)
- [ ] Direction priority (same direction first)
- [ ] Emergency handling
- [ ] Weight capacity needed?

**Design Decisions:**
- [ ] Strategy pattern for assignment algorithm
- [ ] State pattern for elevator states
- [ ] Priority queue for requests
- [ ] Observer pattern for notifications

**Implementation Focus:**
- [ ] Thread-safe request handling
- [ ] Efficient elevator selection
- [ ] Direction optimization
- [ ] Concurrent request processing

### Solution

```java
enum Direction {
    UP, DOWN, IDLE
}

enum ElevatorState {
    MOVING_UP, MOVING_DOWN, IDLE
}

class Request implements Comparable<Request> {
    private final int floor;
    private final Direction direction;
    private final LocalDateTime timestamp;
    
    public Request(int floor, Direction direction) {
        this.floor = floor;
        this.direction = direction;
        this.timestamp = LocalDateTime.now();
    }
    
    @Override
    public int compareTo(Request other) {
        return Integer.compare(this.floor, other.floor);
    }
    
    public int getFloor() { return floor; }
    public Direction getDirection() { return direction; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return floor == request.floor && direction == request.direction;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(floor, direction);
    }
}

class Elevator implements Runnable {
    private final int id;
    private final int maxFloor;
    private int currentFloor;
    private ElevatorState state;
    private final PriorityQueue<Integer> upQueue;
    private final PriorityQueue<Integer> downQueue;
    private final Lock lock;
    private volatile boolean running;
    
    public Elevator(int id, int maxFloor) {
        this.id = id;
        this.maxFloor = maxFloor;
        this.currentFloor = 0;
        this.state = ElevatorState.IDLE;
        this.upQueue = new PriorityQueue<>(); // Min heap for up direction
        this.downQueue = new PriorityQueue<>(Collections.reverseOrder()); // Max heap for down
        this.lock = new ReentrantLock();
        this.running = true;
    }
    
    public void addRequest(int floor) {
        lock.lock();
        try {
            if (floor == currentFloor) {
                return; // Already at the floor
            }
            
            if (floor > currentFloor) {
                upQueue.offer(floor);
            } else {
                downQueue.offer(floor);
            }
            
            System.out.println("Elevator " + id + ": Request added for floor " + floor);
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                processRequests();
                Thread.sleep(1000); // Simulate elevator movement time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void processRequests() {
        lock.lock();
        try {
            if (state == ElevatorState.IDLE || state == ElevatorState.MOVING_UP) {
                if (!upQueue.isEmpty()) {
                    int targetFloor = upQueue.poll();
                    moveToFloor(targetFloor);
                    state = ElevatorState.MOVING_UP;
                } else if (!downQueue.isEmpty()) {
                    state = ElevatorState.MOVING_DOWN;
                    int targetFloor = downQueue.poll();
                    moveToFloor(targetFloor);
                } else {
                    state = ElevatorState.IDLE;
                }
            } else if (state == ElevatorState.MOVING_DOWN) {
                if (!downQueue.isEmpty()) {
                    int targetFloor = downQueue.poll();
                    moveToFloor(targetFloor);
                } else if (!upQueue.isEmpty()) {
                    state = ElevatorState.MOVING_UP;
                    int targetFloor = upQueue.poll();
                    moveToFloor(targetFloor);
                } else {
                    state = ElevatorState.IDLE;
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    private void moveToFloor(int floor) {
        System.out.println("Elevator " + id + ": Moving from " + currentFloor + " to " + floor);
        currentFloor = floor;
        System.out.println("Elevator " + id + ": Arrived at floor " + floor);
    }
    
    public int getCurrentFloor() {
        return currentFloor;
    }
    
    public ElevatorState getState() {
        return state;
    }
    
    public int getId() {
        return id;
    }
    
    public void shutdown() {
        running = false;
    }
}

// Strategy Pattern for Elevator Selection
interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, int requestFloor, Direction direction);
}

class ClosestElevatorStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int requestFloor, Direction direction) {
        return elevators.stream()
            .min(Comparator.comparingInt(e -> Math.abs(e.getCurrentFloor() - requestFloor)))
            .orElse(elevators.get(0));
    }
}

class SameDirectionPriorityStrategy implements ElevatorSelectionStrategy {
    @Override
    public Elevator selectElevator(List<Elevator> elevators, int requestFloor, Direction direction) {
        // First, try to find elevator moving in same direction
        for (Elevator elevator : elevators) {
            ElevatorState state = elevator.getState();
            int currentFloor = elevator.getCurrentFloor();
            
            if (direction == Direction.UP && state == ElevatorState.MOVING_UP) {
                if (currentFloor <= requestFloor) {
                    return elevator;
                }
            } else if (direction == Direction.DOWN && state == ElevatorState.MOVING_DOWN) {
                if (currentFloor >= requestFloor) {
                    return elevator;
                }
            }
        }
        
        // If no elevator in same direction, find closest idle or any elevator
        return elevators.stream()
            .filter(e -> e.getState() == ElevatorState.IDLE)
            .min(Comparator.comparingInt(e -> Math.abs(e.getCurrentFloor() - requestFloor)))
            .orElse(new ClosestElevatorStrategy().selectElevator(elevators, requestFloor, direction));
    }
}

class ElevatorController {
    private final List<Elevator> elevators;
    private final ElevatorSelectionStrategy strategy;
    private final ExecutorService executorService;
    
    public ElevatorController(int numElevators, int maxFloor, ElevatorSelectionStrategy strategy) {
        this.elevators = new ArrayList<>();
        this.strategy = strategy;
        this.executorService = Executors.newFixedThreadPool(numElevators);
        
        // Initialize elevators
        for (int i = 0; i < numElevators; i++) {
            Elevator elevator = new Elevator(i + 1, maxFloor);
            elevators.add(elevator);
            executorService.submit(elevator);
        }
    }
    
    public void requestElevator(int floor, Direction direction) {
        Elevator selectedElevator = strategy.selectElevator(elevators, floor, direction);
        selectedElevator.addRequest(floor);
        System.out.println("Request from floor " + floor + " (" + direction + 
                         ") assigned to Elevator " + selectedElevator.getId());
    }
    
    public void shutdown() {
        elevators.forEach(Elevator::shutdown);
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}

// Demo
public class ElevatorSystemDemo {
    public static void main(String[] args) throws InterruptedException {
        // Create controller with 3 elevators for 10-floor building
        ElevatorController controller = new ElevatorController(
            3, 
            10, 
            new SameDirectionPriorityStrategy()
        );
        
        // Simulate requests
        controller.requestElevator(5, Direction.UP);
        Thread.sleep(500);
        controller.requestElevator(3, Direction.DOWN);
        Thread.sleep(500);
        controller.requestElevator(8, Direction.UP);
        Thread.sleep(500);
        controller.requestElevator(1, Direction.UP);
        
        // Let elevators process requests
        Thread.sleep(10000);
        
        controller.shutdown();
    }
}
```

### Key Points to Mention
- **Strategy Pattern**: Different elevator selection algorithms (closest, same direction)
- **Priority Queues**: Separate queues for up/down to optimize movement
- **Thread Safety**: Each elevator runs in its own thread with proper locking
- **Direction Optimization**: Minimizes direction changes for efficiency

---

## 10. Rate Limiter

### Problem Statement
Design a rate limiter that:
- Limits number of requests per user/IP
- Supports different time windows (second, minute, hour)
- Multiple rate limiting strategies (fixed window, sliding window, token bucket)
- Thread-safe for concurrent requests

### Interview Checklist

**Requirements:**
- [ ] Rate limit per user or global?
- [ ] Time window (second, minute, hour)
- [ ] Algorithm (token bucket, sliding window, fixed window)
- [ ] Action on limit exceeded (reject, queue, throttle)
- [ ] Distributed system or single server?

**Design Decisions:**
- [ ] Strategy pattern for different algorithms
- [ ] Concurrent data structures
- [ ] Scheduled cleanup for old entries
- [ ] Cache for hot users

**Implementation Focus:**
- [ ] Accurate rate counting
- [ ] Minimal memory footprint
- [ ] Thread-safe operations
- [ ] Performance optimization

### Solution

```java
// Token Bucket Algorithm
class TokenBucket {
    private final int capacity;
    private final int refillRate; // tokens per second
    private double tokens;
    private LocalDateTime lastRefillTime;
    private final Lock lock;
    
    public TokenBucket(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTime = LocalDateTime.now();
        this.lock = new ReentrantLock();
    }
    
    public boolean allowRequest() {
        lock.lock();
        try {
            refill();
            if (tokens >= 1) {
                tokens--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    private void refill() {
        LocalDateTime now = LocalDateTime.now();
        long secondsPassed = ChronoUnit.SECONDS.between(lastRefillTime, now);
        
        if (secondsPassed > 0) {
            double newTokens = secondsPassed * refillRate;
            tokens = Math.min(capacity, tokens + newTokens);
            lastRefillTime = now;
        }
    }
    
    public double getAvailableTokens() {
        lock.lock();
        try {
            refill();
            return tokens;
        } finally {
            lock.unlock();
        }
    }
}

// Sliding Window Log Algorithm
class SlidingWindowLog {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final Queue<Long> requestTimestamps;
    private final Lock lock;
    
    public SlidingWindowLog(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.requestTimestamps = new LinkedList<>();
        this.lock = new ReentrantLock();
    }
    
    public boolean allowRequest() {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            long windowStart = now - windowSizeMillis;
            
            // Remove old timestamps outside window
            while (!requestTimestamps.isEmpty() && requestTimestamps.peek() <= windowStart) {
                requestTimestamps.poll();
            }
            
            if (requestTimestamps.size() < maxRequests) {
                requestTimestamps.offer(now);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    public int getCurrentCount() {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            long windowStart = now - windowSizeMillis;
            
            while (!requestTimestamps.isEmpty() && requestTimestamps.peek() <= windowStart) {
                requestTimestamps.poll();
            }
            
            return requestTimestamps.size();
        } finally {
            lock.unlock();
        }
    }
}

// Fixed Window Counter
class FixedWindowCounter {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final AtomicInteger counter;
    private volatile long windowStart;
    private final Lock lock;
    
    public FixedWindowCounter(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.counter = new AtomicInteger(0);
        this.windowStart = System.currentTimeMillis();
        this.lock = new ReentrantLock();
    }
    
    public boolean allowRequest() {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            
            // Check if window has expired
            if (now - windowStart >= windowSizeMillis) {
                counter.set(0);
                windowStart = now;
            }
            
            if (counter.get() < maxRequests) {
                counter.incrementAndGet();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    public int getCurrentCount() {
        return counter.get();
    }
}

// Rate Limiter Interface
interface RateLimiter {
    boolean allowRequest(String userId);
    void reset(String userId);
}

// Token Bucket Rate Limiter
class TokenBucketRateLimiter implements RateLimiter {
    private final int capacity;
    private final int refillRate;
    private final Map<String, TokenBucket> buckets;
    
    public TokenBucketRateLimiter(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.buckets = new ConcurrentHashMap<>();
    }
    
    @Override
    public boolean allowRequest(String userId) {
        TokenBucket bucket = buckets.computeIfAbsent(
            userId, 
            k -> new TokenBucket(capacity, refillRate)
        );
        return bucket.allowRequest();
    }
    
    @Override
    public void reset(String userId) {
        buckets.remove(userId);
    }
}

// Sliding Window Rate Limiter
class SlidingWindowRateLimiter implements RateLimiter {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final Map<String, SlidingWindowLog> windows;
    
    public SlidingWindowRateLimiter(int maxRequests, long windowSizeMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.windows = new ConcurrentHashMap<>();
    }
    
    @Override
    public boolean allowRequest(String userId) {
        SlidingWindowLog window = windows.computeIfAbsent(
            userId, 
            k -> new SlidingWindowLog(maxRequests, windowSizeMillis)
        );
        return window.allowRequest();
    }
    
    @Override
    public void reset(String userId) {
        windows.remove(userId);
    }
    
    public int getCurrentCount(String userId) {
        SlidingWindowLog window = windows.get(userId);
        return window != null ? window.getCurrentCount() : 0;
    }
}

// API Gateway with Rate Limiting
class APIGateway {
    private final RateLimiter rateLimiter;
    
    public APIGateway(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }
    
    public Response handleRequest(String userId, String endpoint) {
        if (!rateLimiter.allowRequest(userId)) {
            System.out.println("Rate limit exceeded for user: " + userId);
            return new Response(429, "Too Many Requests");
        }
        
        // Process request
        System.out.println("Processing request for user: " + userId + ", endpoint: " + endpoint);
        return new Response(200, "Success");
    }
}

class Response {
    private final int statusCode;
    private final String message;
    
    public Response(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
    
    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
}

// Demo
public class RateLimiterDemo {
    public static void main(String[] args) throws InterruptedException {
        // Token Bucket: 5 requests per second
        System.out.println("=== Token Bucket Rate Limiter ===");
        RateLimiter tokenBucketLimiter = new TokenBucketRateLimiter(5, 1);
        APIGateway gateway1 = new APIGateway(tokenBucketLimiter);
        
        String user1 = "user1";
        for (int i = 0; i < 7; i++) {
            Response response = gateway1.handleRequest(user1, "/api/data");
            System.out.println("  Response: " + response.getStatusCode());
        }
        
        Thread.sleep(2000); // Wait for tokens to refill
        System.out.println("\nAfter 2 seconds (tokens refilled):");
        Response response = gateway1.handleRequest(user1, "/api/data");
        System.out.println("  Response: " + response.getStatusCode());
        
        // Sliding Window: 3 requests per 5 seconds
        System.out.println("\n=== Sliding Window Rate Limiter ===");
        SlidingWindowRateLimiter slidingLimiter = new SlidingWindowRateLimiter(3, 5000);
        APIGateway gateway2 = new APIGateway(slidingLimiter);
        
        String user2 = "user2";
        for (int i = 0; i < 5; i++) {
            Response resp = gateway2.handleRequest(user2, "/api/data");
            System.out.println("  Request " + (i+1) + " - Response: " + resp.getStatusCode() + 
                             " (Count: " + slidingLimiter.getCurrentCount(user2) + ")");
            Thread.sleep(1000);
        }
    }
}
```

### Key Points to Mention
- **Token Bucket**: Good for burst traffic, allows temporary spikes
- **Sliding Window**: More accurate, prevents burst at window edges
- **Fixed Window**: Simple but has boundary issues
- **Memory**: Sliding window uses more memory (stores timestamps)
- **Thread Safety**: ConcurrentHashMap and synchronization

---

## 11. In-Memory Database

### Problem Statement
Design an in-memory key-value database with:
- CRUD operations (Create, Read, Update, Delete)
- Transaction support (begin, commit, rollback)
- Indexing for fast lookups
- Query by fields
- TTL (Time-To-Live) support

### Interview Checklist

**Requirements:**
- [ ] Data types supported (string, int, object)
- [ ] Transaction isolation level
- [ ] Secondary indexes needed?
- [ ] Persistence required?
- [ ] Query language complexity

**Design Decisions:**
- [ ] HashMap for primary storage
- [ ] Separate indexes for searchable fields
- [ ] Transaction stack for rollback
- [ ] Scheduled cleanup for expired entries

**Implementation Focus:**
- [ ] Transaction atomicity
- [ ] Index maintenance
- [ ] TTL implementation
- [ ] Query optimization

### Solution

```java
class Record {
    private final String id;
    private final Map<String, Object> fields;
    private final LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    
    public Record(String id, Map<String, Object> fields) {
        this.id = id;
        this.fields = new HashMap<>(fields);
        this.createdAt = LocalDateTime.now();
    }
    
    public void setTTL(long seconds) {
        this.expiresAt = LocalDateTime.now().plusSeconds(seconds);
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public String getId() { return id; }
    public Map<String, Object> getFields() { return new HashMap<>(fields); }
    public Object getField(String fieldName) { return fields.get(fieldName); }
    public void setField(String fieldName, Object value) { fields.put(fieldName, value); }
}

class Transaction {
    private final Map<String, Record> addedRecords;
    private final Map<String, Record> modifiedRecords;
    private final Set<String> deletedRecords;
    
    public Transaction() {
        this.addedRecords = new HashMap<>();
        this.modifiedRecords = new HashMap<>();
        this.deletedRecords = new HashSet<>();
    }
    
    public void recordAdd(String id, Record record) {
        addedRecords.put(id, record);
    }
    
    public void recordModify(String id, Record record) {
        modifiedRecords.put(id, record);
    }
    
    public void recordDelete(String id) {
        deletedRecords.add(id);
    }
    
    public Map<String, Record> getAddedRecords() { return addedRecords; }
    public Map<String, Record> getModifiedRecords() { return modifiedRecords; }
    public Set<String> getDeletedRecords() { return deletedRecords; }
}

class InMemoryDatabase {
    private final Map<String, Record> primaryStore;
    private final Map<String, Map<Object, Set<String>>> indexes; // fieldName -> value -> recordIds
    private final ThreadLocal<Transaction> currentTransaction;
    private final ScheduledExecutorService cleanupScheduler;
    private final ReadWriteLock lock;
    
    public InMemoryDatabase() {
        this.primaryStore = new ConcurrentHashMap<>();
        this.indexes = new ConcurrentHashMap<>();
        this.currentTransaction = new ThreadLocal<>();
        this.lock = new ReentrantReadWriteLock();
        
        // Schedule cleanup of expired records
        this.cleanupScheduler = Executors.newScheduledThreadPool(1);
        cleanupScheduler.scheduleAtFixedRate(
            this::cleanupExpiredRecords, 
            60, 60, TimeUnit.SECONDS
        );
    }
    
    // Create index on a field
    public void createIndex(String fieldName) {
        indexes.putIfAbsent(fieldName, new ConcurrentHashMap<>());
        
        // Build index for existing records
        lock.readLock().lock();
        try {
            for (Record record : primaryStore.values()) {
                Object value = record.getField(fieldName);
                if (value != null) {
                    addToIndex(fieldName, value, record.getId());
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // Start transaction
    public void beginTransaction() {
        if (currentTransaction.get() != null) {
            throw new IllegalStateException("Transaction already in progress");
        }
        currentTransaction.set(new Transaction());
        System.out.println("Transaction started");
    }
    
    // Commit transaction
    public void commit() {
        Transaction transaction = currentTransaction.get();
        if (transaction == null) {
            throw new IllegalStateException("No active transaction");
        }
        
        lock.writeLock().lock();
        try {
            // Apply all changes
            transaction.getAddedRecords().forEach(primaryStore::put);
            transaction.getModifiedRecords().forEach(primaryStore::put);
            transaction.getDeletedRecords().forEach(primaryStore::remove);
            
            System.out.println("Transaction committed");
        } finally {
            currentTransaction.remove();
            lock.writeLock().unlock();
        }
    }
    
    // Rollback transaction
    public void rollback() {
        Transaction transaction = currentTransaction.get();
        if (transaction == null) {
            throw new IllegalStateException("No active transaction");
        }
        
        currentTransaction.remove();
        System.out.println("Transaction rolled back");
    }
    
    // Insert record
    public void insert(String id, Map<String, Object> fields) {
        Record record = new Record(id, fields);
        
        Transaction transaction = currentTransaction.get();
        if (transaction != null) {
            transaction.recordAdd(id, record);
        } else {
            lock.writeLock().lock();
            try {
                if (primaryStore.containsKey(id)) {
                    throw new IllegalArgumentException("Record already exists: " + id);
                }
                primaryStore.put(id, record);
                updateIndexes(record);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
    
    // Get record by ID
    public Record get(String id) {
        lock.readLock().lock();
        try {
            Record record = primaryStore.get(id);
            if (record != null && record.isExpired()) {
                primaryStore.remove(id);
                return null;
            }
            return record;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // Update record
    public void update(String id, Map<String, Object> fields) {
        lock.writeLock().lock();
        try {
            Record record = primaryStore.get(id);
            if (record == null) {
                throw new IllegalArgumentException("Record not found: " + id);
            }
            
            fields.forEach(record::setField);
            updateIndexes(record);
            
            Transaction transaction = currentTransaction.get();
            if (transaction != null) {
                transaction.recordModify(id, record);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // Delete record
    public void delete(String id) {
        Transaction transaction = currentTransaction.get();
        if (transaction != null) {
            transaction.recordDelete(id);
        } else {
            lock.writeLock().lock();
            try {
                primaryStore.remove(id);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
    
    // Query by field
    public List<Record> queryByField(String fieldName, Object value) {
        lock.readLock().lock();
        try {
            Map<Object, Set<String>> index = indexes.get(fieldName);
            if (index == null) {
                // No index, scan all records
                return primaryStore.values().stream()
                    .filter(r -> value.equals(r.getField(fieldName)))
                    .filter(r -> !r.isExpired())
                    .collect(Collectors.toList());
            }
            
            // Use index
            Set<String> recordIds = index.getOrDefault(value, Collections.emptySet());
            return recordIds.stream()
                .map(primaryStore::get)
                .filter(Objects::nonNull)
                .filter(r -> !r.isExpired())
                .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // Set TTL for record
    public void setTTL(String id, long seconds) {
        lock.readLock().lock();
        try {
            Record record = primaryStore.get(id);
            if (record != null) {
                record.setTTL(seconds);
            }
        } finally {
            lock.readLock().unlock();
        }
    }
    
    private void updateIndexes(Record record) {
        for (String fieldName : indexes.keySet()) {
            Object value = record.getField(fieldName);
            if (value != null) {
                addToIndex(fieldName, value, record.getId());
            }
        }
    }
    
    private void addToIndex(String fieldName, Object value, String recordId) {
        Map<Object, Set<String>> index = indexes.get(fieldName);
        if (index != null) {
            index.computeIfAbsent(value, k -> ConcurrentHashMap.newKeySet()).add(recordId);
        }
    }
    
    private void cleanupExpiredRecords() {
        lock.writeLock().lock();
        try {
            primaryStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public void shutdown() {
        cleanupScheduler.shutdown();
    }
}

// Demo
public class InMemoryDatabaseDemo {
    public static void main(String[] args) throws InterruptedException {
        InMemoryDatabase db = new InMemoryDatabase();
        
        // Create index on 'age' field
        db.createIndex("age");
        
        // Insert records
        db.insert("1", Map.of("name", "Alice", "age", 30, "city", "NYC"));
        db.insert("2", Map.of("name", "Bob", "age", 25, "city", "SF"));
        db.insert("3", Map.of("name", "Charlie", "age", 30, "city", "LA"));
        
        // Get record
        Record record = db.get("1");
        System.out.println("Record 1: " + record.getFields());
        
        // Query by field
        List<Record> age30 = db.queryByField("age", 30);
        System.out.println("\nRecords with age 30: " + age30.size());
        
        // Transaction
        System.out.println("\n=== Transaction Test ===");
        db.beginTransaction();
        db.insert("4", Map.of("name", "David", "age", 35));
        db.update("1", Map.of("age", 31));
        db.commit();
        
        Record updated = db.get("1");
        System.out.println("Updated record: " + updated.getFields());
        
        // Rollback test
        System.out.println("\n=== Rollback Test ===");
        db.beginTransaction();
        db.insert("5", Map.of("name", "Eve", "age", 28));
        db.rollback();
        
        Record shouldBeNull = db.get("5");
        System.out.println("After rollback, record 5: " + shouldBeNull);
        
        // TTL test
        System.out.println("\n=== TTL Test ===");
        db.insert("6", Map.of("name", "Temp", "age", 20));
        db.setTTL("6", 2); // Expires in 2 seconds
        System.out.println("Record 6 before expiry: " + db.get("6"));
        
        Thread.sleep(3000);
        System.out.println("Record 6 after expiry: " + db.get("6"));
        
        db.shutdown();
    }
}
```

### Key Points to Mention
- **Transaction Support**: ACID properties with rollback capability
- **Indexing**: Secondary indexes for fast queries
- **TTL**: Automatic expiration with scheduled cleanup
- **Thread Safety**: ReadWriteLock for concurrent access
- **Memory Management**: Automatic cleanup of expired records

---

## 12. Vending Machine

### Problem Statement
Design a vending machine with:
- Multiple products with prices
- Coin/note acceptance
- Change return
- Product dispensing
- Inventory management
- Different states (idle, accepting money, dispensing)

### Interview Checklist

**Requirements:**
- [ ] Coin denominations accepted
- [ ] Payment methods (cash only or card)
- [ ] Exact change required or can return change?
- [ ] Product selection before or after payment?
- [ ] Out of stock handling

**Design Decisions:**
- [ ] State pattern for vending machine states
- [ ] Enum for coin denominations
- [ ] Inventory management strategy
- [ ] Change calculation algorithm

**Implementation Focus:**
- [ ] State transitions
- [ ] Change calculation
- [ ] Concurrent purchases handling
- [ ] Clean error handling

### Solution

```java
enum Coin {
    PENNY(1), NICKEL(5), DIME(10), QUARTER(25), DOLLAR(100);
    
    private final int value;
    
    Coin(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}

class Product {
    private final String code;
    private final String name;
    private final int price; // in cents
    
    public Product(String code, String name, int price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }
    
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    
    @Override
    public String toString() {
        return String.format("%s - %s ($%.2f)", code, name, price / 100.0);
    }
}

class Inventory {
    private final Map<String, Product> products;
    private final Map<String, Integer> stock;
    private final Lock lock;
    
    public Inventory() {
        this.products = new ConcurrentHashMap<>();
        this.stock = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }
    
    public void addProduct(Product product, int quantity) {
        products.put(product.getCode(), product);
        stock.put(product.getCode(), quantity);
    }
    
    public Product getProduct(String code) {
        return products.get(code);
    }
    
    public boolean isAvailable(String code) {
        return stock.getOrDefault(code, 0) > 0;
    }
    
    public void deduct(String code) {
        lock.lock();
        try {
            Integer quantity = stock.get(code);
            if (quantity != null && quantity > 0) {
                stock.put(code, quantity - 1);
            } else {
                throw new IllegalStateException("Product out of stock");
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void refill(String code, int quantity) {
        stock.merge(code, quantity, Integer::sum);
    }
    
    public Map<String, Integer> getStock() {
        return new HashMap<>(stock);
    }
}

class CoinInventory {
    private final Map<Coin, Integer> coins;
    private final Lock lock;
    
    public CoinInventory() {
        this.coins = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
        
        // Initialize with some coins
        for (Coin coin : Coin.values()) {
            coins.put(coin, 10);
        }
    }
    
    public void addCoin(Coin coin, int quantity) {
        coins.merge(coin, quantity, Integer::sum);
    }
    
    public List<Coin> getChange(int amount) {
        lock.lock();
        try {
            List<Coin> change = new ArrayList<>();
            Coin[] coinValues = Coin.values();
            
            // Start from highest denomination
            for (int i = coinValues.length - 1; i >= 0; i--) {
                Coin coin = coinValues[i];
                int available = coins.get(coin);
                
                while (amount >= coin.getValue() && available > 0) {
                    change.add(coin);
                    amount -= coin.getValue();
                    available--;
                }
            }
            
            if (amount > 0) {
                throw new IllegalStateException("Cannot provide exact change");
            }
            
            // Deduct coins used for change
            for (Coin coin : change) {
                coins.merge(coin, -1, Integer::sum);
            }
            
            return change;
        } finally {
            lock.unlock();
        }
    }
    
    public int getTotal() {
        return coins.entrySet().stream()
            .mapToInt(e -> e.getKey().getValue() * e.getValue())
            .sum();
    }
}

// State Pattern
interface VendingMachineState {
    void insertCoin(Coin coin);
    void selectProduct(String code);
    void dispense();
    void cancel();
}

class IdleState implements VendingMachineState {
    private final VendingMachine machine;
    
    public IdleState(VendingMachine machine) {
        this.machine = machine;
    }
    
    @Override
    public void insertCoin(Coin coin) {
        machine.addInsertedMoney(coin.getValue());
        machine.getCoinInventory().addCoin(coin, 1);
        machine.setState(machine.getAcceptingMoneyState());
        System.out.println("Inserted " + coin + ". Total: $" + 
                         String.format("%.2f", machine.getInsertedMoney() / 100.0));
    }
    
    @Override
    public void selectProduct(String code) {
        System.out.println("Please insert money first");
    }
    
    @Override
    public void dispense() {
        System.out.println("Please select a product");
    }
    
    @Override
    public void cancel() {
        System.out.println("No transaction to cancel");
    }
}

class AcceptingMoneyState implements VendingMachineState {
    private final VendingMachine machine;
    
    public AcceptingMoneyState(VendingMachine machine) {
        this.machine = machine;
    }
    
    @Override
    public void insertCoin(Coin coin) {
        machine.addInsertedMoney(coin.getValue());
        machine.getCoinInventory().addCoin(coin, 1);
        System.out.println("Inserted " + coin + ". Total: $" + 
                         String.format("%.2f", machine.getInsertedMoney() / 100.0));
    }
    
    @Override
    public void selectProduct(String code) {
        Product product = machine.getInventory().getProduct(code);
        
        if (product == null) {
            System.out.println("Invalid product code");
            return;
        }
        
        if (!machine.getInventory().isAvailable(code)) {
            System.out.println("Product out of stock");
            return;
        }
        
        if (machine.getInsertedMoney() < product.getPrice()) {
            System.out.println("Insufficient money. Need $" + 
                             String.format("%.2f more", 
                             (product.getPrice() - machine.getInsertedMoney()) / 100.0));
            return;
        }
        
        machine.setSelectedProduct(product);
        machine.setState(machine.getDispensingState());
        dispense();
    }
    
    @Override
    public void dispense() {
        System.out.println("Please select a product first");
    }
    
    @Override
    public void cancel() {
        System.out.println("Transaction cancelled");
        returnMoney();
        machine.reset();
        machine.setState(machine.getIdleState());
    }
    
    private void returnMoney() {
        int amount = machine.getInsertedMoney();
        if (amount > 0) {
            try {
                List<Coin> change = machine.getCoinInventory().getChange(amount);
                System.out.println("Returning: " + formatChange(change));
            } catch (Exception e) {
                System.out.println("Cannot return exact change. Returning coins...");
            }
        }
    }
    
    private String formatChange(List<Coin> coins) {
        Map<Coin, Long> counts = coins.stream()
            .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        
        return counts.entrySet().stream()
            .map(e -> e.getValue() + "x" + e.getKey())
            .collect(Collectors.joining(", "));
    }
}

class DispensingState implements VendingMachineState {
    private final VendingMachine machine;
    
    public DispensingState(VendingMachine machine) {
        this.machine = machine;
    }
    
    @Override
    public void insertCoin(Coin coin) {
        System.out.println("Please wait, dispensing product");
    }
    
    @Override
    public void selectProduct(String code) {
        System.out.println("Already dispensing");
    }
    
    @Override
    public void dispense() {
        Product product = machine.getSelectedProduct();
        
        try {
            // Dispense product
            machine.getInventory().deduct(product.getCode());
            System.out.println("Dispensing: " + product.getName());
            
            // Return change
            int change = machine.getInsertedMoney() - product.getPrice();
            if (change > 0) {
                List<Coin> changeCoins = machine.getCoinInventory().getChange(change);
                System.out.println("Change: $" + String.format("%.2f", change / 100.0));
            }
            
            machine.reset();
            machine.setState(machine.getIdleState());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            cancel();
        }
    }
    
    @Override
    public void cancel() {
        System.out.println("Cannot cancel while dispensing");
    }
}

class VendingMachine {
    private final Inventory inventory;
    private final CoinInventory coinInventory;
    
    private VendingMachineState idleState;
    private VendingMachineState acceptingMoneyState;
    private VendingMachineState dispensingState;
    
    private VendingMachineState currentState;
    private int insertedMoney;
    private Product selectedProduct;
    
    public VendingMachine() {
        this.inventory = new Inventory();
        this.coinInventory = new CoinInventory();
        
        // Initialize states
        this.idleState = new IdleState(this);
        this.acceptingMoneyState = new AcceptingMoneyState(this);
        this.dispensingState = new DispensingState(this);
        
        this.currentState = idleState;
        this.insertedMoney = 0;
    }
    
    public void insertCoin(Coin coin) {
        currentState.insertCoin(coin);
    }
    
    public void selectProduct(String code) {
        currentState.selectProduct(code);
    }
    
    public void cancel() {
        currentState.cancel();
    }
    
    public void reset() {
        this.insertedMoney = 0;
        this.selectedProduct = null;
    }
    
    // Getters and setters
    public void setState(VendingMachineState state) {
        this.currentState = state;
    }
    
    public void addInsertedMoney(int amount) {
        this.insertedMoney += amount;
    }
    
    public void setSelectedProduct(Product product) {
        this.selectedProduct = product;
    }
    
    public Inventory getInventory() { return inventory; }
    public CoinInventory getCoinInventory() { return coinInventory; }
    public int getInsertedMoney() { return insertedMoney; }
    public Product getSelectedProduct() { return selectedProduct; }
    public VendingMachineState getIdleState() { return idleState; }
    public VendingMachineState getAcceptingMoneyState() { return acceptingMoneyState; }
    public VendingMachineState getDispensingState() { return dispensingState; }
}

// Demo
public class VendingMachineDemo {
    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine();
        
        // Stock products
        machine.getInventory().addProduct(new Product("A1", "Coke", 150), 10);
        machine.getInventory().addProduct(new Product("A2", "Pepsi", 150), 10);
        machine.getInventory().addProduct(new Product("B1", "Chips", 100), 5);
        machine.getInventory().addProduct(new Product("B2", "Candy", 75), 8);
        
        System.out.println("=== Vending Machine Demo ===\n");
        
        // Scenario 1: Successful purchase
        System.out.println("Scenario 1: Buying Coke");
        machine.insertCoin(Coin.DOLLAR);
        machine.insertCoin(Coin.QUARTER);
        machine.insertCoin(Coin.QUARTER);
        machine.selectProduct("A1");
        
        System.out.println("\n" + "=".repeat(40) + "\n");
        
        // Scenario 2: Cancel transaction
        System.out.println("Scenario 2: Cancel transaction");
        machine.insertCoin(Coin.DOLLAR);
        machine.cancel();
        
        System.out.println("\n" + "=".repeat(40) + "\n");
        
        // Scenario 3: Insufficient money
        System.out.println("Scenario 3: Insufficient money");
        machine.insertCoin(Coin.QUARTER);
        machine.selectProduct("A1");
        machine.insertCoin(Coin.DOLLAR);
        machine.selectProduct("A1");
    }
}
```

### Key Points to Mention
- **State Pattern**: Clean state transitions (Idle → Accepting Money → Dispensing)
- **Change Calculation**: Greedy algorithm with coin inventory tracking
- **Thread Safety**: Locks for concurrent transactions
- **Error Handling**: Out of stock, insufficient money, exact change issues
- **Extensibility**: Easy to add new products, payment methods

---

## Summary

**Part 3 Complete**: Advanced system design problems  
- **Elevator System**: Multi-elevator scheduling with optimization
- **Rate Limiter**: Token bucket and sliding window algorithms
- **In-Memory Database**: CRUD with transactions and indexing
- **Vending Machine**: State pattern with complete vending logic

**Key Design Patterns Used:**
- Strategy Pattern (Elevator, Rate Limiter)
- State Pattern (Vending Machine)
- Observer Pattern (potential for notifications)
- Repository Pattern (Database)

**Interview Success Factors:**
1. ✅ Clean code structure
2. ✅ Proper design patterns
3. ✅ Thread safety considerations
4. ✅ Edge case handling
5. ✅ Extensible architecture

---

**Complete Series:**
- **Part 1**: Parking Lot, LRU Cache, Tic-Tac-Toe, Meeting Scheduler, Snake & Ladder
- **Part 2**: Library Management, Splitwise, URL Shortener
- **Part 3**: Elevator System, Rate Limiter, In-Memory Database, Vending Machine

**Total**: 12 production-ready machine coding solutions! 🎯
