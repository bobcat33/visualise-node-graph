package visualiser.graphics.objects;

import visualiser.data.graphdata.elements.WeightedNode;
import visualiser.graphics.canvas.Point;
import visualiser.graphics.Graph;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class WeightedDrawableNode extends DrawableNode {
    public static final double
            WEIGHTED_CONTENT_BORDER_WIDTH = BORDER_WIDTH,
            WEIGHTED_CONTENT_PADDING = 30d,
            WEIGHTED_CONTENT_FONT_SIZE = FONT_SIZE/2;

    private final String value;
    private final Weight weight;

    public WeightedDrawableNode(Graph graph, WeightedNode node) {
        this(graph, node, defaultHoverAction);
    }

    public WeightedDrawableNode(Graph graph, WeightedNode node, HoverAction<DrawableNode> hoverAction) {
        this(graph, node.id(), node.name(), node.value(), hoverAction);
    }

    public WeightedDrawableNode(Graph graph, int id, String name, String value) {
        this(graph, id, name, value, defaultHoverAction);
    }

    public WeightedDrawableNode(Graph graph, int id, String name, String value, HoverAction<DrawableNode> hoverAction) {
        super(graph, id, name, hoverAction);
        this.value = value;

        weight = new Weight(this);
        weight.setListener((ignored1, ignored2, isHovered) -> weight.setVisible(isHovered));

        hoverMask.hoverProperty().addListener((ignored1, ignored2, isHovered) -> weight.setVisible(isHovered));
    }

    @Override
    protected void setOrigin(double x, double y) {
        super.setOrigin(x, y);
        weight.moveNear(this);
    }

    @Override
    protected void setCircleRadius(double radius, boolean maintainCentre) {
        super.setCircleRadius(radius, maintainCentre);
        hoverMask.setRadius(getNodeRadius());
    }

    public String value() {
        return value;
    }

    public Weight getWeight() {
        return weight;
    }

    @Override
    public WeightedDrawableNode createCopy() {
        return createWeightedCopy(value);
    }

    public class Weight extends StackPane {
        private final WeightedDrawableNode node;
        private Weight(WeightedDrawableNode node) {
            this.node = node;

            setVisible(false);

            // Set up the text box for storing the value
            Text text = new Text(value);
            text.setFont(new Font(WEIGHTED_CONTENT_FONT_SIZE));

            Rectangle textBorder = new Rectangle(
                    0 + WEIGHTED_CONTENT_BORDER_WIDTH/2,
                    0 + WEIGHTED_CONTENT_BORDER_WIDTH/2,
                    text.getLayoutBounds().getWidth() + WEIGHTED_CONTENT_PADDING*2 + WEIGHTED_CONTENT_BORDER_WIDTH,
                    text.getLayoutBounds().getHeight() + WEIGHTED_CONTENT_PADDING*2 + WEIGHTED_CONTENT_BORDER_WIDTH);

            textBorder.setFill(Color.WHITE);
            textBorder.setStroke(Color.BLACK);
            textBorder.setStrokeWidth(WEIGHTED_CONTENT_BORDER_WIDTH);

            getChildren().addAll(textBorder, text);
        }

        private void setListener(ChangeListener<Boolean> hoverListener) {
            hoverProperty().addListener(hoverListener);
        }

        public String value() {
            return value;
        }

        public WeightedDrawableNode node() {
            return node;
        }

        public void moveNear(DrawableNode node) {

        }

        public void moveWithinBoundsTo(double x, double y) {
            moveWithinBoundsTo(x, y, false, false);
        }

        private void moveWithinBoundsTo(double x, double y, boolean hasCrossedAlongY, boolean hasCrossedAlongX) {
            moveTo(x, y);
            double graphHeight = graph.height();
            double graphWidth = graph.width();

            // Find if the weight crosses any of the graph boundaries and reposition appropriately
            boolean moved = false;

            // If weight crosses the top of the bounds
            if (y <= 0 || getEdgePointTowards(x, 0).getY() <= 0) {
                y = getNodeRadius();
                moved = true;
            }
            // If weight crosses the bottom of the bounds
            else if (y >= graphHeight || getEdgePointTowards(x, graphHeight).getY() >= graphHeight) {
                y = graphHeight - getNodeRadius();
                moved = true;
            }
            // If weight crosses the left of the bounds
            if (x <= 0 || getEdgePointTowards(0, y).getX() <= 0) {
                x = getNodeRadius();
                moved = true;
            }
            // If weight crosses the right of the bounds
            else if (x >= graphWidth || getEdgePointTowards(graphWidth, y).getX() >= graphWidth) {
                x = graphWidth - getNodeRadius();
                moved = true;
            }

            // If the weight was found to cross any bounds, move it to the new position within bounds
            if (moved) {
                moveTo(x, y);
            }
        }

        public void moveWithinBoundsTo(Point point) {
            moveWithinBoundsTo(point.getX(), point.getY());
        }

        public void moveTo(Point point) {
            moveTo(point.getX(), point.getY());
        }

        public void moveTo(double x, double y) {
            setLayoutX(x);
            setLayoutY(y);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (o == this) return true;
            if (o instanceof WeightedDrawableNode compareNode) return node.equals(compareNode);
            if (o instanceof Weight nodeWeight) return node.equals(nodeWeight.node);
            return false;
        }
    }
}