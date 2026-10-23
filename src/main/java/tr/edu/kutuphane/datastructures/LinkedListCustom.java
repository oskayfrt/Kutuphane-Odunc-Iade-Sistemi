package tr.edu.kutuphane.datastructures;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

    /**
     * Listenin sonuna yeni bir eleman ekler (O(1) tail bağlantısı ile).
     */
    public void add(T value) {
        addLast(value);
    }

    public void addLast(T value) {
        Node<T> newNode = new Node<>(value);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        tail.next = null;
        size++;
    }

    /**
     * Listenin başına yeni bir eleman ekler.
     */
    public void addFirst(T value) {
        Node<T> newNode = new Node<>(value);
        if (head == null) {
            head = newNode;
            tail = newNode;
            tail.next = null;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    /**
     * Belirtilen indeksteki elemanı döndürür.
     */
    public T get(int index) {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.value;
    }

    /**
     * Belirtilen indeksteki düğümün değerini günceller (Örn: Loan iade edildiğinde yeni record ile değiştirmek için).
     */
    public T set(int index, T newValue) {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        T oldValue = current.value;
        current.value = newValue;
        return oldValue;
    }

    /**
     * Koşulu sağlayan ilk elemanı bulup döndürür, bulamazsa null döner.
     */
    public T find(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "Arama koşulu null olamaz");
        Node<T> current = head;
        while (current != null) {
            if (predicate.test(current.value)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Değerin listede olup olmadığını kontrol eder.
     */
    public boolean contains(T value) {
        return indexOf(value) != -1;
    }

    /**
     * Aranan değerin indeksini döndürür, yoksa -1 döner.
     */
    public int indexOf(T value) {
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            if (Objects.equals(current.value, value)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    /**
     * Koşulu sağlayan ilk düğümün değerini yeni değerle günceller (İade güncellemesi için).
     */
    public boolean replaceFirst(Predicate<T> predicate, T newValue) {
        Objects.requireNonNull(predicate, "Koşul null olamaz");
        Node<T> current = head;
        while (current != null) {
            if (predicate.test(current.value)) {
                current.value = newValue;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * İlk elemanı siler ve değerini döndürür. Head, tail ve size tutarlılığını korur.
     */
    public T removeFirst() {
        if (head == null) {
            throw new IllegalStateException("Liste boş!");
        }
        T removedValue = head.value;
        head = head.next;
        size--;
        if (head == null) {
            tail = null;
        }
        return removedValue;
    }

    /**
     * Verilen değere eşit olan ilk düğümü listeden siler.
     * İlk, orta ve son düğüm silinirken head, tail ve size tutarlı kalır; tail.next daima null olur.
     */
    public boolean remove(T value) {
        return removeIfFirst(item -> Objects.equals(item, value));
    }

    /**
     * Verilen koşulu sağlayan ilk düğümü listeden siler.
     */
    public boolean removeIfFirst(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "Koşul null olamaz");
        if (head == null) {
            return false;
        }

        // Silinecek düğüm ilk düğüm (head) ise
        if (predicate.test(head.value)) {
            head = head.next;
            size--;
            if (head == null) {
                tail = null;
            }
            return true;
        }

        // Ortadaki veya sondaki düğümü arama
        Node<T> prev = head;
        Node<T> current = head.next;

        while (current != null) {
            if (predicate.test(current.value)) {
                prev.next = current.next;
                // Eğer silinen düğüm son düğüm (tail) ise tail'i güncelle
                if (current == tail) {
                    tail = prev;
                    tail.next = null;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    /**
     * Belirtilen indeksteki düğümü siler ve değerini döndürür.
     */
    public T removeAt(int index) {
        checkIndex(index);
        if (index == 0) {
            return removeFirst();
        }
        Node<T> prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
        }
        Node<T> target = prev.next;
        prev.next = target.next;
        if (target == tail) {
            tail = prev;
            tail.next = null;
        }
        size--;
        return target.value;
    }

    /**
     * Bağlı listedeki tüm elemanları baştan sona dolaşır.
     */
    public void forEach(Consumer<T> action) {
        Objects.requireNonNull(action, "İşlem null olamaz");
        Node<T> current = head;
        while (current != null) {
            action.accept(current.value);
            current = current.next;
        }
    }

    /**
     * Koşula uyan elemanları içeren yeni bir LinkedListCustom döndürür (Örn: gecikenleri filtrelemek için).
     */
    public LinkedListCustom<T> filter(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "Filtre koşulu null olamaz");
        LinkedListCustom<T> filtered = new LinkedListCustom<>();
        Node<T> current = head;
        while (current != null) {
            if (predicate.test(current.value)) {
                filtered.addLast(current.value);
            }
            current = current.next;
        }
        return filtered;
    }

    /**
     * Listeyi tamamen temizler; head ve tail null olur.
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Geçersiz indeks: " + index + ", Boyut: " + size);
        }
    }
}