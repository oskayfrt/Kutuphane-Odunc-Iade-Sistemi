package tr.edu.kutuphane.datastructures;

import java.util.function.BiConsumer;

public class HashTableCustom<K, V> {
    private static final class Entry<K, V> {
        private final K key;
        private V value;
        private Entry<K, V> next;
        private Entry(K key, V value) { this.key = key; this.value = value; }
    }

    private static final double LOAD_FACTOR = 0.75;
    private Object[] buckets = new Object[16];
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    private static void requireKey(Object key) {
        if (key == null) throw new IllegalArgumentException("Anahtar null olamaz.");
    }

    private static int indexFor(Object key, int length) {
        return Math.floorMod(key.hashCode(), length);
    }

    @SuppressWarnings("unchecked")
    private Entry<K, V> findEntry(K key) {
        requireKey(key);
        Entry<K, V> current = (Entry<K, V>) buckets[indexFor(key, buckets.length)];
        while (current != null) {
            if (current.key.equals(key)) return current;
            current = current.next;
        }
        return null;
    }

    /** Yeni anahtarsa null, mevcutsa eski değeri döndürür; mevcut anahtarda size artmaz. */
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        Entry<K, V> existing = findEntry(key);
        if (existing != null) {
            V old = existing.value;
            existing.value = value;
            return old;
        }
        if (size + 1 > buckets.length * LOAD_FACTOR) resize();
        int index = indexFor(key, buckets.length);
        Entry<K, V> entry = new Entry<>(key, value);
        entry.next = (Entry<K, V>) buckets[index];   // zincirin başına ekle
        buckets[index] = entry;
        size++;
        return null;
    }

    public V get(K key) {
        Entry<K, V> entry = findEntry(key);
        return entry == null ? null : entry.value;
    }

    public boolean containsKey(K key) { return findEntry(key) != null; }

    @SuppressWarnings("unchecked")
    public V remove(K key) {
        requireKey(key);
        int index = indexFor(key, buckets.length);
        Entry<K, V> previous = null;
        Entry<K, V> current = (Entry<K, V>) buckets[index];
        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) buckets[index] = current.next;
                else previous.next = current.next;
                size--;
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void forEach(BiConsumer<K, V> action) {
        for (Object head : buckets) {
            Entry<K, V> current = (Entry<K, V>) head;
            while (current != null) {
                action.accept(current.key, current.value);
                current = current.next;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Object[] old = buckets;
        buckets = new Object[old.length * 2];
        for (Object head : old) {
            Entry<K, V> current = (Entry<K, V>) head;
            while (current != null) {
                Entry<K, V> next = current.next;          // bağı koparmadan önce sakla
                int index = indexFor(current.key, buckets.length);
                current.next = (Entry<K, V>) buckets[index];
                buckets[index] = current;
                current = next;
            }
        }
    }
}