package visualiser.graphics.objects;

import visualiser.data.elements.Node;
import visualiser.graphics.canvas.Point;
import visualiser.graphics.Graph;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class DrawableNode extends StackPane {
    public static final double NODE_PADDING = 30d,
            BORDER_WIDTH = 2d,
            FONT_SIZE = 30d,
            MIN_SPACE = DrawableEdge.Arrow.HEIGHT * 3;

    protected final Graph graph;
    private final int id;
    private final String name;
    private double xPos = 0, yPos = 0;
    private Color borderColour = Color.BLACK;
    private Color backgroundColour = Color.WHITE;
    private Color textColour = Color.BLACK;
    protected final Circle border;
    protected final Text textID;
    protected final Circle hoverMask;
    protected HoverAction<DrawableNode> hoverAction;
    // todo: might remove default hover action as it is unclear that this is set and when overwritten may cause confusion
    protected static final HoverAction<DrawableNode> defaultHoverAction = (node, isHovered) -> {
        if (!(node instanceof WeightedDrawableNode)) return;
        if (isHovered) node.setColours(Color.DARKRED, Color.rgb(255, 118, 118, 1), Color.DARKRED);
//        if (isHovered) node.setColours(Color.DARKBLUE, Color.rgb(173, 216, 230, 1), Color.DARKBLUE);
        else node.setColours(Color.BLACK, Color.WHITE, Color.BLACK);
    };

    public DrawableNode(Graph graph, Node node) {
        this(graph, node.id(), node.name());
    }

    public DrawableNode(Graph graph, Node node, HoverAction<DrawableNode> hoverAction) {
        this(graph, node.id(), node.name(), hoverAction);
    }

    public DrawableNode(Graph graph, int id, String name) {
        this(graph, id, name, defaultHoverAction);
    }

    public DrawableNode(Graph graph, int id, String name, HoverAction<DrawableNode> hoverAction) {
        this.graph = graph;
        this.id = id;
        this.name = name;
        this.hoverAction = hoverAction;

        // Create the circle used for the border around the node
        border = new Circle();
        border.setFill(backgroundColour);
        border.setStrokeWidth(BORDER_WIDTH);
        border.setStroke(borderColour);

        // Create the text object that displays the ID of the node
        textID = new Text(name);
        textID.setFont(new Font(FONT_SIZE));
        textID.setStroke(textColour);

        // Define the radius of the border circle using the size of the text and NODE_PADDING
        double radius = getBaseRadius();
        border.setRadius(radius);

        graph.updateMaxRadius(this);

        // Create the hover mask
        hoverMask = new Circle(getNodeRadius(), Color.TRANSPARENT);
        hoverMask.hoverProperty().addListener((ignored1, ignored2, isHovered) -> handleHover(isHovered));
        getChildren().addAll(border, textID, hoverMask);
    }

    public void setHoverAction(HoverAction<DrawableNode> hoverAction) {
        this.hoverAction = hoverAction;
    }

    private void handleHover(boolean isHovering) {
        if (hoverAction != null) hoverAction.handle(this, isHovering);
    }

    public void draw() {
        graph.draw(this);
    }

    public void setBorderColour(Color colour) {
        border.setStroke(colour);
        borderColour = colour;
    }

    public Color getBorderColour() {
        return borderColour;
    }

    public void setBackgroundColour(Color colour) {
        border.setFill(colour);
        backgroundColour = colour;
    }

    public Color getBackgroundColour() {
        return backgroundColour;
    }

    public void setTextColour(Color colour) {
        textID.setStroke(colour);
        textColour = colour;
    }

    public Color getTextColour() {
        return textColour;
    }

    public void setColours(Color borderColour, Color backgroundColour, Color textColour) {
        if (borderColour != null) setBorderColour(borderColour);
        if (backgroundColour != null) setBackgroundColour(backgroundColour);
        if (textColour != null) setTextColour(textColour);
    }

    public void moveWithinBoundsTo(Point point) {
        moveWithinBoundsTo(point.getX(), point.getY());
    }

    public void moveWithinBoundsTo(double x, double y) {
        moveTo(x, y);
        double graphHeight = graph.height();
        double graphWidth = graph.width();

        // Find if the node crosses any of the graph boundaries and reposition appropriately
        boolean moved = false;

        // If node crosses the top of the bounds
        if (y <= 0 || getEdgePointTowards(x, 0).getY() <= 0) {
            y = getNodeRadius();
            moved = true;
        }
        // If node crosses the bottom of the bounds
        else if (y >= graphHeight || getEdgePointTowards(x, graphHeight).getY() >= graphHeight) {
            y = graphHeight - getNodeRadius();
            moved = true;
        }
        // If node crosses the left of the bounds
        if (x <= 0 || getEdgePointTowards(0, y).getX() <= 0) {
            x = getNodeRadius();
            moved = true;
        }
        // If node crosses the right of the bounds
        else if (x >= graphWidth || getEdgePointTowards(graphWidth, y).getX() >= graphWidth) {
            x = graphWidth - getNodeRadius();
            moved = true;
        }

        // If the node was found to cross any bounds, move it to the new position within bounds
        if (moved) {
            moveTo(x, y);
        }
    }


    /**
     * Set the position of the node, defines the position of the centre of the node.
     * @see #moveTo(double, double)
     */
    public void moveTo(Point point) {
        moveTo(point.getX(), point.getY());
    }

    /**
     * Set the position of the node, defines the position of the centre of the node.
     * @see #moveTo(Point)
     */
    public void moveTo(double x, double y) {
        double nodeCentre = getNodeRadius();
        setOrigin(x - nodeCentre, y - nodeCentre);
        graph.reconnectEdgesOf(this);
    }

    public Graph getGraph() {
        return graph;
    }

    public boolean isOnSameGraph(DrawableNode node) {
        return graph.equals(node.graph);
    }

    /**
     * Get the ID that the DrawableNode is associated with.
     */
    public int id() {
        return id;
    }
    public String name() {
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
     * @see #setOrigin(double, double)
     */
    private void setOrigin(Point point) {
        setOrigin(point.getX(), point.getY());
    }

    /**
     * Set the position of the node, defines the top left corner co-ordinates.
     * @see #setOrigin(Point)
     */
    protected void setOrigin(double x, double y) {
        xPos = x;
        yPos = y;
        setLayoutX(x);
        setLayoutY(y);
    }

    /**
     * Get the position of the node's top left corner.
     */
    private Point getOrigin() {
        return new Point(xPos, yPos);
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
    public void matchSize(boolean maintainCentre) {
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
    protected void setCircleRadius(double radius, boolean maintainCentre) {
        Point centre = getCentre();
        border.setRadius(radius);
        if (maintainCentre) moveTo(centre);
        graph.reconnectEdgesOf(this);
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

    public DrawableNode createCopy() {
        DrawableNode copy = new DrawableNode(graph, id, name, hoverAction);
        adjustCopyValues(copy);
        return copy;
    }

    private void adjustCopyValues(DrawableNode copy) {
        // todo: add any other options here
        copy.setOrigin(getOrigin());
        copy.setNodeRadius(getNodeRadius(), false);
        copy.setColours(borderColour, backgroundColour, textColour);
    }

    protected WeightedDrawableNode createWeightedCopy(String value) {
        WeightedDrawableNode copy = new WeightedDrawableNode(graph, id, name, value, hoverAction);
        adjustCopyValues(copy);
        return copy;
    }

    @Override
    public String toString() {
        return id + ":" + name;
    }

    /**
     * Returns true if and only if the object is not null, is a {@code DrawableNode} object, is on the same graph as
     * this node, and has the same ID as this node.
     * @param o the object to compare this node against
     * @return true if the nodes have the same ID and are on the same graph
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DrawableNode node)) return false;
        boolean sameID = id == node.id;
        boolean sameGraph = graph.equals(node.graph);
        return sameID && sameGraph;
    }
}
