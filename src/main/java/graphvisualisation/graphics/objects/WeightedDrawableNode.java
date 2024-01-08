package graphvisualisation.graphics.objects;

import graphvisualisation.data.graph.elements.WeightedNode;
import graphvisualisation.graphics.graphing.Graph;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    private final Circle hoverMask;
    private HoverAction hoverAction;

    public WeightedDrawableNode(Graph graph, WeightedNode node) {
        this(graph, node, (actionNode, isHovered) -> {
            if (isHovered) System.out.println("Hovered over node " + actionNode.name());
            else System.out.println("Moved off node " + actionNode.name());
        });
    }

    public WeightedDrawableNode(Graph graph, WeightedNode node, HoverAction hoverAction) {
        super(graph, node);
        this.value = node.value();
        this.hoverAction = hoverAction;

        this.weight = new Weight(this);

        hoverMask = new Circle(getNodeRadius(), Color.TRANSPARENT);
        getChildren().add(hoverMask);

        this.weight.setListener((ignored1, ignored2, isHovered) -> weight.setVisible(isHovered));

        hoverMask.hoverProperty().addListener((ignored1, ignored2, isHovered) -> {
            weight.setVisible(isHovered);
            handleHover(isHovered);
        });
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

    public void setHoverAction(HoverAction hoverAction) {
        this.hoverAction = hoverAction;
    }

    private void handleHover(boolean isHovering) {
        if (hoverAction != null) hoverAction.handle(this, isHovering);
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

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (o instanceof WeightedDrawableNode compareNode) return node.equals(compareNode);
            if (o instanceof Weight nodeWeight) return node.equals(nodeWeight.node);
            return false;
        }
    }

    public interface HoverAction {
        void handle(WeightedDrawableNode node, boolean isHovering);
    }
}