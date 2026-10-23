package tr.edu.kutuphane.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tr.edu.kutuphane.datastructures.LinkedListCustom;
import tr.edu.kutuphane.model.Book;
import tr.edu.kutuphane.model.Loan;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    private BookService bookService;
    private MemberService memberService;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        bookService = new BookService();
        memberService = new MemberService();
        loanService = new LoanService(bookService, memberService);

        bookService.add(new Book("B1", "Veri Yapıları", "Yazar A", "9781234567890", 2, 2));
        bookService.add(new Book("B2", "Algoritmalar", "Yazar B", "9780987654321", 1, 1));
    }

    @Test
    void borrowBookShouldDecreaseAvailableStockAndRecordHistory() {
        LocalDate borrowDate = LocalDate.of(2026, 10, 1);
        LocalDate dueDate = LocalDate.of(2026, 10, 15);

        Loan loan = loanService.borrowBook("L1", "B1", "M1", borrowDate, dueDate);

        assertNotNull(loan);
        assertEquals(1, loanService.count());
        assertFalse(loan.isReturned());
        assertEquals(1, bookService.findById("B1").availableStock());
    }

    @Test
    void borrowBookShouldFailWhenStockIsZeroOrDuplicateActiveLoan() {
        LocalDate borrowDate = LocalDate.of(2026, 10, 1);
        LocalDate dueDate = LocalDate.of(2026, 10, 15);

        loanService.borrowBook("L1", "B2", "M1", borrowDate, dueDate);

        // Aynı üye aynı kitabı iade etmeden tekrar alamaz
        assertThrows(IllegalArgumentException.class, () ->
                loanService.borrowBook("L2", "B2", "M1", borrowDate, dueDate));

        // Stok 0 olduğunda başka üye de ödünç alamaz
        assertThrows(IllegalArgumentException.class, () ->
                loanService.borrowBook("L3", "B2", "M2", borrowDate, dueDate));
    }

    @Test
    void returnBookShouldMarkReturnedAndIncreaseAvailableStock() {
        LocalDate borrowDate = LocalDate.of(2026, 10, 1);
        LocalDate dueDate = LocalDate.of(2026, 10, 15);
        loanService.borrowBook("L1", "B1", "M1", borrowDate, dueDate);

        Loan returned = loanService.returnBook("L1", LocalDate.of(2026, 10, 10));

        assertTrue(returned.isReturned());
        assertEquals(LocalDate.of(2026, 10, 10), returned.returnedDate());
        assertEquals(2, bookService.findById("B1").availableStock());
        assertEquals(0, loanService.findActiveLoans().size());
        assertEquals(1, loanService.findReturnedLoans().size());
    }

    @Test
    void findOverdueLoansShouldDetectLateBooksAndCalculateDays() {
        LocalDate borrowDate = LocalDate.of(2026, 10, 1);
        LocalDate dueDate = LocalDate.of(2026, 10, 15);
        loanService.borrowBook("L1", "B1", "M1", borrowDate, dueDate);
        loanService.borrowBook("L2", "B2", "M2", borrowDate, LocalDate.of(2026, 10, 25));

        LocalDate checkDate = LocalDate.of(2026, 10, 20);
        LinkedListCustom<Loan> overdueList = loanService.findOverdueLoans(checkDate);

        assertEquals(1, overdueList.size());
        Loan overdueLoan = overdueList.get(0);
        assertEquals("L1", overdueLoan.id());
        assertEquals(5, overdueLoan.overdueDays(checkDate));
    }
}