package practice.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * We will use capacity, minFrequency, HashMap<K,Node<K,V>> cache, HashMap<Integer, 
 * DoubleLinkedList<K,V>> frequencyMap, ReadWriteLock to implement LFUCache
 */

public class LFUCache<K,V>{
    private final int capacity;
    private int minFrequency;
    private final Map<K,Node<K,V>> cache;
    private final Map<Integer,DoubleLinkedList<K,V>> frequencyMap;
    private final ReadWriteLock lock= new ReentrantReadWriteLock();

    public LFUCache(int capacity){
        this.capacity = capacity;
        this.minFrequency = 0;
        this.cache = new HashMap<K,Node<K,V>>();
        this.frequencyMap = new HashMap<Integer,DoubleLinkedList<K,V>>();
    }

    private V get(K key){
        lock.readLock().lock();
        try{
            if(cache.containsKey(key)) {
                Node<K, V> node = cache.get(key);
                lock.readLock().unlock();
                lock.writeLock().lock();
                try{
                    int freq = node.frequency;
                    DoubleLinkedList<K,V> linkedList = frequencyMap.get(freq);
                    linkedList.removeNode(node);
                    freq++;
                    frequencyMap.computeIfAbsent(freq, k -> new DoubleLinkedList<K,V>());
                    DoubleLinkedList<K,V> ll = frequencyMap.get(freq);
                    node.frequency = freq;
                    ll.addFirst(node);
                    if(freq < minFrequency){
                        minFrequency = freq;
                    }
                }finally {
                    lock.writeLock().unlock();
                }
                return node.value;
            }else return null;
        }finally {
            lock.readLock().unlock();
        }
    }

    private void update(K key, V value){
        lock.writeLock().lock();
        try{
            if(cache.containsKey(key)){
                Node<K, V> node = cache.get(key);
                node.value = value;
                int freq = node.frequency;
                DoubleLinkedList<K,V> linkedList = frequencyMap.get(freq);
                linkedList.removeNode(node);
                freq++;
                frequencyMap.computeIfAbsent(freq, k -> new DoubleLinkedList<K,V>());
                DoubleLinkedList<K,V> ll = frequencyMap.get(freq);
                node.frequency = freq;
                ll.addFirst(node);
                if(freq < minFrequency){
                    minFrequency = freq;
                }
            }else{
                // remove the least frequent one if capacity is full
                if(cache.size() == capacity){
                    DoubleLinkedList<K,V> linkedList = frequencyMap.get(minFrequency);
                    Node<K,V> node = linkedList.removeLast();
                    cache.remove(node.key);
                }
                Node<K,V> node = new Node<>(key, value);
                cache.put(key, node);
                frequencyMap.computeIfAbsent(1, k -> new DoubleLinkedList<K,V>());
                DoubleLinkedList<K,V> ll = frequencyMap.get(1);
                ll.addFirst(node);
                minFrequency = 1;
            }
        }finally {
            lock.writeLock().unlock();
        }   
    }


    public class Node<K, V> {
        K key;
        V value;
        int frequency;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value){
            this.key = key;
            this.value = value;
            this.frequency = 1;
        }
    }

    public class DoubleLinkedList<K,V>{
        private final Node<K,V> head;
        private final Node<K,V> tail;

        public DoubleLinkedList(){
            this.head = new Node<K,V>(null, null);
            this.tail = new Node<K,V>(null, null);
            this.head.next = this.tail;
            this.tail.prev = this.head;
        }

        public void addFirst(Node<K,V> node){
            node.next = this.head.next;
            this.head.next.prev = node;
            this.head.next = node;
        }

        public void removeNode(Node<K,V> node){
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        
        public Node<K,V> removeLast(){
            Node<K,V> lastNode = this.tail.prev;
            removeNode(lastNode);
            return lastNode;
        }
        
        public void moveToFirst(Node<K,V> node){
            removeNode(node);
            addFirst(node);
        }
        
    }
}
