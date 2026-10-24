package tr.edu.kutuphane.model;

/** Üyenin temel kayıt bilgileri. */
public record Member(String id, String fullName, String email) {
    public Member {
        id = required(id, "Üye numarası");
        fullName = required(fullName, "Ad soyad");
        email = required(email, "E-posta");
        if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("E-posta geçerli değil.");
        }
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " boş olamaz.");
        return value.strip();
    }
}