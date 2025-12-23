package com.redis.learning.publisherSubscriber;

import redis.clients.jedis.JedisPubSub;

public class RedisSubscriber extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        System.out.println("Received on " + channel + ": " + message);
        // Process message
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println("Subscribed to " + channel);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println("Unsubscribed from " + channel);
    }
}
