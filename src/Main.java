import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // 1. Wygląd "CROSS-PLATFORM"
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Nie udało się ustawić stylu LookAndFeel");
        }

        // 2. Zwiększenie czcionki
        Font bigFont = new Font("SansSerif", Font.BOLD, 24);
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (UIManager.get(key) instanceof javax.swing.plaf.FontUIResource || key.toString().endsWith(".font")) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(bigFont));
            }
        }

        // 3. NOWE: Wybór między plikiem tekstowym a binarnym na starcie
        Object[] options = {"Plik Tekstowy (.txt)", "Plik Binarny (.bin)"};
        int choice = JOptionPane.showOptionDialog(null, 
                "Jaki typ pliku chcesz wczytać na starcie?", 
                "Wybór typu danych",
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, options, options[0]);

        // Jeśli użytkownik zamknie okno wyboru typu - kończymy
        if (choice == JOptionPane.CLOSED_OPTION) System.exit(0);

        // 4. Okno wyboru pliku
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz plik z danymi grafu");
        fileChooser.setPreferredSize(new Dimension(1200, 800));
        fileChooser.updateUI();

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();

            // 5. Wybór odpowiedniego loadera na podstawie wcześniejszej decyzji
            LoaderInterface loader;
            if (choice == 0) {
                loader = new TextLoader();
            } else {
                loader = new BinaryLoader();
            }

            Graph graph = loader.load(path);

            if (graph != null && !graph.getNodes().isEmpty()) {
                System.out.println("SUKCES! Wczytano " + graph.getNodes().size() + " wezlow.");

                // Otwieramy okno i przekazujemy graf
                SwingUtilities.invokeLater(() -> {
                    MainFrame frame = new MainFrame();
                    // Musisz upewnić się, że MainFrame ma metodę, żeby przyjąć ten graf!
                    // Jeśli Nella zrobiła setGraph w GraphPanel, to robimy tak:
                    frame.getGraphPanel().setGraph(graph); 
                    frame.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(null, "Błąd: Nie udało się wczytać grafu lub plik jest pusty.");
                System.exit(1);
            }
        } else {
            System.out.println("Anulowano wybór. Zamykanie.");
            System.exit(0);
        }
    }
}