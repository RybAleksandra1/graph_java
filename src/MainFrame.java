import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {
    private GraphPanel graphPanel; 
    private JPanel sidePanel; 

    public MainFrame() {
        setTitle("Wizualizacja Grafu - Projekt Java");
        setSize(1200, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        graphPanel = new GraphPanel();
        
        // --- ZOOM ---
        graphPanel.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) graphPanel.setZoomFactor(graphPanel.getZoomFactor() * 1.1);
            else graphPanel.setZoomFactor(graphPanel.getZoomFactor() / 1.1);
        });

        setupKeyboardShortcuts();
        add(graphPanel, BorderLayout.CENTER);

        // --- PANEL BOCZNY ---
        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(300, 0));
        JButton btnAnimate = new JButton("Animuj impuls");
            btnAnimate.addActionListener(e -> {
                Graph g = graphPanel.getGraph();
                if (g != null && !g.getEdges().isEmpty()) {
                    // Wybieramy pierwszą lepszą krawędź do testu
                    graphPanel.animateEdge(g.getEdges().get(0));
                }
            });
            sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
            sidePanel.add(btnAnimate);
        JCheckBox chkDarkMode = new JCheckBox("Tryb Ciemny", true);
        chkDarkMode.setFocusPainted(false);
        chkDarkMode.addActionListener(e -> applyGlobalTheme(chkDarkMode.isSelected()));

        JButton btnReset = new JButton("Przywróć układ");
        btnReset.addActionListener(e -> graphPanel.resetLayout());

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

        JSlider sliderSize = new JSlider(5, 50, 16);
        sliderSize.addChangeListener(e -> graphPanel.setNodeSize(sliderSize.getValue()));

        JSlider sliderThick = new JSlider(1, 10, 3);
        sliderThick.addChangeListener(e -> graphPanel.setEdgeThickness(sliderThick.getValue()));

        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(chkDarkMode);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidePanel.add(createStyledLabel(" Kolory:"));
        sidePanel.add(btnNodeColor);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(btnEdgeColor);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidePanel.add(createStyledLabel(" Akcje:"));
        sidePanel.add(btnReset); 
        sidePanel.add(Box.createRigidArea(new Dimension(0, 25)));
        sidePanel.add(createStyledLabel(" Rozmiar punktów:"));
        sidePanel.add(sliderSize);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidePanel.add(createStyledLabel(" Grubość krawędzi:"));
        sidePanel.add(sliderThick);

        add(sidePanel, BorderLayout.EAST);

        applyGlobalTheme(true);
        setupMenu();
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void applyGlobalTheme(boolean isDark) {
        Color bgColor = isDark ? new Color(45, 45, 45) : new Color(240, 240, 240);
        Color fgColor = isDark ? Color.WHITE : Color.BLACK;

        graphPanel.setTheme(isDark); 

        sidePanel.setBackground(bgColor);
        sidePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(isDark ? Color.GRAY : Color.LIGHT_GRAY), 
            "Opcje widoku", 0, 0, null, fgColor));

        for (Component c : sidePanel.getComponents()) {
            if (c instanceof JButton) {
                styleButton((JButton) c, isDark ? new Color(65, 65, 65) : Color.LIGHT_GRAY, fgColor);
            } else if (c instanceof JLabel || c instanceof JCheckBox) {
                c.setForeground(fgColor);
                c.setFont(new Font("SansSerif", Font.BOLD, 16));
                if (c instanceof JCheckBox) {
                    ((JCheckBox) c).setOpaque(false);
                    ((JCheckBox) c).setMaximumSize(new Dimension(280, 40));
                }
            } else if (c instanceof JSlider) {
                styleSlider((JSlider) c, isDark);
            }
        }
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setMaximumSize(new Dimension(280, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
    }

    private void styleSlider(JSlider slider, boolean isDark) {
        slider.setMaximumSize(new Dimension(280, 50));
        slider.setOpaque(false);
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);
        slider.setForeground(isDark ? Color.WHITE : Color.BLACK);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(10);
    }

    private void setupKeyboardShortcuts() {
        InputMap im = graphPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = graphPanel.getActionMap();
        am.put("zoomIn", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { graphPanel.setZoomFactor(graphPanel.getZoomFactor() * 1.1); }
        });
        am.put("zoomOut", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { graphPanel.setZoomFactor(graphPanel.getZoomFactor() / 1.1); }
        });
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomIn");
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ADD, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomIn");
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomOut");
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SUBTRACT, java.awt.event.InputEvent.CTRL_DOWN_MASK), "zoomOut");
    }

    private void setupMenu() {
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
        fileChooser.updateUI();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Graph graph = loader.load(fileChooser.getSelectedFile().getAbsolutePath());
            if (graph != null) {
                graphPanel.setGraph(graph);
            }
        }
    }

    public GraphPanel getGraphPanel() { return graphPanel; }
}