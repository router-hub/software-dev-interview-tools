package com.redis.learning.distributedLock.impl;

import com.redis.learning.distributedLock.DistributedLockStrategy;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import com.redis.learning.distributedLock.LockType;
import org.apache.logging.log4j.util.Supplier;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class RedissonStrategy implements DistributedLockStrategy {
    private final RedissonClient redissonClient;

    public RedissonStrategy(String url) {
        Config config = new Config();

        // 1. Configure for Single Server (standard setup)
        // For Cluster, use config.useClusterServers()...
        config.useSingleServer()
                .setAddress(url)
                .setConnectionPoolSize(10) // Like 'maxTotal' in Jedis
                .setConnectionMinimumIdleSize(1); // Like 'minIdle' in Jedis

        this.redissonClient = Redisson.create(config);
    }

    @Override
    public <T> T executeLock(String key, long waitTime, long leaseTime, Supplier<T> task) {
        RLock lock = redissonClient.getLock(key);
        try{
            if(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)){
                try{
                    return task.get();
                }finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        throw new RuntimeException("Could not acquire lock");
    }

    @Override
    public LockType getLockType() {
        return LockType.REDISSON;
    }
}
