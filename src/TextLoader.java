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
                        int id = Integer.parseInt(parts[0]);
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);

                        // Wywołujemy metodę z klasy Graph
                        graph.addNode(id, x, y);
                    } catch (NumberFormatException e) {
                        System.err.println("Błąd formatu liczb w linii: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Nie udało się otworzyć pliku: " + e.getMessage());
        }

        return graph;
    }
}