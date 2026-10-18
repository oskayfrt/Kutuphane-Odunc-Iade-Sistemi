package tr.edu.kutuphane.service;

import org.junit.jupiter.api.Test;
import tr.edu.kutuphane.model.Book;
import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    private Book sample() { return new Book("K1", "İnce Memed", "Yaşar Kemal", "978-975-08-0000-1", 3, 3); }

    @Test void addsNormalizesAndKeepsSnapshotIndependent() {
        BookService service = new BookService();
        service.add(sample());
        assertEquals("9789750800001", service.findById("K1").isbn());
        service.findAll().remove(0);
        assertEquals(1, service.count());
        assertNull(service.findById("missing"));
    }

    @Test void rejectsDuplicateIdAndNormalizedIsbn() {
        BookService service = new BookService();
        service.add(sample());
        assertThrows(IllegalArgumentException.class, () -> service.add(sample()));
        assertThrows(IllegalArgumentException.class, () -> service.add(
                new Book("K2", "Başka", "Yazar", "9789750800001")));
        assertEquals(1, service.count());
    }

    @Test void searchesTurkishTitleAuthorAndPartialIsbn() {
        BookService service = new BookService();
        service.add(sample());
        assertEquals(1, service.search("ince", BookService.SearchField.TITLE).size());
        assertEquals(1, service.search("YAŞAR", BookService.SearchField.AUTHOR).size());
        assertEquals(1, service.search("978-975", BookService.SearchField.ISBN).size());
        assertEquals(0, service.search("olmayan", BookService.SearchField.TITLE).size());
        assertEquals(1, service.search("", BookService.SearchField.TITLE).size());
    }

    @Test void updatePreservesBorrowedCopiesAndRejectsDestructiveChanges() {
        BookService service = new BookService();
        service.add(sample());
        service.borrowCopy("K1");
        Book updated = service.update("K1", "Yeni başlık", "Yazar", sample().isbn(), 5);
        assertEquals(4, updated.availableStock());
        assertThrows(IllegalArgumentException.class, () -> service.update("K1", "Ad", "Yazar", sample().isbn(), 0));
        assertThrows(IllegalArgumentException.class, () -> service.remove("K1"));
        assertEquals(updated, service.findById("K1"));
        service.returnCopy("K1");
        service.remove("K1");
        assertEquals(0, service.count());
    }

    @Test void enforcesStockLimits() {
        BookService service = new BookService();
        service.add(new Book("K1", "Ad", "Yazar", "123456789X"));
        assertThrows(IllegalArgumentException.class, () -> service.returnCopy("K1"));
        service.borrowCopy("K1");
        assertThrows(IllegalArgumentException.class, () -> service.borrowCopy("K1"));
        assertEquals(0, service.findById("K1").availableStock());
        service.returnCopy("K1");
        assertEquals(1, service.findById("K1").availableStock());
    }

    @Test void failedUpdateDoesNotMutateAndMissingIdsFailClearly() {
        BookService service = new BookService();
        service.add(sample());
        service.add(new Book("K2", "Ad", "Yazar", "123456789X"));
        assertThrows(IllegalArgumentException.class, () -> service.update("K1", "Ad", "Yazar", "123456789X", 4));
        assertEquals(sample(), service.findById("K1"));
        assertThrows(IllegalArgumentException.class, () -> service.remove("missing"));
        assertThrows(IllegalArgumentException.class, () -> service.borrowCopy("missing"));
        assertThrows(IllegalArgumentException.class, () -> service.returnCopy("missing"));
        assertThrows(IllegalArgumentException.class, () -> service.update("missing", "Ad", "Yazar", "123456789X", 1));
    }

    @Test void validatesModelAndInitialStock() {
        assertThrows(IllegalArgumentException.class, () -> new Book(" ", "Ad", "Yazar", "123456789X"));
        assertThrows(IllegalArgumentException.class, () -> new Book("K1", " ", "Yazar", "123456789X"));
        assertThrows(IllegalArgumentException.class, () -> new Book("K1", "Ad", null, "123456789X"));
        assertThrows(IllegalArgumentException.class, () -> new Book("K1", "Ad", "Yazar", "abc"));
        assertThrows(IllegalArgumentException.class, () -> new Book("K1", "Ad", "Yazar", "123456789X", -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Book("K1", "Ad", "Yazar", "123456789X", 1, 2));
        assertThrows(IllegalArgumentException.class, () -> new Book("K1", "Ad", "Yazar", "123456789X", 1, -1));
        assertThrows(IllegalArgumentException.class, () -> new BookService().add(
                new Book("K1", "Ad", "Yazar", "123456789X", 2, 1)));
    }

    @Test void zeroStockBookCanBeStoredButNotBorrowed() {
        BookService service = new BookService();
        service.add(new Book("K0", "Ad", "Yazar", "123456789X", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> service.borrowCopy("K0"));
        service.remove("K0");
        assertEquals(0, service.count());
    }
}
