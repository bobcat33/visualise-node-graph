package graphvisualisation.graphics.objects;

import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.Graph;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayList;

public class DrawableEdge extends Parent {
    public static final Color LINE_COLOUR = Color.BLACK;
    public static final double
            LINE_SIZE = 2d,
            HOVER_MASK_WIDTH = Arrow.WIDTH;

    protected final DrawableNode startNode;
    protected final DrawableNode endNode;
    protected final boolean directed;
    protected final Graph graph;
    protected final Polygon hoverMask = new Polygon();
    protected HoverAction<DrawableEdge> hoverAction;
    // todo: might remove default hover action as it is unclear that this is set and when overwritten may cause confusion
    protected static final HoverAction<DrawableEdge> defaultHoverAction = (edge, isHovering) -> {
        if (!(edge instanceof WeightedDrawableEdge)) return;
        if (isHovering) edge.setColour(Color.RED);
        else edge.setColour(Color.BLACK);
    };
    private DrawableEdge oppositeEdge = null; // If the edge is copied this will still refer to the opposite edge on the original graph
    protected final EdgeLine edgeLine;
    protected Arrow arrow;
    private Color lineColour = Color.BLACK, arrowColour = Color.BLACK;

    public DrawableEdge(DrawableNode startNode, DrawableNode endNode, boolean directed) throws UndefinedNodeException, InvalidEdgeException {
        this(startNode, endNode, directed, defaultHoverAction);
    }

    public DrawableEdge(DrawableNode startNode, DrawableNode endNode, boolean directed, HoverAction<DrawableEdge> hoverAction) throws UndefinedNodeException, InvalidEdgeException {
        // Ensure that both the start node and the end node are defined correctly
        if (startNode == null) throw new UndefinedNodeException(null);
        if (endNode == null) throw new UndefinedNodeException(null);
        // Make sure the start and end nodes are different
        if (startNode.equals(endNode) || !startNode.isOnSameGraph(endNode))
            throw new InvalidEdgeException(startNode, endNode);

        this.startNode = startNode;
        this.endNode = endNode;
        this.directed = directed;
        this.graph = startNode.getGraph();
        this.hoverAction = hoverAction;

        if (directed) {
            // Find the opposite edge to this one, if it exists
            this.oppositeEdge = graph.getEdge(endNode, startNode, true);
            if (oppositeEdge != null) {
                // If this is a copied edge then the original edge would already be stored as the opposite edge's
                // opposite edge, if it isn't then set it to this one.
                if (oppositeEdge.oppositeEdge == null) {
                    oppositeEdge.oppositeEdge = this;
                    oppositeEdge.reconnect();
                }
            }
        }

        // Create the line between the nodes
        edgeLine = new EdgeLine();

        getChildren().add(edgeLine);

        // Create the arrow if the edge is directed
        if (directed) {
            arrow = new Arrow();
            getChildren().add(arrow);
        }

        // Create the hover mask
        hoverMask.setStrokeWidth(0);
        hoverMask.setFill(Color.TRANSPARENT);
        hoverMask.hoverProperty().addListener((ignored1, ignored2, isHovered) -> handleHover(isHovered));
        connectHoverMask();
        getChildren().add(hoverMask);
    }

    public void draw() {
        graph.draw(this);
    }

    public void setColour(Color colour) {
        setColours(colour, colour);
    }

    public void setColours(Color lineColour, Color arrowColour) {
        if (lineColour != null) setLineColour(lineColour);
        if (arrowColour != null) setArrowColour(arrowColour);
    }

    public void setLineColour(Color colour) {
        edgeLine.setStroke(colour);
        lineColour = colour;
    }

    public Color getLineColour() {
        return lineColour;
    }

    public void setArrowColour(Color colour) {
        if (arrow != null) arrow.setFill(colour);
        arrowColour = colour;
    }

    public Color getArrowColour() {
        return arrowColour;
    }

    /**
     * Reconnect the edge to its nodes. If either node changes in size or position this method should be called.
     */
    public void reconnect() {
        edgeLine.reconnect();
        if (directed && arrow != null) arrow.reconnect();
        connectHoverMask();
    }

    private void connectHoverMask() {
        hoverMask.getPoints().clear();

        Point u = getNormalisedLineVector();

        Point lineEnd = endNode.getCentre().sub(u.multiply(endNode.getNodeRadius()));
        Point lineStart = getStartPoint();
        Point vectorHalfWidth = new Point(u.getY(), -u.getX()).multiply(HOVER_MASK_WIDTH/2);
        Point startTop = lineStart.sub(vectorHalfWidth);
        Point startBottom = lineStart.add(vectorHalfWidth);
        Point endTop = lineEnd.sub(vectorHalfWidth);
        Point endBottom = lineEnd.add(vectorHalfWidth);

        addHoverMaskPoint(startTop);
        addHoverMaskPoint(startBottom);
        addHoverMaskPoint(endBottom);
        addHoverMaskPoint(endTop);
    }

    private void addHoverMaskPoint(Point point) {
        hoverMask.getPoints().add(point.getX());
        hoverMask.getPoints().add(point.getY());
    }

    public void setHoverAction(HoverAction<DrawableEdge> hoverAction) {
        this.hoverAction = hoverAction;
    }

    protected void handleHover(boolean isHovering) {
        if (hoverAction != null) hoverAction.handle(this, isHovering);
    }

    /**
     * Check if this edge involves the specified node, i.e. the node is at either end of the edge.
     * @param nodeID the ID of the node
     * @return true if the node is involved in the edge, false otherwise
     */
    public boolean involves(int nodeID) {
        return startNode.id() == nodeID || endNode.id() == nodeID;
    }

    public boolean involves(DrawableNode node) {
        return startNode.equals(node) || endNode.equals(node);
    }

    public boolean intersectsAnyOf(ArrayList<DrawableNode> nodes) {
        boolean hasIntersection = false;
        for (DrawableNode node : nodes) {
            if (intersectsNode(node)) hasIntersection = true;
        }
        return hasIntersection;
    }

    public boolean intersectsNode(DrawableNode node) {
        // If the node is one of the ends of the line then ignore whether it intersects or not
        if (involves(node)) return false;

        // Find the closest point on the line to the node
        Point closest = closestPointTo(node);

        // If the point is not on the line then the node does not intersect
        if (closest == null) return false;

        // Find the distance between the closest point and the centre of the circle
        Point centre = node.getCentre();
        double distanceX = closest.getX() - centre.getX();
        double distanceY = closest.getY() - centre.getY();
        double distance = Math.sqrt((distanceX*distanceX) + (distanceY*distanceY));

        return distance<=node.getNodeRadius();
    }

    public Point closestPointTo(Point point) {
        Point startPoint = startNode.getCentre();
        Point endPoint = endNode.getCentre();

        double cx = point.getX();
        double cy = point.getY();

        // Initialise the start and end coordinates of the line - (x1, y1) and (x2, y2)
        double x1 = startPoint.getX(), x2 = endPoint.getX();
        double y1 = startPoint.getY(), y2 = endPoint.getY();

        // Find the distance between the x coordinates and the y coordinates
        double dx = x1 - x2;
        double dy = y1 - y2;

        // Find the length of the line
        double lineLength = Math.sqrt(dx*dx + dy*dy);

        double dot = ( ((cx-x1)*(x2-x1)) + ((cy-y1)*(y2-y1)) ) / Math.pow(lineLength,2);

        // Find the closest point on the line using the dot product
        double closestX = x1 + (dot * (x2-x1));
        double closestY = y1 + (dot * (y2-y1));
        Point closest = new Point(closestX, closestY);

        // Get the distance between the closest point and either end of the line
        double d1 = closest.distance(x1, y1);
        double d2 = closest.distance(x2, y2);

        // If the point is not on the line then the node does not intersect
        if (!(d1 + d2 >= lineLength-0.01 && d1 + d2 <= lineLength+0.01)) {
            return null;
        }
        return closest;
    }

    public Point closestPointTo(double x, double y) {
        return closestPointTo(new Point(x, y));
    }

    public Point closestPointTo(DrawableNode node) {
        return closestPointTo(node.getCentre());
    }


    /**
     * @return the edge's starting node as a {@link DrawableNode}.
     */
    public DrawableNode startNode() {
        return startNode;
    }

    /**
     * @return the edge's ending node as a {@link DrawableNode}.
     */
    public DrawableNode endNode() {
        return endNode;
    }

    public Point getStartPoint() {
        Point lineStart;
        if (oppositeEdge == null) lineStart = startNode.getCentre().add(getNormalisedLineVector().multiply(startNode.getNodeRadius()));
        else lineStart = startNode.getCentre().midpoint(endNode.getCentre());
        return lineStart;
    }

    protected Point getNormalisedLineVector() {
        return startNode.getCentre().getVectorTo(endNode.getCentre()).normalize();
    }

    public DrawableEdge createCopyWith(ArrayList<DrawableNode> copiedNodes) {
        return createCopyWith(copiedNodes, null);
    }

    protected DrawableEdge createCopyWith(ArrayList<DrawableNode> copiedNodes, String value) {
        DrawableNode node1 = null;
        DrawableNode node2 = null;
        for (DrawableNode copiedNode : copiedNodes) {
            if (copiedNode.equals(startNode)) node1 = copiedNode;
            if (copiedNode.equals(endNode)) node2 = copiedNode;
        }
        DrawableEdge copy;
        if (value == null) copy = new DrawableEdge(node1, node2, directed, hoverAction);
        else copy = new WeightedDrawableEdge(node1, node2, directed, value, hoverAction);
        adjustCopyValues(copy);
        return copy;
    }

    private void adjustCopyValues(DrawableEdge copy) {
        copy.setColours(lineColour, arrowColour);
    }


    public class Arrow extends Polygon {

        public static final double WIDTH = LINE_SIZE * 7.5d, HEIGHT = LINE_SIZE * 15d;

        private Arrow() {
            connectToNode();
            setStrokeWidth(0);
            setFill(LINE_COLOUR);
        }

        /**
         * Public wrapper of the {@link Arrow#connectToNode()} method. If either of the nodes change in size
         * this should be called to adjust the line's start and end points accordingly.
         */
        public void reconnect() {
            connectToNode();
        }

        /**
         * Connect and rotate the arrow to point to the end node.
         * Because the implementation only allows a directed edge to have one direction then the arrow will only ever
         * be drawn on the endNode side.
         */
        private void connectToNode() {
            this.getPoints().clear();

            Point u = getNormalisedLineVector();

            Point endPoint = endNode.getCentre().sub(u.multiply(endNode.getNodeRadius()));
            Point base = endPoint.sub(u.multiply(HEIGHT));
            Point uBase = new Point(u.getY(), -u.getX());
            Point vBase = uBase.multiply(WIDTH/2);
            Point left = base.sub(vBase);
            Point right = base.add(vBase);


            this.getPoints().add(endPoint.getX());
            this.getPoints().add(endPoint.getY());

            this.getPoints().add(left.getX());
            this.getPoints().add(left.getY());
            this.getPoints().add(right.getX());
            this.getPoints().add(right.getY());
        }

    }

    public class EdgeLine extends Line {
        public static final double WIDTH = LINE_SIZE;

        private EdgeLine() {
            setStrokeLineCap(StrokeLineCap.BUTT);
            setStrokeWidth(WIDTH);
            setStroke(LINE_COLOUR);

            connectToNodes();
        }

        /**
         * The public wrapper of the {@link EdgeLine#connectToNodes()} method. If either of the nodes change in size
         * this should be called to adjust the line's start and end points accordingly.
         */
        public void reconnect() {
            connectToNodes();
        }

        /**
         * Connect the edge to its nodes.
         */
        private void connectToNodes() {
            Point endCentre = endNode.getCentre();

            Point u = getNormalisedLineVector();

            Point start = getStartPoint();

            double endRadius = endNode.getNodeRadius();
            Point end = endCentre.sub(u.multiply(endRadius));

            if (directed) end = end.sub(u.multiply(Arrow.HEIGHT));

            setPosition(start, end);
        }

        /**
         * Combine two points into a vector.
         */
        private Point getVector(double x0, double y0, double x1, double y1) {
            return new Point(x1 - x0, y1 - y0);
        }

        /**
         * Normalise a vector.
         * @deprecated {@link Point#normalize()} is now used.
         */
        private Point normaliseVector(Point v) {
            double magnitude = Math.sqrt((v.getX() * v.getX()) + (v.getY() * v.getY()));
            double x = v.getX() / magnitude;
            double y = v.getY() / magnitude;
            return new Point(x, y);
        }

        /**
         * Set the start point of the line.
         */
        private void setStart(Point point) {
            setStartX(point.getX());
            setStartY(point.getY());
        }

        /**
         * Set the end point of the line.
         */
        private void setEnd(Point point) {
            setEndX(point.getX());
            setEndY(point.getY());
        }

        /**
         * Set both the start and end points of the line.
         */
        private void setPosition(Point start, Point end) {
            setStart(start);
            setEnd(end);
        }

        public Point getStartingPoint() {
            return new Point(getStartX(), getStartY());
        }

        public Point getEndPoint() {
            return new Point(getEndX(), getEndY());
        }

    }

    /**
     * Compares this edge with the specified object. The argument must not be null and must be an {@code DrawableEdge} object.
     * To return true they must both have same edge type (directed/undirected) and if directed then the start and
     * end nodes must be the same, if undirected then the start and end nodes must either be the same or opposing.
     * <br/>The rules are as follows:
     * <ul>
     *     <li>a -> b == a -> b&nbsp; = &nbsp;true</li>
     *     <li>a -> b == b -> a&nbsp; = &nbsp;false</li>
     *     <li>a -> b == a --- b&nbsp; = &nbsp;false</li>
     *     <li>a --- b == a --- b&nbsp; = &nbsp;true</li>
     *     <li>a --- b == b --- a&nbsp; = &nbsp;true</li>
     * </ul>
     * Where -> denotes a directed edge (left to right) and --- denotes an undirected edge.
     * @param o the object to compare this edge against
     * @return true if the given object represents an {@code DrawableEdge} equivalent to this edge, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DrawableEdge edge)) return false;
        boolean sameEdgeType = directed == edge.directed;
        boolean sameStartEnd = startNode.equals(edge.startNode) && endNode.equals(edge.endNode);
        boolean oppositeStartEnd = startNode.equals(edge.endNode) && endNode.equals(edge.startNode);
        boolean directedSameNodes = directed && edge.directed && sameStartEnd;
        boolean nonDirectedSameNodes = !directed && !edge.directed
                && (sameStartEnd || oppositeStartEnd);

        return sameEdgeType && (directedSameNodes || nonDirectedSameNodes);
    }
}