package tr.edu.kutuphane.datastructures;

/** 4. kişi: Bekleme kuyruğu için FIFO yapısı. */
public class QueueCustom<T> {
    private Object[] elements = new Object[8];
    private int front;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    // TODO: enqueue, dequeue ve peek metotlarını dairesel diziyle uygulayın.
    // TODO: Ekleme indeksi (front + size) % elements.length olarak hesaplanır.
    // TODO: Büyütmede FIFO sırasını koruyun; boş kuyruktan çıkarma politikasını belgeleyin.
}
