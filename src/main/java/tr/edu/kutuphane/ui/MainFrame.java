package tr.edu.kutuphane.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import tr.edu.kutuphane.service.BookService;
import tr.edu.kutuphane.service.MemberService;
import tr.edu.kutuphane.service.LoanService;

/** Modül panelleri için çalışan ana pencere; bütün paneller aynı servisleri kullanmalı. */
public class MainFrame extends JFrame {
    private final BookService bookService = new BookService();
    private final MemberService memberService = new MemberService();
    private final LoanService loanService = new LoanService(bookService, memberService);
    private final CardLayout layout = new CardLayout();
    private final JPanel content = new JPanel(layout);

    public MainFrame() {
        super("Kütüphane Ödünç ve İade Takip Sistemi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        addModule("Ana Sayfa", "Kütüphane sistemine hoş geldiniz. Modüller menüsünden bir bölüm seçin.");
        content.add(new BookPanel(bookService), "Kitap Yönetimi");
        content.add(new MemberPanel(memberService, loanService), "Üye Yönetimi");
        content.add(new LoanPanel(loanService), "Ödünç / İade");
        addModule("Bekleme Kuyruğu", "Bekleme kuyruğu modülü geliştirilecek.");
        addModule("Kayıt / Yükleme", "Dosya kayıt ve yükleme modülü geliştirilecek.");
        add(content, BorderLayout.CENTER);
        JMenuBar bar = new JMenuBar();
        JMenu modules = new JMenu("Modüller");
        for (String name : new String[] {"Ana Sayfa", "Kitap Yönetimi", "Üye Yönetimi",
                "Ödünç / İade", "Bekleme Kuyruğu", "Kayıt / Yükleme"}) {
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(event -> layout.show(content, name));
            modules.add(item);
        }
        JMenu application = new JMenu("Uygulama");
        JMenuItem exit = new JMenuItem("Çıkış");
        exit.addActionListener(event -> dispose());
        application.add(exit);
        bar.add(application);
        bar.add(modules);
        setJMenuBar(bar);
    }

    private void addModule(String name, String message) {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.add(new JLabel(name, SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(new JLabel(message, SwingConstants.CENTER), BorderLayout.CENTER);
        content.add(panel, name);
        // TODO: İlgili ekip üyesi bu başlangıç panelini kendi modül paneliyle değiştirsin.
    }
}