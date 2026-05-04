import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Klasa czytająca dane binarne wygenerowane w C.
 * Implementuje ten sam interfejs co TextLoader.
 */
public class BinaryLoader implements LoaderInterface {

    @Override
    public Graph load(String filePath) {
        Graph graph = new Graph();

        // DataInputStream pozwala czytać konkretne typy: int, double, float
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {

            // Czytamy dopóki są dane w pliku
            while (dis.available() > 0) {
                // Czytamy dane w takiej samej kolejności, w jakiej były zapisane w C
                int id = dis.readInt();
                double x = dis.readDouble();
                double y = dis.readDouble();

                graph.addNode(id, x, y);
            }

        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu pliku binarnego: " + e.getMessage());
        }

        return graph;
    }
}