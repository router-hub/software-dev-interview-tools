# Complete Redis Guide: From Zero to Pro

## What is Redis?

Redis (Remote Dictionary Server) is an open-source, in-memory data structure store that functions as a database, cache, message broker, and streaming engine. Unlike traditional databases that store data on disk, Redis keeps all data in RAM, making operations extremely fast (microsecond latency).

### Key Characteristics:

* **In-memory storage:** All data lives in RAM for ultra-fast access
* **Single-threaded:** Uses one thread for command execution, ensuring atomic operations
* **Versatile:** Acts as cache, database, message queue, and more
* **Simple:** Data structures mirror familiar programming constructs
* **Fast:** Sub-millisecond response times for most operations
* **Trade-off:** Speed over durability - Redis prioritizes performance, with optional persistence strategies to minimize data loss.

## Redis Internal Architecture

### Single-Threaded Event Loop

Redis uses a single-threaded architecture with event-driven I/O multiplexing. Here's how it works:

**Request Processing Flow:**

1. Client sends command (GET, SET, etc.)
2. Request enters the event queue
3. Single thread processes requests in FIFO order
4. Connection held while processing
5. Response sent back to client
6. Next request processed

**Why Single-Threaded?**

* Eliminates context switching overhead
* No thread synchronization needed
* All operations are atomic by default
* Simpler reasoning about concurrency
* Modern Redis 6+ uses additional threads only for I/O and background tasks

**Java Analogy:**
The code snippet is a "conceptual model" to show the sequential processing, but it simplified the networking part too much.
Real Redis (written in C) uses a mechanism called I/O Multiplexing (specifically epoll on Linux or kqueue on macOS).

* It doesn't poll a queue: It asks the Operating System, "Wake me up when any of these 10,000 client connections sends me data."
* The Sleep: When no commands are coming in, the Redis process actually sleeps. It uses near-zero CPU.
* The Wake: As soon as a packet arrives from the network, the OS wakes Redis up, Redis processes that command, and then goes back to sleep or handles the next one.

```java
// Redis conceptual model (simplified)
public class RedisServer {
    private final Queue<Command> commandQueue = new LinkedBlockingQueue<>();
    private final Map<String, Object> dataStore = new HashMap<>();

    public void eventLoop() {
        while (true) {
            // This BLOCKS. The thread sleeps here if the queue is empty.
            // 0% CPU usage while waiting.
            Command cmd = commandQueue.take();

            Object result = execute(cmd);
            cmd.sendResponse(result);
        }
    }
  
    private Object execute(Command cmd) {
        // Single thread executes all commands atomically
        return switch (cmd.type) {
            case "GET" -> dataStore.get(cmd.key);
            case "SET" -> dataStore.put(cmd.key, cmd.value);
            default -> throw new IllegalArgumentException();
        };
    }
}
```

### Memory Management

Redis stores all data in RAM with configurable memory limits. When memory is full, eviction policies determine what to remove (similar to our cache implementations).

**Configuration:**

```bash
# Set max memory to 2GB
maxmemory 2gb

# Set eviction policy
maxmemory-policy allkeys-lru
```

## Persistence Mechanisms

Since Redis is in-memory, data is lost on crash unless persistence is enabled.

### RDB (Redis Database) Snapshots

Point-in-time snapshots saved to disk at intervals.

**How it works:**

1. Fork child process (copy-on-write)
2. Child writes memory snapshot to disk
3. Atomic replacement of old snapshot
4. Parent continues serving requests

**Configuration:**

```bash
# Save snapshot every 900 seconds if at least 1 key changed
save 900 1
# Save every 300 seconds if at least 10 keys changed
save 300 10
# Save every 60 seconds if at least 10000 keys changed
save 60 10000
```

* **Pros:** Fast restarts, compact files, minimal performance impact
* **Cons:** Data loss risk between snapshots (last N minutes), slower with large datasets

### AOF (Append-Only File)

Logs every write operation to a file. On restart, Redis replays commands to rebuild state.

**How it works:**

1. Client sends write command
2. Command executed in memory
3. Command appended to AOF file
4. File synced to disk (configurable)

**Configuration:**

```bash
# Enable AOF
appendonly yes

# Sync strategy
appendfsync always    # Every command (slow, safest)
appendfsync everysec  # Every second (balanced)
appendfsync no        # OS decides (fast, risky)
```

**AOF Rewriting:**
AOF grows indefinitely, so Redis periodically rewrites it by creating a minimal command set that produces current state.

```bash
# Auto-rewrite when file grows 100% and is at least 64MB
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
```

* **Pros:** Better durability, human-readable, automatic rewriting
* **Cons:** Larger files, slower restarts with large datasets

### Hybrid: RDB + AOF

Use both for maximum reliability. RDB for fast restarts, AOF for minimal data loss.

## Redis Data Structures Deep Dive

### 1. Strings

The simplest type, stores strings, integers, or floats up to 512MB.

**Common Operations:**

```bash
# Basic operations
SET user:1000:name "Rajesh Kumar"
GET user:1000:name                    # Returns "Rajesh Kumar"
MSET key1 "value1" key2 "value2"     # Set multiple
MGET key1 key2                        # Get multiple

# Expiration
SETEX session:abc123 3600 "user_data" # Set with 3600 second TTL
TTL session:abc123                    # Check remaining time
EXPIRE user:1000:name 300             # Set expiration on existing key

# Atomic operations
INCR counter                          # Increment by 1
INCRBY counter 5                      # Increment by 5
DECR counter                          # Decrement by 1

# Bit operations
SETBIT user:1000:active 1 1          # Set bit at position 1
GETBIT user:1000:active 1            # Get bit at position 1
```

**Java Implementation with Jedis:**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisStringOperations {
    private final JedisPool jedisPool;

    public RedisStringOperations(String host, int port) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(50);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        this.jedisPool = new JedisPool(config, host, port);
    }
  
    public void basicOperations() {
        try (Jedis jedis = jedisPool.getResource()) {
            // Set and get
            jedis.set("user:1000:name", "Rajesh Kumar");
            String name = jedis.get("user:1000:name");
        
            // Set with expiration
            jedis.setex("session:abc123", 3600, "user_data");
        
            // Atomic counter
            jedis.set("page_views", "0");
            jedis.incr("page_views");
            jedis.incrBy("page_views", 5);
        
            // Check if exists
            boolean exists = jedis.exists("user:1000:name");
        }
    }
  
    // Rate limiting using string
    public boolean isRateLimited(String userId, int maxRequests, int windowSeconds) {
        String key = "rate_limit:" + userId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            String currentStr = jedis.get(key);
        
            if (currentStr == null) {
                // First request
                jedis.setex(key, windowSeconds, "1");
                return false;
            }
        
            int current = Integer.parseInt(currentStr);
            if (current >= maxRequests) {
                return true; // Rate limited
            }
        
            jedis.incr(key);
            return false;
        }
    }
}
```

**Use Cases:**

* Session storage
* Caching API responses
* Counters (views, likes)
* Rate limiting
* Feature flags

### 2. Hashes

Hash maps that store field-value pairs, perfect for objects.

**Common Operations:**

```bash
# Set fields
HSET user:1000 name "Rajesh" age 30 city "Gurugram"
HMSET user:1000 email "rajesh@example.com" phone "9876543210"

# Get fields
HGET user:1000 name                   # Returns "Rajesh"
HMGET user:1000 name age              # Get multiple fields
HGETALL user:1000                     # Get all fields and values

# Field operations
HEXISTS user:1000 email               # Check if field exists
HDEL user:1000 phone                  # Delete field
HINCRBY user:1000 age 1               # Increment numeric field

# Atomic operations
HSETNX user:1000 created_at "2025"    # Set only if not exists
HLEN user:1000                        # Count fields
```

**Java Implementation:**

```java
public class RedisHashOperations {
    private final JedisPool jedisPool;

    public RedisHashOperations(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
  
    // Store user object
    public void saveUser(String userId, User user) {
        String key = "user:" + userId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> userMap = new HashMap<>();
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());
            userMap.put("age", String.valueOf(user.getAge()));
            userMap.put("city", user.getCity());
        
            jedis.hset(key, userMap);
        }
    }
  
    // Retrieve user object
    public User getUser(String userId) {
        String key = "user:" + userId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> userMap = jedis.hgetAll(key);
        
            if (userMap.isEmpty()) {
                return null;
            }
        
            return new User(
                userMap.get("name"),
                userMap.get("email"),
                Integer.parseInt(userMap.get("age")),
                userMap.get("city")
            );
        }
    }
  
    // Update specific fields
    public void updateUserFields(String userId, Map<String, String> updates) {
        String key = "user:" + userId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, updates);
        }
    }
  
    // Shopping cart implementation
    public void addToCart(String userId, String productId, int quantity) {
        String key = "cart:" + userId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hincrBy(key, productId, quantity);
        }
    }
  
    public Map<String, Integer> getCart(String userId) {
        String key = "cart:" + userId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> cart = jedis.hgetAll(key);
            Map<String, Integer> result = new HashMap<>();
        
            cart.forEach((productId, qty) -> 
                result.put(productId, Integer.parseInt(qty))
            );
        
            return result;
        }
    }
}

class User {
    private String name;
    private String email;
    private int age;
    private String city;

    // Constructor, getters, setters
    public User(String name, String email, int age, String city) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.city = city;
    }
  
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
    public String getCity() { return city; }
}
```

**Use Cases:**

* User profiles
* Shopping carts
* Product details
* Configuration objects
* Session data with multiple attributes

**Why use Hash instead of multiple Strings?**

* More memory efficient
* Single atomic operation for multiple fields
* Better semantic grouping
* Easier to retrieve entire object

### 3. Lists

Linked lists of strings, supports operations at both ends.

**Common Operations:**

```bash
# Push elements
LPUSH queue:jobs "job1" "job2"        # Push to left (head)
RPUSH queue:jobs "job3"               # Push to right (tail)

# Pop elements
LPOP queue:jobs                       # Pop from left
RPOP queue:jobs                       # Pop from right
BLPOP queue:jobs 5                    # Blocking pop (wait 5 seconds)

# Access elements
LRANGE queue:jobs 0 -1                # Get all elements
LINDEX queue:jobs 0                   # Get element at index
LLEN queue:jobs                       # Get length

# Modify
LTRIM queue:jobs 0 99                 # Keep only first 100 elements
LINSERT queue:jobs BEFORE "job2" "newjob"  # Insert before element
```

**Java Implementation - Job Queue:**

```java
public class RedisQueueService {
    private final JedisPool jedisPool;

    public RedisQueueService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
  
    // Simple queue
    public void enqueue(String queueName, String job) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.rpush(queueName, job);
        }
    }
  
    public String dequeue(String queueName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpop(queueName);
        }
    }
  
    // Blocking queue with timeout
    public String blockingDequeue(String queueName, int timeoutSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> result = jedis.blpop(timeoutSeconds, queueName);
            return result != null && result.size() > 1 ? result.get(1) : null;
        }
    }
  
    // Activity feed (keep latest 100 posts)
    public void addToFeed(String userId, String post) {
        String key = "feed:" + userId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.lpush(key, post);
            jedis.ltrim(key, 0, 99);  // Keep only latest 100
        }
    }
  
    public List<String> getFeed(String userId, int count) {
        String key = "feed:" + userId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, 0, count - 1);
        }
    }
  
    // Job processor with retry
    public void processJobs(String queueName, Consumer<String> processor) {
        String processingQueue = queueName + ":processing";
    
        while (true) {
            try (Jedis jedis = jedisPool.getResource()) {
                // Move job from pending to processing
                String job = jedis.rpoplpush(queueName, processingQueue);
            
                if (job == null) {
                    Thread.sleep(1000);
                    continue;
                }
            
                try {
                    processor.accept(job);
                    // Remove from processing queue on success
                    jedis.lrem(processingQueue, 1, job);
                } catch (Exception e) {
                    // Job failed, move back to pending queue
                    jedis.rpoplpush(processingQueue, queueName);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
```

**Use Cases:**

* Message queues
* Activity feeds
* Recent items list
* Task queues
* Notification inbox

### 4. Sets

Unordered collection of unique strings.

**Common Operations:**

```bash
we# Add members
SADD tags:post:1 "java" "redis" "backend"
SADD tags:post:2 "java" "spring" "microservices"

# Check membership
SISMEMBER tags:post:1 "java"          # Returns 1 (true)

# Get members
SMEMBERS tags:post:1                  # Get all members
SCARD tags:post:1                     # Count members
SRANDMEMBER tags:post:1 2             # Get 2 random members

# Remove
SREM tags:post:1 "backend"            # Remove member
SPOP tags:post:1                      # Remove and return random

# Set operations
SINTER tags:post:1 tags:post:2        # Intersection (common tags)
SUNION tags:post:1 tags:post:2        # Union (all unique tags)
SDIFF tags:post:1 tags:post:2         # Difference
```

**Java Implementation:**

```java
public class RedisSetOperations {
    private final JedisPool jedisPool;

    public RedisSetOperations(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
  
    // Tag system
    public void addTags(String postId, Set<String> tags) {
        String key = "tags:post:" + postId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(key, tags.toArray(new String[0]));
        }
    }
  
    public Set<String> getTags(String postId) {
        String key = "tags:post:" + postId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        }
    }
  
    // Find posts with common tags
    public Set<String> findCommonTags(String postId1, String postId2) {
        String key1 = "tags:post:" + postId1;
        String key2 = "tags:post:" + postId2;
    
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sinter(key1, key2);
        }
    }
  
    // Online users tracking
    public void userOnline(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd("online_users", userId);
        }
    }
  
    public void userOffline(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.srem("online_users", userId);
        }
    }
  
    public boolean isUserOnline(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember("online_users", userId);
        }
    }
  
    public long getOnlineCount() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scard("online_users");
        }
    }
  
    // Mutual friends
    public Set<String> getMutualFriends(String userId1, String userId2) {
        String key1 = "friends:" + userId1;
        String key2 = "friends:" + userId2;
    
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sinter(key1, key2);
        }
    }
  
    // Friend suggestions (friends of friends)
    public Set<String> suggestFriends(String userId) {
        String userFriendsKey = "friends:" + userId;
    
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> friends = jedis.smembers(userFriendsKey);
            Set<String> suggestions = new HashSet<>();
        
            for (String friendId : friends) {
                String friendFriendsKey = "friends:" + friendId;
                Set<String> friendsOfFriend = jedis.smembers(friendFriendsKey);
                suggestions.addAll(friendsOfFriend);
            }
        
            // Remove user's own friends and self
            suggestions.removeAll(friends);
            suggestions.remove(userId);
        
            return suggestions;
        }
    }
}
```

**Use Cases:**

* Tags/labels
* Online users tracking
* Social graphs (friends, followers)
* Unique visitors
* Voting systems
* Access control lists

### 5. Sorted Sets (ZSETs)

Sets where each member has a score, maintaining order.

**Common Operations:**

```bash
# Add members with scores
ZADD leaderboard 100 "player1" 250 "player2" 180 "player3"

# Get by rank
ZRANGE leaderboard 0 9                # Top 10 (ascending)
ZREVRANGE leaderboard 0 9             # Top 10 (descending)
ZREVRANGE leaderboard 0 9 WITHSCORES  # With scores

# Get by score
ZRANGEBYSCORE leaderboard 100 200     # Players with score 100-200
ZCOUNT leaderboard 100 200            # Count in score range

# Update score
ZINCRBY leaderboard 50 "player1"      # Add 50 to player1's score

# Rank operations
ZRANK leaderboard "player1"           # Get rank (0-based)
ZREVRANK leaderboard "player1"        # Get reverse rank
ZSCORE leaderboard "player1"          # Get score

# Remove
ZREM leaderboard "player1"            # Remove member
ZREMRANGEBYRANK leaderboard 10 -1     # Keep only top 10
```

**Java Implementation - Leaderboard:**

```java
public class RedisLeaderboardService {
    private final JedisPool jedisPool;

    public RedisLeaderboardService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
  
    // Update player score
    public void updateScore(String leaderboardName, String playerId, double points) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zincrby(leaderboardName, points, playerId);
        }
    }
  
    // Get top N players
    public List<PlayerScore> getTopPlayers(String leaderboardName, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<Tuple> results = jedis.zrevrangeWithScores(leaderboardName, 0, count - 1);
        
            List<PlayerScore> players = new ArrayList<>();
            int rank = 1;
            for (Tuple tuple : results) {
                players.add(new PlayerScore(
                    tuple.getElement(),
                    tuple.getScore(),
                    rank++
                ));
            }
            return players;
        }
    }
  
    // Get player rank
    public Long getPlayerRank(String leaderboardName, String playerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long rank = jedis.zrevrank(leaderboardName, playerId);
            return rank != null ? rank + 1 : null; // Convert to 1-based
        }
    }
  
    // Get players around a specific player
    public List<PlayerScore> getPlayersAround(String leaderboardName, String playerId, int range) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long rank = jedis.zrevrank(leaderboardName, playerId);
            if (rank == null) {
                return Collections.emptyList();
            }
        
            long start = Math.max(0, rank - range);
            long end = rank + range;
        
            Set<Tuple> results = jedis.zrevrangeWithScores(leaderboardName, start, end);
        
            List<PlayerScore> players = new ArrayList<>();
            long currentRank = start + 1;
            for (Tuple tuple : results) {
                players.add(new PlayerScore(
                    tuple.getElement(),
                    tuple.getScore(),
                    currentRank++
                ));
            }
            return players;
        }
    }
  
    // Trending posts (decay over time)
    public void addPost(String postId, double initialScore) {
        String key = "trending_posts";
        double timestamp = System.currentTimeMillis();
        double score = initialScore / (Math.pow((timestamp - 1609459200000.0) / 1000, 1.5));
    
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(key, score, postId);
        }
    }
  
    // Time-based ranking (scheduled tasks)
    public void scheduleTask(String taskId, long executionTimestamp) {
        String key = "scheduled_tasks";
    
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(key, executionTimestamp, taskId);
        }
    }
  
    public List<String> getDueTasks(long currentTimestamp) {
        String key = "scheduled_tasks";
    
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> tasks = jedis.zrangeByScore(key, 0, currentTimestamp);
        
            // Remove processed tasks
            if (!tasks.isEmpty()) {
                jedis.zremrangeByScore(key, 0, currentTimestamp);
            }
        
            return new ArrayList<>(tasks);
        }
    }
}

class PlayerScore {
    private String playerId;
    private double score;
    private long rank;

    public PlayerScore(String playerId, double score, long rank) {
        this.playerId = playerId;
        this.score = score;
        this.rank = rank;
    }
  
    // Getters
    public String getPlayerId() { return playerId; }
    public double getScore() { return score; }
    public long getRank() { return rank; }
}
```

**Use Cases:**

* Leaderboards and rankings
* Priority queues
* Trending content
* Time-series data
* Task scheduling
* Auto-complete

## Advanced Redis Use Cases

### 1. Distributed Locking (Redlock Algorithm)

Distributed locks ensure only one process executes critical section across multiple servers.
1. **Cross-Server Coordination (The "Multiple JVM" Problem)**
   Standard Java locks (synchronized) only work on one machine. In a cluster with multiple servers, Redis acts as a global shared memory to coordinate threads across different machines.

2. **Preventing Race Conditions (Data Integrity)**
   It prevents Double Booking. When multiple users try to buy the last item simultaneously, Redis forces them to line up, ensuring only one person can modify the data at a time.

3. **Protecting the Database (Performance Shield)**
   Database row locks (SELECT FOR UPDATE) are slow and heavy. Redis locks are lightweight and in-memory, handling the high-traffic queuing so your database doesn't crash under load.

4. **Preventing Duplicate Jobs (Cron Jobs)**
   It ensures scheduled tasks (like "Daily Email Report") run exactly once. Without it, every server in your cluster would wake up and run the same job, spamming users.

5. **Deadlock Safety (Auto-Expiry)**
   Redis locks have a Time-To-Live (TTL). If a server crashes while holding a lock, Redis automatically deletes it after a few seconds, preventing the system from freezing forever (Self-Healing).

6. **Efficiency (Fail Fast)**
   Redis can check lock status in microseconds. If a resource is busy, it lets you reject the request immediately ("System Busy") instead of wasting server threads waiting in a long queue.
**Java Implementation: with Redis**

```java
public class RedisDistributedLock {
    private final JedisPool jedisPool;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    public RedisDistributedLock(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
  
    /**
     * Acquire lock with automatic expiration
     * @param lockKey Lock identifier
     * @param requestId Unique request ID (prevents unlock by wrong client)
     * @param expireTimeMs Lock expiration in milliseconds
     * @return true if lock acquired
     */
    public boolean tryLock(String lockKey, String requestId, long expireTimeMs) {
        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTimeMs);
            return LOCK_SUCCESS.equals(result);
        }
    }
  
    /**
     * Release lock safely (only if owned by this client)
     */
    public boolean unlock(String lockKey, String requestId) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Lua script ensures atomic check-and-delete
            String script = 
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "    return redis.call('del', KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end";
        
            Object result = jedis.eval(script, 
                Collections.singletonList(lockKey), 
                Collections.singletonList(requestId));
        
            return Long.valueOf(1).equals(result);
        }
    }
  
    /**
     * Try lock with retry
     */
    public boolean tryLockWithRetry(String lockKey, String requestId, 
                                    long expireTimeMs, int maxRetries, 
                                    long retryDelayMs) {
        for (int i = 0; i < maxRetries; i++) {
            if (tryLock(lockKey, requestId, expireTimeMs)) {
                return true;
            }
        
            try {
                Thread.sleep(retryDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
  
    /**
     * Execute code with distributed lock
     */
    public <T> T executeWithLock(String lockKey, long lockTimeMs, 
                                  Supplier<T> task) {
        String requestId = UUID.randomUUID().toString();
    
        if (!tryLockWithRetry(lockKey, requestId, lockTimeMs, 3, 100)) {
            throw new RuntimeException("Failed to acquire lock: " + lockKey);
        }
    
        try {
            return task.get();
        } finally {
            unlock(lockKey, requestId);
        }
    }
}

// Usage example
public class InventoryService {
    private final RedisDistributedLock distributedLock;
    private final JedisPool jedisPool;

    public boolean purchaseProduct(String productId, String userId, int quantity) {
        String lockKey = "lock:product:" + productId;
    
        return distributedLock.executeWithLock(lockKey, 5000, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                String stockKey = "stock:" + productId;
                String currentStock = jedis.get(stockKey);
            
                if (currentStock == null) {
                    return false;
                }
            
                int stock = Integer.parseInt(currentStock);
                if (stock < quantity) {
                    return false; // Insufficient stock
                }
            
                // Deduct stock
                jedis.decrBy(stockKey, quantity);
            
                // Record purchase
                String purchaseKey = "purchases:" + userId;
                jedis.rpush(purchaseKey, productId + ":" + quantity);
            
                return true;
            }
        });
    }
}
```

**Java Implementation: with Redisson**

```java
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class InventoryService {

    private final RedissonClient redissonClient;

    public InventoryService() {
        Config config = new Config();

        // 1. Configure for Single Server (standard setup)
        // For Cluster, use config.useClusterServers()...
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setConnectionPoolSize(64) // Like 'maxTotal' in Jedis
                .setConnectionMinimumIdleSize(24); // Like 'minIdle' in Jedis
      
        this.redissonClient = Redisson.create(config);
    }

    public boolean purchaseProduct(String productId, int quantity) {
        String lockKey = "lock:product:" + productId;
      
        // 1. Get the Lock Object (Just an object, doesn't lock yet)
        RLock lock = redissonClient.getLock(lockKey);
      
        boolean isLocked = false;
        try {
            // 2. Try to acquire the lock
            // waitTime = 10s (How long to wait for the lock to be free)
            // leaseTime = -1 (See "Watchdog" explanation below!)
            isLocked = lock.tryLock(10, TimeUnit.SECONDS);

            if (isLocked) {
                // --- CRITICAL SECTION START ---
                // You are now the ONLY thread across ALL servers executing this code.
              
                // Simulate DB check & update
                System.out.println("Checking stock for " + productId);
                Thread.sleep(1000); // Simulate work
                System.out.println("Stock deducted!");
              
                return true;
                // --- CRITICAL SECTION END ---
            } else {
                System.out.println("Could not acquire lock. System busy.");
                return false;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            // 3. Safe Unlock
            // We must check if WE hold the lock before unlocking
            // (Redisson handles the unique ID check internally)
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```

**The "Magic" Feature: The Watchdog**
You might have noticed I didn't set a specific expiration time (TTL) in tryLock. I used the default behavior.
The Problem with Manual TTL: If you set a lock for 5 seconds (SET NX PX 5000), but your database query takes 6 seconds, the lock expires mid-task. Another server enters, and you get data corruption.

**The Redisson Solution (Watchdog):**

* When you acquire a lock without specifying a leaseTime, Redisson assigns a default 30-second TTL.
* Background Thread: Redisson spawns a background thread (the Watchdog) that wakes up every 10 seconds.
* Check: It asks: "Is the main thread still running and holding this lock?"
* Action: If YES, it resets the Redis TTL back to 30 seconds.
* Result: The lock never expires as long as your code is running correctly. If your server crashes (power cut), the Watchdog dies, and Redis deletes the lock after the remaining 30 seconds.

**Java Implementation: with Lettuce**

```java
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LettuceDistributedLock {

    private final RedisCommands<String, String> redisCommands;

    // Setup connection
    public LettuceDistributedLock() {
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        this.redisCommands = connection.sync(); // We use synchronous commands for simplicity
    }

    /**
     * Try to acquire the lock
     */
    public boolean tryLock(String lockKey, String requestId, long expireTimeMs) {
        // SetArgs replaces the complex "NX", "PX" string arguments from Jedis
        SetArgs args = SetArgs.Builder.nx().px(expireTimeMs);

        String result = redisCommands.set(lockKey, requestId, args);

        return "OK".equals(result);
    }

    /**
     * Release the lock safely using Lua Script
     */
    public boolean unlock(String lockKey, String requestId) {
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "    return redis.call('del', KEYS[1]) " +
                        "else " +
                        "    return 0 " +
                        "end";

        // Lettuce executes scripts slightly differently
        Object result = redisCommands.eval(
                script,
                ScriptOutputType.INTEGER,
                new String[]{lockKey}, // Keys
                requestId              // Values (Args)
        );

        return result.equals(1L);
    }

    // Usage helper
    public void execute(String lockKey, Runnable task) {
        String reqId = UUID.randomUUID().toString();
        if(tryLock(lockKey, reqId, 5000)) {
            try {
                task.run();
            } finally {
                unlock(lockKey, reqId);
            }
        } else {
            throw new RuntimeException("Could not get lock");
        }
    }
}
```

**Why is Lettuce "Thread Safe"?**
This is the biggest advantage of Lettuce over Jedis.

* Jedis: Uses a standard socket. If Thread A is writing, Thread B cannot use that socket. You need a pool of 50 connections for 50 threads.
* Lettuce: Uses Netty (Event Loop). It "pipelines" requests. Thread A, Thread B, and Thread C can all shove commands into one single connection simultaneously. The commands fly to Redis, and when the answers come back, Lettuce sorts them out and gives the right answer to the right thread.

Result: You usually only need 1 Connection for your entire application.

> **Important:** Distributed locks are complex and have edge cases. Only use when database-level consistency is insufficient.

### 2. Rate Limiting

Multiple strategies for limiting request rates.

**Fixed Window Counter:**
1. The Core Concept
* Time Buckets: Time is divided into fixed, non-overlapping blocks (e.g., 00:00–00:01, 00:01–00:02).
* Fresh Start: Every time a new window (bucket) begins, the user's counter resets to zero immediately.
* Key Logic: It ignores past history beyond the current specific window.

2. The Implementation (Redis)
* Key Formula: Key = "user:" + (CurrentTime / WindowSize)
This integer division automatically groups all timestamps within a window to the same integer ID.
* Atomic Counting: Uses Redis INCR to count requests safely in high concurrency.
* Cleanup: Uses Redis EXPIRE (TTL) on the key to ensure old window data is automatically deleted to save RAM.

3. Pros (Why use it?)
* Memory Efficient: Stores only one integer counter per user. Very cheap.
* Performance: Requires only 1 round-trip to Redis (INCR).
* Simplicity: Easiest algorithm to code and debug.

4. Cons (The Flaw)
* The "Edge Burst" Issue: A user can send the max limit at the end of one window and the start of the next.
* Example: Limit 10/min. Sending 10 requests at 10:59 and 10 requests at 11:01 results in 20 requests in 2 seconds.
* Usage: Good for general API protection; bad for strict, uniform traffic smoothing.
```java
public class FixedWindowRateLimiter {
    private final JedisPool jedisPool;

    public boolean allowRequest(String userId, int maxRequests, int windowSeconds) {
        String key = "rate:" + userId + ":" + (System.currentTimeMillis() / 1000 / windowSeconds);
    
        try (Jedis jedis = jedisPool.getResource()) {
            Long current = jedis.incr(key);
        
            if (current == 1) {
                jedis.expire(key, windowSeconds);
            }
        
            return current <= maxRequests;
        }
    }
}
```

**Sliding Window Log (More Accurate):**

```java
public class SlidingWindowRateLimiter {
    private final JedisPool jedisPool;

    public boolean allowRequest(String userId, int maxRequests, int windowSeconds) {
        String key = "rate:log:" + userId;
        long now = System.currentTimeMillis();
        long windowStart = now - (windowSeconds * 1000L);
    
        try (Jedis jedis = jedisPool.getResource()) {
            // Remove old entries
            jedis.zremrangeByScore(key, 0, windowStart);
        
            // Count current requests
            Long count = jedis.zcount(key, windowStart, now);
        
            if (count < maxRequests) {
                // Add current request
                jedis.zadd(key, now, UUID.randomUUID().toString());
                jedis.expire(key, windowSeconds);
                return true;
            }
        
            return false;
        }
    }
}
```

**Token Bucket (Smooth Rate):**
* Scenario:
Rate: 1 request/sec.
Bucket Size: 10.

* User Behavior: The user stays silent for 10 seconds.
The bucket fills up to 10 tokens.

* The Burst: Suddenly, the user sends 10 requests in 1 second.
Token Bucket: ALLOWS all 10 (because the bucket is full).
Leaky Bucket / Fixed Window: Might block them.

* After the Burst: The bucket is empty. Now the user is forced to wait for the refill rate (1 req/sec).
```java
public class TokenBucketRateLimiter {
    private final JedisPool jedisPool;

    public boolean allowRequest(String userId, int bucketSize, double refillRate) {
        String key = "rate:bucket:" + userId;
        long now = System.currentTimeMillis();
    
        try (Jedis jedis = jedisPool.getResource()) {
            String script = 
                "local tokens = tonumber(redis.call('hget', KEYS[1], 'tokens')) " +
                "local last_refill = tonumber(redis.call('hget', KEYS[1], 'last_refill')) " +
                "local now = tonumber(ARGV[1]) " +
                "local bucket_size = tonumber(ARGV[2]) " +
                "local refill_rate = tonumber(ARGV[3]) " +
                "" +
                "if tokens == nil then " +
                "    tokens = bucket_size " +
                "    last_refill = now " +
                "end " +
                "" +
                "local elapsed = (now - last_refill) / 1000 " +
                "local new_tokens = math.min(bucket_size, tokens + (elapsed * refill_rate)) " +
                "" +
                "if new_tokens >= 1 then " +
                "    redis.call('hset', KEYS[1], 'tokens', new_tokens - 1) " +
                "    redis.call('hset', KEYS[1], 'last_refill', now) " +
                "    redis.call('expire', KEYS[1], 3600) " +
                "    return 1 " +
                "else " +
                "    redis.call('hset', KEYS[1], 'tokens', new_tokens) " +
                "    redis.call('hset', KEYS[1], 'last_refill', now) " +
                "    return 0 " +
                "end";
        
            Object result = jedis.eval(script,
                Collections.singletonList(key),
                Arrays.asList(String.valueOf(now), String.valueOf(bucketSize), String.valueOf(refillRate)));
        
            return Long.valueOf(1).equals(result);
        }
    }
}
```

### 3. Pub/Sub Messaging

Redis Pub/Sub enables real-time messaging between publishers and subscribers.

**Publisher:**

```java
public class RedisPublisher {
    private final JedisPool jedisPool;

    public void publish(String channel, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channel, message);
        }
    }
  
    // Notification system
    public void notifyUser(String userId, String notification) {
        String channel = "notifications:" + userId;
        publish(channel, notification);
    }
  
    // Broadcast to all
    public void broadcast(String message) {
        publish("global", message);
    }
}
```

**Subscriber:**

```java
public class RedisSubscriber extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        System.out.println("Received on " + channel + ": " + message);
        // Process message
    }
  
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println("Subscribed to " + channel);
    }
  
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println("Unsubscribed from " + channel);
    }
}

public class SubscriberService {
    private final JedisPool jedisPool;

    public void startListening(String... channels) {
        new Thread(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                RedisSubscriber subscriber = new RedisSubscriber();
                jedis.subscribe(subscriber, channels);
            }
        }).start();
    }
}
```

> **Important:** Pub/Sub messages are fire-and-forget. If no subscriber is listening, message is lost. For reliable messaging, use Redis Streams instead.

### 4. Redis Streams (Message Queue with Persistence)

Streams provide durable, ordered message queues with consumer groups.

**Producer:**

```java
public class RedisStreamProducer {
    private final JedisPool jedisPool;

    public String addMessage(String streamKey, Map<String, String> message) {
        try (Jedis jedis = jedisPool.getResource()) {
            // "*" means auto-generate ID based on timestamp
            StreamEntryID id = jedis.xadd(streamKey, StreamEntryID.NEW_ENTRY, message);
            return id.toString();
        }
    }
  
    // Order processing
    public void submitOrder(Order order) {
        Map<String, String> orderData = new HashMap<>();
        orderData.put("orderId", order.getId());
        orderData.put("userId", order.getUserId());
        orderData.put("amount", String.valueOf(order.getAmount()));
        orderData.put("timestamp", String.valueOf(System.currentTimeMillis()));
    
        addMessage("orders_stream", orderData);
    }
}
```

**Consumer with Consumer Groups:**

```java
public class RedisStreamConsumer {
    private final JedisPool jedisPool;
    private final String streamKey;
    private final String consumerGroup;
    private final String consumerName;

    public RedisStreamConsumer(JedisPool jedisPool, String streamKey, 
                               String consumerGroup, String consumerName) {
        this.jedisPool = jedisPool;
        this.streamKey = streamKey;
        this.consumerGroup = consumerGroup;
        this.consumerName = consumerName;
    
        createConsumerGroup();
    }
  
    private void createConsumerGroup() {
        try (Jedis jedis = jedisPool.getResource()) {
            try {
                jedis.xgroupCreate(streamKey, consumerGroup, StreamEntryID.LAST_ENTRY, true);
            } catch (Exception e) {
                // Group already exists
            }
        }
    }
  
    public void startConsuming() {
        try (Jedis jedis = jedisPool.getResource()) {
            while (true) {
                // Read new messages
                Map<String, StreamEntryID> streamQuery = new HashMap<>();
                streamQuery.put(streamKey, StreamEntryID.UNRECEIVED_ENTRY);
            
                List<Map.Entry<String, List<StreamEntry>>> result = 
                    jedis.xreadGroup(consumerGroup, consumerName, 1, 2000, false, streamQuery);
            
                if (result != null && !result.isEmpty()) {
                    for (Map.Entry<String, List<StreamEntry>> entry : result) {
                        for (StreamEntry streamEntry : entry.getValue()) {
                            processMessage(streamEntry);
                        
                            // Acknowledge processing
                            jedis.xack(streamKey, consumerGroup, streamEntry.getID());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    private void processMessage(StreamEntry entry) {
        System.out.println("Processing message: " + entry.getID());
        Map<String, String> fields = entry.getFields();
    
        // Process order
        String orderId = fields.get("orderId");
        String amount = fields.get("amount");
        // ... business logic
    }
  
    // Handle failed messages
    public void processPendingMessages() {
        try (Jedis jedis = jedisPool.getResource()) {
            // Get pending messages (not acknowledged)
            List<StreamPendingEntry> pending = 
                jedis.xpending(streamKey, consumerGroup, null, null, 10, consumerName);
        
            for (StreamPendingEntry entry : pending) {
                // Reclaim and reprocess
                StreamEntryID id = entry.getID();
                List<StreamEntry> claimed = 
                    jedis.xclaim(streamKey, consumerGroup, consumerName, 
                                60000, entry.getID()); // 1 minute timeout
            
                for (StreamEntry streamEntry : claimed) {
                    processMessage(streamEntry);
                    jedis.xack(streamKey, consumerGroup, streamEntry.getID());
                }
            }
        }
    }
}

class Order {
    private String id;
    private String userId;
    private double amount;

    public Order(String id, String userId, double amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }
  
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public double getAmount() { return amount; }
}
```

**Streams vs Pub/Sub:**

* Streams persist messages (can read history)
* Consumer groups enable load balancing
* Acknowledgment system prevents message loss
* Supports multiple consumers per group
* Can replay messages from any point

### 5. Geospatial Operations

Store and query location data efficiently.

**Java Implementation:**

```java
public class RedisGeoService {
    private final JedisPool jedisPool;

    // Add location
    public void addLocation(String key, double longitude, double latitude, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.geoadd(key, longitude, latitude, member);
        }
    }
  
    // Add restaurant
    public void addRestaurant(String restaurantId, double longitude, double latitude) {
        addLocation("restaurants", longitude, latitude, restaurantId);
    }
  
    // Find nearby restaurants
    public List<GeoRadiusResponse> findNearbyRestaurants(double longitude, double latitude, 
                                                          double radius, GeoUnit unit) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.georadius("restaurants", longitude, latitude, radius, unit,
                GeoRadiusParam.geoRadiusParam().withDist().sortAscending().count(10));
        }
    }
  
    // Find delivery drivers near order
    public List<String> findAvailableDrivers(double orderLongitude, double orderLatitude, 
                                             double radiusKm) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<GeoRadiusResponse> responses = jedis.georadius("available_drivers", 
                orderLongitude, orderLatitude, radiusKm, GeoUnit.KM);
        
            return responses.stream()
                .map(GeoRadiusResponse::getMemberByString)
                .collect(Collectors.toList());
        }
    }
  
    // Calculate distance between two locations
    public Double getDistance(String key, String member1, String member2, GeoUnit unit) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.geodist(key, member1, member2, unit);
        }
    }
  
    // Get coordinates
    public List<GeoCoordinate> getCoordinates(String key, String... members) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.geopos(key, members);
        }
    }
}
```

**Use Cases:**

* Ride-sharing (find nearby drivers)
* Food delivery (find restaurants in radius)
* Store locator
* Friend proximity features
* Asset tracking

## Redis Scaling Strategies

### 1. Master-Slave Replication

Master handles writes, slaves handle reads.

**Setup:**

```bash
# On slave server
replicaof <master-ip> <master-port>
replica-read-only yes
```

**Java Connection:**

```java
public class RedisReplicationSetup {
    private final JedisPool masterPool;
    private final List<JedisPool> slavePools;
    private final AtomicInteger roundRobin = new AtomicInteger(0);

    public void write(String key, String value) {
        try (Jedis jedis = masterPool.getResource()) {
            jedis.set(key, value);
        }
    }
  
    public String read(String key) {
        // Load balance reads across slaves
        int index = Math.abs(roundRobin.getAndIncrement() % slavePools.size());
    
        try (Jedis jedis = slavePools.get(index).getResource()) {
            return jedis.get(key);
        }
    }
}
```

### 2. Redis Sentinel (High Availability)

Sentinel monitors master/slave and performs automatic failover.

**Java with Sentinel:**

```java
public class RedisSentinelSetup {
    private final JedisSentinelPool sentinelPool;

    public RedisSentinelSetup() {
        Set<String> sentinels = new HashSet<>();
        sentinels.add("sentinel1:26379");
        sentinels.add("sentinel2:26379");
        sentinels.add("sentinel3:26379");
    
        this.sentinelPool = new JedisSentinelPool("mymaster", sentinels);
    }
  
    public void execute(Consumer<Jedis> operation) {
        try (Jedis jedis = sentinelPool.getResource()) {
            operation.accept(jedis);
        }
    }
}
```

### 3. Redis Cluster (Sharding)

Distributes data across multiple nodes using hash slots.

**Key Selection is Critical:**
Redis uses consistent hashing with 16384 hash slots. Keys with same hash tag go to same slot.

```bash
# These go to same slot (use {user:1000} as hash tag)
user:{user:1000}:profile
user:{user:1000}:preferences
user:{user:1000}:cart
```

**Java with Cluster:**

```java
public class RedisClusterSetup {
    private final JedisCluster jedisCluster;

    public RedisClusterSetup() {
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("node1", 7000));
        nodes.add(new HostAndPort("node2", 7001));
        nodes.add(new HostAndPort("node3", 7002));
    
        this.jedisCluster = new JedisCluster(nodes);
    }
  
    // Multi-key operations require hash tags
    public void atomicUserUpdate(String userId) {
        String hashTag = "{user:" + userId + "}";
    
        // These keys will be on same node
        jedisCluster.set(hashTag + ":name", "Rajesh");
        jedisCluster.set(hashTag + ":email", "rajesh@example.com");
    
        // Can use multi/exec on same node
        Pipeline pipeline = jedisCluster.pipelined();
        pipeline.get(hashTag + ":name");
        pipeline.get(hashTag + ":email");
        pipeline.sync();
    }
}
```

> **Important:** Choosing hash tags is how you scale Redis. Group related keys together for efficient multi-key operations.

## Common Problems and Solutions

### 1. Hot Key Problem

One key receives disproportionate traffic, overwhelming single node.

**Solutions:**

```java
public class HotKeyMitigation {
    private final JedisCluster cluster;
    private final LoadingCache<String, String> localCache;

    public HotKeyMitigation(JedisCluster cluster) {
        this.cluster = cluster;
        this.localCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(this::fetchFromRedis);
    }
  
    // Solution 1: Local cache for hot keys
    public String getWithLocalCache(String key) {
        return localCache.get(key);
    }
  
    private String fetchFromRedis(String key) {
        return cluster.get(key);
    }
  
    // Solution 2: Key replication
    public void setHotKey(String key, String value, int replicas) {
        for (int i = 0; i < replicas; i++) {
            cluster.set(key + ":replica:" + i, value);
        }
    }
  
    public String getHotKey(String key, int replicas) {
        int replica = ThreadLocalRandom.current().nextInt(replicas);
        return cluster.get(key + ":replica:" + replica);
    }
}
```

**Alternative Solutions:**

* Add in-memory cache in clients
* Store same data in multiple keys and randomize requests
* Add read replica instances and scale dynamically

### 2. Big Key Problem

Large keys (>10KB) can block Redis and cause memory issues.

**Solutions:**

```java
public class BigKeyMitigation {
    private final Jedis jedis;

    // Split large hash into multiple smaller hashes
    public void saveLargeObject(String objectId, Map<String, String> largeData) {
        int chunkSize = 100;
        List<List<Map.Entry<String, String>>> chunks = 
            Lists.partition(new ArrayList<>(largeData.entrySet()), chunkSize);
    
        for (int i = 0; i < chunks.size(); i++) {
            String chunkKey = objectId + ":chunk:" + i;
            Map<String, String> chunkMap = chunks.get(i).stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
            jedis.hset(chunkKey, chunkMap);
        }
    
        // Store metadata
        jedis.set(objectId + ":chunks", String.valueOf(chunks.size()));
    }
  
    public Map<String, String> loadLargeObject(String objectId) {
        int chunks = Integer.parseInt(jedis.get(objectId + ":chunks"));
        Map<String, String> result = new HashMap<>();
    
        for (int i = 0; i < chunks; i++) {
            String chunkKey = objectId + ":chunk:" + i;
            result.putAll(jedis.hgetAll(chunkKey));
        }
    
        return result;
    }
}
```

### 3. Cache Penetration

Queries for non-existent keys bypass cache and hit database repeatedly.

**Solution: Bloom Filter**

```java
public class BloomFilterCache {
    private final Jedis jedis;
    private final BloomFilter<String> bloomFilter;

    public BloomFilterCache(Jedis jedis) {
        this.jedis = jedis;
        this.bloomFilter = BloomFilter.create(
            Funnels.stringFunnel(Charset.defaultCharset()),
            1000000,
            0.01
        );
    }
  
    public void addKey(String key) {
        bloomFilter.put(key);
    }
  
    public String get(String key) {
        // Check bloom filter first
        if (!bloomFilter.mightContain(key)) {
            return null; // Definitely doesn't exist
        }
    
        // Might exist, check Redis
        String value = jedis.get(key);
    
        if (value == null) {
            // Cache null values temporarily
            jedis.setex(key, 60, "NULL");
        }
    
        return "NULL".equals(value) ? null : value;
    }
}
```

## Production Best Practices

### Connection Pooling

```java
public class RedisConnectionPool {
    private static JedisPool jedisPool;

    public static JedisPool getPool(String host, int port) {
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
        
            // Pool settings
            config.setMaxTotal(200);                // Max connections
            config.setMaxIdle(50);                  // Max idle connections
            config.setMinIdle(10);                  // Min idle connections
            config.setMaxWaitMillis(3000);          // Max wait for connection
        
            // Test connections
            config.setTestOnBorrow(true);           // Test before use
            config.setTestOnReturn(false);
            config.setTestWhileIdle(true);
        
            // Eviction policy
            config.setTimeBetweenEvictionRunsMillis(30000);
            config.setMinEvictableIdleTimeMillis(60000);
        
            jedisPool = new JedisPool(config, host, port, 2000, null);
        }
        return jedisPool;
    }
}
```

### Error Handling and Retry

```java
public class ResilientRedisClient {
    private final JedisPool pool;
    private final int maxRetries = 3;

    public <T> T executeWithRetry(Function<Jedis, T> operation) {
        int attempts = 0;
        Exception lastException = null;
    
        while (attempts < maxRetries) {
            try (Jedis jedis = pool.getResource()) {
                return operation.apply(jedis);
            } catch (JedisConnectionException e) {
                lastException = e;
                attempts++;
            
                if (attempts < maxRetries) {
                    try {
                        Thread.sleep((long) Math.pow(2, attempts) * 100); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry", ie);
                    }
                }
            }
        }
    
        throw new RuntimeException("Failed after " + maxRetries + " attempts", lastException);
    }
}
```

### Monitoring and Metrics

```java
public class RedisMetrics {
    private final JedisPool pool;

    public Map<String, Object> getMetrics() {
        try (Jedis jedis = pool.getResource()) {
            String info = jedis.info();
            Map<String, Object> metrics = new HashMap<>();
        
            // Parse info command output
            String[] lines = info.split("\r\n");
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":");
                    metrics.put(parts[0], parts[1]);
                }
            }
        
            return metrics;
        }
    }
  
    public void logSlowQueries() {
        try (Jedis jedis = pool.getResource()) {
            List<Slowlog> slowlogs = jedis.slowlogGet(10);
        
            for (Slowlog log : slowlogs) {
                System.out.println("Slow query: " + log.getArgs() + 
                                 " took " + log.getExecutionTime() + "μs");
            }
        }
    }
}
```
