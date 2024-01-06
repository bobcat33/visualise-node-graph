package graphvisualisation.graphics.objects;

import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class WeightedDrawableEdge extends DrawableEdge {
    public static final double
            WEIGHTED_CONTENT_BORDER_WIDTH = DrawableNode.BORDER_WIDTH,
            WEIGHTED_CONTENT_PADDING = 30d,
            WEIGHTED_CONTENT_FONT_SIZE = DrawableNode.FONT_SIZE/2;

    private final String value;
    private final Weight weight;

    public WeightedDrawableEdge(DrawableNode startNode, DrawableNode endNode, boolean directed, String value) throws InvalidEdgeException, UndefinedNodeException {
        super(startNode, endNode, directed);
        this.value = value;

        this.weight = new Weight(this);
        System.out.println("New weight created with value: " + value);

        hoverProperty().addListener(new HoverListener(this, weight));
    }

    public String value() {
        return value;
    }

    public Weight getWeight() {
        return weight;
    }

    public class Weight extends StackPane {
        private final WeightedDrawableEdge edge;
        private Weight(WeightedDrawableEdge edge) {
            this.edge = edge;

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

            hoverProperty().addListener(new HoverListener(edge, this));

            getChildren().addAll(textBorder, text);
        }

        public String value() {
            return value;
        }

        public WeightedDrawableEdge edge() {
            return edge;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (o instanceof WeightedDrawableEdge compareEdge) return edge.equals(compareEdge);
            if (o instanceof Weight edgeWeight) return edge.equals(edgeWeight.edge);
            return false;
        }
    }

    private record HoverListener(WeightedDrawableEdge edge, Weight weight) implements ChangeListener<Boolean> {
        @Override
        public void changed(ObservableValue<? extends Boolean> ignored1, Boolean ignored2, Boolean isHovered) {

            weight.setVisible(isHovered);
        }
    }
}
