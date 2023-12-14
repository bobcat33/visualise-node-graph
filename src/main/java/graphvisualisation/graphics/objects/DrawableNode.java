package graphvisualisation.graphics.objects;

import graphvisualisation.graphics.canvas.Canvas;
import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class DrawableNode extends StackPane {

    // todo: i am aware this is an unsafe way of doing this. i promise i will fix it
    /**Maximum radius among nodes that have been created.*/
    public static double maxRadius;
    public static final double NODE_PADDING = 30d,
            BORDER_WIDTH = 2d,
            FONT_SIZE = 30d,
            MIN_SPACE = Edge.Arrow.HEIGHT * 3;

    private final int id;
    private double xPos = 0, yPos = 0;
    private final Circle border;
    private final Text textID;

    public DrawableNode(int id) {
        this.id = id;

        // Create the circle used for the border around the node
        border = new Circle();
        border.setFill(null);
        border.setStrokeWidth(BORDER_WIDTH);
        border.setStroke(Color.BLACK);

        // Create the text object that displays the ID of the node
        textID = new Text(Integer.toString(id));
        textID.setFont(new Font(FONT_SIZE));

        // Define the radius of the border circle using the size of the text and NODE_PADDING
        double radius = getBaseRadius();
        border.setRadius(radius);

        // Store the radius if it is larger than the largest node. This is used if the implementation requires all nodes
        // to be the same size, using matchSize()
        if (radius > maxRadius) maxRadius = radius;

        draw();
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
    public double getBaseRadius() {
        return (textID.getLayoutBounds().getWidth() / 2) + NODE_PADDING;
    }

    /**
     * Get the full radius of the node, including the border width.
     * @see #getCircleRadius()
     */
    public double getNodeRadius() {
        return getCircleRadius() + BORDER_WIDTH;
    }

    /**
     * Set the position of the node, defines the top left corner co-ordinates.
     * @see #setPosition(double, double)
     */
    public void setPosition(Point point) {
        setPosition(point.getX(), point.getY());
    }

    /**
     * Set the position of the node, defines the top left corner co-ordinates.
     * @see #setPosition(Point)
     */
    public void setPosition(double x, double y) {
        xPos = x;
        yPos = y;
        setLayoutX(x);
        setLayoutY(y);
        draw();
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

    // todo: figure out how to remove this completely
    /**
     * Clear and repopulate the graphic elements stored.
     * @implNote mostly useless
     */
    private void draw() {
        getChildren().clear();
        getChildren().addAll(border, textID);
    }

    /**
     * Resize the node based on the largest current node.
     * @see #matchSize(boolean)
     */
    public void matchSize() {
        matchSize(false);
    }

    /**
     * Resize the node based on the largest current node.
     * @param maintainCentre true if the node should keep the same centre point after resizing
     */
    public void matchSize(boolean maintainCentre) {
        setCircleRadius(maxRadius, maintainCentre);
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
        draw();
    }

    public static double distanceBetween(DrawableNode node1, DrawableNode node2) {
        return node1.distanceBetween(node2);
    }

    public double distanceBetween(DrawableNode node) {
        double distance = getCentre().distanceTo(node.getCentre());
        double r1 = getNodeRadius();
        double r2 = node.getNodeRadius();

        return distance - (r1 + r2);
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

    public boolean isValidAmong(ArrayList<DrawableNode> nodes) {
        double radius = getNodeRadius();
        Point centre = getCentre();
        double cx = centre.getX();
        double cy = centre.getY();

        // If out of canvas bounds
        if (cx - radius < 0 || cy - radius < 0 || cx + radius > Canvas.WIDTH || cy + radius > Canvas.HEIGHT)
            return false;

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
     * @return the {@link Edge} created
     * @throws UndefinedNodeException if either of the nodes are undefined
     * @throws InvalidEdgeException if the edge would be invalid
     * @see #connectNode(DrawableNode, boolean)
     */
    public Edge connectNode(DrawableNode node) throws UndefinedNodeException, InvalidEdgeException {
        return new Edge(this, node);
    }

    /**
     * Create a new directional edge between this node and the other node.
     * @param node the node to create an edge with
     * @param successor whether the node is a successor or a predecessor
     * @return the {@link Edge} created
     * @throws UndefinedNodeException if either of the nodes are undefined
     * @throws InvalidEdgeException if the edge would be invalid
     * @see #connectNode(DrawableNode)
     */
    public Edge connectNode(DrawableNode node, boolean successor) throws UndefinedNodeException, InvalidEdgeException {
        if (successor) return new Edge(this, node, true);
        return new Edge(node, this, true);
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
        // todo need to decide if samePosition will be required when comparing
        boolean samePosition = xPos == node.xPos && yPos == node.yPos;
        return sameID/* && samePosition*/;
    }
}
