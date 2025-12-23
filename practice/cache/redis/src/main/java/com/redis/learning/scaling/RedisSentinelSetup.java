package com.redis.learning.scaling;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class RedisSentinelSetup {
    private final JedisSentinelPool sentinelPool;

    public RedisSentinelSetup() {
        Set<String> sentinels = new HashSet<>();
        sentinels.add("sentinel1:26379");
        sentinels.add("sentinel2:26379");
        sentinels.add("sentinel3:26379");

        this.sentinelPool = new JedisSentinelPool("mymaster", sentinels);
    }

    public void execute(Consumer<Jedis> task){
        try(Jedis jedis = sentinelPool.getResource()){
            task.accept(jedis);
        }
    }

}
