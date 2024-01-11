package visualiser.graphics.objects;

import visualiser.graphics.objects.exceptions.InvalidEdgeException;
import visualiser.graphics.objects.exceptions.UndefinedNodeException;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class WeightedDrawableEdge extends DrawableEdge {
    public static final double
            WEIGHTED_CONTENT_BORDER_WIDTH = DrawableNode.BORDER_WIDTH,
            WEIGHTED_CONTENT_PADDING = 30d,
            WEIGHTED_CONTENT_FONT_SIZE = DrawableNode.FONT_SIZE/2;

    private final String value;
    private final Weight weight;

    public WeightedDrawableEdge(DrawableNode startNode, DrawableNode endNode, boolean directed, String value) throws InvalidEdgeException, UndefinedNodeException {
        this(startNode, endNode, directed, value, defaultHoverAction);
    }

    public WeightedDrawableEdge(DrawableNode startNode, DrawableNode endNode, boolean directed, String value, HoverAction<DrawableEdge> hoverAction) throws InvalidEdgeException, UndefinedNodeException {
        super(startNode, endNode, directed, hoverAction);
        this.value = value;

        this.weight = new Weight(this);

        this.weight.setListener((ignored1, ignored2, isHovered) -> weight.setVisible(isHovered));

        hoverMask.hoverProperty().addListener((ignored1, ignored2, isHovered) -> weight.setVisible(isHovered));
    }

    @Override
    public WeightedDrawableEdge createCopyWith(ArrayList<DrawableNode> copiedNodes) {
        if (createCopyWith(copiedNodes, value) instanceof WeightedDrawableEdge weightedEdge) return weightedEdge;
        throw new InvalidEdgeException(startNode, endNode); // todo: more clarity
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

            getChildren().addAll(textBorder, text);
        }

        private void setListener(ChangeListener<Boolean> hoverListener) {
            hoverProperty().addListener(hoverListener);
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
            if (o == this) return true;
            if (o instanceof WeightedDrawableEdge compareEdge) return edge.equals(compareEdge);
            if (o instanceof Weight edgeWeight) return edge.equals(edgeWeight.edge);
            return false;
        }
    }
}
