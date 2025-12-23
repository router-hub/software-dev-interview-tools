package com.redis.learning.stream;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XClaimParams;
import redis.clients.jedis.params.XPendingParams;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;
import redis.clients.jedis.resps.StreamPendingEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisStreamConsumer {
    private final String consumerName;
    private final String consumerGroup;
    private final String streamKey;
    private final JedisPool jedisPool;

    public RedisStreamConsumer(String consumerName, String consumerGroup, String streamKey, JedisPool jedisPool) {
        this.consumerName = consumerName;
        this.consumerGroup = consumerGroup;
        this.streamKey = streamKey;
        this.jedisPool = jedisPool;

        createConsumerGroup();
    }

    private void createConsumerGroup(){
        try(Jedis jedis = jedisPool.getResource()){
            jedis.xgroupCreate(streamKey, consumerGroup, StreamEntryID.XGROUP_LAST_ENTRY, true);
        }
    }

    public void startConsuming(){
        try(Jedis jedis = jedisPool.getResource()){
            while (true){
                // Read new messages
                Map<String, StreamEntryID> streamQuery = new HashMap<>();
                streamQuery.put(streamKey, StreamEntryID.XREADGROUP_UNDELIVERED_ENTRY);
                XReadGroupParams xReadGroupParams = new XReadGroupParams();
                xReadGroupParams.noAck(); // no need to acknowledge
                xReadGroupParams.count(1); // read 1 message at a time
                xReadGroupParams.block(2000); // block for 2 seconds
                List<Map.Entry<String, List<StreamEntry>>> result = jedis.xreadGroup(consumerGroup,consumerName, xReadGroupParams, streamQuery);

                if (result != null && !result.isEmpty()) {
                    for (Map.Entry<String, List<StreamEntry>> entry : result) {
                        for (StreamEntry streamEntry : entry.getValue()) {
                            processMessage(streamEntry);
                            // Acknowledge processing
                            jedis.xack(streamKey, consumerGroup, streamEntry.getID());
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMessage(StreamEntry streamEntry){
        Map<String, String> message = streamEntry.getFields();
        System.out.println("Processing message: " + message);
    }
    
    // Handle failed messages
    public void processPendingMessages() {
        try (Jedis jedis = jedisPool.getResource()) {
            // Get pending messages (not acknowledged)
            XPendingParams xPendingParams = new XPendingParams();
            xPendingParams.count(10);
            List<StreamPendingEntry> pending =
                    jedis.xpending(streamKey,consumerGroup, xPendingParams);

            for (StreamPendingEntry entry : pending) {
                // Reclaim and reprocess
                StreamEntryID id = entry.getID();
                XClaimParams xClaimParams = new XClaimParams();
                xClaimParams.time(60000);
                List<StreamEntry> claimed =
                        jedis.xclaim(streamKey, consumerGroup, consumerName, 1,xClaimParams, id);

                for (StreamEntry streamEntry : claimed) {
                    processMessage(streamEntry);
                    jedis.xack(streamKey, consumerGroup, streamEntry.getID());
                }
            }
        }
    }
}
