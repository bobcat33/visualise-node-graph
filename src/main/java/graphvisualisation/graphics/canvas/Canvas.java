package graphvisualisation.graphics.canvas;

import graphvisualisation.graphics.objects.DrawableNode;
import graphvisualisation.graphics.objects.Edge;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.Parent;

import java.util.ArrayList;

public class Canvas extends Parent {

    public static final int WIDTH = 600, HEIGHT = 600;
    private final ArrayList<DrawableNode> nodes;
    private final ArrayList<Edge> edges;

    public Canvas() throws UndefinedNodeException, InvalidEdgeException {
        this(new ArrayList<>());
    }

    // Later make list of ints instead and convert to DrawableNodes. If later Node objects are make then pass them
    // instead and remove int parameters from the code.
    public Canvas(ArrayList<DrawableNode> nodes) throws InvalidEdgeException, UndefinedNodeException {

        this.nodes = nodes;
        edges = new ArrayList<>();

        // Just creating nodes for testing purposes, this will later be automated by converting a matrix of nodes into
        // a map, but that's a headache for later

        DrawableNode node1 = new DrawableNode(1000000);
        DrawableNode node2 = new DrawableNode(10);
        DrawableNode node3 = new DrawableNode(25);
        DrawableNode midPoint = new DrawableNode(30);
        node1.setCentre(150, 150);
        node2.setCentre(300, 450);
        node3.setCentre(450, 150);
        midPoint.setCentre(375, 300);

//        node1.printNodeInfo();
//        node2.printNodeInfo();

        getChildren().addAll(node1, node2, node3, midPoint,
                new Edge(node1, node2, true),
                new Edge(node1, node3, false),
                new Edge(node2, node3, true),
                new Edge(node3, node2, true)
        );

    }

    // It is likely the below methods will be reworked as they rely on the positioning logic to be handled outside the
    // graphics module. I would prefer to keep this within the canvas class if possible.

    /**
     * Find or create an Edge object using the parameters. This also creates the node objects required if they do not
     * already exist. All new nodes are created using {@link #makeNode(int)}. If the edge does not already exist
     * then a new one is created and added to the {@link #edges} array. This method DOES NOT add edges or nodes
     * to the canvas. This must be done using {@link #draw() draw} or {@link #drawEdge(int, int, boolean) drawEdge}.
     * @param node1 the ID of the first node
     * @param node2 the ID of the second node
     * @param directional whether the edge is directional or not
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
        // Otherwise return the new edge
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
    public void createEdge(int node1, int node2) throws InvalidEdgeException, UndefinedNodeException {
        createEdge(node1, node2, false);
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
    public void createEdge(int node1, int node2, boolean directional) throws InvalidEdgeException, UndefinedNodeException {
        makeEdge(node1, node2, directional);
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
    public void drawEdge(int node1, int node2) throws InvalidEdgeException, UndefinedNodeException {
        drawEdge(node1, node2, false);
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
    public void drawEdge(int node1, int node2, boolean directional) throws InvalidEdgeException, UndefinedNodeException {
        createEdge(node1, node2, directional);
        draw();
    }

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
     */
    public void createNode(int nodeID) {
        makeNode(nodeID);
    }

    /**
     * Create or find a node and draw it on the canvas. If the node already exists no node will be created but the canvas will
     * still be redrawn.
     * @param nodeID the ID of the new node
     */
    public void drawNode(int nodeID) {
        createNode(nodeID);
        draw();
    }

    /**
     * Clear all nodes and edges from the canvas and draw the stored nodes and edges.
     * <br/>todo: make this method draw the entire canvas, including objects that are not nodes or edges
     */
    public void draw() {
        System.out.println("Drawing objects to canvas\nNumber of objects on canvas before remove: " + getChildren().size());
        getChildren().removeIf(canvasObject -> canvasObject instanceof DrawableNode || canvasObject instanceof Edge);
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
     * Resize all current nodes.
     * @param matchLargest if true, all nodes will be resized to match the largest node size. If false, all nodes will
     *                     revert to their original sizes.
     * @param maintainCentre if true, all nodes will maintain their centre position. If false, nodes will be resized
     *                       around their top left corners.
     */
    public void resizeNodes(boolean matchLargest, boolean maintainCentre) {
        for (DrawableNode node : nodes) {
            if (matchLargest) node.matchSize(maintainCentre);
            else node.resetSize(maintainCentre);
        }
    }
}
