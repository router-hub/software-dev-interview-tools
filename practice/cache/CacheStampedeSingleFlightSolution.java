package practice.cache;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CacheStampedeSingleFlightSolution<K,V> {
    private final Cache<K,V> cache;
    private final DataSource<K,V> dataSource;
    private final ConcurrentHashMap<K, CompletableFuture<V>> inFlight = new ConcurrentHashMap<>();

    public  CacheStampedeSingleFlightSolution(Cache cache, DataSource dataSource){
        this.cache = cache;
        this.dataSource = dataSource;
    }

    public V get(K key){
        //check if the key is present in the cache
        V value = cache.get(key);
        if(value != null){
            return value;
        }
        // if key is not present, fallback to db fetch;
       CompletableFuture<V> future = inFlight.computeIfAbsent(key, k -> {
          CompletableFuture.supplyAsync(() -> {
            try{
                value = dataSource.fetch(key);
                cache.put(key, value);
                return value;
            }catch (Exception e){
                throw new RuntimeException(e);
            }finally {
                inFlight.remove(key);
            }
          })
       });
       try {
        return future.get(5, TimeUnit.SECONDS);
       }catch (Exception e){
        inFlight.remove(key);
        throw new RuntimeException(e);
       }
    }
}
