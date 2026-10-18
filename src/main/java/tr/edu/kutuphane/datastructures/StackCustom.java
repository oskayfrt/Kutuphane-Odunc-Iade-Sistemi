package tr.edu.kutuphane.datastructures;

import java.util.NoSuchElementException;

/** 4. kisi: Son islemleri tutmak icin LIFO yapisi. */
public class StackCustom<T> {

    private Object[] elements = new Object[8];
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // Yiginin tepesine eleman ekler.
    public void push(T item) {
        if (size == elements.length) {
            grow();
        }

        elements[size] = item;
        size++;
    }

    // Tepedeki elemani cikarir.
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Yigin bos!");
        }

        size--;
        T item = (T) elements[size];
        elements[size] = null;

        return item;
    }

    // Tepedeki elemani silmeden getirir.
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Yigin bos!");
        }

        return (T) elements[size - 1];
    }

    // Dizi doldugunda kapasiteyi iki katina cikarir.
    private void grow() {
        Object[] newElements =
                new Object[elements.length * 2];

        for (int i = 0; i < size; i++) {
            newElements[i] = elements[i];
        }

        elements = newElements;
    }
}
