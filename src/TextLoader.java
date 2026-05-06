import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.List;      

/**
 * Klasa odpowiedzialna za wczytywanie grafu z pliku tekstowego (.txt).
 * Rozmieszcza wierzchołki w odległościach proporcjonalnych do wag krawędzi.
 */
public class TextLoader implements LoaderInterface {

    @Override
    public Graph load(String filePath) {
        Graph graph = new Graph();

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
                        int startNode = Integer.parseInt(parts[0]);
                        int endNode = Integer.parseInt(parts[1]);
                        double weight = Double.parseDouble(parts[2]);

                        // 1. Sprawdzamy czy wierzchołki już są w grafie, jeśli nie - dodajemy je
                        // Ponieważ nie mamy współrzędnych w pliku, dajemy im tymczasowo 0,0
                        // (Później program Nelli powinien je rozmieścić)
                        if (!graph.getNodes().containsKey(startNode)) {
                            graph.addNode(startNode, 0, 0); 
                        }
                        if (!graph.getNodes().containsKey(endNode)) {
                            graph.addNode(endNode, 0, 0);
                        }

                        // 2. Dodajemy krawędź między nimi
                        graph.addEdge(startNode, endNode, weight);

                    } catch (NumberFormatException e) {
                        System.err.println("Błąd formatu liczb w linii: " + line);
                    }
                }
            }
            List<Node> nodeList = new ArrayList<>(graph.getNodes().values());
            if (!nodeList.isEmpty()) {
                // 1. Pierwszy wierzchołek ląduje w centrum
                Node first = nodeList.get(0);
                first.setX(400);
                first.setY(400);

                // 2. Każdy kolejny wierzchołek ustawiamy względem poprzedniego
                // w odległości równej wadze krawędzi
                for (int j = 1; j < nodeList.size(); j++) {
                    Node currentNode = nodeList.get(j);
                    Node prevNode = nodeList.get(j - 1);

                    // Szukamy wagi krawędzi między prevNode a currentNode
                    double weight = 2.0; // domyślna waga bazowa
                    for (Edge e : graph.getEdges()) {
                        // Sprawdzamy połączenie w obie strony (u->v lub v->u)
                        if ((e.getUId() == prevNode.getId() && e.getVId() == currentNode.getId()) ||
                            (e.getVId() == prevNode.getId() && e.getUId() == currentNode.getId())) {
                            weight = e.getWeight();
                            break;
                        }
                    }

                    // Skalujemy wagę: waga 1.0 = 50 pikseli. 
                    // Jeśli graf ucieka z ekranu, zmniejsz 50.0 na mniejszą liczbę.
                    double visualDistance = weight * 50.0; 
                    
                    // Rozkładamy je spiralnie, żeby nie tworzyły jednej linii
                    double angle = j * (2 * Math.PI / nodeList.size()); 
                    
                    currentNode.setX(prevNode.getX() + Math.cos(angle) * visualDistance);
                    currentNode.setY(prevNode.getY() + Math.sin(angle) * visualDistance);
                }
            }
        } catch (IOException e) {
            System.err.println("Nie udało się otworzyć pliku: " + e.getMessage());
        }

        return graph;
    }
}