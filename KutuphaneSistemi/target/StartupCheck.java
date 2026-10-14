import javax.swing.*;
import java.awt.*;
import tr.edu.kutuphane.Main;
public class StartupCheck {
    public static void main(String[] args) throws Exception {
        Main.main(new String[0]);
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = null;
            for (Frame candidate : Frame.getFrames()) {
                if (candidate instanceof tr.edu.kutuphane.ui.MainFrame) frame = (JFrame) candidate;
            }
            if (frame == null || !frame.isShowing()) throw new AssertionError("Window not visible");
            try {
                JMenu modules = frame.getJMenuBar().getMenu(1);
                if (modules.getItemCount() != 6) throw new AssertionError("Missing modules");
                JPanel cards = (JPanel) frame.getContentPane().getComponent(0);
                for (int i = 0; i < modules.getItemCount(); i++) {
                    modules.getItem(i).doClick();
                    for (int j = 0; j < cards.getComponentCount(); j++) {
                        if (cards.getComponent(j).isVisible() != (i == j)) throw new AssertionError("Card transition failed");
                    }
                }
                frame.getJMenuBar().getMenu(0).getItem(0).doClick();
                if (frame.isDisplayable()) throw new AssertionError("Exit failed");
                System.out.println("PASS: Main startup, visible window, six module transitions, exit");
            } finally { frame.dispose(); }
        });
    }
}
