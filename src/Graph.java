import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Przechowuje strukturę całego grafu.
 */
public class Graph {
    // Mapa pozwala szybko znaleźć współrzędne wierzchołka po jego ID
    private Map<Integer, Node> nodes;
    private List<Edge> edges;

    public Graph() {
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
    }

    /**
     * Metoda, która służy do budowania grafu podczas czytania pliku.
     */
    public void addNode(int id, double x, double y) {
        nodes.put(id, new Node(id, x, y));
    }

    /**
     * Metoda, która służy do dodawania krawędzi.
     */
    public void addEdge(int u, int v, double weight) {
        edges.add(new Edge(u, v, weight));
    }

    /**
     * Metoda, która zwraca wszystkie węzły do narysowania kółek.
     */
    public Map<Integer, Node> getNodes() {
        return nodes;
    }

    /**
     * Metoda, która zwraca wszystkie krawędzie do narysowania linii.
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Czyści graf przed wczytaniem nowego pliku.
     */
    public void clear() {
        nodes.clear();
        edges.clear();
    }
}