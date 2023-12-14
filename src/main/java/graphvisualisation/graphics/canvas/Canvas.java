package graphvisualisation.graphics.canvas;

import graphvisualisation.data.storage.DataLoader;
import graphvisualisation.data.storage.InvalidFileException;
import graphvisualisation.graphics.logic.PositionLogic;
import graphvisualisation.graphics.objects.DrawableNode;
import graphvisualisation.graphics.objects.Edge;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.Parent;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Canvas extends Parent {

    public static final int WIDTH = 1000, HEIGHT = 600;
    private final ArrayList<DrawableNode> nodes = new ArrayList<>();
    private final ArrayList<Edge> edges = new ArrayList<>();

    public Canvas() throws UndefinedNodeException, InvalidEdgeException, InvalidFileException, FileNotFoundException {
        this(DataLoader.loadFileAsMatrix());
    }

    // todo: later make list of ints instead and convert to DrawableNodes.
    // todo: If this class is still being used later for drawing and Node objects have been made, then instead pass
    //  Node objects into functions and remove int parameters from the code.
    public Canvas(boolean[][] edgeMatrix) throws InvalidEdgeException, UndefinedNodeException {

        PositionLogic.generateRandomCanvas(this, edgeMatrix);

    }

    // Ignore that half this code is just doc comments

    // It is likely the below methods will be reworked as they rely on the positioning logic to be handled outside the
    // graphics module. I would prefer to keep this within the canvas class if possible.

    // todo: currently all edge creation methods allow the creation of nodes if they do not already exist, this does
    //  does not account for the position of the nodes. these methods should either disallow the creation of nodes or
    //  have different methods for giving position of the nodes on either end of the line.

    // todo: split makeEdge into getEdge and makeEdge
    /**
     * Find or create an Edge object using the parameters. This also creates the node objects required if they do not
     * already exist. All new nodes are created using {@link #makeNode(int)}. If the edge does not already exist
     * then a new one is created and added to the {@link #edges} array. This method DOES NOT add edges or nodes
     * to the canvas. This must be done using {@link #draw() draw} or {@link #drawEdge(int, int, boolean) drawEdge}.
     * @param node1 the ID of the first node
     * @param node2 the ID of the second node
     * @param directional whether the edge is directed or not
     * @return the edge found or the edge created
     * @throws InvalidEdgeException if the edge is invalid
     * @throws UndefinedNodeException if either of the nodes are not defined. This can be thrown if the nodes have already
     * been created and stored in {@link #nodes} but have not been properly defined
     */
    private Edge makeEdge(int node1, int node2, boolean directional) throws InvalidEdgeException, UndefinedNodeException {
        // If the edge already exists return it
        Edge newEdge = new Edge(makeNode(node1), makeNode(node2), directional);
        for (Edge edge : edges) {
            if (edge.equals(newEdge)) return edge;
        }

        // Otherwise store and return the new edge
        edges.add(newEdge);
        return newEdge;
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
    public boolean createEdge(int node1, int node2) throws InvalidEdgeException, UndefinedNodeException {
        return createEdge(node1, node2, false);
    }

    /**
     * Create a new directed/undirected edge between two nodes. If the nodes do not already exist they are created. This
     * method DOES NOT add edges or nodes to the canvas. To create and draw the edge simultaneously use
     * {@link #drawEdge(int, int, boolean) drawEdge} instead, or use {@link #draw() draw} later to draw all objects.
     * If the edge already exists then this call is ignored.
     * @param node1 the ID of the first node
     * @param node2 the ID of the second node
     * @param directional whether the new edge is directional or not
     * @throws InvalidEdgeException if the new edge is invalid
     * @throws UndefinedNodeException if either of the nodes are not defined. This can be thrown if the nodes have already
     * been created but have not been properly defined
     * @see #createEdge(int, int)
     */
    public boolean createEdge(int node1, int node2, boolean directional) throws InvalidEdgeException, UndefinedNodeException {
        return !makeEdge(node1, node2, directional).intersectsAnyOf(nodes);
    }

    /**
     * Simultaneously create and draw a new undirected edge on the canvas. Edge created using
     * {@link #createEdge(int, int)} then drawn using {@link #draw()}.
     * <br/>todo: create a private method to draw the specific line instead of using draw() to redraw all elements
     * @param node1 the ID of the first node
     * @param node2 the ID of the second node
     * @throws InvalidEdgeException if the new edge is invalid
     * @throws UndefinedNodeException if either of the nodes are not defined. This can be thrown if the nodes have already
     * been created but have not been properly defined
     * @see #drawEdge(int, int, boolean)
     */
    public boolean drawEdge(int node1, int node2) throws InvalidEdgeException, UndefinedNodeException {
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
    public boolean drawEdge(int node1, int node2, boolean directional) throws InvalidEdgeException, UndefinedNodeException {
        boolean validEdge = createEdge(node1, node2, directional);
        draw(); // todo: rewrite to not require draw call, instead only find the specific edge
        return validEdge;
    }

    // todo: split makeNode into getNode and makeNode
    /**
     * Find or create a Node object using the parameters. Searches {@link #nodes}, then if the node does not exist a
     * new one is created and added. This method DOES NOT add the new node to the canvas. This must be done using
     * {@link #draw() draw} or {@link #drawNode(int) drawNode}.
     * @param nodeID the ID of the node
     * @return the {@code DrawableNode} created/found
     */
    private DrawableNode makeNode(int nodeID) {
        // Check if node already exists
        for (DrawableNode searchNode : nodes) {
            if (searchNode.getNodeID() == nodeID) return searchNode;
        }

        // If not create one
        DrawableNode node = new DrawableNode(nodeID);
        nodes.add(node);
        return node;
    }

    /**
     * Create a new node. If the node already exists this call will be ignored. This method DOES NOT add the new node
     * to the canvas. This must be done using {@link #draw() draw} or {@link #drawNode(int) drawNode}.
     * @param nodeID the ID of the new node
     * @see #createNode(int, double, double)
     * @see #createNodePos(int, double, double)
     */
    public void createNode(int nodeID) {
        makeNode(nodeID);
    }

    /**
     * Create a new node at a certain position. Position is the centre of the new node. If the node already exists it
     * will be moved. This method DOES NOT add the new node to the canvas. This must be done using {@link #draw() draw}
     * or {@link #drawNode(int) drawNode}.
     * @param nodeID the ID of the new node
     * @param x x position of the centre of the new node
     * @param y y position of the centre of the new node
     * @see #createNode(int)
     * @see #createNodePos(int, double, double)
     */
    public boolean createNode(int nodeID, double x, double y) {
        DrawableNode node = makeNode(nodeID);
        node.setCentre(x, y);
        return node.isValidAmong(nodes);
    }

    /**
     * Create a new node at a certain position. Position is the centre of the new node. If the node already exists it
     * will be moved. This method DOES NOT add the new node to the canvas. This must be done using {@link #draw() draw}
     * or {@link #drawNode(int) drawNode}.
     * @param nodeID the ID of the new node
     * @param point the centre point of the new node
     * @see #createNode(int)
     * @see #createNodePos(int, double, double)
     */
    public boolean createNode(int nodeID, Point point) {
        return createNode(nodeID, point.getX(), point.getY());
    }

    /**
     * Create a new node at a certain position. Position is the top left corner of the new node. If the node already
     * exists it will be moved. This method DOES NOT add the new node to the canvas. This must be done using
     * {@link #draw() draw} or {@link #drawNode(int) drawNode}.
     * @param nodeID the ID of the new node
     * @param x x position of the top left corner of the new node
     * @param y y position of the top left corner of the new node
     * @see #createNode(int)
     * @see #createNode(int, double, double)
     */
    public boolean createNodePos(int nodeID, double x, double y) {
        DrawableNode node = makeNode(nodeID);
        node.setPosition(x, y);
        return node.isValidAmong(nodes);
    }

    /**
     * Create a new node at a certain position. Position is the top left corner of the new node. If the node already
     * exists it will be moved. This method DOES NOT add the new node to the canvas. This must be done using
     * {@link #draw() draw} or {@link #drawNode(int) drawNode}.
     * @param nodeID the ID of the new node
     * @param point the top left corner of the new node
     * @see #createNode(int)
     * @see #createNode(int, double, double)
     */
    public boolean createNodePos(int nodeID, Point point) {
        return createNodePos(nodeID, point.getX(), point.getY());
    }

    /**
     * Create or find a node and draw it on the canvas. If the node already exists no node will be created but the
     * canvas will still be redrawn.
     * @param nodeID the ID of the new or existing node
     * @see #drawNode(int, double, double)
     * @see #drawNodePos(int, double, double)
     */
    public void drawNode(int nodeID) {
        createNode(nodeID);
        draw();
    }

    /**
     * Create or find a node and draw it at a certain position on the canvas. Position is the centre of the new node.
     * If the node already exists it will be moved.
     * @param nodeID the ID of the new node
     * @param x the new x position of the centre of the node
     * @param y the new y position of the centre of the node
     * @see #drawNode(int)
     * @see #drawNodePos(int, double, double)
     */
    public boolean drawNode(int nodeID, double x, double y) {
        boolean validPosition = createNode(nodeID, x, y);
        draw();
        return validPosition;
    }

    /**
     * Create or find a node and draw it at a certain position on the canvas. Position is the centre of the new node.
     * If the node already exists it will be moved.
     * @param nodeID the ID of the new node
     * @param point the new centre of the node
     * @see #drawNode(int)
     * @see #drawNodePos(int, double, double)
     */
    public boolean drawNode(int nodeID, Point point) {
        return drawNode(nodeID, point.getX(), point.getY());
    }

    /**
     * Create or find a node and draw it at a certain position on the canvas. Position is the top left corner of the
     * node. If the node already exists it will be moved.
     * @param nodeID the ID of the new node
     * @param x the new x position of the top left corner of the node
     * @param y the new y position of the top left corner of the node
     * @see #drawNode(int)
     * @see #drawNode(int, double, double)
     */
    public boolean drawNodePos(int nodeID, double x, double y) {
        boolean validPosition = createNodePos(nodeID, x, y);
        draw();
        return validPosition;
    }

    /**
     * Create or find a node and draw it at a certain position on the canvas. Position is the top left corner of the
     * node. If the node already exists it will be moved.
     * @param nodeID the ID of the new node
     * @param point the new top left corner of the node
     * @see #drawNode(int)
     * @see #drawNode(int, double, double)
     */
    public boolean drawNodePos(int nodeID, Point point) {
        return drawNodePos(nodeID, point.getX(), point.getY());
    }

    public void clearCanvas() {
        getChildren().removeIf(canvasObject -> canvasObject instanceof DrawableNode || canvasObject instanceof Edge);
    }

    /**
     * Clear all nodes and edges from the canvas and draw the stored nodes and edges.
     * <br/>todo: make this method draw the entire canvas, including objects that are not nodes or edges
     */
    public void draw() {
        System.out.println("Drawing objects to canvas\nNumber of objects on canvas before remove: " + getChildren().size());
        clearCanvas();
        System.out.println("Number of objects on canvas after remove: " + getChildren().size());
        getChildren().addAll(nodes);
        getChildren().addAll(edges);
        System.out.println("Drawing done, number of objects on canvas: " + getChildren().size());
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
    public void resizeNode(int nodeID, boolean matchLargest, boolean maintainCentre) {
        boolean resized = false;
        for (DrawableNode node : nodes) {
            if (node.getNodeID() == nodeID) {
                if (matchLargest) node.matchSize(maintainCentre);
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
            if (matchLargest) node.matchSize(maintainCentre);
            else node.resetSize(maintainCentre);
        }
        reconnectEdges();
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

    public void clear() {
        nodes.clear();
        edges.clear();
        clearCanvas();
    }
}
