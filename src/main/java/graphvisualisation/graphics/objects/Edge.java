package graphvisualisation.graphics.objects;

import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayList;

public class Edge extends Parent {
    public static final Color LINE_COLOUR = Color.BLACK;

    private final DrawableNode startNode;
    private final DrawableNode endNode;
    private final boolean directed;
    private final EdgeLine edgeLine;
    private Arrow arrow;

    public Edge(DrawableNode startNode, DrawableNode endNode) throws UndefinedNodeException, InvalidEdgeException {
        this(startNode, endNode, false);
    }

    public Edge(DrawableNode startNode, DrawableNode endNode, boolean directed) throws UndefinedNodeException, InvalidEdgeException {
        // Ensure that both the start node and the end node are defined correctly
        if (startNode.isUndefined()) throw new UndefinedNodeException(startNode);
        if (endNode.isUndefined()) throw new UndefinedNodeException(endNode);
        // Make sure the start and end nodes are different
        if (startNode.equals(endNode))
            throw new InvalidEdgeException(startNode, endNode);

        this.startNode = startNode;
        this.endNode = endNode;
        this.directed = directed;

        // Create the line between the nodes
        edgeLine = new EdgeLine();

        getChildren().add(edgeLine);

        // Create the arrow if the edge is directed
        if (directed) {
            arrow = new Arrow();
            getChildren().add(arrow);
        }
    }

    // todo: ensure that this method is always called when nodes are moved/resized
    /**
     * Reconnect the edge to its nodes. If either node changes in size or position this method should be called.
     */
    public void reconnect() {
        edgeLine.reconnect();
        if (directed && arrow != null) arrow.reconnect();
    }

    /**
     * Check if this edge involves the specified node, i.e. the node is at either end of the edge.
     * @param nodeID the ID of the node
     * @return true if the node is involved in the edge, false otherwise
     */
    public boolean involves(int nodeID) {
        return startNode.getNodeID() == nodeID || endNode.getNodeID() == nodeID;
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
        if (involves(node.getNodeID())) return false;

        Point startPoint = startNode.getCentre();
        Point endPoint = endNode.getCentre();

        Point nodeCentre = node.getCentre();

        double cx = nodeCentre.getX();
        double cy = nodeCentre.getY();

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
        if (!(d1 + d2 >= lineLength-0.01 && d1 + d2 <= lineLength+0.01)) return false;

        // Find the distance between the closest point and the centre of the circle
        double distanceX = closestX - cx;
        double distanceY = closestY - cy;
        double distance = Math.sqrt((distanceX*distanceX) + (distanceY*distanceY));

        return distance<=node.getNodeRadius();

    }


    /**
     * @return the edge's starting node as a {@link DrawableNode}.
     * @deprecated currently unused so set to private
     */
    private DrawableNode getStartNode() {
        return startNode;
    }

    /**
     * @return the edge's ending node as a {@link DrawableNode}.
     * @deprecated currently unused so set to private
     */
    private DrawableNode getEndNode() {
        return endNode;
    }

    private Point getVector(Point point1, Point point2) {
        return new Point(point2.getX() - point1.getX(), point2.getY() - point1.getY());
    }

    private Point getNormalisedLineVector() {
        return getVector(startNode.getCentre(), endNode.getCentre()).normalize();
    }

    /**
     * Dot was used to test to make sure certain points were in the right places
     * @deprecated useless :)
     */
    public class Dot extends Circle {
        public static final double RADIUS = 10;
        public static final Color colour = Color.RED;

        private Dot(Point point) {

            setRadius(RADIUS);
            setCenterX(point.getX());
            setCenterY(point.getY());
            setFill(colour);

        }

    }

    public class Arrow extends Polygon {

        public static final double WIDTH = 15d, HEIGHT = 30d;

        private Arrow() {
            connectToNode();
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

            Point endPoint = endNode.getCentre().sub(u.multiply(endNode.getCircleRadius()));
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
            setFill(LINE_COLOUR);
        }

    }

    public class EdgeLine extends Line {
        public static final double WIDTH = 2d;

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
            Point startCentre = startNode.getCentre();
            Point endCentre = endNode.getCentre();

            double startRadius = startNode.getCircleRadius();
            double endRadius = endNode.getCircleRadius();

            Point u = getNormalisedLineVector();

            Point start = startCentre.add(u.multiply(startRadius));
            Point end = endCentre.sub(u.multiply(endRadius));

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

        public Point getStartPoint() {
            return new Point(getStartX(), getStartY());
        }

        public Point getEndPoint() {
            return new Point(getEndX(), getEndY());
        }

    }

    /**
     * Compares this edge with the specified object. The argument must not be null and must be an {@code Edge} object.
     * To return true they must both have same edge type (directed/undirected) and if directed then the start and
     * end nodes must be the same, if undirected then the start and end nodes must either be the same or opposing.
     * <br/>The rules are as follows:
     * <ul>
     *     <li>a -> b == a -> b&nbsp; = &nbsp;true</li>
     *     <li>a -> b == b -> a&nbsp; = &nbsp;false</li>
     *     <li>a --- b == a --- b&nbsp; = &nbsp;true</li>
     *     <li>a --- b == b --- a&nbsp; = &nbsp;true</li>
     * </ul>
     * Where -> denotes a directed edge (left to right) and --- denotes an undirected edge.
     * @param o the object to compare this edge against
     * @return true if the given object represents an {@code Edge} equivalent to this edge, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Edge edge)) return false;
        boolean sameEdgeType = directed == edge.directed;
        boolean sameStartEnd = startNode.equals(edge.startNode) && endNode.equals(edge.endNode);
        boolean oppositeStartEnd = startNode.equals(edge.endNode) && endNode.equals(edge.startNode);
        boolean directedSameNodes = directed && edge.directed && sameStartEnd;
        boolean nonDirectedSameNodes = !directed && !edge.directed
                && (sameStartEnd || oppositeStartEnd);

        return sameEdgeType && (directedSameNodes || nonDirectedSameNodes);
    }
}