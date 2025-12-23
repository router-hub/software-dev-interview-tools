package com.redis.learning.publisherSubscriber;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubscriberClass {
    private final JedisPool jedisPool ;
    // Create a dedicated pool with exactly 1 thread
    private final ExecutorService listenerExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "redis-subscriber-thread");
        t.setDaemon(true); // Ensures thread dies if the app stops
        return t;
    });

    public SubscriberClass(String host, Integer port){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMinIdle(1);

        this.jedisPool = new JedisPool(config, host, port);
    }

    public void subscribe(String ...channels){
        listenerExecutor.submit(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                RedisSubscriber redisSubscriber = new RedisSubscriber();
                redisSubscriber.subscribe(channels);
            }
        });
    }
    // Call this when Spring shuts down
    public void stop() {
        listenerExecutor.shutdownNow();
    }

}
