import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class GraphPanel extends JPanel {
    private Graph graph;
    
    // --- POLA ANIMACJI KASKADOWEJ ---
    private Timer pathTimer;
    private List<ActivePulse> activePulses = new ArrayList<>();
    private Set<Edge> visitedEdges = new HashSet<>();

    private class ActivePulse {
        Edge edge;
        float pos;
        int targetNodeId;
        Node startNode;
        ActivePulse(Edge e, int target, Node start) {
            this.edge = e; this.pos = 0; this.targetNodeId = target; this.startNode = start;
        }
    }

    // --- KOLORY I PARAMETRY ---
    private Color backgroundColor = new Color(30, 30, 30); 
    private Color nodeColor = new Color(0, 188, 212);     
    private Color edgeColor = new Color(120, 120, 120);   
    private Color textColor = Color.WHITE;                
    private Color highlightColor = new Color(255, 235, 59); 
    
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
        setBackground(backgroundColor);
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (graph == null) return;
                Node previousHover = hoveredNode;
                hoveredNode = null;

                double[] bounds = calculateGraphBounds();
                double scale = calculateScale(bounds);
                int centerX = getWidth() / 2, centerY = getHeight() / 2;
                double midX = (bounds[0] + bounds[1]) / 2, midY = (bounds[2] + bounds[3]) / 2;

                for (Node node : graph.getNodes().values()) {
                    int nx = (int) ((node.getX() - midX) * scale) + centerX + (int)offsetX;
                    int ny = (int) ((node.getY() - midY) * scale) + centerY + (int)offsetY;
                    if (Math.hypot(e.getX() - nx, e.getY() - ny) < nodeSize) {
                        hoveredNode = node;
                        break;
                    }
                }
                if (previousHover != hoveredNode) repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (graph == null) return;
                lastMousePoint = e.getPoint();
                double[] bounds = calculateGraphBounds();
                double scale = calculateScale(bounds);
                int centerX = getWidth() / 2, centerY = getHeight() / 2;
                double midX = (bounds[0] + bounds[1]) / 2, midY = (bounds[2] + bounds[3]) / 2;

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
                draggedNode = null; draggedEdge = null; lastMousePoint = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (graph == null || lastMousePoint == null) return;
                if (draggedNode == null && draggedEdge == null) {
                    offsetX += (e.getX() - lastMousePoint.x);
                    offsetY += (e.getY() - lastMousePoint.y);
                } else {
                    double[] bounds = calculateGraphBounds();
                    double scale = calculateScale(bounds);
                    double dx = (e.getX() - lastMousePoint.x) / scale;
                    double dy = (e.getY() - lastMousePoint.y) / scale;
                    if (draggedNode != null) {
                        draggedNode.setX(draggedNode.getX() + dx);
                        draggedNode.setY(draggedNode.getY() + dy);
                    } else if (draggedEdge != null) {
                        Node n1 = graph.getNodes().get(draggedEdge.getUId());
                        Node n2 = graph.getNodes().get(draggedEdge.getVId());
                        if (n1 != null && n2 != null) {
                            n1.setX(n1.getX() + dx); n1.setY(n1.getY() + dy);
                            n2.setX(n2.getX() + dx); n2.setY(n2.getY() + dy);
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

    public void startCascade(int startNodeId) {
        activePulses.clear();
        visitedEdges.clear();
        triggerPulsesFromNode(startNodeId);
        if (pathTimer != null) pathTimer.stop();
        pathTimer = new Timer(15, e -> {
            List<ActivePulse> finished = new ArrayList<>();
            synchronized(activePulses) {
                for (ActivePulse p : activePulses) {
                    p.pos += 0.03f;
                    if (p.pos >= 1.0f) finished.add(p);
                }
                for (ActivePulse f : finished) {
                    activePulses.remove(f);
                    triggerPulsesFromNode(f.targetNodeId); 
                }
            }
            if (activePulses.isEmpty()) pathTimer.stop();
            repaint();
        });
        pathTimer.start();
    }

    private void triggerPulsesFromNode(int nodeId) {
        Node startNode = graph.getNodes().get(nodeId);
        if (startNode == null) return;
        for (Edge e : graph.getEdges()) {
            if (!visitedEdges.contains(e)) {
                if (e.getUId() == nodeId) {
                    activePulses.add(new ActivePulse(e, e.getVId(), startNode));
                    visitedEdges.add(e);
                } else if (e.getVId() == nodeId) {
                    activePulses.add(new ActivePulse(e, e.getUId(), startNode));
                    visitedEdges.add(e);
                }
            }
        }
    }

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
            highlightColor = new Color(255, 100, 0); 
        }
        setBackground(backgroundColor);
        repaint();
    }

    public Graph getGraph() { return this.graph; }
    public Node getHoveredNode() { return this.hoveredNode; }

    public void resetLayout() {
        if (graph == null || originalPositions.isEmpty()) return;
        for (Node node : graph.getNodes().values()) {
            Point.Double pos = originalPositions.get(node.getId());
            if (pos != null) { node.setX(pos.x); node.setY(pos.y); }
        }
        this.zoomFactor = 1.0; this.offsetX = 0; this.offsetY = 0;
        repaint();
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
        this.offsetX = 0; this.offsetY = 0;
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
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- 1. TŁO GRADIENTOWE ---
        float[] dist = {0.0f, 1.0f};
        Color[] colors = { backgroundColor.brighter(), backgroundColor };
        RadialGradientPaint rgp = new RadialGradientPaint(
            new Point(getWidth() / 2, getHeight() / 2), 
            getWidth(), dist, colors);
        g2.setPaint(rgp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // --- 2. DYNAMICZNA SIATKA (GRID) ---
        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(255, 255, 255, 15)); 
        double gridSize = 50 * zoomFactor;
        if (gridSize < 15) gridSize = 15;
        double startX = (offsetX % gridSize);
        double startY = (offsetY % gridSize);
        for (double x = startX; x < getWidth(); x += gridSize) g2.drawLine((int)x, 0, (int)x, getHeight());
        for (double y = startY; y < getHeight(); y += gridSize) g2.drawLine(0, (int)y, getWidth(), (int)y);

        if (graph == null || graph.getNodes().isEmpty()) return;

        double[] b = calculateGraphBounds();
        double scale = calculateScale(b);
        int centerX = getWidth() / 2, centerY = getHeight() / 2;
        double midX = (b[0] + b[1]) / 2, midY = (b[2] + b[3]) / 2;

        // --- 3. KRAWĘDZIE Z EFEKTEM NEONU ---
        for (Edge edge : graph.getEdges()) {
            Node n1 = graph.getNodes().get(edge.getUId());
            Node n2 = graph.getNodes().get(edge.getVId());
            if (n1 != null && n2 != null) {
                int x1 = (int) ((n1.getX() - midX) * scale) + centerX + (int)offsetX;
                int y1 = (int) ((n1.getY() - midY) * scale) + centerY + (int)offsetY;
                int x2 = (int) ((n2.getX() - midX) * scale) + centerX + (int)offsetX;
                int y2 = (int) ((n2.getY() - midY) * scale) + centerY + (int)offsetY;

                boolean isHovered = (hoveredNode != null && (edge.getUId() == hoveredNode.getId() || edge.getVId() == hoveredNode.getId()));
                Color baseColor = isHovered ? highlightColor : edgeColor;

                // Glow
                g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 40));
                g2.setStroke(new BasicStroke(edgeThickness * 3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x1, y1, x2, y2);
                // Linia
                g2.setColor(baseColor);
                g2.setStroke(new BasicStroke(edgeThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        // --- 4. WIERZCHOŁKI ---
        for (Node node : graph.getNodes().values()) {
            int x = (int) ((node.getX() - midX) * scale) + centerX + (int)offsetX;
            int y = (int) ((node.getY() - midY) * scale) + centerY + (int)offsetY;
            g2.setColor(new Color(nodeColor.getRed(), nodeColor.getGreen(), nodeColor.getBlue(), 60));
            g2.fillOval(x - (nodeSize+8)/2, y - (nodeSize+8)/2, nodeSize+8, nodeSize+8);
            g2.setColor((hoveredNode != null && hoveredNode.getId() == node.getId()) ? highlightColor : nodeColor);
            g2.fillOval(x - nodeSize/2, y - nodeSize/2, nodeSize, nodeSize);
            g2.setColor(textColor);
            g2.drawString(String.valueOf(node.getId()), x + nodeSize/2 + 4, y + 4);
        }

        // --- 5. IMPULSY ---
        synchronized(activePulses) {
            for (ActivePulse p : activePulses) {
                Node nEnd = graph.getNodes().get(p.targetNodeId);
                if (p.startNode != null && nEnd != null) {
                    int x1 = (int) ((p.startNode.getX() - midX) * scale) + centerX + (int)offsetX;
                    int y1 = (int) ((p.startNode.getY() - midY) * scale) + centerY + (int)offsetY;
                    int x2 = (int) ((nEnd.getX() - midX) * scale) + centerX + (int)offsetX;
                    int y2 = (int) ((nEnd.getY() - midY) * scale) + centerY + (int)offsetY;
                    int px = (int) (x1 + (x2 - x1) * p.pos);
                    int py = (int) (y1 + (y2 - y1) * p.pos);
                    g2.setColor(new Color(255, 235, 59, 180));
                    g2.fillOval(px - 12, py - 12, 24, 24);
                    g2.setColor(Color.YELLOW);
                    g2.fillOval(px - 6, py - 6, 12, 12);
                }
            }
        }
    }
}