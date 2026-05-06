import javax.swing.*;
import java.awt.*; // Dodano dla Font i Dimension
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // 1. Wygląd "CROSS-PLATFORM" (Podobny do Linuksa/Java standard)
        try {
            // Ustawiamy styl Metal/Nimbus zamiast systemowego, by mieć większą kontrolę
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Nie udało się ustawić stylu LookAndFeel");
        }

        // 2. Zwiększenie czcionki
        Font bigFont = new Font("SansSerif", Font.BOLD, 24);
        
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            // Nadpisujemy wszystkie zasoby czcionek w systemie
            if (value instanceof javax.swing.plaf.FontUIResource || key.toString().endsWith(".font")) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(bigFont));
            }
        }

        // 3. Tworzymy okno wyboru pliku
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz plik z danymi grafu (z projektu w C)");

        // 4. Ustawienie preferowanego rozmiaru okna
        fileChooser.setPreferredSize(new Dimension(1200, 800));

        // Zwiększamy ikony i przyciski wewnątrz fileChoosera
        fileChooser.updateUI();

        // 5. Pokazujemy okno użytkownikowi
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();

            // uruchamiamy loader
            TextLoader loader = new TextLoader();
            Graph graph = loader.load(path);

            if (graph != null && graph.getNodes() != null) {
                System.out.println("SUKCES! Wczytano " + graph.getNodes().size() + " wezlow.");
            } else {
                System.out.println("Blad: Nie udalo sie przetworzyc pliku.");
            }
        } else {
            System.out.println("Anulowano wybor pliku.");
            System.exit(0);
        }
    }
}