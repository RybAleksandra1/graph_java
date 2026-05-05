import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GraphPanel extends JPanel {
    private Graph graph;

    public void setGraph(Graph graph) {
        this.graph = graph;
        repaint(); // Każemy Javie odmalować panel z nowymi danymi
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Ładniejsze krawędzie

        // Proste skalowanie: mnożymy współrzędne przez np. 50, żeby było je widać
        // Docelowo zrobimy tu lepszą matematykę
        int offset = 50; 
        int scale = 100;

        // Rysujemy krawędzie[cite: 5]
        g2.setColor(Color.GRAY);
        for (Edge edge : graph.getEdges()) {
            Node n1 = graph.getNodes().get(edge.getUId());
            Node n2 = graph.getNodes().get(edge.getVId());
            if (n1 != null && n2 != null) {
                g2.drawLine((int)(n1.getX()*scale) + offset, (int)(n1.getY()*scale) + offset,
                            (int)(n2.getX()*scale) + offset, (int)(n2.getY()*scale) + offset);
            }
        }

        // Rysujemy wierzchołki[cite: 5, 8]
        g2.setColor(Color.BLUE);
        for (Node node : graph.getNodes().values()) {
            int x = (int) (node.getX() * scale) + offset;
            int y = (int) (node.getY() * scale) + offset;
            g2.fillOval(x - 5, y - 5, 10, 10); // Kółko o promieniu 5
            g2.drawString(String.valueOf(node.getId()), x + 7, y); // Podpis wierzchołka
        }
    }
}