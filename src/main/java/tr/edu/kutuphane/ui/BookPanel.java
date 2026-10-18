package tr.edu.kutuphane.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import tr.edu.kutuphane.datastructures.DynamicArray;
import tr.edu.kutuphane.model.Book;
import tr.edu.kutuphane.service.BookService;

/** Fırat Oskay: Kitap formu ve salt okunur liste. İş kuralları BookService'tedir. */
public class BookPanel extends JPanel {
    private final BookService service;
    private final JTextField id = new JTextField();
    private final JTextField title = new JTextField();
    private final JTextField author = new JTextField();
    private final JTextField isbn = new JTextField();
    private final JSpinner stock = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
    private final JTextField query = new JTextField(18);
    private final JComboBox<String> searchField = new JComboBox<>(new String[]{"Kitap adı", "Yazar", "ISBN"});
    private final BookTableModel tableModel = new BookTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel status = new JLabel(" ");

    public BookPanel(BookService service) {
        super(new BorderLayout(8, 12));
        this.service = Objects.requireNonNull(service);
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(new JLabel("Kitap Yönetimi — Fırat Oskay"), BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 6));
        addField(form, "Kitap kimliği", id);
        addField(form, "Kitap adı", title);
        addField(form, "Yazar", author);
        addField(form, "ISBN (10 / 13 hane)", isbn);
        addField(form, "Toplam stok", stock);
        top.add(form, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton(actions, "Ekle", this::addBook);
        addButton(actions, "Güncelle", this::updateBook);
        addButton(actions, "Sil", this::removeBook);
        addButton(actions, "Yeni / Temizle", this::clearForm);
        top.add(actions, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JPanel list = new JPanel(new BorderLayout(8, 8));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT));
        search.add(new JLabel("Ara:"));
        search.add(searchField);
        search.add(query);
        addButton(search, "Tümünü göster", () -> { query.setText(""); refresh(); });
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
        footer.add(new JLabel("Kayıtlar bellekte tutulur; uygulama kapatılınca silinir."));
        add(footer, BorderLayout.SOUTH);
        query.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { refresh(); }
            public void removeUpdate(DocumentEvent event) { refresh(); }
            public void changedUpdate(DocumentEvent event) { refresh(); }
        });
        searchField.addActionListener(event -> refresh());
        // Ödünç/iade modülünden dönüldüğünde güncel stokları yeniden oku.
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentShown(java.awt.event.ComponentEvent event) { refresh(); }
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
            try { action.run(); }
            catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(), "İşlem yapılamadı", JOptionPane.WARNING_MESSAGE);
            }
        });
        panel.add(button);
    }

    private int readStock() {
        try { stock.commitEdit(); }
        catch (java.text.ParseException exception) {
            throw new IllegalArgumentException("Toplam stok geçerli bir tam sayı olmalı.");
        }
        return ((Number) stock.getValue()).intValue();
    }

    private void addBook() {
        if (!id.isEditable()) throw new IllegalArgumentException("Yeni kitap için önce Yeni / Temizle'ye basın.");
        int quantity = readStock();
        service.add(new Book(id.getText(), title.getText(), author.getText(), isbn.getText(), quantity, quantity));
        query.setText("");
        refresh();
    }

    private void updateBook() {
        Book selected = selectedBook();
        service.update(selected.id(), title.getText(), author.getText(), isbn.getText(), readStock());
        refresh();
    }

    private void removeBook() {
        Book selected = selectedBook();
        int answer = JOptionPane.showConfirmDialog(this, "“" + selected.title() + "” silinsin mi?",
                "Kitap silme", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            service.remove(selected.id());
            refresh();
        }
    }

    private Book selectedBook() {
        int row = table.getSelectedRow();
        if (row < 0) throw new IllegalArgumentException("Önce tablodan bir kitap seçin.");
        return tableModel.rows.get(row);
    }

    private void loadSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Book book = tableModel.rows.get(row);
        id.setText(book.id());
        id.setEditable(false);
        title.setText(book.title());
        author.setText(book.author());
        isbn.setText(book.isbn());
        stock.setValue(book.totalStock());
    }

    private void clearForm() {
        table.clearSelection();
        id.setEditable(true);
        id.setText("");
        title.setText("");
        author.setText("");
        isbn.setText("");
        stock.setValue(1);
    }

    public void refresh() {
        table.clearSelection();
        tableModel.rows = service.search(query.getText(), BookService.SearchField.values()[searchField.getSelectedIndex()]);
        tableModel.fireTableDataChanged();
        clearForm();
        long total = 0;
        long available = 0;
        DynamicArray<Book> all = service.findAll();
        for (int i = 0; i < all.size(); i++) {
            total += all.get(i).totalStock();
            available += all.get(i).availableStock();
        }
        status.setText("Kayıt: " + all.size() + " | Gösterilen: " + tableModel.rows.size()
                + " | Toplam stok: " + total + " | Mevcut: " + available + " | Ödünçte: " + (total - available));
    }

    private static final class BookTableModel extends AbstractTableModel {
        private final String[] columns = {"Kimlik", "Kitap adı", "Yazar", "ISBN", "Toplam", "Mevcut", "Ödünçte"};
        private DynamicArray<Book> rows = new DynamicArray<>();
        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }
        @Override public Class<?> getColumnClass(int column) { return column < 4 ? String.class : Integer.class; }
        @Override public Object getValueAt(int row, int column) {
            Book book = rows.get(row);
            return switch (column) {
                case 0 -> book.id();
                case 1 -> book.title();
                case 2 -> book.author();
                case 3 -> book.isbn();
                case 4 -> book.totalStock();
                case 5 -> book.availableStock();
                case 6 -> book.totalStock() - book.availableStock();
                default -> throw new IndexOutOfBoundsException("Sütun: " + column);
            };
        }
    }
}
