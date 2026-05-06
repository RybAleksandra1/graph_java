import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {
    private Graph graph;

    public void setGraph(Graph graph) {
        this.graph = graph;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null || graph.getNodes().isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Szukamy ekstremów, żeby wiedzieć jak duży jest graf
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

        for (Node n : graph.getNodes().values()) {
            if (n.getX() < minX) minX = n.getX();
            if (n.getX() > maxX) maxX = n.getX();
            if (n.getY() < minY) minY = n.getY();
            if (n.getY() > maxY) maxY = n.getY();
        }

        // 2. Obliczamy dostępny rozmiar okna (z marginesem 50px)
        int padding = 50;
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding;

        // 3. Obliczamy skalę, żeby graf zajął całe dostępne miejsce
        double rangeX = maxX - minX;
        double rangeY = maxY - minY;
        
        // Unikamy dzielenia przez zero dla pojedynczego punktu
        double scaleX = (rangeX != 0) ? width / rangeX : 1;
        double scaleY = (rangeY != 0) ? height / rangeY : 1;
        double scale = Math.min(scaleX, scaleY); // Zachowujemy proporcje

        // Ustawienie koloru i grubości krawędzi
            g2.setColor(Color.DARK_GRAY); // Ciemniejszy kolor dla lepszej widoczności
            g2.setStroke(new BasicStroke(3.0f)); // Ustawienie grubości linii na 3 piksele

            // Rysowanie wszystkich krawędzi, które są w obiekcie Graph[cite: 5]
            for (Edge edge : graph.getEdges()) {
                Node n1 = graph.getNodes().get(edge.getUId());
                Node n2 = graph.getNodes().get(edge.getVId());
                
                if (n1 != null && n2 != null) {
                    int x1 = (int) ((n1.getX() - minX) * scale) + padding;
                    int y1 = (int) ((n1.getY() - minY) * scale) + padding;
                    int x2 = (int) ((n2.getX() - minX) * scale) + padding;
                    int y2 = (int) ((n2.getY() - minY) * scale) + padding;
                    
                    // Ustawienie koloru i grubości przed rysowaniem
                    g2.setColor(Color.DARK_GRAY); 
                    g2.setStroke(new BasicStroke(3.0f));
                    // Rysujemy samą krawędź
                    g2.drawLine(x1, y1, x2, y2);

                    // 2. Obliczamy środek linii
                    int midX = (x1 + x2) / 2;
                    int midY = (y1 + y2) / 2;

                    // 3. Opcjonalne: Małe tło pod tekst (żeby waga była czytelna na tle linii)
                    String weightText = String.format("%.2f", edge.getWeight());
                    g2.setFont(new Font("Arial", Font.BOLD, 18));
                    
                    // Pobieramy rozmiar tekstu, żeby idealnie wycentrować prostokąt
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = fm.stringWidth(weightText);
                    int textHeight = fm.getHeight();
                    
                    g2.setColor(new Color(255, 255, 255, 200)); // Półprzezroczysty biały
                    g2.fillRect(midX - textWidth/2 - 4, midY - textHeight/2, textWidth + 8, textHeight);

                    // 4. Rysujemy tekst wagi
                    g2.setColor(Color.RED); // Kolor czerwony dla wyróżnienia wag
                    g2.drawString(weightText, midX - textWidth/2, midY + textHeight/4);
                }
            }
            // Po narysowaniu krawędzi warto zresetować grubość do 1.0 dla innych elementów (np. kółek)
            g2.setStroke(new BasicStroke(1.0f));

        // Rysowanie wierzchołków[cite: 8]
        g2.setColor(Color.BLUE);
        for (Node node : graph.getNodes().values()) {
            int x = (int) ((node.getX() - minX) * scale) + padding;
            int y = (int) ((node.getY() - minY) * scale) + padding;

            g2.setColor(Color.BLACK);
            g2.fillOval(x - 8, y - 8, 16, 16); // Kółka o stałej wielkości

            g2.setColor(Color.BLUE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("ID: " + node.getId(), x + 15, y);
        }
    }
}