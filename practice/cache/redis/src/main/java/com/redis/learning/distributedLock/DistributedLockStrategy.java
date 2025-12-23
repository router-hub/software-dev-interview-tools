package com.redis.learning.distributedLock;

import org.apache.logging.log4j.util.Supplier;

public interface DistributedLockStrategy {

    <T> T executeLock(String key, long waitTime, long leaseTime, Supplier<T> task);
    LockType getLockType();
}
