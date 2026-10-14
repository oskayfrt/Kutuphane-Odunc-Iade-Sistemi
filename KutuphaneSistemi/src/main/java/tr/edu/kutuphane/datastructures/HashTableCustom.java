package tr.edu.kutuphane.datastructures;

/** 2. kişi: Ayrı zincirleme ile çakışma çözecek özel hash tablosu. */
public class HashTableCustom<K, V> {
    private static final class Entry<K, V> {
        private final K key;
        private V value;
        private Entry<K, V> next;
        private Entry(K key, V value) { this.key = key; this.value = value; }
    }

    // Her hücre bir Entry zincirinin başını tutacak; hazır koleksiyon kullanılmaz.
    private Object[] buckets = new Object[16];
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    // TODO: put, get, containsKey ve remove; anahtarları equals ile karşılaştırın.
    // TODO: Math.floorMod(key.hashCode(), buckets.length) ile indeks hesaplayın.
    // TODO: Aynı anahtar güncellenirken size artmamalı; null anahtar politikasını belirleyin.
    // TODO: Yük faktörü arttığında yeni diziye yeniden hash ederek taşıyın.
}
