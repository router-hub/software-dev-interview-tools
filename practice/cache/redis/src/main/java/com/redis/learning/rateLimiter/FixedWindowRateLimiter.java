package com.redis.learning.rateLimiter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class FixedWindowRateLimiter {
    private final JedisPool jedisPool ;
    private final int maxRequestLimitAllowed;

    public FixedWindowRateLimiter(String host, Integer port, int maxRequestLimitAllowed){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMinIdle(1);

        this.jedisPool = new JedisPool(config, host, port);
        this.maxRequestLimitAllowed = maxRequestLimitAllowed;
    }

    public boolean requestAccepted(String userId, int windowSize){
        String limitKey = "lock:" + userId + ":" + (System.currentTimeMillis()/1000/windowSize);
        try(Jedis jedis = jedisPool.getResource()){
            long currentCount = jedis.incrBy(limitKey, 1);
            if (currentCount == 1){
               jedis.expire(limitKey, windowSize);
            }

            return currentCount <= maxRequestLimitAllowed;
        }
    }
}
