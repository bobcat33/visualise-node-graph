package graphvisualisation.graphics.nodes;

import graphvisualisation.graphics.canvas.Point;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DrawableNode extends StackPane {

    public static double maxRadius;
    public static final double NODE_PADDING = 30d,
                                BORDER_WIDTH = 2d,
                                FONT_SIZE = 30d;

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
     * @see DrawableNode#getNodeWidth()
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
     * Get the full width of the node. Used to obtain a more accurate width than {@link DrawableNode#getCircleRadius()}
     */
    public double getNodeWidth() {
        return getBoundsInParent().getWidth();
    }

    /**
     * Set the position of the node, defines the top left corner co-ordinates.
     */
    public void setPosition(Point point) {
        setPosition(point.getX(), point.getY());
    }

    /**
     * @see DrawableNode#setPosition(Point)
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
     */
    public void setCentre(Point point) {
        setCentre(point.getX(), point.getY());
    }

    /**
     * @see DrawableNode#setCentre(Point)
     */
    public void setCentre(double x, double y) {
        double nodeCentre = getNodeWidth()/2;
        xPos = x - nodeCentre;
        yPos = y - nodeCentre;
        setLayoutX(xPos);
        setLayoutY(yPos);
        draw();
    }

    /**
     * Get the position of the centre of the node.
     */
    public Point getCentre() {
        double nodeCentre = getNodeWidth()/2;
        return new Point(xPos + nodeCentre, yPos + nodeCentre);
    }

    /**
     * Clear and repopulate the graphic elements stored.
     */
    private void draw() {
        getChildren().clear();
        getChildren().addAll(border, textID);
    }

    /**
     * Resize the node based on the largest current node.
     * @see DrawableNode#matchSize(boolean)
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
     * @see DrawableNode#resetSize(boolean)
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

    /**
     * Create a new edge between this node and the other node.
     * @param node the node to create an edge from this node to
     * @return the {@link Edge} created
     * @throws UndefinedNodeException if either of the nodes are undefined
     * @throws InvalidEdgeException if the edge would be invalid
     * @see DrawableNode#connectNode(DrawableNode, boolean)
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
     * @see DrawableNode#connectNode(DrawableNode)
     */
    public Edge connectNode(DrawableNode node, boolean successor) throws UndefinedNodeException, InvalidEdgeException {
        if (successor) return new Edge(this, node, true);
        return new Edge(node, this, true);
    }
}
