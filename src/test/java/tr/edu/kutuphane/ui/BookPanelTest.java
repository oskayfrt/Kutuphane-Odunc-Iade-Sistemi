package tr.edu.kutuphane.ui;

import java.awt.Component;
import java.awt.Container;
import javax.swing.*;
import org.junit.jupiter.api.Test;
import tr.edu.kutuphane.service.BookService;
import static org.junit.jupiter.api.Assertions.*;

class BookPanelTest {
    @Test void formAddsUpdatesFiltersAndRefreshesSharedStock() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            BookService service = new BookService();
            BookPanel panel = new BookPanel(service);
            field(panel, "Kitap kimliği").setText("K1");
            field(panel, "Kitap adı").setText("İnce Memed");
            field(panel, "Yazar").setText("Yaşar Kemal");
            field(panel, "ISBN (10 / 13 hane)").setText("9789750800001");
            find(panel, JSpinner.class).setValue(3);
            button(panel, "Ekle").doClick();
            JTable table = find(panel, JTable.class);
            assertEquals(1, table.getRowCount());
            assertFalse(table.isCellEditable(0, 0));
            table.setRowSelectionInterval(0, 0);
            assertFalse(field(panel, "Kitap kimliği").isEditable());
            field(panel, "Kitap adı").setText("Yeni başlık");
            find(panel, JSpinner.class).setValue(4);
            button(panel, "Güncelle").doClick();
            assertEquals("Yeni başlık", service.findById("K1").title());
            assertEquals(4, table.getValueAt(0, 4));
            JTextField search = findSearch(panel);
            search.setText("olmayan");
            assertEquals(0, table.getRowCount());
            button(panel, "Tümünü göster").doClick();
            assertEquals(1, table.getRowCount());
            service.borrowCopy("K1");
            panel.refresh();
            assertEquals(3, table.getValueAt(0, 5));
            assertEquals(1, table.getValueAt(0, 6));
            assertTrue(field(panel, "Kitap kimliği").isEditable());
        });
    }

    private JTextField field(Container root, String name) {
        for (Component component : root.getComponents()) {
            if (component instanceof JLabel label && label.getText().equals(name)) return (JTextField) label.getLabelFor();
            if (component instanceof Container child) {
                JTextField result = field(child, name);
                if (result != null) return result;
            }
        }
        return null;
    }

    private JButton button(Container root, String text) {
        for (Component component : root.getComponents()) {
            if (component instanceof JButton button && button.getText().equals(text)) return button;
            if (component instanceof Container child) {
                JButton result = button(child, text);
                if (result != null) return result;
            }
        }
        return null;
    }

    private JTextField findSearch(Container root) {
        for (Component component : root.getComponents()) {
            if (component instanceof JPanel panel && panel.getComponentCount() >= 3
                    && panel.getComponent(0) instanceof JLabel label && label.getText().equals("Ara:")) {
                return (JTextField) panel.getComponent(2);
            }
            if (component instanceof Container child) {
                JTextField result = findSearch(child);
                if (result != null) return result;
            }
        }
        return null;
    }

    private <T> T find(Container root, Class<T> type) {
        for (Component component : root.getComponents()) {
            if (type.isInstance(component)) return type.cast(component);
            if (component instanceof Container child) {
                T result = find(child, type);
                if (result != null) return result;
            }
        }
        return null;
    }
}
