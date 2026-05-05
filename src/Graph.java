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
     * Tworzy nowy wierzchołek i dodaje go do grafu.
     * @param id Unikalny identyfikator z pliku C
     * @param x Współrzędna X obliczona przez algorytm
     * @param y Współrzędna Y obliczona przez algorytm
     */
    public void addNode(int id, double x, double y) {
        Node newNode = new Node(id, x, y);
        nodes.put(id, newNode);
    }
    

    /**
     * Metoda, która służy do dodawania krawędzi.
     */
    public void addEdge(int uId, int vId, double weight) {
        Edge newEdge = new Edge(uId, vId, weight);
        edges.add(newEdge);
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