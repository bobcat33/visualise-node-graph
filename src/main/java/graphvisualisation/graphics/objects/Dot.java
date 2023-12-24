package graphvisualisation.graphics.objects;

import graphvisualisation.graphics.canvas.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Dot was used to test to make sure certain points were in the right places
 * @deprecated useless :)
 */
public class Dot extends Circle {
    public static final double RADIUS = 10;
    public static final Color colour = Color.RED;

    public Dot(double x, double y) {

        setRadius(RADIUS);
        setCenterX(x);
        setCenterY(y);
        setFill(colour);

    }

    public Dot(Point point) {
        this(point.getX(), point.getY());
    }

}