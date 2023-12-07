package graphvisualisation.graphics.nodes;

import graphvisualisation.graphics.canvas.Point;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DrawableNode extends StackPane {

    public static double maxRadius;
    public static final double NODE_PADDING = 30d,
                                BORDER_WIDTH = 0.5d,
                                FONT_SIZE = 30d;

    private final int id;
    private double xPos = 0, yPos = 0;
    private final Circle border;
    private final Text textID;

    public DrawableNode(int id) {
        this.id = id;

        border = new Circle();
        border.setFill(null);
        border.setStrokeWidth(BORDER_WIDTH);
        border.setStroke(Color.BLACK);
        
        textID = new Text(Integer.toString(id));
        textID.setFont(new Font(FONT_SIZE));

        double radius = getBaseRadius();
        border.setRadius(radius);

        if (radius > maxRadius) maxRadius = radius;

        draw();
    }
    
    public boolean isUndefined() {
        return getChildren().size() != 2 || !(getChildren().get(0) instanceof Circle) || !(getChildren().get(1) instanceof Text);
    }

    public int getNodeID() {
        return id;
    }

    public double getRadius() {
        return border.getRadius();
    }

    public double getBaseRadius() {
        return (textID.getLayoutBounds().getWidth() / 2) + NODE_PADDING;
    }

    public void setPosition(Point point) {
        setPosition(point.getX(), point.getY());
    }
    
    public void setPosition(double x, double y) {
        xPos = x;
        yPos = y;
        setLayoutX(x);
        setLayoutY(y);
    }

    public void setCentre(Point point) {
        setCentre(point.getX(), point.getY());
    }

    public void setCentre(double x, double y) {
        double radius = getRadius();
        xPos = x - radius;
        yPos = y - radius;
        setLayoutX(xPos);
        setLayoutY(yPos);
    }
    
    public Point getCentre() {
        double radius = getRadius();
        return new Point(xPos + radius, yPos + radius);
    }


    private StackPane draw() {

        getChildren().clear();
        getChildren().addAll(border, textID);

        return this;
    }

    public StackPane matchSize() {
        return matchSize(false);
    }

    public StackPane matchSize(boolean maintainCentre) {
        return setRadius(maxRadius, maintainCentre);
    }

    public StackPane resetSize() {
        return resetSize(false);
    }

    public StackPane resetSize(boolean maintainCentre) {
        return setRadius(getBaseRadius(), maintainCentre);
    }

    private StackPane setRadius(double radius, boolean maintainCentre) {
        Point centre = getCentre();
        border.setRadius(radius);
        if (maintainCentre) setCentre(centre);
        return draw();
    }

    public Edge connectNode(DrawableNode node) throws UndefinedNodeException, InvalidEdgeException {
        return new Edge(this, node);
    }
}
