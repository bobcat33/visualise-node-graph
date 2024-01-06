package graphvisualisation.graphics.objects;

import graphvisualisation.data.graph.elements.WeightedNode;
import graphvisualisation.graphics.graphing.Graph;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        super(graph, node);
        this.value = node.value();

        this.weight = new Weight(this);

        border.hoverProperty().addListener(new HoverListener(this, weight));

        textID.hoverProperty().addListener(new HoverListener(this, weight));
    }

    public String value() {
        return value;
    }

    public Weight getWeight() {
        return weight;
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

            hoverProperty().addListener(new HoverListener(node, this));

            getChildren().addAll(textBorder, text);
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

    private record HoverListener(WeightedDrawableNode node, Weight weight) implements ChangeListener<Boolean> {
        @Override
        public void changed(ObservableValue<? extends Boolean> ignored1, Boolean ignored2, Boolean isHovered) {
            weight.setVisible(isHovered);
        }
    }
}