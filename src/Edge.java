/**
 * Klasa reprezentująca krawędź łączącą dwa wierzchołki.
 * Waga jest obliczana na podstawie współrzędnych X i Y.
 */
public class Edge {
    private int uId; // ID wierzchołka początkowego
    private int vId; // ID wierzchołka końcowego
    private double weight;

    public Edge(Node n1, Node n2) {
        this.uId = n1.getId();
        this.vId = n2.getId();
        // Obliczamy wagę jako odległość euklidesową: sqrt((x2-x1)^2 + (y2-y1)^2)
        this.weight = Math.sqrt(Math.pow(n2.getX() - n1.getX(), 2) + 
                                Math.pow(n2.getY() - n1.getY(), 2));
    }

    public int getUId() { return uId; }
    public int getVId() { return vId; }
    public double getWeight() { return weight; }
}