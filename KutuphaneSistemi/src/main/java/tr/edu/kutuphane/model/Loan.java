package tr.edu.kutuphane.model;

import java.time.LocalDate;

/** Kimliklerle kitap ve üyeyi ilişkilendirir; returnedDate iade öncesinde null olur. */
public record Loan(String id, String bookId, String memberId,
                   LocalDate borrowedDate, LocalDate dueDate, LocalDate returnedDate) {
}
