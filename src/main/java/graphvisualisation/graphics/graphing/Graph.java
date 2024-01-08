package graphvisualisation.graphics.graphing;

import graphvisualisation.data.graph.Matrix;
import graphvisualisation.data.graph.elements.Edge;
import graphvisualisation.data.graph.elements.Node;
import graphvisualisation.data.graph.elements.WeightedEdge;
import graphvisualisation.data.graph.elements.WeightedNode;
import graphvisualisation.data.storage.DataLoader;
import graphvisualisation.data.storage.InvalidFileException;
import graphvisualisation.graphics.canvas.Canvas;
import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.logic.GraphBuilder;
import graphvisualisation.graphics.objects.*;
import graphvisualisation.graphics.objects.exceptions.DuplicateEdgeException;
import graphvisualisation.graphics.objects.exceptions.DuplicateNodeException;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.Parent;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Graph extends Parent {
    private final double width, height;
    private final Canvas canvas;
    private final GraphBuilder builder;
    private final ArrayList<DrawableNode> nodes = new ArrayList<>();
    private final ArrayList<DrawableEdge> edges = new ArrayList<>();

    /**Maximum radius among nodes that have been stored on this canvas. Includes nodes that have not been drawn.*/
    private double maxNodeRadius = 0;

    public Graph(GraphBuilder builder, double width, double height) throws InvalidFileException, FileNotFoundException, InvalidEdgeException, UndefinedNodeException, DuplicateNodeException, DuplicateEdgeException {
        this(builder, width, height, DataLoader.loadMatrix());
    }

    public Graph(GraphBuilder builder, double width, double height, Matrix matrix) throws InvalidEdgeException, UndefinedNodeException, DuplicateNodeException, DuplicateEdgeException {
        this(builder, width, height, matrix.getNodes(), matrix.getEdges());
    }

    public Graph(GraphBuilder builder, double width, double height, ArrayList<Node> nodes, ArrayList<Edge> edges) throws InvalidEdgeException, UndefinedNodeException, DuplicateNodeException, DuplicateEdgeException {
        this.width = width;
        this.height = height;
        this.builder = builder;

        this.canvas = new Canvas();
        getChildren().add(canvas);

        loadDrawableNodes(nodes);
        loadDrawableEdges(edges);

        build();
    }

    private void loadDrawableNodes(ArrayList<Node> nodes) throws DuplicateNodeException {
        // Populate drawable nodes
        for (Node node : nodes) {
            DrawableNode drawableNode;

            if (node instanceof WeightedNode weightedNode) drawableNode = new WeightedDrawableNode(this, weightedNode);
            else drawableNode = new DrawableNode(this, node);

            if (this.nodes.contains(drawableNode)) throw new DuplicateNodeException(node);
            this.nodes.add(drawableNode);
        }
    }

    private void loadDrawableEdges(ArrayList<Edge> edges) throws InvalidEdgeException, UndefinedNodeException, DuplicateEdgeException {
        // Populate drawable edges
        for (Edge edge : edges) {
            DrawableNode startNode = getNode(edge.startNode());
            DrawableNode endNode = getNode(edge.endNode());
            DrawableEdge drawableEdge;

            if (edge instanceof WeightedEdge weightedEdge) drawableEdge = new WeightedDrawableEdge(startNode, endNode, edge.directed(), weightedEdge.value());
            else drawableEdge = new DrawableEdge(startNode, endNode, edge.directed());

            if (this.edges.contains(drawableEdge)) throw new DuplicateEdgeException(edge);
            this.edges.add(drawableEdge);
        }
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public void build() throws InvalidEdgeException, UndefinedNodeException {
        buildWith(builder);
    }

    public void buildWith(GraphBuilder builder) throws InvalidEdgeException, UndefinedNodeException {
        builder.build(this, nodes, edges);
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

    public boolean intersectsAnyNode(DrawableEdge edge) {
        return edge.intersectsAnyOf(nodes);
    }

    public boolean areConnected(DrawableNode node1, DrawableNode node2) {
        for (DrawableEdge edge : edges) {
            if (edge.involves(node1) && edge.involves(node2)) return true;
        }
        return false;
    }

    public void draw(DrawableNode node) {
        canvas.draw(node);
    }

    public void draw(DrawableEdge edge) {
        canvas.draw(edge);
    }

    public void drawAllNodes() {
        canvas.drawNodes(nodes);
    }

    public void drawAllEdges() {
        canvas.drawEdges(edges);
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
            if (searchNode.id() == nodeID) return searchNode;
        }
        return null;
    }

    public DrawableNode getNode(Node node) {
        return getNode(node.id());
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

    /**
     * Resize a specific node. All edges involving this node will be reconnected automatically.
     * @param nodeID the ID of the node
     * @param matchLargest if true, the node will be resized to match the largest node size. If false, the node will
     *                     revert to its original size.
     * @param maintainCentre if true, the node will maintain its centre position. If false, it will be resized around
     *                       its top left corner.
     * @see #resizeNodes(boolean, boolean)
     */
    public void resizeNode(DrawableNode node, boolean matchLargest, boolean maintainCentre) {
        if (matchLargest) node.matchSize(maintainCentre);
        else node.resetSize(maintainCentre);
        reconnectEdgesOf(node);
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
        for (DrawableNode node : nodes) resizeNode(node, matchLargest, maintainCentre);
    }

    public void reconnectEdgesOf(DrawableNode node) {
        for (DrawableEdge edge : edges) if (edge.involves(node)) edge.reconnect();
    }

    /**
     * Reconnect all edges to their nodes.
     */
    private void reconnectEdges() {
        for (DrawableEdge edge : edges) edge.reconnect();
    }

    /**
     * Clear all visible nodes from the canvas.
     */
    public void clear() {
        canvas.clear();
    }

    /**
     * @deprecated used for testing
     */
    public void drawDot(double x, double y) {
        drawDot(new Dot(x, y));
    }

    /**
     * @deprecated used for testing
     */
    public void drawDot(Point point) {
        drawDot(point.getX(), point.getY());
    }

    /**
     * @deprecated used for testing
     */
    public void drawDot(Dot dot) {
        canvas.draw(dot);
    }
}