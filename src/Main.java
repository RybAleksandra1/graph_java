import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // 1. zachowuje: Wygląd "CROSS-PLATFORM" od Oli
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Nie udało się ustawić stylu LookAndFeel");
        }

        // 2. zachowuje: Powiększanie czcionek
        Font bigFont = new Font("SansSerif", Font.BOLD, 24);
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource || key.toString().endsWith(".font")) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(bigFont));
            }
        }

        // 3. zmieniam: Zamiast JFileChooser, otwieramy Twoje okno główne
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(); // Twoje okno
            frame.setVisible(true);
        });
    }
}