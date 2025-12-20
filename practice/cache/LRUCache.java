package practice.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * We will use LinkedHasMap, ConcurrentHashMap, Reetrant Read Write Lock, Capacity to implement LRUCache
 */

private class LRUCache<K,V>{
    private final int capacity;
    private final Map<K,Node<K,V>> cache;
    private final DoubleLinkedList<K,V> linkedList;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int capacity){
        this.capacity = capacity;
        this.cache = new ConcurrentHashMap<K,Node<K,V>>(capacity);
        this.linkedList = new DoubleLinkedList<K,V>();
    }

    public V get(K key){
        // in get as well we need to update the list, so first take the read lock to check if it is present
        lock.readLock().lock();
        try{
            if(cache.containsKey(key)) {
                Node<K, V> node = cache.get(key);

                // we will aquire write lock now
                lock.readLock().unlock();
                lock.writeLock().lock();
                try{
                    linkedList.moveToFront(node);
                    lock.writeLock().unlock();
                    return node.value;
                } finally {
                    lock.writeLock().unlock();
                }
            }
            else return null;
        }finally {
            lock.readLock().unlock();
        }
    }

    public void update(K key, V value){
         lock.writeLock().lock();
         try{
             if(cache.containsKey(key)){
                 Node<K, V> node = cache.get(key);
                 node.value = value;
                 linkedList.moveToFront(node);
             }
             else{
                 // remove if whole capacity is used
                 if(cache.size() == capacity){
                    Node<K, V> node =  linkedList.removeLast();
                    cache.remove(node.key);
                 }

                 Node<K, V> node = new Node<>(key, value);
                 cache.put(key, node);
                 linkedList.addFirst(node);
             }
         }finally {
             lock.writeLock().unlock();
         }
    }


    public class Node<K,V>{
        K key;
        V value;
        Node<K,V> prev;
        Node<K,V> next;

        Node(K key, V value){
            this.key = key;
            this.value = value;
        }
    }
    public class DoubleLinkedList<K,V>{
        private final Node<K,V> head = new Node<K,V>(null, null);
        private final Node<K,V> tail = new Node<K,V>(null, null);

        public DoubleLinkedList(){
            this.head.next = this.tail;
            this.tail.prev = this.head;
        }
        public void addFirst(Node<K,V> node){
            node.next = this.head.next;
            node.prev = this.head;
            this.head.next.prev = node;
            this.head.next = node;
        }
        public Node<K, V> removeLast(){
            Node<K,V> lastNode = this.tail.prev;
            removeNode(lastNode);
            return lastNode;
        }
        public void removeNode(Node<K,V> node){
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        public void moveToFront(Node<K,V> node){
            removeNode(node);
            addFirst(node);
        }
    }
}
