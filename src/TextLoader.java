import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.List;      

/**
 * Klasa odpowiedzialna za wczytywanie grafu z pliku tekstowego (.txt).
 * Tworzy graf planarny poprzez łączenie wierzchołków według ich kąta względem środka.
 */
public class TextLoader implements LoaderInterface {

    @Override
    public Graph load(String filePath) {
        Graph graph = new Graph();
        List<Integer> idList = new ArrayList<>(); // Lista do przechowywania kolejności ID

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Pomijamy puste linie
                if (line.trim().isEmpty()) continue;

                // 1. Czyszczenie: zamieniamy przecinki na kropki w całej linii
                String cleanedLine = line.replace(',', '.');

                // 2. Dzielenie linii: "\\s+" obsłuży jedną lub wiele spacji/tabulatorów
                String[] parts = cleanedLine.trim().split("\\s+");

                if (parts.length >= 3) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);

                        // 1. Dodajemy wierzchołek do grafu
                        graph.addNode(id, x, y);
                        // 2. Zapamiętujemy ID, żeby wiedzieć jak połączyć krawędzie
                        idList.add(id);
                    
                    } catch (NumberFormatException e) {
                        System.err.println("Błąd formatu w linii: " + line);
                    }
                }
            }
            // 2. LOGIKA RYSOWANIA KRAWĘDZI BEZ PRZECIĘĆ (Graf Planarny)
            List<Node> nodes = new ArrayList<>(graph.getNodes().values());

            if (nodes.size() > 1) {
                // Obliczamy środek grafu (centroid), aby wiedzieć wokół czego sortować
                double centerX = nodes.stream().mapToDouble(Node::getX).average().orElse(0);
                double centerY = nodes.stream().mapToDouble(Node::getY).average().orElse(0);

                // Sortujemy punkty według kąta względem środka (Radial Sort)
                nodes.sort((n1, n2) -> {
                    double angle1 = Math.atan2(n1.getY() - centerY, n1.getX() - centerX);
                    double angle2 = Math.atan2(n2.getY() - centerY, n2.getX() - centerX);
                    return Double.compare(angle1, angle2);
                });

                // Łączymy wierzchołki w pętlę zgodnie z posortowaną kolejnością
                for (int i = 0; i < nodes.size(); i++) {
                    int uId = nodes.get(i).getId();
                    // Operator % sprawia, że ostatni wierzchołek łączy się z pierwszym
                    int vId = nodes.get((i + 1) % nodes.size()).getId(); 
                    
                    graph.addEdge(uId, vId);
                }
            }

        } catch (IOException e) {
            System.err.println("Nie udało się otworzyć pliku: " + e.getMessage());
        }

        return graph;
    }
}