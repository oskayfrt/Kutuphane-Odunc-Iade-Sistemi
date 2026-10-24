package tr.edu.kutuphane.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import tr.edu.kutuphane.datastructures.DynamicArray;
import tr.edu.kutuphane.datastructures.LinkedListCustom;
import tr.edu.kutuphane.model.Book;
import tr.edu.kutuphane.model.Loan;
import tr.edu.kutuphane.model.Member;
import tr.edu.kutuphane.service.LoanService;
import tr.edu.kutuphane.service.MemberService;

public class MemberPanel extends JPanel {
    private final MemberService service;
    private final LoanService loanService;
    private final JTextField id = new JTextField();
    private final JTextField fullName = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField searchId = new JTextField(12);
    private final MemberTableModel tableModel = new MemberTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel status = new JLabel(" ");

    public MemberPanel(MemberService service, LoanService loanService) {
        super(new BorderLayout(8, 12));
        this.service = Objects.requireNonNull(service);
        this.loanService = Objects.requireNonNull(loanService);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(new JLabel("Üye Yönetimi"), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 6));
        addField(form, "Üye numarası", id);
        addField(form, "Ad soyad", fullName);
        addField(form, "E-posta", email);
        top.add(form, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton(actions, "Ekle", this::addMember);
        addButton(actions, "Güncelle", this::updateMember);
        addButton(actions, "Sil", this::removeMember);
        addButton(actions, "Yeni / Temizle", this::clearForm);
        addButton(actions, "Ödünç kitapları", this::showLoans);
        top.add(actions, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JPanel list = new JPanel(new BorderLayout(8, 8));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        search.add(new JLabel("Üye numarasıyla ara:"));
        search.add(searchId);
        addButton(search, "Ara", this::searchMember);
        addButton(search, "Tümünü göster", () -> { searchId.setText(""); refresh(); });
        list.add(search, BorderLayout.NORTH);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) loadSelection();
        });
        list.add(new JScrollPane(table), BorderLayout.CENTER);
        add(list, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
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
            try { action.run(); }
            catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(),
                        "İşlem yapılamadı", JOptionPane.WARNING_MESSAGE);
            }
        });
        panel.add(button);
    }

    private void addMember() {
        if (!id.isEditable()) throw new IllegalArgumentException("Yeni üye için önce Yeni / Temizle'ye basın.");
        service.add(new Member(id.getText(), fullName.getText(), email.getText()));
        refresh();
    }

    private void updateMember() {
        Member selected = selectedMember();
        service.update(selected.id(), fullName.getText(), email.getText());
        refresh();
    }

    private void removeMember() {
        Member selected = selectedMember();
        if (loanService.findByMemberId(selected.id()).find(loan -> !loan.isReturned()) != null) {
            throw new IllegalArgumentException("İade etmediği kitap olan üye silinemez.");
        }
        int answer = JOptionPane.showConfirmDialog(this, selected.fullName() + " silinsin mi?",
                "Üye silme", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            service.remove(selected.id());
            clearForm();
            refresh();
        }
    }

    private void searchMember() {
        String term = searchId.getText();
        if (term.isBlank()) { refresh(); return; }
        Member found = service.findById(term);
        DynamicArray<Member> rows = new DynamicArray<>();
        if (found != null) rows.add(found);
        tableModel.setRows(rows);
        status.setText(found == null ? "Bu numarada üye bulunamadı." : "1 üye bulundu.");
    }

    private void showLoans() {
        Member selected = selectedMember();
        LinkedListCustom<Loan> loans = loanService.findByMemberId(selected.id());
        if (loans.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bu üyenin ödünç kaydı yok.");
            return;
        }
        StringBuilder text = new StringBuilder();
        loans.forEach(loan -> {
            Book book = loanService.getBookService().findById(loan.bookId());
            String title = book == null ? loan.bookId() : book.title();
            text.append("• ").append(title).append(" — ")
                .append(loan.isReturned() ? "iade edildi (" + loan.returnedDate() + ")"
                                          : "son teslim " + loan.dueDate())
                .append('\n');
        });
        JTextArea area = new JTextArea(text.toString(), 10, 40);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                selected.fullName() + " — ödünç kayıtları", JOptionPane.INFORMATION_MESSAGE);
    }

    private Member selectedMember() {
        int row = table.getSelectedRow();
        if (row < 0) throw new IllegalArgumentException("Önce tablodan bir üye seçin.");
        return tableModel.rows.get(row);
    }

    private void loadSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Member member = tableModel.rows.get(row);
        id.setText(member.id());
        id.setEditable(false);
        fullName.setText(member.fullName());
        email.setText(member.email());
    }

    private void clearForm() {
        table.clearSelection();
        id.setEditable(true);
        id.setText("");
        fullName.setText("");
        email.setText("");
    }

    private void refresh() {
        tableModel.setRows(service.findAll());
        status.setText("Toplam üye: " + service.count());
    }

    private static final class MemberTableModel extends AbstractTableModel {
        private static final String[] COLUMNS = {"Üye no", "Ad soyad", "E-posta"};
        private DynamicArray<Member> rows = new DynamicArray<>();

        void setRows(DynamicArray<Member> newRows) { rows = newRows; fireTableDataChanged(); }
        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int column) { return COLUMNS[column]; }
        @Override public Object getValueAt(int row, int column) {
            Member member = rows.get(row);
            return switch (column) {
                case 0 -> member.id();
                case 1 -> member.fullName();
                default -> member.email();
            };
        }
    }
}