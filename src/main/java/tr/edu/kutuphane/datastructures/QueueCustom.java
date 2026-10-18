
package tr.edu.kutuphane.datastructures;

import java.util.NoSuchElementException;

/** 4. kişi: Bekleme kuyruğu için FIFO yapısı. */
public class QueueCustom<T> {

    private Object[] elements = new Object[8];
    private int front;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Kuyruğun sonuna eleman ekler.
    public void enqueue(T item) {
        if (size == elements.length) {
            grow();
        }

        int rear = (front + size) % elements.length;

        elements[rear] = item;
        size++;
    }

    // Kuyruğun başındaki elemanı çıkarır.
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Kuyruk bos!");
        }

        T item = (T) elements[front];
        elements[front] = null;

        front = (front + 1) % elements.length;
        size--;

        return item;
    }

    // İlk elemanı silmeden döndürür.
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Kuyruk bos!");
        }

        return (T) elements[front];
    }

    // Dizi dolduğunda kapasiteyi iki katına çıkarır.
    private void grow() {
        Object[] newElements =
                new Object[elements.length * 2];

        for (int i = 0; i < size; i++) {
            int index = (front + i) % elements.length;
            newElements[i] = elements[index];
        }

        elements = newElements;
        front = 0;
    }
}
