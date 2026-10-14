package tr.edu.kutuphane.datastructures;

/** 4. kişi: Son işlemleri tutmak için LIFO yapısı. */
public class StackCustom<T> {
    private Object[] elements = new Object[8];
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    // TODO: push, pop ve peek metotlarını yazın; tepe indeksi size - 1 olur.
    // TODO: Kapasite artırmayı ve boş yığından çıkarma davranışını uygulayın.
    // TODO: pop sırasında boşalan hücreyi null yapın.
}
