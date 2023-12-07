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
    private final Text textID;

    public DrawableNode(int id) {
        this.id = id;
        
        textID = new Text(Integer.toString(id));
        textID.setFont(new Font(FONT_SIZE));

        double radius = (textID.getLayoutBounds().getWidth() / 2) + NODE_PADDING;
        if (radius > maxRadius) maxRadius = radius;

    }
    
    public boolean isDefined() {
        return getChildren().size() == 2;
    }

    public int getNodeID() {
        return id;
    }

    public double getRadius() {
        if (!isDefined()) return 0;
        return ((Circle) getChildren().get(0)).getRadius();
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
        if (isDefined()) {
            double radius = getRadius();
            xPos = x - radius;
            yPos = y - radius;
            setLayoutX(xPos);
            setLayoutY(yPos);
        }
    }
    
    public Point getCentre() {
        if (!isDefined()) return null;
        double radius = getRadius();
        return new Point(xPos + radius, yPos + radius);
    }

    public StackPane draw() {

        Circle border = new Circle();
        border.setFill(null);
        border.setStrokeWidth(BORDER_WIDTH);
        border.setStroke(Color.BLACK);
        border.setRadius(maxRadius);
        
        if (isDefined()) getChildren().clear();
        getChildren().addAll(border, textID);

        return this;
    }
    
    public void redrawMaintainCentre() {
        if (isDefined()) {
            Point centre = getCentre();
            draw();
            setCentre(centre.getX(), centre.getY());
        }
    }

    public Edge connectNode(DrawableNode node) throws UndefinedNodeException, InvalidEdgeException {
        return new Edge(this, node);
    }

}
