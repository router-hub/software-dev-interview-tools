package practice.cache;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Here, write will not take place synchronously, 
 * we will first write to the cache and then we will write to the db asynchronously.
 * High write throughput systems with acceptable eventual consistency like analytics and metrics pipelines
 * Data loss risk during power failure
 * Implementation logic, we need to maintain a write queue and a background thread to write to the db
 * 
 * We will use BlockingQueue to store the write requests
 * Advantages of using BlockingQueue - 
 * *(1) it is thread safe 
 * *(2) The BlockingQueue puts the thread to sleep until data arrives. It uses 0% CPU while waiting.
 * *(3) BlockingQueue: If you set a limit, when the queue is full, the put() method will block (wait) until space becomes available. 
 * This naturally slows down the incoming requests to match the speed of the database.
 * 
 * 
 */
private class WriteBehind<K,V> {
    private final Cache<K,V> cache;
    private final DataSource<K,V> dataSource;
    private final BlockingQueue<WriteRequest<K,V>> writeQueue;
    private final ExecutorService executorService;

    public WriteBehind(Cache<K,V> cache, DataSource<K,V> dataSource){
        this.cache = cache;
        this.dataSource = dataSource;
        this.writeQueue = new ArrayBlockingQueue<>(1000);
        this.executorService = Executors.newFixedThreadPool(4);

        for(int i = 0; i < 4; i++){
            executorService.submit(this::processWrites);
        }
    }

    public V get(K key){
        return cache.get(key);
    }

    public void update(K key, V value){
        // put it into the queue
        cache.put(key, value);
       // asynchronously write to the db
       writeQueue.offer(new WriteRequest<>(key, value, System.currentTimeMillis()));
    }

    private void processWrites(){
        while(!Thread.currentThread().isInterrupted()){
            try{
                // GOOD: BlockingQueue approach
                // The thread literally stops here and goes to sleep.
                // The OS wakes it up only when a new item is added.
                WriteRequest<K,V> request = writeQueue.poll(1, TimerUnit.SECONDS);
                if(request != null)
                dataSource.update(request.getKey(), request.getValue(), request.getTimestamp());
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }catch(Exception e){
                // log the error
                System.err.println("Failed to write to db: " + e.getMessage());
            }
        }
    }

    private static class WriteRequest<K,V>{
        private final K key;
        private final V value;
        private final long timestamp;

        public WriteRequest(K key, V value, long timestamp){
            this.key = key;
            this.value = value;
            this.timestamp = timestamp;
        }

        public K getKey(){
            return key;
        }

        public V getValue(){
            return value;
        }

        public long getTimestamp(){
            return timestamp;
        }
    }
}