import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // 1. Ustawienie stabilnego wyglądu (Nimbus jest najlepszy do Dark Mode bez bibliotek)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // 2. Globalne ustawienia UI (Twoja logika zaokrągleń)
        UIManager.put("Button.arc", 15);      
        UIManager.put("Component.arc", 15);   
        UIManager.put("ScrollBar.showButtons", true);
        UIManager.put("ScrollBar.width", 12);

        // 3. Zwiększenie czcionki (Twoja logika)
        // Zmniejszyłem lekko do 20, żeby przyciski w JOptionPane nie były ucięte
        Font bigFont = new Font("SansSerif", Font.BOLD, 20); 
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (UIManager.get(key) instanceof javax.swing.plaf.FontUIResource || key.toString().endsWith(".font")) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(bigFont));
            }
        }

        // 4. Wybór typu pliku na starcie
        Object[] options = {"Plik Tekstowy (.txt)", "Plik Binarny (.bin)"};
        int choice = JOptionPane.showOptionDialog(null, 
                "Jaki typ pliku chcesz wczytać na starcie?", 
                "Wybór typu danych",
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, options, options[0]);

        if (choice == JOptionPane.CLOSED_OPTION) System.exit(0);

        // 5. Okno wyboru pliku
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz plik z danymi grafu");
        fileChooser.setPreferredSize(new Dimension(1000, 700));
        fileChooser.updateUI();

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Wybór odpowiedniego loadera
            LoaderInterface loader = (choice == 0) ? new TextLoader() : new BinaryLoader();
            Graph graph = loader.load(selectedFile.getAbsolutePath());

            if (graph != null && !graph.getNodes().isEmpty()) {
                System.out.println("SUKCES! Wczytano " + graph.getNodes().size() + " węzłów.");

                // Otwieramy główne okno
                SwingUtilities.invokeLater(() -> {
                    MainFrame frame = new MainFrame();
                    // Ustawienie grafu przed pokazaniem okna
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