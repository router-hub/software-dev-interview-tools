package practice.cache;

import java.util.Map;
import java.util.concurrent.*;

/**
 * We will use DelayQueue to implement TTLCache
 */

public class TTLCache<K, V> {
    private final Map<K, CacheAgent<V>> cache = new ConcurrentHashMap<>();
    private final DelayQueue<DelayedKey<K>> cleanupQueue = new DelayQueue<DelayedKey<K>>();
    private final ExecutorService cleanupExecutor = Executors.newSingleThreadExecutor();
    private final long ttlMillis;

    public TTLCache(long ttlMillis){
        this.ttlMillis = ttlMillis;
        cleanupExecutor.submit(() -> {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    DelayedKey<K> delayedKey = cleanupQueue.take();
                    if(cache.containsKey(delayedKey.key) && cache.get(delayedKey.key).expiryTime == delayedKey.expiryTIme){
                        cache.remove(delayedKey.key);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void update(K key, V value){
        long expiryTime = System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheAgent<>(value, expiryTime));
        // Add to cleanup queue
        // If the key already exists, we should ideally remove the old DelayedKey,
        // but DelayQueue removal is O(N).
        // A common tradeoff is to just add the new one; the old one will trigger
        // a harmless "remove(key)" later which does nothing if key is missing/updated.
        cleanupQueue.put(new DelayedKey<>(key, expiryTime));
    }

    public V get(K key){
        return cache.get(key);
    }

    public class CacheAgent<V>{
        V value;
        long expiryTime;

        public CacheAgent(V value, long expiryTime){
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }

    public class DelayedKey<K> implements Delayed{
        K key;
        long expiryTIme;

        public DelayedKey(K key, long expiryTime){
            this.key = key;
            this.expiryTIme = expiryTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expiryTIme - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.expiryTIme, ((DelayedKey<K>) o).expiryTIme);
        }
    }
}
