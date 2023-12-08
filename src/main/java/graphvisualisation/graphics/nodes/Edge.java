package graphvisualisation.graphics.nodes;

import graphvisualisation.graphics.canvas.Point;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Edge extends Line {

    public static final double EDGE_WIDTH = 2d;

    private final DrawableNode startNode;
    private final DrawableNode endNode;
    private final boolean directional;

    public Edge(DrawableNode startNode, DrawableNode endNode) throws UndefinedNodeException, InvalidEdgeException {
        this(startNode, endNode, false);
    }

    public Edge(DrawableNode startNode, DrawableNode endNode, boolean directional) throws UndefinedNodeException, InvalidEdgeException{
        if (startNode.isUndefined()) throw new UndefinedNodeException(startNode);
        if (endNode.isUndefined()) throw new UndefinedNodeException(endNode);
        if (startNode == endNode || startNode.getNodeID() == endNode.getNodeID()) throw new InvalidEdgeException(startNode, endNode);

        this.startNode = startNode;
        this.endNode = endNode;
        this.directional = directional;

        setStrokeLineCap(StrokeLineCap.BUTT);
        setStrokeWidth(EDGE_WIDTH);

        connectToNodes();
    }

    /**
     * The public wrapper for {@link Edge#connectToNodes()}. If either of the nodes change in size this should be
     * called to adjust the line's start and end points accordingly.
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

        double startX = startCentre.getX();
        double startY = startCentre.getY();
        double endX = endCentre.getX();
        double endY = endCentre.getY();

        double startRadius = startNode.getCircleRadius();
        double endRadius = endNode.getCircleRadius();

        Point u = getVector(startX, startY, endX, endY).normalize();

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
    public void setStart(Point point) {
        setStartX(point.getX());
        setStartY(point.getY());
    }

    /**
     * Set the end point of the line.
     */
    public void setEnd(Point point) {
        setEndX(point.getX());
        setEndY(point.getY());
    }

    /**
     * Set both the start and end points of the line.
     */
    public void setPosition(Point start, Point end) {
        setStart(start);
        setEnd(end);
    }

}
