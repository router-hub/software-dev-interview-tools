package practice.cache;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HotKeyReplicateCache<K,V> {
    private final List<Cache<K,V>> cacheReplicas;
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);
    private int replicationFactor;
    private final Set<K> hotKeys = ConcurrentHashMap.newKeySet();

    public HotKeyReplicateCache(List<Cache<K,V>> cache, int replicationFactor){
        this.cache = cache;
        this.replicationFactor = replicationFactor;
    }

    public V get(K key){
        //check if hot keys
        if(hotKeys.contains(key)){
            //apply roundRobin and find which replica we will use to find
            int index = Math.abs(roundRobinIndex.getAndIncrement() % replicationFactor);
            return cacheReplicas.get(index).get(key);
        }
        else cacheReplicas.get(0).get(key);
    }

    public void update(K key, V value){
        if(hotKeys.contains(key)){
            // add in all replicas
            for(int i = 0; i < replicationFactor; i++){
                cacheReplicas.get(i).put(key, value);
            }
        }
        else {
            cacheReplicas.get(0).put(key, value);
        }
    }

    public void markHot(K key){
        hotKeys.add(key);
    }
}
