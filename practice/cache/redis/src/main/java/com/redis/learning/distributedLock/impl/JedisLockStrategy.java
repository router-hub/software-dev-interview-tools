package com.redis.learning.distributedLock.impl;

import com.redis.learning.distributedLock.DistributedLockStrategy;
import com.redis.learning.distributedLock.LockType;
import org.apache.logging.log4j.util.Supplier;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.UUID;

public class JedisLockStrategy implements DistributedLockStrategy {
    private JedisPool jedisPool;
    private static final String LOCK_SUCCESS = "OK";
    private static final String UNLOCK_SCRIPT = """
        if redis.call('get', KEYS[1]) == ARGV[1] then
            return redis.call('del', KEYS[1])
        else
            return 0
        end
        """;

    public JedisLockStrategy(String host, Integer port){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setMinIdle(1);
        this.jedisPool = new JedisPool(jedisPoolConfig, host, port);
    }

    private boolean tryLock(String key, long expiredMs, String requestId){
        try (Jedis jedis = jedisPool.getResource()){
            SetParams params = SetParams.setParams().nx().px(expiredMs);
            String res = jedis.set(key, requestId, params);
            return LOCK_SUCCESS.equals(res);
        }
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
        try(Jedis jedis = jedisPool.getResource()){
            Object res = jedis.eval(UNLOCK_SCRIPT, Collections.singletonList(key), Collections.singletonList(requestId));
            return Long.valueOf(1).equals(res);
        }
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
        return LockType.JEDIS;
    }
}
