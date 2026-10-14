package tr.edu.kutuphane.service;

import java.util.Objects;
import tr.edu.kutuphane.datastructures.LinkedListCustom;
import tr.edu.kutuphane.model.Loan;

/** 3. kişi: Kitap ve üye servislerinin aynı örneklerini kullanır. */
public class LoanService {
    private final BookService bookService;
    private final MemberService memberService;
    private final LinkedListCustom<Loan> loans = new LinkedListCustom<>();

    public LoanService(BookService bookService, MemberService memberService) {
        this.bookService = Objects.requireNonNull(bookService);
        this.memberService = Objects.requireNonNull(memberService);
    }

    public int count() { return loans.size(); }
    // TODO: Üye/kitap varlığını ve kitabın uygunluğunu denetleyerek ödünç verin.
    // TODO: İade tarihi, aktif ödünç kontrolü ve bekleme kuyruğu entegrasyonunu yazın.
}
