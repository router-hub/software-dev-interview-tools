package com.redis.learning.distributedLock;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LockStrategyFactory {

    private final Map<LockType, DistributedLockStrategy> strategyMap;

    // Spring is smart! It finds all classes implementing DistributedLockStrategy
    // and injects them as a List. We then convert it to a fast Lookup Map.
    public LockStrategyFactory(List<DistributedLockStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(DistributedLockStrategy::getLockType, s -> s));
    }

    public DistributedLockStrategy getStrategy(LockType type) {
        return strategyMap.get(type);
    }
}
