package graphvisualisation.graphics.graphing;

import graphvisualisation.data.graph.Matrix;
import graphvisualisation.data.storage.InvalidFileException;
import graphvisualisation.graphics.canvas.Canvas;
import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.logic.GraphBuilder;
import graphvisualisation.graphics.objects.Dot;
import graphvisualisation.graphics.objects.DrawableNode;
import graphvisualisation.graphics.objects.Edge;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.Parent;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Graph extends Parent {
    private final boolean uniformNodeSize = true;
    private final double width, height;
    private final Matrix matrix;
    private final Canvas canvas;
    private GraphBuilder builder;
    private final ArrayList<DrawableNode> nodes = new ArrayList<>();
    private final ArrayList<Edge> edges = new ArrayList<>();

    /**Maximum radius among nodes that have been stored on this canvas. Includes nodes that have not been drawn.*/
    private double maxNodeRadius = 0;

    // todo: nodeID parameters should later be replaced by Node objects so that DrawableNodes can be created with Nodes
    //  that may contain values, nodes should be created with a unique "name" that the user sees, then backend IDs
    public Graph(GraphBuilder builder, double width, double height) throws InvalidFileException, FileNotFoundException, InvalidEdgeException, UndefinedNodeException {
        this(builder, width, height, new Matrix());
    }

    public Graph(GraphBuilder builder, double width, double height, Matrix matrix) throws InvalidEdgeException, UndefinedNodeException {
        this.width = width;
        this.height = height;
        this.matrix = matrix;
        this.canvas = new Canvas();
        getChildren().add(canvas);
        builder.build(this, matrix, uniformNodeSize);
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public void rebuild() throws InvalidEdgeException, UndefinedNodeException {
        builder.build(this, matrix, uniformNodeSize);
    }

    public void rebuild(GraphBuilder builder) throws InvalidEdgeException, UndefinedNodeException {
        this.builder = builder;
        rebuild();
    }

    public Point generatePoint() {
        return Point.generateRandom(0, 0, width, height);
    }

    public double maxNodeRadius() {
        return maxNodeRadius;
    }

    /**
     * Update the value stored for the maximum radius of nodes. The stored value is only updated if the new value is
     * greater than it.
     * @param radius the radius to be compared to the stored radius
     */
    public void updateMaxRadius(DrawableNode node) {
        double radius = node.getNodeRadius();
        if (radius > maxNodeRadius) maxNodeRadius = radius;
    }

    public boolean intersectsAnyNode(Edge edge) {
        return edge.intersectsAnyOf(nodes);
    }

    public boolean areConnected(DrawableNode node1, DrawableNode node2) {
        for (Edge edge : edges) {
            if (edge.involves(node1.getNodeID()) && edge.involves(node2.getNodeID())) return true;
        }
        return false;
    }

    public Edge getEdge(int node1, int node2, boolean directed) {
        Edge newEdge;

        DrawableNode n1 = getNode(node1);
        DrawableNode n2 = getNode(node2);
        if (n1 == null || n2 == null) return null;

        try {
            newEdge = new Edge(n1, n2, directed);
        } catch (InvalidEdgeException | UndefinedNodeException e) {
            return null;
        }

        for (Edge edge : edges) {
            if (edge.equals(newEdge)) return edge;
        }

        return null;
    }

    /**
     * Create a new undirected edge between two nodes. If the nodes do not already exist they are created. This
     * method DOES NOT add edges or nodes to the canvas. To create and draw the edge simultaneously use
     * {@link #drawEdge(int, int, boolean) drawEdge} instead, or use {@link #draw() draw} later to draw all objects.
     * If the edge already exists then this call is ignored.
     * @param node1 the ID of the first node
     * @param node2 the ID of the second node
     * @throws InvalidEdgeException if the new edge is invalid
     * @throws UndefinedNodeException if either of the nodes are not defined. This can be thrown if the nodes have already
     * been created but have not been properly defined
     * @see #createEdge(int, int, boolean)
     */
    public Edge createEdge(int node1, int node2) throws InvalidEdgeException, UndefinedNodeException {
        return createEdge(node1, node2, false);
    }

    /**
     * Create a new directed/undirected edge between two nodes. If the nodes do not already exist they are created. This
     * method DOES NOT add edges or nodes to the canvas. To create and draw the edge simultaneously use
     * {@link #drawEdge(int, int, boolean) drawEdge} instead, or use {@link #draw() draw} later to draw all objects.
     * If the edge already exists then this call is ignored.
     * @param node1 the ID of the first node
     * @param node2 the ID of the second node
     * @param directed whether the new edge is directed or not
     * @throws InvalidEdgeException if the new edge is invalid
     * @throws UndefinedNodeException if either of the nodes are not defined. This can be thrown if the nodes have already
     * been created but have not been properly defined
     * @see #createEdge(int, int)
     */
    public Edge createEdge(int node1, int node2, boolean directed) throws InvalidEdgeException, UndefinedNodeException {
        // If the edge already exists return it
        Edge edge = getEdge(node1, node2, directed);
        if (edge != null) return edge;

        // Otherwise create a new edge, then store it and return it
        edge = new Edge(getNode(node1), getNode(node2), directed);
//        edge.setColour(Color.RED);
        edges.add(edge);
        return edge;
    }

    /**
     * Simultaneously create and draw a new undirected edge on the canvas. Edge created using
     * {@link #createEdge(int, int)} then drawn using {@link #draw()}.
     * @param node1 the ID of the first node
     * @param node2 the ID of the second node
     * @throws InvalidEdgeException if the new edge is invalid
     * @throws UndefinedNodeException if either of the nodes are not defined. This can be thrown if the nodes have already
     * been created but have not been properly defined
     * @see #drawEdge(int, int, boolean)
     */
    public Edge drawEdge(int node1, int node2) throws InvalidEdgeException, UndefinedNodeException {
        return drawEdge(node1, node2, false);
    }

    /**
     * Simultaneously create and draw a new directed/undirected edge on the canvas. Edge created using
     * {@link #createEdge(int, int, boolean)} then drawn using {@link #draw()}.
     * @param node1 the ID of the first node
     * @param node2 the ID of the second node
     * @param directional whether the new edge is directional or not
     * @throws InvalidEdgeException if the new edge is invalid
     * @throws UndefinedNodeException if either of the nodes are not defined. This can be thrown if the nodes have already
     * been created but have not been properly defined
     * @see #drawEdge(int, int)
     */
    public Edge drawEdge(int node1, int node2, boolean directional) throws InvalidEdgeException, UndefinedNodeException {
        Edge edge = createEdge(node1, node2, directional);
        canvas.draw(edge);
        return edge;
    }


    public boolean isValidNode(int nodeID) {
        return isValidNode(getNode(nodeID));
    }

    public boolean isValidNode(DrawableNode node) {
        return isWithinBounds(node) && node.isValidAmong(nodes);
    }

    public boolean isWithinBounds(DrawableNode node) {
        double radius = node.getNodeRadius();
        Point centre = node.getCentre();
        double cx = centre.getX();
        double cy = centre.getY();

        return cx - radius >= 0 && cy - radius >= 0 && cx + radius <= width && cy + radius <= height;
    }

    public DrawableNode getNode(int nodeID) {
        for (DrawableNode searchNode : nodes) {
            if (searchNode.getNodeID() == nodeID) return searchNode;
        }
        return null;
    }

    /**
     * Find or create a Node object. Searches {@link #nodes}, then if the node does not exist a new one is created and
     * added. This method DOES NOT add the new node to the canvas. This must be done using {@link #draw() draw} or
     * {@link #drawNode(int) drawNode}.
     * @param nodeID the ID of the node
     * @return the {@code DrawableNode} created/found
     */
    public DrawableNode createNode(int nodeID) {
        // Check if node already exists
        DrawableNode node = getNode(nodeID);
        if (node != null) return node;

        // If not create one
        node = new DrawableNode(nodeID);
        nodes.add(node);

        // Store the radius if it is larger than the largest node. This is used if the implementation requires all nodes
        // to be the same size, using Node#matchSize
        updateMaxRadius(node);
        return node;
    }

    /**
     * Create a new node at a certain position. Position is the centre of the new node. If the node already exists it
     * will be moved. This method DOES NOT add the new node to the canvas. This must be done using {@link #draw() draw}
     * or {@link #drawNode(int) drawNode}.
     * @param nodeID the ID of the new node
     * @param x x position of the centre of the new node
     * @param y y position of the centre of the new node
     * @see #createNode(int)
     */
    public DrawableNode createNode(int nodeID, double x, double y) {
        DrawableNode node = createNode(nodeID);
        node.setCentre(x, y);
        return node;
    }

    /**
     * Create a new node at a certain position. Position is the centre of the new node. If the node already exists it
     * will be moved. This method DOES NOT add the new node to the canvas. This must be done using {@link #draw() draw}
     * or {@link #drawNode(int) drawNode}.
     * @param nodeID the ID of the new node
     * @param point the centre point of the new node
     * @see #createNode(int)
     */
    public DrawableNode createNode(int nodeID, Point point) {
        return createNode(nodeID, point.getX(), point.getY());
    }

    /**
     * Create or find a node and draw it on the canvas. If the node already exists no node will be created but the
     * canvas will still be redrawn.
     * @param nodeID the ID of the new or existing node
     * @see #drawNode(int, double, double)
     */
    public DrawableNode drawNode(int nodeID) {
        DrawableNode node = createNode(nodeID);
        canvas.draw(node);
        return node;
    }

    /**
     * Create or find a node and draw it at a certain position on the canvas. Position is the centre of the new node.
     * If the node already exists it will be moved.
     * @param nodeID the ID of the new node
     * @param x the new x position of the centre of the node
     * @param y the new y position of the centre of the node
     * @see #drawNode(int)
     */
    public DrawableNode drawNode(int nodeID, double x, double y) {
        DrawableNode node = createNode(nodeID, x, y);
        canvas.draw(node);
        return node;
    }

    /**
     * Create or find a node and draw it at a certain position on the canvas. Position is the centre of the new node.
     * If the node already exists it will be moved.
     * @param nodeID the ID of the new node
     * @param point the new centre of the node
     * @see #drawNode(int)
     */
    public DrawableNode drawNode(int nodeID, Point point) {
        return drawNode(nodeID, point.getX(), point.getY());
    }

    /**
     * Clear all nodes and edges from the canvas and draw the stored nodes and edges. Any other elements on the canvas
     * will remain.
     */
    public void draw() {
        canvas.clear();
        canvas.draw(nodes, edges);
    }

    /**
     * Clear all nodes and edges from the canvas and draw the stored nodes and edges. Then resize all nodes to match
     * the largest node's size. All nodes will maintain their centre position when resizing.
     * @see #draw()
     * @see #resizeNodes(boolean, boolean)
     */
    public void drawAndScale() {
        draw();
        resizeNodes(true, true);
    }

    public void moveNode(int nodeID, Point point, boolean withinBounds) throws UndefinedNodeException {
        moveNode(nodeID, point.getX(), point.getY(), withinBounds);
    }

    public void moveNode(int nodeID, double x, double y, boolean withinBounds) throws UndefinedNodeException {
        DrawableNode node = getNode(nodeID);

        if (node == null) throw new UndefinedNodeException(null);

        moveNode(node, x, y, withinBounds);
    }

    public void moveNode(DrawableNode node, Point point, boolean withinBounds) {
        moveNode(node, point.getX(), point.getY(), withinBounds);
    }

    public void moveNode(DrawableNode node, double x, double y, boolean withinBounds) {
        node.setCentre(x, y);

        // If the node must be kept within the bounds of the graph then find if it crosses any of the edges and
        // reposition appropriately
        if (withinBounds) {
            boolean moved = false;

            // If node crosses the top of the bounds
            if (y <= 0 || node.getEdgePointTowards(x, 0).getY() <= 0) {
                y = node.getNodeRadius();
                moved = true;
            }
            // If node crosses the bottom of the bounds
            else if (y >= height || node.getEdgePointTowards(x, height).getY() >= height) {
                y = height - node.getNodeRadius();
                moved = true;
            }
            // If node crosses the left of the bounds
            if (x <= 0 || node.getEdgePointTowards(0, y).getX() <= 0) {
                x = node.getNodeRadius();
                moved = true;
            }
            // If node crosses the right of the bounds
            else if (x >= width || node.getEdgePointTowards(width, y).getX() >= width) {
                x = width - node.getNodeRadius();
                moved = true;
            }

            // If the node was found to cross any bounds, move it to the new position within bounds
            if (moved) {
                node.setCentre(x, y);
            }
        }

        reconnectEdgesOf(node);
    }

    /**
     * Resize a specific node. All edges involving this node will be reconnected automatically.
     * @param nodeID the ID of the node
     * @param matchLargest if true, the node will be resized to match the largest node size. If false, the node will
     *                     revert to its original size.
     * @param maintainCentre if true, the node will maintain its centre position. If false, it will be resized around
     *                       its top left corner.
     * @see #resizeNodes(boolean, boolean)
     */
    public void resizeNode(int nodeID, boolean matchLargest, boolean maintainCentre) {
        boolean resized = false;
        for (DrawableNode node : nodes) {
            if (node.getNodeID() == nodeID) {
                if (matchLargest) node.matchSize(this, maintainCentre);
                else node.resetSize(maintainCentre);
                resized = true;
            }
        }
        if (resized) reconnectEdgesOf(nodeID);
    }

    /**
     * Resize all current nodes. All edges will be reconnected automatically.
     * @param matchLargest if true, all nodes will be resized to match the largest node size. If false, all nodes will
     *                     revert to their original sizes.
     * @param maintainCentre if true, all nodes will maintain their centre position. If false, nodes will be resized
     *                       around their top left corners.
     * @see #resizeNode(int, boolean, boolean)
     */
    public void resizeNodes(boolean matchLargest, boolean maintainCentre) {
        for (DrawableNode node : nodes) {
            if (matchLargest) node.matchSize(this, maintainCentre);
            else node.resetSize(maintainCentre);
        }
        reconnectEdges();
    }

    private void reconnectEdgesOf(DrawableNode node) {
        reconnectEdgesOf(node.getNodeID());
    }

    /**
     * Reconnect all edges where an end is connected to the specified node.
     * @param nodeID the ID of the node
     */
    private void reconnectEdgesOf(int nodeID) {
        for (Edge edge : edges) if (edge.involves(nodeID)) edge.reconnect();
    }

    /**
     * Reconnect all edges to their nodes.
     */
    private void reconnectEdges() {
        for (Edge edge : edges) edge.reconnect();
    }

    /**
     * Clear all stored objects (nodes and edges) and all visible nodes from the canvas.
     */
    public void clear() {
        maxNodeRadius = 0;
        nodes.clear();
        edges.clear();
        canvas.clear();
    }

    /**
     * @deprecated used for testing
     */
    public void drawDot(double x, double y) {
        canvas.draw(new Dot(x, y));
    }

    /**
     * @deprecated used for testing
     */
    public void drawDot(Point point) {
        drawDot(point.getX(), point.getY());
    }
}