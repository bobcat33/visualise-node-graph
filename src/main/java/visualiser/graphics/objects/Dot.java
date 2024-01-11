package visualiser.graphics.objects;

import visualiser.graphics.canvas.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Dot was used to test to make sure certain points were in the right places
 * @deprecated useless :)
 */
public class Dot extends Circle {
    public static final double RADIUS = 10;
    public static final Color colour = Color.RED;

    public Dot() {
        this(0, 0);
    }

    public Dot(double x, double y) {

        setRadius(RADIUS);
        move(x, y);
        setFill(colour);

    }

    public Dot(Point point) {
        this(point.getX(), point.getY());
    }

    public void move(Point point) {
        move(point.getX(), point.getY());
    }

    public void move(double x, double y) {
        setCenterX(x);
        setCenterY(y);
    }

}