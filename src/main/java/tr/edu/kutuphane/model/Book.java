package tr.edu.kutuphane.model;

/** Değişmez kitap kaydı. Aynı baskının kopyaları stok olarak tutulur. */
public record Book(String id, String title, String author, String isbn,
                   int totalStock, int availableStock) {
    /** Eski dört alanlı oluşturma sözleşmesi: bir adet, ödünçte olmayan kitap. */
    public Book(String id, String title, String author, String isbn) {
        this(id, title, author, isbn, 1, 1);
    }

    public Book {
        id = required(id, "Kitap kimliği");
        title = required(title, "Kitap adı");
        author = required(author, "Yazar");
        isbn = normalizeIsbn(isbn);
        if (totalStock < 0 || availableStock < 0 || availableStock > totalStock) {
            throw new IllegalArgumentException("Stoklar 0 ≤ mevcut ≤ toplam koşulunu sağlamalı.");
        }
    }

    /** Ayraçları temizler; ISBN-10/13 rakam uzunluğu denetlenir, sağlama hanesi denetlenmez. */
    public static String normalizeIsbn(String value) {
        String normalized = required(value, "ISBN").replaceAll("[\\s-]", "")
                .toUpperCase(java.util.Locale.ROOT);
        if (!normalized.matches("[0-9]{13}|[0-9]{9}[0-9X]")) {
            throw new IllegalArgumentException("ISBN, 10 veya 13 haneli olmalı (ISBN-10 son hanesi X olabilir).");
        }
        return normalized;
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " boş olamaz.");
        return value.strip();
    }
}
