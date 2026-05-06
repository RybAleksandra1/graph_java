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
        setLocationRelativeTo(null); // Centrowanie na ekranie

        // 2. Inicjalizacja panelu rysowania
        graphPanel = new GraphPanel();
        graphPanel.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                // Scroll w górę - powiększamy
                graphPanel.setZoomFactor(graphPanel.getZoomFactor() * 1.1);
            } else {
                // Scroll w dół - pomniejszamy
                graphPanel.setZoomFactor(graphPanel.getZoomFactor() / 1.1);
            }
        });
        // --- OBSŁUGA ZOOM KLAWIATURĄ (CTRL + "+" / CTRL + "-") ---

            // Pobieramy mapę wejść dla głównego panelu
            InputMap im = graphPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = graphPanel.getActionMap();

            // Definicja akcji przybliżania
            am.put("zoomIn", new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    graphPanel.setZoomFactor(graphPanel.getZoomFactor() * 1.1);
                }
            });

            // Definicja akcji oddalania
        am.put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                graphPanel.setZoomFactor(graphPanel.getZoomFactor() / 1.1);
            }
        });

        // Przypisanie skrótów klawiszowych (obsługuje zwykłe klawisze i numeryczne)
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomIn"); // CTRL + = (czyli + bez shift)
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomIn");    // CTRL + Plus na numerycznej
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomOut"); // CTRL + -
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomOut"); // CTRL + Minus na numerycznej
        add(graphPanel, BorderLayout.CENTER);

        // 3. Tworzenie panelu bocznego (DODANO OPCJE WYGLĄDU)
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createTitledBorder("Opcje widoku"));
        sidePanel.setPreferredSize(new Dimension(220, 0));

        // Przyciski kolorów
        JButton btnNodeColor = new JButton("Kolor punktów");
        btnNodeColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Wybierz kolor punktów", Color.BLUE);
            if (c != null) graphPanel.setNodeColor(c);
        });

        JButton btnEdgeColor = new JButton("Kolor krawędzi");
        btnEdgeColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Wybierz kolor krawędzi", Color.DARK_GRAY);
            if (c != null) graphPanel.setEdgeColor(c);
        });

        // Suwaki rozmiarów (JSpinner)
        JSpinner spinSize = new JSpinner(new SpinnerNumberModel(16, 5, 50, 2));
        spinSize.addChangeListener(e -> graphPanel.setNodeSize((int) spinSize.getValue()));

        JSpinner spinThick = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        spinThick.addChangeListener(e -> graphPanel.setEdgeThickness((int) spinThick.getValue()));

        // Dodawanie do panelu bocznego
        sidePanel.add(new JLabel(" Kolory:"));
        sidePanel.add(btnNodeColor);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(btnEdgeColor);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidePanel.add(new JLabel(" Rozmiar punktów:"));
        sidePanel.add(spinSize);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(new JLabel(" Grubość krawędzi:"));
        sidePanel.add(spinThick);

        add(sidePanel, BorderLayout.EAST);

        // 4. Pasek menu (bez zmian)
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

    private void handleFileOpen(LoaderInterface loader) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setPreferredSize(new Dimension(1200, 800));

        // Odświeżamy UI, aby zastosować duże czcionki z UIManager
        fileChooser.updateUI();

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Graph graph = loader.load(selectedFile.getAbsolutePath());
            if (graph != null) {
                graphPanel.setGraph(graph);
                System.out.println("Wczytano graf pomyślnie.");
            } else {
                JOptionPane.showMessageDialog(this, "Błąd podczas wczytywania pliku!");
            }
        }
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}
