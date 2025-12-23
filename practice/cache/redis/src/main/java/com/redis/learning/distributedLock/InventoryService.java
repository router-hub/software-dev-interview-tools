package com.redis.learning.distributedLock;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final LockStrategyFactory lockFactory;

    // You can change this dynamically via application.properties or API!
    private LockType currentLockType = LockType.REDISSON;

    public InventoryService(LockStrategyFactory lockFactory) {
        this.lockFactory = lockFactory;
    }

    // Method to switch strategy at runtime (The "Trick")
    public void switchLockEngine(LockType newType) {
        this.currentLockType = newType;
        System.out.println("⚠️ Switched locking engine to: " + newType);
    }

    public void purchaseItem(String itemId) {
        // 1. Get the chosen locker (Jedis, Lettuce, or Redisson)
        DistributedLockStrategy locker = lockFactory.getStrategy(currentLockType);

        // 2. Execute business logic wrapped in the lock
        locker.executeLock("lock:" + itemId, 5000, 10000, () -> {

            // --- CORE BUSINESS LOGIC ---
            System.out.println("📦 Checking inventory...");
            System.out.println("💰 Payment processed.");
            System.out.println("✅ Item sold!");
            return true;

        });
    }
}
