package tr.edu.kutuphane;

import javax.swing.SwingUtilities;
import tr.edu.kutuphane.ui.MainFrame;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
