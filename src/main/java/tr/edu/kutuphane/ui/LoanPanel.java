package tr.edu.kutuphane.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import tr.edu.kutuphane.datastructures.LinkedListCustom;
import tr.edu.kutuphane.model.Book;
import tr.edu.kutuphane.model.Loan;
import tr.edu.kutuphane.service.LoanService;

/** Enes Kurt: Ödünç alma, iade, gecikme takibi ve bağlı liste işlem geçmişi ekranı. */
public class LoanPanel extends JPanel {
    private final LoanService service;

    private final JTextField loanIdField = new JTextField();
    private final JTextField bookIdField = new JTextField();
    private final JTextField memberIdField = new JTextField();
    private final JTextField borrowedDateField = new JTextField();
    private final JTextField dueDateField = new JTextField();
    private final JTextField referenceDateField = new JTextField();

    private final JComboBox<String> filterBox = new JComboBox<>(new String[]{
            "Tüm geçmiş", "Aktif ödünçler", "Geciken kitaplar", "İade edilenler"
    });
    private final JTextField query = new JTextField(16);

    private final LoanTableModel tableModel = new LoanTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel status = new JLabel(" ");

    public LoanPanel(LoanService service) {
        super(new BorderLayout(8, 12));
        this.service = Objects.requireNonNull(service);

        resetDatesToDefault();

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(new JLabel("Ödünç Alma ve İade Yönetimi — Enes Kurt"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 6));
        addField(form, "İşlem kimliği (Boş bırakılırsa otomatik)", loanIdField);
        addField(form, "Kitap kimliği", bookIdField);
        addField(form, "Üye kimliği", memberIdField);
        addField(form, "Ödünç alma tarihi (YYYY-AA-GG)", borrowedDateField);
        addField(form, "Son teslim tarihi (YYYY-AA-GG)", dueDateField);
        addField(form, "İade / Kontrol tarihi (YYYY-AA-GG)", referenceDateField);
        top.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton(actions, "Ödünç Ver", this::borrowBook);
        addButton(actions, "Seçili Kaydı İade Al", this::returnSelectedBook);
        addButton(actions, "Gecikenleri Listele", () -> filterBox.setSelectedIndex(2));
        addButton(actions, "Yeni / Temizle", this::clearForm);
        top.add(actions, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JPanel list = new JPanel(new BorderLayout(8, 8));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        search.add(new JLabel("Görünüm:"));
        search.add(filterBox);
        search.add(new JLabel("Ara (İşlem / Kitap / Üye):"));
        search.add(query);
        addButton(search, "Tümünü göster", () -> {
            filterBox.setSelectedIndex(0);
            query.setText("");
            refresh();
        });
        list.add(search, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) loadSelection();
        });
        list.add(new JScrollPane(table), BorderLayout.CENTER);
        add(list, BorderLayout.CENTER);

        JPanel footer = new JPanel(new GridLayout(0, 1, 0, 4));
        footer.add(status);
        footer.add(new JLabel("İşlem geçmişi LinkedListCustom bağlı listesinde tutulur; uygulama kapatılınca silinir."));
        add(footer, BorderLayout.SOUTH);

        DocumentListener refreshListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { refresh(); }
            public void removeUpdate(DocumentEvent event) { refresh(); }
            public void changedUpdate(DocumentEvent event) { refresh(); }
        };
        query.getDocument().addDocumentListener(refreshListener);
        referenceDateField.getDocument().addDocumentListener(refreshListener);
        filterBox.addActionListener(event -> refresh());

        // Kitap veya üye modülünden dönüldüğünde güncel verileri yeniden oku.
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent event) {
                refresh();
            }
        });

        refresh();
    }

    private void addField(JPanel form, String label, JComponent field) {
        JLabel caption = new JLabel(label);
        caption.setLabelFor(field);
        form.add(caption);
        form.add(field);
    }

    private void addButton(JPanel panel, String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(event -> {
            try {
                action.run();
            } catch (IllegalArgumentException | IllegalStateException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "İşlem yapılamadı", JOptionPane.WARNING_MESSAGE);
            }
        });
        panel.add(button);
    }

    private LocalDate parseDate(String text, String fieldLabel) {
        try {
            return LocalDate.parse(text == null ? "" : text.strip());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(fieldLabel + " YYYY-AA-GG formatında olmalıdır (Örn: 2026-10-24).");
        }
    }

    private LocalDate currentReferenceDateOrToday() {
        try {
            return LocalDate.parse(referenceDateField.getText().strip());
        } catch (Exception ignored) {
            return LocalDate.now();
        }
    }

    private void borrowBook() {
        LocalDate borrowedDate = parseDate(borrowedDateField.getText(), "Ödünç alma tarihi");
        LocalDate dueDate = parseDate(dueDateField.getText(), "Son teslim tarihi");
        String customLoanId = loanIdField.getText() == null ? "" : loanIdField.getText().strip();

        if (customLoanId.isEmpty() || !loanIdField.isEditable()) {
            service.borrowBook(bookIdField.getText(), memberIdField.getText(), borrowedDate, dueDate);
        } else {
            service.borrowBook(customLoanId, bookIdField.getText(), memberIdField.getText(), borrowedDate, dueDate);
        }

        query.setText("");
        clearForm();
        refresh();
    }

    private void returnSelectedBook() {
        int row = table.getSelectedRow();
        LocalDate returnDate = parseDate(referenceDateField.getText(), "İade / Kontrol tarihi");

        if (row >= 0) {
            Loan selected = tableModel.rows.get(row);
            service.returnBook(selected.id(), returnDate);
        } else if (!bookIdField.getText().isBlank() && !memberIdField.getText().isBlank()) {
            service.returnBookByBookAndMember(bookIdField.getText(), memberIdField.getText(), returnDate);
        } else {
            throw new IllegalArgumentException("İade için önce tablodan bir kayıt seçin veya Kitap/Üye kimliği girin.");
        }

        clearForm();
        refresh();
    }

    private void loadSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Loan loan = tableModel.rows.get(row);
        loanIdField.setText(loan.id());
        loanIdField.setEditable(false);
        bookIdField.setText(loan.bookId());
        memberIdField.setText(loan.memberId());
        borrowedDateField.setText(loan.borrowedDate().toString());
        dueDateField.setText(loan.dueDate().toString());
    }

    private void resetDatesToDefault() {
        LocalDate today = LocalDate.now();
        borrowedDateField.setText(today.toString());
        dueDateField.setText(today.plusDays(LoanService.DEFAULT_LOAN_DAYS).toString());
        referenceDateField.setText(today.toString());
    }

    private void clearForm() {
        table.clearSelection();
        loanIdField.setEditable(true);
        loanIdField.setText("");
        bookIdField.setText("");
        memberIdField.setText("");
        LocalDate today = LocalDate.now();
        borrowedDateField.setText(today.toString());
        dueDateField.setText(today.plusDays(LoanService.DEFAULT_LOAN_DAYS).toString());
    }

    public void refresh() {
        table.clearSelection();
        LocalDate refDate = currentReferenceDateOrToday();

        LinkedListCustom<Loan> baseList = switch (filterBox.getSelectedIndex()) {
            case 1 -> service.findActiveLoans();
            case 2 -> service.findOverdueLoans(refDate);
            case 3 -> service.findReturnedLoans();
            default -> service.findAll();
        };

        String term = query.getText() == null ? "" : query.getText().strip().toLowerCase(Locale.forLanguageTag("tr"));
        if (!term.isEmpty()) {
            baseList = baseList.filter(loan -> matchesSearch(loan, term));
        }

        tableModel.referenceDate = refDate;
        tableModel.rows = baseList;
        tableModel.fireTableDataChanged();

        int total = service.count();
        int active = service.findActiveLoans().size();
        int returned = service.findReturnedLoans().size();
        int overdue = service.findOverdueLoans(refDate).size();

        status.setText("Toplam işlem: " + total
                + " | Gösterilen: " + tableModel.rows.size()
                + " | Aktif ödünç: " + active
                + " | İade edilen: " + returned
                + " | Geciken: " + overdue);
    }

    private boolean matchesSearch(Loan loan, String term) {
        String bookTitle = resolveBookTitle(loan.bookId()).toLowerCase(Locale.forLanguageTag("tr"));
        return loan.id().toLowerCase(Locale.forLanguageTag("tr")).contains(term)
                || loan.bookId().toLowerCase(Locale.forLanguageTag("tr")).contains(term)
                || loan.memberId().toLowerCase(Locale.forLanguageTag("tr")).contains(term)
                || bookTitle.contains(term);
    }

    private String resolveBookTitle(String bookId) {
        Book book = service.getBookService().findById(bookId);
        return book != null ? book.title() : "-";
    }

    private final class LoanTableModel extends AbstractTableModel {
        private final String[] columns = {
                "İşlem No", "Kitap Kimliği", "Kitap Adı", "Üye Kimliği",
                "Ödünç Tarihi", "Son Teslim", "İade Tarihi", "Durum"
        };
        private LinkedListCustom<Loan> rows = new LinkedListCustom<>();
        private LocalDate referenceDate = LocalDate.now();

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return String.class;
        }

        @Override
        public Object getValueAt(int row, int column) {
            Loan loan = rows.get(row);
            return switch (column) {
                case 0 -> loan.id();
                case 1 -> loan.bookId();
                case 2 -> resolveBookTitle(loan.bookId());
                case 3 -> loan.memberId();
                case 4 -> loan.borrowedDate().toString();
                case 5 -> loan.dueDate().toString();
                case 6 -> loan.returnedDate() != null ? loan.returnedDate().toString() : "-";
                case 7 -> formatStatus(loan, referenceDate);
                default -> throw new IndexOutOfBoundsException("Sütun: " + column);
            };
        }

        private String formatStatus(Loan loan, LocalDate refDate) {
            if (loan.isReturned()) {
                return loan.isOverdue(refDate)
                        ? "İade Edildi (" + loan.overdueDays(refDate) + " gün gecikmeli)"
                        : "İade Edildi";
            }
            return loan.isOverdue(refDate)
                    ? "Gecikti (" + loan.overdueDays(refDate) + " gün)"
                    : "Ödünçte";
        }
    }
}