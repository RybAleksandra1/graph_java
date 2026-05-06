import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {
    private GraphPanel graphPanel; // Panel rysujący graf

    public MainFrame() {
        // 1. Podstawowe ustawienia okna głównego
        setTitle("Wizualizacja Grafu - Projekt Java");
        setSize(1200, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        // 2. Inicjalizacja panelu rysowania
        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        // 3. Tworzenie panelu bocznego
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createTitledBorder("Opcje widoku"));
        sidePanel.setPreferredSize(new Dimension(200, 0));

        JButton btnReset = new JButton("Odśwież widok");
        btnReset.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReset.addActionListener(e -> graphPanel.repaint());

        sidePanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        sidePanel.add(btnReset);
        add(sidePanel, BorderLayout.EAST);

        // 4. Pasek menu z obsługą loaderów tekstowych i binarnych
        JMenuBar menuBar = new JMenuBar();
        JMenu menuPlik = new JMenu("Plik");

        JMenuItem itemOpenTxt = new JMenuItem("Otwórz plik tekstowy (.txt)");
        itemOpenTxt.addActionListener(e -> handleFileOpen(new TextLoader()));

        JMenuItem itemOpenBin = new JMenuItem("Otwórz plik binarny (.bin)");
        itemOpenBin.addActionListener(e -> handleFileOpen(new BinaryLoader()));

        menuPlik.add(itemOpenTxt);
        menuPlik.add(itemOpenBin);
        menuBar.add(menuPlik);
        setJMenuBar(menuBar);
    }

    /**
     * Metoda obsługująca automatyczne wczytywanie danych z pliku.
     */
    private void handleFileOpen(LoaderInterface loader) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setPreferredSize(new Dimension(800, 600));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Loader wczytuje dane. Teraz krawędzie muszą być częścią logiki loadera
            Graph graph = loader.load(selectedFile.getAbsolutePath());

            if (graph != null) {
                // Teraz graphPanel narysuje automatycznie wszystkie krawędzie 
                // zwrócone przez graph.getEdges()
                graphPanel.setGraph(graph); 
                System.out.println("Wczytano graf pomyślnie. Liczba krawędzi: " + graph.getEdges().size());
            } else {
                JOptionPane.showMessageDialog(this, "Błąd podczas wczytywania pliku!");
            }
        }
    }
    // Metoda, która pozwala klasie Main dostać się do panelu grafu
    public GraphPanel getGraphPanel() {
        return this.graphPanel;
    }
}