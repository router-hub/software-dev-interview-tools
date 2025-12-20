package practice.cache;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.*;

public class CacheWarmer<K,V> {
    private final Cache<K,V> cache;
    private final DataBase<K,V> dataBase;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public CacheWarmer(Cache<K,V> cache, DataBase<K,V> dataBase){
        this.cache = cache;
        this.dataBase = dataBase;
    }

    public void warmPopularKeys(List<K> popularKeys, long refreshedAtInterval){
        for(K k : popularKeys){
            executorService.scheduleAtFixedRate(() -> {
                try{
                    V value = dataBase.fetch(key);
                    cache.put(k, value);
                } catch (Exception e){
                    //
                }
            }, 0, refreshedAtInterval, TimeUnit.SECONDS);
        }
    }
}
