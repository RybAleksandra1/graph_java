import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GraphPanel extends JPanel {
    private Graph graph;
    
    // Ustawienia wyglądu
    private Color nodeColor = Color.BLACK;
    private Color edgeColor = Color.DARK_GRAY;
    private int nodeSize = 16;
    private int edgeThickness = 3;
    private double zoomFactor = 1.0;

    // Przesuwanie wierzchołków
    private Node draggedNode = null; 

    public GraphPanel() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (graph == null) return;

                // Obliczamy skalę tak samo jak w paintComponent
                double[] bounds = calculateGraphBounds();
                double scale = calculateScale(bounds);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                for (Node node : graph.getNodes().values()) {
                    int nx = (int) ((node.getX() - (bounds[0] + bounds[1])/2) * scale) + centerX;
                    int ny = (int) ((node.getY() - (bounds[2] + bounds[3])/2) * scale) + centerY;

                    if (Math.hypot(e.getX() - nx, e.getY() - ny) < nodeSize) {
                        draggedNode = node;
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    double[] bounds = calculateGraphBounds();
                    double scale = calculateScale(bounds);
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;

                    // Przeliczamy pozycję myszy na współrzędne grafu
                    double newX = (e.getX() - centerX) / scale + (bounds[0] + bounds[1]) / 2;
                    double newY = (e.getY() - centerY) / scale + (bounds[2] + bounds[3]) / 2;

                    draggedNode.setX(newX);
                    draggedNode.setY(newY);
                    repaint();
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    // Ta metoda znów nazywa się bezpiecznie
    private double[] calculateGraphBounds() {
        double minX = -200, maxX = 200, minY = -200, maxY = 200; // Domyślne wartości
        if (graph != null && !graph.getNodes().isEmpty()) {
            minX = Double.MAX_VALUE; maxX = Double.MIN_VALUE;
            minY = Double.MAX_VALUE; maxY = Double.MIN_VALUE;
            for (Node n : graph.getNodes().values()) {
                if (n.getX() < minX) minX = n.getX(); if (n.getX() > maxX) maxX = n.getX();
                if (n.getY() < minY) minY = n.getY(); if (n.getY() > maxY) maxY = n.getY();
            }
        }
        return new double[]{minX, maxX, minY, maxY};
    }

    private double calculateScale(double[] b) {
        double rangeX = Math.max(b[1] - b[0], 1);
        double rangeY = Math.max(b[3] - b[2], 1);
        return Math.min((getWidth() - 100) / rangeX, (getHeight() - 100) / rangeY) * zoomFactor;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        repaint();
    }

    // Gettery i Settery dla MainFrame
    public void setNodeColor(Color c) { this.nodeColor = c; repaint(); }
    public void setEdgeColor(Color c) { this.edgeColor = c; repaint(); }
    public void setNodeSize(int s) { this.nodeSize = s; repaint(); }
    public void setEdgeThickness(int t) { this.edgeThickness = t; repaint(); }
    public void setZoomFactor(double z) { this.zoomFactor = Math.max(0.1, z); repaint(); }
    public double getZoomFactor() { return zoomFactor; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null || graph.getNodes().isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double[] b = calculateGraphBounds();
        double scale = calculateScale(b);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double midX = (b[0] + b[1]) / 2;
        double midY = (b[2] + b[3]) / 2;

        // Rysowanie krawędzi
        g2.setStroke(new BasicStroke(edgeThickness));
        g2.setColor(edgeColor);
        for (Edge edge : graph.getEdges()) {
            Node n1 = graph.getNodes().get(edge.getUId());
            Node n2 = graph.getNodes().get(edge.getVId());
            if (n1 != null && n2 != null) {
                int x1 = (int) ((n1.getX() - midX) * scale) + centerX;
                int y1 = (int) ((n1.getY() - midY) * scale) + centerY;
                int x2 = (int) ((n2.getX() - midX) * scale) + centerX;
                int y2 = (int) ((n2.getY() - midY) * scale) + centerY;
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        // Rysowanie wierzchołków
        for (Node node : graph.getNodes().values()) {
            int x = (int) ((node.getX() - midX) * scale) + centerX;
            int y = (int) ((node.getY() - midY) * scale) + centerY;
            g2.setColor(nodeColor);
            g2.fillOval(x - nodeSize/2, y - nodeSize/2, nodeSize, nodeSize);
            g2.setColor(Color.BLUE);
            g2.drawString(String.valueOf(node.getId()), x + nodeSize/2 + 2, y);
        }
    }
}