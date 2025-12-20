# Comprehensive Caching Guide
## Where to Cache
### External Caching (Distributed Cache)
External caching uses dedicated infrastructure like Redis or Memcached to share cached data across multiple application instances. This enables horizontal scaling and provides a centralized cache layer.​

Use cases: User sessions, API responses, database query results, computed aggregations

Trade-offs: Network latency overhead, serialization costs, requires cache infrastructure management

### CDN (Content Delivery Network)
CDNs cache static assets at edge locations globally, serving content from servers closest to users. When a user requests an image, the CDN edge server returns it immediately if cached, otherwise fetches from origin, caches it, and serves future requests from cache.​

Use cases: Images, videos, CSS/JS files, static HTML pages

### Client-Side Caching
Browsers cache resources using HTTP headers (Cache-Control, ETag), reducing server load and improving user experience through instant page loads.

Use cases: Static assets, API responses with conditional requests, offline-first applications

### In-Process Caching
In-process caches store data in application memory using structures like ConcurrentHashMap or Caffeine cache. This provides the fastest access times but is limited to single-instance scope.​

Use cases: Configuration values, feature flags, reference datasets, hot keys, rate limiting counters, precomputed values​

## Cache Architectures
### Cache-Aside (Lazy Loading)
The application explicitly manages cache reads and writes. On read, check cache first; if miss, fetch from database and populate cache.​

Flow:

Application checks cache for key

Cache hit: Return cached value

Cache miss: Query database, store in cache, return value

#### Implementation:

```java
public class CacheAsideService<K, V> {
    private final Cache<K, V> cache;
    private final DataSource<K, V> dataSource;

    public CacheAsideService(Cache<K, V> cache, DataSource<K, V> dataSource) {
        this.cache = cache;
        this.dataSource = dataSource;
    }
    
    public V get(K key) {
        // Check cache first
        V value = cache.get(key);
        if (value != null) {
            return value;
        }
        
        // Cache miss - fetch from database
        value = dataSource.fetch(key);
        if (value != null) {
            cache.put(key, value);
        }
        
        return value;
    }
    
    public void update(K key, V value) {
        // Update database first
        dataSource.update(key, value);
        
        // Invalidate cache to maintain consistency
        cache.invalidate(key);
    }
}

public interface DataSource<K, V> {
    V fetch(K key);
    void update(K key, V value);
}
```
Pros: Simple, cache failures don't break system, only requested data is cached
Cons: Cache miss penalty (extra latency), potential inconsistency window, stampede risk

### Write-Through Caching
Every write goes through the cache to the database synchronously. Cache and database are always consistent.​

#### Implementation:

```java
public class WriteThroughCache<K, V> {
    private final Cache<K, V> cache;
    private final DataSource<K, V> dataSource;

    public V get(K key) {
        V value = cache.get(key);
        if (value == null) {
            value = dataSource.fetch(key);
            if (value != null) {
                cache.put(key, value);
            }
        }
        return value;
    }
    
    public void put(K key, V value) {
        // Write to cache first
        cache.put(key, value);
        
        // Then write to database synchronously
        try {
            dataSource.update(key, value);
        } catch (Exception e) {
            // Rollback cache on failure
            cache.invalidate(key);
            throw e;
        }
    }
}
```
Best for: Systems where reads must always return fresh data and slower writes are acceptable​

Pros: Strong consistency, simple reasoning, no stale reads
Cons: Write latency increases, wasted cache space for rarely-read data

### Write-Behind (Write-Back) Caching
Writes go to cache immediately and are asynchronously persisted to database. This maximizes write throughput at the cost of consistency.​

#### Implementation:

```java
public class WriteBehindCache<K, V> {
    private final Cache<K, V> cache;
    private final DataSource<K, V> dataSource;
    private final BlockingQueue<WriteOperation<K, V>> writeQueue;
    private final ExecutorService asyncWriter;

    public WriteBehindCache(Cache<K, V> cache, DataSource<K, V> dataSource) {
        this.cache = cache;
        this.dataSource = dataSource;
        this.writeQueue = new LinkedBlockingQueue<>();
        this.asyncWriter = Executors.newFixedThreadPool(4);
        
        // Start background writers
        for (int i = 0; i < 4; i++) {
            asyncWriter.submit(this::processWrites);
        }
    }
    
    public V get(K key) {
        return cache.get(key);
    }
    
    public void put(K key, V value) {
        // Write to cache immediately
        cache.put(key, value);
        
        // Queue for async persistence
        writeQueue.offer(new WriteOperation<>(key, value, System.currentTimeMillis()));
    }
    
    private void processWrites() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WriteOperation<K, V> op = writeQueue.poll(1, TimeUnit.SECONDS);
                if (op != null) {
                    dataSource.update(op.key, op.value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Log and retry logic
            }
        }
    }
    
    private static class WriteOperation<K, V> {
        final K key;
        final V value;
        final long timestamp;
        
        WriteOperation(K key, V value, long timestamp) {
            this.key = key;
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
```
Best for: High write throughput systems with acceptable eventual consistency like analytics and metrics pipelines​

Pros: Maximum write performance, reduced database load
Cons: Data loss risk on crash, complex failure handling, eventual consistency

### Read-Through Caching
Cache acts as the primary interface, automatically fetching from database on miss. Application only interacts with cache.​

#### Implementation:

```java
public class ReadThroughCache<K, V> implements Cache<K, V> {
    private final Map<K, V> storage = new ConcurrentHashMap<>();
    private final DataSource<K, V> dataSource;

    public ReadThroughCache(DataSource<K, V> dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public V get(K key) {
        return storage.computeIfAbsent(key, k -> dataSource.fetch(k));
    }
    
    @Override
    public void put(K key, V value) {
        storage.put(key, value);
    }
    
    @Override
    public void invalidate(K key) {
        storage.remove(key);
    }
}
```
Pros: Simplified application code, transparent caching
Cons: Tight coupling, cache failure impacts reads

## Cache Eviction Policies - Complete Implementations
### LRU (Least Recently Used)
Evicts the least recently accessed item when cache is full. Optimal for workloads with temporal locality.​

#### Production-Grade Implementation:

```java
public class LRUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final DoublyLinkedList<K, V> accessList;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.accessList = new DoublyLinkedList<>();
    }
    
    @Override
    public V get(K key) {
        lock.readLock().lock();
        try {
            Node<K, V> node = cache.get(key);
            if (node == null) {
                return null;
            }
            
            // Move to front (most recently used)
            lock.readLock().unlock();
            lock.writeLock().lock();
            try {
                accessList.moveToFront(node);
                lock.readLock().lock();
                return node.value;
            } finally {
                lock.writeLock().unlock();
            }
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            Node<K, V> existing = cache.get(key);
            
            if (existing != null) {
                // Update existing
                existing.value = value;
                accessList.moveToFront(existing);
            } else {
                // Evict if at capacity
                if (cache.size() >= capacity) {
                    Node<K, V> lru = accessList.removeLast();
                    cache.remove(lru.key);
                }
                
                // Add new entry
                Node<K, V> newNode = new Node<>(key, value);
                accessList.addFirst(newNode);
                cache.put(key, newNode);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public void invalidate(K key) {
        lock.writeLock().lock();
        try {
            Node<K, V> node = cache.remove(key);
            if (node != null) {
                accessList.remove(node);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private static class DoublyLinkedList<K, V> {
        private final Node<K, V> head = new Node<>(null, null);
        private final Node<K, V> tail = new Node<>(null, null);
        
        DoublyLinkedList() {
            head.next = tail;
            tail.prev = head;
        }
        
        void addFirst(Node<K, V> node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }
        
        void remove(Node<K, V> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        
        Node<K, V> removeLast() {
            Node<K, V> last = tail.prev;
            if (last == head) {
                return null;
            }
            remove(last);
            return last;
        }
        
        void moveToFront(Node<K, V> node) {
            remove(node);
            addFirst(node);
        }
    }
}
```
Time Complexity: O(1) for get and put operations
Space Complexity: O(capacity)

### LFU (Least Frequently Used)
Evicts items accessed least frequently. Better for workloads with frequency-based patterns.​

#### Production-Grade Implementation:

```java
public class LFUCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cache;
    private final Map<Integer, FrequencyList<K, V>> frequencyMap;
    private int minFrequency;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.frequencyMap = new HashMap<>();
        this.minFrequency = 0;
    }
    
    @Override
    public V get(K key) {
        lock.writeLock().lock();
        try {
            Node<K, V> node = cache.get(key);
            if (node == null) {
                return null;
            }
            
            // Update frequency
            updateFrequency(node);
            return node.value;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            if (capacity <= 0) {
                return;
            }
            
            Node<K, V> existing = cache.get(key);
            
            if (existing != null) {
                existing.value = value;
                updateFrequency(existing);
            } else {
                if (cache.size() >= capacity) {
                    evict();
                }
                
                Node<K, V> newNode = new Node<>(key, value, 1);
                cache.put(key, newNode);
                
                frequencyMap.computeIfAbsent(1, k -> new FrequencyList<>()).add(newNode);
                minFrequency = 1;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public void invalidate(K key) {
        lock.writeLock().lock();
        try {
            Node<K, V> node = cache.remove(key);
            if (node != null) {
                FrequencyList<K, V> list = frequencyMap.get(node.frequency);
                if (list != null) {
                    list.remove(node);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private void updateFrequency(Node<K, V> node) {
        int oldFreq = node.frequency;
        FrequencyList<K, V> oldList = frequencyMap.get(oldFreq);
        oldList.remove(node);
        
        if (oldList.isEmpty() && oldFreq == minFrequency) {
            minFrequency++;
        }
        
        node.frequency++;
        frequencyMap.computeIfAbsent(node.frequency, k -> new FrequencyList<>()).add(node);
    }
    
    private void evict() {
        FrequencyList<K, V> minList = frequencyMap.get(minFrequency);
        Node<K, V> toEvict = minList.removeFirst();
        cache.remove(toEvict.key);
    }
    
    private static class Node<K, V> {
        K key;
        V value;
        int frequency;
        Node<K, V> prev;
        Node<K, V> next;
        
        Node(K key, V value, int frequency) {
            this.key = key;
            this.value = value;
            this.frequency = frequency;
        }
    }
    
    private static class FrequencyList<K, V> {
        private final Node<K, V> head = new Node<>(null, null, 0);
        private final Node<K, V> tail = new Node<>(null, null, 0);
        
        FrequencyList() {
            head.next = tail;
            tail.prev = head;
        }
        
        void add(Node<K, V> node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }
        
        void remove(Node<K, V> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        
        Node<K, V> removeFirst() {
            if (isEmpty()) {
                return null;
            }
            Node<K, V> first = head.next;
            remove(first);
            return first;
        }
        
        boolean isEmpty() {
            return head.next == tail;
        }
    }
}
```
Time Complexity: O(1) for all operations
Use case: Video streaming, content recommendation where popularity matters

### FIFO (First In First Out)
Evicts the oldest entry regardless of access patterns. Simplest policy, useful when all entries have equal value.​

#### Implementation:

```java
public class FIFOCache<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, V> cache;
    private final Queue<K> insertionQueue;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public FIFOCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity);
        this.insertionQueue = new LinkedList<>();
    }
    
    @Override
    public V get(K key) {
        lock.readLock().lock();
        try {
            return cache.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            if (!cache.containsKey(key)) {
                if (cache.size() >= capacity) {
                    K oldest = insertionQueue.poll();
                    cache.remove(oldest);
                }
                insertionQueue.offer(key);
            }
            cache.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public void invalidate(K key) {
        lock.writeLock().lock();
        try {
            cache.remove(key);
            insertionQueue.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```
Pros: Simple, predictable, low overhead
Cons: Ignores access patterns, may evict hot keys

### TTL (Time To Live)
Expires entries after a fixed duration. Best for data with known freshness requirements.​

#### Implementation with Scheduled Cleanup:

```java
public class TTLCache<K, V> implements Cache<K, V> {
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;
    private final ScheduledExecutorService cleanupExecutor;

    public TTLCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        
        // Periodic cleanup
        cleanupExecutor.scheduleAtFixedRate(
            this::cleanup,
            ttlMillis,
            ttlMillis / 2,
            TimeUnit.MILLISECONDS
        );
    }
    
    @Override
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        
        return entry.value;
    }
    
    @Override
    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis));
    }
    
    @Override
    public void invalidate(K key) {
        cache.remove(key);
    }
    
    private void cleanup() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> entry.getValue().expiryTime < now);
    }
    
    public void shutdown() {
        cleanupExecutor.shutdown();
    }
    
    private static class CacheEntry<V> {
        final V value;
        final long expiryTime;
        
        CacheEntry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}
```
```java
import java.util.Map;
import java.util.concurrent.*;

public class OptimizedTTLCache<K, V> {

   // Stores the data and the "Version" (expiry time) of the data
   private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();

   // Priority Queue that holds keys sorted by death time
   private final DelayQueue<DelayedKey<K>> cleanupQueue = new DelayQueue<>();

   // The background worker thread
   private final ExecutorService cleanupExecutor = Executors.newSingleThreadExecutor();

   public OptimizedTTLCache() {
      // Start the "Janitor" thread
      cleanupExecutor.submit(this::processCleanup);
   }

   /**
    * The Background Janitor Logic
    */
   private void processCleanup() {
      while (!Thread.currentThread().isInterrupted()) {
         try {
            // 1. BLOCK/SLEEP until the next item expires (Zero CPU usage)
            DelayedKey<K> task = cleanupQueue.take();

            // 2. THE CRITICAL FIX: "Version Check"
            // We fetch the CURRENT entry from the map to see if it has changed
            CacheEntry<V> currentEntry = cache.get(task.key);

            // 3. Only delete if the timestamps match exactly.
            // If currentEntry.expiryTime > task.expiryTime, it means the user 
            // updated the key and extended its life. This task is now a "ghost" and we ignore it.
            if (currentEntry != null && currentEntry.expiryTime == task.expiryTime) {
               cache.remove(task.key);
            }

         } catch (InterruptedException e) {
            // Restore interrupt status and exit gracefully
            Thread.currentThread().interrupt();
         }
      }
   }

   /**
    * Adds or Updates a value with a specific TTL
    */
   public void put(K key, V value, long ttlMillis) {
      long expiryTime = System.currentTimeMillis() + ttlMillis;

      // 1. Put into Map (Updates the "Version" of this key)
      cache.put(key, new CacheEntry<>(value, expiryTime));

      // 2. Add to Queue
      // If this is an update, we now have TWO tasks in the queue for the same key.
      // The old task will expire first, but the "Version Check" in processCleanup 
      // will safely ignore it.
      cleanupQueue.put(new DelayedKey<>(key, expiryTime));
   }

   /**
    * Retrieves a value. Includes a "Lazy" check for extra safety.
    */
   public V get(K key) {
      CacheEntry<V> entry = cache.get(key);

      if (entry == null) {
         return null;
      }

      // Double-check expiration (Belt and Suspenders)
      // This handles the tiny millisecond gap between actual expiry and the Janitor waking up.
      if (entry.expiryTime < System.currentTimeMillis()) {
         cache.remove(key);
         return null;
      }

      return entry.value;
   }

   /**
    * Shut down the background thread when destroying the cache
    */
   public void shutdown() {
      cleanupExecutor.shutdownNow();
   }

   // =========================================================
   // HELPER CLASSES
   // =========================================================

   // Wrapper to hold Value + Expiry Time in the Map
   private static class CacheEntry<V> {
      final V value;
      final long expiryTime;

      CacheEntry(V value, long expiryTime) {
         this.value = value;
         this.expiryTime = expiryTime;
      }
   }

   // Wrapper for the DelayQueue
   private static class DelayedKey<K> implements Delayed {
      final K key;
      final long expiryTime;

      DelayedKey(K key, long expiryTime) {
         this.key = key;
         this.expiryTime = expiryTime;
      }

      @Override
      public long getDelay(TimeUnit unit) {
         long diff = expiryTime - System.currentTimeMillis();
         return unit.convert(diff, TimeUnit.MILLISECONDS);
      }

      @Override
      public int compareTo(Delayed o) {
         return Long.compare(this.expiryTime, ((DelayedKey) o).expiryTime);
      }
   }
}
```
Best for: Session tokens, API rate limits, computed aggregations with known staleness tolerance

## Common Caching Problems
### Cache Stampede (Thundering Herd)
Occurs when a popular cache key expires and multiple threads simultaneously attempt to rebuild it, overwhelming the database.​

#### Solution: Request Coalescing (Single Flight)

```java
public class StampedePreventionCache<K, V> {
    private final Cache<K, V> cache;
    private final DataSource<K, V> dataSource;
    private final ConcurrentHashMap<K, CompletableFuture<V>> inFlightRequests = new ConcurrentHashMap<>();

    public StampedePreventionCache(Cache<K, V> cache, DataSource<K, V> dataSource) {
        this.cache = cache;
        this.dataSource = dataSource;
    }
    
    public V get(K key) {
        V cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        
        // Single flight pattern - only one thread fetches
        CompletableFuture<V> future = inFlightRequests.computeIfAbsent(key, k -> 
            CompletableFuture.supplyAsync(() -> {
                try {
                    V value = dataSource.fetch(k);
                    if (value != null) {
                        cache.put(k, value);
                    }
                    return value;
                } finally {
                    inFlightRequests.remove(k);
                }
            })
        );
        
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            inFlightRequests.remove(key);
            throw new RuntimeException("Failed to fetch data", e);
        }
    }
}
```
This is the most effective solution, ensuring only one request rebuilds the cache while others wait for the result.​

#### Cache Warming Alternative:

```java
public class CacheWarmer<K, V> {
    private final Cache<K, V> cache;
    private final DataSource<K, V> dataSource;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void warmPopularKeys(List<K> popularKeys, long refreshIntervalSeconds) {
        for (K key : popularKeys) {
            scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        V value = dataSource.fetch(key);
                        cache.put(key, value);
                    } catch (Exception e) {
                        // Log error
                    }
                },
                0,
                refreshIntervalSeconds,
                TimeUnit.SECONDS
            );
        }
    }
}
```
Cache warming helps only with TTL-based expiration; it doesn't prevent stampedes when invalidating cache on writes.​

## Cache Consistency
Keeping cache and database synchronized is challenging. Common strategies include:​

### Cache Invalidation on Writes:

```java
public class ConsistentCacheService<K, V> {
    private final Cache<K, V> cache;
    private final DataSource<K, V> dataSource;

    public void update(K key, V value) {
        // Update database first
        dataSource.update(key, value);
        
        // Then invalidate cache
        cache.invalidate(key);
        
        // Next read will populate fresh data
    }
    
    public void updateWithRefresh(K key, V value) {
        dataSource.update(key, value);
        
        // Refresh cache immediately instead of invalidate
        cache.put(key, value);
    }
}
```
Trade-offs: Invalidation is safer (avoids stale data if DB update fails), refresh reduces read latency

### Short TTLs for Stale Tolerance:
For systems where eventual consistency is acceptable (feeds, metrics, analytics), use short TTLs to let slightly stale data exist temporarily.​

## Hot Keys
When a small number of keys receive disproportionate traffic, they can overwhelm cache infrastructure.​

### Solution: Hot Key Replication

```java
public class HotKeyReplicatedCache<K, V> {
    private final List<Cache<K, V>> cacheReplicas;
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);
    private final Set<K> hotKeys = ConcurrentHashMap.newKeySet();
    private final int replicationFactor;

    public HotKeyReplicatedCache(List<Cache<K, V>> cacheReplicas, int replicationFactor) {
        this.cacheReplicas = cacheReplicas;
        this.replicationFactor = replicationFactor;
    }
    
    public V get(K key) {
        if (hotKeys.contains(key)) {
            // Load balance across replicas
            int index = Math.abs(roundRobinIndex.getAndIncrement() % replicationFactor);
            return cacheReplicas.get(index).get(key);
        }
        
        return cacheReplicas.get(0).get(key);
    }
    
    public void put(K key, V value) {
        if (hotKeys.contains(key)) {
            // Replicate to multiple caches
            for (int i = 0; i < replicationFactor; i++) {
                cacheReplicas.get(i).put(key, value);
            }
        } else {
            cacheReplicas.get(0).put(key, value);
        }
    }
    
    public void markAsHot(K key) {
        hotKeys.add(key);
    }
}
```
### Local Fallback Cache:

```java
public class TieredCache<K, V> {
    private final Cache<K, V> localCache;  // In-process, very fast
    private final Cache<K, V> distributedCache;  // Redis, shared
    
    public V get(K key) {
        // L1: Check local cache
        V value = localCache.get(key);
        if (value != null) {
            return value;
        }
        
        // L2: Check distributed cache
        value = distributedCache.get(key);
        if (value != null) {
            localCache.put(key, value);  // Populate L1
            return value;
        }
        
        return null;
    }
}
```
This keeps extremely hot values in-process to avoid pounding Redis.​

## Cache Interface Design
### Generic Cache Interface:

```java
public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    void invalidate(K key);
    void clear();
    int size();
    
    default V getOrCompute(K key, Function<K, V> loader) {
        V value = get(key);
        if (value == null) {
            value = loader.apply(key);
            if (value != null) {
                put(key, value);
            }
        }
        return value;
    }
}
```
### Statistics and Monitoring:

```java
public class MonitoredCache<K, V> implements Cache<K, V> {
    private final Cache<K, V> delegate;
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final AtomicLong evictions = new AtomicLong();

    @Override
    public V get(K key) {
        V value = delegate.get(key);
        if (value != null) {
            hits.incrementAndGet();
        } else {
            misses.incrementAndGet();
        }
        return value;
    }
    
    public CacheStats getStats() {
        long total = hits.get() + misses.get();
        double hitRate = total > 0 ? (double) hits.get() / total : 0.0;
        return new CacheStats(hits.get(), misses.get(), hitRate, evictions.get());
    }
    
    public static class CacheStats {
        public final long hits;
        public final long misses;
        public final double hitRate;
        public final long evictions;
        
        CacheStats(long hits, long misses, double hitRate, long evictions) {
            this.hits = hits;
            this.misses = misses;
            this.hitRate = hitRate;
            this.evictions = evictions;
        }
    }
}
```
## System Design Interview Strategy
### When to Introduce Caching
Bring up caching when discussing performance optimization after identifying bottlenecks. Don't lead with caching; establish the baseline architecture first.​

### How to Present Caching in Interviews
1. Identify the bottleneck​
   "Our user service queries the database for every profile request. At 10K QPS, we're seeing P99 latencies of 200ms."

2. Decide what to cache​
   "User profiles are read-heavy (99% reads) and change infrequently. We should cache complete profile objects keyed by user_id."

3. Choose your cache architecture​
   "I'll use cache-aside with Redis. Application checks Redis first, falls back to PostgreSQL on miss, then populates cache with 1-hour TTL."

4. Set an eviction policy​
   "We'll use LRU with 100K capacity to keep active users in cache while automatically evicting inactive profiles."

5. Address the downsides​
   "For consistency, we'll invalidate cache on profile updates. To prevent stampedes on popular users, we'll implement request coalescing."

This demonstrates depth beyond just "add Redis here".​

## Advanced Patterns
### Multi-Level Caching

```java
public class MultiLevelCache<K, V> implements Cache<K, V> {
    private final List<Cache<K, V>> levels;

    public MultiLevelCache(Cache<K, V>... caches) {
        this.levels = Arrays.asList(caches);
    }
    
    @Override
    public V get(K key) {
        for (int i = 0; i < levels.size(); i++) {
            V value = levels.get(i).get(key);
            if (value != null) {
                // Populate higher levels
                for (int j = 0; j < i; j++) {
                    levels.get(j).put(key, value);
                }
                return value;
            }
        }
        return null;
    }
    
    @Override
    public void put(K key, V value) {
        // Write to all levels
        levels.forEach(cache -> cache.put(key, value));
    }
}
```
### Probabilistic Early Expiration (XFetch)
Prevents stampedes by probabilistically refreshing before TTL expires:

```java
public class ProbabilisticCache<K, V> {
    private final Cache<K, CacheEntry<V>> cache;
    private final DataSource<K, V> dataSource;
    private final Random random = new Random();

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        
        if (entry == null) {
            return fetchAndCache(key);
        }
        
        long now = System.currentTimeMillis();
        long timeLeft = entry.expiryTime - now;
        long totalTTL = entry.expiryTime - entry.insertTime;
        
        // Probabilistic early refresh
        // As expiry approaches, probability increases
        double refreshProbability = 1.0 - ((double) timeLeft / totalTTL);
        
        if (random.nextDouble() < refreshProbability) {
            // Asynchronously refresh
            CompletableFuture.runAsync(() -> fetchAndCache(key));
        }
        
        return entry.value;
    }
    
    private V fetchAndCache(K key) {
        V value = dataSource.fetch(key);
        long now = System.currentTimeMillis();
        cache.put(key, new CacheEntry<>(value, now, now + 60000));
        return value;
    }
}
```
This ensures cache is refreshed before expiry, distributing load over time rather than creating stampedes.