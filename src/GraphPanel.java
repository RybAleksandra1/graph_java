import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

public class GraphPanel extends JPanel {
    private Graph graph;
    
    // --- DYNAMICZNE KOLORY (ZALEŻNE OD TRYBU) ---
    private Color backgroundColor = new Color(30, 30, 30); 
    private Color nodeColor = new Color(0, 188, 212);     
    private Color edgeColor = new Color(120, 120, 120);   
    private Color textColor = Color.WHITE;                
    private Color highlightColor = new Color(255, 235, 59); 
    
    // --- ZMIENNA DO PODŚWIETLANIA ---
    private Node hoveredNode = null; 

    private int nodeSize = 16;
    private int edgeThickness = 3;
    private double zoomFactor = 1.0;

    private Node draggedNode = null; 
    private Edge draggedEdge = null; 
    private Point lastMousePoint = null; 
    private double offsetX = 0; 
    private double offsetY = 0; 

    private Map<Integer, Point.Double> originalPositions = new HashMap<>();

    public GraphPanel() {
        // Ustawienie początkowego tła
        setBackground(backgroundColor);

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (graph == null) return;
                
                Node previousHover = hoveredNode;
                hoveredNode = null;

                double[] bounds = calculateGraphBounds();
                double scale = calculateScale(bounds);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                double midX = (bounds[0] + bounds[1]) / 2;
                double midY = (bounds[2] + bounds[3]) / 2;

                for (Node node : graph.getNodes().values()) {
                    int nx = (int) ((node.getX() - midX) * scale) + centerX + (int)offsetX;
                    int ny = (int) ((node.getY() - midY) * scale) + centerY + (int)offsetY;

                    if (Math.hypot(e.getX() - nx, e.getY() - ny) < nodeSize) {
                        hoveredNode = node;
                        break;
                    }
                }

                if (previousHover != hoveredNode) {
                    repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (graph == null) return;
                lastMousePoint = e.getPoint();

                double[] bounds = calculateGraphBounds();
                double scale = calculateScale(bounds);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                double midX = (bounds[0] + bounds[1]) / 2;
                double midY = (bounds[2] + bounds[3]) / 2;

                draggedNode = null;
                for (Node node : graph.getNodes().values()) {
                    int nx = (int) ((node.getX() - midX) * scale) + centerX + (int)offsetX;
                    int ny = (int) ((node.getY() - midY) * scale) + centerY + (int)offsetY;

                    if (Math.hypot(e.getX() - nx, e.getY() - ny) < nodeSize) {
                        draggedNode = node;
                        return;
                    }
                }

                draggedEdge = null;
                for (Edge edge : graph.getEdges()) {
                    Node n1 = graph.getNodes().get(edge.getUId());
                    Node n2 = graph.getNodes().get(edge.getVId());
                    if (n1 != null && n2 != null) {
                        int x1 = (int) ((n1.getX() - midX) * scale) + centerX + (int)offsetX;
                        int y1 = (int) ((n1.getY() - midY) * scale) + centerY + (int)offsetY;
                        int x2 = (int) ((n2.getX() - midX) * scale) + centerX + (int)offsetX;
                        int y2 = (int) ((n2.getY() - midY) * scale) + centerY + (int)offsetY;

                        if (Line2D.ptSegDist(x1, y1, x2, y2, e.getX(), e.getY()) < 5) {
                            draggedEdge = edge;
                            break;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
                draggedEdge = null;
                lastMousePoint = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (graph == null || lastMousePoint == null) return;

                if (draggedNode == null && draggedEdge == null) {
                    offsetX += (e.getX() - lastMousePoint.x);
                    offsetY += (e.getY() - lastMousePoint.y);
                } 
                else {
                    double[] bounds = calculateGraphBounds();
                    double scale = calculateScale(bounds);

                    double dx = (e.getX() - lastMousePoint.x) / scale;
                    double dy = (e.getY() - lastMousePoint.y) / scale;

                    if (draggedNode != null) {
                        double oldX = draggedNode.getX();
                        double oldY = draggedNode.getY();
                        draggedNode.setX(oldX + dx);
                        draggedNode.setY(oldY + dy);
                        if (!isGraphPlanar()) {
                            draggedNode.setX(oldX);
                            draggedNode.setY(oldY);
                        }
                    } 
                    else if (draggedEdge != null) {
                        Node n1 = graph.getNodes().get(draggedEdge.getUId());
                        Node n2 = graph.getNodes().get(draggedEdge.getVId());
                        if (n1 != null && n2 != null) {
                            double oldX1 = n1.getX(), oldY1 = n1.getY();
                            double oldX2 = n2.getX(), oldY2 = n2.getY();
                            n1.setX(oldX1 + dx); n1.setY(oldY1 + dy);
                            n2.setX(oldX2 + dx); n2.setY(oldY2 + dy);
                            if (!isGraphPlanar()) {
                                n1.setX(oldX1); n1.setY(oldY1);
                                n2.setX(oldX2); n2.setY(oldY2);
                            }
                        }
                    }
                }
                lastMousePoint = e.getPoint();
                repaint();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    // --- NOWA METODA: PRZEŁĄCZANIE MOTYWU ---
    public void setTheme(boolean isDark) {
        if (isDark) {
            backgroundColor = new Color(30, 30, 30);
            nodeColor = new Color(0, 188, 212);
            edgeColor = new Color(120, 120, 120);
            textColor = Color.WHITE;
            highlightColor = new Color(255, 235, 59);
        } else {
            backgroundColor = Color.WHITE;
            nodeColor = Color.BLACK;
            edgeColor = Color.DARK_GRAY;
            textColor = Color.BLACK;
            highlightColor = new Color(255, 100, 0); // Pomarańczowy dla Light Mode
        }
        setBackground(backgroundColor);
        repaint();
    }

    public void resetLayout() {
        if (graph == null || originalPositions.isEmpty()) return;
        for (Node node : graph.getNodes().values()) {
            Point.Double pos = originalPositions.get(node.getId());
            if (pos != null) {
                node.setX(pos.x);
                node.setY(pos.y);
            }
        }
        this.zoomFactor = 1.0;
        this.offsetX = 0;
        this.offsetY = 0;
        repaint();
    }

    private boolean isGraphPlanar() {
        java.util.List<Edge> edges = graph.getEdges();
        int size = edges.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Edge e1 = edges.get(i);
                Edge e2 = edges.get(j);
                Node a = graph.getNodes().get(e1.getUId());
                Node b = graph.getNodes().get(e1.getVId());
                Node c = graph.getNodes().get(e2.getUId());
                Node d = graph.getNodes().get(e2.getVId());
                if (a == null || b == null || c == null || d == null) continue;
                if (intersect(a, b, c, d)) return false;
            }
        }
        return true;
    }

    private boolean intersect(Node a, Node b, Node c, Node d) {
        int o1 = relativeOrientation(a, b, c);
        int o2 = relativeOrientation(a, b, d);
        int o3 = relativeOrientation(c, d, a);
        int o4 = relativeOrientation(c, d, b);
        if (a != c && a != d && b != c && b != d) {
            if (o1 != o2 && o3 != o4) return true;
        }
        if (isPointTooCloseToSegment(c, a, b)) return true;
        if (isPointTooCloseToSegment(d, a, b)) return true;
        if (isPointTooCloseToSegment(a, c, d)) return true;
        if (isPointTooCloseToSegment(b, c, d)) return true;
        return false;
    }

    private boolean isPointTooCloseToSegment(Node p, Node a, Node b) {
        if (isSamePos(p, a) || isSamePos(p, b)) return false;
        double dist = Line2D.ptSegDist(a.getX(), a.getY(), b.getX(), b.getY(), p.getX(), p.getY());
        return dist < 0.1; 
    }

    private boolean isSamePos(Node n1, Node n2) {
        return Math.abs(n1.getX() - n2.getX()) < 1e-9 && 
               Math.abs(n1.getY() - n2.getY()) < 1e-9;
    }

    private int relativeOrientation(Node p, Node q, Node r) {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) -
                     (q.getX() - p.getX()) * (r.getY() - q.getY());
        if (Math.abs(val) < 1e-9) return 0;
        return (val > 0) ? 1 : 2;
    }

    private double[] calculateGraphBounds() {
        double minX = -200, maxX = 200, minY = -200, maxY = 200;
        if (graph != null && !graph.getNodes().isEmpty()) {
            minX = Double.MAX_VALUE; maxX = Double.MIN_VALUE;
            minY = Double.MAX_VALUE; maxY = Double.MIN_VALUE;
            for (Node n : graph.getNodes().values()) {
                if (n.getX() < minX) minX = n.getX(); if (n.getX() > maxX) maxX = n.getX();
                if (n.getY() < minY) minY = n.getY(); if (n.getY() > maxY) maxY = n.getY();
            }
        }
        return new double[]{minX, maxX, minY, maxY};
    }

    private double calculateScale(double[] b) {
        double rangeX = Math.max(b[1] - b[0], 1);
        double rangeY = Math.max(b[3] - b[2], 1);
        return Math.min((getWidth() - 100) / rangeX, (getHeight() - 100) / rangeY) * zoomFactor;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        originalPositions.clear();
        if (graph != null) {
            for (Node node : graph.getNodes().values()) {
                originalPositions.put(node.getId(), new Point.Double(node.getX(), node.getY()));
            }
        }
        this.offsetX = 0;
        this.offsetY = 0;
        repaint();
    }

    public void setNodeColor(Color c) { this.nodeColor = c; repaint(); }
    public void setEdgeColor(Color c) { this.edgeColor = c; repaint(); }
    public void setNodeSize(int s) { this.nodeSize = s; repaint(); }
    public void setEdgeThickness(int t) { this.edgeThickness = t; repaint(); }
    public void setZoomFactor(double z) { this.zoomFactor = Math.max(0.1, z); repaint(); }
    public double getZoomFactor() { return zoomFactor; }

    @Override
    protected void paintComponent(Graphics g) {
        // Tło zależne od zmiennej backgroundColor
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (graph == null || graph.getNodes().isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double[] b = calculateGraphBounds();
        double scale = calculateScale(b);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        double midX = (b[0] + b[1]) / 2;
        double midY = (b[2] + b[3]) / 2;

        // Rysowanie krawędzi
        for (Edge edge : graph.getEdges()) {
            Node n1 = graph.getNodes().get(edge.getUId());
            Node n2 = graph.getNodes().get(edge.getVId());
            if (n1 != null && n2 != null) {
                int x1 = (int) ((n1.getX() - midX) * scale) + centerX + (int)offsetX;
                int y1 = (int) ((n1.getY() - midY) * scale) + centerY + (int)offsetY;
                int x2 = (int) ((n2.getX() - midX) * scale) + centerX + (int)offsetX;
                int y2 = (int) ((n2.getY() - midY) * scale) + centerY + (int)offsetY;

                if (hoveredNode != null && (edge.getUId() == hoveredNode.getId() || edge.getVId() == hoveredNode.getId())) {
                    g2.setColor(highlightColor);
                    g2.setStroke(new BasicStroke(edgeThickness + 2));
                } else {
                    g2.setColor(edgeColor);
                    g2.setStroke(new BasicStroke(edgeThickness));
                }
                
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        // Rysowanie wierzchołków
        for (Node node : graph.getNodes().values()) {
            int x = (int) ((node.getX() - midX) * scale) + centerX + (int)offsetX;
            int y = (int) ((node.getY() - midY) * scale) + centerY + (int)offsetY;
            
            if (hoveredNode != null && hoveredNode.getId() == node.getId()) {
                g2.setColor(highlightColor);
                g2.fillOval(x - (nodeSize + 4)/2, y - (nodeSize + 4)/2, nodeSize + 4, nodeSize + 4);
            } else {
                g2.setColor(nodeColor);
                g2.fillOval(x - nodeSize/2, y - nodeSize/2, nodeSize, nodeSize);
            }

            g2.setColor(textColor);
            g2.drawString(String.valueOf(node.getId()), x + nodeSize/2 + 2, y);
        }
    }
}