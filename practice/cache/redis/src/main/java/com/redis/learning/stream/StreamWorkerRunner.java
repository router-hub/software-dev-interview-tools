package com.redis.learning.stream;

import org.springframework.boot.CommandLineRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StreamWorkerRunner implements CommandLineRunner {
    private final RedisStreamConsumer redisStreamConsumer;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public StreamWorkerRunner(RedisStreamConsumer redisStreamConsumer) {
        this.redisStreamConsumer = redisStreamConsumer;
    }

    @Override
    public void run(String... args) throws Exception {
        executorService.submit(redisStreamConsumer::startConsuming);
    }
}
