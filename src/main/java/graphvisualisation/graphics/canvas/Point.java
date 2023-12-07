package graphvisualisation.graphics.canvas;

import javafx.geometry.Point2D;

public class Point extends Point2D {

    public Point(double x, double y) {
        super(x, y);
        if (x > Canvas.WIDTH || x < 0 || y > Canvas.HEIGHT || y < 0)
            System.err.println("WARN: Point (" + x + ", " + y + ") is out of canvas bounds.");
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

    public Point add(Point point) {
        return new Point(getX() + point.getX(), getY() + point.getY());
    }

    public Point sub(Point point) {
        return new Point(this.getX() - point.getX(), this.getY() - point.getY());
    }
}
