package tr.edu.kutuphane.datastructures;

/** Fırat Oskay: Hazır koleksiyon kullanmadan büyüyen dizi.
 * Erişim O(1), sona ekleme amortize O(1), silme O(n).
 */
public class DynamicArray<T> {
    private Object[] elements = new Object[8];
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void add(T value) {
        if (size == elements.length) {
            Object[] expanded = new Object[elements.length * 2];
            for (int i = 0; i < size; i++) expanded[i] = elements[i];
            elements = expanded;
        }
        elements[size++] = value;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) elements[index];
    }

    public void set(int index, T value) {
        checkIndex(index);
        elements[index] = value;
    }

    public T remove(int index) {
        T previous = get(index);
        for (int i = index; i < size - 1; i++) elements[i] = elements[i + 1];
        elements[--size] = null;
        return previous;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("İndeks: " + index + ", boyut: " + size);
        }
    }
}
