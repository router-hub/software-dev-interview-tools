package com.redis.learning.stream;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.StreamEntryID;

import java.util.HashMap;
import java.util.Map;

public class RedisPublisher {
    private final JedisPool jedisPool ;

    public RedisPublisher(String host, Integer port){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(5);
        config.setMinIdle(1);

        this.jedisPool = new JedisPool(config, host, port);
    }

    public void publish(String key, Map<String, String> message){
        try (Jedis jedis = this.jedisPool.getResource()){
            StreamEntryID id = jedis.xadd(key,StreamEntryID.NEW_ENTRY, message);
            System.out.println("Published message with ID: " + id);
        }
    }

    public void submitOrder(Orders orders){
        Map<String, String> message = new HashMap<>();
        message.put("orderId", orders.getOrderId());
        message.put("userId", orders.getUserId());
        message.put("productId", orders.getProductId());
        message.put("quantity", orders.getQuantity().toString());
        message.put("price", orders.getPrice().toString());
        message.put("timestamp", String.valueOf(System.currentTimeMillis()));
        publish("orders_stream", message);
    }
}
