/**
 * Klasa reprezentująca pojedynczy wierzchołek grafu.
 */
public class Node {
    private int id;
    private double x;
    private double y;

    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    // Gettery i Settery
    public int getId() { return id; }
    
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
}