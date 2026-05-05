/**
 * Klasa reprezentująca krawędź łączącą dwa wierzchołki.
 */
public class Edge {
    private int uId; // ID wierzchołka początkowego
    private int vId; // ID wierzchołka końcowego
    private double weight;

    public Edge(int uId, int vId, double weight) {
        this.uId = uId;
        this.vId = vId;
        this.weight = weight;
    }

    public int getUId() { return uId; }
    public int getVId() { return vId; }
    public double getWeight() { return weight; }
}