package com.redis.learning.scaling;

import redis.clients.jedis.ClusterPipeline;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Pipeline;

import java.util.HashSet;
import java.util.Set;

public class RedisClusterSetup {
    private final JedisCluster jedisCluster;

    public RedisClusterSetup() {
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("node1", 7000));
        nodes.add(new HostAndPort("node2", 7001));
        nodes.add(new HostAndPort("node3", 7002));

        this.jedisCluster = new JedisCluster(nodes);
    }

    public void operationExample(String userId){
        // we need to create hash key to shard across different pod

        String hashKey = "{users:" + userId + "}";

        jedisCluster.set(hashKey + ":name", "aditya");
        jedisCluster.set(hashKey + ":email", "a@gm.sdf");

        ClusterPipeline pipeline = jedisCluster.pipelined();
        pipeline.get(hashKey+":name");
        pipeline.get(hashKey+":email");
        pipeline.sync();
    }

}
