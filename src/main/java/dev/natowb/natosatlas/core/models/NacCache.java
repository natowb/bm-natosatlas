package dev.natowb.natosatlas.core.models;

import java.util.*;

public class NacCache<K, V> {

    private final int maxSize;
    private final Map<K, V> map;
    private final LinkedList<K> lruList;

    public NacCache(int maxSize) {
        this.maxSize = maxSize;
        this.map = new HashMap<>();
        this.lruList = new LinkedList<>();
    }

    public Collection<V> values() {
        return map.values();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    public V get(K key) {
        V value = map.get(key);
        if (value != null) {
            touch(key);
        }
        return value;
    }

    public void put(K key, V value) {
        map.put(key, value);
        touch(key);
        evictIfNeeded();
    }

    public void remove(K key) {
        map.remove(key);
        lruList.remove(key);
    }

    public void clear() {
        map.clear();
        lruList.clear();
    }

    public int size() {
        return map.size();
    }

    private void touch(K key) {
        lruList.remove(key);
        lruList.addFirst(key);
    }

    private void evictIfNeeded() {
        while (map.size() > maxSize) {
            K oldest = lruList.removeLast();
            map.remove(oldest);
        }
    }
}
