import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {
    private GraphPanel graphPanel; // Twój panel do rysowania

    public MainFrame() {
        // Podstawowe ustawienia okna
        setTitle("Wizualizacja Grafu - Projekt");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Środek ekranu

        // Tworzymy Twój panel grafu i dodajemy go na środek
        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        // Tworzymy pasek menu do wczytywania plików
        JMenuBar menuBar = new JMenuBar();
        JMenu menuPlik = new JMenu("Plik");
        JMenuItem opcjaOtworz = new JMenuItem("Otwórz plik .txt");

        // Obsługa kliknięcia w menu - tutaj łączymy się z kodem Oli
        opcjaOtworz.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File wybranyPlik = fileChooser.getSelectedFile();
                
                // Używamy loadera napisanego przez Olę
                TextLoader loader = new TextLoader();
                Graph wczytanyGraph = loader.load(wybranyPlik.getAbsolutePath());
                
                // Przekazujemy wczytane dane do Twojego panelu
                if (wczytanyGraph != null) {
                    graphPanel.setGraph(wczytanyGraph);
                }
            }
        });

        menuPlik.add(opcjaOtworz);
        menuBar.add(menuPlik);
        setJMenuBar(menuBar);
    }
}