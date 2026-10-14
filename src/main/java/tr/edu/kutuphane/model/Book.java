package tr.edu.kutuphane.model;

/** Kitap kimliği sabittir; stok ve ödünç uygunluğu servis katmanında yönetilir. */
public record Book(String id, String title, String author, String isbn) {
}
