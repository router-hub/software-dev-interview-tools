package com.redis.learning.rateLimiter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.UUID;

public class SlidingWindowRateLimiter {
    private final JedisPool jedisPool ;
    private final int maxRequestLimitAllowed;

    public SlidingWindowRateLimiter(String host, Integer port, int maxRequestLimitAllowed){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMinIdle(1);

        this.jedisPool = new JedisPool(config, host, port);
        this.maxRequestLimitAllowed = maxRequestLimitAllowed;
    }

    public boolean requestAccepted(String userId, int windowSize){
        String key = "rate:log:" + userId;
        long currentTime = System.currentTimeMillis();
        long expiredTime = currentTime - (windowSize * 1000L);
        try (Jedis jedis = jedisPool.getResource()){
            // remove till expired time
            jedis.zremrangeByRank(key, 0, expiredTime);
            long currentCount = jedis.zcount(key, expiredTime, currentTime);

            if ( currentCount <= maxRequestLimitAllowed){
                jedis.zadd(key, currentTime, UUID.randomUUID().toString());
                jedis.expire(key, windowSize);
                return true;
            }
            return false;
        }
    }
}
