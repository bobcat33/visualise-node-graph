package graphvisualisation.graphics.nodes;

import graphvisualisation.graphics.canvas.Point;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Edge extends Line {

    public static final double EDGE_WIDTH = 2d;

    public Edge(DrawableNode startNode, DrawableNode endNode) throws UndefinedNodeException, InvalidEdgeException {
        if (!startNode.isDefined()) throw new UndefinedNodeException(startNode);
        if (!endNode.isDefined()) throw new UndefinedNodeException(endNode);
        if (startNode == endNode || startNode.getNodeID() == endNode.getNodeID()) throw new InvalidEdgeException(startNode, endNode);

        setStrokeLineCap(StrokeLineCap.BUTT);
        setStrokeWidth(EDGE_WIDTH);

        connectToNodes(startNode, endNode);
    }

    private void connectToNodes(DrawableNode startNode, DrawableNode endNode) {
        Point startCentre = startNode.getCentre();
        Point endCentre = endNode.getCentre();

        double startX = startCentre.getX();
        double startY = startCentre.getY();
        double endX = endCentre.getX();
        double endY = endCentre.getY();

        double startRadius = startNode.getRadius();
        double endRadius = endNode.getRadius();

        Point u = getVector(startX, startY, endX, endY).normalize();

        Point start = startCentre.add(u.multiply(startRadius));
        Point end = endCentre.sub(u.multiply(endRadius));

        setPosition(start, end);
    }

    private Point getVector(double x0, double y0, double x1, double y1) {
        return new Point(x1 - x0, y1 - y0);
    }

    private Point normaliseVector(Point v) {
        double magnitude = Math.sqrt((v.getX() * v.getX()) + (v.getY() * v.getY()));
        double x = v.getX() / magnitude;
        double y = v.getY() / magnitude;
        return new Point(x, y);
    }

    public void setStart(Point point) {
        setStartX(point.getX());
        setStartY(point.getY());
    }

    public void setEnd(Point point) {
        setEndX(point.getX());
        setEndY(point.getY());
    }

    public void setPosition(Point start, Point end) {
        setStart(start);
        setEnd(end);
    }

}
