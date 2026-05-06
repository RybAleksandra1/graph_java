import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Klasa odpowiedzialna za wczytywanie grafu z pliku tekstowego (.txt).
 * Implementuje LoaderInterface.
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
            // Rozmieszczenie wierzchołków na planie koła
            int n = graph.getNodes().size();
            int i = 0;
            double radius = 200; // promień koła

            for (Node node : graph.getNodes().values()) {
                double angle = 2.0 * Math.PI * i / n;
                node.setX(Math.cos(angle) * radius + 300); // 300 to środek ekranu
                node.setY(Math.sin(angle) * radius + 300);
                i++;
            }
        } catch (IOException e) {
            System.err.println("Nie udało się otworzyć pliku: " + e.getMessage());
        }

        return graph;
    }
}