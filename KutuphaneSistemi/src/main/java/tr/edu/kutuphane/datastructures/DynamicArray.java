package tr.edu.kutuphane.datastructures;

/** 1. kişi: Elemanlar gerçek bir dizi içinde tutulacak. */
public class DynamicArray<T> {
    private Object[] elements = new Object[8];
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    // TODO: add, get, set ve remove metotlarını yazın; indeks sınırlarını kontrol edin.
    // TODO: Kapasite dolduğunda daha büyük dizi oluşturup elemanları kopyalayın.
    // TODO: Silinen son hücreyi null yapın; yalnızca [0, size) aralığı dolu olsun.
    // Henüz ekleme/erişim API'si yoktur; mevcut metotlar boş durumu doğru bildirir.
}
