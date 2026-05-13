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
        InputMap im = graphPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = graphPanel.getActionMap();

        am.put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                graphPanel.setZoomFactor(graphPanel.getZoomFactor() * 1.1);
            }
        });

        am.put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                graphPanel.setZoomFactor(graphPanel.getZoomFactor() / 1.1);
            }
        });

        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomIn");
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomIn");
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomOut");
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomOut");
        
        add(graphPanel, BorderLayout.CENTER);

        // 3. Tworzenie panelu bocznego (MODYFIKACJA: DARK MODE I ESTETYKA)
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(220, 0));
        
        // --- MOJE DODATKI: KOLORY PANELU ---
        sidePanel.setBackground(new Color(45, 45, 45)); // Ciemne tło
        sidePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "Opcje widoku", 0, 0, null, Color.WHITE));

        // Przycisk reset układu
        JButton btnReset = new JButton("Przywróć układ");
        styleButton(btnReset, new Color(70, 130, 180)); // Niebieskawy
        btnReset.addActionListener(e -> graphPanel.resetLayout());

        // Przyciski kolorów
        JButton btnNodeColor = new JButton("Kolor punktów");
        styleButton(btnNodeColor, new Color(60, 60, 60));
        btnNodeColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Wybierz kolor punktów", Color.BLUE);
            if (c != null) graphPanel.setNodeColor(c);
        });

        JButton btnEdgeColor = new JButton("Kolor krawędzi");
        styleButton(btnEdgeColor, new Color(60, 60, 60));
        btnEdgeColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Wybierz kolor krawędzi", Color.DARK_GRAY);
            if (c != null) graphPanel.setEdgeColor(c);
        });

        // Suwaki rozmiarów (JSpinner)
        JSpinner spinSize = new JSpinner(new SpinnerNumberModel(16, 5, 50, 2));
        spinSize.addChangeListener(e -> graphPanel.setNodeSize((int) spinSize.getValue()));

        JSpinner spinThick = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        spinThick.addChangeListener(e -> graphPanel.setEdgeThickness((int) spinThick.getValue()));

        // Dodawanie elementów (z poprawionymi kolorami napisów)
        sidePanel.add(createWhiteLabel(" Kolory:"));
        sidePanel.add(btnNodeColor);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(btnEdgeColor);

        sidePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidePanel.add(createWhiteLabel(" Akcje:"));
        sidePanel.add(btnReset); 
        
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidePanel.add(createWhiteLabel(" Rozmiar punktów:"));
        sidePanel.add(spinSize);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(createWhiteLabel(" Grubość krawędzi:"));
        sidePanel.add(spinThick);

        add(sidePanel, BorderLayout.EAST);

        // 4. Pasek menu (bez zmian logiki)
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

    // --- METODY POMOCNICZE DO STYLIZACJI ---
    
    private void styleButton(JButton btn, Color bgColor) {
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    private JLabel createWhiteLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void handleFileOpen(LoaderInterface loader) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setPreferredSize(new Dimension(1200, 800));
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