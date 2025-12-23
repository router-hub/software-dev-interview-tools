package com.redis.learning;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

public class ImplementationWithJedis {
    private final JedisPool jedisPool;

    public ImplementationWithJedis(String host, String port){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // what is the meaning of connections ?
        // connections are the number of connections that can be made to the redis server
        poolConfig.setMaxTotal(10); // max number of connections
        poolConfig.setMaxIdle(5); // max number of idle connections
        poolConfig.setMinIdle(1); // min number of idle connections
        this.jedisPool = new JedisPool(poolConfig, host, Integer.parseInt(port));
    }

    public void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value); // SET key:value
        }
    }

    public void set(String key, Integer value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value.toString()); // SET key:value
        }
    }

    public void incr(String key){
        try (Jedis jedis = jedisPool.getResource()){
            jedis.incr(key);
        }
    }

    public void incrBy(String key, int incrementBy){
        try (Jedis jedis = jedisPool.getResource()){
            jedis.incrBy(key, incrementBy);
        }
    }

    public String get(String key){
        try (Jedis jedis = jedisPool.getResource()){
           return jedis.get(key);
        }
    }

    public void setWithExpiry(String key, String value, int expiryAfterSeconds){
        try(Jedis jedis = jedisPool.getResource()){
            jedis.setex(key, expiryAfterSeconds, value);
        }
    }

    public void setInTable(String table, String userId, String col, String val){
        try(Jedis jedis = jedisPool.getResource()){
            jedis.set(table + ":" + userId + ":" + col, val);
        }
    }

    public boolean isExist(String key){
        try(Jedis jedis = jedisPool.getResource()){
            return jedis.exists(key);
        }
    }

    public void setUserDetails(String table, String userId, String name, Integer age, String city){
        try(Jedis jedis = jedisPool.getResource()){
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("age", age.toString());
            map.put("city", city);
            jedis.hset(table+":"+userId, map);
        }
    }


}
