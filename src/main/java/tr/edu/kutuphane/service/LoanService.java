package tr.edu.kutuphane.service;

import tr.edu.kutuphane.datastructures.LinkedListCustom;
import tr.edu.kutuphane.model.Book;
import tr.edu.kutuphane.model.Loan;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Objects;

/** 3. kişi: Kitap ve üye servislerinin aynı örneklerini kullanır. */
public class LoanService {
    public static final int DEFAULT_LOAN_DAYS = 14;

    private final BookService bookService;
    private final MemberService memberService;
    private final LinkedListCustom<Loan> loans = new LinkedListCustom<>();
    private int nextSequence = 1;

    public LoanService(BookService bookService, MemberService memberService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService gerekli.");
        this.memberService = Objects.requireNonNull(memberService, "MemberService gerekli.");
    }

    public int count() {
        return loans.size();
    }

    public BookService getBookService() {
        return bookService;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    /**
     * Bugünün tarihi ve varsayılan ödünç süresi (14 gün) ile kitap ödünç verir.
     */
    public Loan borrowBook(String bookId, String memberId) {
        LocalDate today = LocalDate.now();
        return borrowBook(bookId, memberId, today, today.plusDays(DEFAULT_LOAN_DAYS));
    }

    /**
     * Belirtilen ödünç tarihi ve gün sayısı ile kitap ödünç verir.
     */
    public Loan borrowBook(String bookId, String memberId, LocalDate borrowedDate, int loanDays) {
        Objects.requireNonNull(borrowedDate, "Ödünç alma tarihi gerekli.");
        if (loanDays <= 0) {
            throw new IllegalArgumentException("Ödünç süresi en az 1 gün olmalıdır.");
        }
        return borrowBook(bookId, memberId, borrowedDate, borrowedDate.plusDays(loanDays));
    }

    /**
     * Otomatik işlem kimliği (ID) üreterek kitap ödünç verir.
     */
    public Loan borrowBook(String bookId, String memberId, LocalDate borrowedDate, LocalDate dueDate) {
        String generatedId = generateLoanId();
        return borrowBook(generatedId, bookId, memberId, borrowedDate, dueDate);
    }

    /**
     * Üye/kitap varlığını ve kitabın stok uygunluğunu denetleyerek ödünç verir.
     */
    public Loan borrowBook(String loanId, String bookId, String memberId, LocalDate borrowedDate, LocalDate dueDate) {
        String cleanLoanId = requireText(loanId, "Ödünç işlem kimliği gerekli.");
        String cleanBookId = requireText(bookId, "Kitap kimliği gerekli.");
        String cleanMemberId = requireText(memberId, "Üye kimliği gerekli.");
        Objects.requireNonNull(borrowedDate, "Ödünç alma tarihi gerekli.");
        Objects.requireNonNull(dueDate, "Son teslim tarihi gerekli.");

        if (dueDate.isBefore(borrowedDate)) {
            throw new IllegalArgumentException("Son teslim tarihi ödünç alma tarihinden önce olamaz.");
        }

        if (findById(cleanLoanId) != null) {
            throw new IllegalArgumentException("Bu ödünç işlem kimliği zaten kayıtlı: " + cleanLoanId);
        }

        // 1. Üye varlığını kontrol et (MemberService tamamlandığında otomatik denetler)
        validateMemberExists(cleanMemberId);

        // 2. Kitap varlığını kontrol et
        Book book = bookService.findById(cleanBookId);
        if (book == null) {
            throw new IllegalArgumentException("Kitap bulunamadı.");
        }

        // 3. Aynı üyenin aynı kitabı henüz iade etmeden tekrar almasını engelle
        Loan existingActive = loans.find(l ->
                !l.isReturned()
                        && l.bookId().equals(book.id())
                        && l.memberId().equals(cleanMemberId));
        if (existingActive != null) {
            throw new IllegalArgumentException("Bu üye aynı kitabı zaten ödünç almış ve henüz iade etmemiş.");
        }

        // 4. Kitap stok kontrolü ve stoktan 1 düşme (BookService entegrasyonu)
        bookService.borrowCopy(book.id());

        // 5. Yeni Loan kaydını oluştur ve özel bağlı listeye (LinkedListCustom) ekle
        Loan newLoan = new Loan(cleanLoanId, book.id(), cleanMemberId, borrowedDate, dueDate, null);
        loans.addLast(newLoan);
        return newLoan;
    }

    /**
     * Bugünün tarihiyle ödünç kaydını iade alır.
     */
    public Loan returnBook(String loanId) {
        return returnBook(loanId, LocalDate.now());
    }

    /**
     * İşlem kimliği (loanId) ve iade tarihi ile kitabı iade alır, kitap stokunu 1 artırır.
     */
    public Loan returnBook(String loanId, LocalDate returnedDate) {
        String cleanLoanId = requireText(loanId, "Ödünç işlem kimliği gerekli.");
        Objects.requireNonNull(returnedDate, "İade tarihi gerekli.");

        Loan existing = findById(cleanLoanId);
        if (existing == null) {
            throw new IllegalArgumentException("Ödünç kaydı bulunamadı.");
        }
        if (existing.isReturned()) {
            throw new IllegalArgumentException("Bu kitap zaten iade edilmiş.");
        }
        if (returnedDate.isBefore(existing.borrowedDate())) {
            throw new IllegalArgumentException("İade tarihi ödünç alma tarihinden önce olamaz.");
        }

        // Record değişmez (immutable) olduğu için yeni iade tarihli kayıt oluşturulur
        Loan updatedLoan = existing.withReturnedDate(returnedDate);
        loans.replaceFirst(l -> l.id().equals(cleanLoanId), updatedLoan);

        // Kitap servisinde mevcut stoku 1 artır
        bookService.returnCopy(existing.bookId());

        return updatedLoan;
    }

    /**
     * Kitap kimliği ve üye kimliği üzerinden aktif ödünç kaydını bulup iade alır.
     */
    public Loan returnBookByBookAndMember(String bookId, String memberId, LocalDate returnedDate) {
        String cleanBookId = requireText(bookId, "Kitap kimliği gerekli.");
        String cleanMemberId = requireText(memberId, "Üye kimliği gerekli.");

        Loan activeLoan = loans.find(l ->
                !l.isReturned()
                        && l.bookId().equals(cleanBookId)
                        && l.memberId().equals(cleanMemberId));
        if (activeLoan == null) {
            throw new IllegalArgumentException("Bu üye ve kitap için aktif ödünç kaydı bulunamadı.");
        }
        return returnBook(activeLoan.id(), returnedDate);
    }

    /**
     * İşlem kimliğine (ID) göre ödünç kaydını bulur, yoksa null döner.
     */
    public Loan findById(String loanId) {
        if (loanId == null) return null;
        String clean = loanId.strip();
        return loans.find(l -> l.id().equals(clean));
    }

    /**
     * Tüm ödünç ve iade geçmişini (bağlı liste kopyası olarak) döndürür.
     */
    public LinkedListCustom<Loan> findAll() {
        return loans.filter(l -> true);
    }

    /**
     * Henüz iade edilmemiş (aktif) ödünç kayıtlarını döndürür.
     */
    public LinkedListCustom<Loan> findActiveLoans() {
        return loans.filter(l -> !l.isReturned());
    }

    /**
     * İade edilmiş geçmiş kayıtları döndürür.
     */
    public LinkedListCustom<Loan> findReturnedLoans() {
        return loans.filter(Loan::isReturned);
    }

    /**
     * Bugünün tarihine göre geciken (teslim tarihi geçmiş ve iade edilmemiş) kitapları belirler.
     */
    public LinkedListCustom<Loan> findOverdueLoans() {
        return findOverdueLoans(LocalDate.now());
    }

    /**
     * Verilen referans tarihe göre geciken (henüz iade edilmemiş ve son teslim tarihi geçmiş) kitapları belirler.
     */
    public LinkedListCustom<Loan> findOverdueLoans(LocalDate referenceDate) {
        Objects.requireNonNull(referenceDate, "Kontrol tarihi gerekli.");
        return loans.filter(l -> !l.isReturned() && l.isOverdue(referenceDate));
    }

    /**
     * Belirli bir üyenin tüm ödünç/iade geçmişini döndürür.
     */
    public LinkedListCustom<Loan> findByMemberId(String memberId) {
        String cleanMemberId = requireText(memberId, "Üye kimliği gerekli.");
        return loans.filter(l -> l.memberId().equals(cleanMemberId));
    }

    /**
     * Belirli bir kitabın tüm ödünç/iade geçmişini döndürür.
     */
    public LinkedListCustom<Loan> findByBookId(String bookId) {
        String cleanBookId = requireText(bookId, "Kitap kimliği gerekli.");
        return loans.filter(l -> l.bookId().equals(cleanBookId));
    }

    private String generateLoanId() {
        String candidate;
        do {
            candidate = "L-" + (nextSequence++);
        } while (findById(candidate) != null);
        return candidate;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.strip().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.strip();
    }

    /**
     * 2. kişi MemberService içerisine findById metodunu eklediğinde ve üye kaydı bulunduğunda
     * üyenin sistemde kayıtlı olup olmadığını denetler.
     */
    private void validateMemberExists(String memberId) {
        if (memberService.count() <= 0) {
            return;
        }
        try {
            Method findById = memberService.getClass().getMethod("findById", String.class);
            Object member = findById.invoke(memberService, memberId);
            if (member == null) {
                throw new IllegalArgumentException("Üye bulunamadı: " + memberId);
            }
        } catch (NoSuchMethodException ignored) {
            // MemberService.findById henüz 2. kişi tarafından eklenmemişse engelleme yapma
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception ignored) {
        }
    }
}