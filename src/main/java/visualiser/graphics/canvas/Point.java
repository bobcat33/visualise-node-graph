package visualiser.graphics.canvas;

import java.util.Random;

public class Point {
    private static final Random random = new Random();
    private final double x, y;

    public Point() {
        this(0, 0);
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return the X value of this point
     */
    public double getX() {
        return x;
    }

    /**
     * @return the Y value of this point
     */
    public double getY() {
        return y;
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
     * Create a random point between the points min and max.
     * @param min the minimum point
     * @param max the maximum point
     * @return a new random {@code Point} between the minimum and maximum values
     */
    public static Point generateRandom(Point min, Point max) {
        return generateRandom(min.getX(), min.getY(), max.getX(), max.getY());
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

    /**
     * @return the normalisation of this point
     */
    public Point normalize() {
        double magnitude = magnitude();
        return magnitude == 0d ? new Point(0d, 0d) : new Point(getX() / magnitude, getY() / magnitude);
    }

    /**
     * @return the magnitude of this point
     */
    public double magnitude() {
        double x = this.getX();
        double y = this.getY();
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Multiply the x and y parameters of this point by v.
     * @param v the multiplier
     * @return the result of the multiplication
     */
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

    /**
     * Calculate the magnitude of the distance between this point and the parameter point.
     * @param point the point to calculate the magnitude to
     * @return the distance between the points
     */
    public double distanceTo(Point point) {
        return distanceTo(point.getX(), point.getY());
    }

    /**
     * Calculate the magnitude of the distance between this point and the parameter coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the distance between the points
     */
    public double distanceTo(double x, double y) {

        double xDifference = this.getX() - x;
        double yDifference = this.getY() - y;

        return Math.sqrt((xDifference * xDifference) + (yDifference * yDifference));
    }

    /**
     * The vector from this point to the parameter point.
     * @param point the point to get the vector to
     * @return the vector to the point
     * @see #getVectorBetween(Point, Point)
     */
    public Point getVectorTo(Point point) {
        return getVectorBetween(this, point);
    }

    /**
     * Get a vector from point1 to point2.
     * @param point1 the start point
     * @param point2 the end point
     * @return the vector from point1 to point2
     * @see #getVectorTo(Point)
     */
    public static Point getVectorBetween(Point point1, Point point2) {
        return new Point(point2.getX() - point1.getX(), point2.getY() - point1.getY());
    }

    /**
     * @return the midpoint between this point and the parameter point
     */
    public Point midpoint(Point point) {
        return midpoint(point.getX(), point.getY());
    }

    /**
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @return the midpoint between this point and the parameter coordinates
     */
    public Point midpoint(double x, double y) {
        return new Point(x + (this.getX() - x) / 2d, y + (this.getY() - y) / 2d);
    }

    /**
     * Add v to each of this point's coordinates.
     * @param v the value to add
     * @return the result of the addition
     */
    public Point add(double v) {
        return new Point(getX() + v, getY() + v);
    }

    /**
     * Subtract v from each of this point's coordinates.
     * @param v the value to subtract
     * @return the result of the subtraction
     */
    public Point sub(double v) {
        return new Point(getX() - v, getY() + v);
    }

    /**
     * Check if this point is equal to the object.
     * @param o the object to compare to this object
     * @return true if o is an instance of {@code Point} and has the same X and Y coordinates
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Point point)) return false;
        return this.getX() == point.getX() && this.getY() == point.getY();
    }

    /**
     * Check if this point is equal to the parameter coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if this point has the same x and y values as the x and y parameters
     */
    public boolean equals(double x, double y) {
        return getX() == x && getY() == y;
    }
}
