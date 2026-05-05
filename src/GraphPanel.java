import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel {
    private Graph graph; // Referencja do grafu, który wczyta Ola[cite: 5]

    // Metoda do ustawiania grafu i odświeżania widoku
    public void setGraph(Graph graph) {
        this.graph = graph;
        repaint(); // Wywołuje ponowne malowanie panelu
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null) return; // Jeśli nie ma grafu, nic nie rób

        Graphics2D g2 = (Graphics2D) g;
        // Włączenie wygładzania krawędzi (antyaliasing)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int skala = 100; // Tymczasowa skala, żeby punkty nie były za blisko siebie
        int przesuniecie = 50; // Margines od krawędzi okna

        // Rysowanie krawędzi (linii)[cite: 5]
        g2.setColor(Color.LIGHT_GRAY);
        for (Edge edge : graph.getEdges()) {
            Node n1 = graph.getNodes().get(edge.getUId());
            Node n2 = graph.getNodes().get(edge.getVId());
            if (n1 != null && n2 != null) {
                g2.drawLine(
                    (int)(n1.getX() * skala) + przesuniecie, 
                    (int)(n1.getY() * skala) + przesuniecie,
                    (int)(n2.getX() * skala) + przesuniecie, 
                    (int)(n2.getY() * skala) + przesuniecie
                );
            }
        }

        // Rysowanie wierzchołków (kółek)[cite: 5, 8]
        g2.setColor(Color.BLUE);
        for (Node node : graph.getNodes().values()) {
            int x = (int) (node.getX() * skala) + przesuniecie;
            int y = (int) (node.getY() * skala) + przesuniecie;
            g2.fillOval(x - 6, y - 6, 12, 12); // Rysuje kółko o średnicy 12
            g2.drawString("ID: " + node.getId(), x + 10, y); // Podpis wierzchołka
        }
    }
}