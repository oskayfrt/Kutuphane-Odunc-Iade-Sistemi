package tr.edu.kutuphane.service;

import tr.edu.kutuphane.datastructures.DynamicArray;
import tr.edu.kutuphane.model.Book;
import java.util.Locale;
import java.util.Objects;

/** 1. kişi: Kitap işlemlerinin tek giriş noktası. */
public class BookService {
    private final DynamicArray<Book> books = new DynamicArray<>();

    public int count() { return books.size(); }

    public void add(Book book) {
        Objects.requireNonNull(book, "Kitap gerekli.");
        if (indexOf(book.id()) >= 0) throw new IllegalArgumentException("Bu kitap kimliği zaten kayıtlı.");
        checkUniqueIsbn(book.isbn(), -1);
        if (book.availableStock() != book.totalStock()) {
            throw new IllegalArgumentException("Yeni kayıtta mevcut stok toplam stoka eşit olmalı.");
        }
        books.add(book);
    }

    /** Mevcut stok doğrudan düzenlenmez; ödünçteki adet korunarak hesaplanır. */
    public Book update(String id, String title, String author, String isbn, int totalStock) {
        int index = requireIndex(id);
        Book previous = books.get(index);
        int borrowed = previous.totalStock() - previous.availableStock();
        if (totalStock < borrowed) {
            throw new IllegalArgumentException("Toplam stok ödünçteki adetten (" + borrowed + ") az olamaz.");
        }
        Book updated = new Book(previous.id(), title, author, isbn, totalStock, totalStock - borrowed);
        checkUniqueIsbn(updated.isbn(), index);
        books.set(index, updated);
        return updated;
    }

    public Book remove(String id) {
        int index = requireIndex(id);
        Book book = books.get(index);
        if (book.availableStock() != book.totalStock()) {
            throw new IllegalArgumentException("Ödünçte kopyası bulunan kitap silinemez.");
        }
        return books.remove(index);
    }

    /** Bulunamazsa null döner. */
    public Book findById(String id) {
        int index = indexOf(id);
        return index < 0 ? null : books.get(index);
    }

    /** Depolama dizisini dışarı açmayan anlık kopya. Book kayıtları değişmezdir. */
    public DynamicArray<Book> findAll() {
        DynamicArray<Book> copy = new DynamicArray<>();
        for (int i = 0; i < books.size(); i++) copy.add(books.get(i));
        return copy;
    }

    public enum SearchField { TITLE, AUTHOR, ISBN }

    /** Kısmi eşleşme, O(n). Türkçe harfler Türkçe locale ile karşılaştırılır. */
    public DynamicArray<Book> search(String query, SearchField field) {
        Objects.requireNonNull(field, "Arama alanı gerekli.");
        String term = query == null ? "" : query.strip();
        term = field == SearchField.ISBN ? term.replaceAll("[\\s-]", "") : term;
        term = term.toLowerCase(Locale.forLanguageTag("tr"));
        DynamicArray<Book> results = new DynamicArray<>();
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            String value = switch (field) {
                case TITLE -> book.title();
                case AUTHOR -> book.author();
                case ISBN -> book.isbn();
            };
            if (value.toLowerCase(Locale.forLanguageTag("tr")).contains(term)) results.add(book);
        }
        return results;
    }

    /** LoanService entegrasyon noktası: başarılı ödünçte bir kopya ayırır. */
    public Book borrowCopy(String id) {
        int index = requireIndex(id);
        Book book = books.get(index);
        if (book.availableStock() == 0) throw new IllegalArgumentException("Ödünç verilebilecek kopya yok.");
        return replaceStock(index, book, book.availableStock() - 1);
    }

    /** LoanService aktif ödünç kaydını kontrol ettikten sonra çağırmalıdır. */
    public Book returnCopy(String id) {
        int index = requireIndex(id);
        Book book = books.get(index);
        if (book.availableStock() == book.totalStock()) {
            throw new IllegalArgumentException("Bu kitap için iade bekleyen kopya yok.");
        }
        return replaceStock(index, book, book.availableStock() + 1);
    }

    private Book replaceStock(int index, Book book, int available) {
        Book updated = new Book(book.id(), book.title(), book.author(), book.isbn(), book.totalStock(), available);
        books.set(index, updated);
        return updated;
    }

    private int indexOf(String id) {
        if (id == null) return -1;
        for (int i = 0; i < books.size(); i++) if (books.get(i).id().equals(id.strip())) return i;
        return -1;
    }

    private int requireIndex(String id) {
        int index = indexOf(id);
        if (index < 0) throw new IllegalArgumentException("Kitap bulunamadı.");
        return index;
    }

    private void checkUniqueIsbn(String isbn, int excludedIndex) {
        for (int i = 0; i < books.size(); i++) {
            if (i != excludedIndex && books.get(i).isbn().equals(isbn)) {
                throw new IllegalArgumentException("Bu ISBN zaten kayıtlı; mevcut kitabın stokunu güncelleyin.");
            }
        }
    }
}
