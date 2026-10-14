package tr.edu.kutuphane.datastructures;

/** 3. kişi: Tek yönlü bağlı listenin başlangıç iskeleti. */
public class LinkedListCustom<T> {
    private static final class Node<T> {
        private T value;
        private Node<T> next;
        private Node(T value) { this.value = value; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    // TODO: Ekleme, arama, dolaşma ve silmeyi düğüm bağlantılarıyla uygulayın.
    // TODO: İlk/son düğüm silinirken head, tail ve size tutarlı kalmalı.
    // TODO: Boş listede head ve tail null olmalı; tail.next daima null olmalı.
}
