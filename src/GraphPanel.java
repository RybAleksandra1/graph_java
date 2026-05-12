import javax.swing.*;
import java.awt.*;


public class GraphPanel extends JPanel {
    private Graph graph;
    
    // Zmienne wyglądu
    private Color nodeColor = Color.BLACK;
    private Color edgeColor = Color.DARK_GRAY;
    private int nodeSize = 16;
    private int edgeThickness = 3;

    // NOWE: Współczynnik przybliżenia
    private double zoomFactor = 1.0;

    public void setGraph(Graph graph) {
        this.graph = graph;
        this.zoomFactor = 1.0; // Reset zoomu przy nowym pliku
        repaint();
    }

    // NOWE: Metody do obsługi zoomu
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = Math.max(0.1, zoomFactor); // Nie pozwalamy na zoom < 10%
        repaint();
    }
    public double getZoomFactor() { return zoomFactor; }

    // Metody do zmiany kolorów (zostają bez zmian)
    public void setNodeColor(Color c) { this.nodeColor = c; repaint(); }
    public void setEdgeColor(Color c) { this.edgeColor = c; repaint(); }
    public void setNodeSize(int s) { this.nodeSize = s; repaint(); }
    public void setEdgeThickness(int t) { this.edgeThickness = t; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null || graph.getNodes().isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Obliczanie bazy skalowania ---
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

        for (Node n : graph.getNodes().values()) {
            if (n.getX() < minX) minX = n.getX();
            if (n.getX() > maxX) maxX = n.getX();
            if (n.getY() < minY) minY = n.getY();
            if (n.getY() > maxY) maxY = n.getY();
        }

        int padding = 50;
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding;

        double rangeX = maxX - minX;
        double rangeY = maxY - minY;
        
        double scaleX = (rangeX > 0.1) ? width / rangeX : 1;
        double scaleY = (rangeY > 0.1) ? height / rangeY : 1;
        
        // NOWE: Mnożymy podstawową skalę przez zoomFactor
        double baseScale = Math.min(scaleX, scaleY);
        double finalScale = baseScale * zoomFactor;

        // Środek panelu (żeby zoom "celował" w środek)
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // RYSOWANIE KRAWĘDZI
        g2.setStroke(new BasicStroke(edgeThickness));
        for (Edge edge : graph.getEdges()) {
            Node n1 = graph.getNodes().get(edge.getUId());
            Node n2 = graph.getNodes().get(edge.getVId());
            
            if (n1 != null && n2 != null) {
                // Obliczamy pozycję względem środka grafu i nakładamy zoom
                int x1 = (int) ((n1.getX() - (minX + maxX)/2) * finalScale) + centerX;
                int y1 = (int) ((n1.getY() - (minY + maxY)/2) * finalScale) + centerY;
                int x2 = (int) ((n2.getX() - (minX + maxX)/2) * finalScale) + centerX;
                int y2 = (int) ((n2.getY() - (minY + maxY)/2) * finalScale) + centerY;
                
                g2.setColor(edgeColor); 
                g2.drawLine(x1, y1, x2, y2);

                String weightText = String.format("%.2f", edge.getWeight());
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.setColor(Color.RED);
                g2.drawString(weightText, (x1 + x2) / 2, (y1 + y2) / 2);
            }
        }

        // RYSOWANIE WIERZCHOŁKÓW
        for (Node node : graph.getNodes().values()) {
            int x = (int) ((node.getX() - (minX + maxX)/2) * finalScale) + centerX;
            int y = (int) ((node.getY() - (minY + maxY)/2) * finalScale) + centerY;

            g2.setColor(nodeColor);
            g2.fillOval(x - nodeSize/2, y - nodeSize/2, nodeSize, nodeSize);

            g2.setColor(Color.BLUE);
            g2.setFont(new Font("Arial", Font.BOLD, 15));
            g2.drawString(String.valueOf(node.getId()), x + nodeSize/2 + 2, y);
        }
    }
}