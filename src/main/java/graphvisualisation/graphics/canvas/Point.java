package graphvisualisation.graphics.canvas;

import javafx.geometry.Point2D;

public class Point extends Point2D {

    // todo: at the moment this class relies on extending Point2D, once I have finished the code I will extract the methods I need and remove the superclass
    public Point(double x, double y) {
        super(x, y);
        /*if (x > Canvas.WIDTH || x < 0 || y > Canvas.HEIGHT || y < 0)
            System.err.println("WARN: Point (" + x + ", " + y + ") is out of canvas bounds.");*/
    }

    @Override
    public Point normalize() {
        Point2D point = super.normalize();
        return new Point(point.getX(), point.getY());
    }

    @Override
    public Point multiply(double v) {
        return new Point(v * getX(), v * getY());
    }

    /**
     * Add the x and y values of this point and the parameter point.
     * <br/>Formula: (x1 + x2, y1 + y2)
     * <br/>Where x1 and y1 are the x and y values of this point and x2 and y2 are the x and y values of the parameter
     * point
     * @param point the point to be added to this point
     * @return the result of the addition
     */
    public Point add(Point point) {
        return new Point(getX() + point.getX(), getY() + point.getY());
    }

    /**
     * Subtract the x and y values of the parameter point from the x and y values of this point.
     * <br/>Formula: (x1 - x2, y1 - y2)
     * <br/>Where x1 and y1 are the x and y values of this point and x2 and y2 are the x and y values of the parameter
     * point
     * @param point the point to be subtracted from this point
     * @return the result of the subtraction
     */
    public Point sub(Point point) {
        return new Point(this.getX() - point.getX(), this.getY() - point.getY());
    }

    public double distance(Point point) {

        double xDifference = this.getX() - point.getX();
        double yDifference = this.getY() - point.getY();

        return Math.sqrt((xDifference * xDifference) + (yDifference * yDifference));
    }
}
