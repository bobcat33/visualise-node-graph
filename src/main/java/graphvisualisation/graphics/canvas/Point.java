package graphvisualisation.graphics.canvas;

import javafx.geometry.Point2D;

import java.util.Random;

public class Point extends Point2D {
    private static Random random = new Random();

    // todo: at the moment this class relies on extending Point2D, once I have finished the code I will extract the methods I need and remove the superclass
    public Point(double x, double y) {
        super(x, y);
        /*if (x > Canvas.WIDTH || x < 0 || y > Canvas.HEIGHT || y < 0)
            System.err.println("WARN: Point (" + x + ", " + y + ") is out of canvas bounds.");*/
    }

    /**
     * Create a random point between values min and max.
     * @param min the minimum x and y values
     * @param max the maximum x and y values
     * @return a new random {@code Point} between the minimum and maximum values
     */
    public static Point generateRandom(double min, double max) {
        return generateRandom(min, min, max, max);
    }

    /**
     * Create a random point between the minimum and maximum X and Y values.
     * @param minX minimum X value
     * @param minY minimum Y value
     * @param maxX maximum X value
     * @param maxY maximum Y value
     * @return a new random {@code Point} between the minimum and maximum values
     */
    public static Point generateRandom(double minX, double minY, double maxX, double maxY) {
        return new Point(random.nextDouble(maxX - minX) + minX, random.nextDouble(maxY - minY) + minY);
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

    public double distanceTo(Point point) {

        double xDifference = this.getX() - point.getX();
        double yDifference = this.getY() - point.getY();

        return Math.sqrt((xDifference * xDifference) + (yDifference * yDifference));
    }

    public Point getVectorTo(Point point) {
        return getVectorBetween(this, point);
    }

    public static Point getVectorBetween(Point point1, Point point2) {
        return new Point(point2.getX() - point1.getX(), point2.getY() - point1.getY());
    }

    public Point add(double v) {
        return new Point(getX() + v, getY() + v);
    }

    public Point sub(double v) {
        return new Point(getX() - v, getY() + v);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Point point)) return false;
        return this.getX() == point.getX() && this.getY() == point.getY();
    }

    public boolean equals(double x, double y) {
        return getX() == x && getY() == y;
    }
}
