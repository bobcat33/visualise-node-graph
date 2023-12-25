package graphvisualisation.graphics.objects;

import graphvisualisation.data.graph.elements.Node;
import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.graphing.Graph;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class DrawableNode extends StackPane {
    public static final double NODE_PADDING = 20d,
            BORDER_WIDTH = 2d,
            FONT_SIZE = 30d,
            MIN_SPACE = DrawableEdge.Arrow.HEIGHT * 3;
    private final Graph graph;
    private final int id;
    private final String name;
    private double xPos = 0, yPos = 0;
    protected final Circle border;
    protected final Text textID;

    public DrawableNode(Graph graph, Node node) {
        this(graph, node.id(), node.name());
    }

    public DrawableNode(Graph graph, int id, String name) {
        this.graph = graph;
        this.id = id;
        this.name = name;

        // Create the circle used for the border around the node
        border = new Circle();
        border.setFill(Color.WHITE);
        border.setStrokeWidth(BORDER_WIDTH);
        border.setStroke(Color.BLACK);

        // Create the text object that displays the ID of the node
        textID = new Text(name);
        textID.setFont(new Font(FONT_SIZE));

        // Define the radius of the border circle using the size of the text and NODE_PADDING
        double radius = getBaseRadius();
        border.setRadius(radius);

        getChildren().addAll(border, textID);
    }

    public Point getPos() {
        return new Point(xPos, yPos);
    }

    /**
     * Determines if the node is undefined by doing a basic check of the stored elements and their types.
     * Cannot be fully relied on to determine if the DrawableNode has been correctly defined, can only
     * properly determine if the node is undefined.
     * @return true if the DrawableNode is undefined.
     */
    public boolean isUndefined() {
        return getChildren().size() != 2 || !(getChildren().get(0) instanceof Circle) || !(getChildren().get(1) instanceof Text);
    }

    /**
     * Get the ID that the DrawableNode is associated with.
     */
    public int getNodeID() {
        return id;
    }
    public String getName() {
        return name;
    }

    /**
     * Get the radius of the circle used to display the border. This radius DOES NOT include the border width.
     * @see #getNodeRadius()
     */
    public double getCircleRadius() {
        return border.getRadius();
    }

    /**
     * Get the radius of the node regardless of the greatest node size. Radius is determined using the width of
     * the text in the node and the NODE_PADDING property.
     */
    private double getBaseRadius() {
        return (textID.getLayoutBounds().getWidth() / 2) + NODE_PADDING;
    }

    /**
     * Get the full radius of the node, including the border width.
     * @see #getCircleRadius()
     */
    public double getNodeRadius() {
        return getCircleRadius() + BORDER_WIDTH/2;
    }

    /**
     * Set the position of the node, defines the top left corner co-ordinates.
     * @see #setPosition(double, double)
     */
    public void setPosition(Point point) {
        setPosition(point.getX(), point.getY());
    }

    // todo: might be ideal to remove the position element from the node completely as ultimately the centre is
    //  most useful. There could still be get and set position but instead should be renamed to be more specific.
    //  Centre methods can then be renamed to remove the word centre - thus making the code cleaner
    /**
     * Set the position of the node, defines the top left corner co-ordinates.
     * @see #setPosition(Point)
     */
    public void setPosition(double x, double y) {
        xPos = x;
        yPos = y;
        setLayoutX(x);
        setLayoutY(y);
    }

    /**
     * Get the position of the node's top left corner.
     */
    public Point getPosition() {
        return new Point(xPos, yPos);
    }

    /**
     * Set the position of the node, defines the position of the centre of the node.
     * @see #setCentre(double, double)
     */
    public void setCentre(Point point) {
        setCentre(point.getX(), point.getY());
    }

    /**
     * Set the position of the node, defines the position of the centre of the node.
     * @see #setCentre(Point)
     */
    public void setCentre(double x, double y) {
        double nodeCentre = getNodeRadius();
        setPosition(x - nodeCentre, y - nodeCentre);
    }

    /**
     * Get the position of the centre of the node.
     */
    public Point getCentre() {
        double nodeCentre = getNodeRadius();
        return new Point(xPos + nodeCentre, yPos + nodeCentre);
    }

    /**
     * Resize the node based on the largest current node.
     * @param maintainCentre true if the node should keep the same centre point after resizing
     */
    public void matchSize(Graph graph, boolean maintainCentre) {
        setNodeRadius(graph.maxNodeRadius(), maintainCentre);
    }

    /**
     * Resize the node to its original size using {@link DrawableNode#getBaseRadius()}
     * @see #resetSize(boolean)
     */
    public void resetSize() {
        resetSize(false);
    }

    /**
     * Resize the node to its original size using {@link DrawableNode#getBaseRadius()}
     * @param maintainCentre true if the node should keep the same centre point after resizing
     */
    public void resetSize(boolean maintainCentre) {
        setCircleRadius(getBaseRadius(), maintainCentre);
    }

    /**
     * Resize the node by directly defining the new radius.
     * @param radius the new radius of the inner circle, this DOES NOT include the circle border width
     * @param maintainCentre true if the node should keep the same centre point after resizing
     */
    private void setCircleRadius(double radius, boolean maintainCentre) {
        Point centre = getCentre();
        border.setRadius(radius);
        if (maintainCentre) setCentre(centre);
    }

    private void setNodeRadius(double radius) {
        setNodeRadius(radius, false);
    }

    private void setNodeRadius(double radius, boolean maintainCentre) {
        setCircleRadius(radius - BORDER_WIDTH/2, maintainCentre);
    }

    /**
     * Find the size of the gap between the circular borders of two nodes.
     * @param node1 the first node (in any order)
     * @param node2 the second node (in any order)
     * @return the distance between the nodes
     */
    public static double distanceBetween(DrawableNode node1, DrawableNode node2) {
        return node1.distanceBetween(node2);
    }

    /**
     * Find the size of the gap between the circular borders of this node and the parameter node.
     * @param node the node to get the distance from this one to
     * @return the distance between this node and the parameter node
     */
    public double distanceBetween(DrawableNode node) {
        double distance = getCentre().distanceTo(node.getCentre());
        double r1 = getNodeRadius();
        double r2 = node.getNodeRadius();

        return distance - (r1 + r2);
    }

    public Point getEdgePointTowards(DrawableNode node) {
        return getEdgePointTowards(node.getCentre());
    }

    public Point getEdgePointTowards(Point point) {
        Point centre = getCentre();
        return centre.add(centre.getVectorTo(point).normalize().multiply(getNodeRadius()));
    }

    public Point getEdgePointTowards(double x, double y) {
        return getEdgePointTowards(new Point(x, y));
    }

    /**
     * Determine if this node intersects any other node. It is considered an intersection if any part of either node is
     * touching/contained within the other. This method does not compare equal nodes to each other.
     * @param nodes the nodes to check against this one
     * @return true if the node does intersect another node in the list, false otherwise
     */
    public boolean intersectsAnyOf(ArrayList<DrawableNode> nodes) {
        for (DrawableNode node : nodes) {
            if (!this.equals(node)) {
                if (this.intersects(node)) return true;
            }
        }
        return false;
    }

    /**
     * Determine if this node intersects another. It is considered an intersection if any part of either node is
     * touching/contained within the other. This method will compare equal nodes against each other.
     * @param node the node to check against this one
     * @return true if the nodes intersect, false otherwise
     */
    public boolean intersects(DrawableNode node) {
        // If either circle contains the other, if the circles intersect or if the circles touch then return true
        return distanceBetween(node) <= 0;
    }

    /**
     * Check if this node is valid among a set of nodes, i.e. no part of the node falls outside its canvas bounds, it
     * doesn't overlap or touch any of the nodes, and it is at least the {@link #MIN_SPACE minimum distance} from any
     * other node. This method does not compare equal nodes against each other.
     * @param nodes the nodes to check this one against
     * @return true if the node is valid, false otherwise
     */
    public boolean isValidAmong(ArrayList<DrawableNode> nodes) {
        for (DrawableNode node : nodes) {
            if (!this.equals(node))
                if (distanceBetween(node) <= MIN_SPACE)
                    return false;
        }
        return true;
    }

    /**
     * Create a new edge between this node and the other node.
     * @param node the node to create an edge from this node to
     * @return the {@link DrawableEdge} created
     * @throws UndefinedNodeException if either of the nodes are undefined
     * @throws InvalidEdgeException if the edge would be invalid
     * @see #connectNode(DrawableNode, boolean)
     */
    public DrawableEdge connectNode(DrawableNode node) throws UndefinedNodeException, InvalidEdgeException {
        return new DrawableEdge(this, node);
    }

    /**
     * Create a new directional edge between this node and the other node.
     * @param node the node to create an edge with
     * @param successor whether the node is a successor or a predecessor
     * @return the {@link DrawableEdge} created
     * @throws UndefinedNodeException if either of the nodes are undefined
     * @throws InvalidEdgeException if the edge would be invalid
     * @see #connectNode(DrawableNode)
     */
    public DrawableEdge connectNode(DrawableNode node, boolean successor) throws UndefinedNodeException, InvalidEdgeException {
        if (successor) return new DrawableEdge(this, node, true);
        return new DrawableEdge(node, this, true);
    }

    /**
     * Display data about the node for debugging.
     * @deprecated only to be used for debugging
     */
    public void printNodeInfo() {

        System.out.println("Node: " + id
                + "\nCircle Radius: " + getCircleRadius()
                + "\nNode Radius: " + getNodeRadius()
                + "\nWidth: " + getNodeRadius()*2
                + "\nCentre: ("  + getCentre().getX() + ", " + getCentre().getY() + ")"
                + "\nOrigin: ("  + getPosition().getX() + ", " + getPosition().getY() + ")"
        );
    }

    /**
     * Returns true if and only if the object is not null, a {@code DrawableNode} object, and has the same ID as
     * this node.
     * @param o the object to compare this node against
     * @return true if the nodes have the same ID
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DrawableNode node)) return false;
        boolean sameID = id == node.id;
        boolean sameGraph = graph.equals(node.graph);
        // todo need to decide if samePosition will be required when comparing
        // todo could also store the relevant graph/graphID that the node is drawn to so that no DrawableNode can be used
        //  on a different graph
        boolean samePosition = xPos == node.xPos && yPos == node.yPos;
        return sameID && sameGraph/* && samePosition*/;
    }
}
