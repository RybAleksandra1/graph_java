import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa czytająca dane binarne wygenerowane w C.
 * Implementuje ten sam interfejs co TextLoader.
 */
public class BinaryLoader implements LoaderInterface {

    @Override
    public Graph load(String filePath) {
        Graph graph = new Graph();
        File file = new File(filePath);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] allBytes = fis.readAllBytes();
            // Używamy ByteBuffer, aby wymusić kolejność bajtów Little-Endian (standard dla C)
            ByteBuffer bb = ByteBuffer.wrap(allBytes);
            bb.order(ByteOrder.LITTLE_ENDIAN);

            // W Twoim kodzie C zapisujesz pola po kolei:
            // id (4b), x (8b), y (8b) = 20 bajtów na rekord.
            // NIE ma node_count na początku i NIE ma paddingu.

            int loadedNodes = 0;
            while (bb.remaining() >= 20) {
                int id = bb.getInt();      // 4 bajty
                double x = bb.getDouble(); // 8 bajtów
                double y = bb.getDouble(); // 8 bajtów

                System.out.println("BIN Load -> ID: " + id + " X: " + x + " Y: " + y);
                
                graph.addNode(id, x, y);
                loadedNodes++;
            }
            
            System.out.println("Suma wczytanych węzłów: " + loadedNodes);

            // LOGIKA RYSOWANIA KRAWĘDZI (identyczna jak w TextLoaderze)
            List<Node> nodes = new ArrayList<>(graph.getNodes().values());

            if (nodes.size() > 1) {
                // Obliczamy środek
                double centerX = nodes.stream().mapToDouble(Node::getX).average().orElse(0);
                double centerY = nodes.stream().mapToDouble(Node::getY).average().orElse(0);

                // Sortujemy radialnie (brak przecięć)
                nodes.sort((n1, n2) -> {
                    double angle1 = Math.atan2(n1.getY() - centerY, n1.getX() - centerX);
                    double angle2 = Math.atan2(n2.getY() - centerY, n2.getX() - centerX);
                    return Double.compare(angle1, angle2);
                });

                // Łączymy w pętlę
                for (int i = 0; i < nodes.size(); i++) {
                    graph.addEdge(nodes.get(i).getId(), nodes.get((i + 1) % nodes.size()).getId());
                }
            }

        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu pliku binarnego: " + e.getMessage());
            return null;
        }

        return graph;
    }
}