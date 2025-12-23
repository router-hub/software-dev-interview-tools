package com.redis.learning.scaling;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisReplicationSetup {
    private final JedisPool primaryJedis;
    private final AtomicInteger roundRobin = new AtomicInteger(0);
    private final List<JedisPool> secondaryPools;


    public RedisReplicationSetup(JedisPool primaryJedis, List<JedisPool> secondaryPools) {
        this.primaryJedis = primaryJedis;
        this.secondaryPools = secondaryPools;
    }

    public void write(String key, String value){
        try (Jedis jedis = primaryJedis.getResource()){
            jedis.set(key, value);
        }
    }

    public String read(String key){
        int index = Math.abs(roundRobin.incrementAndGet()%secondaryPools.size());

        try(Jedis jedis = secondaryPools.get(index).getResource()){
            return jedis.get(key);
        }
    }
}
