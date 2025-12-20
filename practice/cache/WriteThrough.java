package practice.cache;

/**
 * Everything is same as cache aside, here, write is different,
 * we will first write the data to the cache and then we will synchronously write to the db
 * cache and db are always consistent
 * Best for system where reads must always return fresh data and slower write are acceptable
 * Write latency increases, wasted cahce space for rarely read data
 */
class WriteThrough<K,V> {
  private final Cache<K,V> cache;
  private final DataSource<K,V> dataSource;

  public WriteThrough(Cache<K,V> cache, DataSource<K,V> dataSource){
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
    // write to cache first
    cache.put(key, value);

    // then write to db synchronously
    try{
        dataSource.update(key, value);
    }catch(Exception e){
        cache.invalidate(key);
        throw e;
    }
  }
}
