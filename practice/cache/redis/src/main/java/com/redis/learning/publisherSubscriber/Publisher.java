package com.redis.learning.publisherSubscriber;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Publisher {
    private final JedisPool jedisPool ;

    Publisher(String host, Integer port){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMinIdle(1);

        this.jedisPool = new JedisPool(config, host, port);
    }

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
