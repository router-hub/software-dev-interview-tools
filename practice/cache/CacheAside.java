package practice.cache;

/**
 * we will first check the key in the cache if it is present then we will return the value
 * if it is not present then we will fetch the value from the db and store it in the cache and return the value
 */
private class CacheAside<K,V> {
     private final Cache<K,V> cache;
     private final DataSource<K,V> dataSource;

     public CacheAside(Cache<K,V> cache, DataSource<K,V> dataSource){
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
        value = dataSource.fetch(key);
        if(value != null){
            cache.put(key, value);
        }
        return value;
     }
     
     public void update(K key, V value){
        // update the data source and the invalidate the cache
        dataSource.update(key, value);
        cache.invalidate(key);
     }
}