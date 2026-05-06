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
     * Dodaje krawędź między wierzchołkami.
     * Waga jest obliczana automatycznie na podstawie pozycji wierzchołków. 
     * (Waga = Długość krawędzi)
     */
    public void addEdge(int uId, int vId) {
        Node n1 = nodes.get(uId);
        Node n2 = nodes.get(vId);

        if (n1 != null && n2 != null) {
            // Wywołujemy konstruktor Edge(Node n1, Node n2), 
            // który sam policzy dystans (wagę)
            Edge newEdge = new Edge(n1, n2);
            edges.add(newEdge);
        } else {
            System.err.println("Nie można dodać krawędzi: brak wierzchołka o ID " + uId + " lub " + vId);
        }
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