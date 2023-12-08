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
                                BORDER_WIDTH = 20d,
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

    public double getNodeWidth() {
        return getBoundsInParent().getWidth();
    }

    public void setPosition(Point point) {
        setPosition(point.getX(), point.getY());
    }
    
    public void setPosition(double x, double y) {
        xPos = x;
        yPos = y;
        setLayoutX(x);
        setLayoutY(y);
        draw();
    }

    public Point getPosition() {
        return new Point(xPos, yPos);
    }

    public void setCentre(Point point) {
        setCentre(point.getX(), point.getY());
    }

    public void setCentre(double x, double y) {
        double nodeCentre = getNodeWidth()/2;
        xPos = x - nodeCentre;
        yPos = y - nodeCentre;
        setLayoutX(xPos);
        setLayoutY(yPos);
        draw();
    }
    
    public Point getCentre() {
        double nodeCentre = getNodeWidth()/2;
        return new Point(xPos + nodeCentre, yPos + nodeCentre);
    }


    private void draw() {
        getChildren().clear();
        getChildren().addAll(border, textID);
    }

    public void matchSize() {
        matchSize(false);
    }

    public void matchSize(boolean maintainCentre) {
        setRadius(maxRadius, maintainCentre);
    }

    public void resetSize() {
        resetSize(false);
    }

    public void resetSize(boolean maintainCentre) {
        setRadius(getBaseRadius(), maintainCentre);
    }

    private void setRadius(double radius, boolean maintainCentre) {
        Point centre = getCentre();
        border.setRadius(radius);
        if (maintainCentre) setCentre(centre);
        draw();
    }

    public Edge connectNode(DrawableNode node) throws UndefinedNodeException, InvalidEdgeException {
        return new Edge(this, node);
    }
}
