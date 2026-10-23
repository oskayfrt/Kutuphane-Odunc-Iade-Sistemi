package tr.edu.kutuphane.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Kimliklerle kitap ve üyeyi ilişkilendirir; returnedDate iade öncesinde null olur.
 * İade durumu, tarih takibi ve gecikme hesaplama yardımcı metotlarını içerir.
 */
public record Loan(String id, String bookId, String memberId,
                   LocalDate borrowedDate, LocalDate dueDate, LocalDate returnedDate) {

    /**
     * Kitabın iade edilip edilmediğini döndürür.
     */
    public boolean isReturned() {
        return returnedDate != null;
    }

    /**
     * Record yapısı değişmez (immutable) olduğu için, iade işlemi yapıldığında
     * iade tarihi güncellenmiş yeni bir Loan nesnesi oluşturup döndürür.
     */
    public Loan withReturnedDate(LocalDate newReturnedDate) {
        return new Loan(id, bookId, memberId, borrowedDate, dueDate, newReturnedDate);
    }

    /**
     * Verilen kontrol tarihine göre kitabın gecikip gecikmediğini belirler.
     * - Kitap iade edilmişse: İade tarihi son teslim tarihini (dueDate) geçmiş mi diye bakar.
     * - Kitap henüz iade edilmemişse: Kontrol tarihi son teslim tarihini geçmiş mi diye bakar.
     */
    public boolean isOverdue(LocalDate referenceDate) {
        if (dueDate == null) {
            return false;
        }
        LocalDate checkDate = (returnedDate != null) ? returnedDate : referenceDate;
        if (checkDate == null) {
            return false;
        }
        return checkDate.isAfter(dueDate);
    }

    /**
     * Bugünün tarihine göre kitabın gecikip gecikmediğini kontrol eder.
     */
    public boolean isOverdue() {
        return isOverdue(LocalDate.now());
    }

    /**
     * Geciken kitaplar için kaç gün geciktiğini hesaplar. Gecikme yoksa 0 döner.
     */
    public long overdueDays(LocalDate referenceDate) {
        if (!isOverdue(referenceDate)) {
            return 0;
        }
        LocalDate checkDate = (returnedDate != null) ? returnedDate : referenceDate;
        return ChronoUnit.DAYS.between(dueDate, checkDate);
    }
}