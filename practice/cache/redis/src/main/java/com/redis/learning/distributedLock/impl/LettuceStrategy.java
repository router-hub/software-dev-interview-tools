package com.redis.learning.distributedLock.impl;

import com.redis.learning.distributedLock.DistributedLockStrategy;
import com.redis.learning.distributedLock.LockType;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.logging.log4j.util.Supplier;

import java.util.UUID;

public class LettuceStrategy implements DistributedLockStrategy {
    private final RedisCommands<String, String> redisCommands;
    private static final String LOCK_SUCCESS = "OK";
    private static final String UNLOCK_SCRIPT = """
        if redis.call('get', KEYS[1]) == ARGV[1] then
            return redis.call('del', KEYS[1])
        else
            return 0
        end
        """;


    // Setup connection
    public LettuceStrategy(String url) {
        RedisClient redisClient = RedisClient.create(url);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        this.redisCommands = connection.sync(); // We use synchronous commands for simplicity
    }
    private boolean tryLock(String key, long expiredMs, String requestId){
        // SetArgs replaces the complex "NX", "PX" string arguments from Jedis
        SetArgs args = SetArgs.Builder.nx().px(expiredMs);

        String res = redisCommands.set(key, requestId, args);
        return LOCK_SUCCESS.equals(res);
    }

    private boolean lockWithRetry(String key, String requestId, long expiryMs, long waitTime, long sleep){
        long endTime = System.currentTimeMillis() + waitTime;
        while(System.currentTimeMillis() < endTime){
            if(tryLock(key, expiryMs, requestId)){
                return true;
            }
            try{
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the flag
                return false;
            }
        }
        return false;
    }

    private boolean unlock(String key, String requestId){
        Object result = redisCommands.eval(
                UNLOCK_SCRIPT,
                ScriptOutputType.INTEGER,
                new String[]{key}, // Keys
                requestId              // Values (Args)
        );

        return result.equals(1L);
    }

    @Override
    public <T> T executeLock(String key, long waitTime, long leaseTime, Supplier<T> task) {
        String requestId = UUID.randomUUID().toString();

        if(!lockWithRetry(key, requestId, leaseTime, waitTime, 100))
            throw new RuntimeException("Failed to acquire lock: " + key);
        try{
            return task.get();
        }finally {
            unlock(key, requestId);
        }
    }

    @Override
    public LockType getLockType() {
        return LockType.LETTUCE;
    }
}
