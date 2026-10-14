package tr.edu.kutuphane.service;

import tr.edu.kutuphane.datastructures.DynamicArray;
import tr.edu.kutuphane.model.Book;

/** 1. kişi: Kitap işlemlerinin tek giriş noktası. */
public class BookService {
    private final DynamicArray<Book> books = new DynamicArray<>();

    public int count() { return books.size(); }
    // TODO: Dizi API'si tamamlandıktan sonra kitap ekleme, silme, güncelleme ve arama.
    // TODO: Benzersiz kitap kimliği ve zorunlu alan doğrulamalarını burada yapın.
}
